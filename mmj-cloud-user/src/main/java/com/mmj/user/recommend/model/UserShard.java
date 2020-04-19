package com.mmj.user.recommend.model;

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
 * 分享关联表
 * </p>
 *
 * @author dashu
 * @since 2019-06-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user_shard")
@ApiModel(value = "UserShard对象", description = "分享关联表")
public class UserShard extends BaseModel {

    private static final long serialVersionUID = 4866552346563596571L;

    @ApiModelProperty(value = "主键，分享ID")
    @TableId(value = "SHARD_ID", type = IdType.AUTO)
    private Integer shardId;

    @ApiModelProperty(value = "分享人的用户ID")
    @TableField("SHARD_FROM")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long shardFrom;

    @ApiModelProperty(value = "推荐人头像")
    @TableField("FROM_HEAD_IMG")
    private String fromHeadImg;

    @ApiModelProperty(value = "推荐人昵称")
    @TableField("FROM_NICL_NAME")
    private String fromNiclName;

    @ApiModelProperty(value = "分享人的oppenid")
    @TableField("FROM_SHARD_OPENID")
    private String fromShardOpenid;

    @ApiModelProperty(value = "被分享人的用户ID")
    @TableField("SHARD_TO")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long shardTo; //被动接受分享的人

    @ApiModelProperty(value = "被分享人头像")
    @TableField("TO_HEAD_IMG")
    private String toHeadImg;

    @ApiModelProperty(value = "被分享人昵称")
    @TableField("TO_NICK_NAME")
    private String toNickName;

    @ApiModelProperty(value = "被分享人的oppenid")
    @TableField("TO_SHARD_OPENID")
    private String toShardOpenid;

    @ApiModelProperty(value = "分享类型 RECOMMEND：推荐; MEMBER 分享成为会员")
    @TableField("SHARD_TYPE")
    private String shardType;

    @ApiModelProperty(value = "分享渠道，对应Header中的appType")
    @TableField("SHARD_SOURCE")
    private String shardSource;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "关联唯一值(推荐返现关联商品id)")
    @TableField("BUSINESS_VALUE")
    private String businessValue;

    @ApiModelProperty(value = "订单状态 :  0:待成团  1:待付款  2:已付款  3:已收货(确认收货)  4:退款")
    @TableField("ORDER_STATUS")
    private Integer orderStatus;

    @ApiModelProperty(value = "订单金额，以分为单位")
    @TableField("ORDER_AMOUNT")
    private Integer orderAmount;

    @ApiModelProperty(value = "订单创建时间")
    @TableField("ORDER_CREATE_TIME")
    private Date orderCreateTime;

    @ApiModelProperty(value = "订单结束时间")
    @TableField("ORDER_END_TIME")
    private Date orderEndTime;

    @ApiModelProperty(value = "是否新用户,0:新用户  1:老用户")
    @TableField("USER_FLAG")
    private Integer userFlag;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
