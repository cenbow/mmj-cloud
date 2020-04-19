package com.mmj.oauth.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.exception.CustomException;
import com.mmj.common.model.WxConfig;
import com.mmj.oauth.mapper.WxConfigMapper;
import com.mmj.oauth.service.WxConfigService;

/**
 * <p>
 * 微信信息表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-15
 */
@Slf4j
@Service
public class WxConfigServiceImpl extends ServiceImpl<WxConfigMapper, WxConfig> implements WxConfigService {
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	private static final String CACHEKEY_APPID_PREFIX = "APP:SECRET:INFO:";
	private static final String CACHEKEY_TYPE_PREFIX = "APP:SECRET:INFOS:";
	private static final String APP_ID = "app_id";
	private static final String TYPE = "type";

	@Override
	public WxConfig queryByAppId(String appId) {
		log.info("-->queryByAppId-->根据appId获取app配置信息，参数：{}", appId);
    	WxConfig wxConfig = null;
    	String cacheKey = CACHEKEY_APPID_PREFIX + appId;
    	String cacheValue = redisTemplate.opsForValue().get(cacheKey);
    	if(StringUtils.isNotBlank(cacheValue)) {
    		wxConfig = JSONObject.parseObject(cacheValue, WxConfig.class);
    	} else {
    		EntityWrapper<WxConfig> wrapper = new EntityWrapper<WxConfig>();
        	wrapper.eq(APP_ID, appId);
        	List<WxConfig> list = this.selectList(wrapper);
        	if(!list.isEmpty()) {
        		wxConfig = list.get(0);
        		redisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(wxConfig, SerializerFeature.WriteMapNullValue));
        	} else {
        		log.error("-->appid未配置：{}", appId);
    			throw new CustomException("appid未配置");
        	}
    	}
		return wxConfig;
	}

	@Override
	public List<WxConfig> queryByType(String type) {
		log.info("-->queryByType-->根据type获取app配置信息，参数：{}", type);
    	List<WxConfig> wxConfigList = null;
    	String cacheKey = CACHEKEY_TYPE_PREFIX + type;
    	String cacheValue = redisTemplate.opsForValue().get(cacheKey);
    	if(StringUtils.isNotBlank(cacheValue)) {
    		wxConfigList = JSONArray.parseArray(cacheValue, WxConfig.class);
    	} else {
    		EntityWrapper<WxConfig> wrapper = new EntityWrapper<WxConfig>();
        	wrapper.eq(TYPE, type);
        	wxConfigList = this.selectList(wrapper);
        	if(!wxConfigList.isEmpty()) {
        		redisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(wxConfigList, SerializerFeature.WriteMapNullValue));
        	} else {
        		log.error("-->根据type获取app配置信息，appid未配置：{}", type);
    			throw new CustomException("appid未配置");
        	}
    	}
		return wxConfigList;
	}

}
