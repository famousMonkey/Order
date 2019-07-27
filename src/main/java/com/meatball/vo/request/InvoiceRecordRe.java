package com.meatball.vo.request;

import com.meatball.common.base.BaseEntityRe;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
public class InvoiceRecordRe extends BaseEntityRe {

    @ApiModelProperty(value="油站id")
    private String oilStationId;

    @ApiModelProperty(value="订单ID")
    private String orderId;

    @ApiModelProperty(value="会员ID")
    private String memberId;

    @ApiModelProperty(value="会员手机号")
    private String memberPhone;

    @ApiModelProperty(value="邮箱")
    private String email;

    @ApiModelProperty(value="识别号")
    private String taxNum;

    @ApiModelProperty(value="抬头")
    private String title;

    @ApiModelProperty(value="发票类型")
    private String type;

    @ApiModelProperty(value="服务类型")
    private String goodsName;

    @ApiModelProperty(value="收银员")
    private String  salesperson;

    @ApiModelProperty(value="开票人")
    private String clerk;

    @ApiModelProperty(value="金额",hidden = true)
    private String price;

    @ApiModelProperty(value="最小金额")
    private String leastPrice;

    @ApiModelProperty(value="最大金额")
    private String maxPrice;

    @ApiModelProperty(value="开票时间",hidden = true)
    private String invoiceDate;

    @ApiModelProperty(value="开票开始时间")
    private String startTime;

    @ApiModelProperty(value="开票结束时间")
    private String endTime;

    @ApiModelProperty(value="状态(0正常，1撤销)")
    private Integer status;

    @ApiModelProperty(value="撤销原因")
    private String cancelReason;

    @ApiModelProperty(value="操作人")
    private String cancelOperator;

    @ApiModelProperty(value="描述")
    private String desc;

    public void  setStartTime(String startTime){
      this.invoiceDate  = (StringUtils.isBlank( this.invoiceDate)?"":this.invoiceDate + "##" ) + startTime + "@5";
    }

    public void  setEndTime(String endTime){
      this.invoiceDate  = (StringUtils.isBlank( this.invoiceDate)?"":this.invoiceDate + "##" ) + endTime + "@6";
    }

    public void  setLeastPrice(String leastPrice){
        this.invoiceDate  = (StringUtils.isBlank( this.price)?"":this.price + "##" ) + leastPrice + "@1_1";
    }

    public void  setMaxPrice(String maxPrice){
        this.invoiceDate  = (StringUtils.isBlank( this.price)?"":this.price + "##" ) + maxPrice + "@2_1";
    }
}
