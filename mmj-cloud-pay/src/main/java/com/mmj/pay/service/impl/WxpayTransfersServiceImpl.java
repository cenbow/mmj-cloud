package com.mmj.pay.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.exception.WxException;
import com.mmj.common.utils.EnvUtil;
import com.mmj.pay.mapper.WxpayTransfersMapper;
import com.mmj.pay.model.WxMchInfo;
import com.mmj.pay.model.WxpayTransfers;
import com.mmj.pay.sdk.weixin.WXPayUtil;
import com.mmj.pay.service.WxMchInfoService;
import com.mmj.pay.service.WxpayTransfersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 微信发送零钱表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-16
 */
@Service
public class WxpayTransfersServiceImpl extends ServiceImpl<WxpayTransfersMapper, WxpayTransfers> implements WxpayTransfersService {

    @Autowired
    WxapiService wxapiService;

    @Value("${spring.cloud.config.profile}")
    private String profile;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    WxMchInfoService wxMchInfoService;

    /**
     * 发送零钱
     *
     * @param wxpayTransfers
     * @return
     */
    @Override
    public WxpayTransfers transfers(WxpayTransfers wxpayTransfers) {
        String key = "com.mmj.pay.service.impl.WxpayTransfersServiceImpl.transfers" + wxpayTransfers.getOpenid();
        Long increment = redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, 5, TimeUnit.SECONDS);
        if(increment > 1){
            throw new WxException("手速太快, 五秒钟以后再试哈");
        }
        Map<String, String> map = new HashMap<>();
        String mchBillno = wxpayTransfers.getPartnerTradeNo();
        if(mchBillno.length() > 28){
            mchBillno = mchBillno.substring(0,28);
        }
        map.put("partner_trade_no", mchBillno);
        map.put("openid", wxpayTransfers.getOpenid());
        map.put("check_name", "NO_CHECK"); //不校验真实姓名
        Integer amount = wxpayTransfers.getAmount();
        String desc = wxpayTransfers.getDesc();
        if(!EnvUtil.isPro(profile)){ //测试环境
            desc = "test_" + desc;
            while (amount > 100){
                amount = amount / 10;
            }
            if(amount < 30){
                amount = 30;
            }
        }
        map.put("amount", amount+ "");
        map.put("desc", desc);
        map.put("mch_appid", wxpayTransfers.getMchAppid());
        WxMchInfo wxMchInfo = wxMchInfoService.getMchInfo("all");
        map.put("mchId", wxMchInfo.getMchId());
        map.put("mchKey", wxMchInfo.getMchKey());
        Map<String, String> result = wxapiService.transfers(map);
        if(WXPayUtil.checkResult(result)){ //微信结果正确返回
            wxpayTransfers.setMchid(result.get("mchid"));
            wxpayTransfers.setSpbillCreateIp(result.get("spbill_create_ip"));
            wxpayTransfers.setState(0);
            wxpayTransfers.setCreateTime(new Date());
            insert(wxpayTransfers);
        }else {
            wxpayTransfers.setCreateTime(new Date());
            wxpayTransfers.setState(1);
            wxpayTransfers.setErrorDesc(result.get("err_code_des"));
            wxpayTransfers.setCreateTime(new Date());
            insert(wxpayTransfers);
            throw new WxException(result.get("err_code_des"));
        }
        return wxpayTransfers;
    }
}
