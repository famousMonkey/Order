package com.meatball.vo.report;

import com.meatball.vo.ProductorderVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ProductorderReport extends ProductorderVo {

   @ApiModelProperty(value = "分组ID")
   private String groupInfoId ;

   @ApiModelProperty(value = "分组名称")
   private String groupName;


}
