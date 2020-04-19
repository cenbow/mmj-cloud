package com.mmj.active.cut.model.vo;

import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: boss砍价查询
 * @auther: KK
 * @date: 2019/6/12
 */
@Data
@ApiModel(value = "查询条件", description = "砍价查询条件")
public class BossCutQueryVo extends BaseModel {
    @ApiModelProperty(value = "商品名称")
    private String goodName;
    @ApiModelProperty(value = "商品SPU")
    private String goodSpu;
}
