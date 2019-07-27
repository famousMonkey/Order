package com.meatball.vo;

import com.meatball.entity.Oilsorder;
import com.meatball.utils.TextConversion;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class OilsorderVo extends Oilsorder {

   @ApiModelProperty(value="状态（文本）")
   private String textOrderStatus;

   @ApiModelProperty(value="支付方式（文本）")
   private String textPaymentMethod;

   public void setOilSta(Integer oilSta){
      super.setOilSta(oilSta);
      this.textOrderStatus = TextConversion.textStatus(oilSta,super.getRefundSta());
   }

   public void setRefundSta(Integer refundSta){
      super.setRefundSta(refundSta);
      this.textOrderStatus = TextConversion.textStatus(super.getOilSta(),refundSta);
   }

   public void setPaymentMethod(Integer paymentMethod){
      super.setPaymentMethod(paymentMethod);
      this.textPaymentMethod = TextConversion.textPaymentMethod(paymentMethod);
   }

}
