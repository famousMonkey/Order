package com.meatball.service.Impl;


import com.meatball.client.ResourceClient;
import com.meatball.common.base.BaseService;
import com.meatball.common.response.PageResultMsg;
import com.meatball.common.utils.CopyUtils;
import com.meatball.common.utils.DateUtil;
import com.meatball.common.utils.MapObjUtil;
import com.meatball.common.utils.UpdateUtil;
import com.meatball.common.vo.basicsResource.OilsVo;
import com.meatball.common.vo.ordervo.customize.*;
import com.meatball.dao.OilRepository;
import com.meatball.entity.Blanketorder;
import com.meatball.entity.Oilsorder;
import com.meatball.service.OilService;
import com.meatball.vo.report.OilsorderReport;
import com.meatball.vo.request.StatisticsCondition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName:OilServiceImpl
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/11/3 14:56
 * @Version: 1.0
 **/
@Slf4j
@Service
public class OilServiceImpl extends BaseService<Oilsorder> implements OilService {
    @Autowired
    private OilRepository oilRepository;
    @Autowired
    private ResourceClient resourceClient;

    @Override
    @Transactional
    public Boolean saveResourceInfo(Oilsorder resourceInfo) {
        oilRepository.save(resourceInfo);
        return true;
    }

    @Override
    @Transactional
    public Oilsorder oilsOrderMutual(OrderMutualInfo orderMutualInfo, Blanketorder blanketorder){

        if(orderMutualInfo.getOilOrder()== null){
            Oilsorder oilsorder = selectOilorderByBlanketId(blanketorder.getId());
            if(oilsorder != null){
                deleteOilsOrder(oilsorder);
            }
            return null;
        }

        OilOrderMutualInfo mutualInfo = orderMutualInfo.getOilOrder();
        //添加油品订单信息
        Oilsorder oilsorder= new Oilsorder();

        oilsorder.setBlanketOrderId(blanketorder.getId());

        oilsorder.setName("-");

        // 会员
        oilsorder.setInterim(orderMutualInfo.getInterim());
        oilsorder.setMemberId(orderMutualInfo.getMemberId());
        oilsorder.setMemberName(orderMutualInfo.getMemberName());
        oilsorder.setMemberNum(orderMutualInfo.getMemberNum());
        oilsorder.setMemberPhone(orderMutualInfo.getMemberPhone());
        oilsorder.setMemberType(orderMutualInfo.getMemberType());
        oilsorder.setTeamInfoId(orderMutualInfo.getTeamInfoId());
        oilsorder.setTeamCardId(orderMutualInfo.getTeamCardId());

        // 油站
        oilsorder.setOrgId(orderMutualInfo.getOrgId());
        oilsorder.setOilStationId(orderMutualInfo.getOilStationId());
        oilsorder.setVersion(orderMutualInfo.getVersion());


        // 班组ID
        oilsorder.setSquadId(orderMutualInfo.getSquadId());
        // 班组执勤ID
        oilsorder.setDutyId(orderMutualInfo.getDutyId());

        // 收银员，如果收银员为空，就使用加油员的名称
        oilsorder.setStaffId(mutualInfo.getRefuelStaffId());
        oilsorder.setSalesperson(mutualInfo.getRefuelStaffName());

        // 加油员
        oilsorder.setRefuelStaffId(mutualInfo.getRefuelStaffId());
        oilsorder.setRefuelStaffName(mutualInfo.getRefuelStaffName());
        oilsorder.setStaffDutyId(mutualInfo.getStaffDutyId());

        // 油品 油枪
        oilsorder.setOilGunId(mutualInfo.getOilGunId());
        oilsorder.setOilGunName(mutualInfo.getOilGunName());
        oilsorder.setOilsId(mutualInfo.getOilsId());
        oilsorder.setOilsDicId(mutualInfo.getOilsDicId());
        oilsorder.setOilsName(mutualInfo.getOilsName());
        oilsorder.setOilsCategory(mutualInfo.getOilsCategory());

        // 支付类别
        oilsorder.setPaymentType(orderMutualInfo.getPaymentType());

        //支付方式
        oilsorder.setPaymentMethod(orderMutualInfo.getPaymentMethod());

        // 来源
        oilsorder.setOrderSource(orderMutualInfo.getOrderSource());

        // 订单数据来源，油机数据编码
        oilsorder.setDataCode(mutualInfo.getDataCode());

        //单价
        BigDecimal price = mutualInfo.getPrice();
        oilsorder.setPrice(price);

        //售价
        BigDecimal sellingPrice = mutualInfo.getSellingPrice();
        oilsorder.setSellingPrice(sellingPrice);

        // 数量
        oilsorder.setQuantity(mutualInfo.getQuantity()==null?new BigDecimal(0):mutualInfo.getQuantity());

        // 订单额度
        oilsorder.setOilMoney(mutualInfo.getOilMoney()==null?new BigDecimal(0):mutualInfo.getOilMoney());

        if(orderMutualInfo.getPend()){
            //优惠
            oilsorder.setDiscounts(new BigDecimal(0));
            oilsorder.setCouponDis(new BigDecimal(0));
            oilsorder.setGrantDis(new BigDecimal(0));
            oilsorder.setPriceDis(new BigDecimal(0));
        }else {
            //油品优惠
            oilsorder.setDiscounts(mutualInfo.getDiscounts());
            oilsorder.setCouponDis(mutualInfo.getCouponDis());
            oilsorder.setGrantDis(mutualInfo.getGrantDis());
            oilsorder.setPriceDis(mutualInfo.getPriceDis());

            oilsorder.setCouponId(mutualInfo.getCouponId());
            oilsorder.setDetailId(mutualInfo.getDetailId());
            oilsorder.setActivityType(mutualInfo.getActivityType()==null?0:mutualInfo.getActivityType());
        }

        //退款
        oilsorder.setRefundMoney(new BigDecimal("0"));

        // 实际付款 = 订单额度-订单优惠
        BigDecimal copeWith  = oilsorder.getOilMoney().subtract(oilsorder.getDiscounts());
        oilsorder.setCopeWith(copeWith);

        // 是否单独油品订单
        if(orderMutualInfo.getProductOrders() != null && orderMutualInfo.getProductOrders().size() > 0){
            oilsorder.setSingly(false);
        }else {
            oilsorder.setSingly(true);
        }

        //状态
        oilsorder.setOilSta(orderMutualInfo.getBlankSta());

        oilsorder.setPend(orderMutualInfo.getPend());

        oilsorder.setRefundSta(0);

        // 订单编号
        oilsorder.setOilsorderNo(orderMutualInfo.getBlanketorderNo());

        // 时间
        oilsorder.setOrderGenerationTime(blanketorder.getOrderGenerationTime());

        if(orderMutualInfo.getNewOrder()){
            super.packageInsertProperty(oilsorder);
            saveResourceInfo(oilsorder);
        }else {
            Oilsorder order = selectOilorderByoilsorderNo(oilsorder.getOilsorderNo());
            if(order != null){
                super.packageUpdateProperty(oilsorder);
                oilsorder.setId(order.getId());
                updateResourceInfo(oilsorder);
            }else {
                super.packageInsertProperty(oilsorder);
                saveResourceInfo(oilsorder);
            }
        }
        return oilsorder;
    }

