package com.meatball.service.Impl;

import com.meatball.client.ResourceClient;
import com.meatball.common.base.BaseService;
import com.meatball.common.base.PageEntity;
import com.meatball.common.base.PageInfo;
import com.meatball.common.response.PageResultMsg;
import com.meatball.common.utils.DateUtil;
import com.meatball.common.utils.MapObjUtil;
import com.meatball.common.utils.UpdateUtil;
import com.meatball.common.vo.ordervo.customize.OrderMutualInfo;
import com.meatball.common.vo.ordervo.customize.ProductOrderMutualInfo;
import com.meatball.common.vo.ordervo.customize.SalesStatistics;
import com.meatball.dao.ProductorderRepository;
import com.meatball.entity.Blanketorder;
import com.meatball.entity.Productorder;
import com.meatball.service.PorderService;
import com.meatball.service.ProdetailService;
import com.meatball.vo.report.DailyReport;
import com.meatball.vo.report.ProductorderReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
 * @ClassName:PorderServiceImpl
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/11/2 22:35
 * @Version: 1.0
 **/
@Service
@Slf4j
public class PorderServiceImpl extends BaseService<Productorder> implements PorderService {

    @Autowired
    private ProductorderRepository repository;

    @Autowired
    private ProdetailService prodetailService;

    @Autowired
    private ResourceClient resourceClient;

    @Override
    @Transactional
    public Boolean saveResourceInfo(Productorder resourceInfo) {
        repository.save(resourceInfo);
        return true;
    }

    @Override
    @Transactional
    public Productorder productOrderMutual(OrderMutualInfo orderMutualInfo, Blanketorder blanketorder){

        // 如果不是新订单
        if(!orderMutualInfo.getNewOrder()){
            Productorder order = selectProductByProductorderNo(orderMutualInfo.getBlanketorderNo());
            if(order != null){
                deleteProductorder(order);
            }
        }

        if(orderMutualInfo.getProductOrders() != null && orderMutualInfo.getProductOrders().size()>0){
            BigDecimal porMoney = new BigDecimal(0);
            BigDecimal quantity = new BigDecimal(0);
            BigDecimal discounts = new BigDecimal(0);
            for (ProductOrderMutualInfo product :orderMutualInfo.getProductOrders()){
                BigDecimal money = product.getQuantity().multiply(product.getPrice()).setScale(2, RoundingMode.HALF_UP);
                porMoney = porMoney.add(money);
                quantity = quantity.add(product.getQuantity());
                discounts = discounts.add(product.getDiscounts());
            }
            
            Productorder productorder =new Productorder();

            // 总订单ID
            productorder.setBlanketOrderId(blanketorder.getId());

            // 商品单号"
            productorder.setProductorderNo(orderMutualInfo.getBlanketorderNo());

            // 油站id
            productorder.setOrgId(orderMutualInfo.getOrgId());
            productorder.setOilStationId(orderMutualInfo.getOilStationId());

            // 会员信息
            productorder.setMemberId(orderMutualInfo.getMemberId());
            productorder.setMemberName(orderMutualInfo.getMemberName());
            productorder.setMemberNum(orderMutualInfo.getMemberNum());
            productorder.setMemberPhone(orderMutualInfo.getMemberPhone());
            productorder.setMemberType(orderMutualInfo.getMemberType());
            productorder.setTeamInfoId(orderMutualInfo.getTeamInfoId());
            productorder.setTeamCardId(orderMutualInfo.getTeamCardId());

            // 班组ID
            productorder.setSquadId(orderMutualInfo.getSquadId());

            // 班组执勤ID
            productorder.setDutyId(orderMutualInfo.getDutyId());

            // 售货员
            productorder.setStaffId(orderMutualInfo.getStaffId());
            productorder.setSalesperson(orderMutualInfo.getSalesperson());

            productorder.setQuantity(quantity);

            // 商品金额
            productorder.setProMoney(porMoney);

            // 商品优惠
            productorder.setDiscounts(discounts);

            //退款
            productorder.setRefundMoney(new BigDecimal("0"));

            productorder.setRefundDiscounts(new BigDecimal(0));

            productorder.setRefundQuantity(new BigDecimal("0"));

            // 应付
            productorder.setCopeWith(porMoney.subtract(discounts));

            // 状态
            productorder.setProSta(orderMutualInfo.getBlankSta());

            productorder.setPend(orderMutualInfo.getPend());

            productorder.setRefundSta(0);

            // 订单生成时间
            productorder.setOrderGenerationTime(blanketorder.getOrderGenerationTime());

            // 支付种类
            productorder.setPaymentType(orderMutualInfo.getPaymentType());

            // 支付方式
            productorder.setPaymentMethod(orderMutualInfo.getPaymentMethod());

            // 订单来源
            productorder.setOrderSource(orderMutualInfo.getOrderSource());

            // 版本，0基础版，1减配版，2标准版
            productorder.setVersion(orderMutualInfo.getVersion());

            super.packageInsertProperty(productorder);
            saveResourceInfo(productorder);

            prodetailService.productdetailMutual(productorder,orderMutualInfo);
            return productorder;
        }
        return null;
    }

