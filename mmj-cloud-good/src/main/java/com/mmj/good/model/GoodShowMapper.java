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
 * 商品展示关联表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_show_mapper")
@ApiModel(value="GoodShowMapper对象", description="商品展示关联表")
public class GoodShowMapper extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "映射ID")
    @TableId(value = "MAPPER_ID", type = IdType.AUTO)
    private Integer mapperId;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "数据字典类型")
    @TableField("DICT_TYPE")
    private String dictType;

    @ApiModelProperty(value = "数据字典编码")
    @TableField("DICT_CODE")
    private String dictCode;

    @ApiModelProperty(value = "数据字典值")
    @TableField("DICT_VALUE")
    private String dictValue;


}
