package com.mmj.oauth.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.common.model.WxConfig;

/**
 * <p>
 * 微信信息表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-17
 */
public interface WxConfigService extends IService<WxConfig> {
	
	WxConfig queryByAppId(String appId);
	
	List<WxConfig> queryByType(String type);

}
