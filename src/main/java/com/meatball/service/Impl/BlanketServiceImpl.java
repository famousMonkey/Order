package com.meatball.service.Impl;

import com.alibaba.fastjson.JSON;
import com.codingapi.txlcn.tc.annotation.TxcTransaction;
import com.meatball.client.ResourceClient;
import com.meatball.common.base.BaseService;
import com.meatball.common.constant.MeatballConst;
import com.meatball.common.response.ResultMsg;
import com.meatball.common.utils.*;
import com.meatball.common.vo.basicsResource.CommodityVo;
import com.meatball.common.vo.basicsResource.OilsVo;
import com.meatball.common.vo.classes.OilGunDutyVo;
import com.meatball.common.vo.classes.StaffDutyVo;
import com.meatball.common.vo.loginvo.LoginVo;
import com.meatball.common.vo.member.MemberDetailsVo;
import com.meatball.common.vo.oilstationvo.OilStationVo;
import com.meatball.common.vo.ordervo.customize.*;
import com.meatball.common.vo.payinfo.customize.PayNotifyVo;
import com.meatball.dao.BlanketRepository;
import com.meatball.entity.*;
import com.meatball.service.*;
import com.meatball.vo.OrderComplete;
import com.meatball.vo.discount.DisCountSumVo;
import com.meatball.vo.discount.DiscountDataVo;
import com.meatball.vo.discount.DiscountOilVo;
import com.meatball.vo.discount.DiscountProVo;
import com.meatball.vo.request.BlanketorderRe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:BlanketServiceImpl
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:MeatballConst.TWOMeatballConst.ZEROMeatballConst.ONE8/MeatballConst.ONEMeatballConst.ONE/3 MeatballConst.ONE4:3MeatballConst.ZERO
 * @Version: MeatballConst.ONE.MeatballConst.ZERO
 **/
@Service
@Slf4j
public class BlanketServiceImpl extends BaseService<Blanketorder> implements BlanketService {

    @Autowired
    private BlanketRepository blanketRepository;

    @Autowired
    private ResourceClient resourceClient;

    @Autowired
    private OilService oilService;

    @Autowired
    private PorderService porderService;

    @Autowired
    private ProdetailService prodetailService;

    @Autowired
    private OrderCancellationService orderCancellationService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public Boolean saveResourceInfo(Blanketorder resourceInfo) {
        blanketRepository.save(resourceInfo);
        return true;
    }

