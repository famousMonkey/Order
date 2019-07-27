package com.meatball.service;

import com.meatball.common.base.FacadeService;
import com.meatball.common.vo.basicsResource.OilsVo;
import com.meatball.common.vo.ordervo.customize.*;
import com.meatball.entity.Blanketorder;
import com.meatball.entity.Oilsorder;
import com.meatball.vo.report.OilsorderReport;
import com.meatball.vo.request.StatisticsCondition;

import java.util.List;
import java.util.Map;

public interface OilService extends FacadeService<Oilsorder> {

    Oilsorder oilsOrderMutual(OrderMutualInfo orderMutualInfo, Blanketorder blanketorder);

    Oilsorder selectOilorderByBlanketId(String blanketOrderId);

    ReceiptStatistics queryReceiptStatistics(StatisticsCondition condition);

    SalesStatistics querySalesStatistics(StatisticsCondition condition );

    SalesStatistics queryListSalesStatistics(Map<String, String> query);

    Oilsorder selectOilorderByoilsorderNo(String oilsorderNo);

    OrderStatistics queryOrderStatistics(StatisticsCondition condition);

    List<OilSalesStatistics> queryOilSalesStatisticsByRelatedId(String relatedId, String startDate, String endDate);

    Boolean deleteOilsOrder(Oilsorder oilsorder);

    List<OilsorderReport> queryOilsorderReportList(Map<String, String> query);

    OilOrderMutualInfo selectOrderMutual(String blanketOrderId);

    SalesStatistics queryConsumptionByRelatedId(String relatedId, String startDate, String endDate);

    List<Map> queryDailyReport( String oilStationId,String startDate, String endDate,List<OilsVo> oilsList);
}

