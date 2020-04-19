package com.mmj.order.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 下单请求
 * 2 * @Author: pengwenhao
 * 3 * @Date: 2019/6/4 10:41
 * 4
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderSaveVo {
    /**
     * 是否会员下单
     */
    @NotNull
    private Boolean memberOrder;
    /**
     * 订单类型
     */
    @NotNull
    private Integer orderType;
    /**
     * 订单金额
     */
    @NotNull
    private String orderAmount;
    /**
     * 传递数据
     */
    private String passingData;

    /**
     * 下单来源 0 购物车 1 立即下单
     */
    private Integer type;
    /**
     * 关联id（活动id）
     */
    private Integer businessId;
    /**
     * 优惠券编码
     */
    private String couponCode;

    /**
     * 是否选择使用买买金
     */
    private Boolean kingSelected = false;

    /**
     * 使用买买金的个数
     */
    private Integer useKingNum;

    /**
     * 买买金兑换的金额/抵扣的金额
     */
    private String exchangeMoney;

    /**
     * 订单来源 MIN(小程序) MH5(站内H5) H5(站外h5)
     */
    private String source;
    /**
     * appId 下单平台标识
     */
    private String appId;
    /**
     * 微信用户标识
     */
    private String openId;
    /**
     * 订单渠道
     */
    private String channel;

    @NotNull
    private List<OrderGoodsVo> good;

    @NonNull
    private ConsignessVo consigness;   // 收件人信息

    private String dk;
}
