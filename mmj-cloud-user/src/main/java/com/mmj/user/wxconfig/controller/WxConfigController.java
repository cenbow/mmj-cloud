package com.mmj.user.wxconfig.controller;

import io.swagger.annotations.ApiOperation;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.user.wxconfig.model.WxConfig;
import com.mmj.user.wxconfig.service.WxConfigService;

@Slf4j
@RequestMapping("/wx/config")
@RestController
public class WxConfigController extends BaseController {
	
	@Autowired
	private WxConfigService wxConfigService;
	
	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	
	private static final String CACHEKEY_APPID_PREFIX = "APP:SECRET:INFO:";
	private static final String CACHEKEY_TYPE_PREFIX = "APP:SECRET:INFOS:";
	private static final String APP_ID = "app_id";
	private static final String TYPE = "type";
	private static final String WX_NO = "wx_no";
	
	@ApiOperation(value="根据appid查询app配置")
    @RequestMapping(value="/queryByAppId/{appId}", method=RequestMethod.POST)
    public ReturnData<WxConfig> queryByAppId(@PathVariable String appId){
    	log.info("-->/wx/config/queryByAppId-->根据appId获取app配置信息，参数：{}", appId);
    	WxConfig wxConfig = null;
    	String cacheKey = CACHEKEY_APPID_PREFIX + appId;
    	String cacheValue = redisTemplate.opsForValue().get(cacheKey);
    	if(StringUtils.isNotBlank(cacheValue)) {
    		wxConfig = JSONObject.parseObject(cacheValue, WxConfig.class);
    	} else {
    		EntityWrapper<WxConfig> wrapper = new EntityWrapper<WxConfig>();
        	wrapper.eq(APP_ID, appId);
        	List<WxConfig> list = wxConfigService.selectList(wrapper);
        	if(!list.isEmpty()) {
        		wxConfig = list.get(0);
        		redisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(wxConfig, SerializerFeature.WriteMapNullValue));
        	} 
    	}
    	return initSuccessObjectResult(wxConfig);
    }

	@ApiOperation(value="根据wxNo查询app配置")
	@RequestMapping(value="/queryByWxNo/{wxNo}", method=RequestMethod.POST)
    public ReturnData<WxConfig> queryByWxNo(@PathVariable String wxNo){
    	log.info("-->/wx/config/queryByWxNo-->根据wxNo获取app配置信息，参数：{}", wxNo);
    	WxConfig wxConfig = null;
    	String cacheKey = CACHEKEY_APPID_PREFIX + wxNo;
    	String cacheValue = redisTemplate.opsForValue().get(cacheKey);
    	if(StringUtils.isNotBlank(cacheValue)) {
    		wxConfig = JSONObject.parseObject(cacheValue, WxConfig.class);
    	} else {
    		EntityWrapper<WxConfig> wrapper = new EntityWrapper<WxConfig>();
        	wrapper.eq(WX_NO, wxNo);
        	wxConfig = wxConfigService.selectOne(wrapper);
        	if(wxConfig != null) {
        		redisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(wxConfig, SerializerFeature.WriteMapNullValue));
        	}
    	}
    	return initSuccessObjectResult(wxConfig);
    }
	
	@ApiOperation(value="根据type查询app配置")
	@RequestMapping(value="/queryByType/{type}", method=RequestMethod.POST)
    public ReturnData<List<WxConfig>> queryByAppType(@PathVariable String type){
    	log.info("-->/wx/config/queryByType-->根据type获取app配置信息，参数：{}", type);
    	List<WxConfig> wxConfigList = null;
    	String cacheKey = CACHEKEY_TYPE_PREFIX + type;
    	String cacheValue = redisTemplate.opsForValue().get(cacheKey);
    	if(StringUtils.isNotBlank(cacheValue)) {
    		wxConfigList = JSONArray.parseArray(cacheValue, WxConfig.class);
    	} else {
    		EntityWrapper<WxConfig> wrapper = new EntityWrapper<WxConfig>();
        	wrapper.eq(TYPE, type);
        	wxConfigList = wxConfigService.selectList(wrapper);
        	if(!wxConfigList.isEmpty()) {
        		redisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(wxConfigList, SerializerFeature.WriteMapNullValue));
        	}
    	}
    	return initSuccessObjectResult(wxConfigList);
    }
}
