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
 * 角色权限映射表
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_role_permission_mapper")
@ApiModel(value="BaseRolePermission对象", description="角色权限映射表")
public class BaseRolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "MAPPER_ID", type = IdType.AUTO)
    private Integer mapperId;

    @ApiModelProperty(value = "角色ID")
    @TableField("ROLE_ID")
    private Integer roleId;

    @ApiModelProperty(value = "权限ID")
    @TableField("PER_ID")
    private Integer perId;


}
