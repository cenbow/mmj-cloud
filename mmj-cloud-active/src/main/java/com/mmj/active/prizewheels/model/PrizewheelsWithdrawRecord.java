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
 * 转盘活动 - 用户提现记录表
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_prizewheels_withdraw_record")
@ApiModel(value="PrizewheelsWithdrawRecord对象", description="转盘活动 - 用户提现记录表")
public class PrizewheelsWithdrawRecord extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "用户昵称")
    @TableField("NICKNAME")
    private String nickname;

    @ApiModelProperty(value = "此次提现前的红包余额")
    @TableField("LAST_BALANCE")
    private Double lastBalance;

    @ApiModelProperty(value = "用户提现金额")
    @TableField("WITHDRAW_MONEY")
    private Double withdrawMoney;

    @ApiModelProperty(value = "提现后的实时余额")
    @TableField("REALTIME_BALANCE")
    private Double realtimeBalance;
    
    @ApiModelProperty(value = "交易号")
    @TableField("TRADE_NO")
    private String tradeNo;

    @ApiModelProperty(value = "创建时间/提现时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
