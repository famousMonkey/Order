package com.meatball.dao;

import com.meatball.entity.Blanketorder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;

public interface BlanketRepository extends JpaSpecificationExecutor,JpaRepository<Blanketorder,String> {

    Blanketorder findBlanketorderById(String resourceId);

    Blanketorder findBlanketorderByBlanketorderNo(String blanketorderNo);

    @Query(value = "select \n" +
            "\tsum(ifnull(a.order_sum,0))-sum(ifnull(a.refund_order,0)) receivableAmount,\n" +
            "\tsum(CASE WHEN b.refund_sta=0 THEN ifnull(b.discounts,0) ELSE 0 END) discountAmount,\n" +
            "\tsum(ifnull(a.cope_with,0))-sum(ifnull(a.refund_money,0)) realAmount,\n" +
            "\tsum(ifnull(a.refund_money,0)) refundAmount,\n" +
            "\tsum(1) orderQuantity,\n" +
            "\t0 salesVolume\n" +
            "\tfrom blanketorder a left join oilorder b on a.res_id = b.blanket_order_id\n" +
            "\twhere a.blank_sta = 2 and a.member_id=:memberId and a.order_generation_time >= :startDate and a.order_generation_time<= :endDate", nativeQuery = true)
    Map findMemberConsumption(@Param("memberId") String memberId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query(value = "select \n" +
            "\tsum(ifnull(a.order_sum,0))-sum(ifnull(a.refund_order,0)) receivableAmount,\n" +
            "\tsum(CASE WHEN b.refund_sta=0 THEN ifnull(b.discounts,0) ELSE 0 END) discountAmount,\n" +
            "\tsum(ifnull(a.cope_with,0))-sum(ifnull(a.refund_money,0)) realAmount,\n" +
            "\tsum(ifnull(a.refund_money,0)) refundAmount,\n" +
            "\tsum(1) orderQuantity,\n" +
            "\t0 salesVolume\n" +
            "\tfrom blanketorder a left join oilorder b on a.res_id = b.blanket_order_id\n" +
            "\twhere a.blank_sta = 2 and (a.oil_station_id=:relatedId or a.org_id=:relatedId) and a.order_generation_time >= :startDate and a.order_generation_time<= :endDate", nativeQuery = true)
    Map findConsumptionByOilRelatedId(@Param("relatedId") String relatedId, @Param("startDate") String startDate, @Param("endDate") String endDate);


    @Query(value = "select \n" +
            "\tsum(CASE WHEN payment_type=1 THEN ifnull(cope_with,0) - ifnull(refund_money,0) ELSE 0 END) cash,\n" +
            "\tsum(CASE WHEN payment_type=1 THEN 1 ELSE 0 END) cashOrder,\n" +
            "\tsum(CASE WHEN payment_type=2 THEN ifnull(cope_with,0) - ifnull(refund_money,0) ELSE 0 END) swipe,\n" +
            "\tsum(CASE WHEN payment_type=2 THEN 1 ELSE 0 END) swipeOrder,\n" +
            "\tsum(CASE WHEN payment_method=31 THEN ifnull(cope_with,0) - ifnull(refund_money,0)  ELSE 0 END) wechat,\n" +
            "\tsum(CASE WHEN payment_method=31 THEN 1 ELSE 0 END) wechatOrder,\n" +
            "\tsum(CASE WHEN payment_method=32 THEN ifnull(cope_with,0) - ifnull(refund_money,0) ELSE 0 END) alipay,\n" +
            "\tsum(CASE WHEN payment_method=32 THEN 1 ELSE 0 END) alipayOrder,\n" +
            "\tsum(CASE WHEN payment_type=4 THEN ifnull(cope_with,0) - ifnull(refund_money,0) ELSE 0 END) account,\n" +
            "\tsum(CASE WHEN payment_type=4 THEN 1 ELSE 0 END) accountOrder,\n" +
            "\tsum(CASE WHEN payment_type=5 THEN ifnull(cope_with,0) - ifnull(refund_money,0) ELSE 0 END) iou,\n" +
            "\tsum(CASE WHEN payment_type=5 THEN 1 ELSE 0 END) iouOrder,\n" +
            "\tsum(ifnull(cope_with,0) - ifnull(refund_money,0)) total,\n" +
            "\tsum(1) totalOrder\n" +
            "from blanketorder \n" +
            "where blank_sta = 2 and (oil_station_id = :relatedId or org_id=:relatedId) and order_generation_time >= :startDate and order_generation_time<= :endDate", nativeQuery = true)
    Map findReceiptStatisticsByRelatedId(@Param("relatedId") String relatedId, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
