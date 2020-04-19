package com.mmj.pay.common.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UseUserCouponVo {

    private Long userId;

    @NotNull
    private Integer couponCode;

    // "使用状态 true使用优惠券 false恢复优惠券"
    private Boolean useStatus = true;
}
