package com.mmj.active.limit.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.limit.model.ActiveLimit;
import com.mmj.active.limit.model.ActiveLimitEx;

import java.util.List;

/**
 * <p>
 * 活动商品限购表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-29
 */
public interface ActiveLimitService extends IService<ActiveLimit> {

    void save(ActiveLimitEx activeLimitEx);

    List<ActiveLimitEx> query(Integer goodId);

    ActiveLimitEx queryLimit(String activeType, Integer goodId);
}
