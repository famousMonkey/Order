package com.meatball.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.meatball.common.base.BaseEntity;
import com.meatball.common.utils.DateSerializer;
import com.vdurmont.emoji.EmojiParser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName:总订单信息
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/11/1 16:58
 * @Version: 1.0
 **/
@Entity
@Data
@Table(name="Blanketorder")
public class Blanketorder extends BaseEntity implements Serializable{
    @ApiModelProperty(value="订单编号",name="blanketorderNo")
    @Column(length = 32)
    private String blanketorderNo;

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
    @Column(length = 128)
    private String salesperson;

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

    @ApiModelProperty(value = "会员车牌号")
    @Column(length = 16)
    private String memberCarNum;

    @ApiModelProperty(value = "会员等级")
    @Column(length = 128)
    private String memberCardName;

    @ApiModelProperty(value="会员积分")
    private Integer memberIntegral;

    @ApiModelProperty(value = "会员类型(0.个人 1.团体)")
    private Integer memberType;

    @ApiModelProperty(value = "团体ID")
    @Column(length = 24)
    private String teamInfoId;

    @ApiModelProperty(value = "团体卡ID")
    @Column(length = 24)
    private String teamCardId;

    @ApiModelProperty(value="油品金额")
    private BigDecimal oilMoney;

    @ApiModelProperty(value="商品金额")
    private BigDecimal proMoney;

    @ApiModelProperty(value="订单总额")
    private BigDecimal orderSum;

    @ApiModelProperty(value="总优惠")
    private BigDecimal discounts;

    @ApiModelProperty(value="优惠券优惠")
    private BigDecimal couponDis;

    @ApiModelProperty(value="油站优惠")
    private BigDecimal grantDis;

    @ApiModelProperty(value="实际支付")
    private BigDecimal copeWith;

    @ApiModelProperty(value="订单总额退还额度")
    private BigDecimal refundOrder;

    @ApiModelProperty(value="退款")
    private BigDecimal refundMoney;

    @ApiModelProperty(value="退款优惠")
    private BigDecimal refundDiscounts;

    @ApiModelProperty(value = "收款")
    private BigDecimal payAmount;

    @ApiModelProperty(value = "找零")
    private BigDecimal returnMoney;

    @ApiModelProperty(value="积分")
    private Integer integral;

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

    @ApiModelProperty(value="订单来源")
    private Integer orderSource;

    @JSONField(serializeUsing = DateSerializer.class)
    @ApiModelProperty(value="订单生成时间")
    @Column(length = 11)
    private Date orderGenerationTime;

    @JSONField(serializeUsing = DateSerializer.class)
    @ApiModelProperty(value="订单完成时间")
    @Column(length = 11)
    private Date orderCompletionTime;

    @ApiModelProperty(value="订单描述")
    @Column(length = 256)
    private String orderDesc;

    @ApiModelProperty(value="版本，0基础版，1减配版，2标准版")
    private Integer version;

    @ApiModelProperty(value="退款状态，0正常 1退款 2部分退款")
    private Integer refundSta;

    @ApiModelProperty(value="发票状态，0未开 1已开 2撤销")
    private Integer invoiceSta;

    @ApiModelProperty(value="是否挂单，默认不是")
    private Boolean pend;

    @ApiModelProperty(value="订单状态")
    private Integer blankSta;

    public String getMemberName() {
        return StringUtils.isNotEmpty(memberName)? EmojiParser.parseToUnicode(memberName):memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = StringUtils.isNotEmpty(memberName)?EmojiParser.parseToAliases(memberName):memberName;
    }

}
