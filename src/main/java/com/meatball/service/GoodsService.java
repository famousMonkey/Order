package com.meatball.service;

import com.meatball.common.base.FacadeService;
import com.meatball.common.vo.ordervo.customize.RefundMutualInfo;
import com.meatball.entity.GoodsRefund;

import java.util.List;

public interface GoodsService extends FacadeService<GoodsRefund> {

    int refundMutual(RefundMutualInfo refundMutualInfo);

    String test(Long time);

    List<GoodsRefund> selectGoodsRefundsByBlanketOrderId(String blanketOrderId);

}
