package com.meatball.vo;

import com.meatball.entity.Productorder;
import com.meatball.utils.TextConversion;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ProductorderVo extends Productorder {

   @ApiModelProperty(value="状态（文本）")
   private String textOrderStatus;

   @ApiModelProperty(value="支付方式（文本）")
   private String textPaymentMethod;

   public void setProSta(Integer proSta){
      super.setProSta(proSta);
      this.textOrderStatus = TextConversion.textStatus(proSta,super.getRefundSta());
   }

   public void setRefundSta(Integer refundSta){
      super.setRefundSta(refundSta);
      this.textOrderStatus = TextConversion.textStatus(super.getProSta(),refundSta);
   }

   public void setPaymentMethod(Integer paymentMethod){
      super.setPaymentMethod(paymentMethod);
      this.textPaymentMethod = TextConversion.textPaymentMethod(paymentMethod);
   }

}
