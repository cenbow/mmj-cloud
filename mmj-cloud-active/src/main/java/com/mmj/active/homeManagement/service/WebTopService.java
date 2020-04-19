package com.mmj.active.homeManagement.service;

import com.mmj.active.homeManagement.model.WebTop;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.homeManagement.model.WebTopEx;

/**
 * <p>
 * 顶部配置表 服务类
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
public interface WebTopService extends IService<WebTop> {

    Object save(WebTop entity);

    WebTopEx selectWebTop(String classCode,Long userid);

    WebTopEx query(String classCode);

    boolean deleteByTopId(Integer topId);

}
