package com.mmj.active.topic.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.topic.model.TopicInfo;
import com.mmj.active.topic.model.dto.TopicInfoDto;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 专题表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-25
 */
@Repository
public interface TopicInfoMapper extends BaseMapper<TopicInfo> {

    List<TopicInfoDto> queryTopicPage(Page<TopicInfoDto> page, TopicInfo topicInfo);

}
