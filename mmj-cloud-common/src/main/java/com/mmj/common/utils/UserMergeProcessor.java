package com.mmj.common.utils;


import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 考虑4种情况
 * 1.新userId,旧userId都不存在    --> 新、旧分别创建UUID --> 对应同一个ZSET(内容是2个用户ID)
 * 2.新userId,旧userId都存在      --> 删除旧对应的UUID --> 更新ZSET把旧的ZSET挪到新的ZSET-->把新的用户id变成最新的用户ID --> 把旧的指向新的UUID
 * 3.新userId存在,旧userId不存在   --> 旧指向新的UUID --> 把旧加入到ZSET里
 * 4.新userId不存在,旧userId存在   --> 新用户Id写入ZSET并表示最新 --> 新用户Id指向就的UUID(新旧指向同一个UUID)
 */
@Component
public class UserMergeProcessor {

    private final static String USER_ID_UUID_HASH = "USER_ID_UUID_HASH";

    private final static String UUID_MAPPED_SET = "UUID_MAPPED_SET:";

    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 查询最新的用户id
     *
     * @param userId //用户id
     * @return BaseUser
     */
    public Long getLast(Long userId) {
        if (null == userId)
            throw new NullPointerException("缺少userId");
        Object obj = redisTemplate.opsForHash().get(USER_ID_UUID_HASH, String.valueOf(userId));
        if (null == obj)
            return userId;
        Set<String> set = redisTemplate.opsForZSet().range(UUID_MAPPED_SET + obj, 0, -1);
        if (set.size() == 0) {
            return userId;
        } else {
            List<Long> list = new ArrayList<>(set.size());
            set.forEach(ud -> {
                if (Objects.nonNull(ud))
                    list.add(Long.parseLong(ud));
            });
            return list.get(list.size() - 1);
        }
    }

    /**
     * 查询所有的用户id
     *
     * @param userId //用户id
     * @return BaseUser
     */
    public List<Long> getAll(Long userId) {
        if (null == userId)
            throw new NullPointerException("缺少userId");
        Object obj = redisTemplate.opsForHash().get(USER_ID_UUID_HASH, String.valueOf(userId));
        if (null == obj)
            return Collections.singletonList(userId);
        Set<String> set = redisTemplate.opsForZSet().range(UUID_MAPPED_SET + obj, 0, -1);
        if (set.size() == 0) {
            return Collections.singletonList(userId);
        } else {
            List<Long> list = new ArrayList<>(set.size());
            set.forEach(ud -> {
                if (Objects.nonNull(ud))
                    list.add(Long.parseLong(ud));
            });
            return list;
        }
    }

    /**
     * 根据用户取模分组(售后)
     * 通过传入的用户ID查询关联的用户ID，取模后分组返回，返回结构：Map<Long,List<Long>>
     *
     * @param userId
     * @return
     */
    public Map<Long, List<Long>> getAllAfterSaleToMoldMap(Long userId) {
        return toMoldMap(getAll(userId), 10);
    }

    /**
     * 根据用户取模分组(订单)
     * 通过传入的用户ID查询关联的用户ID，取模后分组返回，返回结构：Map<Long,List<Long>>
     *
     * @param userId
     * @return
     */
    public Map<Long, List<Long>> getAllOrderToMoldMap(Long userId) {
        return toMoldMap(getAll(userId), 100);
    }

    /**
     * 根据用户取模分组
     * 通过传入的用户ID查询关联的用户ID，取模后分组返回，返回结构：Map<Long,List<Long>>
     *
     * @param userId
     * @param mold   取模数量
     * @return
     */
    public Map<Long, List<Long>> getAllToMoldMap(Long userId, int mold) {
        return toMoldMap(getAll(userId), mold);
    }

    /**
     * 根据用户取模分组
     * 通过传入的用户ID取模后分组返回，返回结构：Map<Long,List<Long>>
     *
     * @param mergeUserList
     * @param mold          取模数量
     * @return
     */
    private Map<Long, List<Long>> toMoldMap(List<Long> mergeUserList, int mold) {
        if (mergeUserList.isEmpty())
            throw new IllegalArgumentException("mergeUserList isEmpty");
        List<MergeUserModel> mergeUserModels = Lists.newArrayListWithCapacity(mergeUserList.size());
        mergeUserList.forEach(mergeUserId ->
                mergeUserModels.add(new MergeUserModel(mergeUserId, mergeUserId % mold)));
        return mergeUserModels
                .stream()
                .collect(Collectors.groupingBy(MergeUserModel::getMold
                        , Collectors.mapping(MergeUserModel::getUserId, Collectors.toList())));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class MergeUserModel {
        private Long userId;
        private long mold;
    }
}
