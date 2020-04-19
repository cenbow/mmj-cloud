package com.mmj.active.cut.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 砍价商品配置对象
 * @auther: KK
 * @date: 2019/6/12
 */
@Data
@ApiModel(value = "砍价商品配置对象", description = "砍价信息表")
public class BossCutAddItemVo {
    @ApiModelProperty(value = "商品ID")
    @NotNull
    private Integer goodId;

    @ApiModelProperty(value = "商品SPU")
    @NotNull
    private String goodSpu;

    @ApiModelProperty(value = "商品名称")
    @NotNull
    private String goodName;

    @ApiModelProperty(value = "商品图片")
    @NotNull
    private String goodImage;

    @ApiModelProperty(value = "商品排序")
    @NotNull
    private Integer goodOrder;
}
