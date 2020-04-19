package com.mmj.active.coupon.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @description: boss后台新增优惠券
 * @auther: KK
 * @date: 2019/6/27
 */
@Data
@ApiModel(value = "新增优惠券", description = "新增优惠券")
public class BossCouponAddVo {
    @NotNull
    @ApiModelProperty(value = "运营备注")
    private String maketingDesc;

    @NotNull
    @ApiModelProperty(value = "优惠券标题")
    private String couponTitle;

    @NotNull
    @ApiModelProperty(value = "优惠主体 1 商品金额; 2 订单金额 ; 3 运费金额")
    private Integer couponMain;

    @NotNull
    @ApiModelProperty(value = "条件类型 1 无限制 ; 2 满X元; 3 满X件  ")
    private String whereType;

    @NotNull
    @ApiModelProperty(value = "条件值")
    private Integer whereValue;

    @NotNull
    @ApiModelProperty(value = "优惠类型  1 减X元; 2 打X拆 ")
    private String couponAmount;

    @NotNull
    @ApiModelProperty(value = "优惠值")
    private Integer couponValue;

    @NotNull
    @ApiModelProperty(value = "使用范围  1：所有商品可用；2：部分商品可用；3：部分商品不可用；4：指定分类可用")
    private String couponScope;

    @ApiModelProperty(value = "关联商品分类")
    private List<BossCouponClassAddVo> classItems;

    @ApiModelProperty(value = "关联商品")
    private List<BossCouponGoodAddVo> goodItems;

    @NotNull
    @ApiModelProperty(value = "优惠说明")
    private String couponDesc;

    @NotNull
    @ApiModelProperty(value = "发放总量 -1为不限总量")
    private Integer countNum;

    @NotNull
    @ApiModelProperty(value = "每天发放量 -1为不限制")
    private Integer everyDayNum;

    @NotNull
    @ApiModelProperty(value = "适用价格 1：普通价格；2:拼团价；3：拼团价和普通价格都适用")
    private String applyPrice;

    @NotNull
    @ApiModelProperty(value = "有效期类型 1 时间区间 2 领取后生效")
    private String indateType;

    @ApiModelProperty(value = "有效期开始时间 格式：yyyy-MM-dd HH:mm:ss")
    private String couponStart;

    @ApiModelProperty(value = "有效期结束时间 格式：yyyy-MM-dd HH:mm:ss")
    private String couponEnd;

    @ApiModelProperty(value = "几天后生效")
    private Integer afterDay;

    @ApiModelProperty(value = "某日期前有效 格式：yyyy-MM-dd")
    private String afterDate;

    @ApiModelProperty(value = "多少时间内有效值")
    private Integer afterTime;

    @ApiModelProperty(value = "有效时间单位 DATE：表示日期（则afterDate有效）；HOURS：小时；MINUTES：分钟；DAYS：天")
    private String afterUnit;

    @NotNull
    @ApiModelProperty(value = "距离结束时间值（转分钟）")
    private Integer distanceTime;

    @NotNull
    @ApiModelProperty(value = "跳转类型 INDEX 首页; GOOD_CLASS 分类; SUBJECT 专题;  GOOD 指定单品")
    private String hrafType;

    @NotNull
    @ApiModelProperty(value = "跳转值")
    private String hrafArg;

    @NotNull
    @ApiModelProperty(value = "商详页是否展示")
    private Integer detailShow;

    @NotNull
    @ApiModelProperty(value = "活动标识 BUY_GIVE - 会员买多少送多少；MEMBER_DAY - 会员日领券")
    private String activeFlag;

    @NotNull
    @ApiModelProperty(value = "是否会员专用 0否 1是")
    private Integer memberFlag;
}
