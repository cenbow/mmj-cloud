package com.mmj.user.recommend.constants;

public interface UserShardConstants {

    interface UserShardType {
         String RECOMMEND = "RECOMMEND"; // 推荐
         String MEMBER = "MEMBER"; // 分享成为会员
         String FREE_ORDER = "FREE_ORDER"; // 免费送
    }

    interface Other {
        //红包来源
        interface packageSource{
             String RECOMMEND = "RECOMMEND"; // 推荐
             String MEMBER = "MEMBER"; // 分享成为会员
        }

        interface orderStatus{
             Integer CANCEL_PAY = 0; // 取消付款
             Integer WAIT_PAY = 1; // 待付款
             Integer FINISH_PAY = 2; // 已付款
             Integer CONFIRM_GOOD = 3; // 确定收货
             Integer REFUND_GOOD = 4; // 退款
        }

        interface packageStatus{
             Integer WAIT_GET = 0;  //未领取
             Integer FINISH_GET = 1; //领取成功
             Integer PAST_DUE = 2; //过期（取消付款）
        }
    }

}
