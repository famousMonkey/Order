package com.meatball.service;

import com.meatball.common.base.FacadeService;
import com.meatball.common.vo.ordervo.customize.OrderMutualInfo;
import com.meatball.common.vo.ordervo.customize.ProSalesStatistics;
import com.meatball.common.vo.ordervo.customize.ProductOrderMutualInfo;
import com.meatball.entity.Productdetail;
import com.meatball.entity.Productorder;
import com.meatball.vo.report.DailyReport;

import java.util.List;

public interface ProdetailService extends FacadeService<Productdetail> {

    Boolean deleteProductdetailByProductorder(Productorder productorder);

    List<Productdetail> selectProductdetailsByProrderId(String prorderId);

    List<Productdetail> selectProductdetailsByBlanketOrderId(String blanketOrderId);

    Boolean productdetailMutual(Productorder productorder, OrderMutualInfo orderMutualInfo);

    List<ProductOrderMutualInfo> selectOrderMutual(String productorderId);

    Boolean updateResourceInfos(List<Productdetail> resourceInfos);

    List<DailyReport> queryDailyReportByDutyId(String dutyIds);

    List<ProSalesStatistics> queryProSalesStatisticsByRelatedId(String relatedId, String startDate, String endDate);
}
