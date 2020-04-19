package com.mmj.third.jushuitan.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 商品映射查询
 * @auther: KK
 * @date: 2019/8/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("聚水潭商品映射查询返回参数")
public class SkuMapQueryDto {
    @ApiModelProperty(value = "店铺id")
    private Integer shopId;
    @ApiModelProperty(value = "平台")
    private String channel;
    @ApiModelProperty(value = "款号id")
    private String iId;
    @ApiModelProperty(value = "商品id")
    private String sku;
    @ApiModelProperty(value = "店铺款号id")
    private String shopIId;
    @ApiModelProperty(value = "店铺商品id")
    private String shopSkuId;
    @ApiModelProperty(value = "修改时间")
    private String modified;
    @ApiModelProperty(value = "是否在售")
    private Boolean enabled;
}
