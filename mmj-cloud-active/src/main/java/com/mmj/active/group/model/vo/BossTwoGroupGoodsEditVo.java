package com.mmj.active.group.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 二人团商品编辑
 * @auther: KK
 * @date: 2019/7/22
 */
@Data
public class BossTwoGroupGoodsEditVo {
    @ApiModelProperty(value = "关联ID")
    @NotNull
    private Long mapperyId;

    @ApiModelProperty(value = "活动价格")
    private String activePrice;

    @ApiModelProperty(value = "商品名称")
    private String goodName;

    @ApiModelProperty(value = "卖点")
    private String sellingPoint;

    @ApiModelProperty(value = "0:停用 1:启用")
    private Integer goodStatus;

    @ApiModelProperty(value = "是否限购 0不限购 1限购")
    private Integer goodLimit;

    @ApiModelProperty(value = "限购模式 1每单限购 如果是接力购 接力购模式：0:正常模式，1:老带新，2:所有人")
    private Integer limitType;

    @ApiModelProperty(value = "限购数量")
    private Integer limitNum;
}
