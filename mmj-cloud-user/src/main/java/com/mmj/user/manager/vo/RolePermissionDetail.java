package com.mmj.user.manager.vo;

import java.util.Date;



import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

@Data
@ApiModel(value="RolePermissionDetail对象", description="角色包含的权限详细信息")
public class RolePermissionDetail {
	
	@ApiModelProperty(value = "主键")
    @TableId(value = "MAPPER_ID", type = IdType.AUTO)
    private Integer mapperId;

    @ApiModelProperty(value = "角色ID")
    @TableField("ROLE_ID")
    private Integer roleId;

    @ApiModelProperty(value = "权限ID")
    @TableField("PER_ID")
    private Integer perId;
    
    @ApiModelProperty(value = "父节点")
    @TableField("PARENT_ID")
    private Integer parentId;

    @ApiModelProperty(value = "权限名称")
    @TableField("PER_NAME")
    private String perName;

    @ApiModelProperty(value = "权限表达式")
    @TableField("PER_PATTERN")
    private String perPattern;
    
    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Integer createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    @TableField("MODIFY_ID")
    private Integer modifyId;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;


}
