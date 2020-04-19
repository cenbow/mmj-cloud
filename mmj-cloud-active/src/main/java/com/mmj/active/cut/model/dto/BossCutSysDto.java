package com.mmj.active.cut.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 砍价公共设置
 * @auther: KK
 * @date: 2019/6/13
 */
@Data
@ApiModel(value = "砍价公共设置", description = "砍价公共设置")
public class BossCutSysDto {
    @ApiModelProperty(value = "公共配置ID")
    private Integer confId;

    @ApiModelProperty(value = "榜单公众号")
    private String weixnName;

    @ApiModelProperty(value = "活动规则")
    private String ruleCintext;

    @ApiModelProperty(value = "商品对象")
    private List<Item> items;

    @Data
    @ApiModel(value = "商品配置对象", description = "砍价信息表")
    public static class Item {
        @ApiModelProperty(value = "ID")
        private Integer mapperyId;

        @ApiModelProperty(value = "商品ID")
        private Integer goodId;

        @ApiModelProperty(value = "商品SPU")
        private String goodSpu;

        @ApiModelProperty(value = "商品名称")
        private String goodName;

        @ApiModelProperty(value = "商品图片")
        private String goodImage;

        @ApiModelProperty(value = "商品排序")
        private Integer goodOrder;
    }
}
