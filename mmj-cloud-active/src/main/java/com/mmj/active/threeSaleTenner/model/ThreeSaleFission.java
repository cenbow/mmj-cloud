package com.mmj.active.threeSaleTenner.model;

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
 * 十元三件红包裂变
 * </p>
 *
 * @author dashu
 * @since 2019-07-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_3sale10_fission")
@ApiModel(value="ThreeSaleFission对象", description="十元三件红包裂变")
public class ThreeSaleFission extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "FISSION_ID", type = IdType.AUTO)
    private Integer fissionId;

    @ApiModelProperty(value = "分享人openid")
    @TableField("FROM_OPENID")
    private String fromOpenid;

    @ApiModelProperty(value = "分享人userid")
    @TableField("FROM_USERID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fromUserid;

    @ApiModelProperty(value = "分享人unionid")
    @TableField("FROM_UNIONID")
    private String fromUnionid;

    @ApiModelProperty(value = "分享人昵称")
    @TableField("FROM_NICK_NAME")
    private String fromNickName;

    @ApiModelProperty(value = "分享人头像")
    @TableField("FROM_HEAD_IMG")
    private String fromHeadImg;

    @ApiModelProperty(value = "分享人分享的订单")
    @TableField("FROM_ORDER_NO")
    private String fromOrderNo;

    @ApiModelProperty(value = "被分享人openid")
    @TableField("TO_OPENID")
    private String toOpenid;

    @ApiModelProperty(value = "被分享人userid")
    @TableField("TO_USERID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long toUserid;

    @ApiModelProperty(value = "被分享人unionid")
    @TableField("TO_UNIONID")
    private String toUnionid;

    @ApiModelProperty(value = "被分享人昵称")
    @TableField("TO_NICK_NAME")
    private String toNickName;

    @ApiModelProperty(value = "被分享人头像")
    @TableField("TO_HEAD_IMG")
    private String toHeadImg;

    @ApiModelProperty(value = "被分享人订单号")
    @TableField("TO_ORDER_NO")
    private String toOrderNo;

    @ApiModelProperty(value = "被分享人订单状态（0:已失效;1:未支付;2:已支付;3:已确认收货）")
    @TableField("TO_ORDER_STATUS")
    private String toOrderStatus;

    @ApiModelProperty(value = "支付时间")
    @TableField("TO_ODER_TIME")
    private Date toOderTime;

    @ApiModelProperty(value = "订单确认收货时间")
    @TableField("TO_ORDER_CONFIRM_TIME")
    private Date toOrderConfirmTime;

    @ApiModelProperty(value = "红包金额")
    @TableField("RED_MONEY")
    private Integer redMoney;

    @ApiModelProperty(value = "红包状态(1:未发送;2:已发送)")
    @TableField("RED_STATUS")
    private String redStatus;

    @ApiModelProperty(value = "领红包的时间")
    @TableField("RED_TIME")
    private Date redTime;

    @ApiModelProperty(value = "创建时间(该分享人订单创建时间)")
    @TableField("CREATE_TIME")
    private Date createTime;


}
