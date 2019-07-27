package com.meatball.service.Impl;


import com.meatball.common.utils.CopyUtils;
import com.meatball.common.utils.MapObjUtil;
import com.meatball.common.utils.UpdateUtil;
import com.meatball.common.vo.ordervo.customize.OrderMutualInfo;
import com.meatball.common.vo.ordervo.customize.ProSalesStatistics;
import com.meatball.common.vo.ordervo.customize.ProductOrderMutualInfo;
import com.meatball.entity.Productdetail;
import com.meatball.common.base.BaseService;
import com.meatball.dao.ProdetailRepository;
import com.meatball.entity.Productorder;
import com.meatball.service.ProdetailService;
import com.meatball.vo.report.DailyReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName:ProdetailServiceImpl
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/11/3 14:00
 * @Version: 1.0
 **/
@Service
@Slf4j
public class ProdetailServiceImpl extends BaseService<Productdetail> implements ProdetailService {
    @Autowired
    private ProdetailRepository prodetailRepository;

    @Override
    @Transactional
    public Boolean saveResourceInfo(Productdetail resourceInfo) {
        prodetailRepository.save(resourceInfo);
        return true;
    }

    @Override
    @Transactional
    public Boolean updateResourceInfo(Productdetail resourceInfo) {
        Productdetail entity = selectResourceInfo(resourceInfo.getId());
        if(entity == null){
            return false;
        }
        UpdateUtil.copyNonNullProperties(resourceInfo,entity);
        prodetailRepository.save(entity);
        return true;
    }

    @Override
    @Transactional
    public Boolean updateResourceInfos(List<Productdetail> resourceInfos) {
        prodetailRepository.saveAll(resourceInfos);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteProductdetailByProductorder(Productorder productorder){
        List<Productdetail> productdetails = selectProductdetailsByProrderId(productorder.getId());
        if(productdetails != null){
            for (Productdetail entity: productdetails) {
                prodetailRepository.delete(entity);
            }
        }
        return  true;
    }

    @Override
    @Transactional
    public Boolean productdetailMutual(Productorder productorder,OrderMutualInfo orderMutualInfo){
        List<Productdetail> productdetails = new ArrayList<>();
        for (ProductOrderMutualInfo product: orderMutualInfo.getProductOrders()){
            Productdetail productdetail = new Productdetail();
            productdetail.setName(product.getProductName());
            productdetail.setOrgId(orderMutualInfo.getOrgId());
            productdetail.setOilStationId(orderMutualInfo.getOilStationId());
            productdetail.setMemberId(orderMutualInfo.getMemberId());
            productdetail.setProrderId(productorder.getId());
            productdetail.setBlanketOrderId(productorder.getBlanketOrderId());
            productdetail.setProductId(product.getProductId());
            productdetail.setProductBarCode(product.getProductBarCode());
            productdetail.setProductName(product.getProductName());
            productdetail.setPrice(product.getPrice());
            productdetail.setQuantity(product.getQuantity());

            //优惠
            productdetail.setDetailId(product.getDetailId());
            productdetail.setDetailName(product.getDetailName());
            productdetail.setDiscounts(product.getDiscounts());

            //商品金额 = 数量 * 现价
            BigDecimal proMoney = product.getQuantity().multiply(product.getPrice()).setScale(2, RoundingMode.HALF_UP);
            productdetail.setProMoney(proMoney);
            //实付金额 = 商品金额-优惠
            productdetail.setCopeWith(proMoney.subtract(product.getDiscounts()));

            // 退款
            productdetail.setRefundMoney(new BigDecimal(0));
            productdetail.setRefundQuantity(new BigDecimal(0));

            productdetail.setSquadId(productorder.getSquadId());
            productdetail.setSalesperson(productorder.getSalesperson());

            productdetail.setProdeSta(productorder.getProSta());
            productdetail.setPend(orderMutualInfo.getPend());
            productdetail.setRefundSta(0);
            super.packageInsertProperty(productdetail);
            saveResourceInfo(productdetail);
            productdetails.add(productdetail);
        }
        return true;
    }

    @Override
    public  List<ProductOrderMutualInfo> selectOrderMutual(String productorderId){
        List<ProductOrderMutualInfo> productOrderMutualInfos = null;
        List<Productdetail> productdetails = selectProductdetailsByProrderId(productorderId);
        if(productdetails != null && productdetails.size() >0){
            productOrderMutualInfos = new ArrayList<>();
            for (Productdetail productdetail : productdetails){
                ProductOrderMutualInfo productOrderMutualInfo = CopyUtils.copyObject(ProductOrderMutualInfo.class,productdetail);
                productOrderMutualInfos.add(productOrderMutualInfo);
            }
        }

        return productOrderMutualInfos;
    }

    @Override
    public List<Productdetail> selectProductdetailsByProrderId(String prorderId){
        return prodetailRepository.findProductdetailsByProrderId(prorderId);
    }

    @Override
    public List<Productdetail> selectProductdetailsByBlanketOrderId(String blanketOrderId){
        return prodetailRepository.findProductdetailsByBlanketOrderId(blanketOrderId);
    }

    @Override
    @Transactional
    public Boolean deleteResourceInfo(List<Productdetail> list) {
        for (Productdetail entity: list) {
            entity.setDeleted(true);
            prodetailRepository.save(entity);
        }
        return true;
    }

    @Override
    public Productdetail selectResourceInfo(String resourceId) {
        Productdetail resourceInfo = prodetailRepository.findProductdetailById(resourceId);
        return resourceInfo;
    }

    @Override
    public Page<Productdetail> queryPage(Map<String, String> query) {
        Map<String, Object> queryCondition = queryUsualPageCondition(query);
        Specification<Productdetail> specification = (Specification<Productdetail>) queryCondition.get("specification");
        Pageable pageable = (Pageable) queryCondition.get("pageable");
        Page<Productdetail> page;
        if (specification != null) {
            page = prodetailRepository.findAll(specification, pageable);
        } else {
            page = prodetailRepository.findAll(pageable);
        }
        return page;
    }

    @Override
    public List<Productdetail> queryList(Map<String, String> query) {
        Specification<Productdetail> specification = queryUsualListCondition(query);
        List<Productdetail> list;
        if (specification != null) {
            list = prodetailRepository.findAll(specification);
        } else {
            list = prodetailRepository.findAll();
        }
        return list;
    }

    @Override
    public List<DailyReport> queryDailyReportByDutyId(String dutyIds){
        List<Map> maps = prodetailRepository.findDailyReportByDutyId(dutyIds.split(","));
        List<DailyReport> list = new ArrayList<>();
        for (Map map : maps){
            if(map != null){
                list.add(MapObjUtil.map2Object(map,DailyReport.class));
            }
        }
        return list;
    }

    @Override
    public List<ProSalesStatistics> queryProSalesStatisticsByRelatedId(String relatedId,String startDate,String endDate){
        List<Map> maps = prodetailRepository.findProSalesStatisticsByRelatedId(relatedId,startDate,endDate);
        List<ProSalesStatistics> list = new ArrayList<>();
        for (Map map : maps){
            if(map != null){
                list.add(MapObjUtil.map2Object(map,ProSalesStatistics.class));
            }
        }
        return list;
    }
}
