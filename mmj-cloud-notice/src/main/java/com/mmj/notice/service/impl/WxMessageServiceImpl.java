package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.google.common.base.Joiner;
import com.mmj.common.exception.WxException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import com.mmj.common.utils.HttpTools;
import com.mmj.common.utils.StringUtils;
import com.mmj.notice.common.mq.WxMessageProduce;
import com.mmj.notice.common.utils.WechatMessageUtil;
import com.mmj.notice.common.utils.WxTokenUtils;
import com.mmj.notice.feigin.OauthFeignClient;
import com.mmj.notice.feigin.WxMessageFeignClient;
import com.mmj.notice.model.OfficialAccountUser;
import com.mmj.notice.model.WxConstants;
import com.mmj.notice.model.WxForm;
import com.mmj.notice.service.CustomCallBack;
import com.mmj.notice.service.WxFormService;
import com.mmj.notice.service.WxMessageService;
import com.mmj.notice.service.WxTagService;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 微信客服消息处理
 */
@Service
@Slf4j
@EnableAsync
public class WxMessageServiceImpl implements WxMessageService {

    @Autowired
    WxMessageFeignClient wxMessageFeignClient;

    @Autowired
    OauthFeignClient oauthFeignClient;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    HttpTools httpTools;

    @Autowired
    WxTokenUtils wxTokenUtils;

    @Autowired
    WxMessageProduce wxMessageProduce;

    @Autowired
    WxFormService wxFormService;

    @Autowired
    WxTagService wxTagService;

