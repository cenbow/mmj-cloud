package com.mmj.user.manager.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 商品优惠券
 * @auther: KK
 * @date: 2019/7/11
 */
@Data
@ApiModel(value = "商品优惠券", description = "商品优惠券")
public class GoodsCouponDto {
    @ApiModelProperty(value = "优惠券信息")
    private CouponInfoDataDto couponInfo;

    @ApiModelProperty(value = "是否已领取，true：是；false：否")
    private Boolean hasCollected;

}
