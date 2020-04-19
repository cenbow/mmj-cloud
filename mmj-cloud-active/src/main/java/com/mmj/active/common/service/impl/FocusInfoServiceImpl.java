package com.mmj.active.common.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.mapper.FocusInfoMapper;
import com.mmj.active.common.model.FocusInfo;
import com.mmj.active.common.service.FocusInfoService;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.UserMerge;
import com.mmj.common.properties.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-31
 */
@Service
public class FocusInfoServiceImpl extends ServiceImpl<FocusInfoMapper, FocusInfo> implements FocusInfoService {

    private static final Logger logger = LoggerFactory.getLogger(FocusInfoServiceImpl.class);

    @Transactional(rollbackFor = Exception.class)
    public void updateUserID(UserMerge userMerge) {
        long oldUserId = userMerge.getOldUserId();
        long newUserId = userMerge.getNewUserId();
        logger.info("-->流量池合并表合并-->oldUserId:{}, newUserId:{}", oldUserId, newUserId);
        if (oldUserId == newUserId) {
            logger.info("-->流量池合并表合并-->新旧userId相等，不用合并");
            return;
        }

        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
        Wrapper<FocusInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("USER_ID", oldUserId);
        FocusInfo um = this.selectOne(wrapper);
        if (um == null) {
            logger.info("-->流量池合并表合并-->根据oldUserId:{}未查到会员信息，不用合并", oldUserId);
            return;
        }
        // 判断是否需要切换表
        EntityWrapper<FocusInfo> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("USER_ID", userMerge.getOldUserId());
        FocusInfo userFocus = new FocusInfo();
        userFocus.setUserId(userMerge.getNewUserId());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
        update(userFocus, entityWrapper);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
    }

}
