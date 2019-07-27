package com.meatball.dao;

import com.meatball.entity.InvoiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvoiceRecordRepository extends JpaSpecificationExecutor,JpaRepository<InvoiceRecord,String> {
    InvoiceRecord findByOrderId(String orderId);

    InvoiceRecord findByInvoiceNum(String invoiceNum);
}
