package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.exception.WxException;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import com.mmj.common.utils.HttpTools;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.notice.common.utils.WechatMessageUtil;
import com.mmj.notice.common.utils.WxTokenUtils;
import com.mmj.notice.feigin.WxMessageFeignClient;
import com.mmj.notice.mapper.WxMenuMapper;
import com.mmj.notice.model.*;
import com.mmj.notice.service.WxMediaService;
import com.mmj.notice.service.WxMenuKeyService;
import com.mmj.notice.service.WxMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  公众号菜单栏配置表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-26
 */
@Service
public class WxMenuServiceImpl extends ServiceImpl<WxMenuMapper, WxMenu> implements WxMenuService {

    @Autowired
    WxMessageFeignClient wxMessageFeignClient;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    WxMenuKeyService wxMenuKeyService;

    @Autowired
    WxMediaService wxMediaService;

    @Autowired
    HttpTools httpTools;

    @Autowired
    WxTokenUtils wxTokenUtils;

    /**
     * 保存公众号菜单栏信息
     *
     * @param wxMenuExes
     * @return
     */
    @Override
    public void save(List<WxMenuEx>  wxMenuExes) {
        wxMenuExes.forEach(wxMenuEx -> {
            String token = redisTemplate.opsForValue().get("access_token_" + wxMenuEx.getAppid());
            JSONObject content = JSONObject.parseObject(wxMenuEx.getContent());
            JSONObject result = httpTools.doPost(WxConstants.URL_MENU_CREATE + "?access_token=" + token, content);
            if(WxConstants.CODE_INVALID_TOKEN.equals(result.getString("errcode"))){ //token失效 获取最新的token再次请求
                ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByAppId(wxMenuEx.getAppid());
                WxConfig wxConfig = wxConfigReturnData.getData();
                token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
                result = httpTools.doPost(WxConstants.URL_MENU_CREATE + "?access_token=" + token, content);
            }
            if(0 == result.getInteger("errcode")){ //创建菜单栏成功了
                JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
                wxMenuEx.setCreateUserId(userDetails.getUserId());
                wxMenuEx.setCreateUserName(userDetails.getUserFullName());
                wxMenuEx.setCreateTime(new Date());
                insert(wxMenuEx);
                Integer id = wxMenuEx.getId(); //生成的主键id
                List<WxMenuKey> wxMenuKeys = wxMenuEx.getWxMenuKeys();
                if(null !=  wxMenuKeys && !wxMenuKeys.isEmpty()){
                    for(WxMenuKey wxMenuKey : wxMenuKeys){
                        String replyType = wxMenuKey.getReplyType();
                        if(WechatMessageUtil.MESSAGE_IMG.equals(replyType)){ //如果是图片消息 那么要上传获取media_d
                            WxMedia wxMedia = new WxMedia();
                            wxMedia.setMediaType(WxMediaEx.mediaType.forever.name());
                            wxMedia.setBusinessName("wx_menu");
                            wxMedia.setAppid(wxMenuEx.getAppid());
                            wxMedia.setMediaUrl(wxMenuKey.getReplyImg());
                            WxMedia upload = wxMediaService.upload(wxMedia);
                            wxMenuKey.setReplyContent(upload.getMediaId());
                        }
                        wxMenuKey.setMenuId(id);
                        wxMenuKeyService.insert(wxMenuKey);
                    }
                }
            }else {// 返回异常给前端
                throw new WxException(result.getString("errmsg"));
            }
        });
    }

    /**
     * 查询公众号菜单栏配置信息
     *
     * @param appid
     * @return
     */
    @Override
    public WxMenuEx query(String appid) {
        Wrapper<WxMenu> wxMenuWrapper = new EntityWrapper<>();
        wxMenuWrapper.eq("APPID", appid).orderBy("CREATE_TIME desc");
        List<WxMenu> wxMenus = selectList(wxMenuWrapper);
        if(wxMenus.isEmpty()){
            return null;
        }
        WxMenu wxMenu = wxMenus.get(0);
        WxMenuEx wxMenuEx = JSONObject.parseObject(JSONObject.toJSONString(wxMenu), WxMenuEx.class);
        Integer id = wxMenuEx.getId();
        Wrapper<WxMenuKey> wxMenuKeyWrapper = new EntityWrapper<>();
        wxMenuKeyWrapper.eq("MENU_id", id);
        List<WxMenuKey> wxMenuKeys = wxMenuKeyService.selectList(wxMenuKeyWrapper);
        wxMenuEx.setWxMenuKeys(wxMenuKeys);
        return wxMenuEx;
    }
}
