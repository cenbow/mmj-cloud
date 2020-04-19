package com.mmj.order.constant;

public interface MMKingShareType {


    public String SIGN = "SIGN"; //签到

    public String BARGAIN = "BARGAIN";  //砍价

    public String WHEELS = "WHEELS";    //转盘

    public String LOTTERY = "LOTTERY";   //抽奖

    public interface OrderKingStatus {

        //正常
        public Integer NORMAL = 1;

        //冻结
        public Integer FROZEN = 0;

        //质检通过后删除
        public Integer DELETE = 2;
    }
}
