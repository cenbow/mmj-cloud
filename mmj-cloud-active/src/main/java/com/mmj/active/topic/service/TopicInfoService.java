package com.mmj.active.topic.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.topic.model.TopicInfo;
import com.mmj.active.topic.model.dto.TopicEx;
import com.mmj.active.topic.model.dto.TopicInfoDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 专题表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-25
 */
public interface TopicInfoService extends IService<TopicInfo> {

    /**
     * 根据专题id查询专题
     * @param topicId
     * @return
     */
    TopicInfoDto getTopicById(Integer topicId);

    /**
     * 分页查询专题
     * @param topicInfo
     * @return
     */
    Page<TopicInfoDto> queryPage(TopicInfo topicInfo);

    /**
     * 新增专题
     * @param topicInfoDto
     * @param bSave
     * @return
     */
    Map<String,Object> saveTopicById(TopicInfoDto topicInfoDto, Boolean bSave);

    /**
     * 更新专题
     * @param topicInfoDto
     * @return
     */
    Map<String,Object> updateTopicById(TopicInfoDto topicInfoDto);

    /**
     * 删除专题
     * @param topicId
     * @return
     */
    String deleteTopicById(Integer topicId);

    /**
     * 根据专题id查询优惠券
     * @param topicId
     * @return
     */
    List<Map<String,Object>> getTopicsCoupon(int topicId);

    /**
     * 保存橱窗组件
     * @param topicEx
     */
    void updateTopicComponent(TopicEx topicEx);

    /**
     * 查询橱窗组件
     * @param topic
     * @param appType
     * @return
     */
    Object queryTopicComponent(TopicInfo topic, String appType);

}
