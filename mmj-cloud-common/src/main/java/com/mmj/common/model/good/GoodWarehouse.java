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
 * 商品库存表
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_warehouse")
@ApiModel(value="GoodWarehouse对象", description="商品库存表")
public class GoodWarehouse extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "库存ID")
    @TableId(value = "WAREHOUSE_ID", type = IdType.AUTO)
    private Integer warehouseId;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "销售ID")
    @TableField("SALE_ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品SKU")
    @TableField("GOOD_SKU")
    private String goodSku;

    @ApiModelProperty(value = "库存名称")
    @TableField("WAREHOUSE_NAME")
    private String warehouseName;

    @ApiModelProperty(value = "库存数据")
    @TableField("WAREHOUSE_NUM")
    private Integer warehouseNum;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
