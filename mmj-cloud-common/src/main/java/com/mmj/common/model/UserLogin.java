package com.mmj.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
 * 登陆关联表
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user_login")
@ApiModel(value="UserLogin对象", description="登陆关联表")
public class UserLogin extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "LOGIN_ID", type = IdType.AUTO)
    private Integer loginId;

    @ApiModelProperty(value = "登陆名")
    @TableField("USER_NAME")
    private String userName;

    @ApiModelProperty(value = "登陆类型")
    @TableField("LOGIN_TYPE")
    private String loginType;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "应用ID")
    @TableField("APP_ID")
    private String appId;

}
