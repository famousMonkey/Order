package com.meatball.service.Impl;

import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.TxcTransaction;
import com.meatball.client.ResourceClient;
import com.meatball.common.base.BaseService;
import com.meatball.common.constant.MeatballConst;
import com.meatball.common.response.ResultMsg;
import com.meatball.common.utils.UpdateUtil;
import com.meatball.common.vo.classes.StaffDutyVo;
import com.meatball.common.vo.oilstationvo.SafePassVo;
import com.meatball.common.vo.ordervo.customize.RefundMutualInfo;
import com.meatball.common.vo.payinfo.customize.RefundVO;
import com.meatball.dao.GoodsReturnRepository;
import com.meatball.entity.*;
import com.meatball.service.*;
import com.meatball.vo.OilSaleRetreatInfoVo;
import com.meatball.vo.OilStationVO;
import org.apache.commons.lang.StringUtils;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @ClassName:GoodsReturnServiceImpl
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/11/12 15:25
 * @Version: 1.0
 **/
@Service
public class GoodsReturnServiceImpl extends BaseService<GoodsRefund> implements GoodsService {

    @Autowired
    private GoodsReturnRepository goodsRepository;

    @Autowired
    private ResourceClient resourceClient;

    @Autowired
    private DetailReturnService detailReturnService;

    @Autowired
    BlanketService blanketService;

    @Autowired
    OilService oilService;

    @Autowired
    PorderService porderService;

    @Autowired
    ProdetailService prodetailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public Boolean saveResourceInfo(GoodsRefund resourceInfo) {
        goodsRepository.save(resourceInfo);
        return true;
    }

    private GoodsRefund saveGoodsReturn(RefundMutualInfo refundMutualInfo, Blanketorder blanketorder, Integer integral){
        GoodsRefund goods = new GoodsRefund();
        goods.setOilStationId(blanketorder.getOilStationId());
        goods.setGoodsRefundNo(refundMutualInfo.getReturnNo());
        goods.setApplicationDate(new Date());
        goods.setApplicationDate(new Date());

        goods.setDutyId(refundMutualInfo.getDutyId());
        goods.setSquadId(refundMutualInfo.getSquadId());

        goods.setStaffId(refundMutualInfo.getStaffId());
        goods.setSalesperson(refundMutualInfo.getStaffName());
        goods.setCollator(null);

        goods.setCollator(refundMutualInfo.getStaffId());
        goods.setBlanketOrderId(refundMutualInfo.getOrderId());
        goods.setMemberId(blanketorder.getMemberId());
        goods.setGoodsType(refundMutualInfo.getType());
        goods.setMoney(blanketorder.getRefundMoney());

        goods.setIntegral(integral);

        super.packageInsertProperty(goods);
        saveResourceInfo(goods);
        return goods;
    }

    private void saveDetailReturn(RefundMutualInfo refundMutualInfo, GoodsRefund goodsReturn, Oilsorder oilsorder){
        DetailRefund detail = new DetailRefund();
        detail.setDetailRefundNo(refundMutualInfo.getReturnNo());
        detail.setApplicationDate(new Date());
        detail.setApplicationDate(new Date());

        detail.setDutyId(refundMutualInfo.getDutyId());
        detail.setSquadId(refundMutualInfo.getSquadId());

        detail.setStaffId(refundMutualInfo.getStaffId());
        detail.setSalesperson(refundMutualInfo.getStaffName());
        detail.setCollator(null);

        detail.setBlanketOrderId(oilsorder.getBlanketOrderId());
        detail.setRelevantId(oilsorder.getId());
        detail.setGoodsType(1);
        detail.setMoney(oilsorder.getRefundMoney());
        detail.setDiscounts(oilsorder.getDiscounts());
        super.packageInsertProperty(detail);
        detailReturnService.saveResourceInfo(detail);
    }

