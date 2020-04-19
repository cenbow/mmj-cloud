package com.mmj.good.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.good.model.BaseDict;

/**
 * <p>
 * 数据字典表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-21
 */
public interface BaseDictService extends IService<BaseDict> {

    String getDictCode(Integer parentId, String dictType) throws Exception;

}
