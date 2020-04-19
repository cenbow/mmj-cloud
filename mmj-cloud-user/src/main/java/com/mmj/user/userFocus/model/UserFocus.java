package com.mmj.user.userFocus.model;

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
 * 用户关注公众号记录
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user_focus")
@ApiModel(value="UserFocus对象", description="用户关注公众号记录")
public class UserFocus extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "FOCUS_ID", type = IdType.AUTO)
    private Integer focusId;

    @ApiModelProperty(value = "用户id")
    @TableField("USER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @TableField("OPEN_ID")
    private String openId;

    @ApiModelProperty(value = "公众号id")
    @TableField("APP_ID")
    private String appId;

    @ApiModelProperty(value = "状态(0 未关注 1 已关注 2 已取消 3 取消后再关注 4 未授权已关注)")
    @TableField("STATUS")
    private Integer status;

    @ApiModelProperty(value = "奖励获取状态(0 未获取 1 已获取 2 已使用)")
    @TableField("REWARD")
    private Integer reward;

    @ApiModelProperty(value = "模块(0: 首页 1:秒杀 2:砍价 3:抽奖 4:签到 5:十元三件 6:转盘 7:店铺订单)")
    @TableField("MODULE")
    private Integer module;

    @TableField("TYPE")
    private Integer type;

    @ApiModelProperty(value = "关注类型（0：群；1：公众号）")
    @TableField("FORM")
    private Integer form;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;

    @TableField("PASSING_DATA")
    private String passingData;


}