    /**
     * 将xml转化为map
     *
     * @param xml
     * @return
     */
    public static Map<String, String> xmlToMap(String xml) {
        Map<String, String> map = new HashMap<>();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点
            List<Element> list = rootElt.elements();// 获取根节点下所有节点
            for (Element element : list) { // 遍历节点
                map.put(element.getName(), element.getText()); // 节点的name为map的key，text为map的value
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 校验客服消息配置的地址是否正确
     *
     * @param signParams
     * @return
     */
    @Override
    public boolean checkSignature(Map<String, String[]> signParams) {
        String signature = signParams.get("signature")[0];
        String timestamp = signParams.get("timestamp")[0];
        String nonce = signParams.get("nonce")[0];
        return getSHA1("Javen", timestamp, nonce).equals(signature);
    }

    /**
     * 将微信接受到的流转化位map消息(公众号)
     *
     * @param inputStream
     * @return
     */
    @Override
    public Map<String, String> transform(InputStream inputStream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Map<String, String> map = xmlToMap(sb.toString());
            log.info("公众号接收到客服消息 " + JSONObject.toJSONString(map));
            Long increment = redisTemplate.opsForValue().increment("wx_msg:" + JSONObject.toJSONString(map), 1);
            redisTemplate.expire("wx_msg:" + JSONObject.toJSONString(map), 3, TimeUnit.SECONDS);
            log.info("接受次数=====" + increment);
            if (increment > 1) {
                return null;
            }
            ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByWxNo(map.get("ToUserName"));
            if (null != wxConfigReturnData && null != wxConfigReturnData.getData()) {
                WxConfig wxConfig = wxConfigReturnData.getData();
                String token = redisTemplate.opsForValue().get("access_token_" + wxConfig.getAppId());
                Map<String, String> params = new HashMap<>();
                params.put("access_token", token);
                params.put("openid", map.get("FromUserName"));
                params.put("lang", "zh_CN");
                JSONObject userInfo = httpTools.doGet(WxConstants.URL_GET_USERINFO, params);
                if (WxConstants.CODE_INVALID_TOKEN.equals(userInfo.getString("errcode"))) { //token失效 获取最新的token再次请求
                    token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
                    params.put("access_token", token);
                    userInfo = httpTools.doGet(WxConstants.URL_GET_USERINFO, params);
                }
                log.info("公众号客服消息用户信息" + userInfo.toJSONString());
                JSONObject msg = JSON.parseObject(JSON.toJSONString(map));
                String event = map.get("Event"); //公众号事件
                userInfo.put("appid", wxConfig.getAppId());
                if (StringUtils.isNotEmpty(event)) { //有event
                    switch (event) {
                        case WechatMessageUtil.MESSAGE_EVENT_SUBSCRIBE: //关注事件
                            saveUser(userInfo);
                            break;
                        case WechatMessageUtil.MESSAGE_EVENT_UNSUBSCRIBE: //取消关注
                            unsubUser(userInfo);
                            break;
                    }
                }
                msg.put("ex", userInfo);
                wxMessageProduce.sendWxH5Msg(msg);
            }
            return map;
        } catch (Exception e) {
            log.error("公众号微信客服消息转化失败", new Throwable(e));
        }
        return null;
    }

    /**
     * 关注时候保存用户
     *
     * @param userInfo
     */
    private void saveUser(JSONObject userInfo) {
        JSONObject userJson = new JSONObject();
        String appid = userInfo.getString("appid");
        userJson.put("appId", appid);
        userJson.put("openId", userInfo.getString("openid"));
        userJson.put("unionId", userInfo.getString("unionid"));
        userJson.put("nickname", userInfo.getString("nickname"));
        userJson.put("sex", userInfo.getInteger("sex"));
        userJson.put("headimgurl", userInfo.getString("headimgurl"));
        String appType = "";
        if ("wx77a4d1dccaab522c".equals(appid)) { //买买家
            appType = "MH5";
        } else if ("wx23c94214fcd23771".equals(appid)) { //买买发
            appType = "mmfa";
        }
        userJson.put("appType", appType);
        userJson.put("country", userInfo.getString("country"));
        userJson.put("province", userInfo.getString("province"));
        userJson.put("city", userInfo.getString("city"));
        userJson.put("subscribe", userInfo.getInteger("subscribe"));
        String tagidList = userInfo.getString("tagid_list").replace("[", "").replace("]", "");
        userJson.put("tagidList", tagidList);
        List<String> tagNames = wxTagService.queryTagName(appid, Arrays.asList(tagidList.split(",")));
        userJson.put("tagName", Joiner.on(",").join(tagNames));
        userJson.put("subscribeScene", userInfo.getString("subscribe_scene"));
        userJson.put("qrScene", userInfo.getString("qr_scene"));
        userJson.put("qrSceneStr", userInfo.getString("qr_scene_str"));
        userJson.put("groupid", userInfo.getString("groupid"));
        OfficialAccountUser officialAccountUser = JSONObject.parseObject(userJson.toJSONString(), OfficialAccountUser.class);
        oauthFeignClient.savePublic(officialAccountUser); //将关注的用户存入用户表
    }

    /**
     * 取消关注事件
     *
     * @param userInfo
     */
    private void unsubUser(JSONObject userInfo) {
        String openid = userInfo.getString("openid");
        oauthFeignClient.unsubUser(openid);
    }

    /**
     * 将微信接受到的流转化位map消息(小程序)
     *
     * @param inputStream
     * @return
     */
    @Override
    public Map<String, String> transformM(InputStream inputStream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            JSONObject msg = JSON.parseObject(sb.toString());
            log.info("小程序接收到客服消息 " + msg);
            wxMessageProduce.sendWxMinMsg(msg);
            return null;
        } catch (Exception e) {
            log.error("小程序微信客服消息转化失败", new Throwable(e));
        }
        return null;
    }

    /**
     * 发送公众号模板消息
     *
     * @param msgJson
     * @return
     */
    @Override
    public JSONObject sendTemplate(JSONObject msgJson) {
        String appid = msgJson.getString("appid");
        String token = redisTemplate.opsForValue().get("access_token_" + appid);
        msgJson.remove("appid");
        JSONObject result = httpTools.doPost(WxConstants.URL_SEND_MP_MSG + "?access_token=" + token, msgJson);
        if (WxConstants.CODE_INVALID_TOKEN.equals(result.getString("errcode"))) { //token失效 获取最新的token再次请求
            ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByAppId(appid);
            if (null != wxConfigReturnData && null != wxConfigReturnData.getData()) {
                WxConfig wxConfig = wxConfigReturnData.getData();
                token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
                result = httpTools.doPost(WxConstants.URL_MSG_TEMPLATE_MIN + "?access_token=" + token, msgJson);
            }
            return result;
        }
        return result;
    }

