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
 * 转盘活动 - 账户表，包含买买币余额、红包余额
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_prizewheels_account")
@ApiModel(value="PrizewheelsAccount对象", description="转盘活动 - 账户表，包含买买币余额、红包余额")
public class PrizewheelsAccount extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    @TableId(value = "USER_ID", type = IdType.INPUT)
    private Long userId;

    @ApiModelProperty(value = "买买币余额")
    @TableField("COINS_BALANCE")
    private Integer coinsBalance;

    @ApiModelProperty(value = "红包余额")
    @TableField("REDPACKET_BALANCE")
    private Double redpacketBalance;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

    @ApiModelProperty(value = "是否发送10元翻倍红包 0: 抽到10元红包后,等待关注公众号; 1:关注了公众号,等待用户领取红包; 2:创建账号时为初始化(历史数据为空); 3:前端已提醒")
    @TableField("TEN_PRIZE")
    private Integer tenPrize;


}
