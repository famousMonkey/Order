package com.meatball.vo;

import com.meatball.entity.Productdetail;
import com.meatball.utils.TextConversion;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ProductdetailVo extends Productdetail {

    @ApiModelProperty(value="状态（文本）")
    private String textOrderStatus;

    public void setProdeSta(Integer prodeSta){
        super.setProdeSta(prodeSta);
        this.textOrderStatus = TextConversion.textStatus(prodeSta,super.getRefundSta());
    }

    public void setRefundSta(Integer refundSta){
        super.setRefundSta(refundSta);
        this.textOrderStatus = TextConversion.textStatus(super.getProdeSta(),refundSta);
    }

}
