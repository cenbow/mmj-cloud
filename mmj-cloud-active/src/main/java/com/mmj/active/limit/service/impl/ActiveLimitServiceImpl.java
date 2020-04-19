package com.mmj.active.limit.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.limit.mapper.ActiveLimitMapper;
import com.mmj.active.limit.model.ActiveLimit;
import com.mmj.active.limit.model.ActiveLimitDetail;
import com.mmj.active.limit.model.ActiveLimitEx;
import com.mmj.active.limit.service.ActiveLimitDetailService;
import com.mmj.active.limit.service.ActiveLimitService;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.SecurityUserUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动商品限购表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-29
 */
@Service
public class ActiveLimitServiceImpl extends ServiceImpl<ActiveLimitMapper, ActiveLimit> implements ActiveLimitService {

    @Autowired
    private ActiveGoodService activeGoodService;

    @Autowired
    private ActiveLimitDetailService activeLimitDetailService;

    @Autowired
    private ActiveLimitMapper activeLimitMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String ACTIVE_LIMIT_INFO = "ACTIVE_LIMIT_INFO:";

    @Transactional(rollbackFor = Exception.class)
    public void save(ActiveLimitEx activeLimitEx) {
        ActiveLimit activeLimit = JSON.parseObject(JSON.toJSONString(activeLimitEx), ActiveLimit.class);
        String activeTypesStr = activeLimit.getActiveType();
        String[] activeTypes = activeTypesStr.split(",");
        //删除缓存
        if (activeTypes != null && activeTypes.length != 0) {
            for (String a : activeTypes) {
                redisTemplate.delete(ACTIVE_LIMIT_INFO + a);
            }
        }
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        //更新限购信息
        activeLimit.setCreaterId(userDetails.getUserId());
        insert(activeLimit);

        List<ActiveLimitDetail> activeLimitDetails = activeLimitEx.getActiveLimitDetails();
        if (activeLimitDetails != null && !activeLimitDetails.isEmpty()) {
            activeLimitDetails.stream().forEach(activeLimitDetail -> {
                activeLimitDetail.setLimitId(activeLimit.getLimitId());
            });
            activeLimitDetailService.insertBatch(activeLimitDetails);
        }

        //关联商品
        List<Integer> goodIds = activeLimitEx.getGoodIds();
        if (goodIds != null) {
            ActiveGood activeGood = new ActiveGood();
            activeGood.setGoodLimit(activeLimit.getStatus());
            activeGood.setArg4(String.valueOf(activeLimit.getLimitId()));
            EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
            entityWrapper.in("GOOD_ID", goodIds);
            entityWrapper.in("ACTIVE_TYPE", Arrays.asList(activeTypes));
            activeGoodService.update(activeGood, entityWrapper);
        }
    }

