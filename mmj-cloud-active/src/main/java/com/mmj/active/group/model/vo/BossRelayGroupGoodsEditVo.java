package com.mmj.active.group.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BossRelayGroupGoodsEditVo {
    @ApiModelProperty(value = "关联ID")
    @NotNull
    private Long mapperyId;

    @ApiModelProperty(value = "活动价格")
    private String activePrice;

    @ApiModelProperty(value = "商品名称")
    private String goodName;

    @ApiModelProperty(value = "是否关注公众号")
    private Integer ag3;

    @ApiModelProperty(value = "接力人数")
    private Integer groupPerson;

    @ApiModelProperty(value = "接力模式")
    private Integer limitType;

    @ApiModelProperty(value = "0:停用 1:启用")
    private Integer goodStatus;
}
