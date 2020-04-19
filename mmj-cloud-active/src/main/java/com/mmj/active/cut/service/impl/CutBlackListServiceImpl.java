package com.mmj.active.cut.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.cut.mapper.CutBlackListMapper;
import com.mmj.active.cut.model.CutBlackList;
import com.mmj.active.cut.service.CutBlackListService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 砍价黑名单 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-09-24
 */
@Service
public class CutBlackListServiceImpl extends ServiceImpl<CutBlackListMapper, CutBlackList> implements CutBlackListService {
    @Override
    public int blackList(Long userId, String openId) {
        EntityWrapper<CutBlackList> blackListEntityWrapper = new EntityWrapper<>();
        blackListEntityWrapper.eq("USER_ID", userId).or().eq("OPEN_ID", openId);
        return selectCount(blackListEntityWrapper);
    }
}
