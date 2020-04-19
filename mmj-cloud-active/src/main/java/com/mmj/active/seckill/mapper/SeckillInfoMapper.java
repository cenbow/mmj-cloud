package com.mmj.active.seckill.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.active.seckill.model.SeckillInfo;
import com.mmj.active.seckill.model.SeckillInfoEx;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 秒杀信息表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-13
 */
public interface SeckillInfoMapper extends BaseMapper<SeckillInfo> {

    SeckillInfoEx queryDetail(@Param(value = "seckillType") Integer seckillType, @Param(value = "seckillId") Integer seckillId);

    SeckillInfoEx queryDetailActive(@Param(value = "seckillType") Integer seckillType, @Param(value = "seckillId") Integer seckillId);

    SeckillInfoEx queryDetailIn(@Param(value = "seckillType") Integer seckillType, @Param(value = "seckillId") Integer seckillId);
}
