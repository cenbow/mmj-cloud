package com.mmj.active.cut.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 发起砍价
 * @auther: KK
 * @date: 2019/7/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("发起砍价")
public class CutUserDto {
    @ApiModelProperty("砍价号")
    private String cutNo;
}
