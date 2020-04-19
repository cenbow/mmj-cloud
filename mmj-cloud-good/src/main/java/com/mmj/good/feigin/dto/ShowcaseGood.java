package com.mmj.good.feigin.dto;

import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="ShowcaseGood对象", description="橱窗商品关联表")
public class ShowcaseGood extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关联ID")
    private Integer mapperId;

    @ApiModelProperty(value = "橱窗ID")
    private Integer showcaseId;

    @ApiModelProperty(value = "商品ID")
    private Integer goodId;

    @ApiModelProperty(value = "排序")
    private Integer orderId;

    @ApiModelProperty(value = "是否展示标签")
    private Integer showFlag;

    @ApiModelProperty(value = "商品名称")
    private String goodName;

    @ApiModelProperty(value = "商品图片")
    private String goodImage;

    @ApiModelProperty(value = "商品排序")
    private String goodOrder;

    @ApiModelProperty(value = "商品SPU")
    private String goodSpu;


}