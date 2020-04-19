package com.mmj.user.manager.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.manager.model.BaseResource;

/**
 * <p>
 * 资源表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
public interface BaseResourceService extends IService<BaseResource> {
	
	boolean deleteByResId(Integer resId);

}
