package com.mmj.order.constant;

public enum RedPackageType {

    LOTTERY(1,"抽奖"),
    JIELIGOU(2,"接力购"),
    JIELIGOU_LOTTERY(3,"接力购抽奖"),
    TEN_FOR_THREE(4,"十元三件"),
    FLASH_SELL(5,"秒杀"),
    COUPON(6,"优惠券"),
    CUT_PRICE(7,"砍价"),
    FREE_ORDER(8,"免费送");

    private int type;
    private String name;

    RedPackageType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static String getNameByType(int type){
        RedPackageType redPackageType [] = RedPackageType.values();
        for (RedPackageType redPackageType1 : redPackageType){
            if (redPackageType1.getType() == type){
                return redPackageType1.getName();
            }
        }
        return "";
    }
}
