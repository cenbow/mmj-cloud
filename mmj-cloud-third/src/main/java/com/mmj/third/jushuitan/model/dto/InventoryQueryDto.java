package com.mmj.third.jushuitan.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 查询库存返回实体
 * @auther: KK
 * @date: 2019/6/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("聚水潭查询库存返回参数")
public class InventoryQueryDto {
    @ApiModelProperty("款式编码")
    private String spu;
    @ApiModelProperty("商品编码")
    private String sku;
    @ApiModelProperty("库存数(主仓实际库存+虚拟库存-订单占有数)")
    private Integer stockNum;
}