    @Override
    @Transactional
    public Boolean updateResourceInfo(Blanketorder resourceInfo) {
        Blanketorder entity = selectResourceInfo(resourceInfo.getId());
        if(entity == null){
            return false;
        }
        UpdateUtil.copyNonNullProperties(resourceInfo,entity);
        entity = blanketRepository.save(entity);
        UpdateUtil.copyNonNullProperties(entity,resourceInfo);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteResourceInfo(List<Blanketorder> list) {
        for (Blanketorder entity: list) {
            entity.setDeleted(true);
            blanketRepository.save(entity);
        }
        return true;
    }

    @Override
    public Blanketorder selectResourceInfo(String resourceId) {

        Blanketorder resourceInfo = blanketRepository.findBlanketorderById(resourceId);
        return resourceInfo;
    }

    @Override
    public Blanketorder getBlanketorderByblanketorderNo(String blanketorderNo) {
        Blanketorder blanketorder = blanketRepository.findBlanketorderByBlanketorderNo(blanketorderNo);
        return blanketorder;
    }
    @Override
    public Page<Blanketorder> queryPage(Map<String, String> query) {
        Map<String, Object> queryCondition = queryUsualPageCondition(query);
        Specification<Blanketorder> specification = (Specification<Blanketorder>) queryCondition.get("specification");
        Pageable pageable = (Pageable) queryCondition.get("pageable");
        Page<Blanketorder> page;
        if (specification != null) {
            page = blanketRepository.findAll(specification, pageable);
        } else {
            page = blanketRepository.findAll(pageable);
        }
        return page;
    }

    @Override
    public List<Blanketorder> queryList(Map<String, String> query) {
        Specification<Blanketorder> specification = queryUsualListCondition(query);
        List<Blanketorder> list;
        if (specification != null) {
            list = blanketRepository.findAll(specification);
        } else {
            list = blanketRepository.findAll();
        }
        return list;
    }

    @Override
    public List<Blanketorder> queryUndoneByMemberId(String oilStationId,String memberId) {
        BlanketorderRe blanketorder = new BlanketorderRe();
        blanketorder.setPend(null);
        blanketorder.setMemberId(memberId);
        blanketorder.setOilStationId(oilStationId);
        blanketorder.setBlankSta("0,1@7");
        Map query= MapObjUtil.object2Map(blanketorder);
        return queryList(query);
    }

    private boolean querydiscountOil(OilOrderMutualInfo oilOrder,DiscountOilVo discountOil){

        Map<String,String> query = new HashMap<>();

        // 卡券ID
        if(StringUtils.isNotBlank(oilOrder.getCouponId())){
            query.put("couponId",oilOrder.getCouponId());
        }

        // 档位ID
        if(StringUtils.isNotBlank(oilOrder.getDetailId())){
            // 档位ID
            query.put("detailId",oilOrder.getDetailId());
            //活动类型 1消费直降 2 会员等级
            query.put("activityType",oilOrder.getActivityType().toString());
        }

        // 油品id
        query.put("oilId",oilOrder.getOilsId());

        // 油品升数
        query.put("oilRise",oilOrder.getQuantity().toString());

        // 订单金额(单位元)
        query.put("orderMoney",oilOrder.getOilMoney().toString());

        ResultMsg<DiscountDataVo> resultMsg = resourceClient.
                discountOil(query);

        if(resultMsg == null || resultMsg.getCode() != 10000){
            return false;
        }

        DiscountDataVo discountData = resultMsg.getData();

        discountOil.setDiscounts(discountData.getCardMoney());

        for (DisCountSumVo disCountSum : discountData.getDisCountSumList()){
            if(disCountSum.getType() == 0){
                discountOil.setCouponDis(disCountSum.getTotalDiscount());
            }else {
                discountOil.setGrantDis(disCountSum.getTotalDiscount());
                discountOil.setPriceDis(disCountSum.getDiscountMoney());
            }
        }

        return true;
    }

    private int verifyOrder(OilStationVo oilStation,OrderMutualInfo orderMutualInfo){

        Boolean unitPrice = true;

        // 如果不是新订单
        if (!orderMutualInfo.getNewOrder()){
            // 验证这个编号是否能够获取到订单信息,并且后台获取的订单的状态是0进行中
            Blanketorder blanketorder = blanketRepository.findBlanketorderByBlanketorderNo(orderMutualInfo.getBlanketorderNo());
            if(blanketorder == null){
                return 8;// 没查到订单
            }
            if(blanketorder.getBlankSta() != 0){
                return 7;// 订单不允许修改
            }
            orderMutualInfo.setId(blanketorder.getId());
            //订单来源不允许修改
            orderMutualInfo.setOrderSource(blanketorder.getOrderSource());
        }else {
            orderMutualInfo.setBlanketorderNo(null);
        }

        // 订单来源必须填
        if(orderMutualInfo.getOrderSource()==null){
            return 11;
        }


        LoginVo login = super.getLoginInfo();
        if(login == null){
            return 6;//没有执勤
        }
        StaffDutyVo staffDuty = null;

        // 获取操作员执勤信息
        if(StringUtils.isNotBlank(login.getType()) && login.getType().equals("staff")){
            // 根据员工ID获取员工执勤信息
            ResultMsg<StaffDutyVo> resultMsg = resourceClient.getStaffDutyByStaffId(login.getId());
            if(resultMsg == null || resultMsg.getCode() != 10000 ||resultMsg.getData() == null){
                return 6;//没有执勤
            }
            staffDuty = resultMsg.getData();
            orderMutualInfo.setSquadId(staffDuty.getSquadId()); // 班组ID
            orderMutualInfo.setDutyId(staffDuty.getDutyId());   // 班组执勤ID
            orderMutualInfo.setStaffId(staffDuty.getStaffId()); // 员工ID
            orderMutualInfo.setSalesperson(staffDuty.getName());// 收银员
        }else {
            orderMutualInfo.setStaffId(login.getId());
            orderMutualInfo.setSalesperson("小程序");
        }

        // 如果有油品信息 并且不是挂单
        if(orderMutualInfo.getOilOrder() != null){
            OilOrderMutualInfo oilOrder = orderMutualInfo.getOilOrder();
                if(StringUtils.isBlank(oilOrder.getOilGunId()) || oilOrder.getPrice()==null || oilOrder.getOilGunId()==null){
                    //必须传油枪、单价、油枪ID
                    return 11;
                }

                if(oilOrder.getOilMoney() == null && oilOrder.getPrice()==null){
                    // 总价和数量必须要一个
                    return 11;
                }

                ResultMsg<OilGunDutyVo> staffDutyByOilGun = resourceClient.getOilgunDutyByOilGunId(oilOrder.getOilGunId());
                if(staffDutyByOilGun == null || staffDutyByOilGun.getCode() != 10000 ||staffDutyByOilGun.getData() == null){
                    return 6;//没有执勤
                }else {
                    OilGunDutyVo oilGunDuty = staffDutyByOilGun.getData();
                    if(!oilGunDuty.getOilStationId().equals(orderMutualInfo.getOilStationId())){
                        return 10;// 油枪错误
                    }

                    OilsVo oils = super.queryOils(oilGunDuty.getOilsId());
                    if(oils == null){
                        return 10;// 油枪错误
                    }

                    // 油品信息
                    oilOrder.setOilsId(oils.getId());
                    oilOrder.setOilsName(oils.getName());
                    oilOrder.setOilsDicId(oils.getDictionaryId());

                    // 油枪信息
                    oilOrder.setOilGunId(oilGunDuty.getOilGunId());
                    oilOrder.setOilGunName(oilGunDuty.getName());

                    orderMutualInfo.setSquadId(oilGunDuty.getSquadId());// 班组ID
                    orderMutualInfo.setDutyId(oilGunDuty.getDutyId());  // 班组执勤ID

                    // 如果油站不绑定油枪，使用登录人的人的信息
                    if(oilStation.getBindingOilGun().equals(0)){
                        if(staffDuty != null){
                            oilOrder.setRefuelStaffId(staffDuty.getStaffId()); // 登录员工ID
                            oilOrder.setRefuelStaffName(staffDuty.getName());// 登录员工名称
                            oilOrder.setStaffDutyId(staffDuty.getId());// 员工执勤ID
                        }
                    }else {
                        oilOrder.setRefuelStaffId(oilGunDuty.getStaffId()); // 登录员工ID
                        oilOrder.setRefuelStaffName(oilGunDuty.getStaffName());// 登录员工名称
                        oilOrder.setStaffDutyId(oilGunDuty.getStaffDutyId());// 员工执勤 ID
                    }

                }

                // 如果总价是空，就算出来
                if(oilOrder.getOilMoney() == null ){
                    oilOrder.setOilMoney(oilOrder.getQuantity().multiply(oilOrder.getPrice()).setScale(2, RoundingMode.HALF_UP));
                }else {
                    //如果总价不为空，就重新计算数量
                    oilOrder.setQuantity(oilOrder.getOilMoney().divide(oilOrder.getPrice(),2, RoundingMode.HALF_UP));
                }

                // 判断是否挂单
                if(!orderMutualInfo.getPend()){
                    //获取前台直降的油品优惠后的单价
                    if(oilOrder.getPrice() == null){
                        oilOrder.setPrice(new BigDecimal(0));
                    }

                    //获取油品单价
                    ResultMsg<OilsVo> oilsVoResultMsg = resourceClient.selectOilsById(oilOrder.getOilsId());
                    if (oilsVoResultMsg!=null && oilsVoResultMsg.getCode()==10000 && oilsVoResultMsg.getData() != null) {
                        OilsVo oils = oilsVoResultMsg.getData();
                        if (oilOrder.getPrice().compareTo(oils.getPrice())!=0) {
                            oilOrder.setPrice(oils.getPrice());
                            //单价错误
                            unitPrice = false;
                        }else {
                            oilOrder.setOilsCategory(oils.getOilsCategory());
                            // 计算优惠
                            if(oilOrder.getDiscounts() == null || oilOrder.getDiscounts().doubleValue() == 0){
                                oilOrder.setDiscounts(new BigDecimal(0));
                                oilOrder.setGrantDis(new BigDecimal(0));
                                oilOrder.setCouponDis(new BigDecimal(0));
                                oilOrder.setPriceDis(new BigDecimal(0));
                                oilOrder.setCouponId(null);
                            }else {
                                DiscountOilVo discountOil = new DiscountOilVo();
                                if(!querydiscountOil(oilOrder,discountOil)){
                                    return 12;
                                }

                                if(discountOil.getDiscounts().doubleValue() != oilOrder.getDiscounts().doubleValue()){
                                    return 3;
                                }
                                oilOrder.setGrantDis(discountOil.getGrantDis());
                                oilOrder.setCouponDis(discountOil.getCouponDis());
                                oilOrder.setPriceDis(discountOil.getPriceDis());
                                // 总优惠
                                orderMutualInfo.setDiscounts(orderMutualInfo.getDiscounts().add(oilOrder.getDiscounts()));
                                orderMutualInfo.setGrantDis(orderMutualInfo.getGrantDis().add(discountOil.getGrantDis()));
                                orderMutualInfo.setCouponDis(discountOil.getCouponDis());

                                if(discountOil.getCouponDis().doubleValue() == 0){
                                    oilOrder.setCouponId(null);
                                }
                            }
                        }
                    }else {
                        return 4;// 计算错误
                    }
                }

        }

        // 如果有商品并且不是挂单
        if(orderMutualInfo.getProductOrders() != null && orderMutualInfo.getProductOrders().size()>0 ){
            for (ProductOrderMutualInfo productOrder : orderMutualInfo.getProductOrders()){

                // 必须传数量
                if(productOrder.getQuantity() == null){
                    return 11;
                }
                if(productOrder.getPrice() == null){
                    productOrder.setPrice(new BigDecimal(0));
                }

                if(!orderMutualInfo.getPend()){
                    //获取商品单价
                    ResultMsg<CommodityVo> CommodityResultMsg = resourceClient.selectCommodityById(productOrder.getProductId());
                    if (CommodityResultMsg!=null && CommodityResultMsg.getCode()==10000 && CommodityResultMsg.getData() != null) {
                        CommodityVo commodity = CommodityResultMsg.getData();
                        if (productOrder.getPrice().compareTo(commodity.getSellingPrice())!=0) {
                            productOrder.setPrice(commodity.getSellingPrice());
                            unitPrice = false;
                        }else {
                            Integer quantity =commodity.getAvailableStock() == null?0:commodity.getAvailableStock();
                            if(quantity < productOrder.getQuantity().intValue()){
                                return 13;// 库存不足
                            }

                            // 验证商品优惠
                            Map discountCondition = new HashMap();
                            discountCondition.put("oilStationId",orderMutualInfo.getOilStationId());
                            discountCondition.put("goodsId",productOrder.getProductId());
                            discountCondition.put("goodsCount",productOrder.getQuantity());
                            ResultMsg<DiscountProVo> discountProVoResultMsg = resourceClient.discountPro(discountCondition);
                            BigDecimal proDis = new BigDecimal(0);
                            if(discountProVoResultMsg != null && discountProVoResultMsg.getCode() == 10000 && discountProVoResultMsg.getData() != null){
                                proDis = discountProVoResultMsg.getData().getDiscountSumMoney();
                                productOrder.setDetailId(discountProVoResultMsg.getData().getDetailId());
                            }

                            if(productOrder.getDiscounts() == null){
                                productOrder.setDiscounts(new BigDecimal(0));
                            }

                            if (proDis.compareTo(productOrder.getDiscounts())!=0) {
                                productOrder.setDiscounts(proDis);
                                return 3;
                            }
                            // 总优惠
                            orderMutualInfo.setDiscounts(orderMutualInfo.getDiscounts().add(proDis));
//                            orderMutualInfo.setGrantDis(orderMutualInfo.getGrantDis().add(proDis));
                        }
                        productOrder.setProductName(commodity.getName());
                        productOrder.setProductBarCode(commodity.getBarCode());

                    }else {
                        return 4;// 计算错误
                    }
                }
                if(productOrder.getDiscounts() == null){
                    productOrder.setDiscounts(new BigDecimal(0));
                }
            }

        }

        if(!unitPrice){
            return 2;
        }

        return MeatballConst.ZERO;//(商品单价,活动优惠，代金券优惠全部正确)
    }

    @Override
    @Transactional
    @TxcTransaction
    public int orderMutual(OrderMutualInfo orderMutualInfo){
        MemberDetailsVo member = null;

        //获取油站信息
        OilStationVo oilStation = super.queryOilStation(orderMutualInfo.getOilStationId());
        if(oilStation == null){
            return 11;
        }
        orderMutualInfo.setOrgId(oilStation.getOrgId());
        // 新增订单判断会员信息是否正确
        // 不是临时订单，判断会员信息
        if(!orderMutualInfo.getInterim()){
            String memberId = orderMutualInfo.getMemberId();
            ResultMsg<MemberDetailsVo> resultMsg = resourceClient.queryMemberById(memberId);

            if(resultMsg== null || resultMsg.getCode() != 10000 || resultMsg.getData() == null){
                return 9;//会员信息错误
            }

            // 判断会员是否在这个油站，组织为空，判断油站
            if(StringUtils.isBlank(oilStation.getOrgId()) && !resultMsg.getData().getOilStationId().equals(oilStation.getId())){
                return 9;
            }

            // 判断会员是否在这个油站，组织不为空，判断组织
            if(StringUtils.isNotBlank(oilStation.getOrgId()) && !resultMsg.getData().getOrgId().equals(oilStation.getOrgId())){
                return 9;
            }

            // 如果是新增的订单，并且有未完成的订单，不允许再下单
            if(orderMutualInfo.getNewOrder() && queryUndoneByMemberId(oilStation.getId(),memberId).size()>0){
                return 1;
            }
            member=resultMsg.getData();
            orderMutualInfo.setMemberName(member.getName());
            orderMutualInfo.setMemberNum(member.getMemberNum());
            orderMutualInfo.setMemberPhone(member.getPhone());
            orderMutualInfo.setMemberCarNum(member.getCarNum());
            orderMutualInfo.setMemberCardName(member.getMemberCardName());
            orderMutualInfo.setMemberIntegral(member.getResidueIntegral());

        }else {
            orderMutualInfo.setMemberId(null);

            orderMutualInfo.setMemberName(null);
            orderMutualInfo.setMemberNum(null);
            orderMutualInfo.setMemberPhone(null);
            orderMutualInfo.setMemberCarNum(null);
            orderMutualInfo.setMemberCardName(null);
            orderMutualInfo.setMemberIntegral(null);
            orderMutualInfo.setMemberType(null);
            orderMutualInfo.setTeamInfoId(null);
            orderMutualInfo.setTeamCardId(null);
        }

        int i = verifyOrder(oilStation,orderMutualInfo);
        if(i != 0){
            return i;
        }

        // 新订单生成订单编号
        if(orderMutualInfo.getNewOrder()){
            String oilStationNumber = oilStation.getOilStationNumber();
            //通过油站编号获取订单编号
            String orderNumber = resourceClient.createOrderNumber(oilStationNumber);
            orderMutualInfo.setBlanketorderNo(orderNumber);
        }

        if(orderMutualInfo.getPend()){
            //状态
            orderMutualInfo.setBlankSta(0);
        }else {
            //状态
            orderMutualInfo.setBlankSta(1);
        }

        Blanketorder blanketorder = getBlanketorder(orderMutualInfo);

        if(orderMutualInfo.getNewOrder()){
            super.packageInsertProperty(blanketorder);
            saveResourceInfo(blanketorder);
        }else {
            super.packageUpdateProperty(blanketorder);
            blanketorder.setId(orderMutualInfo.getId());
            updateResourceInfo(blanketorder);
        }

        Oilsorder oilsorder = oilService.oilsOrderMutual(orderMutualInfo,blanketorder);
        Productorder productorder = porderService.productOrderMutual(orderMutualInfo,blanketorder);

        if(!orderMutualInfo.getPend() && orderMutualInfo.getOilOrder() != null
                && StringUtils.isNotBlank(orderMutualInfo.getOilOrder().getCouponId())
                && orderMutualInfo.getOilOrder().getCouponDis().doubleValue()>0){
            System.out.println("占用卡券 传输数据："+ orderMutualInfo.getOilOrder().getCouponId()+"?modifier=" + orderMutualInfo.getSalesperson()+"&oilStationId="+orderMutualInfo.getOilStationId()) ;
            ResultMsg resultMsg = resourceClient.userCouponOccupy(orderMutualInfo.getOilOrder().getCouponId(),orderMutualInfo.getSalesperson(),orderMutualInfo.getOilStationId());
            if(resultMsg == null){
                throw new RuntimeException("占用优惠券错误");
            }
            if(resultMsg.getCode() != 10000){
                throw new RuntimeException("占用优惠券：" + resultMsg.getMessage());
            }

            System.out.println("占用卡券 接收数据："+JSON.toJSONString(resultMsg));
        }

        if(!orderMutualInfo.getPend()){

            if(orderMutualInfo.getProductOrders() != null && orderMutualInfo.getProductOrders().size()>0){
                // 调用进销存
                System.out.println("订单提交通知（进销存） 传输数据："+ JSON.toJSONString(orderMutualInfo));
                ResultMsg resultMsg = resourceClient.orderPostNotice(orderMutualInfo);
                if(resultMsg == null){
                    throw new RuntimeException("调用进销存错误");
                }
                if(resultMsg.getCode() != 10000){
                    throw new RuntimeException("调用进销存：" + resultMsg.getMessage());
                }
                System.out.println("调用进销存 接收数据："+ JSON.toJSONString(resultMsg));

            }

            Map<String,String> rabbitInfo = new HashMap<>();
            rabbitInfo.put("blanketOrder",JSON.toJSONString(blanketorder));
            if(oilsorder!=null){
                rabbitInfo.put("oilsOrder",JSON.toJSONString(oilsorder));
            }
            if(productorder!=null){
                rabbitInfo.put("productOrder",JSON.toJSONString(productorder));
            }
            if(!orderMutualInfo.getInterim()){
                rabbitInfo.put("member",JSON.toJSONString(member));
            }
            System.out.println("报表数据发送："+ rabbitInfo);
            rabbitTemplate.convertAndSend(MeatballConst.ORDER,rabbitInfo);
        }

        return MeatballConst.ZERO;
    }

    @Override
    public OrderMutualInfo selectOrderMutual(String orderNo){
        OrderMutualInfo orderMutualInfo = null;
        Blanketorder blanketorder = getBlanketorderByblanketorderNo(orderNo);
        if(blanketorder != null){

            orderMutualInfo = CopyUtils.copyObject(OrderMutualInfo.class,blanketorder);
            orderMutualInfo.setSalesperson(blanketorder.getSalesperson());

            orderMutualInfo.setOilOrder(oilService.selectOrderMutual(blanketorder.getId()));

            Productorder productorder = porderService.selectProductByblanketOrderId(blanketorder.getId());
            if(productorder != null){
                orderMutualInfo.setProductOrders(prodetailService.selectOrderMutual(productorder.getId()));
            }
        }
        return orderMutualInfo;
    }

    // 获取总订单
    private Blanketorder getBlanketorder(OrderMutualInfo orderMutualInfo){
        Blanketorder blanketorder = new Blanketorder();

        blanketorder.setName("-");

        blanketorder.setOrgId(orderMutualInfo.getOrgId());

        blanketorder.setOilStationId(orderMutualInfo.getOilStationId());

        blanketorder.setBlanketorderNo(orderMutualInfo.getBlanketorderNo());

        // 收银员
        blanketorder.setStaffId(orderMutualInfo.getStaffId());
        blanketorder.setSalesperson(orderMutualInfo.getSalesperson());

        // 班组ID
        blanketorder.setSquadId(orderMutualInfo.getSquadId());

        // 班组执勤ID
        blanketorder.setDutyId(orderMutualInfo.getDutyId());

        //是否临时会员
        blanketorder.setInterim(orderMutualInfo.getInterim());

        // 会员信息
        blanketorder.setMemberId(orderMutualInfo.getMemberId());
        blanketorder.setMemberName(orderMutualInfo.getMemberName());
        blanketorder.setMemberPhone(orderMutualInfo.getMemberPhone());
        blanketorder.setMemberCarNum(orderMutualInfo.getMemberCarNum());
        blanketorder.setMemberCardName(orderMutualInfo.getMemberCardName());
        blanketorder.setMemberIntegral(orderMutualInfo.getMemberIntegral());
        blanketorder.setMemberNum(orderMutualInfo.getMemberNum());
        blanketorder.setMemberType(orderMutualInfo.getMemberType());
        blanketorder.setTeamInfoId(orderMutualInfo.getTeamInfoId());
        blanketorder.setTeamCardId(orderMutualInfo.getTeamCardId());

        // 油品金额
        BigDecimal oilMoney = new BigDecimal(0);

        // 商品金额
        BigDecimal proMoney = new BigDecimal(0);
        String orderDesc=null;

        if(orderMutualInfo.getOilOrder()!=null){
            orderDesc = orderMutualInfo.getOilOrder().getOilsName() + orderMutualInfo.getOilOrder().getOilsCategory();
            if(orderMutualInfo.getOilOrder().getOilMoney()!=null){
                oilMoney = orderMutualInfo.getOilOrder().getOilMoney();
            }
        }

        if(orderMutualInfo.getProductOrders()!=null&&orderMutualInfo.getProductOrders().size()>0){
            if(StringUtils.isNotBlank(orderDesc)){
                orderDesc+="等多件商品";
            }else {
                if(orderMutualInfo.getProductOrders().size() == 1){
                    orderDesc = orderMutualInfo.getProductOrders().get(0).getProductName();
                }else {
                    orderDesc = orderMutualInfo.getProductOrders().get(0).getProductName() + "等多件商品";
                }
            }
            for (ProductOrderMutualInfo product :orderMutualInfo.getProductOrders()){
                BigDecimal money = product.getQuantity().multiply(product.getPrice()).setScale(2, RoundingMode.HALF_UP);
                proMoney = proMoney.add(money);
            }
        }

        blanketorder.setOrderDesc(orderDesc);

        //油品金额
        blanketorder.setOilMoney(oilMoney);

        //商品金额
        blanketorder.setProMoney(proMoney);

        //总金额
        blanketorder.setOrderSum(oilMoney.add(proMoney));

        //退款
        blanketorder.setRefundMoney(new BigDecimal("0"));

        blanketorder.setRefundDiscounts(new BigDecimal("0"));

        blanketorder.setRefundOrder(new BigDecimal("0"));

        //实际支付
        blanketorder.setCopeWith(blanketorder.getOrderSum().subtract(orderMutualInfo.getDiscounts()));

        //挂单
        if(!orderMutualInfo.getPend()){
            // 优惠
            blanketorder.setDiscounts(orderMutualInfo.getDiscounts());
            blanketorder.setCouponDis(orderMutualInfo.getCouponDis());
            blanketorder.setGrantDis(orderMutualInfo.getGrantDis());

        }else {
            // 优惠
            blanketorder.setDiscounts(new BigDecimal(0));
            blanketorder.setCouponDis(new BigDecimal(0));
            blanketorder.setGrantDis(new BigDecimal(0));
        }

        // 积分
        blanketorder.setIntegral(0);

        if(orderMutualInfo.getNewOrder()){
            // 时间
            blanketorder.setOrderGenerationTime(new Date());
        }

        //状态
        blanketorder.setBlankSta(orderMutualInfo.getBlankSta());

        blanketorder.setPend(orderMutualInfo.getPend());

        blanketorder.setRefundSta(0);

        blanketorder.setInvoiceSta(0);

        //来源
        blanketorder.setOrderSource(orderMutualInfo.getOrderSource());

        // 支付类别
        blanketorder.setPaymentType(orderMutualInfo.getPaymentType());
        //支付方式
        blanketorder.setPaymentMethod(orderMutualInfo.getPaymentMethod());

        blanketorder.setVersion(orderMutualInfo.getVersion());

        return blanketorder;
    }

    //支付完成通知
    @Override
    @TxcTransaction
    @Transactional
    public Map orderPaymentNotice(PayNotifyVo payNotifyVo) {

        Map<String,String> rabbitInfo = new HashMap<>();

        //根据总订单Id查询总订单信息
        Blanketorder blanketorder= selectResourceInfo(payNotifyVo.getRelevantId());

        //填充总订单支付信息 和 订单状态
        Blanketorder blanket = new Blanketorder();
        if(payNotifyVo.getPayAmount() == null){
            payNotifyVo.setPayAmount(blanketorder.getCopeWith());
        }

        if(payNotifyVo.getReturnMoney() == null){
            payNotifyVo.setReturnMoney(new BigDecimal(0));
        }

        OrderMutualInfo orderMutualInfo = selectOrderMutual(blanketorder.getBlanketorderNo());
        Date date = new Date();
        orderMutualInfo.setPaymentType(payNotifyVo.getPaymentType());
        orderMutualInfo.setPaymentMethod(payNotifyVo.getPaymentMethod());
        orderMutualInfo.setPaymentNumber(payNotifyVo.getPaymentNumber());
        orderMutualInfo.setOrderCompletionTime(date);

        // 积分
        blanket.setIntegral(0);
        Object integralObject=null;
        if(!blanketorder.getInterim()) {

            System.out.println("添加积分 传输数据："+ JSON.toJSONString(orderMutualInfo));
            // 添加积分
            ResultMsg resultMsg = resourceClient.integralprovideOrdercomplete(orderMutualInfo);
            if(resultMsg == null){
                throw new RuntimeException("添加积分错误");
            }
            if(resultMsg.getCode() != 10000){
                throw new RuntimeException("添加积分：" + resultMsg.getMessage());
            }

            Map map = (Map)resultMsg.getData();
            integralObject = map.get("allIntegralItem");
            blanket.setIntegral(Integer.parseInt(map.get("sumIntegral").toString()));
            System.out.println("添加积分 接收数据："+ JSON.toJSONString(resultMsg));
        }

        //支付方式
        blanket.setId(payNotifyVo.getRelevantId());
        blanket.setPaymentId(payNotifyVo.getPayId());
        blanket.setPaymentNumber(payNotifyVo.getPaymentNumber());
        blanket.setPaymentType(payNotifyVo.getPaymentType());
        blanket.setPaymentMethod(payNotifyVo.getPaymentMethod());
        blanket.setPayAmount(payNotifyVo.getPayAmount().add(payNotifyVo.getReturnMoney()));
        blanket.setReturnMoney(payNotifyVo.getReturnMoney());
        blanket.setBlankSta(2);
        blanket.setOrderCompletionTime(date);
        updateResourceInfo(blanket);

        // 公众号消息
        OrderComplete orderComplete = new OrderComplete();
        orderComplete.setMemberId(blanketorder.getMemberId());
        orderComplete.setOilStationId(blanketorder.getOilStationId());
        orderComplete.setMobile(blanketorder.getMemberPhone());
        orderComplete.setOrderNo(blanketorder.getBlanketorderNo());
        orderComplete.setPayTime(blanket.getOrderCompletionTime());
        orderComplete.setAmountPaid(blanketorder.getCopeWith().doubleValue());
        orderComplete.setDiscountAmount(blanketorder.getDiscounts().doubleValue());

        rabbitInfo.put("blanketOrder",JSON.toJSONString(blanket));

        //根据总订单id查询油品信息
        Oilsorder oilsorder = oilService.selectOilorderByBlanketId(payNotifyVo.getRelevantId());
        //修改油品状态
        if(oilsorder != null){
            String oilId = oilsorder.getId();
            Oilsorder oil = new Oilsorder();
            oil.setId(oilId);
            oil.setOilSta(2);
            oil.setPaymentId(payNotifyVo.getPayId());
            oil.setPaymentNumber(payNotifyVo.getPaymentNumber());
            oil.setPaymentType(payNotifyVo.getPaymentType());
            oil.setPaymentMethod(payNotifyVo.getPaymentMethod());
            oil.setOrderCompletionTime(date);
            super.packageUpdateProperty(oil);
            oilService.updateResourceInfo(oil);

            orderComplete.setPurchaseDetails(oilsorder.getOilsName()+oilsorder.getOilsCategory()+oilsorder.getQuantity()+"L");

            rabbitInfo.put("oilsOrder",JSON.toJSONString(oilsorder));
        }

        Productorder productorder = porderService.selectProductByblanketOrderId(payNotifyVo.getRelevantId());
        if(productorder != null){
            Productorder pro = new Productorder();
            pro.setId(productorder.getId());
            pro.setProSta(2);
            pro.setPaymentId(payNotifyVo.getPayId());
            pro.setPaymentNumber(payNotifyVo.getPaymentNumber());
            pro.setPaymentType(payNotifyVo.getPaymentType());
            pro.setPaymentMethod(payNotifyVo.getPaymentMethod());
            pro.setOrderCompletionTime(date);
            super.packageUpdateProperty(pro);
            porderService.updateResourceInfo(pro);

            List<Productdetail> productdetails = prodetailService.selectProductdetailsByBlanketOrderId(blanketorder.getId());
            for (Productdetail productdetail : productdetails){
                if(StringUtils.isNotBlank(orderComplete.getPurchaseDetails())){
                    orderComplete.setPurchaseDetails(orderComplete.getPurchaseDetails() + "," + productdetail.getName() + "×" + productdetail.getQuantity());
                }else {
                    orderComplete.setPurchaseDetails(productdetail.getName() + "×" + productdetail.getQuantity());
                }
                productdetail.setProdeSta(2);
            }
            prodetailService.updateResourceInfos(productdetails);
            rabbitInfo.put("productOrder",JSON.toJSONString(productorder));
        }


        // 订单完成通知
        orderComplete(oilsorder,orderMutualInfo);


        Map map =new HashMap();
        map.put("rabbitInfo",rabbitInfo);
        if(!blanketorder.getInterim()){
            map.put("orderComplete",JSON.toJSONString(orderComplete));
        }

        if(integralObject!=null){
            map.put("integral",integralObject);
        }

//        System.out.println("报表数据发送："+ rabbitInfo);
//
//        rabbitTemplate.convertAndSend(MeatballConst.ORDER,rabbitInfo);
//
//        rabbitTemplate.convertAndSend(MeatballConst.NOTIFICATION_ORDER_COMPLETE,JSON.toJSONString(orderComplete));


        return map;
    }

    private void orderComplete(Oilsorder oilsorder,  OrderMutualInfo orderMutualInfo){
        ResultMsg resultMsg;
        if(!orderMutualInfo.getInterim()){
            if(oilsorder != null && oilsorder.getDiscounts().doubleValue()>0 ){
                System.out.println("订单完成通知（优惠统计） 传输数据："+ JSON.toJSONString(orderMutualInfo));
                resultMsg = resourceClient.discountInfoOrdercomplete(orderMutualInfo);
                if(resultMsg == null){
                    throw new RuntimeException("添加优惠统计错误");
                }
                if(resultMsg.getCode() != 10000){
                    throw new RuntimeException("添加优惠统计：" + resultMsg.getMessage());
                }
                System.out.println("添加优惠统计 接收数据："+ JSON.toJSONString(resultMsg));

            }
            // 通知营销
            if(oilsorder != null){
                System.out.println("订单完成通知（营销） 传输数据："+ JSON.toJSONString(orderMutualInfo));
                resultMsg = resourceClient.consumeCompleteHandleNotice(orderMutualInfo);
//            if(resultMsg == null){
//                throw new RuntimeException("通知营销错误");
//            }
//            if(resultMsg.getCode() != 10000){
//                throw new RuntimeException("通知营销 ：" + resultMsg.getMessage());
//            }
                System.out.println("通知营销 接收数据："+ JSON.toJSONString(resultMsg));
            }

            // 通知会员
            System.out.println("订单完成通知（会员） 传输数据："+ JSON.toJSONString(orderMutualInfo));
            resultMsg = resourceClient.memberOrderComplete(orderMutualInfo);
            if(resultMsg == null){
                throw new RuntimeException("通知会员错误");
            }
            if(resultMsg.getCode() != 10000){
                throw new RuntimeException("通知会员：" + resultMsg.getMessage());
            }
            System.out.println("通知会员 接收数据："+ JSON.toJSONString(resultMsg));
        }

        // 通知进销存
        System.out.println("订单完成通知（进销存） 传输数据："+ JSON.toJSONString(orderMutualInfo));
        resultMsg = resourceClient.orderCompleteNotice(orderMutualInfo);
        if(resultMsg == null){
            throw new RuntimeException("调用进销存错误");
        }
        if(resultMsg.getCode() != 10000){
            throw new RuntimeException("调用进销存：" + resultMsg.getMessage());
        }
        System.out.println("调用进销存 接收数据："+ JSON.toJSONString(resultMsg));

        if(oilsorder != null){
            if(StringUtils.isNotBlank(oilsorder.getDataCode())){
                // 油机数据释放;
                resourceClient.dispenserDelete(oilsorder.getDataCode());
            }
        }
    }

    //订单取消
    @Override
    @TxcTransaction
    @Transactional
    public Blanketorder orderCancellation(OrderCancellation orderCancellation) {
        //根据订单Id查询总订单信息
        Blanketorder blanketorder= blanketRepository.findBlanketorderById(orderCancellation.getBlanketorderId());

        Integer sta = blanketorder.getBlankSta();

        //根据总订单id查询油品信息
        Oilsorder oilsorder = oilService.selectOilorderByBlanketId(orderCancellation.getBlanketorderId());
        if(oilsorder != null){
            Oilsorder oil = new Oilsorder();
            oil.setId(oilsorder.getId());
            oil.setOilSta(3);
            oilService.updateResourceInfo(oil);
            oilsorder = oil;

            if(StringUtils.isNotBlank(oilsorder.getDataCode())){
                // 油机数据解锁
                Map map = new HashMap();
                map.put("code",oilsorder.getDataCode());
                map.put("lock",false);
                resourceClient.dispenserUnlock(oilsorder.getDataCode(),map);
            }
        }

        //根据总订单id查询商品信息
        Productorder productorder=porderService.selectProductByblanketOrderId(orderCancellation.getBlanketorderId());

        if(productorder != null){
            List<Productdetail> productdetails = prodetailService.selectProductdetailsByBlanketOrderId(blanketorder.getId());
            for (Productdetail productdetail : productdetails){
                productdetail.setProdeSta(3);
            }
            prodetailService.updateResourceInfos(productdetails);
            Productorder pro = new Productorder();
            pro.setId(productorder.getId());
            pro.setProSta(3);
            porderService.updateResourceInfo(pro);
            productorder = pro;
        }

        //修改总订单状态
        blanketorder.setBlankSta(3);
        updateResourceInfo(blanketorder);

        orderCancellation.setCancellationDate(new Date());
        orderCancellation.setName("-");
        orderCancellationService.saveResourceInfo(orderCancellation);

        // 撤销占用卡券
        if(sta ==1 && oilsorder != null && StringUtils.isNotBlank(oilsorder.getCouponId())){
            System.out.println("撤销占用卡券 传输数据："+ oilsorder.getCouponId() + "?modifier=" + oilsorder.getSalesperson()+"&oilStationId="+oilsorder.getOilStationId());
            ResultMsg resultMsg = resourceClient.userCouponCancel(oilsorder.getCouponId(),oilsorder.getSalesperson(),oilsorder.getOilStationId());
            if(resultMsg == null){
                throw new RuntimeException("撤销占用卡券错误");
            }
            if(resultMsg.getCode() != 10000){
                throw new RuntimeException("撤销占用卡券：" + resultMsg.getMessage());
            }

            System.out.println("撤销占用卡券 接收数据："+ JSON.toJSONString(resultMsg));
        }

        if(sta ==1){
            // 调用进销存
            OrderMutualInfo orderMutualInfo = selectOrderMutual(blanketorder.getBlanketorderNo());
            System.out.println("订单取消通知（进销存） 传输数据："+ JSON.toJSONString(orderMutualInfo));
            if(orderMutualInfo.getProductOrders() != null && orderMutualInfo.getProductOrders().size()>0){
                ResultMsg resultMsg = resourceClient.orderCancelNotice(orderMutualInfo);
                if(resultMsg == null){
                    throw new RuntimeException("调用进销存错误");
                }
                if(resultMsg.getCode() != 10000){
                    throw new RuntimeException("调用进销存：" + resultMsg.getMessage());
                }
                System.out.println("调用进销存 接收数据："+ JSON.toJSONString(resultMsg));
            }

            Map<String,String> rabbitInfo = new HashMap<>();
            rabbitInfo.put("blanketOrder",JSON.toJSONString(blanketorder));
            if(oilsorder!=null){
                rabbitInfo.put("oilsOrder",JSON.toJSONString(oilsorder));
            }
            if(productorder!=null){
                rabbitInfo.put("productOrder",JSON.toJSONString(productorder));
            }
            System.out.println("报表数据发送："+ rabbitInfo);
            rabbitTemplate.convertAndSend(MeatballConst.ORDER,rabbitInfo);
        }

        return blanketorder;
    }
    @Override
    public SalesStatistics queryMemberConsumption(String memberId, String startDate, String endDate){
        if(StringUtils.isBlank(memberId)){
            return null;
        }

        if(StringUtils.isBlank(startDate)){
            startDate = "1900-01-01 01:00:00";
        }
        if(StringUtils.isBlank(endDate)){
            endDate = DateUtil.getDateTime();
        }

        Map map = blanketRepository.findMemberConsumption(memberId,startDate,endDate);
        SalesStatistics salesStatistics = null;
        if(map != null){
            salesStatistics = MapObjUtil.map2Object(map,SalesStatistics.class);
        }
        return salesStatistics;
    }

    @Override
    public SalesStatistics queryConsumptionByOilRelatedId(String relatedId, String startDate, String endDate){
        if(StringUtils.isBlank(relatedId)){
            return null;
        }

        if(StringUtils.isBlank(startDate)){
            startDate = "1900-01-01 01:00:00";
        }
        if(StringUtils.isBlank(endDate)){
            endDate = DateUtil.getDateTime();
        }
        Map map = blanketRepository.findConsumptionByOilRelatedId(relatedId,startDate,endDate);
        SalesStatistics salesStatistics = new SalesStatistics();
        if(map != null){
            salesStatistics = MapObjUtil.map2Object(map,SalesStatistics.class);
        }
        return salesStatistics;
    }

    @Override
    public  ReceiptStatistics queryReceiptStatisticsByRelatedId(String relatedId, String startDate, String endDate){
        if(StringUtils.isBlank(relatedId)){
            return null;
        }

        if(StringUtils.isBlank(startDate)){
            startDate = "1900-01-01 01:00:00";
        }
        if(StringUtils.isBlank(endDate)){
            endDate = DateUtil.getDateTime();
        }
        Map map = blanketRepository.findReceiptStatisticsByRelatedId(relatedId,startDate,endDate);
        ReceiptStatistics receiptStatistics = new ReceiptStatistics();

        if(map != null){
            receiptStatistics = MapObjUtil.map2Object(map,ReceiptStatistics.class);
        }
        return receiptStatistics;
    }
}
