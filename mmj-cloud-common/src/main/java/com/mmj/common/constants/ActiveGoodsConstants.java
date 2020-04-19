package com.mmj.common.constants;

public interface ActiveGoodsConstants {

    //sku库存
    String SKU_STOCK = "GOOD:SALE:SKU_STOCK:";
    //sku 销量
    String SKU_SALE = "GOOD:SALE:SKU_SALE:";
    //组合商品 sku
    String SKU_STOCK_COMBINE = "GOOD:SALE:SKU_STOCK:COMBINE:";

    interface ActiveType {

        int GROUP_LOTTERY = 1;  //普通抽奖

        int GROUP_JIELIGOU = 2;  //接力购

        int GROUP_RELAY_LOTTERY = 3;  //接力购抽奖

        int TEN_YUAN_THREE_PIECES = 4;  //十元三件

        int SECKILL = 5;      //秒杀

        int CUT = 7; //砍价

        int TOPIC = 8; //专题

        int GUESS_LIKE = 9;     //9 猜你喜欢

        int HOT_SALE = 10;      // 10 免邮热卖

        int CLASS_GOOD = 11;    // 11 分类商品

        int TUAN = 12;          //拼团

        int FREE_ORDER = 13;    //免费送

        int PRIZEWHEELS_6 = 14; //转盘6个十元店商品

        int VIRTUAL_GOOD = 15; //虚拟商品

        int SHOP_GOOD = 16; //店铺商品

        int MMJ_GOOD = 17; //买买金兑换商品

        int SEARCH_GOOD = 18; //商品搜索销量前十商品
    }

    interface GroupStatus {
        //0进行中 1已完成 2已过期 3已取消(拼主取消订单) 4已结束(活动已结束)
        int JOINING = 0;
        int COMPLETED = 1;
        int EXPIRE = 2;
        int CANCELLED = 3;
        int END = 4;
    }

    interface ActiveOrder {
        //活动类型
        Integer ACTIVE_TYPE_LOTTERY = 1;  //1 抽奖
        Integer ACTIVE_TYPE_RELAY = 2;  //2 接力购
        Integer ACTIVE_TYPE_RELAY_LOTTERY = 3;  //3 接力购抽奖
        Integer ACTIVE_TYPE_TEN_YUAN = 4;  //4 十元三件
        Integer ACTIVE_TYPE_SECKILL = 5;  //5 秒杀
        Integer ACTIVE_TYPE_COUPON = 6;  //6 优惠券
        Integer ACTIVE_TYPE_BARGIN = 7;  //7 砍价
        Integer ACTIVE_TYPE_TOPIC = 8;  //8 主题
        Integer ACTIVE_TYPE_GUESS = 9;  //9 猜你喜欢
        Integer ACTIVE_TYPE_HOT = 10;    //10 免邮热卖

    }

    interface FilterRule{
        //排序类型
        String FILTER_RULE_SALE = "SALE";//按销量
        String FILTER_RULE_WAREHOUSE = "WAREHOUSE";//按库存
        String FILTER_RULE_CREATER = "CREATER";//按创建时间
        String FILTER_RULE_MODIFY = "MODIFY";//按编辑时间
        String FILTER_RULE_THIRD = "THIRD";//按三级分类
    }

    interface VirtualGoodType{
        //虚拟商品类型：1 优惠券 2 买买金 3 话费
        Integer COUPON = 1;
        Integer MMJ = 2;
        Integer HF = 3;
    }

    /**
     * 商品状态
     * -1：删除 0：暂不发布 1：立即上架 2：自动上架 3：上架失败
     */
    interface goodStatus{
        String DELETED = "-1";
        String WAIT_ON = "0";
        String PUT_ON = "1";
        String PUT_ON_AUTO = "2";
        String PUT_NO_FAIL = "3";
    }

    interface goodSearch{
        //商品搜索 编辑商品 缓存
        String GOOD_SEARCH_TOP = "GOOD:SEARCH:TOP:";
        //商品搜索 编辑商品 缓存
        String GOOD_SEARCH_TOP_ORDER = "GOOD:SEARCH:TOP:ORDER:";
    }

}
