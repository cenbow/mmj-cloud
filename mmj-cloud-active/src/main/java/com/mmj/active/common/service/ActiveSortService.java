package com.mmj.active.common.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.common.model.ActiveSort;

import java.util.List;

/**
 * <p>
 * 活动排序公用表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-27
 */
public interface ActiveSortService extends IService<ActiveSort> {

    void deleteBusinessId(Integer topId);

    /**
     * 根据活动id查询排序
     * @param topId
     * @return
     */
    List<ActiveSort> selectBusinessList(Integer topId);
}
