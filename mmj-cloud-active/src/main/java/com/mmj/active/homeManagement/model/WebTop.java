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
 * 顶部配置表
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_web_top")
@ApiModel(value="WebTop对象", description="顶部配置表")
public class WebTop extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "顶部大图ID")
    @TableId(value = "TOP_ID", type = IdType.AUTO)
    private Integer topId;

    @ApiModelProperty(value = "分类编码")
    @TableField("GOOD_CLASS")
    private String goodClass;

    @ApiModelProperty(value = "图片地址")
    @TableField("IMAGE_URL")
    private String imageUrl;

    @ApiModelProperty(value = "链接URL")
    @TableField("HRAF_URL")
    private String hrafUrl;

    @ApiModelProperty(value = "开始时间")
    @TableField("START_TIME")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("END_TIME")
    private Date endTime;

    @ApiModelProperty(value = "是否永久生效")
    @TableField("FOREVER_FLAG")
    private Integer foreverFlag;

    @ApiModelProperty(value = "新用户是否显示")
    @TableField("SHOW_NEW")
    private Integer showNew;

    @ApiModelProperty(value = "老用户是否显示")
    @TableField("SHOW_OLD")
    private Integer showOld;

    @ApiModelProperty(value = "是否会员显示: 0:否  ;  1:是")
    @TableField("SHOW_MEMBER")
    private Integer showMember;

    @ApiModelProperty(value = "排序")
    @TableField("ORDER_ID")
    private Integer orderId;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
