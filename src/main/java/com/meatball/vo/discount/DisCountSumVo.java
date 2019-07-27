package com.meatball.vo.discount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DisCountSumVo implements Serializable {

    @ApiModelProperty(value="每升优惠金额")
    private BigDecimal discountMoney;

    @ApiModelProperty(value="优惠总金额")
    private BigDecimal totalDiscount;

    @ApiModelProperty(value="折扣类别，0 优惠券 1油站活动 2级别优惠")
    private Integer type;
}
