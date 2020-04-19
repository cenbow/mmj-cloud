package com.mmj.common.model.wx;

import lombok.Data;

/**
 * 退款成功时候的kafka通知
 */
@Data
public class RefundSuccess {

    private String outRefundNo; //商户退款单号

    private String outTradeNo; //商户订单号

    private String refundAccount; //退款资金来源 REFUND_SOURCE_RECHARGE_FUNDS:基本商户;REFUND_SOURCE_UNSETTLED_FUNDS:未结算资金退款

    private int refundFee; //申请退款金额

    private String refundId; //微信退款单号

    private String refundRecvAccout; //退款入账账户

    private String refundRequestSource; //退款发起来源  API:api接口退款;VENDOR_PLATFORM:商户平台退款

    private String refundStatus; //退款状态 SUCCESS:成功;CHANGE:异常;REFUNDCLOSE:关闭

    private int settlementRefundFee; //实际退款金额

    private int settlementTotalFee; //应结订单金额

    private String successTime; //退款成功时间

    private int totalFee; //订单金额

    private String transactionId; //微信订单号
}
