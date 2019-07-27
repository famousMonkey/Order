package com.meatball.vo.request;

import com.meatball.common.base.BaseEntityRe;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
public class BlanketorderRe extends BaseEntityRe {

   @ApiModelProperty(value="订单编号")
   private String blanketorderNo;

   @ApiModelProperty(value="组织ID")
   private String orgId;

   @ApiModelProperty(value="油站id")
   private String oilStationId;

   @ApiModelProperty(value = "班组执勤Id")
   private String dutyId;

   @ApiModelProperty(value="会员id")
   private String memberId;

   @ApiModelProperty(value="会员手机号")
   private String memberPhone;

   @ApiModelProperty(value="会员名称")
   private String memberName;

   @ApiModelProperty(value="支付编号")
   private String paymentNumber;

   @ApiModelProperty(value="支付种类")
   private String paymentType;

   @ApiModelProperty(value="支付方式")
   private String paymentMethod;

   @ApiModelProperty(value="订单生成时间")
   private String orderGenerationTime;

   @ApiModelProperty(value="订单完成时间")
   private String orderCompletionTime;

    @ApiModelProperty(value="退款状态，0正常 1退款")
    private String refundSta;

   @ApiModelProperty(value="发票状态，0未开 1已开")
   private String invoiceSta;

    @ApiModelProperty(value="订单状态")
    private String blankSta;

   @ApiModelProperty(value="是否挂单，默认不是")
   private String pend="false";

   @ApiModelProperty(value="开始时间")
   private String startTime;

   @ApiModelProperty(value="结束时间")
   private String endTime;

   public void  setStartTime(String startTime){
      this.orderGenerationTime  = (StringUtils.isBlank( this.orderGenerationTime)?"":this.orderGenerationTime + "##" ) + startTime + "@5";
   }

   public void  setEndTime(String endTime){
      this.orderGenerationTime  = (StringUtils.isBlank( this.orderGenerationTime)?"":this.orderGenerationTime + "##" ) + endTime + "@6";
   }

}
