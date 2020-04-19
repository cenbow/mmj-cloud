package com.mmj.active.prizewheels.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.mmj.common.model.BaseModel;

/**
 * <p>
 * 转盘活动 - 用户买买币变更记录表
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_prizewheels_coins_record")
@ApiModel(value="PrizewheelsCoinsRecord对象", description="转盘活动 - 用户买买币变更记录表")
public class PrizewheelsCoinsRecord extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "UUID主键")
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "买买币 变动的数量，增加为正数，买买币消耗时为负数")
    private Integer increaseCoins;

    @ApiModelProperty(value = "每次变动后当前实时的买买币余额")
    private Integer blanceCoins;

    @ApiModelProperty(value = " 买买币变动的类型：NEW_USER - 新人获得 ，COINS_BAG -  转盘抽奖获得一袋买买币，COINS_BOX - 转盘抽奖获得一箱买买币，INCREMENT - 自增获得， SIGN - 签到获得，INVITE - 邀请好友获得， SHARE_GOODS - 分享商品获得，CLICK_DRAW - 转盘消耗")
    private String gotWays;

    @ApiModelProperty(value = "奖励编码，对应t_prizewheels_prize_type.prize_code")
    private String prizeCode;

    @ApiModelProperty(value = "奖品名称，对应t_prizewheels_prize_type.prize_name")
    private String prizeName;

    @ApiModelProperty(value = "领取状态：PENDING - 待领取(没有点击放入)， GOT - 已领取, CONSUMED - 已被消耗")
    private String status;

    @ApiModelProperty(value = "我的奖品ID，只有当获得方式为转盘抽奖获得，此字段值有效")
    private String prizeRecordId;

    @ApiModelProperty(value = "邀请的好友的用户ID，当好友点击分享时才存储此值，如抽奖分享、商品分享")
    private Long friendsUserId;

    @ApiModelProperty(value = "商品ID，当got_ways值为SHARE_GOODS时此字段值有效")
    private Integer goodId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
