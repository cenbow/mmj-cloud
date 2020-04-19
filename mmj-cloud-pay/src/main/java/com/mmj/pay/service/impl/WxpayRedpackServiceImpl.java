package com.mmj.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.exception.WxException;
import com.mmj.common.utils.EnvUtil;
import com.mmj.pay.common.feign.NoticeFeignClient;
import com.mmj.pay.mapper.WxpayRedpackMapper;
import com.mmj.pay.model.WxMchInfo;
import com.mmj.pay.model.WxpayRedpack;
import com.mmj.pay.sdk.weixin.WXPayUtil;
import com.mmj.pay.service.WxMchInfoService;
import com.mmj.pay.service.WxpayRedpackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 微信红包记录表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-17
 */
@Service
@Slf4j
public class WxpayRedpackServiceImpl extends ServiceImpl<WxpayRedpackMapper, WxpayRedpack> implements WxpayRedpackService {

    @Autowired
    WxapiService wxapiService;

    @Value("${spring.cloud.config.profile}")
    private String profile;

    @Autowired
    NoticeFeignClient noticeFeignClient;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    WxMchInfoService wxMchInfoService;

    /**
     * 发送普通红包
     *
     * @param wxpayRedpack
     * @return
     */
    @Override
    @Transactional
    public WxpayRedpack sendRedpack(WxpayRedpack wxpayRedpack) {
        String key = getClass().getName() + ":" + wxpayRedpack.getReOpenid();
        Long increment = redisTemplate.opsForValue().increment(key, 1);
        boolean ex = redisTemplate.expire(key, 5, TimeUnit.SECONDS);
        log.info("increment值:{},expire结果:{}", increment, ex);
        if (increment > 1) {
            throw new WxException("手速太快, 五秒钟以后再试哈");
        }
        Map<String, String> map = new HashMap<>();
        String mchBillno = wxpayRedpack.getMchBillno();
        if (mchBillno.length() > 28) {
            mchBillno = mchBillno.substring(0, 28);
        }
        map.put("mch_billno", mchBillno);
        map.put("send_name", wxpayRedpack.getSendName());
        map.put("re_openid", wxpayRedpack.getReOpenid());
        Integer totalAmount = wxpayRedpack.getTotalAmount();
        String remark = wxpayRedpack.getRemark();
        if (!EnvUtil.isPro(profile)) { //测试环境
            remark = "test_" + remark;
            while (totalAmount > 100) {
                totalAmount = totalAmount / 10;
            }
            if (totalAmount <= 30) {
                totalAmount = 30;
            }
        }
        map.put("total_amount", totalAmount+"");
        map.put("total_num", "1");
        map.put("wishing", wxpayRedpack.getWishing());
        map.put("act_name", wxpayRedpack.getActName());
        map.put("remark", remark);
        map.put("wxappid", wxpayRedpack.getWxappid());
        WxMchInfo mchInfo = wxMchInfoService.getMchInfo("all");
        map.put("mchId", mchInfo.getMchId());
        map.put("mchKey", mchInfo.getMchKey());
        Map<String, String> result = wxapiService.sendRedpack(map);
        if (WXPayUtil.checkResult(result)) { //微信结果正确返回
            wxpayRedpack.setMchId(result.get("mchid"));
            wxpayRedpack.setClientIp(result.get("client_ip"));
            wxpayRedpack.setState(0);
            wxpayRedpack.setCreateTime(new Date());
            insert(wxpayRedpack);
        } else {
            wxpayRedpack.setState(1);
            wxpayRedpack.setErrorDesc(result.get("err_code_des"));
            wxpayRedpack.setCreateTime(new Date());
            insert(wxpayRedpack);
            JSONObject msgJson = new JSONObject();
            msgJson.put("appid", wxpayRedpack.getWxappid());
            msgJson.put("touser", wxpayRedpack.getReOpenid());
            msgJson.put("msgtype", "text");
            JSONObject textJson = new JSONObject();
            String checkRedMoney = checkRedMoney(JSONObject.toJSONString(result));
            textJson.put("content", checkRedMoney);
            msgJson.put("text", textJson);
            noticeFeignClient.sendCustom(msgJson.toJSONString());
            throw new WxException(result.get("err_code_des"));
        }
        return null;
    }

    private String checkRedMoney(String params){
        if(params.contains("帐号余额不足")){
            return "宝宝没钱了,等一下再来问我好吗";
        }else if(params.contains("该用户今日操作次数超过限制")){
            return "小伙子,你领的太多了,明天再来吧";
        }else if(params.contains("此请求可能存在风险")){
            return "年轻人，你的微信干过什么坏事啊";
        }else if(params.contains("每个红包的平均金额必须在")){
            return "对不起小伙伴,微信说至少给你发一块钱,我和他正商量";
        }else if(params.contains("IP地址非你在商户平台设置的可用IP地址")){
            return "服务器换地方了,请联系客服人员找开发人员！ 嗯";
        }
        return null;
    }
}
