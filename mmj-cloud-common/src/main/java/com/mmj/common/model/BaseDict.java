package com.mmj.common.model;

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
 * 数据字典表
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_base_dict")
@ApiModel(value="BaseDict对象", description="数据字典表")
public class BaseDict extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "字典ID")
    @TableId(value = "DICT_ID", type = IdType.AUTO)
    private Integer dictId;

    @ApiModelProperty(value = "父ID")
    @TableField("PARENT_ID")
    private Integer parentId;

    @ApiModelProperty(value = "类型")
    @TableField("DICT_TYPE")
    private String dictType;

    @ApiModelProperty(value = "编码")
    @TableField("DICT_CODE")
    private String dictCode;

    @ApiModelProperty(value = "字典值")
    @TableField("DICT_VALUE")
    private String dictValue;

    @ApiModelProperty(value = "是否删除")
    @TableField("DEL_FLAG")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    @TableField("MODIFY_ID")
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;


}
