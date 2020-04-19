package com.mmj.pay.common.model.vo;


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
@ApiModel(value="CouponClass对象", description="优惠券关联商品分类表")
public class CouponClass extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "映射ID")
    private Integer mapperId;

    @ApiModelProperty(value = "优惠券ID")
    private Integer couponId;

    @ApiModelProperty(value = "分类ID")
    private Integer classId;

    @ApiModelProperty(value = "商品分类编码")
    private String goodClass;

    @ApiModelProperty(value = "分类名称")
    private String className;


}
