package com.mmj.order.common.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel(value = "用户批量领取优惠券", description = "用户批量领取优惠券")
public class UserCouponBatchVo {
    @ApiModelProperty(name = "用户ID，可为空（当为空时，默认获取当前用户）")
    private Long userId;
    @NotNull
    @Size(min = 1)
    @ApiModelProperty(name = "优惠券ID")
    private List<Integer> couponIds;
    @NotNull
    @ApiModelProperty(name = "优惠券来源 SYSTEM：系统发送，TOPIC：专题页领取，BRUSH：刷一刷，INDEX：弹出优惠券领用")
    private String couponSource;
}