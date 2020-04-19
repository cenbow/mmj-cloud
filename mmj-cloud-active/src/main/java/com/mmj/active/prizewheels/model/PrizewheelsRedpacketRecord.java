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
 * 转盘活动 - 用户红包变更记录表
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_prizewheels_redpacket_record")
@ApiModel(value="PrizewheelsRedpacketRecord对象", description="转盘活动 - 用户红包变更记录表")
public class PrizewheelsRedpacketRecord extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "UUID主键")
    @TableId(value = "ID", type = IdType.INPUT)
    private String id;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "红包金额变动的数目，增加为正数， 提现时为负数")
    @TableField("INCREASE_MONEY")
    private Double increaseMoney;

    @ApiModelProperty(value = "变动后的实时红包余额")
    @TableField("BLANCE_REDPACKET")
    private Double blanceRedpacket;

    @ApiModelProperty(value = "红包金额变动的类型：NEW_USER - 新人获得 ，FIXED_REDPACKET_5 - 转盘抽奖获得的5元固定红包，FIXED_REDPACKET_10 - 转盘抽奖获得的10元固定红包，FIXED_REDPACKET_100 - 转盘抽奖获得的100元固定红包，RANDOM_REDPACKET - 转盘抽奖获得的随机金额红包，INVITE - 邀请好友数量达标获得随机红包，SHARE - 分享商品达标获得随机红包 ，WITHDRAW - 提现")
    @TableField("GOT_WAYS")
    private String gotWays;

    @ApiModelProperty(value = "领取状态：PENDING - 待领取(获得抽到固定红包后需要分享才能获得，获得随机红包后需要点击放入我的余额才能获得)， GOT - 已领取， WITHDRAW - 已提现")
    @TableField("STATUS")
    private String status;

    @ApiModelProperty(value = "对应的奖品ID，只有当获取方式为抽奖获得的红包时，此字段值有效")
    @TableField("PRIZE_RECORD_ID")
    private String prizeRecordId;

    @ApiModelProperty(value = "任务达标的数量 ，每次任务达标时就记录当前达标时的数量，如分享好友的个数，分享商品的个数")
    @TableField("REACHED_COUNT")
    private Integer reachedCount;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;


}
