package com.mmj.pay.model;

public class WxpayOrderEx extends WxpayOrder {

    private String payType; //支付方式

    //支付端
    public static enum paySource{
        MIN, //小程序
        H5, //站外h5
        MH5, //公众号
        APP  //app端
    }

    //交易状态
    public static enum tradeStatus{
        order, //未支付
        pay    //已支付
    }

    //交易类型
    public static enum tradeType{
        JSAPI,  //JSAPI支付（或小程序支付）
        NATIVE,  //Native支付
        APP,     //app支付
        MWEB,   //H5支付
        MICROPAY  //付款码支付
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
