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
 * 弹窗管理
 * </p>
 *
 * @author dashu
 * @since 2019-06-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_web_alert")
@ApiModel(value="WebAlert对象", description="弹窗管理")
public class WebAlert extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "弹窗ID")
    @TableId(value = "ALERT_ID", type = IdType.AUTO)
    private Integer alertId;

    @ApiModelProperty(value = "弹窗名称")
    @TableField("ALERT_NAME")
    private String alertName;

    @ApiModelProperty(value = "弹窗类型：ALERT：弹窗 PAGE：浮层 APPALERT:app弹窗")
    @TableField("ALERT_TYPE")
    private String alertType;

    @ApiModelProperty(value = "是否新用户显示")
    @TableField("SHOW_NEW")
    private Integer showNew;

    @ApiModelProperty(value = "是否老用户显示")
    @TableField("SHOW_OLD")
    private Integer showOld;

    @ApiModelProperty(value = "是否会员显示")
    @TableField("SHOW_MEMBER")
    private Integer showMember;

    @ApiModelProperty(value = "前端图片名")
    @TableField("WEB_IMAGE")
    private String webImage;

    @ApiModelProperty(value = "弹出图片地址")
    @TableField("ALERT_IMAGE")
    private String alertImage;

    @ApiModelProperty(value = "跳转类型 COUPON：优惠券 HRAF：链接")
    @TableField("HRAF_TYPE")
    private String hrafType;

    @ApiModelProperty(value = "链接地址")
    @TableField("HRAF_URL")
    private String hrafUrl;

    @ApiModelProperty(value = "弹窗状态 0：禁用 1：正常")
    @TableField("ALERT_STATUS")
    private Integer alertStatus;

    @ApiModelProperty(value = "弹窗长度")
    @TableField("ALERT_LENGTH")
    private Integer alertLength;

    @ApiModelProperty(value = "弹窗宽度")
    @TableField("ALERT_WIDE")
    private Integer alertWide;

    @ApiModelProperty(value = "弹窗模块 INDEX：首页")
    @TableField("ALERT_MODEL")
    private String alertModel;

    @ApiModelProperty(value = "弹窗优先级")
    @TableField("ALERT_ORDER")
    private Integer alertOrder;

    @ApiModelProperty(value = "优惠券ID")
    @TableField("COUPON_ID")
    private String couponId;

    @ApiModelProperty(value = "开始时间")
    @TableField("START_TIME")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("END_TIME")
    private Date endTime;

    @ApiModelProperty(value = "提示时长")
    @TableField("TIME_LONG")
    private Integer timeLong;

    @ApiModelProperty(value = "提示文案")
    @TableField("ALERT_DESC")
    private String alertDesc;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
