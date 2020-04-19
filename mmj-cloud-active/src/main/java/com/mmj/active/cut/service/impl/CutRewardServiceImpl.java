package com.mmj.active.cut.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.collect.Lists;
import com.mmj.active.cut.model.CutAward;
import com.mmj.active.cut.model.CutReward;
import com.mmj.active.cut.mapper.CutRewardMapper;
import com.mmj.active.cut.model.dto.FirstRateRewardDto;
import com.mmj.active.cut.service.CutAwardService;
import com.mmj.active.cut.service.CutRewardService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户砍价奖励表 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-07-26
 */
@Slf4j
@Service
public class CutRewardServiceImpl extends ServiceImpl<CutRewardMapper, CutReward> implements CutRewardService {
    @Autowired
    private CutAwardService cutAwardService;

    @Override
    public FirstRateRewardDto getFirstRateReward(Long userId) {
        CutReward queryCutReward = new CutReward();
        queryCutReward.setUserId(userId);
        queryCutReward.setUseFlag(0);
        EntityWrapper<CutReward> entityWrapper = new EntityWrapper<>(queryCutReward);
        entityWrapper.in("REWARD_TYPE", Arrays.asList(0, 1, 3));
        entityWrapper.gt("EXPIRE_TIME", new Date());
        List<CutReward> cutRewardList = selectList(entityWrapper);
        if (Objects.nonNull(cutRewardList) && cutRewardList.size() > 0) {
            cutRewardList = cutRewardList.stream().sorted(new Comparator<CutReward>() {
                @Override
                public int compare(CutReward o1, CutReward o2) {
                    int a = o1.getRewardType().compareTo(o2.getRewardType());
                    if (a == 0) {
                        return o2.getRewardValue().compareTo(o1.getRewardValue());
                    }
                    return a;
                }
            }).collect(Collectors.toList());
            CutReward cutReward = cutRewardList.get(0);
            if (Objects.nonNull(cutReward.getExpireTime())) {
                return new FirstRateRewardDto(cutReward.getRewardValue(), cutReward.getRewardValueType(), cutReward.getExpireTime().getTime());
//                if (System.currentTimeMillis() - cutReward.getCreateTime().getTime() < 900000)
//                else {
//                    if (cutRewardList.size() > 1) {
//                        cutReward = cutRewardList.get(1);
//                        return new FirstRateRewardDto(cutReward.getRewardValue(), cutReward.getRewardValueType(), null);
//                    }
//                }
            } else {
                return new FirstRateRewardDto(cutReward.getRewardValue(), cutReward.getRewardValueType(), null);
            }
        }
        return null;
    }

    @Override
    public CutReward getMaxCutReward(Long userId) {
        CutReward queryCutReward = new CutReward();
        queryCutReward.setUserId(userId);
        queryCutReward.setUseFlag(0);
        EntityWrapper<CutReward> entityWrapper = new EntityWrapper<>(queryCutReward);
        entityWrapper.in("REWARD_TYPE", Arrays.asList(0, 1, 3));
        entityWrapper.orderBy("REWARD_VALUE", false);
        return selectOne(entityWrapper);
    }

    @Override
    public void editUseFlagById(Integer rewardId, String cutNo) {
        CutReward cutReward = new CutReward();
        cutReward.setUseFlag(1);
        cutReward.setUseTime(new Date());
        cutReward.setUseCutNo(cutNo);
        CutReward queryCutReward = new CutReward();
        queryCutReward.setUseFlag(0);
        queryCutReward.setRewardId(rewardId);
        EntityWrapper<CutReward> entityWrapper = new EntityWrapper<>(queryCutReward);
        boolean result = update(cutReward, entityWrapper);
        Assert.isTrue(result, "首砍已被使用");
    }

    @Override
    public CutReward addFirstCutReward(Long userId, Integer cutId, String cutNo) {
        CutAward queryCutAward = new CutAward();
        queryCutAward.setCutId(cutId);
        EntityWrapper<CutAward> cutAwardEntityWrapper = new EntityWrapper<>(queryCutAward);
        List<CutAward> cutAwards = cutAwardService.selectList(cutAwardEntityWrapper);
        int rate = 0;
        for (int i = 0, size = cutAwards.size(); i < size; i++) {
            rate += cutAwards.get(i).getAwardRate().intValue();
        }
        if (rate == 100) {
            List<CutAward> cutAwardList = Lists.newArrayListWithCapacity(100);
            cutAwards.forEach(cutAward -> {
                for (int i = 0, size = cutAward.getAwardRate().intValue(); i < size; i++) {
                    cutAwardList.add(cutAward);
                }
            });
            int index = new Random().nextInt(99 - 0) + 0;
            BigDecimal rewardAmount = cutAwardList.get(index).getFristCutRate();
            CutReward cutReward = new CutReward();
            cutReward.setRewardType(0);
            cutReward.setUserId(userId);
            cutReward.setCutId(cutId);
            cutReward.setCutNo(cutNo);
            cutReward.setRewardValue(rewardAmount);
            cutReward.setRewardValueType(1);
            cutReward.setExpireTime(new Date(System.currentTimeMillis() + 86400000));
            boolean result = insert(cutReward);
            if (result) {
                return cutReward;
            }
        } else {
            log.warn("=> 帮砍时，新用户获取奖励失败 cutNo:{},userId:{},award:{}", cutNo, userId, JSON.toJSONString(cutAwards));
        }
        return null;
    }

    @Override
    public CutReward addCutPaySuccessReward(Long userId, Integer cutId, String cutNo) {
        CutReward cutReward = new CutReward();
        cutReward.setRewardType(3);
        cutReward.setUserId(userId);
        cutReward.setCutId(cutId);
        cutReward.setCutNo(cutNo);
        cutReward.setRewardValue(BigDecimal.valueOf(10));
        cutReward.setRewardValueType(1);
        cutReward.setExpireTime(new Date(System.currentTimeMillis() + 900000));
        boolean result = insert(cutReward);
        if (result) {
            return cutReward;
        }
        return null;
    }
}