    @Override
    public OilOrderMutualInfo selectOrderMutual(String blanketOrderId){
        OilOrderMutualInfo oilOrderMutualInfo = null;
        Oilsorder oilsorder = selectOilorderByBlanketId(blanketOrderId);
        if(oilsorder != null){
            oilOrderMutualInfo = CopyUtils.copyObject(OilOrderMutualInfo.class,oilsorder);
        }
        return oilOrderMutualInfo;
    }


    @Override
    @Transactional
    public Boolean deleteOilsOrder(Oilsorder oilsorder) {
        oilRepository.delete(oilsorder);
        return true;
    }

    @Override
    @Transactional
    public Boolean updateResourceInfo(Oilsorder resourceInfo) {
        Oilsorder entity = selectResourceInfo(resourceInfo.getId());
        if(entity == null){
            return false;
        }
        UpdateUtil.copyNonNullProperties(resourceInfo,entity);
        entity = oilRepository.save(entity);
        UpdateUtil.copyNonNullProperties(entity,resourceInfo);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteResourceInfo(List<Oilsorder> list) {
        for (Oilsorder entity: list) {
            entity.setDeleted(true);
            oilRepository.save(entity);
        }
        return true;
    }

    @Override
    public Oilsorder selectResourceInfo(String resourceId) {
        Oilsorder resourceInfo = oilRepository.findOilsorderById(resourceId);
        return resourceInfo;
    }

    @Override
    public Page<Oilsorder> queryPage(Map<String, String> query) {
        Map<String, Object> queryCondition = queryUsualPageCondition(query);
        Specification<Oilsorder> specification = (Specification<Oilsorder>) queryCondition.get("specification");
        Pageable pageable = (Pageable) queryCondition.get("pageable");
        Page<Oilsorder> page;
        if (specification != null) {
             page = oilRepository.findAll(specification, pageable);
        } else {
            page = oilRepository.findAll(pageable);
        }
        return page;
    }

    @Override
    public List<OilsorderReport> queryOilsorderReportList(Map<String, String> query) {
        PageResultMsg<List<OilsorderReport>> resultMsg = resourceClient.oilsOrderList(query);
        List<OilsorderReport> list = new ArrayList<>();
        if(resultMsg != null && resultMsg.getCode() == 10000){
            list = resultMsg.getData();
        }
        return list;
    }

    @Override
    public List<Oilsorder> queryList(Map<String, String> query) {
        Specification<Oilsorder> specification = queryUsualListCondition(query);
        List<Oilsorder> list;
        if (specification != null) {
            list = oilRepository.findAll(specification);
        } else {
            list = oilRepository.findAll();
        }
        return list;
    }

    @Override
    public Oilsorder selectOilorderByBlanketId(String blanketOrderId) {
        Oilsorder oilsorder=oilRepository.findOilsorderByBlanketOrderId(blanketOrderId);
        return  oilsorder;
    }

    @Override
    public OrderStatistics queryOrderStatistics(StatisticsCondition condition) {
        Map map = oilRepository.findOrderStatistics(condition);
        OrderStatistics orderStatistics = null;
        if(map != null){
            orderStatistics = MapObjUtil.map2Object(map,OrderStatistics.class);
        }
        return orderStatistics;
    }

    @Override
    public ReceiptStatistics queryReceiptStatistics(StatisticsCondition condition){

        Map map = oilRepository.findReceiptStatistics(condition);
        ReceiptStatistics receiptStatistics = new ReceiptStatistics();
        if(map != null){
            receiptStatistics = MapObjUtil.map2Object(map,ReceiptStatistics.class);
        }
        return receiptStatistics;
    }

    @Override
    public SalesStatistics queryListSalesStatistics(Map<String, String> query){
        List<Oilsorder> list = queryList(query);
        SalesStatistics salesStatistics = new SalesStatistics();

        if(list != null && list.size() > 0){

            List<Oilsorder> result = list.stream() .filter(u -> u.getRefundSta().equals(0)).collect(Collectors.toList());
            if(result != null && result.size() > 0){
                //订单金额
                BigDecimal receivableAmount = result.stream().map(Oilsorder::getOilMoney).reduce(BigDecimal::add).get();
                salesStatistics.setReceivableAmount(receivableAmount);

                //优惠
                BigDecimal discountAmount = result.stream().map(Oilsorder::getDiscounts).reduce(BigDecimal::add).get();
                salesStatistics.setDiscountAmount(discountAmount);

                //实际收款
                BigDecimal realAmount = result.stream().map(Oilsorder::getCopeWith).reduce(BigDecimal::add).get();
                salesStatistics.setRealAmount(realAmount);

                // 销量
                BigDecimal salesVolume =result.stream().map(Oilsorder::getQuantity).reduce(BigDecimal::add).get();
                salesStatistics.setSalesVolume(salesVolume);
            }

            //订单量
            Integer  orderQuantity = list.size();
            salesStatistics.setOrderQuantity(orderQuantity);

            // 退款
            BigDecimal refundAmount=list.stream().map(Oilsorder::getRefundMoney).reduce(BigDecimal::add).get();
            salesStatistics.setRefundAmount(refundAmount);

        }
        return salesStatistics;
    }

    @Override
    public SalesStatistics querySalesStatistics(StatisticsCondition condition){

        Map  map = oilRepository.findSalesStatistics(condition);
        SalesStatistics salesStatistics = new SalesStatistics();
        if(map != null){
            salesStatistics = MapObjUtil.map2Object(map,SalesStatistics.class);
        }
        return salesStatistics;
    }

    @Override
    public List<OilSalesStatistics> queryOilSalesStatisticsByRelatedId(String relatedId, String startDate, String endDate){
        if(StringUtils.isBlank(relatedId)){
            return null;
        }

        if(StringUtils.isBlank(startDate)){
            startDate = "1900-01-01 01:00:00";
        }
        if(StringUtils.isBlank(endDate)){
            endDate = DateUtil.getDateTime();
        }
        List<Map> maps = oilRepository.findOilSalesStatisticsByRelatedId(relatedId,startDate,endDate);
        List<OilSalesStatistics> list = new ArrayList<>();

        if(maps != null && maps.size()>0){
            for (Map map : maps){
                OilSalesStatistics oilSalesStatistics = MapObjUtil.map2Object(map,OilSalesStatistics.class);
                list.add(oilSalesStatistics);
            }

        }
        return list;
    }

    @Override
    public SalesStatistics queryConsumptionByRelatedId(String relatedId, String startDate, String endDate){
        if(StringUtils.isBlank(relatedId)){
            return null;
        }

        if(StringUtils.isBlank(startDate)){
            startDate = "1900-01-01 01:00:00";
        }
        if(StringUtils.isBlank(endDate)){
            endDate = DateUtil.getDateTime();
        }
        Map map = oilRepository.findConsumptionByRelatedId(relatedId,startDate,endDate);
        SalesStatistics salesStatistics = new SalesStatistics();
        if(map != null){
            salesStatistics = MapObjUtil.map2Object(map,SalesStatistics.class);
        }
        return salesStatistics;
    }

    @Override
    public Oilsorder selectOilorderByoilsorderNo(String oilsorderNo) {
        Oilsorder oilsorder = oilRepository.findOilsorderByOilsorderNo(oilsorderNo);
        return  oilsorder;
    }

    @Override
    public List<Map> queryDailyReport(String oilStationId, String startDate, String endDate,List<OilsVo> oilsList){
        if(StringUtils.isBlank(oilStationId)){
            return null;
        }

        if(StringUtils.isBlank(startDate)){
            startDate = "1900-01-01 01:00:00";
        }
        if(StringUtils.isBlank(endDate)){
            endDate = DateUtil.getDateTime();
        }

        List<Map> list = oilRepository.findDateReceiptStatistics(oilStationId,startDate,endDate);
        Map<String ,Map> maps = new LinkedHashMap<>();

        Map totalMap = new HashMap();
        totalMap.put("orderDate","合计");
        totalMap.put("cash", "0.00");
        totalMap.put("swipe", "0.00");
        totalMap.put("wechat", "0.00");
        totalMap.put("alipay", "0.00");
        totalMap.put("commonAccount","0.00");
        totalMap.put("gasolineAccount","0.00");
        totalMap.put("dieselAccount","0.00");
        totalMap.put("cngAccount","0.00");
        totalMap.put("iou", "0.00");
        totalMap.put("total","0.00");
        if(list != null){
            for (Map map : list){
                Map orderMap = new HashMap(map);
                for (OilsVo oils : oilsList) {
                    orderMap.put(oils.getId(), "0.00");
                }

                maps.put(map.get("orderDate").toString(),orderMap);
                totalMap.put("cash",new BigDecimal(map.get("cash").toString()).add( new BigDecimal(totalMap.get("cash").toString())));
                totalMap.put("swipe",new BigDecimal(map.get("swipe").toString()).add( new BigDecimal(totalMap.get("swipe").toString())));
                totalMap.put("wechat",new BigDecimal(map.get("wechat").toString()).add( new BigDecimal(totalMap.get("wechat").toString())));
                totalMap.put("alipay",new BigDecimal(map.get("alipay").toString()).add( new BigDecimal(totalMap.get("alipay").toString())));
                totalMap.put("commonAccount",new BigDecimal(map.get("commonAccount").toString()).add( new BigDecimal(totalMap.get("commonAccount").toString())));
                totalMap.put("gasolineAccount",new BigDecimal(map.get("gasolineAccount").toString()).add( new BigDecimal(totalMap.get("gasolineAccount").toString())));
                totalMap.put("dieselAccount",new BigDecimal(map.get("dieselAccount").toString()).add( new BigDecimal(totalMap.get("dieselAccount").toString())));
                totalMap.put("cngAccount",new BigDecimal(map.get("cngAccount").toString()).add( new BigDecimal(totalMap.get("cngAccount").toString())));
                totalMap.put("iou",new BigDecimal(map.get("iou").toString()).add( new BigDecimal(totalMap.get("iou").toString())));
                totalMap.put("total",new BigDecimal(map.get("total").toString()).add( new BigDecimal(totalMap.get("total").toString())));
            }
        }

        for (OilsVo oils : oilsList){
            totalMap.put(oils.getId(),"0.00");
            list = oilRepository.findDateSalesStatistics(oils.getId(),startDate,endDate);
            if(list != null){
                for (Map map : list){
                    if(maps.containsKey(map.get("orderDate").toString())){
                        maps.get(map.get("orderDate").toString()).put(oils.getId(),map.get("salesVolume"));
                        totalMap.put(oils.getId(),new BigDecimal(totalMap.get(oils.getId()).toString()).add(new BigDecimal(map.get("salesVolume").toString())));
                    }
                }
            }
        }
        List result = new ArrayList<>(maps.values());
        result.add(totalMap);
        return result;
    }
}
