package com.mmj.active.coupon.model.dto;

import com.mmj.active.coupon.model.CouponInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 优惠券信息
 * @auther: KK
 * @date: 2019/8/28
 */
@Data
public class CouponInfoDto extends CouponInfo {
    @ApiModelProperty(value = "当天发放量")
    private Integer toDaySendNumber;

    @ApiModelProperty(value = "可用商品分类")
    private List<String> goodClassList;

    @ApiModelProperty(value = "可用或不可用商品ID")
    private List<Integer> goodIdList;
}
