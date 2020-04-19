package com.mmj.active.cut.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 跳转砍价详情
 * @auther: KK
 * @date: 2019/6/14
 */
@Data
@ApiModel("砍价详情请求参数")
public class CutDetailsVo {
    @ApiModelProperty("砍价号")
    @NotNull
    private String cutNo;

    @ApiModelProperty("发起砍价用户ID")
//    @NotNull
    private String bargainUserId;
}
