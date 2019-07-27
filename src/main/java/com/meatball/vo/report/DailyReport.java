package com.meatball.vo.report;

import com.meatball.common.vo.ordervo.customize.ReceiptStatistics;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DailyReport extends ReceiptStatistics {

   @ApiModelProperty(value="商品ID")
   private String productId;

   @ApiModelProperty(value="编号")
   private String productBarCode;

   @ApiModelProperty(value="名称")
   private String productName;

   @ApiModelProperty(value="销量")
   private BigDecimal quantity;

   @ApiModelProperty(value="其它")
   private String other;

}
