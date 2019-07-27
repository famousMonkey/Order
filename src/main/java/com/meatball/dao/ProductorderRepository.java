package com.meatball.dao;

import com.meatball.entity.Productorder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;

public interface ProductorderRepository extends JpaSpecificationExecutor, JpaRepository<Productorder, String> {

    Productorder findProductorderById(String resourceId);

    Productorder findProductorderByBlanketOrderId(String blanketOrderId);

    Productorder findProductorderByProductorderNo(String productorderNo);

    @Query(value = "select \n" +
            "\tsum(ifnull(cope_with,0)-ifnull(refund_money,0)) receivableAmount,\n" +
            "\tsum(0) discountAmount,\n" +
            "\tsum(ifnull(cope_with,0)-ifnull(refund_money,0)) realAmount,\n" +
            "\tsum(ifnull(refund_money,0)) refundAmount,\n" +
            "\tsum(1) orderQuantity,\n" +
            "\tsum(ifnull(quantity,0)-ifnull(refund_quantity,0)) salesVolume\n" +
            "\tfrom productorder\n" +
            "\twhere pro_sta = 2\n" +
            "\tand duty_id = :dutyId", nativeQuery = true)
    Map findSalesStatisticsByDutyId(@Param("dutyId") String dutyId);

    @Query(value = "select \n" +
            "\tsum(ifnull(cope_with,0)-ifnull(refund_money,0)) receivableAmount,\n" +
            "\tsum(0) discountAmount,\n" +
            "\tsum(ifnull(cope_with,0)-ifnull(refund_money,0)) realAmount,\n" +
            "\tsum(ifnull(refund_money,0)) refundAmount,\n" +
            "\tsum(1) orderQuantity,\n" +
            "\tsum(ifnull(quantity,0)-ifnull(refund_quantity,0)) salesVolume\n" +
            "\tfrom productorder\n" +
            "\twhere pro_sta = 2\n" +
            "\tand duty_id = :dutyId and (payment_type = :payment or payment_method = :payment)", nativeQuery = true)
    Map findSalesStatisticsByDutyId(@Param("dutyId") String dutyId,@Param("payment") String payment);

    @Query(value = "select \n" +
            "\tsum(ifnull(cope_with,0)-ifnull(refund_money,0)) receivableAmount,\n" +
            "\tsum(0) discountAmount,\n" +
            "\tsum(ifnull(cope_with,0)-ifnull(refund_money,0)) realAmount,\n" +
            "\tsum(ifnull(refund_money,0)) refundAmount,\n" +
            "\tsum(1) orderQuantity,\n" +
            "\tsum(ifnull(quantity,0)-ifnull(refund_quantity,0)) salesVolume\n" +
            "\tfrom productorder\n" +
            "\twhere pro_sta = 2 and (oil_station_id=:relatedId or org_id=:relatedId) and order_generation_time >= :startDate and order_generation_time<= :endDate", nativeQuery = true)
    Map findConsumptionByRelatedId(@Param("relatedId") String relatedId, @Param("startDate") String startDate, @Param("endDate") String endDate);

}
