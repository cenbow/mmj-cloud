package com.mmj.good.feigin.dto;

import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class WebShow extends BaseModel {

    @ApiModelProperty(value = "显示ID")
    private Integer showId;

    @ApiModelProperty(value = "分类编码（首页为空）")
    private String classCode;

    @ApiModelProperty(value = "是否显示分类")
    private Integer showFlag;

    @ApiModelProperty(value = "顶部大图是否显示")
    private Integer topShow;

    @ApiModelProperty(value = "营销是否显示")
    private Integer maketingShow;

    @ApiModelProperty(value = "橱窗是否显示")
    private Integer showcaseShow;

    @ApiModelProperty(value = "小程序分享图是否展示")
    private Integer wxshardShow;

    @ApiModelProperty(value = "是否显示商品排序")
    private Integer goddOrder;

    @ApiModelProperty(value = "创建人")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    private Date createrTime;
}
