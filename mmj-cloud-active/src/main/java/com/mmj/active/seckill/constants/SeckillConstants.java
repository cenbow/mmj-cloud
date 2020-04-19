package com.mmj.active.seckill.constants;

public interface SeckillConstants {

    //秒杀活动库存 + businessId + ":" + goodId + ":" + goodSku
    String SECKILL_STORE = "ACTIVE:SECKILL:GOOD:STORE:";
    //秒杀活动库存处理失败 + businessId + ":" + goodId + ":" + goodSku
    String SECKILL_STORE_FAIL = "ACTIVE:SECKILL:GOOD:STORE:FAIL:";
    //秒杀活动虚拟库存 + businessId + ":" + goodId + ":" + goodSku
    String SECKILL_VIRTUAL_STORE = "ACTIVE:SECKILL:GOOD:VIRTUAL_STORE:";

    //秒杀订单 - 每人每天购买数量 + userid + ":" + businessId + ":" + goodId + ":" + saleId
    String SECKILL_ORDER_LIMIT = "ACTIVE:SECKILL:GOOD:ORDER:";
    //秒杀每人每天购买数量 站内
    String SECKILL_LIMIT_IN = "ACTIVE:SECKILL:LIMIT:IN";
    //秒杀每人每天购买数量 站外
    String SECKILL_LIMIT_OUT = "ACTIVE:SECKILL:LIMIT:OUT:";

    //1站内秒杀
    Integer SECKILL_TYPE_1 = 1;
    //2独立秒杀
    Integer SECKILL_TYPE_2 = 2;

    String ACTIVE_SECKILL_USER_ORDER = "ACTIVE:SECKILL:USER:";

    String ACTIVE_SECKILL_USER_ORDER_IN = "ACTIVE:SECKILL:USER:ORDER_IN:";

    String ACTIVE_SECKILL_USER_ORDER_OUT = "ACTIVE:SECKILL:USER:ORDER_OUT:";

    interface SeckillTimesPriod {
        //进行中的档期
        String SECKILL_PRIOD_NOW_1 = "ACTIVE:SECKILL:TIMES:NOW_PRIOD1";
        //最大档期
        String SECKILL_PRIOD_MAX_1 = "ACTIVE:SECKILL:TIMES:MAX_PRIOD1";

        //查询批次
        String TIMES_NOW = "NOW"; //进行中
        String TIMES_NEXT = "NEXT"; //即将开始
        String TIMES_TOMORROW = "TOMORROW"; //明日预告
        String TIMES_TOMORROW_NEXT = "TOMORROW-NEXT"; //明日第二场
    }

    interface  SeckillTimesActive {
        Integer PASS = 2;
        Integer YES = 1;
        Integer NO = 0;
    }
}
