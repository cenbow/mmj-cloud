package com.mmj.notice.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.notice.model.BaseDict;

/**
 * <p>
 * 数据字典表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-18
 */
public interface BaseDictService extends IService<BaseDict> {
	
	List<BaseDict> queryByDictType(String dictType);
	
	BaseDict queryByDictTypeAndCode(String dictType, String dictCode);
	
	BaseDict queryGlobalConfigByDictCode(String dictCode);

	Integer saveBaseDict(BaseDict entity);
}
