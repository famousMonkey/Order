package com.meatball.service;

import com.meatball.common.base.FacadeService;
import com.meatball.common.base.PageEntity;
import com.meatball.common.base.PageInfo;
import com.meatball.common.vo.ordervo.customize.OrderMutualInfo;
import com.meatball.common.vo.ordervo.customize.SalesStatistics;
import com.meatball.entity.Blanketorder;
import com.meatball.entity.Productorder;
import com.meatball.vo.report.DailyReport;
import com.meatball.vo.report.ProductorderReport;

import java.util.List;
import java.util.Map;

public interface PorderService extends FacadeService<Productorder> {

    Productorder productOrderMutual(OrderMutualInfo orderMutualInfo, Blanketorder blanketorder);

    Productorder selectProductByblanketOrderId(String blanketOrderId);

    Productorder selectProductByProductorderNo(String productorderNo);

    Boolean deleteProductorder(Productorder productorder);

    SalesStatistics querySalesStatisticsByDutyId(String dutyId,String payment);

    SalesStatistics querySalesStatistics(Map<String, String> query);

    List<ProductorderReport> queryProductorderReportList(Map<String, String> query);

    PageInfo<DailyReport> queryDailyReport(PageEntity pageEntity, String oilStationId, String squadId, String date);

    Map<String,List<DailyReport>> queryDailyReport(String oilStationId,String squadId,String date);

    SalesStatistics queryConsumptionByRelatedId(String relatedId, String startDate, String endDate);
}
