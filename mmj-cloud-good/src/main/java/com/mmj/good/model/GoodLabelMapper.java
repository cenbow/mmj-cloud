package com.mmj.good.model;

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
 * 商品标签映射表
 * </p>
 *
 * @author lyf
 * @since 2019-06-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_label_mapper")
@ApiModel(value="GoodLabelMapper对象", description="商品标签映射表")
public class GoodLabelMapper extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "映射ID")
    @TableId(value = "MAPPER_ID", type = IdType.AUTO)
    private Integer mapperId;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "标签ID")
    @TableField("LABEL_ID")
    private Integer labelId;


}
