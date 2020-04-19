package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import com.mmj.common.utils.HttpTools;
import com.mmj.notice.common.utils.WxTokenUtils;
import com.mmj.notice.feigin.WxMessageFeignClient;
import com.mmj.notice.model.WxConstants;
import com.mmj.notice.service.WxTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 微信标签处理
 */
@Service
public class WxTagServiceImpl implements WxTagService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    HttpTools httpTools;

    @Autowired
    WxTokenUtils wxTokenUtils;

    @Autowired
    WxMessageFeignClient wxMessageFeignClient;

    /**
     * 给用户打标签
     *
     * @param params
     * @return
     */
    @Override
    public JSONObject doTag(JSONObject params) {
        String appid = params.getString("appid");
        String openid = params.getString("openid");
        List<String> tagIds = new ArrayList<>(); //标签id的集合
        JSONArray jsonArray = params.getJSONArray("tagNames");
        JSONObject tagResult = getTag(appid); //获取公众号的标签信息
        JSONArray tags = tagResult.getJSONArray("tags");
        //和要打的标签做对比，如果没有 那么就新创建 最终结果是把标签id放进tagIds集合
        for (int i =0; i< jsonArray.size(); i++){
            String tagName = jsonArray.getString(i);
            boolean hasTag = false;
            for (int j = 0; j < tags.size(); j++){
                JSONObject tagRecord = tags.getJSONObject(j);
                if(tagRecord.getString("name").equals(tagName)){
                    tagIds.add(tagRecord.getString("id"));
                    hasTag = true;
                    break;
                }
            }
            if(!hasTag){ //说明原来没有这个标签 那么就创建标签
                Map mapc = new HashMap<>();
                Map tagm = new HashMap();
                tagm.put("name", tagName);
                mapc.put("tag",tagm);
                String token =  redisTemplate.opsForValue().get("access_token_" + appid);
                tagResult = httpTools.doPost(WxConstants.URL_CREATE_TAG+"?access_token=" + token, mapc);
                if(WxConstants.CODE_INVALID_TOKEN.equals(tagResult.getString("errcode"))){ //token失效 获取最新的token再次请求
                    ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByAppId(appid);
                    WxConfig wxConfig = wxConfigReturnData.getData();
                    token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
                    tagResult = httpTools.doPost(WxConstants.URL_CREATE_TAG+"?access_token=" + token, mapc);
                }
                String tagid = tagResult.getJSONObject("tag").getString("id");
                tagIds.add(tagid);
            }
        }
        //开始给用户打标签
        for (int i = 0; i < tagIds.size(); i++){
            String token =  redisTemplate.opsForValue().get("access_token_" + appid);
            Map mapc = new HashMap<>();
            List openList = new ArrayList();
            openList.add(openid);
            mapc.put("openid_list", openList);
            mapc.put("tagid", tagIds.get(i));
            JSONObject result = httpTools.doPost(WxConstants.URL_BATCH_TAG + "?access_token=" + token, mapc);
            if(WxConstants.CODE_INVALID_TOKEN.equals(result.getString("errcode"))){//token失效 获取最新的token再次请求
                ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByAppId(appid);
                WxConfig wxConfig = wxConfigReturnData.getData();
                token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
                params.put("access_token", token);
                httpTools.doGet(WxConstants.URL_BATCH_TAG, mapc);
            }
        }
        return null;
    }

    /**
     * 获取公众号的标签信息
     * @param appid
     * @return
     */
    private JSONObject getTag(String appid){
        String token = redisTemplate.opsForValue().get("access_token_" + appid);
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        JSONObject tagResult = httpTools.doGet(WxConstants.URL_GET_TAGS, map);
        //获取公众号原有的标签
        if(WxConstants.CODE_INVALID_TOKEN.equals(tagResult.getString("errcode"))){ //token失效 获取最新的token再次请求
            ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByAppId(appid);
            WxConfig wxConfig = wxConfigReturnData.getData();
            token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
            map.put("access_token", token);
            tagResult = httpTools.doGet(WxConstants.URL_GET_TAGS, map);
        }
        return tagResult;
    }

    /**
     * 根据公众号id和标签查询对应的标签名称
     *
     * @param appid   公众号id
     * @param tagList 标签集合
     * @return
     */
    @Override
    public List<String> queryTagName(String appid, List<String> tagList) {
        JSONObject tag = getTag(appid);
        List<String> tagNames = new ArrayList<>();
        JSONArray jsonArray = tag.getJSONArray("tags");
        for (int i =0; i< tagList.size(); i++) {
            String tagId = tagList.get(0);
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject tagRecord = jsonArray.getJSONObject(j);
                String id = tagRecord.getString("id");
                if (tagId.equals(id)) {
                    tagNames.add(tagRecord.getString("name"));
                }
            }
        }
        return tagNames;
    }

    /**
     * 根据appid查询公众号标签信息
     *
     * @param appid
     * @return
     */
    @Override
    public JSONObject query(String appid) {
        return getTag(appid);
    }
}
