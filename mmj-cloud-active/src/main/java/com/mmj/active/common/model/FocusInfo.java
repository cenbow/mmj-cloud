package com.mmj.active.common.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_focus_info")
@ApiModel(value="FocusInfo对象", description="")
public class FocusInfo extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    @TableId(value = "USER_ID", type = IdType.INPUT)
    private Long userId;

    @ApiModelProperty(value = "公众号openId")
    @TableId(value = "OPEN_ID")
    private String openId;

    @ApiModelProperty(value = "模块(0: 首页 1:秒杀 2:砍价 3:抽奖 4:签到 5:十元三件 6:转盘 7:店铺订单)(1:秒杀 2:砍价 3:抽奖 4:签到)")
    @TableField("MODULE")
    private Integer module;

    @ApiModelProperty(value = "状态(0 未关注 1 已关注 2 已取消 3 取消后再关注 4 未授权已关注)")
    @TableField(value = "STATUS", strategy = FieldStrategy.IGNORED)
    private Integer status;


}
