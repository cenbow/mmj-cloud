package com.mmj.active.cut.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 砍价帮砍用户获取奖励配置对象
 * @auther: KK
 * @date: 2019/6/12
 */
@Data
@ApiModel(value = "砍价帮砍用户获取奖励配置对象", description = "砍价信息表")
public class BossCutEditAwardVo extends BossCutAddAwardVo {
    @ApiModelProperty(value = "ID")
    @NotNull
    private Integer awardId;

}
