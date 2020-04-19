package com.mmj.active.topic.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.topic.model.TopicInfo;
import com.mmj.active.topic.model.dto.TopicEx;
import com.mmj.active.topic.model.dto.TopicInfoDto;
import com.mmj.active.topic.service.TopicInfoService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 专题表 前端控制器
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-25
 */
@RestController
@RequestMapping("/topicInfo")
@Slf4j
public class TopicInfoController extends BaseController {


    @Autowired
    private TopicInfoService topicInfoService;

    @Autowired
    private ActiveGoodService activeGoodService;

    /**
     * 根据id查询专题
     *
     * @param topicId
     * @return
     */
    @RequestMapping(value = "/getTopicById/{topicId}", method = RequestMethod.POST)
    @ApiOperation(value = "通过Id查找专题")
    public ReturnData<TopicInfoDto> getTopicById(@PathVariable int topicId) {
        log.info("通过Id查找专题入参：{}", topicId);
        return initSuccessObjectResult(topicInfoService.getTopicById(topicId));
    }

    /**
     * 查询专题列表
     *
     * @param topic
     * @return
     */
    @RequestMapping(value = "/getTopics", method = RequestMethod.POST)
    @ApiOperation(value = "通过条件查找专题")
    public ReturnData<Page<TopicInfoDto>> getTopics(@RequestBody TopicInfo topic) {
        log.info("通过条件查找专题入参:{}", topic.toString());
        return initSuccessObjectResult(topicInfoService.queryPage(topic));
    }

    /**
     * 新增专题
     *
     * @param topicInfoDto
     * @return
     */
    @RequestMapping(value = "/saveTopic", method = RequestMethod.POST)
    @ApiOperation(value = "新增专题")
    public ReturnData<Object> saveTopic(@RequestBody TopicInfoDto topicInfoDto) {
        log.info("新增专题入参:{}", topicInfoDto.toString());
        activeGoodService.cleanGoodCache(8);
        return initSuccessObjectResult(topicInfoService.saveTopicById(topicInfoDto, true));
    }

    /**
     * 更新专题
     *
     * @param topicInfoDto
     * @return
     */
    @RequestMapping(value = "/updateTopic", method = RequestMethod.POST)
    @ApiOperation(value = "更新专题")
    public ReturnData<Object> updateTopic(@RequestBody TopicInfoDto topicInfoDto) {
        log.info("更新专题入参：{}", topicInfoDto.toString());
        activeGoodService.cleanGoodCache(8);
        return initSuccessObjectResult(topicInfoService.updateTopicById(topicInfoDto));
    }

    /**
     * 删除专题
     *
     * @param topicId
     * @return
     */
    @RequestMapping(value = "/deleteTopic/{topicId}", method = RequestMethod.POST)
    @ApiOperation(value = "通过Id删除专题")
    public ReturnData<String> deleteTopic(@PathVariable int topicId) {
        log.info("通过Id删除专题入参：{}", topicId);
        activeGoodService.cleanGoodCache(8);
        return initSuccessObjectResult(topicInfoService.deleteTopicById(topicId));
    }


    /**
     * 通过id查询优惠券
     *
     * @param topicId
     * @return
     */
    @RequestMapping(value = "/getTopicsCoupon/{topicId}", method = RequestMethod.POST)
    @ApiOperation(value = "通过Id查询优惠券")
    public ReturnData<List<Map<String, Object>>> getTopicsCoupon(@PathVariable int topicId) {
        log.info("通过Id查询优惠券入参：{}", topicId);
        return initSuccessObjectResult(topicInfoService.getTopicsCoupon(topicId));
    }

    /**
     * 根据专题查询专题组件(橱窗)
     *
     * @param topic
     * @param request
     * @return
     */
    @RequestMapping(value = "/queryTopicComponent", method = RequestMethod.POST)
    @ApiOperation(value = "根据专题查询专题组件(橱窗)")
    public Object queryTopicComponent(@RequestBody TopicInfo topic, HttpServletRequest request) {
        String appType = request.getHeader("appType");
        return initSuccessObjectResult(topicInfoService.queryTopicComponent(topic, appType));
    }

    /**
     * 修改专题组件(包含修改和新增)
     *
     * @param topicEx
     * @return
     */
    @RequestMapping(value = "/updateTopicComponent", method = RequestMethod.POST)
    @ApiOperation(value = "修改专题组件(包含修改和新增)")
    public Object updateTopicComponent(@RequestBody TopicEx topicEx) {
        try {
            activeGoodService.cleanGoodCache(8);
            topicInfoService.updateTopicComponent(topicEx);
        } catch (Exception e) {
            return initExcetionObjectResult(e.getMessage());
        }
        return initSuccessResult();
    }
}