    /**
     * 发送小程序模板消息
     *
     * @param msgJson
     * @return
     */
    @Override
    public JSONObject sendTemplateM(JSONObject msgJson) {
        String openid = msgJson.getString("touser");
        String appid = msgJson.getString("appid");
        EntityWrapper<WxForm> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("APPID", appid).eq("OPENID", openid).orderBy("CREATE_TIME");
        WxForm wxForm = wxFormService.selectOne(entityWrapper);
        if (null == wxForm) {
            throw new WxException(openid + "formid不存在");
        }
        String token = redisTemplate.opsForValue().get("access_token_" + appid);
        msgJson.put("form_id", wxForm.getFormId());
        JSONObject result = httpTools.doPost(WxConstants.URL_MSG_TEMPLATE_MIN + "?access_token=" + token, msgJson);
        if (WxConstants.CODE_INVALID_TOKEN.equals(result.getString("errcode"))) { //token失效 获取最新的token再次请求
            ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByAppId(appid);
            if (null != wxConfigReturnData && null != wxConfigReturnData.getData()) {
                WxConfig wxConfig = wxConfigReturnData.getData();
                token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
                result = httpTools.doPost(WxConstants.URL_MSG_TEMPLATE_MIN + "?access_token=" + token, msgJson);
            }
            wxFormService.deleteById(wxForm.getId());
            return result;
        }
        throw new WxException("根据appid查询微信配置不存在");
    }

    @Override
    public void sendTemplateM(Map<String, Object> map) {
        if (!map.containsKey("touser")) {
            log.info("touser 不存在");
            return;
        }
        if (!map.containsKey("appid")) {
            log.info("appid 不存在");
            return;
        }

        String openid = map.get("touser").toString();
        String appid = map.get("appid").toString();

        EntityWrapper<WxForm> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("APPID", appid).eq("OPENID", openid).orderBy("CREATE_TIME");
        WxForm wxForm = wxFormService.selectOne(entityWrapper);
        if (null == wxForm) {
            log.error(openid + " formid不存在");
            return;
        }

        Integer wxFormId = wxForm.getId();
        log.info("WxForm对象:{}", wxForm);

        String token = redisTemplate.opsForValue().get("access_token_" + appid);
        map.put("form_id", wxForm.getFormId());
        JSONObject result = httpTools.doPost(WxConstants.URL_MSG_TEMPLATE_MIN + "?access_token=" + token, map);
        log.info("尝试第一次发送模板消息:{}", result);
        if (WxConstants.CODE_INVALID_TOKEN.equals(result.getString("errcode"))) { //token失效 获取最新的token再次请求
            ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByAppId(appid);
            if (null != wxConfigReturnData && null != wxConfigReturnData.getData()) {
                WxConfig wxConfig = wxConfigReturnData.getData();
                token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
                result = httpTools.doPost(WxConstants.URL_MSG_TEMPLATE_MIN + "?access_token=" + token, map);
            }
        }
        log.info("发送微信模板消息:{}", result.toJSONString());
        if (WxConstants.CODE_WX_SUCCESS_VALUE.equals(result.getString("errcode"))) {
            log.info("要删除的formId：{}", wxFormId);
            wxFormService.deleteById(wxForm.getId());
        }
    }

    /**
     * 发送公众号客服消息 带上回调方法
     *
     * @param msgJson
     * @param customCallBack
     * @return
     */
    @Override
    public JSONObject sendCustom(JSONObject msgJson, CustomCallBack customCallBack) {
        JSONObject jsonObject = sendCustom(msgJson);
        customCallBack.success(jsonObject, msgJson.getString("appid"));
        return jsonObject;
    }

    /**
     * 发送公众号客服消息
     *
     * @param msgJson
     * @return
     */
    @Override
    public JSONObject sendCustom(JSONObject msgJson) {
        String appid = msgJson.getString("appid");
        String token = redisTemplate.opsForValue().get("access_token_" + appid);
        JSONObject result = httpTools.doPost(WxConstants.URL_GET_CUSTOMSEND + "?access_token=" + token, msgJson);
        if (WxConstants.CODE_INVALID_TOKEN.equals(result.getString("errcode"))) { //token失效 获取最新的token再次请求
            ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByAppId(appid);
            if (null != wxConfigReturnData && null != wxConfigReturnData.getData()) {
                WxConfig wxConfig = wxConfigReturnData.getData();
                token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
                result = httpTools.doPost(WxConstants.URL_GET_CUSTOMSEND + "?access_token=" + token, msgJson);
            }
        }
        return result;
    }

