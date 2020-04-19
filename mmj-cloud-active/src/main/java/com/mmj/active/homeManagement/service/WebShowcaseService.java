package com.mmj.active.homeManagement.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.homeManagement.model.WebShowcase;
import com.mmj.active.homeManagement.model.WebShowcaseEx;
import com.mmj.common.model.ReturnData;

import java.util.List;

/**
 * <p>
 * 橱窗配置表 服务类
 * </p>
 *
 * @author dashu
 * @since 2019-06-09
 */
public interface WebShowcaseService extends IService<WebShowcase> {

    ReturnData<Object> save(WebShowcaseEx webShowcaseEx);

    List<WebShowcaseEx> queryByGoodClass(String goodClass);

    List<Object> selectWebShowcase(String goodClass, Long userid);

    boolean deleteWebShowcase(Integer showecaseId);

    WebShowcaseEx selectByShowecaseId(Integer showecaseId);

}
