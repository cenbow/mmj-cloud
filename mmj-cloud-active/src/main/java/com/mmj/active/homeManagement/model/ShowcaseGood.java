package com.mmj.active.homeManagement.model;

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
 * 橱窗商品关联表
 * </p>
 *
 * @author dashu
 * @since 2019-06-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_showcase_good")
@ApiModel(value="ShowcaseGood对象", description="橱窗商品关联表")
public class ShowcaseGood extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关联ID")
    @TableId(value = "MAPPER_ID", type = IdType.AUTO)
    private Integer mapperId;

    @ApiModelProperty(value = "橱窗ID")
    @TableField("SHOWCASE_ID")
    private Integer showcaseId;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "排序")
    @TableField("ORDER_ID")
    private Integer orderId;

    @ApiModelProperty(value = "是否展示标签")
    @TableField("SHOW_FLAG")
    private Integer showFlag;

    @ApiModelProperty(value = "商品名称")
    @TableField("GOOD_NAME")
    private String goodName;

    @ApiModelProperty(value = "商品图片")
    @TableField("GOOD_IMAGE")
    private String goodImage;

    @ApiModelProperty(value = "商品排序")
    @TableField("GOOD_ORDER")
    private String goodOrder;

    @ApiModelProperty(value = "商品SPU")
    @TableField("GOOD_SPU")
    private String goodSpu;


}
