package com.mmj.notice.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.notice.mapper.NoticePersonMapper;
import com.mmj.notice.model.NoticePerson;
import com.mmj.notice.service.NoticePersonService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息关联用户表 服务实现类
 * </p>
 *
 * @author 陈光复
 * @since 2019-06-15
 */
@Service
public class NoticePersonServiceImpl extends ServiceImpl<NoticePersonMapper, NoticePerson> implements NoticePersonService {

}
