package com.mmj.third.jushuitan.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @description: 查询库存
 * @auther: KK
 * @date: 2019/6/6
 */
@Data
@ApiModel("聚水潭查询库存请求参数")
public class InventoryQueryVo {
    /**
     * 分仓公司编号
     */
    @ApiModelProperty("分仓公司编号")
    private Integer wmsCoId;
    /**
     * 商品编码，最多50
     */
    @NotNull
    @Size(min = 1, max = 50)
    @ApiModelProperty("商品编码，最多50")
    private List<String> skus;
}
