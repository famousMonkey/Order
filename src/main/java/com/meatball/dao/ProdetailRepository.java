package com.meatball.dao;

import com.meatball.entity.Productdetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface ProdetailRepository extends JpaSpecificationExecutor,JpaRepository<Productdetail,String> {
    Productdetail findProductdetailById(String resourceId);

    List<Productdetail> findProductdetailsByProrderId(String prorderId);

    List<Productdetail> findProductdetailsByBlanketOrderId(String blanketOrderId);

    @Query(value = "select \n" +
            "\ta.product_id productId,a.product_bar_code productBarCode, a.product_name productName,\n" +
            "\tsum(CASE WHEN b.payment_type=1 THEN ifnull(a.cope_with,0) ELSE 0 END) cash,\n" +
            "\tsum(CASE WHEN b.payment_type=2 THEN ifnull(a.cope_with,0) ELSE 0 END) swipe,\n" +
            "\tsum(CASE WHEN b.payment_method=31 THEN ifnull(a.cope_with,0) ELSE 0 END) wechat,\n" +
            "\tsum(CASE WHEN b.payment_method=32 THEN ifnull(a.cope_with,0) ELSE 0 END) alipay,\n" +
            "\tsum(CASE WHEN b.payment_type=4 THEN ifnull(a.cope_with,0) ELSE 0 END) account,\n" +
            "\tsum(CASE WHEN b.payment_method=40 THEN ifnull(a.cope_with,0) ELSE 0 END) commonAccount,\n" +
            "\tsum(CASE WHEN b.payment_method=41 THEN ifnull(a.cope_with,0) ELSE 0 END) gasolineAccount,\n" +
            "\tsum(CASE WHEN b.payment_method=42 THEN ifnull(a.cope_with,0) ELSE 0 END) dieselAccount,\n" +
            "\tsum(CASE WHEN b.payment_method=43 THEN ifnull(a.cope_with,0) ELSE 0 END) cngAccount,\n" +
            "\tsum(CASE WHEN b.payment_type=5 THEN ifnull(a.cope_with,0) ELSE 0 END) iou,\n" +
            "\tsum(ifnull(a.cope_with,0)) total,\n" +
            "\tsum(ifnull(a.quantity,0)) quantity\n" +
            "\tfrom productdetail a left join productorder b on a.prorder_id = b.res_id\n" +
            "\twhere b.pro_sta = 2 and a.refund_sta=0 and b.duty_id in (:dutyIds)\n" +
            "\tGROUP BY a.product_id,a.product_bar_code,a.product_name", nativeQuery = true)
    List<Map> findDailyReportByDutyId(@Param("dutyIds") String[] dutyIds);

    @Query(value = "select a.product_id as commodityId,a.product_name as commodityName,\n" +
            "sum(ifnull(a.pro_money,0)) - sum(ifnull(a.refund_money,0)) receivableAmount,\n" +
            "sum(CASE WHEN a.refund_sta=0 THEN ifnull(a.discounts,0) ELSE 0 END) discountAmount,\n" +
            "sum(ifnull(a.cope_with,0)) - sum(ifnull(a.refund_money,0)) realAmount,\n" +
            "sum(ifnull(a.refund_money,0)) refundAmount,\n" +
            "sum(1) orderQuantity,\n" +
            "sum(ifnull(a.quantity,0)) - sum(ifnull(a.refund_quantity,0)) salesVolume\n" +
            "from productdetail a,productorder b\n" +
            "where a.prorder_id = b.res_id and a.prode_sta = 2 \n" +
            "and a.oil_station_id = :relatedId \n" +
            "and (:startDate is null or b.order_generation_time >= :startDate) \n" +
            "and (:endDate is null or b.order_generation_time<= :endDate) \n" +
            "group by a.product_id", nativeQuery = true)
    List<Map> findProSalesStatisticsByRelatedId(@Param("relatedId") String relatedId, @Param("startDate") String startDate, @Param("endDate") String endDate);

}
