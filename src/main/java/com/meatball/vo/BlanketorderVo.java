package com.meatball.vo;

import com.meatball.entity.Blanketorder;
import com.meatball.utils.TextConversion;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class BlanketorderVo extends Blanketorder {

   @ApiModelProperty(value="状态（文本）")
   private String textOrderStatus;

   @ApiModelProperty(value="支付方式（文本）")
   private String textPaymentMethod;

   public void setBlankSta(Integer blankSta){
      super.setBlankSta(blankSta);
      this.textOrderStatus = TextConversion.textStatus(blankSta,super.getRefundSta());
   }

   public void setRefundSta(Integer refundSta){
      super.setRefundSta(refundSta);
      this.textOrderStatus = TextConversion.textStatus(super.getBlankSta(),refundSta);
   }

   public void setPaymentMethod(Integer paymentMethod){
      super.setPaymentMethod(paymentMethod);
      this.textPaymentMethod = TextConversion.textPaymentMethod(paymentMethod);
   }

}
