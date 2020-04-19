package com.mmj.order.common.model;

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
 * 会员表
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user_member")
@ApiModel(value="UserMember对象", description="会员表")
public class UserMember extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，会员ID，8位数")
    @TableId(value = "MEMBER_ID", type = IdType.INPUT)
    private Integer memberId;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "会员状态是否有效: 0-否；1-是")
    @TableField("ACTIVE")
    private Boolean active;

    @ApiModelProperty(value = "成为会员的订单号，只记录非会员成为会员的当单")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "成为会员的方式：UPGRADE-准会员升级成为的会员；ORDER-非会员下单支付后满足消费条件成为的会员;BUY-花钱购买会员")
    @TableField("BE_MEMBER_TYPE")
    private String beMemberType;

    @ApiModelProperty(value = "成为会员的时间")
    @TableField("BE_MEMBER_TIME")
    private Date beMemberTime;

    @ApiModelProperty(value = "会员过期时间，成为会员的时间往后推1年")
    @TableField("EXPIRY_DATE")
    private Date expiryDate;

    @ApiModelProperty(value = "降级时间")
    @TableField("DEGRADE_TIME")
    private Date degradeTime;

    @ApiModelProperty(value = "备注,如降级原因")
    @TableField("REMARK")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;


}
