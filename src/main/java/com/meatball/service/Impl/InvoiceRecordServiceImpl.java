package com.meatball.service.Impl;

import com.codingapi.txlcn.tc.annotation.TxcTransaction;
import com.meatball.common.base.BaseService;
import com.meatball.common.utils.UpdateUtil;
import com.meatball.dao.InvoiceRecordRepository;
import com.meatball.entity.Blanketorder;
import com.meatball.entity.GoodsRefund;
import com.meatball.entity.InvoiceRecord;
import com.meatball.service.BlanketService;
import com.meatball.service.InvoiceRecordService;
import com.meatball.vo.InvoiceCancelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class InvoiceRecordServiceImpl extends BaseService<InvoiceRecord> implements InvoiceRecordService {

    @Autowired
    private InvoiceRecordRepository invoiceRecordRepository;
    @Autowired
    private BlanketService blanketService;

    @Override
    @Transactional
    public Boolean saveResourceInfo(InvoiceRecord resourceInfo) {
        invoiceRecordRepository.save(resourceInfo);
        return true;
    }

    @Override
    @Transactional
    public  Boolean invoiceIssue(InvoiceRecord invoiceRecord,Blanketorder order){
        invoiceRecord.setStatus(0);
        invoiceRecord.setMemberId(order.getMemberId());
        invoiceRecord.setOilStationId(order.getOilStationId());
        invoiceRecord.setMemberPhone(order.getMemberPhone());
        invoiceRecord.setCancelOperator(null);
        invoiceRecord.setCancelReason(null);
        super.packageInsertProperty(invoiceRecord);

        saveResourceInfo(invoiceRecord);

        Blanketorder blanketorder = new Blanketorder();
        blanketorder.setId(order.getId());
        blanketorder.setInvoiceSta(1);
        super.packageUpdateProperty(blanketorder);
        blanketService.updateResourceInfo(blanketorder);
        return true;
    }

    @Override
    @Transactional
    public  Boolean invoiceCancel(InvoiceRecord invoiceRecord,InvoiceCancelVo invoiceCancel){
        InvoiceRecord entity = new InvoiceRecord();
        entity.setId(invoiceRecord.getId());
        entity.setCancelReason(invoiceCancel.getCancelReason());
        entity.setCancelDate(new Date());
        entity.setCancelOperator(invoiceCancel.getCancelOperator());
        entity.setStatus(1);
        super.packageUpdateProperty(entity);
        updateResourceInfo(entity);

        Blanketorder blanketorder = new Blanketorder();
        blanketorder.setId(invoiceRecord.getOrderId());
        blanketorder.setInvoiceSta(2);
        super.packageUpdateProperty(blanketorder);
        blanketService.updateResourceInfo(blanketorder);
        return true;
    }

    @Override
    public InvoiceRecord selectResourceInfo(String resourceId) {
        InvoiceRecord resourceInfo = invoiceRecordRepository.getOne(resourceId);
        return resourceInfo;
    }

    @Override
    public InvoiceRecord queryByOrderId(String orderId){
        InvoiceRecord resourceInfo = invoiceRecordRepository.findByOrderId(orderId);
        return resourceInfo;
    }

    @Override
    public InvoiceRecord queryByInvoiceNum(String invoiceNum){
        InvoiceRecord resourceInfo = invoiceRecordRepository.findByInvoiceNum(invoiceNum);
        return resourceInfo;
    }

    @Override
    public Page<InvoiceRecord> queryPage(Map<String, String> query) {
        Map<String, Object> queryCondition = queryUsualPageCondition(query);
        Specification<GoodsRefund> specification = (Specification<GoodsRefund>) queryCondition.get("specification");
        Pageable pageable = (Pageable) queryCondition.get("pageable");
        Page<InvoiceRecord> page;
        if (specification != null) {
            page = invoiceRecordRepository.findAll(specification, pageable);
        } else {
            page = invoiceRecordRepository.findAll(pageable);
        }
        return page;
    }

    @Override
    public List<InvoiceRecord> queryList(Map<String, String> query) {
        Specification<InvoiceRecord> specification = queryUsualListCondition(query);
        List<InvoiceRecord> list;
        if (specification != null) {
            list = invoiceRecordRepository.findAll(specification);
        } else {
            list = invoiceRecordRepository.findAll();
        }
        return list;
    }

    @Override
    @TxcTransaction
    @Transactional
    public Boolean updateResourceInfo(InvoiceRecord resourceInfo) {
        InvoiceRecord entity = selectResourceInfo(resourceInfo.getId());
        if(entity == null){
            return false;
        }
        UpdateUtil.copyNonNullProperties(resourceInfo,entity);
        invoiceRecordRepository.save(entity);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteResourceInfo(List<InvoiceRecord> list) {
        for (InvoiceRecord entity: list) {
            entity.setDeleted(true);
            invoiceRecordRepository.save(entity);
        }
        return true;
    }
}
