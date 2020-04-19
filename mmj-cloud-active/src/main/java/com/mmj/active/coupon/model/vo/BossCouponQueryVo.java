package com.mmj.active.coupon.model.vo;

import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 优惠券查询
 * @auther: KK
 * @date: 2019/7/2
 */
@Data
@ApiModel(value = "优惠券列表查询条件", description = "优惠券列表查询条件")
public class BossCouponQueryVo extends BaseModel {
    @ApiModelProperty(value = "优惠券标题")
    private String couponTitle;

    @ApiModelProperty(value = "运营备注")
    private String maketingDesc;

    @ApiModelProperty(value = "有效期开始时间 格式：yyyy-MM-dd HH:mm:ss")
    private String couponStart;

    @ApiModelProperty(value = "有效期结束时间 格式：yyyy-MM-dd HH:mm:ss")
    private String couponEnd;

    @ApiModelProperty(value = "适用价格 1：普通价格；2:拼团价；3：拼团价和普通价格都适用")
    private String applyPrice;

    @ApiModelProperty(value = "优惠主体 1 商品金额 ; 3 运费金额; 2 订单金额")
    private Integer couponMain;

    @ApiModelProperty(value = "删除标识 0正常 1逻辑删除")
    private Integer delFlag;
}
