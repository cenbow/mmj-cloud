package com.mmj.active.prizewheels.service.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.prizewheels.mapper.PrizewheelsTemplateMapper;
import com.mmj.active.prizewheels.model.PrizewheelsTemplate;
import com.mmj.active.prizewheels.service.PrizewheelsTemplateService;
import com.mmj.common.exception.CustomException;

/**
 * <p>
 * 幸运大转盘-活动配置表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
@Service
public class PrizewheelsTemplateServiceImpl extends ServiceImpl<PrizewheelsTemplateMapper, PrizewheelsTemplate> implements PrizewheelsTemplateService {
	
	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	
	private static final String TEMPLATE_CACHE_KEY = "PRIZEWHEELS:TEMPLATE";

	@Override
	public PrizewheelsTemplate load() {
		String cacheValue = redisTemplate.opsForValue().get(TEMPLATE_CACHE_KEY);
		if (StringUtils.isNotBlank(cacheValue)) {
			return JSONObject.parseObject(cacheValue, PrizewheelsTemplate.class);
		}
		// 转盘活动配置表ID内置为1， 不作更改
		int templateId = 1;
		PrizewheelsTemplate template = this.selectById(templateId);
		if(template == null) {
			throw new CustomException("转盘活动配置异常");
		}
		String jsonString = JSONObject.toJSONString(template, SerializerFeature.WriteMapNullValue);
		redisTemplate.opsForValue().set(TEMPLATE_CACHE_KEY, jsonString, 7, TimeUnit.DAYS);
		return template;
	}

	@Override
	public boolean queryIsOpen() {
		PrizewheelsTemplate template = this.load();
		return template != null ? template.getIsOpen() : false;
	}

}
