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

/**
 * @ClassName:GoodsReturn
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/11/12 15:15
 * @Version: 1.0
 **/
@Entity
@Table(name = "GoodsRefund")
@Data
@ApiModel(value = "退货信息")
public class GoodsRefund extends BaseEntity implements Serializable{

    @ApiModelProperty(value="退款编号",name="refundNumber")
    @Column(length = 32)
    private String goodsRefundNo;

    @ApiModelProperty(value="油站id")
    @Column(length = 24)
    private String oilStationId;

    @ApiModelProperty(value="会员id")
    @Column(length = 24)
    private String memberId;

    @ApiModelProperty(value="总订单id",name="blanketOrderId")
    @Column(length = 24)
    private String blanketOrderId;

    @ApiModelProperty(value="班组id",name="squadId")
    @Column(length = 24)
    private String squadId;

    @ApiModelProperty(value = "班组执勤Id",name="dutyId")
    @Column(length = 24)
    private String dutyId;

    @ApiModelProperty(value="收银员ID",name="staffId")
    @Column(length = 24)
    private String staffId;

    @ApiModelProperty(value="收银员名称",name="salesperson")
    @Column(length = 128)
    private String salesperson;

    @ApiModelProperty(value="退款原因",name="refundReason")
    @Column(length = 512)
    private String refundReason;

    @ApiModelProperty(value="类型,0全部 1油品 2商品")
    private Integer goodsType;

    @ApiModelProperty(value="申请日期",name="applicationDate")
    @Column(length = 11)
    private Date applicationDate;

    @ApiModelProperty(value="退款日期",name="refundDate")
    @Column(length = 11)
    private Date refundDate;

    @ApiModelProperty(value="核对人信息",name="collator")
    @Column(length = 128)
    private String collator;

    @ApiModelProperty(value="金额",name="money")
    private BigDecimal money;

    @ApiModelProperty(value="积分")
    private Integer integral;

    @ApiModelProperty(value="描述",name="description")
    @Column(length = 512)
    private String description;
}
