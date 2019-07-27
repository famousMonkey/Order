package com.meatball.vo.report;

import com.meatball.vo.request.ProductorderRe;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ProductorderReportRe extends ProductorderRe {

    @ApiModelProperty(value="开始时间")
    private String startTime;

    @ApiModelProperty(value="结束时间")
    private String endTime;

    public void  setStartTime(String startTime){
        this.startTime = startTime;
    }

    public void  setEndTime(String endTime){
        this.endTime = endTime;
    }

    @ApiModelProperty(value = "分组ID")
    private String groupInfoId ;

    @ApiModelProperty(value = "分组名称")
    private String groupName;
}
