package com.meatball.service;

import com.meatball.common.base.FacadeService;
import com.meatball.entity.Blanketorder;
import com.meatball.entity.InvoiceRecord;
import com.meatball.vo.InvoiceCancelVo;

public interface InvoiceRecordService extends FacadeService<InvoiceRecord> {
    InvoiceRecord queryByOrderId(String orderId);
    InvoiceRecord queryByInvoiceNum(String invoiceNum);
    Boolean invoiceCancel(InvoiceRecord invoiceRecord,InvoiceCancelVo invoiceCancel);
    Boolean invoiceIssue(InvoiceRecord invoiceRecord,Blanketorder order );
}
