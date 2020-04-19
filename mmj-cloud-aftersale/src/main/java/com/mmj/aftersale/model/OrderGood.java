package com.mmj.aftersale.model;

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
public class OrderGood extends BaseModel {

    private static final long serialVersionUID = -5661798916692942285L;
    @ApiModelProperty(value = "关联ID")
    private Integer ogId;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "包裹号")
    private String packageNo;

    @ApiModelProperty(value = "商品ID")
    private Integer goodId;

    @ApiModelProperty(value = "销售ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品SKU")
    private String goodSku;

    @ApiModelProperty(value = "商品名称")
    private String goodName;

    @ApiModelProperty(value = "商品数量")
    private Integer goodNum;

    @ApiModelProperty(value = "商品单价")
    private Integer goodPrice;

    @ApiModelProperty(value = "商品原价")
    private Integer goodAmount;

    @ApiModelProperty(value = "规格名称（拼接）")
    private String modelName;

    @ApiModelProperty(value = "是否虚拟商品")
    private String virtualFlag;

    @ApiModelProperty(value = "虚拟商品类型 1:优惠券,2:买买金,3:话费")
    private Integer virtualType;

    @ApiModelProperty(value = "商品图片")
    private String goodImage;

    @ApiModelProperty(value = "活动优惠金额")
    private Integer discountAmount;

    @ApiModelProperty(value = "优惠券优惠金额")
    private Integer couponAmount;

    @ApiModelProperty(value = "快照")
    private String snapshotData;

    @ApiModelProperty(value = "买买金兑换金额")
    private Integer goldPrice;

    @ApiModelProperty(value = "会员价")
    private Integer memberPrice;

    @ApiModelProperty(value = "快递费用")
    private Integer logisticsAmount;

    @ApiModelProperty(value = "删除标识")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    private Date createrTime;


}
