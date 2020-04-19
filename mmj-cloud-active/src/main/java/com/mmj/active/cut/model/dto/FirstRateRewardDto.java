package com.mmj.active.cut.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description: 获取首砍奖励
 * @auther: KK
 * @date: 2019/9/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "获取首砍奖励", description = "获取首砍奖励")
public class FirstRateRewardDto {
    @ApiModelProperty("奖励值")
    public BigDecimal rewardValue;
    @ApiModelProperty("奖励值类型 0金额 1比例")
    public Integer rewardValueType;
    @ApiModelProperty("距离过期的时间戳")
    public Long expiredTime;
}
