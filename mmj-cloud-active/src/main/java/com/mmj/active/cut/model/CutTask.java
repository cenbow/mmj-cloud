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
 * 砍价任务
 * </p>
 *
 * @author KK
 * @since 2019-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cut_task")
@ApiModel(value="CutTask对象", description="砍价任务")
public class CutTask extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务ID")
    @TableId(value = "TASK_ID", type = IdType.AUTO)
    private Integer taskId;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "发起数")
    @TableField("SPONSOR_NUMBER")
    private Integer sponsorNumber;

    @ApiModelProperty(value = "帮砍数")
    @TableField("ASSIST_NUMBER")
    private Integer assistNumber;

    @ApiModelProperty(value = "任务一奖励ID")
    @TableField("TASK_ONE_ASSIST_ID")
    private Integer taskOneAssistId;

    @ApiModelProperty(value = "任务一奖励金额")
    @TableField("TASK_ONE_REWARD_AMOUNT")
    private BigDecimal taskOneRewardAmount;

    @ApiModelProperty(value = "任务一奖励时间")
    @TableField("TASK_ONE_REWARD_TIME")
    private Date taskOneRewardTime;

    @ApiModelProperty(value = "任务二奖励ID")
    @TableField("TASK_TWO_ASSIST_ID")
    private Integer taskTwoAssistId;

    @ApiModelProperty(value = "任务二奖励金额")
    @TableField("TASK_TWO_REWARD_AMOUNT")
    private BigDecimal taskTwoRewardAmount;

    @ApiModelProperty(value = "任务二奖励时间")
    @TableField("TASK_TWO_REWARD_TIME")
    private Date taskTwoRewardTime;

    @ApiModelProperty(value = "任务三奖励ID")
    @TableField("TASK_THREE_ASSIST_ID")
    private Integer taskThreeAssistId;

    @ApiModelProperty(value = "任务三奖励金额")
    @TableField("TASK_THREE_REWARD_AMOUNT")
    private BigDecimal taskThreeRewardAmount;

    @ApiModelProperty(value = "任务三奖励时间")
    @TableField("TASK_THREE_REWARD_TIME")
    private Date taskThreeRewardTime;

    @ApiModelProperty(value = "红包码")
    @TableField("RED_CODE")
    private String redCode;

    @ApiModelProperty(value = "是否删除 0否 1是")
    @TableField("DEL_FLAG")
    private Integer delFlag;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
