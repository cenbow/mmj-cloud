package com.mmj.active.cut.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 砍价任务
 * @auther: KK
 * @date: 2019/7/26
 */
@Data
public class CutUserTaskDto {
    @ApiModelProperty(value = "帮砍数")
    private Integer assistNumber = 0;

    @ApiModelProperty(value = "任务一状态 0未完成 1完成未使用 2已使用")
    private Integer taskOneStatus = 0;

    @ApiModelProperty(value = "任务二状态 0未完成 1完成未使用 2已使用")
    private Integer taskTwoStatus = 0;

    @ApiModelProperty(value = "任务三状态 0未完成 1完成未使用 2已使用")
    private Integer taskThreeStatus = 0;

    @ApiModelProperty(value = "任务三状态完成后的红包码")
    private String redCode;

    @ApiModelProperty(value = "任务三状态完成后奖励的红包金额")
    private BigDecimal redAmount;

    @ApiModelProperty(value = "获取红包码的砍价号")
    private String cutNo;
}
