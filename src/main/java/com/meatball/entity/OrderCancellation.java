package com.meatball.entity;

import com.meatball.common.base.BaseEntity;
import com.meatball.common.base.BaseEntityVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName:订单取消信息
 * @Description: TODO
 * @Author :周晓瀚
 * @Date:2018/12/3 16:00
 * @Version: 1.0
 **/
@Entity
@Table(name = "OrderCancellation")
@Data
public class OrderCancellation extends BaseEntity {
    @ApiModelProperty(value="总订单ID",name="blanketorderId")
    @Column(length = 24)
    private String blanketorderId;

    @ApiModelProperty(value="取消原因",name="cancellationReason")
    @Column(length = 256)
    private String cancellationReason;

    @ApiModelProperty(value="取消日期",name="cancellationDate")
    private Date cancellationDate;

    @ApiModelProperty(value="状态",name="cancellationSta")
    private Integer cancellationSta;

    @ApiModelProperty(value="描述",name="cancellationDes")
    @Column(length = 256)
    private String cancellationDes;
}
