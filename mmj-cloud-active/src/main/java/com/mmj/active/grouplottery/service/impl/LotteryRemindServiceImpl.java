package com.mmj.active.grouplottery.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.constants.LotteryRemindConstants;
import com.mmj.active.grouplottery.mapper.LotteryRemindMapper;
import com.mmj.active.grouplottery.model.LotteryRemind;
import com.mmj.active.grouplottery.service.LotteryRemindService;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.properties.SecurityConstants;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>
 * 用户关注抽奖信息表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-26
 */
@Service
public class LotteryRemindServiceImpl extends ServiceImpl<LotteryRemindMapper, LotteryRemind> implements LotteryRemindService {

    private Lock lock = new ReentrantReadWriteLock().readLock();

    @Override
    public void remind(Long userId, String type, String method) {
        lock.lock();
        LotteryRemind remind = new LotteryRemind();
        remind.setUserId(userId);
        EntityWrapper<LotteryRemind> wrapper = new EntityWrapper<>(remind);
        LotteryRemind lotteryRemind = selectOne(wrapper);
        if (lotteryRemind == null) {
            lotteryRemind = new LotteryRemind();
            //不存在
            if (method.equals("remind")) {
                if ("C".equals(type)) {
                    lotteryRemind.setStatusC(LotteryRemindConstants.OPEN);
                    lotteryRemind.setStatusO(LotteryRemindConstants.CLOSE);
                } else {
                    lotteryRemind.setStatusC(LotteryRemindConstants.CLOSE);
                    lotteryRemind.setStatusO(LotteryRemindConstants.OPEN);
                }

            }
            lotteryRemind.setUserId(userId);
            lotteryRemind.setCreateTime(new Date());
            insert(lotteryRemind);
        } else {
            if (method.equals("remind")) {
                if ("C".equals(type)) {
                    lotteryRemind.setStatusC(LotteryRemindConstants.OPEN);
                } else {
                    lotteryRemind.setStatusO(LotteryRemindConstants.OPEN);
                }
            } else {
                if ("C".equals(type)) {
                    lotteryRemind.setStatusC(LotteryRemindConstants.CLOSE);
                } else {
                    lotteryRemind.setStatusO(LotteryRemindConstants.CLOSE);
                }
            }
            updateById(lotteryRemind);
        }
        lock.unlock();
    }

    @Override
    public int getRemind(Long userId, Integer from) {
        LotteryRemind remind = new LotteryRemind();
        remind.setUserId(userId);
        EntityWrapper<LotteryRemind> wrapper = new EntityWrapper<>(remind);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        remind = selectOne(wrapper);
        if (null == remind)
            return 0;
        if (1 == from && LotteryRemindConstants.OPEN.equals(remind.getStatusC())) {
            return 1;
        } else if (2 == from && LotteryRemindConstants.OPEN.equals(remind.getStatusO())) {
            return 1;
        }
        return 0;
    }
}
