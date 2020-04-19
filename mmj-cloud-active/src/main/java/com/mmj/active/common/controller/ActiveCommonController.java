package com.mmj.active.common.controller;

import com.alibaba.fastjson.JSON;
import com.mmj.active.common.service.WatermarkConfigureService;
import com.mmj.active.seckill.constants.SeckillConstants;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.SecurityUserUtil;
import io.swagger.annotations.ApiOperation;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mmj.active.common.model.dto.CacheDto;
import com.mmj.active.common.service.ShareGoodService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.StringUtils;

@RestController
@RequestMapping("/activeCommon")
public class ActiveCommonController extends BaseController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ShareGoodService asyncGoodTaskService;

    @Autowired
    private WatermarkConfigureService watermarkConfigureService;

    @ApiOperation(value = "redis-setValue")
    @RequestMapping(value = "/cache/setValue", method = RequestMethod.POST)
    public ReturnData<String> setValue(@RequestBody CacheDto cacheDto) {
        String key = cacheDto.getKey();
        if (key.startsWith(SeckillConstants.ACTIVE_SECKILL_USER_ORDER)) {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            Long userId = jwtUser.getUserId();
            key = key.replace(String.valueOf(userId), "");
            redisTemplate.opsForHash().put(key, String.valueOf(userId), cacheDto.getValue());
            redisTemplate.expire(key, cacheDto.getExpire(), TimeUnit.MINUTES);
        } else {
            String uUid = StringUtils.getUUid();
            Integer needId = cacheDto.getNeedId();
            if (needId != null && needId == 1) {
                key = key + uUid;
            }
            Long expire = cacheDto.getExpire();
            if (expire != null) {
                redisTemplate.opsForValue().set(key, cacheDto.getValue(), cacheDto.getExpire(), TimeUnit.MINUTES);
            } else {
                redisTemplate.opsForValue().set(key, cacheDto.getValue());
            }
        }
        return initSuccessObjectResult(key);
    }


    @ApiOperation(value = "redis-getValue")
    @RequestMapping(value = "/cache/getValue/{key}", method = RequestMethod.POST)
    public ReturnData<Object> getValue(@PathVariable String key) {
        if (key.startsWith(SeckillConstants.ACTIVE_SECKILL_USER_ORDER)) {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            Long userId = jwtUser.getUserId();
            return initSuccessObjectResult(redisTemplate.opsForHash().get(key.replace(String.valueOf(userId), ""), String.valueOf(userId)));
        } else {
            return initSuccessObjectResult(redisTemplate.opsForValue().get(key));
        }
    }


    @ApiOperation(value = "redis-delValues")
    @RequestMapping(value = "/cache/delValues/{key}", method = RequestMethod.POST)
    public ReturnData<Object> delValues(@PathVariable String key) {
        redisTemplate.delete(redisTemplate.keys(key + "*"));
        return initSuccessResult();
    }


    @ApiOperation(value = "redis-delValue")
    @RequestMapping(value = "/cache/delValue/{key}", method = RequestMethod.POST)
    public ReturnData<Object> delValue(@PathVariable String key) {
        if (key.startsWith(SeckillConstants.ACTIVE_SECKILL_USER_ORDER)) {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            Long userId = jwtUser.getUserId();
            redisTemplate.opsForHash().delete(key.replace(String.valueOf(userId), ""), String.valueOf(userId));
        } else {
            redisTemplate.delete(key);
        }
        return initSuccessResult();
    }
    
    @ApiOperation(value = "分享商品任务")
    @RequestMapping(value="/shareGood/{shareUserId}/{goodId}", method = RequestMethod.POST)
    public ReturnData<Object> shareGood(@PathVariable Long shareUserId, @PathVariable Integer goodId) {
    	asyncGoodTaskService.sendMsg(shareUserId, goodId);
    	return initSuccessResult();
    }

    @ApiOperation(value = "绘图")
    @RequestMapping(value="/createMark", method = RequestMethod.POST)
    public ReturnData<String> createMark(@RequestBody String  params) {
        watermarkConfigureService.createMark(params);
        return initSuccessResult();
    }

    @ApiOperation(value = "redis-setValue-hash")
    @RequestMapping(value = "/cache/setValueHash", method = RequestMethod.POST)
    public ReturnData<String> setValueHash(@RequestBody CacheDto cacheDto) {
        String key = cacheDto.getKey();
        String hashKey = cacheDto.getHashKey();
        Integer needId = cacheDto.getNeedId();
        if (needId != null && needId == 1) {
            hashKey = hashKey + StringUtils.getUUid();
        }
        Long expire = cacheDto.getExpire();
        if (expire != null) {
            redisTemplate.opsForHash().put(key, hashKey, cacheDto.getValue());
            redisTemplate.expire(key, expire, TimeUnit.MINUTES);
        } else {
            redisTemplate.opsForHash().put(key, hashKey, cacheDto.getValue());
        }
        return initSuccessObjectResult(key);
    }

    @ApiOperation(value = "redis-getValue-hash")
    @RequestMapping(value = "/cache/getValueHash", method = RequestMethod.POST)
    public ReturnData<String> getValueHash(@RequestBody CacheDto cacheDto) {
        String key = cacheDto.getKey();
        String hashKey = cacheDto.getHashKey();
        if (hashKey != null && hashKey.length() != 0) {
            return initSuccessObjectResult(JSON.toJSONString(redisTemplate.opsForHash().get(key, hashKey)));
        } else {
            return initSuccessObjectResult(JSON.toJSONString(redisTemplate.opsForHash().entries(key)));
        }
    }

    @ApiOperation(value = "redis-deleteValue-hash")
    @RequestMapping(value = "/cache/deleteValueHash", method = RequestMethod.POST)
    public ReturnData<String> deleteValueHash(@RequestBody CacheDto cacheDto) {
        String key = cacheDto.getKey();
        String hashKey = cacheDto.getHashKey();
        if (hashKey != null && hashKey.length() != 0) {
            redisTemplate.opsForHash().delete(key, hashKey);
        } else {
            redisTemplate.delete(key);
        }
        return initSuccessResult();
    }
}
