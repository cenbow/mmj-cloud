package com.mmj.order.service.impl;

import com.mmj.common.constants.CommonConstant;
import com.mmj.order.common.feign.ActiveFeignClient;
import com.mmj.order.common.model.LotteryConf;
import com.mmj.order.service.LotteryCodeService;
import com.xiaoleilu.hutool.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>
 * 我的抽奖码 服务实现类
 * </p>
 *
 * @author 陈光复
 * @since 2019-07-19
 */
@Service
@Slf4j
public class LotteryCodeServiceImpl implements LotteryCodeService {

    private static String LOTTERY_CODE = "LOTTERY_CODE:";

    @Autowired
    private ActiveFeignClient activeFeignClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private int getJoinSize(Integer lotteryId) {
        String str = redisTemplate.opsForValue().get(CommonConstant.LOTTERY_CODE_COUNT_PREFIX + lotteryId);
        if (null == str)
            //活动第一次成团
            return 0;
        return Integer.parseInt(str);
    }

    @Override
    public String genLotteryCode(Integer lotteryId) {
        lock.readLock().lock();
        Assert.notNull(lotteryId, "活动ID不能为空");
        LotteryConf conf = activeFeignClient.getLotteryById(lotteryId);
        if (null == conf)
            return null;
        long diff = ((conf.getEndTime().getTime() - new Date().getTime()) / (1000 * 3600)) + 1;
        //判断是否需要扩容
        int joinSize = getJoinSize(lotteryId);

        int max = Integer.parseInt(conf.getLotteryCodeEnd());
        //动态扩容
        log.info("生成抽奖码,成团人数:{},joinSize:{}  max:{}", conf.getTuanBuildNum(), joinSize, max);
        if (joinSize + conf.getTuanBuildNum() >= max) {
            max = joinSize + joinSize - max + 50;
        }
        int rand = genCode(Integer.parseInt(conf.getLotteryCodeStart()), max);
        String code = String.valueOf(rand);
        boolean exist = exist(code, lotteryId);
        while (exist) {
            //抽奖码已存在
            rand = genCode(Integer.parseInt(conf.getLotteryCodeStart()), max);
            code = String.valueOf(rand);
            exist = exist(code, lotteryId);
            if (!exist)
                break;
        }
        redisTemplate.opsForValue().set(LOTTERY_CODE + lotteryId + ":" + code, "e", diff, TimeUnit.HOURS);
        lock.readLock().unlock();
        log.info("最终的抽奖码:{}", code);
        return "MMJ-" + code;
    }

    private boolean exist(String code, Integer lotteryId) {
        //LOTTERY_CODE:1856245:MMJ-0256
        String str = redisTemplate.opsForValue().get(LOTTERY_CODE + lotteryId + ":" + code);
        return null != str;
    }

    private static int genCode(int min, int max) {
        return RandomUtil.randomInt(min, max);
    }
}
