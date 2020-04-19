package com.mmj.user.manager.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 个人优惠券中心
 * @auther: KK
 * @date: 2019/7/22
 */
@Data
@ApiModel(value = "个人中心优惠券", description = "个人中心优惠券")
public class PersonalCouponInfoDto {
    @ApiModelProperty(value = "是否会员日 true是 false否")
    private Boolean memberDay;

    @ApiModelProperty(value = "优惠券数量")
    private Integer couponTotal;

    @ApiModelProperty(value = "文案备注")
    private String desc;
}
