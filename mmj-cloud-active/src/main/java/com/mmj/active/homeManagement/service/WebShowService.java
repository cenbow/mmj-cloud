package com.mmj.active.homeManagement.service;

import com.mmj.active.homeManagement.model.GoodClassEx;
import com.mmj.active.homeManagement.model.WebShow;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 页面展示表 服务类
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
public interface WebShowService extends IService<WebShow> {

    List<GoodClassEx> selectGoodClass();

    Object selectCode(String classCode);

    Object updateWebShow(WebShow webShow);

    Object delectByGoodClass(String goodClass);

    void updateIndexCode(String userIdentity);
}
