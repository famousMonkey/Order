package com.meatball.dao;

import com.meatball.entity.GoodsRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GoodsReturnRepository extends JpaSpecificationExecutor,JpaRepository<GoodsRefund,String> {

    GoodsRefund findGoodsReturnById(String resourceId);
}
