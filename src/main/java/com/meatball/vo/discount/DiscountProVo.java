package com.meatball.vo.discount;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DiscountProVo implements Serializable {

    private String goodsPromotionId;

    private String detailId;

    private BigDecimal discountSumMoney = new BigDecimal(0);

}
