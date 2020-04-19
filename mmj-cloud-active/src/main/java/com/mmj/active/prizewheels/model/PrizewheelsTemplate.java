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
 * 幸运大转盘-活动配置表
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_prizewheels_template")
@ApiModel(value="PrizewheelsTemplate对象", description="幸运大转盘-活动配置表")
public class PrizewheelsTemplate extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "转盘活动规则描述")
    @TableField("RULE_DESC")
    private String ruleDesc;

    @ApiModelProperty(value = "新人红包金额")
    @TableField("NEW_USER_REDPACKET")
    private Double newUserRedpacket;

    @ApiModelProperty(value = "新人获得买买币的数量")
    @TableField("NEW_USER_GET_COINS_AMOUNT")
    private Integer newUserGetCoinsAmount;

    @ApiModelProperty(value = "一袋买买币的个数")
    @TableField("BAG_OF_COINS_NUM")
    private Integer bagOfCoinsNum;

    @ApiModelProperty(value = "一箱买买币的个数")
    @TableField("BOX_OF_COINS_NUM")
    private Integer boxOfCoinsNum;

    @ApiModelProperty(value = "设定买买币自增的时间间隔，单位：分钟")
    @TableField("INCREMENT_COINS_MINUTES")
    private Integer incrementCoinsMinutes;

    @ApiModelProperty(value = "设定每次自增买买币的数量")
    @TableField("INCREMENT_COINS_AMOUNT")
    private Integer incrementCoinsAmount;

    @ApiModelProperty(value = "超过多少分钟没有进入转盘活动页停止增加买买币")
    @TableField("EXCEED_MINUTES")
    private Integer exceedMinutes;

    @ApiModelProperty(value = "每天签到可获得的买买币数量")
    @TableField("SIGN_GET_COINS_AMOUNT")
    private Integer signGetCoinsAmount;

    @ApiModelProperty(value = "点击转盘每次消耗的买买币数量")
    @TableField("CONSUMING_COINS_AMOUNT")
    private Integer consumingCoinsAmount;

    @ApiModelProperty(value = "每邀请一个好友并进入落地页可获得的买买币数量")
    @TableField("INVITE_FRIEND_GET_COINS_AMOUNT")
    private Integer inviteFriendGetCoinsAmount;

    @ApiModelProperty(value = "邀请满多少个好友（好友必须点击分享进入落地页），可获得一个随机红包")
    @TableField("INVITE_FRIEND_COUNT_FOR_GET_REDPACKET")
    private Integer inviteFriendCountForGetRedpacket;

    @ApiModelProperty(value = "每分享一次商品可获得的买买币个数，好友必须点击分享进去才会增加")
    @TableField("SHARE_GOODS_GET_COINS_AMOUNT")
    private Integer shareGoodsGetCoinsAmount;

    @ApiModelProperty(value = "商品每天只有前n次分享增加买买币，即n个不同的人通过分享标识点击分享才增加分享人的买买币，理论上和get_coins_share_goods_max_count值保持一致")
    @TableField("GET_COINS_SHARE_GOODS_MAX_COUNT")
    private Integer getCoinsShareGoodsMaxCount;

    @ApiModelProperty(value = "分享商品的任务完成多少次可领取随机红包")
    @TableField("SHARE_GOODS_COUNT_FOR_GET_REDPACKET")
    private Integer shareGoodsCountForGetRedpacket;

    @ApiModelProperty(value = " 初始提现门槛, 如100")
    @TableField("WITHDRAW_THRESHOLD_MIN_MONEY")
    private Double withdrawThresholdMinMoney;

    @ApiModelProperty(value = "提现门槛最高值, 如400")
    @TableField("WITHDRAW_THRESHOLD_MAX_MONEY")
    private Double withdrawThresholdMaxMoney;

    @ApiModelProperty(value = "随机红包的金额范围")
    @TableField("RANDOM_REDPACKET_RANGE")
    private String randomRedpacketRange;

    @ApiModelProperty(value = "用户在公众号回复关键字得到的买买币个数")
    @TableField("OFFICIAL_ACCOUNT_REPLY_GET_COINS")
    private Integer officialAccountReplyGetCoins;

    @ApiModelProperty(value = "在公众号指定回复的关键词以获得买买币")
    @TableField("REPLY_KEYWORD")
    private String replyKeyword;
    
    @ApiModelProperty(value = "任务四配置的公众号名称")
    @TableField("OFFICIAL_ACCOUNT_NAME")
    private String officialAccountName;
    
    @ApiModelProperty(value = "任务四配置的公众号appid")
    @TableField("OFFICIAL_ACCOUNT_APPID")
    private String officialAccountAppid;

    @ApiModelProperty(value = "转盘活动是否开启，0-不开启，1-开启")
    @TableField("IS_OPEN")
    private Boolean isOpen;

    @ApiModelProperty(value = "创建人的用户ID")
    @TableField("CREATE_BY")
    private Integer createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "更新人的用户ID")
    @TableField("UPDATE_BY")
    private Integer updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;


}
