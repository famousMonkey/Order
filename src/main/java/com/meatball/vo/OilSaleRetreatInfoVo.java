package com.meatball.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName:OilSaleRetreatInfoVo
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/12/7 16:40
 * @Version: 1.0
 **/
@Data
@Api(value = "油品进销存信息")
public class OilSaleRetreatInfoVo implements Serializable {

    @ApiModelProperty(value = "单号")
    private String orderNum;

    @ApiModelProperty(value = "油枪ID")
    private String oilGunId;

    @ApiModelProperty(value = "油枪名称")
    private String oilGunName;

    @ApiModelProperty(value = "类别")
    private Integer oilType;

    @ApiModelProperty(value = "数量")
    private BigDecimal count;

    @ApiModelProperty(value = "相关Id")
    private String relevantId;

}
