package com.mmj.user.manager.model;

import java.util.Date;
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

/**
 * <p>
 * 用户权限表
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_base_permission")
@ApiModel(value="BasePermission对象", description="用户权限表")
public class BasePermission extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "PER_ID", type = IdType.AUTO)
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
