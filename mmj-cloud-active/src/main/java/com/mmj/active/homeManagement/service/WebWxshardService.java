package com.mmj.active.homeManagement.service;

import com.mmj.active.homeManagement.model.WebWxshard;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.homeManagement.model.WebWxshardEx;

import java.util.Map;

/**
 * <p>
 * 小程序分享配置 服务类
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
public interface WebWxshardService extends IService<WebWxshard> {

    Map<String, Object> selectWebWxshard(String classCode);

    Object save(WebWxshardEx entity);

    Map<String, Object> query(String classCode);
}
