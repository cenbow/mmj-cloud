package com.mmj.active.limit.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.active.limit.model.ActiveLimit;
import com.mmj.active.limit.model.ActiveLimitEx;

/**
 * <p>
 * 活动商品限购表 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-29
 */
public interface ActiveLimitMapper extends BaseMapper<ActiveLimit> {

    ActiveLimitEx query(Integer limitId);
}
