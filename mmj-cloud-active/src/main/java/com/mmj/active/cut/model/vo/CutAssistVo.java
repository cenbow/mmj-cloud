package com.mmj.active.cut.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 帮砍
 * @auther: KK
 * @date: 2019/6/13
 */
@Data
@ApiModel("帮砍")
public class CutAssistVo {
    @ApiModelProperty("砍价号")
    @NotNull
    private String cutNo;
}
