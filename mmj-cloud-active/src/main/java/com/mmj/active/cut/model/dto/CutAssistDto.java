package com.mmj.active.cut.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 帮砍返回
 * @auther: KK
 * @date: 2019/9/23
 */
@Data
@ApiModel(value = "帮砍返回信息", description = "帮砍返回信息")
public class CutAssistDto {
    @ApiModelProperty(value = "帮砍金额")
    private BigDecimal cutAmount;
    @ApiModelProperty(value = "帮砍获取首砍提升比例")
    private BigDecimal firstCutRate;
}
