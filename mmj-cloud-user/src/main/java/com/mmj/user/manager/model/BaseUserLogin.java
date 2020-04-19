package com.mmj.user.manager.model;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 登陆关联表
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_login")
@ApiModel(value="BaseUserLogin对象", description="登陆关联表")
public class BaseUserLogin implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "LOGIN_ID", type = IdType.AUTO)
    private Integer loginId;

    @ApiModelProperty(value = "登陆名")
    @TableField("USER_NAME")
    private String userName;

    @ApiModelProperty(value = "登陆类型(NAME_PWDSMSWECHAT)")
    @TableField("LOGIN_TYPE")
    private String loginType;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    private Integer userId;


}
