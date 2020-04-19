package com.mmj.active.coupon.model;

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
 * 优惠券关联商品表
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_coupon_good")
@ApiModel(value="CouponGood对象", description="优惠券关联商品表")
public class CouponGood extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "映射ID")
    @TableId(value = "MAPPER_ID", type = IdType.AUTO)
    private Integer mapperId;

    @ApiModelProperty(value = "范围类型 1：可用商品，表示指定商品可以使用；2：不可用商品")
    @TableField("SCOPE_TYPE")
    private String scopeType;

    @ApiModelProperty(value = "优惠券ID")
    @TableField("COUPON_ID")
    private Integer couponId;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "商品名称")
    @TableField("GOOD_NAME")
    private String goodName;

    @ApiModelProperty(value = "商品图片")
    @TableField("GOOD_IMAGE")
    private String goodImage;

    @ApiModelProperty(value = "商品SPU")
    @TableField("GOOD_SPU")
    private String goodSpu;


}
