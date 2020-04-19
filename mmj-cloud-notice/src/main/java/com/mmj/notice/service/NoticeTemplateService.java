package com.mmj.notice.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.notice.model.NoticeTemplate;

/**
 * <p>
 * 通知模版表 服务类
 * </p>
 *
 * @author 陈光复
 * @since 2019-06-15
 */
public interface NoticeTemplateService extends IService<NoticeTemplate> {

    NoticeTemplate getNoticeTemp(String model, String type);

    void saveOrUpdate(NoticeTemplate template);

    Page<NoticeTemplate> list(NoticeTemplate dto);

    void on(Integer id);

    void off(Integer id);

    void delete(Integer id);

    void saveAndSend(NoticeTemplate template);

    NoticeTemplate getById(Integer id);
}
