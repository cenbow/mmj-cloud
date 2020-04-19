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
 * 优惠券关联商品分类表
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_coupon_class")
@ApiModel(value="CouponClass对象", description="优惠券关联商品分类表")
public class CouponClass extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "映射ID")
    @TableId(value = "MAPPER_ID", type = IdType.AUTO)
    private Integer mapperId;

    @ApiModelProperty(value = "优惠券ID")
    @TableField("COUPON_ID")
    private Integer couponId;

    @ApiModelProperty(value = "分类ID")
    @TableField("CLASS_ID")
    private Integer classId;

    @ApiModelProperty(value = "商品分类编码")
    @TableField("GOOD_CLASS")
    private String goodClass;

    @ApiModelProperty(value = "分类名称")
    @TableField("CLASS_NAME")
    private String className;


}
