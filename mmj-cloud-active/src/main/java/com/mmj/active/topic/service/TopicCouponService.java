package com.mmj.active.topic.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.topic.model.TopicCoupon;

import java.util.List;

/**
 * <p>
 * 主题优惠券 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-24
 */
public interface TopicCouponService extends IService<TopicCoupon> {

    /**
     * 根据topicID删除优惠券
     * @param topicId
     */
    void deleteTopicId(Integer topicId);

    /**
     * 根据专题id查询优惠券
     * @param topicId
     * @return
     */
    List<TopicCoupon> selectTopicList(Integer topicId);

}
