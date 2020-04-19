package com.mmj.active.common.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.ActiveGoodEx;
import com.mmj.active.common.model.ActiveGoodStore;
import com.mmj.common.model.active.ActiveGoodStoreResult;

import java.util.List;


/**
 * <p>
 * 活动商品关联表 服务类
 * </p>
 *
 * @author KK
 * @since 2019-06-15
 */
public interface ActiveGoodService extends IService<ActiveGood> {

    /**
     * 秒杀商品库存扣减
     *
     * @param activeGoodStore
     * @return
     */
    Integer seckillCheck(ActiveGoodStore activeGoodStore);

    /**
     * 秒杀商品金额校验
     * @param activeGoodStore
     */
    void seckillAmountCheck(ActiveGoodStore activeGoodStore);

    /**
     * 活动虚拟库存存
     *
     * @param businessId
     * @return
     */
    Integer decActiveVirtual(Integer businessId);

    /**
     * 根据活动id删除商品
     *
     * @param businessId
     */
    void deleteBusinessId(Integer businessId);

    /**
     * 查询活动商品基本信息
     *
     * @param activeGood
     * @return
     */
    Page<ActiveGood> queryBaseList(ActiveGood activeGood);

    /**
     * 新人团下单验证
     *
     * @param activeGoodStore
     * @return
     */
    Boolean produceNewComers(ActiveGoodStore activeGoodStore);

    /**
     * 砍价验证
     *
     * @param activeGoodStore
     * @return
     */
    ActiveGoodStoreResult cutOrderCheck(ActiveGoodStore activeGoodStore);

    /**
     * 查询活动商品基本信息-排序
     *
     * @param activeGoodEx
     * @return
     */
    Page<ActiveGood> queryBaseOrder(ActiveGoodEx activeGoodEx);

    /**
     * 清除商品缓存
     * @param activeType
     */
    void cleanGoodCache(Integer activeType);

}
