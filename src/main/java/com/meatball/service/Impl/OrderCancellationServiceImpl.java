package com.meatball.service.Impl;

import com.meatball.common.base.BaseService;
import com.meatball.dao.OrderCancellRepository;
import com.meatball.entity.OrderCancellation;
import com.meatball.service.OrderCancellationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderCancellationServiceImpl extends BaseService<OrderCancellation> implements OrderCancellationService {
    @Autowired
    private OrderCancellRepository orderCancellRepository;
    @Override
    public Boolean saveResourceInfo(OrderCancellation resourceInfo) {
        orderCancellRepository.save(resourceInfo);
        return true;
    }

    @Override
    public Boolean updateResourceInfo(OrderCancellation resourceInfo) {
        return null;
    }

    @Override
    public Boolean deleteResourceInfo(List<OrderCancellation> list) {
        return null;
    }

    @Override
    public OrderCancellation selectResourceInfo(String resourceId) {
        return null;
    }

    @Override
    public Page<OrderCancellation> queryPage(Map<String, String> query) {
        return null;
    }

    @Override
    public List<OrderCancellation> queryList(Map<String, String> query) {
        return null;
    }
}