    @Override
    @Transactional
    public Boolean deleteProductorder(Productorder resourceInfo){
        prodetailService.deleteProductdetailByProductorder(resourceInfo);
        repository.delete(resourceInfo);
        return true;
    }

    @Override
    @Transactional
    public Boolean updateResourceInfo(Productorder resourceInfo) {
        Productorder entity = selectResourceInfo(resourceInfo.getId());
        if(entity == null){
            return false;
        }
        UpdateUtil.copyNonNullProperties(resourceInfo,entity);
        entity = repository.save(entity);
        UpdateUtil.copyNonNullProperties(entity,resourceInfo);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteResourceInfo(List<Productorder> list) {
        for (Productorder entity: list) {
            entity.setDeleted(true);
            repository.save(entity);
        }
        return true;
    }

    @Override
    public Productorder selectResourceInfo(String resourceId) {
        Productorder resourceInfo = repository.findProductorderById(resourceId);
        return resourceInfo;
    }

    @Override
    public Page<Productorder> queryPage(Map<String, String> query) {
        Map<String, Object> queryCondition = queryUsualPageCondition(query);
        Specification<Productorder> specification = (Specification<Productorder>) queryCondition.get("specification");
        Pageable pageable = (Pageable) queryCondition.get("pageable");
        Page<Productorder> page;
        if (specification != null) {
            page = repository.findAll(specification, pageable);
        } else {
            page = repository.findAll(pageable);
        }
        return page;
    }

    @Override
    public List<Productorder> queryList(Map<String, String> query) {
        Specification<Productorder> specification = queryUsualListCondition(query);
        List<Productorder> list;
        if (specification != null) {
            list = repository.findAll(specification);
        } else {
            list = repository.findAll();
        }
        return list;
    }

    @Override
    public SalesStatistics querySalesStatistics(Map<String, String> query){
        List<Productorder> list = queryList(query);
        SalesStatistics salesStatistics = new SalesStatistics();
        if(list != null && list.size() > 0){

            //优惠
            salesStatistics.setDiscountAmount(new BigDecimal(0.00));

            //实际收款
            BigDecimal realAmount = list.stream().map(Productorder::getCopeWith).reduce(BigDecimal::add).get();

            // 销量
            BigDecimal salesVolume =list.stream().map(Productorder::getQuantity).reduce(BigDecimal::add).get();


            //订单量
            Integer orderQuantity = list.size();
            salesStatistics.setOrderQuantity(orderQuantity);

            // 退款
            BigDecimal refundAmount=list.stream().map(Productorder::getRefundMoney).reduce(BigDecimal::add).get();
            salesStatistics.setRefundAmount(refundAmount);

            // 退货量
            BigDecimal refundQuantity=list.stream().map(Productorder::getRefundQuantity).reduce(BigDecimal::add).get();

            salesStatistics.setRealAmount(realAmount.subtract(refundAmount));

            salesStatistics.setReceivableAmount(realAmount.subtract(refundAmount));

            salesStatistics.setSalesVolume(salesVolume.subtract(refundQuantity));

        }
        return salesStatistics;
    }

    @Override
    public PageInfo<DailyReport> queryDailyReport(PageEntity pageEntity, String oilStationId, String squadId, String date){
        Date d = DateUtil.minimumParse(date);
        PageInfo<DailyReport> pageInfo = new PageInfo(null);
        if(d != null) {
            Map<String, String> squads = getSquadDuty(oilStationId, squadId, date);
            if (squads != null && squads.containsKey("全部")) {
                List<DailyReport> list = prodetailService.queryDailyReportByDutyId(squads.get("全部"));
                pageInfo = new PageInfo<>(pageEntity,list);
            }
        }
        return pageInfo;
    }

    @Override
    public Map<String,List<DailyReport>> queryDailyReport(String oilStationId,String squadId,String date){

        Map<String,List<DailyReport>> dailyReportMap = new HashMap<>();
        Date d = DateUtil.minimumParse(date);
        if(d != null){
            Map<String,String> squads = getSquadDuty(oilStationId,squadId,date);
            if(squads != null){
                for (String key : squads.keySet()){
                    if(StringUtils.isNotBlank(squadId) && key.equals("全部")){
                        continue;
                    }

                    List<DailyReport> list = prodetailService.queryDailyReportByDutyId(squads.get(key));
                    if(list.size() > 0){
                        DailyReport dailyReport = new DailyReport();
                        dailyReport.setOther("合计                        ");
                        // 现金
                        BigDecimal cash =list.stream().map(DailyReport::getCash).reduce(BigDecimal::add).get();
                        dailyReport.setCash(cash);
                        // 刷卡
                        BigDecimal swipe =list.stream().map(DailyReport::getSwipe).reduce(BigDecimal::add).get();
                        dailyReport.setSwipe(swipe);
                        // 微信
                        BigDecimal wechat =list.stream().map(DailyReport::getWechat).reduce(BigDecimal::add).get();
                        dailyReport.setWechat(wechat);
                        // 支付宝
                        BigDecimal alipay =list.stream().map(DailyReport::getAlipay).reduce(BigDecimal::add).get();
                        dailyReport.setAlipay(alipay);
                        // 通用账户
                        BigDecimal commonAccount =list.stream().map(DailyReport::getCommonAccount).reduce(BigDecimal::add).get();
                        dailyReport.setCommonAccount(commonAccount);
                        // 汽油账户
                        BigDecimal gasolineAccount =list.stream().map(DailyReport::getGasolineAccount).reduce(BigDecimal::add).get();
                        dailyReport.setGasolineAccount(gasolineAccount);
                        // 柴油账户
                        BigDecimal dieselAccount =list.stream().map(DailyReport::getDieselAccount).reduce(BigDecimal::add).get();
                        dailyReport.setDieselAccount(dieselAccount);
                        // CNG账户
                        BigDecimal cngAccount =list.stream().map(DailyReport::getCngAccount).reduce(BigDecimal::add).get();
                        dailyReport.setCngAccount(cngAccount);
                        // 白条
                        BigDecimal iou =list.stream().map(DailyReport::getIou).reduce(BigDecimal::add).get();
                        dailyReport.setIou(iou);

                        // 销售总金额
                        BigDecimal total =list.stream().map(DailyReport::getTotal).reduce(BigDecimal::add).get();
                        dailyReport.setTotal(total);

                        // 销量
                        BigDecimal quantity =list.stream().map(DailyReport::getQuantity).reduce(BigDecimal::add).get();
                        dailyReport.setQuantity(quantity);
                        list.add(dailyReport);
                    }else {
                        DailyReport dailyReport = new DailyReport();
                        dailyReport.setOther("合计                        ");
                        list.add(dailyReport);
                    }

                    dailyReportMap.put(key,list);
                }
            }

        }

        if(dailyReportMap.size() == 0){
            List<DailyReport> list = new ArrayList<>();
            DailyReport dailyReport = new DailyReport();
            dailyReport.setOther("合计                        ");
            list.add(dailyReport);
            dailyReportMap.put("Sheet1",list);
        }

        return dailyReportMap;
    }

    private Map<String,String> getSquadDuty(String oilStationId,String squadId, String date){

        Map condition = new HashMap();
        condition.put("oilStationId", oilStationId);
        if (StringUtils.isNotBlank(squadId)) {
            condition.put("squadId", squadId);
        }
        condition.put("timeLabel", date + "@5##" + date + "@6");
        Map<String, String> squads = null;
        PageResultMsg<List<Map>> resultMsg = resourceClient.squadDuty(condition);
        if (resultMsg != null && resultMsg.getCode() == 10000) {
            List<Map> squadDutys = resultMsg.getData();
            squads = new HashMap<>();

            for (Map squadDuty : squadDutys) {
                if (squads.containsKey(squadDuty.get("name").toString())) {
                    squads.put(squadDuty.get("name").toString(), squads.get(squadDuty.get("name").toString()) + "," + squadDuty.get("id").toString());
                } else {
                    squads.put(squadDuty.get("name").toString(), squadDuty.get("id").toString());
                }

                if (squads.containsKey("全部")) {
                    squads.put("全部", squads.get("全部") + "," + squadDuty.get("id").toString());
                } else {
                    squads.put("全部", squadDuty.get("id").toString());
                }
            }
        }

        return squads;
    }

    @Override
    public Productorder selectProductByProductorderNo(String productorderNo){
        Productorder productorder=repository.findProductorderByProductorderNo(productorderNo);
        return productorder;
    }

    @Override
    public Productorder selectProductByblanketOrderId(String blanketOrderId) {
        Productorder productorder=repository.findProductorderByBlanketOrderId(blanketOrderId);
        return productorder;
    }

    @Override
    public SalesStatistics querySalesStatisticsByDutyId(String dutyId,String payment){
        Map map;
        if(StringUtils.isBlank(payment)){
            map = repository.findSalesStatisticsByDutyId(dutyId);
        }else {
            map = repository.findSalesStatisticsByDutyId(dutyId,payment);
        }

        SalesStatistics salesStatistics = new SalesStatistics();
        if(map != null){
            salesStatistics = MapObjUtil.map2Object(map,SalesStatistics.class);
        }
        return salesStatistics;
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
        Map map = repository.findConsumptionByRelatedId(relatedId,startDate,endDate);
        SalesStatistics salesStatistics = new SalesStatistics();
        if(map != null){
            salesStatistics = MapObjUtil.map2Object(map,SalesStatistics.class);
        }
        return salesStatistics;
    }

    @Override
    public List<ProductorderReport> queryProductorderReportList(Map<String, String> query){
        PageResultMsg<List<ProductorderReport>> resultMsg = resourceClient.productprderList(query);
        List<ProductorderReport> list = new ArrayList<>();
        if(resultMsg != null && resultMsg.getCode() == 10000){
            list = resultMsg.getData();
        }
        return list;
    }
}
