package com.mmj.active.group.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @description: 二人团商品编辑
 * @auther: KK
 * @date: 2019/7/22
 */
@Data
public class BossTwoGroupGoodsVo {
    @ApiModelProperty(value = "关联ID")
    @NotNull
    @Size(min = 1)
    private List<Long> mapperyIds;
}
