package com.mmj.user.member.constant;

public interface MemberKingConstant {

    public interface ShareType {

        public String SHARE_GOODS = "SHARE_GOODS"; //分享商品

        public String SIGN = "SIGN"; //签到

        public String BARGAIN = "BARGAIN";  //砍价

        public String WHEELS = "WHEELS";    //转盘

        public String LOTTERY = "LOTTERY";   //抽奖

        public String ORDER = "ORDER";      //订单

        public String GIVE_BUY = "GIVE_BUY";  //买送活动

        public String EXCHANGE = "EXCHANGE"; //兑换商品

        public String EXCHANGE_COUPON = "EXCHANGE_COUPON"; //兑换优惠券商品

        public String REFUND = "REFUND";        //兑换商品取消

        public String RECOMMEND = "RECOMMEND";        //兑换商品
    }

    public interface OrderKingStatus {

        //正常
        public Integer NORMAL = 1;

        //冻结
        public Integer FROZEN = 0;

        //质检通过后删除
        public Integer DELETE = 2;
    }

    public interface KingLogSort {

        //分享等活动获得
        public Integer ACTIVE_GET = 0;

        //订单获得
        public Integer ORDER_GET = 1;

        //买送活动获得
        public Integer GIVE_BUY_GET = 2;
    }
}
