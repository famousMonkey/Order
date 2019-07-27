package com.meatball.vo.discount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DiscountOilVo implements Serializable {

    @ApiModelProperty(value="优惠")
    private BigDecimal discounts = new BigDecimal(0);

    @ApiModelProperty(value="优惠券优惠")
    private BigDecimal couponDis = new BigDecimal(0);

    @ApiModelProperty(value="油站优惠")
    private BigDecimal grantDis = new BigDecimal(0);

    @ApiModelProperty(value="每升优惠")
    private BigDecimal priceDis = new BigDecimal(0);
}
