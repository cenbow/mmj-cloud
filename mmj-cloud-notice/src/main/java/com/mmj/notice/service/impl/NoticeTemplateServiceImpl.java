package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.model.MessageConstants;
import com.mmj.common.model.SmsDto;
import com.mmj.common.utils.DateUtils;
import com.mmj.notice.common.MQProducer;
import com.mmj.notice.common.constants.MessageNode;
import com.mmj.notice.common.constants.SMSConstants;
import com.mmj.notice.mapper.NoticeTemplateMapper;
import com.mmj.notice.model.NoticePerson;
import com.mmj.notice.model.NoticeSendLog;
import com.mmj.notice.model.NoticeTemplate;
import com.mmj.notice.service.NoticePersonService;
import com.mmj.notice.service.NoticeSendLogService;
import com.mmj.notice.service.NoticeTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;

/**
 * <p>
 * 通知模版表 服务实现类
 * </p>
 *
 * @author 陈光复
 * @since 2019-06-15
 */
@Service
@Slf4j
public class NoticeTemplateServiceImpl extends ServiceImpl<NoticeTemplateMapper, NoticeTemplate> implements NoticeTemplateService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private NoticePersonService noticePersonService;

    @Autowired
    private NoticeSendLogService logService;

    @Autowired
    private MQProducer mqProducer;


    @Override
    public NoticeTemplate getNoticeTemp(String model, String type) {
        NoticeTemplate noticeTemplate = new NoticeTemplate();
        noticeTemplate.setDictCode(model);
        noticeTemplate.setNoticeNode(type);
        noticeTemplate.setNoticeStatus(MessageConstants.NoticeStatus.ON);
        EntityWrapper<NoticeTemplate> wrapper = new EntityWrapper<>(noticeTemplate);
        wrapper.orderDesc(Collections.singleton("MODIFY_TIME"));
        return selectOne(wrapper);
    }

    @Override
    public void saveAndSend(NoticeTemplate template) {
        Assert.notNull(template, "模板不能为空");
        Integer id = null;
        if (null == template.getNoticeName())
            template.setNoticeName(MessageNode.getMsgByCode(template.getDictCode()));
        if (template.getSendType() == MessageConstants.SendType.NOW)
            template.setSendAfter(0);
        if (null == template.getNoticeId()) {
            save(template);
        } else {
            update(template);
        }
        id = template.getNoticeId();

        if (template.getNoticeType() == MessageConstants.NoticeType.DEFINE &&
                null != template.getPersonList() && template.getPersonList().size() > 0) {
            for (NoticePerson p : template.getPersonList()) {
                p.setNoticeId(id);
            }
            noticePersonService.insertBatch(template.getPersonList());
            //发送短信
            log.info("保存模板并发送短信:{}", JSON.toJSONString(template.getPersonList()));
            for (NoticePerson person : template.getPersonList()) {
                SmsDto smsDto = new SmsDto();
                smsDto.setNeedTemplate(false);
                smsDto.setPhone(person.getPhone());
                smsDto.setNickName(person.getUserName());
                smsDto.setTemplate(template.getMsgTemplate());
                smsDto.setEndTime(template.getSendTime());
                smsDto.setNoticeId(id);
                mqProducer.sendSmsMsg(smsDto);
            }
        }
    }

    @Override
    public NoticeTemplate getById(Integer id) {
        NoticeTemplate template = this.selectById(id);
        if (template.getNoticeType() == MessageConstants.NoticeType.DEFINE) {
            //自定义消息查询用户
            template.setUserNames(this.getUserNames(id));
        }
        return template;
    }

    private String getUserNames(Integer id) {
        EntityWrapper<NoticePerson> entityWrapper = new EntityWrapper<>();
        entityWrapper.setSqlSelect("GROUP_CONCAT(USER_NAME)");
        entityWrapper.where("NOTICE_ID= " + id);
        entityWrapper.groupBy("NOTICE_ID");
        Object obj = noticePersonService.selectObj(entityWrapper);
        if (obj == null)
            return null;
        return obj.toString();
    }

    @Override
    @Transactional
    public void saveOrUpdate(NoticeTemplate template) {
        Assert.notNull(template, "模板不能为空");
        /*
        BaseDict dict = new BaseDict();
        dict.setDictCode(template.getDictCode());
        EntityWrapper<BaseDict> wrapper = new EntityWrapper<BaseDict>();
        wrapper.setEntity(dict);
        BaseDict baseDict = baseDictService.selectOne(wrapper);
        if (null != baseDict) {
            template.setDictCode(baseDict.getDictCode());
            template.setDictValue(baseDict.getDictValue());
        }
        */
        Integer id = null;
        if (null == template.getNoticeName())
            template.setNoticeName(MessageNode.getMsgByCode(template.getDictCode()));
        if (template.getSendType() == MessageConstants.SendType.NOW)
            template.setSendAfter(0);
        if (null == template.getNoticeId()) {
            save(template);
        } else {
            update(template);
        }
        id = template.getNoticeId();

        if (template.getNoticeType() == MessageConstants.NoticeType.DEFINE &&
                null != template.getPersonList() && template.getPersonList().size() > 0) {
            for (NoticePerson p : template.getPersonList()) {
                p.setNoticeId(id);
            }
            noticePersonService.insertBatch(template.getPersonList());
        }
    }

    @Override
    public Page<NoticeTemplate> list(NoticeTemplate template) {
        EntityWrapper<NoticeTemplate> wrapper = new EntityWrapper<>();
        if (StringUtils.isNotBlank(template.getSendTimeStart()))
            wrapper.ge("SEND_TIME", template.getSendTimeStart());
        if (StringUtils.isNotBlank(template.getSendTimeEnd()))
            wrapper.le("SEND_TIME", template.getSendTimeEnd());
        if (StringUtils.isNotBlank(template.getNoticeName()))
            wrapper.like("NOTICE_NAME", template.getNoticeName());
        if (null != template.getNoticeType())
            wrapper.eq("NOTICE_TYPE", template.getNoticeType());

        Page<NoticeTemplate> page = new Page<>(template.getCurrentPage(), template.getPageSize());
        wrapper.orderDesc(Collections.singleton("CREATER_TIME"));
        Page<NoticeTemplate> result = selectPage(page, wrapper);
        for (NoticeTemplate noticeTemplate : result.getRecords()) {
            noticeTemplate.setFailAll(getSendCount(noticeTemplate.getNoticeId(), false, true));
            noticeTemplate.setFailToday(getSendCount(noticeTemplate.getNoticeId(), true, true));
            noticeTemplate.setTotalAll(getSendCount(noticeTemplate.getNoticeId(), false, false));
            noticeTemplate.setTotalToday(getSendCount(noticeTemplate.getNoticeId(), true, false));
            if (noticeTemplate.getNoticeType() == MessageConstants.NoticeType.DEFINE) {
                //自定义消息查询用户
                noticeTemplate.setUserNames(this.getUserNames(noticeTemplate.getNoticeId()));
            }
        }
        return result;
    }


    private int getSendCount(Integer id, boolean isTaday, boolean isFail) {
        EntityWrapper<NoticeSendLog> wrapper = new EntityWrapper<>();
        wrapper.eq("NOTICE_ID", id);
        if (isFail)
            wrapper.eq("SEND_STATUS", "004");
        if (isTaday) {
            wrapper.ge("CREATE_TIME", DateUtils.getNowDate(DateUtils.DATE_PATTERN_10) + " 00:00:00");
            wrapper.le("CREATE_TIME", DateUtils.getNowDate(DateUtils.DATE_PATTERN_10) + " 23:59:59");
        }
        return logService.selectCount(wrapper);
    }

    @Override
    @Transactional
    public void on(Integer id) {
        NoticeTemplate nt = selectById(id);
        if (null == nt)
            return;

        NoticeTemplate template = new NoticeTemplate();
        template.setNoticeId(id);
        template.setNoticeStatus(1);
        updateById(template);
        redisTemplate.delete(redisTemplate.keys(SMSConstants.redisTemplateKey + "*"));
    }

    @Override
    @Transactional
    public void off(Integer id) {
        NoticeTemplate nt = selectById(id);
        if (null == nt)
            return;
        NoticeTemplate template = new NoticeTemplate();
        template.setNoticeId(id);
        template.setNoticeStatus(0);
        updateById(template);
        redisTemplate.delete(redisTemplate.keys(SMSConstants.redisTemplateKey + "*"));
    }

    @Override
    public void delete(Integer id) {
        NoticeTemplate nt = selectById(id);
        if (null == nt)
            return;
        deleteById(id);
        redisTemplate.delete(redisTemplate.keys(SMSConstants.redisTemplateKey + "*"));
    }

    private void save(NoticeTemplate template) {
        insert(template);
    }


    private void update(NoticeTemplate template) {
        Assert.notNull(template.getNoticeId(), "模板id不能为空");
        boolean result = updateById(template);
        if (result = true) {
            //删除之前的用户
            EntityWrapper<NoticePerson> wrapper = new EntityWrapper<>();
            wrapper.eq("NOTICE_ID", template.getNoticeId());
            noticePersonService.delete(wrapper);
        }
        redisTemplate.delete(redisTemplate.keys(SMSConstants.redisTemplateKey + "*"));
    }
}
