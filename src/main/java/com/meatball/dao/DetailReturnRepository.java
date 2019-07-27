package com.meatball.dao;

import com.meatball.entity.DetailRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;

public interface DetailReturnRepository extends JpaSpecificationExecutor,JpaRepository<DetailRefund,String> {

    DetailRefund findDetailReturnById(String resourceId);

    @Query(value = "SELECT \n" +
            "sum(CASE WHEN goods_type=1 THEN ifnull(money,0) ELSE 0 END) oilMoney,\n" +
            "sum(CASE WHEN goods_type=2 THEN ifnull(money,0) ELSE 0 END) productMoney,\n" +
            "count(distinct case when goods_type=1 then detail_refund_no end) as oilOrder,\n" +
            "count(distinct case when goods_type=2 then detail_refund_no end) as productOrder\n" +
            "FROM detail_refund\n" +
            "where duty_id = :dutyId", nativeQuery = true)
    Map findRefundStatisticsByDutyId(@Param("dutyId") String dutyId);

}
