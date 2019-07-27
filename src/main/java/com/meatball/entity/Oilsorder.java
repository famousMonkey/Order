package com.meatball.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.meatball.common.base.BaseEntity;
import com.meatball.common.utils.DateSerializer;
import com.vdurmont.emoji.EmojiParser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName:油品订单
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/11/1 16:16
 * @Version: 1.0
 **/
@Entity
@Table(name = "Oilorder")
@Data
public class Oilsorder extends BaseEntity{
    @ApiModelProperty(value="油品单号")
    @Column(length = 32)
    private String oilsorderNo;

    @ApiModelProperty(value="总订单id")
    @Column(length = 24)
    private String blanketOrderId;

    @ApiModelProperty(value="组织ID")
    @Column(length = 24)
    private String orgId;

    @ApiModelProperty(value="油站id")
    @Column(length = 24)
    private String oilStationId;

    @ApiModelProperty(value="班组id")
    @Column(length = 24)
    private String squadId;

    @ApiModelProperty(value = "班组执勤Id")
    @Column(length = 24)
    private String dutyId;

    @ApiModelProperty(value="加油员ID")
    @Column(length = 24)
    private String refuelStaffId;

    @ApiModelProperty(value="加油员名称")
    @Column(length = 128)
    private String refuelStaffName;

    @ApiModelProperty(value="收银员ID")
    @Column(length = 24)
    private String staffId;

    @ApiModelProperty(value="收银员名称")
    @Column(length = 128)
    private String salesperson;

    @ApiModelProperty(value="加油员执勤Id")
    @Column(length = 24)
    private String staffDutyId;

    @ApiModelProperty(value="是否临时会员，默认不是")
    private Boolean interim;

    @ApiModelProperty(value="会员id")
    @Column(length = 24)
    private String memberId;

    @ApiModelProperty(value="会员名称")
    @Column(length = 128)
    private String memberName;

    @ApiModelProperty(value = "会员编号")
    @Column(length = 64)
    private String memberNum;

    @ApiModelProperty(value="会员手机号")
    @Column(length = 32)
    private String memberPhone;

    @ApiModelProperty(value = "会员类型(0.个人 1.团体)")
    private Integer memberType;

    @ApiModelProperty(value = "团体ID")
    @Column(length = 24)
    private String teamInfoId;

    @ApiModelProperty(value = "团体卡ID")
    @Column(length = 24)
    private String teamCardId;

    @ApiModelProperty(value="油枪ID")
    @Column(length = 24)
    private String oilGunId;

    @ApiModelProperty(value="油枪名称")
    @Column(length = 32)
    private String oilGunName;

    @ApiModelProperty(value="数据来源编码")
    @Column(length = 32)
    private String dataCode;

    @ApiModelProperty(value="油品关联ID")
    @Column(length = 24)
    private String oilsDicId;

    @ApiModelProperty(value="油品ID")
    @Column(length = 24)
    private String oilsId;

    @ApiModelProperty(value="优惠券ID")
    @Column(length = 32)
    private String couponId;

    @ApiModelProperty(value="活动ID")
    @Column(length = 24)
    private String detailId;

    @ApiModelProperty(value="活动类型,1油站活动 2等级活动")
    private Integer activityType;

    @ApiModelProperty(value="油品名称")
    @Column(length = 32)
    private String oilsName;

    @ApiModelProperty(value="油品类别")
    @Column(length = 32)
    private String oilsCategory;

    @ApiModelProperty(value="单价")
    private BigDecimal price;

    @ApiModelProperty(value="销售价格")
    private BigDecimal sellingPrice;

    @ApiModelProperty(value="数量")
    private BigDecimal quantity;

    @ApiModelProperty(value="订单金额")
    private BigDecimal oilMoney;

    @ApiModelProperty(value="优惠")
    private BigDecimal discounts;

    @ApiModelProperty(value="优惠券优惠")
    private BigDecimal couponDis;

    @ApiModelProperty(value="油站优惠")
    private BigDecimal grantDis;

    @ApiModelProperty(value="每升优惠")
    private BigDecimal priceDis;

    @ApiModelProperty(value="实际付款")
    private BigDecimal copeWith;

    @ApiModelProperty(value="退款")
    private BigDecimal refundMoney;

    @JSONField(serializeUsing = DateSerializer.class)
    @ApiModelProperty(value="订单生成时间")
    @Column(length = 11)
    private Date orderGenerationTime;

    @JSONField(serializeUsing = DateSerializer.class)
    @ApiModelProperty(value="订单完成时间")
    @Column(length = 11)
    private Date orderCompletionTime;

    @ApiModelProperty(value="支付信息ID")
    @Column(length = 32)
    private String paymentId;

    @ApiModelProperty(value="支付编号")
    @Column(length = 128)
    private String paymentNumber;

    @ApiModelProperty(value="支付种类")
    private Integer paymentType;

    @ApiModelProperty(value="支付方式")
    private Integer paymentMethod;

    @ApiModelProperty(value="订单来源,")
    private Integer orderSource;

    @ApiModelProperty(value="结算方式")
    private Integer tradeType;

    @ApiModelProperty(value="是否单独油品订单")
    private Boolean singly;

    @ApiModelProperty(value="版本，0基础版，1减配版，2标准版")
    private Integer version;

    @ApiModelProperty(value="退款状态，0正常 1退款")
    private Integer refundSta;

    @ApiModelProperty(value="是否挂单，默认不是")
    private Boolean pend;

    @ApiModelProperty(value="订单状态")
    private Integer oilSta;

    public String getMemberName() {
        return StringUtils.isNotEmpty(memberName)? EmojiParser.parseToUnicode(memberName):memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = StringUtils.isNotEmpty(memberName)?EmojiParser.parseToAliases(memberName):memberName;
    }

}
