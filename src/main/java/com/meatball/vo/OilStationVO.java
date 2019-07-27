package com.meatball.vo;
/**
 * Project Name:OilStationVO.java
 * File Name:OilStationVO.java
 * Date:2018/11/29 14:48
 * Copyright (c) 2018, xia.yukunu@foxmail.com All Rights Reserved.
 */

import com.meatball.common.base.BaseEntityVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.persistence.*;

/**
 * @Title: OilStationVO.java
 * @Description: todo()
 * @Author: 夏玉昆
 * @Date: 2018/11/29 14:49
 * @Version: V1.0
 */
@Data
@ApiModel("油站信息VO")
public class OilStationVO extends BaseEntityVo {
    @ApiModelProperty(value="油站编号",name="oilStationNumber")
    private String oilStationNumber;
    @ApiModelProperty(value="所属区域",name="oilStationArea",example = "山东")
    private String oilStationArea;
    @ApiModelProperty(value="详细地址",name="oilStationSite")
    private String oilStationSite;
    @ApiModelProperty(value="经纬度",name="longitudeAndLatitude")
    private String longitudeAndLatitude;
    @ApiModelProperty(value="联系方式",name="oilStationContact")
    private String oilStationContact;
    @ApiModelProperty(value="简介",name="oilStationIntroduction")
    private String oilStationIntroduction;
    @ApiModelProperty(value="状态",name="staType")
    private Integer staType;
    @Transient
    private String[] city;
}
