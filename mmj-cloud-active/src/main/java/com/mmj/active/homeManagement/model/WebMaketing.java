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
 * 营销配置表
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_web_maketing")
@ApiModel(value="WebMaketing对象", description="营销配置表")
public class WebMaketing extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "营销ID")
    @TableId(value = "MAKET_ID", type = IdType.AUTO)
    private Integer maketId;

    @ApiModelProperty(value = "营销名称")
    @TableField("MAKET_NAME")
    private String maketName;

    @ApiModelProperty(value = "分类编码")
    @TableField("GOOD_CLASS")
    private String goodClass;

    @ApiModelProperty(value = "图片地址")
    @TableField("IMAGE_URL")
    private String imageUrl;

    @ApiModelProperty(value = "链接URL")
    @TableField("HRAF_URL")
    private String hrafUrl;

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
