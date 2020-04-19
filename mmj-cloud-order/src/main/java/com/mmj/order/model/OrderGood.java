package com.mmj.order.model;

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
 * 订单商品表
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order_good")
@ApiModel(value = "OrderGood对象", description = "订单商品表")
public class OrderGood extends BaseModel {

    private static final long serialVersionUID = -5661798916692942285L;
    @ApiModelProperty(value = "关联ID")
    @TableId(value = "OG_ID", type = IdType.AUTO)
    private Integer ogId;

    @ApiModelProperty(value = "订单ID")
    @TableField("ORDER_ID")
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "包裹号")
    @TableField("PACKAGE_NO")
    private String packageNo;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "商品SPU")
    @TableField("GOOD_SPU")
    private String goodSpu;

    @ApiModelProperty(value = "销售ID")
    @TableField("SALE_ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品SKU")
    @TableField("GOOD_SKU")
    private String goodSku;

    @ApiModelProperty(value = "分类编码")
    @TableField("CLASS_CODE")
    private String classCode;

    @ApiModelProperty(value = "下单时仓库编码")
    @TableField("WAREHOUSE_ID")
    private String warehouseId;

    @ApiModelProperty(value = "商品名称")
    @TableField("GOOD_NAME")
    private String goodName;

    @ApiModelProperty(value = "商品数量")
    @TableField("GOOD_NUM")
    private Integer goodNum;

    @ApiModelProperty(value = "单价类型 0店铺价 1会员价")
    @TableField("PRICE_TYPE")
    private Integer priceType;

    @ApiModelProperty(value = "商品单价")
    @TableField("GOOD_PRICE")
    private Integer goodPrice;

    @ApiModelProperty(value = "商品原价")
    @TableField("GOOD_AMOUNT")
    private Integer goodAmount;

    @ApiModelProperty(value = "规格名称（拼接）")
    @TableField("MODEL_NAME")
    private String modelName;

    @ApiModelProperty(value = "是否组合商品")
    @TableField("COMBINA_FLAG")
    private Integer combinaFlag;

    @ApiModelProperty(value = "是否虚拟商品")
    @TableField("VIRTUAL_FLAG")
    private String virtualFlag;

    @ApiModelProperty(value = "虚拟商品类型 1:优惠券,2:买买金,3:话费,4:直冲话费")
    @TableField("VIRTUAL_TYPE")
    private Integer virtualType;

    @ApiModelProperty(value = "商品图片")
    @TableField("GOOD_IMAGE")
    private String goodImage;

    @ApiModelProperty(value = "活动优惠金额")
    @TableField("DISCOUNT_AMOUNT")
    private Integer discountAmount;

    @ApiModelProperty(value = "优惠券优惠金额")
    @TableField("COUPON_AMOUNT")
    private Integer couponAmount;

    @ApiModelProperty(value = "快照")
    @TableField("SNAPSHOT_DATA")
    private String snapshotData;

    @ApiModelProperty(value = "买买金兑换金额")
    @TableField("GOLD_PRICE")
    private Integer goldPrice;

    @ApiModelProperty(value = "会员价")
    @TableField("MEMBER_PRICE")
    private Integer memberPrice;

    @ApiModelProperty(value = "快递费用")
    @TableField("LOGISTICS_AMOUNT")
    private Integer logisticsAmount;

    @ApiModelProperty(value = "删除标识")
    @TableField("DEL_FLAG")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
