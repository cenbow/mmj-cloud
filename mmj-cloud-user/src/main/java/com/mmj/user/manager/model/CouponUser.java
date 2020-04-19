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
 * 用户关联优惠券表
 * </p>
 *
 * @author KK
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_coupon_user")
@ApiModel(value="CouponUser对象", description="用户关联优惠券表")
public class CouponUser extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关联ID")
    @TableId(value = "MAPPER_ID", type = IdType.AUTO)
    private Integer mapperId;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "优惠券ID")
    @TableField("COUPON_ID")
    private Integer couponId;

    @ApiModelProperty(value = "优惠券来源 SYSTEM：系统发送，TOPIC：专题页领取，BRUSH：刷一刷，INDEX：弹出优惠券领用")
    @TableField("COUPON_SOURCE")
    private String couponSource;

    @ApiModelProperty(value = "有效期开始时间")
    @TableField("START_TIME")
    private Date startTime;

    @ApiModelProperty(value = "有效期结束时间")
    @TableField("END_TIME")
    private Date endTime;

    @ApiModelProperty(value = "获得时间")
    @TableField("CHECK_TIME")
    private Date checkTime;

    @ApiModelProperty(value = "是否使用")
    @TableField("USED_FLAG")
    private Integer usedFlag;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "是否提示即将过期")
    @TableField("SEND_FLAG")
    private Integer sendFlag;

    @ApiModelProperty(value = "是否失效")
    @TableField("MISS_FLAG")
    private Integer missFlag;

    @ApiModelProperty(value = "距离结束时间值")
    @TableField("DISTANCE_TIME")
    private Date distanceTime;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;


}
