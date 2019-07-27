package com.meatball.vo.discount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DiscountDataVo implements Serializable {

    @ApiModelProperty(value="总优惠")
    private BigDecimal cardMoney;

    List<DisCountSumVo> disCountSumList;

}
