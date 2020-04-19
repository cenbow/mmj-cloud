package com.mmj.notice.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.model.NoticeTemplate;
import com.mmj.notice.service.NoticeTemplateService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 通知模版表 前端控制器
 * </p>
 *
 * @author 陈光复
 * @since 2019-06-15
 */
@RestController
@RequestMapping("/noticeTemplate")
@Slf4j
public class NoticeTemplateController extends BaseController {

    @Autowired
    private NoticeTemplateService templateService;

    @ApiOperation("新增或编辑模板")
    @PostMapping("/saveOrUpdate")
    public ReturnData<Object> saveOrUpdate(@RequestBody NoticeTemplate template) {
        try {
            templateService.saveOrUpdate(template);
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }


    @ApiOperation("新增模板并发送短信")
    @PostMapping("/saveAndSend")
    public ReturnData<Object> saveAndSend(@RequestBody NoticeTemplate template) {
        try {
            templateService.saveAndSend(template);
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initExcetionObjectResult(e.getMessage());
        }
    }

    @ApiOperation("根据id查询")
    @PostMapping("/get/{id}")
    public ReturnData<NoticeTemplate> get(@PathVariable Integer id) {
        try {
            return initSuccessObjectResult(templateService.getById(id));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("启用模板")
    @PostMapping("/on/{id}")
    public ReturnData<Object> on(@PathVariable Integer id) {
        try {
            templateService.on(id);
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("停用模板")
    @PostMapping("/off/{id}")
    public ReturnData<Object> off(@PathVariable Integer id) {
        try {
            templateService.off(id);
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("删除模板")
    @PostMapping("/delete/{id}")
    public ReturnData<Object> delete(@PathVariable Integer id) {
        try {
            templateService.delete(id);
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("获取模板列表")
    @PostMapping("/list")
    public ReturnData<Page<NoticeTemplate>> list(@RequestBody NoticeTemplate template) {
        try {
            if (template.getCurrentPage() < 1)
                template.setCurrentPage(1);
            if (template.getPageSize() <= 0)
                template.setPageSize(10);

            return initSuccessObjectResult(templateService.list(template));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }
/*
    private final static String SMS_TOPIC = "mmj-auto-sms";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @GetMapping("/sendSMS")
    public String sendSMS() {
        Long key = snowflakeIdWorker.nextId();
        SmsDto dto = new SmsDto();
        dto.setUserid(123456789l);
        dto.setNode(MessageNode.FLASH_PAY.getCode());
        dto.setPhone("18503015212");
        Map<String, Object> parmas = Maps.newHashMapWithExpectedSize(8);
        parmas.put("orderno","1559122578268248001110");
        parmas.put("title","充电宝10元装");
        parmas.put("nickname","哥尔赞");
        parmas.put("url","https://www.baidu.com/");
        dto.setSmsParams(parmas);
        kafkaTemplate.send(SMS_TOPIC, key.toString(), JSONObject.toJSONString(dto));
        return "suc";
    }
    */
}

