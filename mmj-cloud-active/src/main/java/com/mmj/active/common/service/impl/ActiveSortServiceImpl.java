package com.mmj.active.common.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.mapper.ActiveSortMapper;
import com.mmj.active.common.model.ActiveSort;
import com.mmj.active.common.service.ActiveSortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 活动排序公用表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-27
 */
@Service
public class ActiveSortServiceImpl extends ServiceImpl<ActiveSortMapper, ActiveSort> implements ActiveSortService {

    @Autowired
    private ActiveSortMapper activeSortMapper;

    @Override
    public void deleteBusinessId(Integer topId){
        EntityWrapper<ActiveSort> activeSortEntityWrapper = new EntityWrapper<>();
        activeSortEntityWrapper.eq("BUSINESS_ID",topId);
        activeSortMapper.delete(activeSortEntityWrapper);
    }

    @Override
    public List<ActiveSort> selectBusinessList(Integer topId){
        EntityWrapper<ActiveSort> activeSortEntityWrapper = new EntityWrapper<>();
        activeSortEntityWrapper.eq("BUSINESS_ID",topId);
        return activeSortMapper.selectList(activeSortEntityWrapper);
    }
}
