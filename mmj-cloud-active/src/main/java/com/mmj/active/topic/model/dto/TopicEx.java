package com.mmj.active.topic.model.dto;

import com.mmj.active.topic.model.TopicInfo;

import java.util.List;

public class TopicEx extends TopicInfo {

    private List<TopicComponentEx> topicComponentExes;

    public List<TopicComponentEx> getTopicComponentExes() {
        return topicComponentExes;
    }

    public void setTopicComponentExes(List<TopicComponentEx> topicComponentExes) {
        this.topicComponentExes = topicComponentExes;
    }
}
