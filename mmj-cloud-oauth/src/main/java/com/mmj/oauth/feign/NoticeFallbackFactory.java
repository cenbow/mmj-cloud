package com.mmj.oauth.feign;

import org.springframework.stereotype.Component;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;

import feign.hystrix.FallbackFactory;

@Component
public class NoticeFallbackFactory implements FallbackFactory<NoticeFeignClient> {

	@Override
	public NoticeFeignClient create(Throwable cause) {
		return new NoticeFeignClient() {
			
			@Override
			public ReturnData<Object> sendSms(String params) {
				throw new BusinessException("调用发送短信接口报错," + cause.getMessage(), 500);
			}
		};
	}


}
