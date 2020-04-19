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
 * 资源表
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_base_resource")
@ApiModel(value="BaseResource对象", description="资源表")
public class BaseResource extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "资源ID")
    @TableId(value = "RES_ID", type = IdType.AUTO)
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
