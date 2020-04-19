package com.mmj.active.threeSaleTenner.constant;

/**
 * 十元三件列表
 */
public interface ThreeSaleFissionConstant {

    /**
     * 助力人(被分享人)订单状态
     */
    interface toOrderStatus{
        String CANCEL_PAY = "0";    // 取消付款(已失效 )
        String WAIT_PAY = "1";     // 待付款
        String FINISH_PAY = "2";    // 已付款
        String CONFIRM_GOOD = "3";  // 确定收货
    }

    /**
     * 分享人红包状态
     */
    interface redStatus{
         String PAST_DUE = "0";  //已经过期(取消订单, 红包过期)
         String WAIT_GET = "1";  //未发送
         String FINISH_GET = "2"; //已发送
    }
}
