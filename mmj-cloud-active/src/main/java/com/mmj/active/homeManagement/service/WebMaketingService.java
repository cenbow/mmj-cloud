package com.mmj.active.homeManagement.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.homeManagement.model.WebMaketing;
import com.mmj.active.homeManagement.model.WebMaketingEx;

/**
 * <p>
 * 营销配置表 服务类
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
public interface WebMaketingService extends IService<WebMaketing> {

    Object save(WebMaketing webMaketing);

    WebMaketingEx selectWebMaketing(String classCode,Long userid);

    WebMaketingEx query(String classCode);

    WebMaketing selectByMaketId(Integer maketId);

    boolean deleteByMaketId(Integer maketId);

    void deleteReids(WebMaketing webMaketing);
}
