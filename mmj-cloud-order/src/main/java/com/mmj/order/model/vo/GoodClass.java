package com.mmj.order.model.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class GoodClass {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "分类ID")

    private Integer classId;

    @ApiModelProperty(value = "分类编码")
    private String classCode;

    @ApiModelProperty(value = "分类名称")
    private String className;

    @ApiModelProperty(value = "展示名称")
    private String showName;

    @ApiModelProperty(value = "图标")
    private String showImage;

    @ApiModelProperty(value = "是否显示")
    private Integer showFlag;

    @ApiModelProperty(value = "分类排序")
    private Integer classOrder;

    @ApiModelProperty(value = "是否删除")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;

}
