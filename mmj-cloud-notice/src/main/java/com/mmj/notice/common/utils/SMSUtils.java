package com.mmj.notice.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.model.MessageConstants;
import com.mmj.common.model.SmsDto;
import com.mmj.notice.common.constants.SMSConstants;
import com.mmj.notice.model.NoticeDelaySms;
import com.mmj.notice.model.NoticePerson;
import com.mmj.notice.model.NoticeTemplate;
import com.mmj.notice.service.DelaySmsService;
import com.mmj.notice.service.NoticeTemplateService;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class SMSUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private NoticeTemplateService templateService;

    @Autowired
    private DelaySmsService delaySmsService;

    private static String getSms(String code, String template, Map<String, Object> params) {
        if (StringUtils.isBlank(code) || StringUtils.isBlank(template) ||
                null == params || params.size() == 0)
            return template;
        return processFreemarker(template, params, SMSConstants.redisLocalKey + code);
    }

    private static Date getDelayTime(Long delay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());//可以省略，默认就是当前时间
        calendar.add(Calendar.MINUTE, delay.intValue());
        return calendar.getTime();
    }

    private static Configuration getFreemarkerConf() {
        return InnerSingletion.freemarkerConf;
    }

    private static String processFreemarker(String template, Map<String, Object> params, String name) {
        try (StringWriter result = new StringWriter()) {
            Template tpl = createTemplate(name, template);
            tpl.process(params, result);
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Template createTemplate(String name, String templateContent) throws Exception {
        // 获取模板加载器
        TemplateLoader templateLoader = getFreemarkerConf().getTemplateLoader();
        Template template = null;
        try {
            template = getFreemarkerConf().getTemplate(name, "UTF-8");
        } catch (Exception e) {
            log.error("模板不存在，初始化模板。");
        }

        if (template == null) {
            ((StringTemplateLoader) templateLoader).putTemplate(name, templateContent);
            getFreemarkerConf().clearTemplateCache();
            template = getFreemarkerConf().getTemplate(name, "UTF-8");
        }
        return template;
    }

    private static boolean isPhone(String phone) {
        if (StringUtils.isBlank(phone))
            return false;
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            return m.matches();
        }
    }

    /**
     * @param dto 消息模块
     */
    public void send(SmsDto dto) {
        log.info("开始消费短信:{}", JSON.toJSONString(dto));
        if (null == dto)
            return;
        //校验手机号
        if (!isPhone(dto.getPhone())) {
            log.info("手机号正则匹配失败:{}", dto.getPhone());
            return;
        }

        //获取模板
        String msgTemp;
        NoticeTemplate template = null;
        StringBuilder sb;
        if (dto.isNeedTemplate()) {
            String key = SMSConstants.redisTemplateKey + dto.getMsgType() + ":" + dto.getModel() + ":" + dto.getType();
            Object obj = redisTemplate.opsForValue().get(key);
            if (null == obj) {
                template = templateService.getNoticeTemp(dto.getModel(), dto.getType());
                if (null == template) {
                    log.error("发送短信,短信模板不存在:{}", dto);
                    return;
                }
                redisTemplate.opsForValue().set(key, JSONObject.toJSONString(template), 3, TimeUnit.HOURS);
            } else {
                template = JSONObject.parseObject(obj.toString(), NoticeTemplate.class);
            }
            log.info("消息模板:{}", JSON.toJSONString(template));

            int afterTime = template.getSendAfter();

            dto.setDelayTime((long) afterTime);

            if (MessageConstants.NoticeStatus.OFF == template.getNoticeStatus()) {
                log.info("{},{} 模板已关闭", template.getNoticeId(), template.getNoticeName());
                return;
            }
            msgTemp = getSms(dto.getNode(), template.getMsgTemplate(), dto.getSmsParams());
            sb = new StringBuilder(SMSConstants.smsAcount);
        } else {
            //自定义消息不需要模板
            msgTemp = dto.getTemplate();
            sb = new StringBuilder(SMSConstants.smsAcountDelay);
        }
        log.info("发送短息内容:{}", msgTemp);

        if (StringUtils.isBlank(msgTemp))
            return;

        String msg;
        try {
            msg = URLEncoder.encode(msgTemp + ",退订回T【买买家】", "GBK");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            return;
        }
        sb.append(dto.getPhone()).append("&msgid=").append(getMsgId()).append("&msg=").append(msg);


        //短信新增到数据库,等待发送
        NoticeDelaySms noticeDelaySms = new NoticeDelaySms();
        noticeDelaySms.setModel(dto.getModel());
        noticeDelaySms.setType(dto.getType());
        noticeDelaySms.setDelayTime(dto.getDelayTime());
        if (template == null) {
            //自定义消息设置发送时间
            noticeDelaySms.setSendTime(null == dto.getEndTime() ? new Date() : dto.getEndTime());
            noticeDelaySms.setNoticeId(dto.getNoticeId());
            noticeDelaySms.setNeedTemplate(1);
        } else {
            noticeDelaySms.setSendTime(getDelayTime(dto.getDelayTime()));
            noticeDelaySms.setNoticeId(template.getNoticeId());
            noticeDelaySms.setNeedTemplate(0);
        }
        noticeDelaySms.setContent(sb.toString());
        noticeDelaySms.setOrderNo(dto.getOrderNo());
        noticeDelaySms.setUserId(dto.getUserId());
        noticeDelaySms.setNode(dto.getNode());
        noticeDelaySms.setPhone(dto.getPhone());
        noticeDelaySms.setRecvName(dto.getNickName());
        noticeDelaySms.setCreateTime(new Date());
        delaySmsService.insert(noticeDelaySms);
    }

    public void recevieSms(String xml) {
        log.info("------recevieSms:" + xml);
        try {
            Document doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            Element list = rootElt.element("list");
            if (null == list)
                return;

            Element pushStatusForm = list.element("pushStatusForm");
            if (null == pushStatusForm)
                return;

            Element userId = pushStatusForm.element("userId");

            if (null == userId || !SMSConstants.uid.equals(userId.getText()))
                return;

            Element status = pushStatusForm.element("status");
            if (null == status || StringUtils.isBlank(status.getText()))
                return;

            Element msgId = pushStatusForm.element("msgId");
            if (null == msgId || StringUtils.isBlank(msgId.getText()))
                return;


            NoticePerson person = new NoticePerson();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getMsgId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    private static class InnerSingletion {
        private static Configuration freemarkerConf = new Configuration();

        static {
            freemarkerConf.setTemplateLoader(new StringTemplateLoader());
        }
    }
}
