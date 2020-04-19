package com.mmj.active.topic.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.topic.mapper.TopicCouponMapper;
import com.mmj.active.topic.model.TopicCoupon;
import com.mmj.active.topic.service.TopicCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 主题优惠券 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-24
 */
@Service
public class TopicCouponServiceImpl extends ServiceImpl<TopicCouponMapper, TopicCoupon> implements TopicCouponService {

    @Autowired
    private TopicCouponMapper topicCouponMapper;

    @Override
    public void deleteTopicId(Integer topicId){
        EntityWrapper<TopicCoupon> topicCouponEntityWrapper = new EntityWrapper<>();
        topicCouponEntityWrapper.eq("TOPIC_ID",topicId);
        topicCouponMapper.delete(topicCouponEntityWrapper);
    }

    @Override
    public List<TopicCoupon> selectTopicList(Integer topicId){
        EntityWrapper<TopicCoupon> topicCouponEntityWrapper = new EntityWrapper<>();
        topicCouponEntityWrapper.eq("TOPIC_ID",topicId);
        return topicCouponMapper.selectList(topicCouponEntityWrapper);
    }
}
