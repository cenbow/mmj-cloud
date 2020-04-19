package com.mmj.active.common.feigin;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WxConfigFallbackFactory implements FallbackFactory<WxConfigFeignClient> {
    
    private final Logger logger = LoggerFactory.getLogger(WxConfigFallbackFactory.class);

	@Override
	public WxConfigFeignClient create(Throwable cause) {
		logger.info("WxConfigFallbackFactory error message is {}", cause.getMessage());
		return new WxConfigFeignClient() {
			
			@Override
			public ReturnData<WxConfig> queryByWxNo(String wxNo) {
				throw new BusinessException("根据wxNo获取微信配置接口报错: " + cause.getMessage(), 500);
			}
			
			@Override
			public ReturnData<List<WxConfig>> queryByAppType(String type) {
				throw new BusinessException("根据type获取微信配置接口报错: " + cause.getMessage(), 500);
			}
			
			@Override
			public ReturnData<WxConfig> queryByAppId(String appId) {
				throw new BusinessException("根据appId获取微信配置接口报错: " + cause.getMessage(), 500);
			}
		};
	}


}
