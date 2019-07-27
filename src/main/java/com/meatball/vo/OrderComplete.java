/**
 * Project Name:OrderComplete.java
 * File Name:OrderComplete.java
 * Date:2019/3/20 10:44
 * Copyright (c) 2019, zhang.xiangyu@foxmail.com All Rights Reserved.
 */
package com.meatball.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Title: OrderComplete.java
 * @Description: todo(訂單完成通知消息實體)
 * @Author: 張翔宇
 * @Date: 2019/3/20 10:44
 * @Version: V1.0
 */
@Data
@Component
public class OrderComplete {

    /**
     * 会员ID
     */
    private String memberId;

    /**
     * 油站ID
     */
    private String oilStationId;

    /**
     * 手機號碼
     */
    private String mobile;

    /**
     * 訂單號
     */
    private String orderNo;

    /**
     * 支付時間
     */
    private Date payTime;

    /**
     * 購物明細
     */
    private String purchaseDetails;

    /**
     * 優惠金額
     */
    private Double discountAmount;

    /**
     * 實付金額
     */
    private Double amountPaid;
}
