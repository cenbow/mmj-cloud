package com.mmj.user.manager.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 用户活动参与表
 * </p>
 *
 * @author lyf
 * @since 2019-06-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user_active")
@ApiModel(value="UserActive对象", description="用户活动参与表")
public class UserActive extends BaseModel {

    private static final long serialVersionUID = -5688501841245385180L;

    @ApiModelProperty(value = "中奖ID")
    @TableId(value = "CHECK_ID", type = IdType.AUTO)
    private Integer checkId;

    @ApiModelProperty(value = "活动类型 1 抽奖 2 接力购 3 接力购抽奖 4十元三件 5 秒杀 ")
    @TableField("ACTIVE_TYPE")
    private Integer activeType;

    @ApiModelProperty(value = "活动ID")
    @TableField("BUSINESS_ID")
    private Integer businessId;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "抽奖码")
    @TableField("LOTTERY_CODE")
    private String lotteryCode;

    @ApiModelProperty(value = "订单ID")
    @TableField("ORDER_ID")
    private Long orderId;

    @ApiModelProperty(value = "订单编号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
