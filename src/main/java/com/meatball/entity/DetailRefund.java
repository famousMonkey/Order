package com.meatball.entity;

import com.meatball.common.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "DetailRefund")
@Data
@ApiModel(value = "退货详细信息")
public class DetailRefund extends BaseEntity implements Serializable{

    @ApiModelProperty(value="退款编号")
    @Column(length = 32)
    private String detailRefundNo;

    @ApiModelProperty(value="退货单ID")
    @Column(length = 24)
    private String  goodRefundId;

    @ApiModelProperty(value="总订单ID")
    @Column(length = 24)
    private String blanketOrderId;

    @ApiModelProperty(value="油品/商品ID")
    @Column(length = 24)
    private String relevantId;

    @ApiModelProperty(value="1油品 2商品")
    private Integer goodsType;

    @ApiModelProperty(value="油站id")
    @Column(length = 24)
    private String oilStationId;

    @ApiModelProperty(value="班组id")
    @Column(length = 24)
    private String squadId;

    @ApiModelProperty(value = "班组执勤Id")
    @Column(length = 24)
    private String dutyId;

    @ApiModelProperty(value="收银员ID")
    @Column(length = 24)
    private String staffId;

    @ApiModelProperty(value="收银员名称")
    @Column(length = 128)
    private String salesperson;

    @ApiModelProperty(value="退款原因")
    @Column(length = 512)
    private String refundReason;

    @ApiModelProperty(value="申请日期")
    @Column(length = 11)
    private Date applicationDate;

    @ApiModelProperty(value="退款日期")
    @Column(length = 11)
    private Date refundDate;

    @ApiModelProperty(value="核对人信息")
    @Column(length = 128)
    private String collator;

    @ApiModelProperty(value="优惠")
    private BigDecimal discounts;

    @ApiModelProperty(value="金额")
    private BigDecimal money;

    @ApiModelProperty(value="描述")
    @Column(length = 512)
    private String description;
}
