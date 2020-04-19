package com.mmj.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value="GoodStock对象", description="库存记录表")
public class GoodStock {

    @ApiModelProperty(value = "ID")
    private Integer stockId;

    @ApiModelProperty(value = "SKU编码")
    private String goodSku;

    @ApiModelProperty(value = "库存(占用、扣减为负数，释放、回退为正数)")
    private Integer goodNum;

    @ApiModelProperty(value = "状态(1：占用 2：扣减 3：释放 4:回退 5：过期)")
    private Integer status;

    @ApiModelProperty(value = "业务id")
    private String businessId;

    @ApiModelProperty(value = "业务类型(定义规则：模块_类型[order_4])")
    private String businessType;

    @ApiModelProperty(value = "过期时间")
    private Date expireTime;

    @ApiModelProperty(value = "创建时间")
    private Date createrTime;
}
