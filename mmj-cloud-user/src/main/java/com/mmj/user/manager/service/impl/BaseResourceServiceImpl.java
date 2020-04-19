package com.mmj.user.manager.service.impl;

import java.util.HashMap;
import java.util.Map;
import com.mmj.user.manager.mapper.BaseResourceMapper;
import com.mmj.user.manager.model.BaseResource;
import com.mmj.user.manager.service.BaseResourceService;
import com.mmj.user.manager.service.BaseRoleResourceService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 资源表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@Service
public class BaseResourceServiceImpl extends ServiceImpl<BaseResourceMapper, BaseResource> implements BaseResourceService {

	@Autowired
	private BaseRoleResourceService baseRoleResourceService;
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public boolean deleteByResId(Integer resId) {
		boolean flag = this.deleteById(resId);
		if(flag) {
			// 删除该资源和角色的关联关系
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("RES_ID", resId);
			baseRoleResourceService.deleteByMap(map);
		}
		return flag;
	}

}
