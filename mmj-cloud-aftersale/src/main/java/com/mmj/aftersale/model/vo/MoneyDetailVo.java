package com.mmj.aftersale.model.vo;


import com.mmj.aftersale.model.dto.CouponDto;

/**
 * 2 * @Author: zhangyicao
 * 3 * @Date: 2019/06/17
 * 4
 */
public class MoneyDetailVo {

    private String orderNo;

    private String orderAmount; // 订单金额

    private String goodsAmount;  // 商品金额

    private String couponAmount;  // 优惠金额

    private String freight;  // 运费

    private String resultAmount;  // 返现金额

    private String payAmount;  // 支付金额

    private String shareAmout;   // 分享金额

    private Integer oweKingNum;  //降级时欠的买买金数量

    private String canRefundAmount; // 可退金额

    private CouponDto couponDto;

    private RefOtherOrderVo refOtherOrderVo;    // 关联订单


    public MoneyDetailVo() {

    }

    public RefOtherOrderVo getRefOtherOrderVo() {
        return refOtherOrderVo;
    }

    public void setRefOtherOrderVo(RefOtherOrderVo refOtherOrderVo) {
        this.refOtherOrderVo = refOtherOrderVo;
    }



    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderAmount() {
        return orderAmount;
    }


    public String getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(String goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public String getCouponAmount() {
        return couponAmount;
    }

    public void setCouponAmount(String couponAmount) {
        this.couponAmount = couponAmount;
    }

    public String getFreight() {
        return freight;
    }

    public void setFreight(String freight) {
        this.freight = freight;
    }

    public String getResultAmount() {
        return resultAmount;
    }

    public void setResultAmount(String resultAmount) {
        this.resultAmount = resultAmount;
    }

    public String getShareAmout() {
        return shareAmout;
    }

    public void setShareAmout(String shareAmout) {
        this.shareAmout = shareAmout;
    }

    public String getPayAmount() {
        return payAmount;
    }


    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public Integer getOweKingNum() {
        return oweKingNum;
    }

    public void setOweKingNum(Integer oweKingNum) {
        this.oweKingNum = oweKingNum;
    }

    public String getCanRefundAmount() {
        return canRefundAmount;
    }

    public void setCanRefundAmount(String canRefundAmount) {
        this.canRefundAmount = canRefundAmount;
    }

    public CouponDto getCouponDto() {
        return couponDto;
    }

    public void setCouponDto(CouponDto couponDto) {
        this.couponDto = couponDto;
    }
}
