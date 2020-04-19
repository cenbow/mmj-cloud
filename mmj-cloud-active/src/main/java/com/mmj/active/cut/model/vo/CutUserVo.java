package com.mmj.active.cut.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 用户发起砍价
 * @auther: KK
 * @date: 2019/6/13
 */
@Data
@ApiModel("发起砍价")
public class CutUserVo {
    @ApiModelProperty(value = "ID")
    @NotNull
    private Integer cutId;

    @ApiModelProperty(value = "商品ID")
    @NotNull
    private Integer goodId;

    @ApiModelProperty(value = "商品SPU")
    @NotNull
    private String goodSpu;

    @ApiModelProperty(value = "消售ID")
    @NotNull
    private Integer saleId;

    @ApiModelProperty(value = "商品SKU")
    @NotNull
    private String goodSku;

    @ApiModelProperty(value = "商品图片")
//    @NotNull
    private String goodImage;

    @ApiModelProperty(value = "规格名称")
//    @NotNull
    private String modelName;

    @ApiModelProperty("收件人信息")
    @NotNull
    private Logistics logistics;

    /**
     * 订单来源 MIN(小程序) MH5(站内H5) H5(站外h5)
     */
    private String source;
    /**
     * appId 下单平台标识
     */
    private String appId;
    /**
     * 微信用户标识
     */
    private String openId;
    /**
     * 订单渠道
     */
    private String channel;

    @Data
    @ApiModel("收件人信息")
    public static class Logistics {
        @ApiModelProperty(value = "国家")
        @NotNull
        private String country;

        @ApiModelProperty(value = "省")
        @NotNull
        private String province;

        @ApiModelProperty(value = "市")
        @NotNull
        private String city;

        @ApiModelProperty(value = "区")
        @NotNull
        private String area;

        @ApiModelProperty(value = "收货地址")
        @NotNull
        private String consumerAddr;

        @ApiModelProperty(value = "收货人")
        @NotNull
        private String consumerName;

        @ApiModelProperty(value = "收货电话")
        @NotNull
        private String consumerMobile;
    }
}
