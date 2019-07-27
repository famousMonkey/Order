package com.meatball.service.Impl;

import com.meatball.common.base.BaseService;
import com.meatball.common.utils.MapObjUtil;
import com.meatball.common.utils.UpdateUtil;
import com.meatball.common.vo.ordervo.customize.RefundStatistics;
import com.meatball.dao.DetailReturnRepository;
import com.meatball.entity.DetailRefund;
import com.meatball.service.DetailReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class DetailReturnServiceImpl extends BaseService<DetailRefund> implements DetailReturnService {

    @Autowired
    private DetailReturnRepository detailReturnRepository;

    @Override
    @Transactional
    public Boolean saveResourceInfo(DetailRefund resourceInfo) {
        detailReturnRepository.save(resourceInfo);
        return true;
    }

    @Override
    @Transactional
    public Boolean updateResourceInfo(DetailRefund resourceInfo) {
        DetailRefund entity = selectResourceInfo(resourceInfo.getId());
        if(entity == null){
            return false;
        }
        UpdateUtil.copyNonNullProperties(resourceInfo,entity);
        detailReturnRepository.save(entity);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteResourceInfo(List<DetailRefund> list) {
        for (DetailRefund entity: list) {
            entity.setDeleted(true);
            detailReturnRepository.save(entity);
        }
        return true;
    }

    @Override
    public DetailRefund selectResourceInfo(String resourceId) {
        DetailRefund resourceInfo = detailReturnRepository.findDetailReturnById(resourceId);
        return resourceInfo;
    }

    @Override
    public RefundStatistics queryRefundStatisticsByDutyId(String dutyId){
        Map map = detailReturnRepository.findRefundStatisticsByDutyId(dutyId);
        RefundStatistics refundStatistics = new RefundStatistics();
        if(map != null){
            refundStatistics = MapObjUtil.map2Object(map,RefundStatistics.class);
        }
        return refundStatistics;
    }

    @Override
    public Page<DetailRefund> queryPage(Map<String, String> query) {
        Map<String, Object> queryCondition = queryUsualPageCondition(query);
        Specification<DetailRefund> specification = (Specification<DetailRefund>) queryCondition.get("specification");
        Pageable pageable = (Pageable) queryCondition.get("pageable");
        Page<DetailRefund> page;
        if (specification != null) {
            page = detailReturnRepository.findAll(specification, pageable);
        } else {
            page = detailReturnRepository.findAll(pageable);
        }
        return page;
    }
    @Override
    public List<DetailRefund> queryList(Map<String, String> query) {
        Specification<DetailRefund> specification = queryUsualListCondition(query);
        List<DetailRefund> list;
        if (specification != null) {
            list = detailReturnRepository.findAll(specification);
        } else {
            list = detailReturnRepository.findAll();
        }
        return list;
    }
}
