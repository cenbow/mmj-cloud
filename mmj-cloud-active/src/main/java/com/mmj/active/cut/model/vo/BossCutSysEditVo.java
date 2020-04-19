package com.mmj.active.cut.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @description: 砍价公共设置
 * @auther: KK
 * @date: 2019/6/13
 */
@Data
@ApiModel(value = "砍价公共设置", description = "砍价公共设置")
public class BossCutSysEditVo {
    @ApiModelProperty(value = "公共配置ID")
    private Integer confId;

    @ApiModelProperty(value = "榜单公众号")
    @NotNull
    private String weixnName;

    @ApiModelProperty(value = "活动规则")
    @NotNull
    private String ruleCintext;

    @ApiModelProperty(value = "商品对象")
    @Size(min = 1)
    private List<Item> items;

    @Data
    @ApiModel(value = "商品配置对象", description = "砍价信息表")
    public static class Item {
        @ApiModelProperty(value = "ID")
        @NotNull
        private Long mapperyId;

        @ApiModelProperty(value = "商品ID")
        @NotNull
        private Integer goodId;

        @ApiModelProperty(value = "商品排序")
        @NotNull
        private Integer goodOrder;
    }
}
