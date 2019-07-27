package com.meatball.service;

import com.meatball.common.base.FacadeService;
import com.meatball.common.vo.ordervo.customize.RefundStatistics;
import com.meatball.entity.DetailRefund;

public interface DetailReturnService extends FacadeService<DetailRefund> {
    RefundStatistics queryRefundStatisticsByDutyId(String dutyId);
}
