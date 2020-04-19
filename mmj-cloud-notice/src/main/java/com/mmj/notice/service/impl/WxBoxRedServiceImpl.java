package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.exception.WxException;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.MD5Util;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.notice.feigin.PayFeignClient;
import com.mmj.notice.mapper.WxBoxRedMapper;
import com.mmj.notice.model.WxBoxRecord;
import com.mmj.notice.model.WxBoxRed;
import com.mmj.notice.model.WxMedia;
import com.mmj.notice.model.WxpayRedpack;
import com.mmj.notice.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 物流箱红包码 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-12
 */
@Service
@Slf4j
public class WxBoxRedServiceImpl extends ServiceImpl<WxBoxRedMapper, WxBoxRed> implements WxBoxRedService {

    @Autowired
    WxBoxRecordService wxBoxRecordService;

    @Autowired
    WxMessageService wxMessageService;

    @Autowired
    PayFeignClient payFeignClient;

    @Autowired
    WxImageService wxImageService;

    @Autowired
    WxMediaService wxMediaService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * 发送物流箱上面的红包
     *
     * @param redCode
     */
    @Override
    public Object send(String redCode) {
//        if(redCode.startsWith("box")){ //物流箱裂变，用户的专属红包
//            return sendBoxRed(redCode);
//        }else { //物流箱上面的红包
//            return sendRed(redCode);
//        }
        return sendRed(redCode);
    }

    /**
     * 发送个人的专属红包
     *1.判断该红包码是否正确
     * 2.该红包码是否被领了三次
     * 3.如果这是第三次领取 那么就给这个红包码的拥有者在发一个红包
     * @param redCode
     */
    private Object sendBoxRed(String redCode) {
        Map map = new HashMap();
        EntityWrapper<WxBoxRecord> wxBoxRecordEntityWrapper = new EntityWrapper<>();
        wxBoxRecordEntityWrapper.eq("OPEN_CODE", redCode);
        WxBoxRecord wxBoxRecord = wxBoxRecordService.selectOne(wxBoxRecordEntityWrapper);
        if(null == wxBoxRecord){
            throw new WxException("该红包码不对");
        }
        wxBoxRecordEntityWrapper = new EntityWrapper<>();
        wxBoxRecordEntityWrapper.eq("RED_CODE", redCode);
        List<WxBoxRecord> wxBoxRecords = wxBoxRecordService.selectList(wxBoxRecordEntityWrapper);
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        List<WxBoxRecord> collect = wxBoxRecords.stream().filter(n -> userDetails.getOpenId().equals(userDetails.getOpenId()))
                .collect(Collectors.toList());
        if(!collect.isEmpty()){
            throw new WxException("你已经领取过红包了");
        }
        map.put("users", wxBoxRecords);
        int size = wxBoxRecords.size();
        if(size < 3){ //说明该红包码还可以领取
            int amout = new Random().nextInt(40) % (9) + 30;
            try {
                sendRedPackage(amout, userDetails.getOpenId(), null); //发送当前人领取的红包
            } catch (Exception e) {
                JSONObject error = JSON.parseObject(e.getCause().getMessage().split("content:\n")[1]);
                throw new WxException(error.getString("desc"));
            }
            if(size != 2){ //这种情况不发任务完成的红包码
                String content = "进度提示：你的好友["+userDetails.getUserFullName()+"]通过你的推荐领到红包啦，再推荐["+(2-size)+"]个人，你也能再领一个~";
                saveBoxRecord(redCode, amout, null); //保存红包发送记录
                sendCustomTxt(content, wxBoxRecord.getOpenid()); //发送客服消息
            }else {//这种情况发任务完成的红包码
                amout = new Random().nextInt(40) % (9) + 30;
                try {
                    sendRedPackage(amout, wxBoxRecord.getOpenid(), null); //发送专属红包码人的红包
                    String content = "完成提示：你的好友["+userDetails.getUserFullName()+"]通过你的推荐领到红包啦，恭喜你再得一个红包~如有任何问题，请截图联系微信客服^^";
                    sendCustomTxt(content, wxBoxRecord.getOpenid()); //发送客服消息
                } catch (Exception e) {
                    JSONObject error = JSON.parseObject(e.getCause().getMessage().split("content:\n")[1]);
                    throw new WxException(error.getString("desc"));
                }
            }
            map.put("money", amout);
        }else {
            map.put("money", 0);
        }
        return map;
    }

