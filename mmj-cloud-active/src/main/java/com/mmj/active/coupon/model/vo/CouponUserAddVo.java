package com.mmj.active.coupon.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @description: 用户优惠券
 * @auther: KK
 * @date: 2019/6/27
 */
@Data
@ApiModel(value = "用户新增优惠券", description = "用户新增优惠券")
public class CouponUserAddVo {
    @ApiModelProperty(value = "用户ID （可为空，默认取当前用户）")
    private Long userId;
    @NotNull
    @ApiModelProperty(value = "优惠券ID")
    private Integer couponId;
    @NotBlank
    @ApiModelProperty(value = "优惠券来源 SYSTEM：系统发送，TOPIC：专题页领取，BRUSH：刷一刷，INDEX：弹出优惠券领用")
    private String couponSource;
}
