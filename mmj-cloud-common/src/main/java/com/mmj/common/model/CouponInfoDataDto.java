package com.mmj.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 返回优惠券模板信息
 * @auther: KK
 * @date: 2019/7/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "优惠券模板信息", description = "优惠券模板信息")
public class CouponInfoDataDto {
    @ApiModelProperty(value = "优惠券ID")
    private Integer couponId;

    @ApiModelProperty(value = "优惠券标题")
    private String couponTitle;

    @ApiModelProperty(value = "优惠主体 1 商品金额 ; 3 运费金额; 2 订单金额")
    private Integer couponMain;

    @ApiModelProperty(value = "条件类型 1 无限制 ; 2 满X元; 3 满X件  ")
    private String whereType;

    @ApiModelProperty(value = "条件值")
    private String whereValue;

    @ApiModelProperty(value = "优惠类型  1 减X元; 2 打X拆 ")
    private String couponAmount;

    @ApiModelProperty(value = "优惠值")
    private String couponValue;

    @ApiModelProperty(value = "使用范围 - 1：所有商品可用；2：部分商品可用；3：部分商品不可用；4：指定分类可用")
    private String couponScope;

    @ApiModelProperty(value = "优惠说明")
    private String couponDesc;

    @ApiModelProperty(value = "活动标识 BUY_GIVE - 会员买多少送多少；MEMBER_DAY - 会员日领券")
    private String activeFlag;

    @ApiModelProperty(value = "跳转类型 INDEX 首页; GOOD_CLASS 分类; SUBJECT 专题;  GOOD 指定单品")
    private String hrafType;

    @ApiModelProperty(value = "跳转值")
    private String hrafArg;

    @ApiModelProperty(value = "是否会员专用")
    private Integer memberFlag;
}