    /**
     * 发送物流箱上面的红包
     * @param redCode
     */
    private Object sendRed(String redCode) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        String key1 = "com.mmj.notice.service.impl.WxBoxRedServiceImpl.sendRed"+ redCode + userDetails.getOpenId();
        Long increment1 = redisTemplate.opsForValue().increment(key1, 1);
        redisTemplate.expire(key1, 3, TimeUnit.SECONDS);
        if(increment1 > 1){
            throw new WxException("手速太快了啊 大兄弟 三秒之后再试试!");
        }
        String key2 = "com.mmj.notice.service.impl.WxBoxRedServiceImpl.sendRed"+ redCode;
        Long increment2 = redisTemplate.opsForValue().increment(key2, 1);
        redisTemplate.expire(key2, 3, TimeUnit.SECONDS);
        if(increment2 > 1){
            throw new WxException("呵呵 多个人同时也领不到红包的");
        }
        EntityWrapper<WxBoxRed> wxBoxRedEntityWrapper = new EntityWrapper<>();
        wxBoxRedEntityWrapper.eq("RED_CODE", redCode);
        WxBoxRed wxBoxRed = selectOne(wxBoxRedEntityWrapper);
        if(null == wxBoxRed){
            throw new WxException("红包码不对啊 老弟");
        }
        EntityWrapper<WxBoxRecord> wxBoxRecordEntityWrapper = new EntityWrapper<>();
        wxBoxRecordEntityWrapper.eq("RED_CODE", redCode);
        WxBoxRecord wxBoxRecord = wxBoxRecordService.selectOne(wxBoxRecordEntityWrapper);
        if(null != wxBoxRecord){
            throw new WxException("你的红包码已经被'" + wxBoxRecord.getNickName() + "'领取了!");
        }
        int amout = new Random().nextInt(40) % (9) + 30;
        try {
            sendRedPackage(amout, userDetails.getOpenId(), redCode); //发送红包
            saveBoxRecord(redCode, amout, null); //保存红包发送记录
        } catch (Exception e) {
            JSONObject error = JSON.parseObject(e.getCause().getMessage().split("content:\n")[1]);
            throw new WxException(error.getString("desc"));
        }
//        String openCode = "box" + (int) ((Math.random() * 9 + 1) * 100000);
//        saveBoxRecord(redCode, amout, openCode); //保存红包码领取记录
//        String content = "恭喜！为您生成了专属的兑换码及图片，只要48小时内，3个好友通过你的专属图片获得红包，你可再得一个！";
//        sendCustomTxt(content, userDetails.getOpenId()); //发送专属红包码客服消息
//        String boxOpenCodeImg = wxImageService.createBoxOpenCode(openCode, userDetails.getUserFullName(), userDetails.getImagesUrl());//卡片图片
//        WxMedia wxMedia = new WxMedia();
//        wxMedia.setAppid(userDetails.getAppId());
//        wxMedia.setBusinessName("物流箱红包");
//        wxMedia.setMediaType("temporary");
//        wxMedia.setMediaUrl(boxOpenCodeImg);
//        WxMedia upload = wxMediaService.upload(wxMedia); //生成mediaid
//        String mediaId = upload.getMediaId();
//        sendCustomImg(mediaId, userDetails.getOpenId()); //发送图片消息
        return amout;
    }

    /**
     * 发送红包
     * @param amount
     * @param openid
     */
    private void  sendRedPackage(int amount, String openid, String redCode){
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        WxpayRedpack wxpayRedpack = new WxpayRedpack();
        wxpayRedpack.setMchBillno(MD5Util.MD5Encode(this.getClass().getName() + amount + userDetails.getOpenId() + redCode, "utf-8"));
        wxpayRedpack.setSendName("买买家");
        wxpayRedpack.setReOpenid(openid);
        wxpayRedpack.setTotalAmount(amount);
        wxpayRedpack.setWishing("恭喜得红包 嗯!");
        wxpayRedpack.setRemark("红包码送红包" + redCode);
        wxpayRedpack.setWxappid(userDetails.getAppId());
        wxpayRedpack.setActName("买买家");
        payFeignClient.sendRedpack(wxpayRedpack);
    }

    /**
     * 保存物流箱红包领取记录
     */
    private void saveBoxRecord(String redCode, int amount, String openCode){
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        WxBoxRecord wxBoxRecord = new WxBoxRecord();
        wxBoxRecord.setOpenid(userDetails.getOpenId());
        wxBoxRecord.setNickName(userDetails.getUserFullName());
        wxBoxRecord.setHeadImg(userDetails.getImagesUrl());
        wxBoxRecord.setRedCode(redCode);
        wxBoxRecord.setOpenCode(openCode);
        wxBoxRecord.setAmount(amount);
        wxBoxRecord.setAppId(userDetails.getAppId());
        wxBoxRecord.setCreateTime(new Date());
        wxBoxRecordService.insert(wxBoxRecord);
    }

    /**
     * 发送客服消息 文本
     * @param content
     */
    private void sendCustomTxt(String content, String openid){
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        JSONObject params = new JSONObject();
        params.put("touser", openid);
        params.put("msgtype", "text");
        JSONObject textParams = new JSONObject();
        textParams.put("content", content);
        params.put("text", textParams);
        params.put("appid", userDetails.getAppId());
        wxMessageService.sendCustom(params);
    }


    /**
     * 发送客服消息 图片
     * @param img
     */
    private void sendCustomImg(String img, String openid){
        JSONObject params = new JSONObject();
        params.put("touser", openid);
        params.put("msgtype", "image");
        JSONObject imgParams = new JSONObject();
        imgParams.put("media_id", img);
        params.put("image", imgParams);
        params.put("appid", openid);
        wxMessageService.sendCustom(params);
    }
}
