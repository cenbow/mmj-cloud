package com.mmj.active.seckill.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.active.seckill.model.SeckillTimes;
import com.mmj.active.seckill.model.SeckillTimesEx;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 秒杀期次表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-14
 */
public interface SeckillTimesMapper extends BaseMapper<SeckillTimes> {

    List<SeckillTimesEx> queryAndGood(@Param("isActive") Integer isActive, @Param("seckillId") Integer seckillId, @Param("seckillPriod") Integer seckillPriod, @Param("times") String times, @Param("seckillType") Integer seckillType);
}
