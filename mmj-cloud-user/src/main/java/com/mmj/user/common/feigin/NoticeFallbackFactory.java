package com.mmj.user.common.feigin;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.BaseDict;
import com.mmj.common.model.ReturnData;

import feign.hystrix.FallbackFactory;

@Component
public class NoticeFallbackFactory implements FallbackFactory<NoticeFeignClient> {
    
    private final Logger logger = LoggerFactory.getLogger(NoticeFallbackFactory.class);

    @Override
    public NoticeFeignClient create(Throwable cause) {
        logger.info("NoticeFallbackFactory error message is {}", cause.getMessage());
        return new NoticeFeignClient() {

			@Override
			public ReturnData<List<BaseDict>> queryByDictType(String dictType) {
				throw new BusinessException("调用获取数据字典接口queryByDictType报错," + cause.getMessage(), 500);
			}

			@Override
			public ReturnData<BaseDict> queryByDictTypeAndCode(String dictType,
					String dictCode) {
				throw new BusinessException("调用获取数据字典接口queryByDictTypeAndCode报错," + cause.getMessage(), 500);
			}

			@Override
			public ReturnData<BaseDict> queryGlobalConfigByDictCode(
					String dictCode) {
				throw new BusinessException("调用获取数据字典接口queryGlobalConfigByDictCode报错," + cause.getMessage(), 500);
			}

			@Override
			public ReturnData<Object> sendSms(String params) {
				throw new BusinessException("调用发送短信接口报错," + cause.getMessage(), 500);
			}

			@Override
			public ReturnData<Integer> saveBaseDict(BaseDict entity) {
				throw new BusinessException("调用修改数据字典接口报错," + cause.getMessage(), 500);
			}

			@Override
			public ReturnData<JSONObject> sendCustom(String msg) {
				throw new BusinessException("发送微信客服消息失败," + cause.getMessage(), 500);
			}
		};

    }
}
