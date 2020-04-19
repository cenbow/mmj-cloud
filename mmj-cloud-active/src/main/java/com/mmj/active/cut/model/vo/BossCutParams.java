package com.mmj.active.cut.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 砍价操作参数
 * @auther: KK
 * @date: 2019/6/12
 */
@Data
@ApiModel(value = "砍价操作请求参数", description = "砍价操作请求参数")
public class BossCutParams {
    @NotNull
    private Integer cutId;
}
