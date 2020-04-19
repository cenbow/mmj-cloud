package com.mmj.active.homeManagement.model;


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

import java.util.Date;

/**
 * <p>
 * 橱窗配置表
 * </p>
 *
 * @author dashu
 * @since 2019-06-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_web_showcase")
@ApiModel(value="WebShowcase对象", description="橱窗配置表")
public class WebShowcase extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "橱窗ID")
    @TableId(value = "SHOWECASE_ID", type = IdType.AUTO)
    private Integer showecaseId;

    @ApiModelProperty(value = "橱窗标题")
    @TableField("SHOWECASE_TITLE")
    private String showecaseTitle;

    @ApiModelProperty(value = "开关")
    @TableField("ACTIVE_FLAG")
    private Integer activeFlag;

    @ApiModelProperty(value = "分类编码")
    @TableField("GOOD_CLASS")
    private String goodClass;

    @ApiModelProperty(value = "模版ID")
    @TableField("TEMPLATE_ID")
    private Integer templateId;

    @ApiModelProperty(value = "模板编码")
    @TableField("TEMPLATE_CODE")
    private String templateCode;

    @ApiModelProperty(value = "标签图片")
    @TableField("LABEL_IMAGE")
    private String labelImage;

    @ApiModelProperty(value = "展示形式 SINGLE：单行 DOUBLE：双行")
    @TableField("SHOW_TYPE")
    private String showType;

    @ApiModelProperty(value = "排序")
    @TableField("SHOWECASE_ORDER")
    private Integer showecaseOrder;

    @ApiModelProperty(value = "是否新用户展示")
    @TableField("SHOW_NEW")
    private Integer showNew;

    @ApiModelProperty(value = "是否老用户展示")
    @TableField("SHOW_OLD")
    private Integer showOld;

    @ApiModelProperty(value = "是否会员显示: 0:否 ;  1:是")
    @TableField("SHOW_MEMBER")
    private Integer showMember;

    @ApiModelProperty(value = "链接地址")
    @TableField("HRAF_URL")
    private String hrafUrl;

    @ApiModelProperty(value = "顶部图片")
    @TableField("TOP_IMAGE")
    private String topImage;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
