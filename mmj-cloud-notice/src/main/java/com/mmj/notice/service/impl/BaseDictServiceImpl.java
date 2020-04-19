package com.mmj.notice.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.SecurityUserUtil;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.notice.mapper.BaseDictMapper;
import com.mmj.notice.model.BaseDict;
import com.mmj.notice.service.BaseDictService;

/**
 * <p>
 * 数据字典表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-18
 */
@Slf4j
@Service
public class BaseDictServiceImpl extends ServiceImpl<BaseDictMapper, BaseDict> implements BaseDictService {
	
	private static final String DICT_TYPE_GLOBALCONFIG = "GLOBAL_CONFIG";
	
	private static final String COLUNM_DICT_TYPE = "DICT_TYPE";
	private static final String COLUMN_DICT_CODE = "DICT_CODE";
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Override
	public List<BaseDict> queryByDictType(String dictType) {
		log.info("-->根据dictType获取数据字典配置，参数:{}", dictType);
		String cacheKey = "DICT:TYPE:" + dictType;
		String cacheValue = redisTemplate.opsForValue().get(cacheKey);
		if(StringUtils.isNotBlank(cacheValue)) {
			return JSONArray.parseArray(cacheValue, BaseDict.class);
		}
		Wrapper<BaseDict> wrapper = new EntityWrapper<BaseDict>();
		wrapper.eq(COLUNM_DICT_TYPE, dictType);
		List<BaseDict> list = this.selectList(wrapper);
		if(!list.isEmpty()) {
			String jsonString = JSONObject.toJSONString(list, SerializerFeature.WriteMapNullValue);
			redisTemplate.opsForValue().set(cacheKey, jsonString, 7, TimeUnit.DAYS);
		}
		return list;
	}

	@Override
	public BaseDict queryByDictTypeAndCode(String dictType, String dictCode) {
		log.info("-->根据dictType和dictCode获取数据字典配置，参数:{}", dictType, dictCode);
		BaseDict baseDict = null;
		StringBuilder cacheKey = new StringBuilder();
		cacheKey.append("DICT:TYPE&CODE:");
		cacheKey.append(dictType);
		cacheKey.append(":");
		cacheKey.append(dictCode);
		String cacheValue = redisTemplate.opsForValue().get(cacheKey.toString());
		if(StringUtils.isNotBlank(cacheValue)) {
			baseDict = JSONObject.parseObject(cacheValue, BaseDict.class);
			log.info("-->根据{}和{}从缓存中取到的字典值为:{}", dictType, dictCode, baseDict.getDictValue());
		} else {
			Wrapper<BaseDict> wrapper = new EntityWrapper<BaseDict>();
			wrapper.eq(COLUNM_DICT_TYPE, dictType);
			wrapper.eq(COLUMN_DICT_CODE, dictCode);
			baseDict = this.selectOne(wrapper);
			if(baseDict != null) {
				log.info("-->根据{}和{}从数据库中取到的字典值为:{}", dictType, dictCode, baseDict.getDictValue());
				String jsonString = JSONObject.toJSONString(baseDict, SerializerFeature.WriteMapNullValue);
				redisTemplate.opsForValue().set(cacheKey.toString(), jsonString, 7, TimeUnit.DAYS);
			}
		}
		
		
		return baseDict;
	}

	@Override
	public BaseDict queryGlobalConfigByDictCode(String dictCode) {
		log.info("-->根据dictCode获取数据全局配置，参数:{}", dictCode);
		return this.queryByDictTypeAndCode(DICT_TYPE_GLOBALCONFIG, dictCode);
	}


	@Override
	public Integer saveBaseDict(BaseDict entity) {
		JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
		if(null == entity.getDictId()){
			entity.setDelFlag(0);
			entity.setCreaterId(userDetails.getUserId());
			baseMapper.insert(entity);
		}else{
			entity.setModifyId(userDetails.getUserId());
			baseMapper.updateById(entity);
		}

		//清楚缓存
		StringBuilder cacheKey = new StringBuilder();
		cacheKey.append("DICT:TYPE&CODE:");
		cacheKey.append(DICT_TYPE_GLOBALCONFIG);
		cacheKey.append(":");
		cacheKey.append(entity.getDictCode());
		redisTemplate.delete(cacheKey.toString());
		return entity.getDictId();
	}

}
