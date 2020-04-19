package com.mmj.good.feigin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel("普通商品查询请求参数")
public class SkuQueryVo {
    /**
     * 商品编码，最多50
     */
    @NotNull
    @Size(min = 1, max = 50)
    @ApiModelProperty("商品编码，最多50")
    private List<String> skus;
}

