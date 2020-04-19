package com.mmj.good.stock.model;

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
 * 库存记录表
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_stock")
@ApiModel(value="GoodStock对象", description="库存记录表")
public class GoodStock extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "STOCK_ID", type = IdType.AUTO)
    private Integer stockId;

    @ApiModelProperty(value = "SKU编码")
    @TableField("GOOD_SKU")
    private String goodSku;

    @ApiModelProperty(value = "库存(占用、扣减为负数，释放、回退为正数)")
    @TableField("GOOD_NUM")
    private Integer goodNum;

    @ApiModelProperty(value = "状态(1：占用 2：扣减 3：释放 4:回退 5：过期)")
    @TableField("STATUS")
    private Integer status;

    @ApiModelProperty(value = "业务id")
    @TableField("BUSINESS_ID")
    private String businessId;

    @ApiModelProperty(value = "业务类型(定义规则：模块_类型[order_4])")
    @TableField("BUSINESS_TYPE")
    private String businessType;

    @ApiModelProperty(value = "过期时间")
    @TableField("EXPIRE_TIME")
    private Date expireTime;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
