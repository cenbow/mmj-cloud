package com.mmj.active.coupon.model.dto;

import com.mmj.active.coupon.model.vo.BossCouponClassAddVo;
import com.mmj.active.coupon.model.vo.BossCouponGoodAddVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description: boss优惠券列表信息
 * @auther: KK
 * @date: 2019/6/27
 */
@Data
public class BossCouponDto {
    @ApiModelProperty(value = "优惠券ID")
    private Integer couponId;

    @ApiModelProperty(value = "运营备注")
    private String maketingDesc;

    @ApiModelProperty(value = "优惠券标题")
    private String couponTitle;

    @ApiModelProperty(value = "优惠主体 1 商品金额; 2 订单金额 ; 3 运费金额")
    private Integer couponMain;

    @ApiModelProperty(value = "条件类型 1 无限制 ; 2 满X元; 3 满X件  ")
    private String whereType;

    @ApiModelProperty(value = "条件值")
    private Integer whereValue;

    @ApiModelProperty(value = "优惠类型  1 减X元; 2 打X拆 ")
    private String couponAmount;

    @ApiModelProperty(value = "优惠值")
    private Integer couponValue;

    @ApiModelProperty(value = "使用范围  1：所有商品可用；2：部分商品可用；3：部分商品不可用；4：指定分类可用")
    private String couponScope;

    @ApiModelProperty(value = "关联商品分类")
    private List<BossCouponClassAddVo> classItems;

    @ApiModelProperty(value = "关联商品")
    private List<BossCouponGoodAddVo> goodItems;

    @ApiModelProperty(value = "优惠说明")
    private String couponDesc;

    @ApiModelProperty(value = "发放总量 -1为不限总量")
    private Integer countNum;

    @ApiModelProperty(value = "每天发放量 -1为不限制")
    private Integer everyDayNum;

    @ApiModelProperty(value = "已发放总数量")
    private Integer totalSendNumber;

    @ApiModelProperty(value = "当天发放量")
    private Integer toDaySendNumber;

    @ApiModelProperty(value = "适用价格 1：普通价格；2:拼团价；3：拼团价和普通价格都适用")
    private String applyPrice;

    @ApiModelProperty(value = "有效期类型 1 时间区间 2 领取后生效")
    private String indateType;

    @ApiModelProperty(value = "有效期开始时间 格式：yyyy-MM-dd HH:mm:ss")
    private Date couponStart;

    @ApiModelProperty(value = "有效期结束时间 格式：yyyy-MM-dd HH:mm:ss")
    private Date couponEnd;

    @ApiModelProperty(value = "几天后生效")
    private Integer afterDay;

    @ApiModelProperty(value = "某日期前有效 格式：yyyy-MM-dd")
    private Date afterDate;

    @ApiModelProperty(value = "多少时间内有效值")
    private Integer afterTime;

    @ApiModelProperty(value = "有效时间单位 DATE：表示日期（则afterDate有效）；HOURS：小时；MINUTES：分钟；DAYS：天")
    private String afterUnit;

    @ApiModelProperty(value = "距离结束时间值（转分钟）")
    private Integer distanceTime;

    @ApiModelProperty(value = "跳转类型 INDEX 首页; GOOD_CLASS 分类; SUBJECT 专题;  GOOD 指定单品")
    private String hrafType;

    @ApiModelProperty(value = "跳转值")
    private String hrafArg;

    @ApiModelProperty(value = "商详页是否展示")
    private Integer detailShow;

    @ApiModelProperty(value = "活动标识 BUY_GIVE - 会员买多少送多少；MEMBER_DAY - 会员日领券")
    private String activeFlag;

    @ApiModelProperty(value = "是否会员专用 0否 1是")
    private Integer memberFlag;
}
