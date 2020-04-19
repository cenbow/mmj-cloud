package com.mmj.active.prizewheels.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.mmj.common.model.BaseModel;

/**
 * <p>
 * 转盘 - 我的奖品记录表
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_prizewheels_prize_record")
@ApiModel(value="PrizewheelsPrizeRecord对象", description="转盘 - 我的奖品记录表")
public class PrizewheelsPrizeRecord extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "UUID主键")
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "奖励名称：10元红包、5元红包、100元红包、随机红包、3元无门槛优惠券、5元无门槛优惠券、一袋买买币、一箱买买币")
    @TableField("PRIZE_NAME")
    private String prizeName;

    @ApiModelProperty(value = "对应T_PRIZEWHEELS_PRIZE_TYPE表的PRIZE_CODE，奖励类型，FIXED_REDPACKET_5 - 5元固定红包，FIXED_REDPACKET_10 - 10元固定红包，FIXED_REDPACKET_100 - 100元固定红包，RANDOM_REDPACKET - 随机红包， COUPON_3 - 3元无门槛优惠券，COUPON_5 - 5元无门槛优惠券， COINS_BAG - 一袋买买币，COINS_BOX - 一箱买买币")
    @TableField("PRIZE_CODE")
    private String prizeCode;

    @ApiModelProperty(value = "奖品类型，是大分类 ：REDPACKET - 红包，COUPON - 优惠券 ， COINS - 买买币")
    @TableField("PRIZE_TYPE")
    private String prizeType;

    @ApiModelProperty(value = "增加的数量，当奖励为优惠券时，此字段不用存值")
    @TableField("INCREASE_AMOUNT")
    private Double increaseAmount;

    @ApiModelProperty(value = "优惠券的失效时间，当奖励为优惠券时此字段值有效")
    @TableField("INVALID_TIME")
    private Date invalidTime;

    @ApiModelProperty(value = "领取状态：PENDING - 待领取(获得抽到固定红包后需要分享才能获得，获得随机红包后需要点击放入我的余额才能获得)， GOT - 已领取")
    @TableField("STATUS")
    private String status;

    @ApiModelProperty(value = "优惠券编码")
    @TableField("COUPON_CODE")
    private String couponCode;

    @ApiModelProperty(value = "优惠券使用地址")
    @TableField("COUPON_URL")
    private String couponUrl;

    @ApiModelProperty(value = "奖品的图标URL地址")
    @TableField("ICON_URL")
    private String iconUrl;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;


}
