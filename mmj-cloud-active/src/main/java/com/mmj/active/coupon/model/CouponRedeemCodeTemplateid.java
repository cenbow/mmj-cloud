package com.mmj.active.coupon.model;

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
 * 
 * </p>
 *
 * @author zhangyicao
 * @since 2019-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_coupon_redeem_code_templateid")
@ApiModel(value="CouponRedeemCodeTemplateid对象", description="")
public class CouponRedeemCodeTemplateid extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "批次编码")
    private String batchCode;

    @ApiModelProperty(value = "coupon_template的主键")
    private Integer couponTemplateid;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


}
