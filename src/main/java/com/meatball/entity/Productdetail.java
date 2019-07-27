package com.meatball.entity;

import com.meatball.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName:商品明细
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/11/1 16:47
 * @Version: 1.0
 **/
@Entity
@Table(name = "Productdetail")
@Data
public class Productdetail extends BaseEntity implements Serializable {

    @ApiModelProperty(value="总订单id")
    @Column(length = 24)
    private String blanketOrderId;

    @ApiModelProperty(value="商品订单ID")
    @Column(length = 24)
    private String prorderId;

    @ApiModelProperty(value="组织ID")
    @Column(length = 24)
    private String orgId;

    @ApiModelProperty(value="油站id")
    @Column(length = 24)
    private String oilStationId;

    @ApiModelProperty(value="班组id")
    @Column(length = 24)
    private String squadId;

    @ApiModelProperty(value="售货员")
    @Column(length = 128)
    private String salesperson;

    @ApiModelProperty(value="会员id")
    @Column(length = 24)
    private String memberId;

    @ApiModelProperty(value="商品ID")
    @Column(length = 24)
    private String productId;

    @ApiModelProperty(value="商品名称")
    @Column(length = 128)
    private String productName;

    @ApiModelProperty(value="商品条形码")
    @Column(length = 32)
    private String productBarCode;

    @ApiModelProperty(value="单价")
    private BigDecimal price;

    @ApiModelProperty(value="数量")
    private BigDecimal quantity;

    @ApiModelProperty(value="退款数量")
    private BigDecimal refundQuantity;

    @ApiModelProperty(value="金额")
    private BigDecimal proMoney;

    @ApiModelProperty(value="退款")
    private BigDecimal refundMoney;

    @ApiModelProperty(value="活动ID")
    @Column(length = 24)
    private String detailId;

    @ApiModelProperty(value="活动名称")
    @Column(length = 128)
    private String detailName;

    @ApiModelProperty(value="优惠")
    private BigDecimal discounts;

    @ApiModelProperty(value="实际付款")
    private BigDecimal copeWith;

    @ApiModelProperty(value="退款状态，0正常 1全部退款 2部分退款")
    private Integer refundSta;

    @ApiModelProperty(value="订单状态")
    private Integer prodeSta;

    @ApiModelProperty(value="是否挂单，默认不是")
    private Boolean pend;
}
