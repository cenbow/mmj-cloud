package com.mmj.active.common.model;

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
 * 商品分类表
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_class")
@ApiModel(value="GoodClass对象", description="商品分类表")
public class GoodClass extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分类ID")
    @TableId(value = "CLASS_ID", type = IdType.AUTO)
    private Integer classId;

    @ApiModelProperty(value = "分类编码")
    @TableField("CLASS_CODE")
    private String classCode;

    @ApiModelProperty(value = "分类名称")
    @TableField("CLASS_NAME")
    private String className;

    @ApiModelProperty(value = "展示名称")
    @TableField("SHOW_NAME")
    private String showName;

    @ApiModelProperty(value = "图标")
    @TableField("SHOW_IMAGE")
    private String showImage;

    @ApiModelProperty(value = "是否显示")
    @TableField("SHOW_FLAG")
    private Integer showFlag;

    @ApiModelProperty(value = "分类排序")
    @TableField("CLASS_ORDER")
    private Integer classOrder;

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
