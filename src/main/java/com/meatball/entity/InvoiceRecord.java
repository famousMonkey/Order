package com.meatball.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.meatball.common.base.BaseEntity;
import com.meatball.common.utils.DateSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "InvoiceRecord")
@Data
public class InvoiceRecord extends BaseEntity {

    @ApiModelProperty(value="油站id")
    @Column(length = 24)
    private String oilStationId;

    @ApiModelProperty(value="订单ID")
    @Column(length = 24)
    private String orderId;

    @ApiModelProperty(value="会员ID")
    @Column(length = 32)
    private String memberId;

    @ApiModelProperty(value="会员手机号")
    @Column(length = 32)
    private String memberPhone;

    @ApiModelProperty(value="邮箱")
    @Column(length = 512)
    private String email;

    @ApiModelProperty(value="手机号")
    @Column(length = 32)
    private String phone;

    @ApiModelProperty(value="发票流水号")
    private String invoiceNum;

    @ApiModelProperty(value="识别号")
    @Column(length = 128)
    private String taxNum;

    @ApiModelProperty(value="抬头")
    @Column(length = 128)
    private String title;

    @ApiModelProperty(value="发票类型")
    private Integer type;

    @ApiModelProperty(value="服务类型")
    @Column(length = 128)
    private String goodsName;

    @ApiModelProperty(value="收银员")
    @Column(length = 128)
    private String  salesperson;

    @ApiModelProperty(value="开票人")
    @Column(length = 128)
    private String clerk;

    @ApiModelProperty(value="金额")
    private BigDecimal price;

    @JSONField(serializeUsing = DateSerializer.class)
    @ApiModelProperty(value="开票时间")
    @Column(length = 11)
    private Date invoiceDate;

    @ApiModelProperty(value="状态(0正常，1撤销)")
    private Integer status;

    @ApiModelProperty(value="撤销原因")
    @Column(length = 512)
    private String cancelReason;


    @JSONField(serializeUsing = DateSerializer.class)
    @ApiModelProperty(value="撤销时间")
    @Column(length = 11)
    private Date cancelDate;

    @ApiModelProperty(value="操作人")
    @Column(length = 128)
    private String cancelOperator;

    @ApiModelProperty(value="描述")
    @Column(length = 1024)
    private String remarks;
}
