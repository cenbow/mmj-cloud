package com.mmj.active.cut.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 我的砍价
 * @auther: KK
 * @date: 2019/6/15
 */
@Data
@ApiModel("我的砍价")
public class MyCutListDto extends CutGoodListDto {
    @ApiModelProperty("砍价号")
    private String cutNo;

    @ApiModelProperty(value = "消售ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品SKU")
    private String goodSku;

    @ApiModelProperty(value = "规格名称")
    private String modelName;

    @ApiModelProperty(value = "当前价")
    private BigDecimal currentPrice;

    @ApiModelProperty(value = "砍价状态 -1 己过期  0 正在进行 1 己完成")
    private int cutFlag;

    @ApiModelProperty(value = "已砍金额")
    private BigDecimal totalBargainPrice;

    @ApiModelProperty(value = "剩余待砍金额")
    private BigDecimal readyBargainPrice;

    @ApiModelProperty(value = "砍价订单号")
    private String cutOrderNo;

    @ApiModelProperty(value = "砍价订单状态")
    private Integer cutOrderStatus;

    @ApiModelProperty(value = "过期时间（距离过期的时间戳）")
    private long expiredTime;

    @ApiModelProperty(value = "当前用户是否可以帮砍 true可以 false不可以")
    private Boolean currentUserCutFlag;

    @ApiModelProperty(value = "当前用户已砍价次数")
    private Integer currentUserCutNumber;
}
