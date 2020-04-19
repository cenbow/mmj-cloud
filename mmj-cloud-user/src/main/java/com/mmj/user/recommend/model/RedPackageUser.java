package com.mmj.user.recommend.model;

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
 * 用户红包表
 * </p>
 *
 * @author dashu
 * @since 2019-06-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_redpackage_user")
@ApiModel(value="RedPackageUser对象", description="用户红包表")
public class RedPackageUser extends BaseModel {

    private static final long serialVersionUID = -1206658000277170554L;

    @ApiModelProperty(value = "红包ID")
    @TableId(value = "PACKAGE_ID", type = IdType.AUTO)
    private Integer packageId;

    @ApiModelProperty(value = "用户名ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "用户OPEN_ID")
    @TableField("OPEN_ID")
    private String openId;

    @ApiModelProperty(value = "用户UNION_ID")
    @TableField("UNION_ID")
    private String unionId;

    @ApiModelProperty(value = "用户电话")
    @TableField("USER_MOBILE")
    private String userMobile;

    @ApiModelProperty(value = "活动类型 1 抽奖 2 接力购 3 接力购抽奖 4十元三件 5 秒杀  6 优惠券 7 砍价 8免费送")
    @TableField("ACTIVE_TYPE")
    private Integer activeType;

    @ApiModelProperty(value = "关联ID")
    @TableField("BUSINESS_ID")
    private Integer businessId;

    @ApiModelProperty(value = "红包来源")
    @TableField("PACKAGE_SOURCE")
    private String packageSource;

    @ApiModelProperty(value = "红包码")
    @TableField("PACKAGE_CODE")
    private String packageCode;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "红包金额")
    @TableField("PACKAGE_AMOUNT")
    private Integer packageAmount;

    @ApiModelProperty(value = "红包状态")
    @TableField("PACKAGE_STATUS")
    private Integer packageStatus;

    @ApiModelProperty(value = "到帐时间")
    @TableField("ACCOUNT_TIME")
    private Date accountTime;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    @TableField("MODIFY_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;


}
