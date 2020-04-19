package com.mmj.active.cut.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 新增/编辑砍价返回
 * @auther: KK
 * @date: 2019/8/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "新增-编辑砍价返回", description = "新增-编辑砍价返回")
public class BossCutEditDto {
    @ApiModelProperty(value = "砍价ID")
    private Integer cutId;
}
