package com.meatball.dao;

import com.meatball.entity.OrderCancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderCancellRepository extends JpaSpecificationExecutor, JpaRepository<OrderCancellation,String> {
}
