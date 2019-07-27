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
 * @ClassName:商品订单信息
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/11/1 16:35
 * @Version: 1.0
 **/
@Entity
@Table(name = "Productorder")
@Data
public class Productorder extends BaseEntity{
    @ApiModelProperty(value="商品单号")
    @Column(length = 32)
    private String productorderNo;

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

    @ApiModelProperty(value="收银员ID")
    @Column(length = 24)
    private String staffId;

    @ApiModelProperty(value="收银员名称")
    @Column(length = 24)
    private String salesperson;

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

    @ApiModelProperty(value="数量")
    private BigDecimal quantity;

    @ApiModelProperty(value="商品金额")
    private BigDecimal proMoney;

    @ApiModelProperty(value="商品优惠")
    private BigDecimal discounts;

    @ApiModelProperty(value="退款优惠")
    private BigDecimal refundDiscounts;

    @ApiModelProperty(value="实际支付")
    private BigDecimal copeWith;

    @ApiModelProperty(value="退款")
    private BigDecimal refundMoney;

    @ApiModelProperty(value="退货数量")
    private BigDecimal refundQuantity;

    @JSONField(serializeUsing = DateSerializer.class)
    @ApiModelProperty(value="订单生成时间")
    private Date orderGenerationTime;

    @JSONField(serializeUsing = DateSerializer.class)
    @ApiModelProperty(value="订单完成时间")
    private Date orderCompletionTime;

    @ApiModelProperty(value="支付信息")
    @Column(length = 24)
    private String paymentId;

    @ApiModelProperty(value="支付编号")
    @Column(length = 128)
    private String paymentNumber;

    @ApiModelProperty(value="支付种类")
    private Integer paymentType;

    @ApiModelProperty(value="支付方式")
    private Integer paymentMethod;

    @ApiModelProperty(value="订单来源")
    private Integer orderSource;

    @ApiModelProperty(value="结算方式")
    private Integer tradeType;

    @ApiModelProperty(value="版本，0基础版，1减配版，2标准版")
    private Integer version;

    @ApiModelProperty(value="退款状态，0正常 1退款 2部分退款")
    private Integer refundSta;

    @ApiModelProperty(value="是否挂单，默认不是")
    private Boolean pend;

    @ApiModelProperty(value="订单状态")
    @Column(length = 2)
    private Integer proSta;

    public String getMemberName() {
        return StringUtils.isNotEmpty(memberName)? EmojiParser.parseToUnicode(memberName):memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = StringUtils.isNotEmpty(memberName)?EmojiParser.parseToAliases(memberName):memberName;
    }
}
