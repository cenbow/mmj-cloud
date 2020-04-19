package com.mmj.order.async.service.impl;

import com.google.common.collect.Maps;
import com.mmj.common.model.UserMerge;
import com.mmj.order.async.service.OrderAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Configuration
@EnableAsync
public class OrderAsyncServiceImpl implements OrderAsyncService {


    private final static String USER_ID_UUID_HASH = "USER_ID_UUID_HASH";

    private final static String UUID_MAPPED_SET = "UUID_MAPPED_SET:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

        /*
      合并用户id

      @param nUserId 最新的用户id
     * @param oUserId 旧用户id
     * @desc 用户关系 判断用户是否存在，不存在建立关系=Hash 用户ID-> uuid（唯一值）-> zset - [{用户ID,score}]
     */

    private static long getScore() {
        return System.currentTimeMillis();
    }

    private static int checkType(Object nUserId, Object oUserId) {
        if (null == nUserId) {
            if (null == oUserId)
                return 1;
            return 4;
        }
        if (null == oUserId) {
            return 3;
        }
        return 2;
    }

    /**
     * 考虑4种情况
     * 1.新userId,旧userId都不存在    --> 新、旧分别创建UUID --> 对应同一个ZSET(内容是2个用户ID)
     * 2.新userId,旧userId都存在      --> 删除旧对应的UUID --> 更新ZSET把旧的ZSET挪到新的ZSET-->把新的用户id变成最新的用户ID --> 把旧的指向新的UUID
     * 3.新userId存在,旧userId不存在   --> 旧指向新的UUID --> 把旧加入到ZSET里
     * 4.新userId不存在,旧userId存在   --> 新用户Id写入ZSET并表示最新 --> 新用户Id指向就的UUID(新旧指向同一个UUID)
     */
    @Override
    @Async
    public void mergeOrder(UserMerge userMerge) {
        Long nUserId = userMerge.getNewUserId();
        Long oUserId = userMerge.getOldUserId();

        Object nRes = redisTemplate.opsForHash().get(USER_ID_UUID_HASH, String.valueOf(nUserId));
        Object oRes = redisTemplate.opsForHash().get(USER_ID_UUID_HASH, String.valueOf(oUserId));
        int type = checkType(nRes, oRes);

        log.info("nUserId:{},nRes:{},oUserId:{},oRes:{},type:{}", nUserId, nRes, oUserId, oRes, type);
        if (1 == type) {
            String nUUID = UUID.randomUUID().toString().replaceAll("-", "");
            long score = getScore();
            ZSetOperations.TypedTuple<String> tuple1 = new DefaultTypedTuple<>(String.valueOf(nUserId), Double.valueOf(String.valueOf(score + 1)));
            ZSetOperations.TypedTuple<String> tuple2 = new DefaultTypedTuple<>(String.valueOf(oUserId), Double.valueOf(String.valueOf(score)));
            Set<ZSetOperations.TypedTuple<String>> set = new HashSet<>();
            set.add(tuple1);
            set.add(tuple2);
            redisTemplate.opsForZSet().add(UUID_MAPPED_SET + nUUID, set); //新userId插入ZSET集合
            redisTemplate.opsForHash().put(USER_ID_UUID_HASH, String.valueOf(nUserId), nUUID); //把旧userId的UUID更新为新userID的UUID
            redisTemplate.opsForHash().put(USER_ID_UUID_HASH, String.valueOf(oUserId), nUUID); //旧用户id也指向zset
        } else if (2 == type) {
            //查出旧的UUID
            Set<ZSetOperations.TypedTuple<String>> oSet = redisTemplate.opsForZSet().rangeWithScores(UUID_MAPPED_SET + oRes.toString(), 0, -1);
            Set<ZSetOperations.TypedTuple<String>> nSet = redisTemplate.opsForZSet().rangeWithScores(UUID_MAPPED_SET + nRes.toString(), 0, -1);
            nSet.addAll(oSet);
            long score = getScore();
            ZSetOperations.TypedTuple<String> tuple = new DefaultTypedTuple<>(String.valueOf(nUserId), Double.valueOf(String.valueOf(score)));
            nSet.add(tuple);
            redisTemplate.opsForZSet().add(UUID_MAPPED_SET + nRes, nSet); //新userId插入ZSET集合
            redisTemplate.opsForHash().put(USER_ID_UUID_HASH, String.valueOf(oUserId), nRes); //旧用户id也指向zset

            //把旧的用户id全部查出来、把就用户的uuid替换成最新的uuid
            Map<String, String> map = Maps.newHashMapWithExpectedSize(oSet.size());
            for (ZSetOperations.TypedTuple<String> oTypedTuple : oSet) {
                //把就用户uuid替换成最新的uuid
                map.put(oTypedTuple.getValue(), nRes.toString());
            }
            redisTemplate.opsForHash().putAll(USER_ID_UUID_HASH, map);

            if (!oRes.equals(nRes))
                redisTemplate.delete(UUID_MAPPED_SET + oRes.toString());
        } else if (3 == type) {
            /*
            ZSetOperations.TypedTuple<String> tuple = new DefaultTypedTuple<>(String.valueOf(oUserId), Double.valueOf(String.valueOf(1)));
            Set<ZSetOperations.TypedTuple<String>> set = new HashSet<>();
            set.add(tuple);
            */
            redisTemplate.opsForZSet().add(UUID_MAPPED_SET + nRes, String.valueOf(oUserId), Double.valueOf(String.valueOf(1)));  //把就用户id加入到zset里，并设置成旧用户id
            redisTemplate.opsForHash().put(USER_ID_UUID_HASH, String.valueOf(oUserId), nRes);  //把就用户uuid指向这个zset
        } else if (4 == type) {
            long score = getScore();
            ZSetOperations.TypedTuple<String> tuple = new DefaultTypedTuple<>(String.valueOf(nUserId), Double.valueOf(String.valueOf(score)));
            Set<ZSetOperations.TypedTuple<String>> set = new HashSet<>();
            set.add(tuple);
            redisTemplate.opsForZSet().add(UUID_MAPPED_SET + oRes, set); //把新用户id设置为最新的用户id，再加入到zset里
            redisTemplate.opsForHash().put(USER_ID_UUID_HASH, String.valueOf(nUserId), oRes); //新用户uuid指向这个zset
        }
    }
}
