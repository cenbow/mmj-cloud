package com.mmj.active.cut.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户砍价奖励表
 * </p>
 *
 * @author KK
 * @since 2019-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cut_reward")
@ApiModel(value = "CutReward对象", description = "用户砍价奖励表")
public class CutReward extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "奖励ID")
    @TableId(value = "REWARD_ID", type = IdType.AUTO)
    private Integer rewardId;

    @ApiModelProperty(value = "奖励类型 0新用户帮砍获取首砍金额奖励 1任务获取首砍奖励 2.任务获取微信红包奖励 3.砍价订单支付后后获取奖励")
    @TableField("REWARD_TYPE")
    private Integer rewardType;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "砍价编码")
    @TableField("CUT_NO")
    private String cutNo;

    @ApiModelProperty(value = "砍价ID")
    @TableField("CUT_ID")
    private Integer cutId;

    @ApiModelProperty(value = "奖励值")
    @TableField("REWARD_VALUE")
    private BigDecimal rewardValue;

    @ApiModelProperty(value = "奖励值类型 0金额 1比例")
    @TableField("REWARD_VALUE_TYPE")
    private Integer rewardValueType;

    @ApiModelProperty(value = "红包码")
    @TableField("RED_CODE")
    private String redCode;

    @ApiModelProperty(value = "是否使用 0 未使用 1 已使用")
    @TableField("USE_FLAG")
    private Integer useFlag;

    @ApiModelProperty(value = "使用时间")
    @TableField("USE_TIME")
    private Date useTime;

    @ApiModelProperty(value = "使用砍价编码")
    @TableField("USE_CUT_NO")
    private String useCutNo;

    @ApiModelProperty(value = "是否删除 0否 1是")
    @TableField("DEL_FLAG")
    private Integer delFlag;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "过期时间")
    @TableField("EXPIRE_TIME")
    private Date expireTime;
}
