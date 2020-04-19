package com.mmj.active.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.ActiveGoodEx;

import java.util.List;

/**
 * <p>
 * 活动商品关联表 Mapper 接口
 * </p>
 *
 * @author KK
 * @since 2019-06-15
 */
public interface ActiveGoodMapper extends BaseMapper<ActiveGood> {

    Integer decActiveVirtual(Integer businessId);

    List<ActiveGood> queryBaseList(Page<ActiveGood> page, ActiveGood activeGood);

    List<ActiveGood> queryBaseOrder(Page<ActiveGood> page, ActiveGoodEx activeGoodEx);

}
