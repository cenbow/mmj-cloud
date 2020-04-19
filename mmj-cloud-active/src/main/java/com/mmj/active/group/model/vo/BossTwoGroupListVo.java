package com.mmj.active.group.model.vo;

import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 二人团列表
 * @auther: KK
 * @date: 2019/7/23
 */
@Data
@ApiModel(value = "二人团列表查询", description = "二人团列表查询")
public class BossTwoGroupListVo extends BaseModel {
    @ApiModelProperty(value = "商品名称")
    private String goodName;
    @ApiModelProperty(value = "商品SPU")
    private String goodSpu;
}
