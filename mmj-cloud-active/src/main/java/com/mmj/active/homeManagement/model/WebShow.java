package com.mmj.active.homeManagement.model;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 页面展示表
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_web_show")
@ApiModel(value="WebShow对象", description="页面展示表")
public class WebShow extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "显示ID")
    @TableId(value = "SHOW_ID", type = IdType.AUTO)
    private Integer showId;

    @ApiModelProperty(value = "分类编码（首页为空）")
    @TableField("CLASS_CODE")
    private String classCode;

    @ApiModelProperty(value = "是否显示分类")
    @TableField("SHOW_FLAG")
    private Integer showFlag;

    @ApiModelProperty(value = "顶部大图是否显示")
    @TableField("TOP_SHOW")
    private Integer topShow;

    @ApiModelProperty(value = "营销是否显示")
    @TableField("MAKETING_SHOW")
    private Integer maketingShow;

    @ApiModelProperty(value = "橱窗是否显示")
    @TableField("SHOWCASE_SHOW")
    private Integer showcaseShow;

    @ApiModelProperty(value = "小程序分享图是否展示")
    @TableField("WXSHARD_SHOW")
    private Integer wxshardShow;

    @ApiModelProperty(value = "是否显示商品排序")
    @TableField("GODD_ORDER")
    private Integer goddOrder;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
