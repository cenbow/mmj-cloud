package com.mmj.order.common.model.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class GoodWarehouse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "库存ID")
    private Integer warehouseId;

    @ApiModelProperty(value = "商品ID")
    private Integer goodId;

    @ApiModelProperty(value = "销售ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品SKU")
    private String goodSku;

    @ApiModelProperty(value = "库存名称")
    private String warehouseName;

    @ApiModelProperty(value = "库存数据")
    private Integer warehouseNum;

    private Integer createrId;

    private Date createrTime;

}