    /**
     * 发送公众号客服消息 发送给appid下的所有人
     *
     * @param msgJson
     * @param appid
     * @return 返回的是最后一次拉取的情况
     */
    @Override
    @Async
    public JSONObject sendCustom(JSONObject msgJson, String appid) {
        JSONObject openList = getOpenList(appid, null);
        Integer count = openList.getInteger("count");
        while (count == 10000) { //说明此次拉取的是10000个用户 继续往下面获取
            JSONArray jsonArray = openList.getJSONObject("data").getJSONArray("openid");
            jsonArray.parallelStream().forEach(n -> {
                msgJson.put("touser", n);
                sendCustom(msgJson);
            });
            String nextOpenid = openList.getString("next_openid");
            openList = getOpenList(appid, nextOpenid);
            count = openList.getInteger("count");
        }
        //最后一次拉取不是一万 要发送这批用户
        JSONArray jsonArray = openList.getJSONObject("data").getJSONArray("openid");
        jsonArray.parallelStream().forEach(n -> {
            msgJson.put("touser", n);
            sendCustom(msgJson);
        });
        return openList;
    }

    /**
     * 查询一个公众号下面的总人数
     *
     * @param appid
     * @return
     */
    @Override
    public int queryTotal(String appid) {
        JSONObject openList = getOpenList(appid, null);
        return openList.getInteger("total");
    }

    /**
     * 发送公众号客服消息 发送给appid下的所有人
     *
     * @param msgJson
     * @param appid
     * @return 返回的是最后一次拉取的情况
     */
    @Override
    @Async
    public JSONObject sendCustomCallBack(JSONObject msgJson, String appid, CustomCallBack customCallBack) {
        JSONObject openList = getOpenList(appid, null);
        Integer count = openList.getInteger("count");
        while (count == 10000) { //说明此次拉取的是10000个用户 继续往下面获取
            JSONArray jsonArray = openList.getJSONObject("data").getJSONArray("openid");
            jsonArray.parallelStream().forEach(n -> {
                msgJson.put("touser", n);
                msgJson.put("appid", appid);
                sendCustom(msgJson, customCallBack);
            });
            String nextOpenid = openList.getString("next_openid");
            openList = getOpenList(appid, nextOpenid);
            count = openList.getInteger("count");
        }
        //最后一次拉取不是一万 要发送这批用户
        JSONArray jsonArray = openList.getJSONObject("data").getJSONArray("openid");
        jsonArray.parallelStream().forEach(n -> {
            msgJson.put("touser", n);
            msgJson.put("appid", appid);
            sendCustom(msgJson, customCallBack);
        });
        customCallBack.complete(appid);
        return openList;
    }

    /**
     * 查询公众号appid下面的这个用户信息
     *
     * @param appid
     * @param openid
     * @return
     */
    @Override
    public JSONObject queryUserInfo(String appid, String openid) {
        ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByAppId(openid);
        if (null != wxConfigReturnData && null != wxConfigReturnData.getData()) {
            WxConfig wxConfig = wxConfigReturnData.getData();
            String token = redisTemplate.opsForValue().get("access_token_" + wxConfig.getAppId());
            Map<String, String> params = new HashMap<>();
            params.put("access_token", token);
            params.put("openid", openid);
            params.put("lang", "zh_CN");
            JSONObject userInfo = httpTools.doGet(WxConstants.URL_GET_USERINFO, params);
            if (WxConstants.CODE_INVALID_TOKEN.equals(userInfo.getString("errcode"))) { //token失效 获取最新的token再次请求
                token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
                params.put("access_token", token);
                userInfo = httpTools.doGet(WxConstants.URL_GET_USERINFO, params);
            }
            return userInfo;
        }
        return null;
    }

    /**
     * 发送公众号客服消息 发送给appid下的指定标签用户,带上回调方法
     *
     * @param msgJson
     * @param appid
     * @param tagIds
     * @param customCallBack
     * @return
     */
    @Override
    public JSONObject sendCustomCallBack(JSONObject msgJson, String appid, List<String> tagIds, CustomCallBack customCallBack) {
        JSONObject openList = getOpenList(appid, null);
        Integer count = openList.getInteger("count");
        while (count == 10000) { //说明此次拉取的是10000个用户 继续往下面获取
            JSONArray jsonArray = openList.getJSONObject("data").getJSONArray("openid");
            jsonArray.parallelStream().forEach(n -> {
                sendMsgTag(appid, (String) n, tagIds, msgJson, customCallBack);
            });
            String nextOpenid = openList.getString("next_openid");
            openList = getOpenList(appid, nextOpenid);
            count = openList.getInteger("count");
        }
        //最后一次拉取不是一万 要发送这批用户
        JSONArray jsonArray = openList.getJSONObject("data").getJSONArray("openid");
        jsonArray.parallelStream().forEach(n -> {
            //查询用户信息 看看是否有这些标签
            sendMsgTag(appid, (String) n, tagIds, msgJson, customCallBack);
        });
        customCallBack.complete(appid);
        return openList;
    }

