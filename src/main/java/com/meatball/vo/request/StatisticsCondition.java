package com.meatball.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class StatisticsCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="机构ID(品牌商或者油站)")
    private String orgId;

    @ApiModelProperty(value = "班组执勤Id")
    String dutyId;

    @ApiModelProperty(value="加油员执勤Id")
    private String staffDutyId;

    @ApiModelProperty(value="加油员Id")
    private String  refuelStaffId;

    @ApiModelProperty(value="油枪ID")
    private String oilGunId;

    @ApiModelProperty(value="油品ID")
    String oilsId;

    @ApiModelProperty(value="支付方式")
    private String payment;

    @ApiModelProperty(value="开始时间")
    String startDate;

    @ApiModelProperty(value="结束时间")
    String endDate;
}
