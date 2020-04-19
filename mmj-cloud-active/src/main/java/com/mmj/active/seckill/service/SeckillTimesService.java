package com.mmj.active.seckill.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.seckill.model.SeckillTimes;
import com.mmj.active.seckill.model.SeckillTimesEx;

import java.util.List;

/**
 * <p>
 * 秒杀期次表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-13
 */
public interface SeckillTimesService extends IService<SeckillTimes> {

    List<SeckillTimesEx> queryAndGood(Integer isActive, Integer seckillId, Integer seckillPriod, String times, Integer seckillType);

    //void deleteTimesGood(Integer goodId);

    //void deleteTimes(Integer timesId);

    void decrStore(String sku, Integer num, Integer businessId);
}
