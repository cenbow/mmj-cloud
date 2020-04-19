package com.mmj.user.manager.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 会员优惠券
 * @auther: KK
 * @date: 2019/7/11
 */
@Data
@ApiModel(value = "会员优惠券", description = "会员优惠券")
public class MemberCouponDto {
    @ApiModelProperty(value = "是否会员日 true是 false否")
    private Boolean memberDay;
    @ApiModelProperty(value = "会员日可领的总共金额，单位：元")
    private BigDecimal totalCouponMoney;
    @ApiModelProperty(value = "距离下个会员日的毫秒数，前端需根据此值转换成距离下个会员日还有XX天XX小时XX分XX秒，倒计时结束后需要重新调用当前接口拉取最新数据，展示可领取多少，以及领取的按钮")
    private Long nextMemberDayIntervalMilliseconds;
    @ApiModelProperty(value = "优惠券信息")
    private List<CouponStat> couponInfoList;

    @Data
    @ApiModel(value = "用户优惠券信息")
    public static class CouponStat {
        @ApiModelProperty(value = "是否已领取，true：是；false：否")
        private Boolean hasCollected;

        @ApiModelProperty(value = "已领取的百分比")
        private String percent;

        @ApiModelProperty(value = "已领取的文字描述")
        private String percentStr;

        @ApiModelProperty(value = "优惠券信息")
        private CouponInfoDataDto couponInfo;
    }

}
