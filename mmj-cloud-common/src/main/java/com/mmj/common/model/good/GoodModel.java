package com.mmj.common.model.good;

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
 * 商品规格表
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_model")
@ApiModel(value="GoodModel对象", description="商品规格表")
public class GoodModel extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "规格ID")
    @TableId(value = "MODEL_ID", type = IdType.AUTO)
    private Integer modelId;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "销售ID")
    @TableField("SALE_ID")
    private Integer saleId;

    @ApiModelProperty(value = "规格名")
    @TableField("MODEL_NAME")
    private String modelName;

    @ApiModelProperty(value = "规格值")
    @TableField("MODEL_VALUE")
    private String modelValue;

    @ApiModelProperty(value = "规格排序")
    @TableField("MODEL_ORDER")
    private Integer modelOrder;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
