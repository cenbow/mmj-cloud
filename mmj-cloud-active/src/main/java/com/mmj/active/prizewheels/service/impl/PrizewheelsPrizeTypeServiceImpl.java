package com.mmj.active.prizewheels.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.prizewheels.mapper.PrizewheelsPrizeTypeMapper;
import com.mmj.active.prizewheels.model.PrizewheelsPrizeType;
import com.mmj.active.prizewheels.service.PrizewheelsPrizeTypeService;

/**
 * <p>
 * 转盘活动 - 奖励配置(包含概率)表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
@Service
public class PrizewheelsPrizeTypeServiceImpl extends ServiceImpl<PrizewheelsPrizeTypeMapper, PrizewheelsPrizeType> implements PrizewheelsPrizeTypeService {
	
	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	
	private static final String PRIZETYPE_CACHE_KEY = "PRIZEWHEELS:PRIZETYPE";

	@Override
	public List<PrizewheelsPrizeType> loadAllPrize() {
		String cacheValue = redisTemplate.opsForValue().get(PRIZETYPE_CACHE_KEY);
		if (StringUtils.isNotBlank(cacheValue)) {
			return JSONArray.parseArray(cacheValue, PrizewheelsPrizeType.class);
		}
		EntityWrapper<PrizewheelsPrizeType> wrapper = new EntityWrapper<PrizewheelsPrizeType>();
		wrapper.orderBy("sort");
		List<PrizewheelsPrizeType> list = this.selectList(wrapper);
		if(!list.isEmpty()) {
			String jsonString = JSONObject.toJSONString(list, SerializerFeature.WriteMapNullValue);
			redisTemplate.opsForValue().set(PRIZETYPE_CACHE_KEY, jsonString, 7, TimeUnit.DAYS);
		}
		return list;
	}

}
