package com.mmj.order.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("聚水潭查询库存请求参数")
public class InventoryQueryDto {

    @ApiModelProperty("款式编码")
    private String spu;
    @ApiModelProperty("商品编码")
    private String sku;
    @ApiModelProperty("库存数(主仓实际库存+虚拟库存-订单占有数)")
    private Integer stockNum;

}
