package com.meatball.dao;

import com.meatball.entity.Oilsorder;
import com.meatball.vo.request.StatisticsCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface OilRepository extends JpaSpecificationExecutor,JpaRepository<Oilsorder,String> {

    Oilsorder findOilsorderById(String resourceId);

    Oilsorder findOilsorderByOilsorderNo(String oilsorderNo);

    Oilsorder findOilsorderByBlanketOrderId(String blanketOrderId);

    @Query(value = "select \n" +
            "\tsum(CASE WHEN oil_sta=0 THEN 1 ELSE 0 END) proceed,\n" +
            "\tsum(CASE WHEN oil_sta=1 THEN 1 ELSE 0 END) unpaid,\n" +
            "\tsum(CASE WHEN oil_sta=2 THEN 1 ELSE 0 END) success,\n" +
            "\tsum(CASE WHEN oil_sta=3 THEN 1 ELSE 0 END) cancel,\n" +
            "\tsum(CASE WHEN refund_sta=1 THEN 1 ELSE 0 END) refund,\n" +
            "\tcount(*) total\n" +
            "\tfrom oilorder \n" +
            "\twhere (:#{#condition.oilsId} is null or oils_id = :#{#condition.oilsId}) \n" +
            "\tand (:#{#condition.oilGunId} is null or oil_gun_id = :#{#condition.oilGunId}) \n" +
            "\tand (:#{#condition.staffDutyId} is null or staff_duty_id = :#{#condition.staffDutyId })" +
            "", nativeQuery = true)
    Map findOrderStatistics(@Param("condition") StatisticsCondition condition);

    @Query(value = "select \n" +
            "\tsum(CASE WHEN payment_type=1 THEN ifnull(cope_with,0) - ifnull(refund_money,0) ELSE 0 END) cash,\n" +
            "\tsum(CASE WHEN payment_type=1 THEN 1 ELSE 0 END) cashOrder,\n" +
            "\tsum(CASE WHEN payment_type=2 THEN ifnull(cope_with,0) - ifnull(refund_money,0) ELSE 0 END) swipe,\n" +
            "\tsum(CASE WHEN payment_type=2 THEN 1 ELSE 0 END) swipeOrder,\n" +
            "\tsum(CASE WHEN payment_method=31 THEN ifnull(cope_with,0) - ifnull(refund_money,0) ELSE 0 END) wechat,\n" +
            "\tsum(CASE WHEN payment_method=31 THEN 1 ELSE 0 END) wechatOrder,\n" +
            "\tsum(CASE WHEN payment_method=32 THEN ifnull(cope_with,0) - ifnull(refund_money,0) ELSE 0 END) alipay,\n" +
            "\tsum(CASE WHEN payment_method=32 THEN 1 ELSE 0 END) alipayOrder,\n" +
            "\tsum(CASE WHEN payment_type=4 THEN ifnull(cope_with,0) - ifnull(refund_money,0) ELSE 0 END) account,\n" +
            "\tsum(CASE WHEN payment_type=4 THEN 1 ELSE 0 END) accountOrder,\n" +
            "\tsum(CASE WHEN payment_type=5 THEN ifnull(cope_with,0) - ifnull(refund_money,0)  ELSE 0 END) iou,\n" +
            "\tsum(CASE WHEN payment_type=5 THEN 1 ELSE 0 END) iouOrder,\n" +
            "\tsum(ifnull(cope_with,0) - ifnull(refund_money,0)) total,\n" +
            "\tsum(1) totalOrder\n" +
            "\tfrom oilorder \n" +
            "\twhere oil_sta = 2 \n" +
            "\tand (:#{#condition.dutyId} is null or duty_id = :#{#condition.dutyId}) \n" +
            "\tand (:#{#condition.refuelStaffId} is null or refuel_staff_id = :#{#condition.refuelStaffId}) \n" +
            "\tand (:#{#condition.oilGunId} is null or oil_gun_id = :#{#condition.oilGunId}) \n" +
            "\tand (:#{#condition.staffDutyId} is null or staff_duty_id = :#{#condition.staffDutyId })" +
            "",nativeQuery = true)
    Map findReceiptStatistics(@Param("condition") StatisticsCondition condition);

    @Query(value = "select \n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(oil_money,0) ELSE 0 END) receivableAmount,\n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(discounts,0) ELSE 0 END) discountAmount,\n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(cope_with,0) ELSE 0 END) realAmount,\n" +
            "\tsum(ifnull(refund_money,0)) refundAmount,\n" +
            "\tsum(1) orderQuantity,\n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(quantity,0) ELSE 0 END) salesVolume\n" +
            "\tfrom oilorder\n" +
            "\twhere oil_sta = 2 \n" +
            "\tand (:#{#condition.dutyId} is null or duty_id = :#{#condition.dutyId}) \n" +
            "\tand (:#{#condition.staffDutyId} is null or staff_duty_id = :#{#condition.staffDutyId })\n" +
            "\tand (:#{#condition.oilGunId} is null or oil_gun_id = :#{#condition.oilGunId}) \n" +
            "\tand (:#{#condition.payment} is null or (payment_type = :#{#condition.payment} or payment_method = :#{#condition.payment}))" +
            "", nativeQuery = true)
    Map findSalesStatistics(@Param("condition") StatisticsCondition condition);

    @Query(value = "select oils_name oilsName,\n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(oil_money,0) ELSE 0 END) receivableAmount,\n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(discounts,0) ELSE 0 END) discountAmount,\n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(cope_with,0) ELSE 0 END) realAmount,\n" +
            "\tsum(ifnull(refund_money,0)) refundAmount,\n" +
            "\tsum(1) orderQuantity,\n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(quantity,0) ELSE 0 END) salesVolume\n" +
            "\tfrom oilorder\n" +
            "\twhere oil_sta = 2 \n" +
            "\tand (oil_station_id = :relatedId or org_id= :relatedId)" +
            "\tand order_generation_time >= :startDate and order_generation_time<= :endDate " +
            "\tgroup by oils_name", nativeQuery = true)
    List<Map> findOilSalesStatisticsByRelatedId(@Param("relatedId") String relatedId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(value = "select date_format(order_generation_time,'%Y-%c-%d') orderDate," +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(oil_money,0) ELSE 0 END) receivableAmount,\n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(discounts,0) ELSE 0 END) discountAmount,\n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(cope_with,0) ELSE 0 END) realAmount,\n" +
            "\tsum(ifnull(refund_money,0)) refundAmount,\n" +
            "\tsum(1) orderQuantity,\n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(quantity,0) ELSE 0 END) salesVolume\n" +
            "\tfrom oilorder\n" +
            "\twhere oil_sta = 2\n" +
            "\tand oils_id = :oilsId" +
            "\tand order_generation_time >= :startDate and order_generation_time<= :endDate " +
            "\tgroup by  date_format(order_generation_time,'%Y-%c-%d')", nativeQuery = true)
    List<Map> findDateSalesStatistics(@Param("oilsId") String oilsId, @Param("startDate") String startDate, @Param("endDate") String endDate);


    @Query(value = "select  date_format(order_generation_time,'%Y-%c-%d') orderDate,\n" +
            "\tsum(CASE WHEN payment_type=1 THEN ifnull(cope_with,0) ELSE 0 END) cash,\n" +
            "\tsum(CASE WHEN payment_type=2 THEN ifnull(cope_with,0) ELSE 0 END) swipe,\n" +
            "\tsum(CASE WHEN payment_method=31 THEN ifnull(cope_with,0) ELSE 0 END) wechat,\n" +
            "\tsum(CASE WHEN payment_method=32 THEN ifnull(cope_with,0) ELSE 0 END) alipay,\n" +
            "\tsum(CASE WHEN payment_type=4 THEN ifnull(cope_with,0) ELSE 0 END) account,\n" +
            "\tsum(CASE WHEN payment_method=40 THEN ifnull(cope_with,0) ELSE 0 END) commonAccount,\n" +
            "\tsum(CASE WHEN payment_method=41 THEN ifnull(cope_with,0) ELSE 0 END) gasolineAccount,\n" +
            "\tsum(CASE WHEN payment_method=42 THEN ifnull(cope_with,0) ELSE 0 END) dieselAccount,\n" +
            "\tsum(CASE WHEN payment_method=43 THEN ifnull(cope_with,0) ELSE 0 END) cngAccount,\n" +
            "\tsum(CASE WHEN payment_type=5 THEN ifnull(cope_with,0) ELSE 0 END) iou,\n" +
            "\tsum(ifnull(cope_with,0)) total,\n" +
            "\tsum(1) totalOrder\n" +
            "\tfrom oilorder \n" +
            "\twhere oil_sta = 2 and refund_sta=0 " +
            "\tand oil_station_id = :oilStationId " +
            "\tand order_generation_time >= :startDate and order_generation_time<= :endDate " +
            "\tgroup by  date_format(order_generation_time,'%Y-%c-%d') order by order_generation_time", nativeQuery = true)
    List<Map> findDateReceiptStatistics(@Param("oilStationId") String oilStationId, @Param("startDate") String startDate, @Param("endDate") String endDate);


    @Query(value = "select \n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(oil_money,0) ELSE 0 END) receivableAmount,\n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(discounts,0) ELSE 0 END) discountAmount,\n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(cope_with,0) ELSE 0 END) realAmount,\n" +
            "\tsum(ifnull(refund_money,0)) refundAmount,\n" +
            "\tsum(1) orderQuantity,\n" +
            "\tsum(CASE WHEN refund_sta=0 THEN ifnull(quantity,0) ELSE 0 END) salesVolume\n" +
            "\tfrom oilorder\n" +
            "\twhere oil_sta = 2 and (oil_station_id=:relatedId or org_id=:relatedId) and order_generation_time >= :startDate and order_generation_time<= :endDate", nativeQuery = true)
    Map findConsumptionByRelatedId(@Param("relatedId") String relatedId, @Param("startDate") String startDate, @Param("endDate") String endDate);


}
