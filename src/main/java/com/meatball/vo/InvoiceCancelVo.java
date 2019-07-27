package com.meatball.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class InvoiceCancelVo implements Serializable {

    @ApiModelProperty(value="订单ID")
    private String orderId;

    @ApiModelProperty(value="撤销原因")
    private String cancelReason;

    @ApiModelProperty(value="操作人")
    private String cancelOperator;
}