    private void saveDetailReturn(RefundMutualInfo refundMutualInfo, GoodsRefund goodsReturn, Productdetail productdetail){
        DetailRefund detail = new DetailRefund();
        detail.setDetailRefundNo(refundMutualInfo.getReturnNo());
        detail.setApplicationDate(new Date());
        detail.setApplicationDate(new Date());

        detail.setDutyId(refundMutualInfo.getDutyId());
        detail.setSquadId(refundMutualInfo.getSquadId());

        detail.setStaffId(refundMutualInfo.getStaffId());
        detail.setSalesperson(refundMutualInfo.getStaffName());
        detail.setCollator(null);

        detail.setBlanketOrderId(productdetail.getBlanketOrderId());
        detail.setGoodRefundId(goodsReturn.getId());
        detail.setRelevantId(productdetail.getId());
        detail.setGoodsType(2);
        detail.setMoney(productdetail.getCopeWith());
        detail.setDiscounts(productdetail.getDiscounts());
        super.packageInsertProperty(detail);
        detailReturnService.saveResourceInfo(detail);
    }

    @Override
    @Transactional
    @TxcTransaction
    public int refundMutual(RefundMutualInfo refundMutualInfo){

        // 验证密码
        SafePassVo safePass = new SafePassVo();
        safePass.setSafePass(refundMutualInfo.getSafePass());
        safePass.setStaffId(refundMutualInfo.getStaffId());
        ResultMsg resultMsg = resourceClient.verifySafePass(safePass);
        if(resultMsg == null){
            return 1;//操作失败
        }
        if(resultMsg.getCode() != 10000){
            return 3;//密码错误
        }

        Map<String,String> rabbitInfo = new HashMap<>();

        // 调用支付
        RefundVO refund = new RefundVO();

        // 总订单
        Blanketorder blanketorder = null;

        // 油品订单
        Oilsorder oilsorder = null;

        // 商品订单
        Productorder productorder = null;

        // 商品订单明细
        List<Productdetail> productdetails = null;

        // 油品退款金额
        BigDecimal oilRefundMoney = new BigDecimal(0);
        // 商品退款金额
        BigDecimal proRefundMoney = new BigDecimal(0);
        // 商品优惠
        BigDecimal proRefundDiscounts = new BigDecimal(0);

        // 总优惠
        BigDecimal refundDiscounts = new BigDecimal(0);

        // 总退款金额
        BigDecimal refundOrder = new BigDecimal(0);
        // 总退款数量
        BigDecimal refundQuantity = new BigDecimal(0);

        // 全部
        if(refundMutualInfo.getType() == 0){
            blanketorder = blanketService.selectResourceInfo(refundMutualInfo.getOrderId());
            if(blanketorder == null){
                return 2;// 信息错误
            }
            if(blanketorder.getRefundSta() == 1){
                return 6;// 已经退款
            }

            refundOrder = blanketorder.getOrderSum().subtract(blanketorder.getRefundOrder());

            refundDiscounts = blanketorder.getDiscounts().subtract(blanketorder.getRefundDiscounts());

            oilsorder = oilService.selectOilorderByBlanketId(blanketorder.getId());
            if(oilsorder!=null){
                oilRefundMoney = oilsorder.getCopeWith();
            }

            productorder = porderService.selectProductByblanketOrderId(blanketorder.getId());
            if(productorder!=null){
                proRefundMoney = productorder.getCopeWith().subtract(productorder.getRefundMoney());
                refundQuantity = productorder.getQuantity().subtract(productorder.getRefundQuantity());
                proRefundDiscounts = productorder.getDiscounts().subtract(productorder.getRefundDiscounts());
            }


            productdetails = prodetailService.selectProductdetailsByBlanketOrderId(blanketorder.getId());

            refund.setOrderId(blanketorder.getId());
            refund.setType(1);
        }

        //油品
        if(refundMutualInfo.getType() == 1){
            oilsorder = oilService.selectResourceInfo(refundMutualInfo.getOrderId());
            if(oilsorder == null){
                return 2;// 信息错误
            }
            if(oilsorder.getRefundSta() == 1){
                return 6;// 已经退款
            }
            blanketorder = blanketService.selectResourceInfo(oilsorder.getBlanketOrderId());

            refundOrder = oilsorder.getOilMoney();
            refundDiscounts = oilsorder.getDiscounts();
            oilRefundMoney = oilsorder.getCopeWith();

            refund.setOrderId(blanketorder.getId());
            refund.setGoodsId(oilsorder.getId());
            refund.setType(2);

        }

        // 商品
        if(refundMutualInfo.getType() == 2){
            Productdetail productdetail = prodetailService.selectResourceInfo(refundMutualInfo.getOrderId());
            if(productdetail == null){
                return 2;// 信息错误
            }
            if(productdetail.getRefundSta() == 1){
                return 6;// 已经退款
            }

            // 商品数量
            BigDecimal quantity = productdetail.getQuantity();

            // 已退数量
            BigDecimal already = productdetail.getRefundQuantity();

            // 退款数量
            BigDecimal goon = refundMutualInfo.getQuantity();

            if(already.add(goon).doubleValue() > quantity.doubleValue()){
                return 8;// 超出
            }
            if(goon.doubleValue()<quantity.doubleValue() && productdetail.getDiscounts().doubleValue()>0){
                return 9;// 有优惠的商品不允许单独退
            }
            productdetails = new ArrayList<>();
            productdetails.add(productdetail);

            productorder = porderService.selectResourceInfo(productdetail.getProrderId());
            blanketorder = blanketService.selectResourceInfo(productorder.getBlanketOrderId());
            // 如果已经全部退款
            if(already.add(goon).doubleValue() == quantity.doubleValue()){
                proRefundMoney = productdetail.getCopeWith().subtract(productdetail.getRefundMoney());
                productdetail.setRefundSta(1);
                productdetail.setRefundMoney(productdetail.getCopeWith());
            }else {
                proRefundMoney = productdetail.getCopeWith().divide(quantity).multiply(goon).setScale(2, RoundingMode.HALF_UP);
                productdetail.setRefundSta(2);
                productdetail.setRefundMoney(productdetail.getRefundMoney().add(proRefundMoney));
            }

            refundQuantity = goon;
            refundOrder = proRefundMoney;
            proRefundDiscounts = productorder.getDiscounts();
            refundDiscounts = proRefundDiscounts;

            productdetail.setRefundQuantity(productdetail.getRefundQuantity().add(refundQuantity));

            refund.setOrderId(blanketorder.getId());
            refund.setGoodsId(productdetail.getId());
            refund.setType(3);
        }

        if(blanketorder == null){
            return 2;
        }

        if(blanketorder.getBlankSta() != 2){
            return 7;
        }

        String operationPerson = super.getOperationPersonId();

        // 对比传入的员工ID登录的员工ID
        if(!operationPerson.equals(refundMutualInfo.getStaffId())){
            return 10;// 信息错误
        }

        // 验证执勤
        ResultMsg<StaffDutyVo> staffDuty = resourceClient.getStaffDutyByStaffId(operationPerson);
        if(staffDuty == null || staffDuty.getCode() != 10000 ||staffDuty.getData() == null){
            return 4;//无执勤信息
        }

        String dutyId = staffDuty.getData().getDutyId();
        if(!blanketorder.getDutyId().equals(dutyId)){
            return 5;//退款过期
        }

        refundMutualInfo.setSquadId(staffDuty.getData().getSquadId());
        refundMutualInfo.setDutyId(staffDuty.getData().getDutyId());
        refundMutualInfo.setStaffName(staffDuty.getData().getName());

        // 修改总订单
        Blanketorder blanket = new Blanketorder();
        blanket.setId(blanketorder.getId());

        blanket.setRefundMoney(blanketorder.getRefundMoney().add(proRefundMoney).add(oilRefundMoney));
        blanket.setRefundOrder(blanketorder.getRefundOrder().add(refundOrder));
        blanket.setRefundDiscounts(blanketorder.getRefundDiscounts().add(refundDiscounts));

        if(blanketorder.getCopeWith().doubleValue() == blanket.getRefundMoney().doubleValue()){
            blanket.setRefundSta(1);
        }else {
            blanket.setRefundSta(2);
        }
        super.packageUpdateProperty(blanket);
        blanketService.updateResourceInfo(blanket);

        rabbitInfo.put("blanketOrder",JSON.toJSONString(blanket));

        //通过油站id获取油站编号
        ResultMsg<OilStationVO> oilstation = resourceClient.getOilstationById(blanketorder.getOilStationId());
        String oilStationNumber = oilstation.getData().getOilStationNumber();
        //通过油站编号获取订单编号
        String returnNo = resourceClient.createOrderNumber(oilStationNumber);
        refundMutualInfo.setReturnNo(returnNo);

        refundMutualInfo.setOrderNo(blanketorder.getBlanketorderNo());

        // 非临时会员更新积分
        Integer integral = 0;
        if(StringUtils.isNotBlank(blanketorder.getMemberId())){
            // 更新积分
            integral = updateIntegral(refundMutualInfo);
        }

        GoodsRefund goodRefund = saveGoodsReturn(refundMutualInfo,blanket,integral);
        rabbitInfo.put("refundOrder",JSON.toJSONString(goodRefund));
        // 油品
        if(oilsorder != null){
            Oilsorder oils = new Oilsorder();
            oils.setId(oilsorder.getId());
            oils.setRefundSta(1);
            oils.setRefundMoney(oilRefundMoney);
            super.packageUpdateProperty(oils);
            oilService.updateResourceInfo(oils);
            saveDetailReturn(refundMutualInfo,goodRefund,oilsorder);
            rabbitInfo.put("oilsOrder",JSON.toJSONString(oils));
        }

        // 商品
        if(productorder != null){
            Productorder porduct = new Productorder();
            porduct.setId(productorder.getId());
            porduct.setRefundMoney(productorder.getRefundMoney().add(proRefundMoney));
            porduct.setRefundQuantity(productorder.getRefundQuantity().add(refundQuantity));
            porduct.setRefundDiscounts(productorder.getRefundDiscounts().add(proRefundDiscounts));
            if(productorder.getCopeWith().doubleValue() == porduct.getRefundMoney().doubleValue()){
                porduct.setRefundSta(1);
            }else {
                porduct.setRefundSta(2);
            }
            super.packageUpdateProperty(porduct);
            porderService.updateResourceInfo(porduct);
            rabbitInfo.put("productOrder",JSON.toJSONString(porduct));
        }

        // 商品明细
        if(productdetails != null && productdetails.size()>0){
            for (Productdetail productdetail : productdetails){
                if(refundMutualInfo.getType() != 2){
                    productdetail.setRefundSta(1);
                    productdetail.setRefundQuantity(productdetail.getQuantity());
                    productdetail.setRefundMoney(productdetail.getCopeWith());
                }
                saveDetailReturn(refundMutualInfo,goodRefund,productdetail);
            }

            prodetailService.updateResourceInfos(productdetails);
        }

        updateOilSaleRetreat(refundMutualInfo);

        // 非临时会员更新优惠统计
        if(StringUtils.isNotBlank(blanketorder.getMemberId()) && oilsorder != null){
            refundMutualInfo.setOrderId(oilsorder.getId());
            if(oilsorder.getDiscounts().doubleValue() > 0){
                updateDiscount(refundMutualInfo);
            }

            //调用营销
            refundCompleteHandleNotice(refundMutualInfo);
        }

        refund.setPassword(refundMutualInfo.getSafePass());
        refund.setEmployeeId(refundMutualInfo.getStaffId());
        refund.setGoodsId(refundMutualInfo.getOrderId());

        //调用退款
        payserviceRefund(refund);

        System.out.println("报表数据发送："+ rabbitInfo);
        rabbitTemplate.convertAndSend(MeatballConst.ORDER,rabbitInfo);
        return 0;
    }

