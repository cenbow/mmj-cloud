package com.mmj.user.manager.vo;

import java.util.Date;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

@Data
@ApiModel(value="RoleResourceDetail对象", description="角色包含的资源详细信息")
public class RoleResourceDetail {
	
	@ApiModelProperty(value = "主键")
    @TableId(value = "MAPPER_ID", type = IdType.AUTO)
    private Integer mapperId;

    @ApiModelProperty(value = "角色ID")
    @TableField("ROLE_ID")
    private Integer roleId;

    @ApiModelProperty(value = "资源ID")
    @TableField("RES_ID")
    private Integer resId;
    
    @ApiModelProperty(value = "父ID")
    @TableField("PARENT_ID")
    private Integer parentId;

    @ApiModelProperty(value = "资源名称")
    @TableField("RES_NAME")
    private String resName;

    @ApiModelProperty(value = "资源类型（1：目录 2：菜单 3：按扭）")
    @TableField("RES_TYPE")
    private Integer resType;

    @ApiModelProperty(value = "资源URL")
    @TableField("RES_URL")
    private String resUrl;

    @ApiModelProperty(value = "资源图片")
    @TableField("RES_IMAGE")
    private String resImage;
    
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

    @ApiModelProperty(value = "资源模块")
    @TableField("RES_MODEL")
    private String resModel;

    @ApiModelProperty(value = "是否显示")
    @TableField("IS_SHOW")
    private Integer isShow;

}
