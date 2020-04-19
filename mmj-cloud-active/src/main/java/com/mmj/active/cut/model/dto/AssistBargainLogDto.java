package com.mmj.active.cut.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: 帮砍记录
 * @auther: KK
 * @date: 2019/6/13
 */
@Data
@ApiModel("帮砍记录")
public class AssistBargainLogDto {
    @ApiModelProperty(value = "帮砍用户ID")
    private Long userId;
    @ApiModelProperty(value = "帮砍用户昵称")
    private String nickname;
    @ApiModelProperty(value = "帮砍用户头像")
    private String picUrl;
    @ApiModelProperty(value = "帮砍金额")
    private BigDecimal cutAmount;
    @ApiModelProperty(value = "奖励帮砍金额")
    private BigDecimal rewardAmount;
    @ApiModelProperty(value = "帮砍时间")
    private Date cutTime;
    @ApiModelProperty(value = "标识该条记录是否为当前用户砍的")
    private boolean assistPeople = false;
}