    // 调用退款
    private void  payserviceRefund(RefundVO refund ){
        System.out.println("调用退款 传输数据："+ JSON.toJSONString(refund));
        ResultMsg resultMsg = resourceClient.payserviceRefund(refund);
        if(resultMsg == null){
            throw new RuntimeException("退款出现错误");
        }
        if(resultMsg.getCode() != 10000){
            throw new RuntimeException("退款：" + resultMsg.getMessage());
        }
        System.out.println("调用退款 接收数据："+ JSON.toJSONString(resultMsg));
    }

    // 调用营销
    private void refundCompleteHandleNotice(RefundMutualInfo refundMutualInfo){
        System.out.println("调用营销 传输数据："+ JSON.toJSONString(refundMutualInfo));
        ResultMsg resultMsg = resourceClient.refundCompleteHandleNotice(refundMutualInfo);
//        if(resultMsg == null){
//            throw new RuntimeException("调用营销出现错误");
//        }
//        if(resultMsg.getCode() != 10000){
//            throw new RuntimeException("调用营销：" + resultMsg.getMessage());
//        }
        System.out.println("调用营销 接收数据："+ JSON.toJSONString(resultMsg));
    }

    // 更新进销存
    private void updateOilSaleRetreat(RefundMutualInfo refundMutualInfo){
        System.out.println("调用进销存 传输数据："+ JSON.toJSONString(refundMutualInfo));
        ResultMsg resultMsg = resourceClient.orderRetreatNotice(refundMutualInfo);
        if(resultMsg == null){
            throw new RuntimeException("调用进销存错误");
        }
        if(resultMsg.getCode() != 10000){
            throw new RuntimeException("调用进销存：" + resultMsg.getMessage());
        }
        System.out.println("调用进销存 接收数据："+ JSON.toJSONString(resultMsg));
    }