    public List<ActiveLimitEx> query(Integer goodId) {
        EntityWrapper<ActiveGood> goodEntityWrapper = new EntityWrapper<>();
        goodEntityWrapper.eq("GOOD_ID", goodId);
        goodEntityWrapper.isNotNull("ARG_4");
        goodEntityWrapper.groupBy(" ARG_4 ");
        List<ActiveGood> activeGoods = activeGoodService.selectList(goodEntityWrapper);
        if (activeGoods != null && !activeGoods.isEmpty()) {
            List<ActiveLimitEx> list = new ArrayList<>();
            List<String> limitIds = activeGoods.stream().map(ActiveGood::getArg4).collect(Collectors.toList());
            List<Integer> out = new ArrayList<>();
            CollectionUtils.collect(limitIds,
                    new Transformer() {
                        public Object transform(Object input) {
                            return Integer.valueOf(String.valueOf(input));
                        }
                    }, out);

            EntityWrapper<ActiveLimit> entityWrapper = new EntityWrapper<>();
            entityWrapper.in("LIMIT_ID", out);
            List<ActiveLimit> activeLimits = selectList(entityWrapper);
            if (activeLimits != null && !activeLimits.isEmpty()) {
                for (ActiveLimit activeLimit : activeLimits) {
                    ActiveLimitEx activeLimitEx = JSON.parseObject(JSON.toJSONString(activeLimit), ActiveLimitEx.class);
                    EntityWrapper<ActiveLimitDetail> activeLimitDetailEntityWrapper = new EntityWrapper<>();
                    activeLimitDetailEntityWrapper.eq("LIMIT_ID", activeLimitEx.getLimitId());
                    //限购详情
                    List<ActiveLimitDetail> activeLimitDetails = activeLimitDetailService.selectList(activeLimitDetailEntityWrapper);
                    if (activeLimitDetails != null && !activeLimitDetails.isEmpty()) {
                        activeLimitEx.setActiveLimitDetails(activeLimitDetails);
                    }

                    //限购商品类型
                    EntityWrapper<ActiveGood> wrapper = new EntityWrapper<>();
                    wrapper.setSqlSelect("ACTIVE_TYPE");
                    wrapper.eq("ARG_4", activeLimit.getLimitId());
                    wrapper.eq("GOOD_LIMIT", 1);
                    wrapper.groupBy("ACTIVE_TYPE");
                    List<ActiveGood> activeGoods1 = activeGoodService.selectList(wrapper);
                    if (activeGoods1 != null && !activeGoods1.isEmpty()) {
                        activeLimitEx.setActiveType(StringUtils.join(activeGoods1, ","));
                    }
                    list.add(activeLimitEx);
                }
            }
            return list;
        }
        return null;
    }

    public ActiveLimitEx queryLimit(String activeType, Integer goodId){
        String key = ACTIVE_LIMIT_INFO + activeType;
        Object o = redisTemplate.opsForHash().get(key, goodId);
        if (o != null && !"".equals(o)) {
            return JSON.parseObject(String.valueOf(o), ActiveLimitEx.class);
        }
        //限购信息
        EntityWrapper<ActiveGood> goodEntityWrapper = new EntityWrapper<>();
        goodEntityWrapper.eq("GOOD_ID", goodId);
        goodEntityWrapper.eq("ACTIVE_TYPE", activeType);
        goodEntityWrapper.eq("GOOD_LIMIT", 1);
        goodEntityWrapper.groupBy(" ACTIVE_TYPE, GOOD_ID ");
        List<ActiveGood> activeGoods = activeGoodService.selectList(goodEntityWrapper);
        if (activeGoods != null && !activeGoods.isEmpty()) {
            ActiveGood activeGood = activeGoods.get(0);
            ActiveLimit activeLimit = selectById(Integer.valueOf(activeGood.getArg4()));
            if (activeLimit != null) {
                ActiveLimitEx activeLimitEx = JSON.parseObject(JSON.toJSONString(activeLimit), ActiveLimitEx.class);
                EntityWrapper<ActiveLimitDetail> activeLimitDetailEntityWrapper = new EntityWrapper<>();
                activeLimitDetailEntityWrapper.eq("LIMIT_ID", activeLimitEx.getLimitId());
                //限购详情
                List<ActiveLimitDetail> activeLimitDetails = activeLimitDetailService.selectList(activeLimitDetailEntityWrapper);
                if (activeLimitDetails != null && !activeLimitDetails.isEmpty()) {
                    activeLimitEx.setActiveLimitDetails(activeLimitDetails);
                }
                //款式限定处理
                if (activeLimit.getLimitGood() != null) {
                    EntityWrapper<ActiveGood> goodEntityWrapper1 = new EntityWrapper<>();
                    goodEntityWrapper1.eq("ARG_4", activeLimitEx.getLimitId());
                    goodEntityWrapper1.eq("ACTIVE_TYPE", activeType);
                    goodEntityWrapper1.groupBy(" ACTIVE_TYPE, GOOD_ID ");
                    List<ActiveGood> activeGoods1 = activeGoodService.selectList(goodEntityWrapper1);
                    if (activeGoods1 != null && !activeGoods1.isEmpty()) {
                        activeLimitEx.setActiveGoods(activeGoods1);
                    }
                }
                redisTemplate.opsForHash().put(key, goodId, JSON.toJSONString(activeLimitEx));
                return activeLimitEx;
            }
        }
        return null;
    }
}
