package com.mmj.active.coupon.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 商详隐藏
 * @auther: KK
 * @date: 2019/8/16
 */
@Data
@ApiModel(value = "商详隐藏请求", description = "商详隐藏请求")
public class DetailShowVo {
    @ApiModelProperty(value = "优惠券ID")
    private Integer couponId;
    @ApiModelProperty(value = "商详页是否展示 0否 1是")
    private Integer detailShow;
}
