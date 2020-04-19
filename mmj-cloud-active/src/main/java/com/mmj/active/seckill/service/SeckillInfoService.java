package com.mmj.active.seckill.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.seckill.model.SeckillInfo;
import com.mmj.active.seckill.model.SeckillInfoEx;
import com.mmj.common.model.GoodStock;

import java.util.List;

/**
 * <p>
 * 秒杀信息表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-13
 */
public interface SeckillInfoService extends IService<SeckillInfo> {

    void save(SeckillInfoEx entityEx);

    /**
     * 查询全部信息
     * @return
     */
    SeckillInfoEx queryDetail(Integer seckillType, Integer seckillId);

    /**
     * 查询全部进行中信息
     * @return
     */
    SeckillInfoEx queryDetailActive(Integer seckillType, Integer seckillId);

    /**
     * 查询进行中的档期
     * @return
     */
    Integer getNowPriod();

    /**
     * 查询最大档期
     * @return
     */
    Integer getMaxPriod();

    /**
     * 获取下一个档期
     * @return
     */
    Integer getNextPriod();

    /**
     * 获取下一个档期
     * @return
     */
    Integer getNextPriodS();


    /**
     * 删除站外活动
     * @param seckillId
     */
    void delete(Integer seckillId) throws Exception;

    /**
     * 修改当前期次
     * @param nextPriod
     * @param seckillType
     * @throws Exception
     */
    List<GoodStock> cp(Integer nextPriod, Integer seckillType) throws Exception;

    /**
     * 清理过期活动，每小时执行一次
     * @param seckillType
     * @throws Exception
     */
    void qto(Integer seckillType) throws Exception;


    /**
     * 减少虚拟库存，每10秒 执行一次
     * @param seckillType
     * @throws Exception
     */
    void dav(Integer seckillType) throws Exception;

    void orderFail(String orderNo);

    void paySuccess(String orderNo);

    void payCancelled(String orderNo);

    void paydClosed(String orderNo);

    void sendFlashSaleSMS(String orderNo);

}
