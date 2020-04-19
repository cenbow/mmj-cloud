package com.mmj.common.constants;


/**
 * 全局通用工具类
 */
public interface CommonConstant {
	
	String UTF_8 = "UTF-8";

    String REPLACE_INDEX_0 = "{0}";

    String REPLACE_INDEX_1 = "{1}";

    String REPLACE_INDEX_2 = "{2}";

    String OPENID = "openid";

    String USERID = "userid";

    /**
     * 满3件免运费
     */
    Integer FREIGHT_COUNT_LIMIT = 3;

    /**
     * 垮端支付时订单号的后缀
     */
    String orderSuffix = "_wx";

    /**
     * 满30元免邮
     */
    Double FREIGHT_TOTAL_PRICE_LIMIT = 30.0;

    Double FREIGHT_PRICE = 10.0d;

    Integer[] FREIGHT_FREE_ORDERTYPE = {OrderType.ORDINARY, OrderType.TWO_GROUP, OrderType.NEWCOMERS, OrderType.BARGAIN, OrderType.TEN_FOR_THREE_PIECE, OrderType.LOTTERY, OrderType.RELAY_LOTTERY, OrderType.FREE_ORDER, OrderType.SPIKE};

    /**
     * 常用符号
     *
     * @author shenfuding
     */
    interface Symbol {

        String COMMA = ",";

        String ENUMERATION_COMMA = "、";

        String PERIOD = ".";

        String COLON = ":";

        String SEMICOLON = ";";

        String EXCLAMATION = "!";

        String QUESTION_MARK = "?";

        String UNDERLINE = "_";

        String SPRIT = "/";
    }

    String LOTTERY_CACHE_KEY = "LOTTERY_CACHE_KEY:";  //存储抽奖活动信息
    long LOTTERY_CACHE_TIME = 168L;  //存一个星期
    String LOTTERY_JOIN_COUNT_PREFIX = "LOTTERY_JOIN_COUNT:";  //抽奖下单人数
    String LOTTERY_PAY_COUNT_PREFIX = "LOTTERY_PAY_COUNT:";  //抽奖支付人数
    String LOTTERY_CODE_COUNT_PREFIX = "LOTTERY_CODE_COUNT:"; //已生成抽奖码数量

    //占用库存
    String GOOD_STOCK_OCCUPY = "GOOD:STOCK:OCCUPY:";

    //占用库存
    String GOOD_STOCK_OCCUPY_BUSINESS = "GOOD:STOCK:OCCUPY:BUSINESS:";

    //扣减库存
    String GOOD_STOCK_DEDUCT = "GOOD:STOCK:DEDUCT:";

    interface GoodStockStatus {
        //状态(1：占用 2：扣减 3：释放 4:回退 5：过期)"

        //失败订单 OCCUPY = RELIEVE + ROLLBACK
        //成功订单 OCCUPY = DEDUCT;

        Integer OCCUPY = 1; //占用

        Integer DEDUCT = 2; //扣减

        Integer RELIEVE = 3;//释放

        Integer ROLLBACK = 4;//回退

        Integer EXPIRE = 5;//过期
    }
}
