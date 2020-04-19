package com.mmj.user.manager.model;

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
 * 用户砍价表
 * </p>
 *
 * @author KK
 * @since 2019-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cut_user")
@ApiModel(value="CutUser对象", description="用户砍价表")
public class CutUser extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录ID")
    @TableId(value = "LOG_ID", type = IdType.AUTO)
    private Integer logId;

    @ApiModelProperty(value = "发起砍价用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "发起砍价ID")
    @TableField("SPONSOR_ID")
    private Integer sponsorId;

    @ApiModelProperty(value = "砍价编码")
    @TableField("CUT_NO")
    private String cutNo;

    @ApiModelProperty(value = "砍价ID")
    @TableField("CUT_ID")
    private Integer cutId;

    @ApiModelProperty(value = "帮砍人ID")
    @TableField("CUT_MEMBER")
    private Long cutMember;

    @ApiModelProperty(value = "帮砍时间")
    @TableField("CUT_TIME")
    private Date cutTime;

    @ApiModelProperty(value = "帮砍金额")
    @TableField("CUT_AMOUNT")
    private BigDecimal cutAmount;

    @ApiModelProperty(value = "奖励金额")
    @TableField("REWARD_AMOUNT")
    private BigDecimal rewardAmount;

    @ApiModelProperty(value = "剩余金额")
    @TableField("SURPLUS_AMOUNT")
    private BigDecimal surplusAmount;

    @ApiModelProperty(value = "砍价状态 -1 己过期  0 正在进行 1 己完成")
    @TableField("CUT_FLAG")
    private Integer cutFlag;


}