    /**
     * 发送带标签用户的客服消息
     *
     * @param appid
     * @param openid
     * @param tagIds
     * @param msgJson
     * @param customCallBack
     */
    private void sendMsgTag(String appid, String openid, List<String> tagIds, JSONObject msgJson, CustomCallBack customCallBack) {
        //查询用户信息 看看是否有这些标签
        JSONObject userInfo = queryUserInfo(appid, openid);
        if (null != userInfo) {
            JSONArray tagidList = userInfo.getJSONArray("tagid_list");
            if (tagIds.retainAll(tagidList)) { //说明发送的用户有对应的标签 那么就发送
                msgJson.put("touser", openid);
                msgJson.put("appid", appid);
                sendCustom(msgJson, customCallBack);
            }
        }
    }

    /**
     * 快速发送短信
     *
     * @param msgJson
     * @return
     */
    @Override
    public Object sendSms(JSONObject msgJson) {
        Map<String, String> map = new HashMap<>();
        map.put("cmd", "send");
        map.put("uid", "4843");
        map.put("psw", "fa11fe6246de6816230deed845bfb524");
        map.put("mobiles", msgJson.getString("mobiles"));
        map.put("msgid", StringUtils.getUUid());
        String msg = "";
        try {
            msg = URLEncoder.encode(msgJson.getString("msg"), "GBK");
        } catch (UnsupportedEncodingException e) {
            log.error("短信内容转码错误", e);
        }
        map.put("msg", msg);
        String result = httpTools.doGetString(WxConstants.smsMsg, map);
        return result;
    }

    /**
     * 慢速发短信
     *
     * @param msgJson
     * @return
     */
    @Override
    public Object sendSmsl(JSONObject msgJson) {
        Map<String, String> map = new HashMap<>();
        map.put("cmd", "send");
        map.put("uid", "3856");
        map.put("psw", "c2b1d04373e80dd126ef805d9593791a");
        map.put("mobiles", msgJson.getString("mobiles"));
        map.put("msgid", StringUtils.getUUid());
        String msg = "";
        try {
            msg = URLEncoder.encode(msgJson.getString("msg"), "GBK");
        } catch (UnsupportedEncodingException e) {
            log.error("短信内容转码错误", e);
        }
        map.put("msg", msg);
        String result = httpTools.doGetString(WxConstants.smsMsg, map);
        return result;
    }

    /**
     * 用SHA1算法验证Token
     *
     * @param token     票据
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @return 安全签名
     */
    public String getSHA1(String token, String timestamp, String nonce) {
        try {
            String[] array = new String[]{token, timestamp, nonce};
            StringBuffer sb = new StringBuffer();
            // 字符串排序
            Arrays.sort(array);
            for (int i = 0; i < 3; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            // SHA1签名生成
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();

            StringBuffer hexstr = new StringBuffer();
            String shaHex = "";
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            return hexstr.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取该appid下面的用户列表
     *
     * @param appid
     * @param nextOpenid
     * @return
     */
    private JSONObject getOpenList(String appid, String nextOpenid) {
        Map<String, String> params = new HashMap<>();
        String token = redisTemplate.opsForValue().get("access_token_" + appid);
        params.put("access_token", token);
        if (null != nextOpenid) {
            params.put("next_openid", nextOpenid);
        }
        JSONObject userResult = httpTools.doGet(WxConstants.URL_USER_LIST, params);
        if (WxConstants.CODE_INVALID_TOKEN.equals(userResult.getString("errcode"))) { //token失效 获取最新的token再次请求
            ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByAppId(appid);
            if (null != wxConfigReturnData && null != wxConfigReturnData.getData()) {
                WxConfig wxConfig = wxConfigReturnData.getData();
                token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
                params.put("access_token", token);
                userResult = httpTools.doGet(WxConstants.URL_USER_LIST, params);
            }
        }
        return userResult;
    }
}
