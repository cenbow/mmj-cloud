package com.mmj.active.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mmj.active.common.MQProducer;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.common.service.ShareGoodService;
import com.mmj.active.prizewheels.service.PrizewheelsFacadeService;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.model.GoodShare;
import com.mmj.common.utils.SecurityUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@Configuration
@EnableAsync
public class ShareGoodServiceImpl implements ShareGoodService {

    @Autowired
    private PrizewheelsFacadeService prizewheelsFacadeService;

    @Autowired
    private UserFeignClient userFeignClient;
    
    @Autowired
    private MQProducer mqProducer;
    
    @Override
	public void sendMsg(Long shareUserId, Integer goodId) {
		GoodShare gs = new GoodShare();
		long userId = SecurityUserUtil.getUserDetails().getUserId();
		gs.setUserId(userId);
		gs.setShareUserId(shareUserId);
		gs.setGoodId(goodId);
		log.info("-->点击商品分享，发送消息-->{}点击了好友{}的商品分享，商品ID：{}", userId, shareUserId, goodId);
		mqProducer.send(MQTopicConstant.TOPIC_GOODSHARE, JSONObject.toJSONString(gs));
	}

    @Override
    public void shareGood(Long userId, Long shareUserId, Integer goodId) {
    	if(userId.equals(shareUserId)) {
    		log.error("-->点击分享的商品-->当前用户就是分享人：{}，程序返回", userId);
    		return;
    	}
        // 处理转盘的商品分享
        this.handlePrizewheels(userId, shareUserId, goodId);
        // 处理其它活动的商品分享
        this.handleMMKing(userId, shareUserId, goodId);
    }

    @Async
    @Override
    public void handlePrizewheels(Long userId, Long shareUserId, Integer goodId) {
        prizewheelsFacadeService.clickGoodsShare(userId, shareUserId, goodId);
    }

    @Async
    @Override
    public void handleMMKing(Long userId, Long shareUserId, Integer goodId) {
        JSONObject object = new JSONObject();
        object.put("friendUserId", userId);
        object.put("userId", shareUserId);
        object.put("goodId",goodId);
        object.put("shareType","SHARE_GOODS");
        userFeignClient.clickShare(object);
    }

}
