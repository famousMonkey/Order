package com.meatball.service;

import com.meatball.common.base.FacadeService;
import com.meatball.common.vo.ordervo.customize.SalesStatistics;
import com.meatball.common.vo.ordervo.customize.OrderMutualInfo;
import com.meatball.common.vo.ordervo.customize.ReceiptStatistics;
import com.meatball.common.vo.payinfo.customize.PayNotifyVo;
import com.meatball.entity.Blanketorder;
import com.meatball.entity.OrderCancellation;

import java.util.List;
import java.util.Map;

public interface BlanketService extends FacadeService<Blanketorder> {

    int orderMutual(OrderMutualInfo orderMutualInfo);

    List<Blanketorder> queryUndoneByMemberId(String oilStationId,String memberId);

    Blanketorder getBlanketorderByblanketorderNo(String blanketorderNo);

    Map orderPaymentNotice(PayNotifyVo payNotifyVo);

    Blanketorder orderCancellation(OrderCancellation orderCancellation);

    SalesStatistics queryMemberConsumption(String memberId, String startDate, String endDate);

    SalesStatistics queryConsumptionByOilRelatedId(String relatedId, String startDate, String endDate);

    ReceiptStatistics queryReceiptStatisticsByRelatedId(String relatedId, String startDate, String endDate);

    OrderMutualInfo selectOrderMutual(String orderNo);
}

