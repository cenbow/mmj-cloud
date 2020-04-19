package com.mmj.user.member.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.constants.MemberConstant;
import com.mmj.common.utils.StringUtils;
import com.mmj.user.member.dto.SaveUserMemberDto;
import com.mmj.user.member.service.UserMemberService;
import com.mmj.user.recommend.service.UserShardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;


@Slf4j
@Component
public class PayMQConsumer {

    @Autowired
    UserMemberService userMemberService;

    @Autowired
    private UserShardService userShardService;

    /**
     * 支付成功通知
     *
     * @param msgs
     */
    @KafkaListener(topics = {MQTopicConstant.WX_ORDER_TOPIC})
    public void listen(List<String> msgs) {
        msgs.forEach( msg ->{
            log.info("会员kafka接收到微信支付成功通知" + msg);
            JSONObject msgJson = JSONObject.parseObject(msg);
            String outTradeNo = msgJson.getString("outTradeNo");
            if(outTradeNo.contains("hy")){ //如果这个订单是属于会员的订单
                SaveUserMemberDto dto = new SaveUserMemberDto();
                dto.setBeMemberType(MemberConstant.BE_MEMBER_TYPE_BUY);
                dto.setOrderNo(outTradeNo);
                dto.setOpenId(msgJson.getString("openId"));
                userMemberService.save(dto);
            }
            log.info("-->推荐返现，支付成功，开始方法处理中:{}",JSONObject.toJSONString(msg));
            if (!StringUtils.isEmpty(msg)) {
                JSONObject jsonObject = JSON.parseObject(msg);
                Long createrId = jsonObject.getLong("createrId");
                Integer totalFee = jsonObject.getInteger("totalFee");
                outTradeNo = jsonObject.getString("outTradeNo");
                log.info("-->开始进入推荐返现,支付完成修改状态中,userid:{}, 订单号:{},订单金额:{}", createrId, outTradeNo, totalFee);
                userShardService.updateRecommendShared(createrId, outTradeNo, msgJson.getString("appId"),2);
            }
        });
    }

    @PreDestroy
    public void close(){
        log.info("关闭了 user");
    }
}