    // 更新积分
    private Integer updateIntegral(RefundMutualInfo refundMutualInfo){
        System.out.println("更新积分 传输数据："+ JSON.toJSONString(refundMutualInfo));
        ResultMsg<Integer> resultMsg = resourceClient.integralprovideOrderrefund(refundMutualInfo);
        if(resultMsg == null){
            throw new RuntimeException("更新积分错误");
        }
        if(resultMsg.getCode() != 10000){
            throw new RuntimeException("更新积分：" + resultMsg.getMessage());
        }
        System.out.println("更新积分 接收数据："+ JSON.toJSONString(resultMsg));
        return resultMsg.getData();
    }

    // 更新优惠
    private void updateDiscount(RefundMutualInfo refundMutualInfo){
        System.out.println("更新优惠 传输数据："+ JSON.toJSONString(refundMutualInfo));
        ResultMsg resultMsg = resourceClient.discountInfoOrderrefund(refundMutualInfo);
        if(resultMsg == null){
            throw new RuntimeException("优惠统计错误");
        }
        if(resultMsg.getCode() != 10000){
            throw new RuntimeException("更新优惠统计：" + resultMsg.getMessage());
        }
        System.out.println("更新优惠 接收数据："+ JSON.toJSONString(resultMsg));

    }

    @Override
    @TxcTransaction
    @Transactional
    public Boolean updateResourceInfo(GoodsRefund resourceInfo) {
        GoodsRefund entity = selectResourceInfo(resourceInfo.getId());
        if(entity == null){
            return false;
        }
        UpdateUtil.copyNonNullProperties(resourceInfo,entity);
        goodsRepository.save(entity);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteResourceInfo(List<GoodsRefund> list) {
        for (GoodsRefund entity: list) {
            entity.setDeleted(true);
            goodsRepository.save(entity);
        }
        return true;
    }

    @Override
    public GoodsRefund selectResourceInfo(String resourceId) {
        GoodsRefund resourceInfo = goodsRepository.findGoodsReturnById(resourceId);
        return resourceInfo;
    }

    @Override
    public Page<GoodsRefund> queryPage(Map<String, String> query) {
        Map<String, Object> queryCondition = queryUsualPageCondition(query);
        Specification<GoodsRefund> specification = (Specification<GoodsRefund>) queryCondition.get("specification");
        Pageable pageable = (Pageable) queryCondition.get("pageable");
        Page<GoodsRefund> page;
        if (specification != null) {
            page = goodsRepository.findAll(specification, pageable);
        } else {
            page = goodsRepository.findAll(pageable);
        }
        return page;
    }

    @Override
    public List<GoodsRefund> queryList(Map<String, String> query) {
        Specification<GoodsRefund> specification = queryUsualListCondition(query);
        List<GoodsRefund> list;
        if (specification != null) {
            list = goodsRepository.findAll(specification);
        } else {
            list = goodsRepository.findAll();
        }
        return list;
    }

    @Override
    public List<GoodsRefund> selectGoodsRefundsByBlanketOrderId(String blanketOrderId){
        Map<String, String> query = new HashMap<>();
        query.put("blanketOrderId",blanketOrderId);
        return queryList(query);
    }

    @Override
    @Transactional
    @TxcTransaction
    public String test(Long time){
        String str = resourceClient.blockTest(time);
        return str;
    }

//    @Override
//    @Transactional
//    @TxcTransaction
//    public ResultMsg test(OilSaleRetreatInfoVo info){
//        ResultMsg resultMsg = resourceClient.saveAndUpdateResourceInfo(info);
//        if(resultMsg == null){
//            throw new RuntimeException("更新油枪表错误");
//        }
//        if(resultMsg.getCode() != 10000){
//            throw new RuntimeException("更新油枪表：" + resultMsg.getMessage());
//        }
//
//        for (int i = 0 ;i<100; i++){
//            ResultMsg<StaffDutyVo> xx = resourceClient.getStaffDutyByStaffId("123456");
//            System.out.println(i);
//        }
//
//        return resultMsg;
//    }
}
