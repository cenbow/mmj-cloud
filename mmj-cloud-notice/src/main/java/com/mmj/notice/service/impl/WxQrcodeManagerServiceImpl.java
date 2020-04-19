package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.exception.WxException;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.common.utils.StringUtils;
import com.mmj.notice.common.utils.WechatMessageUtil;
import com.mmj.notice.mapper.WxQrcodeManagerMapper;
import com.mmj.notice.model.WxMedia;
import com.mmj.notice.model.WxQrcodeManager;
import com.mmj.notice.service.WxImageService;
import com.mmj.notice.service.WxMediaService;
import com.mmj.notice.service.WxQrcodeManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 公众号二维码 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-13
 */
@Service
public class WxQrcodeManagerServiceImpl extends ServiceImpl<WxQrcodeManagerMapper, WxQrcodeManager> implements WxQrcodeManagerService {

    @Autowired
    WxMediaService wxMediaService;

    @Autowired
    WxImageService wxImageService;

    /**
     * 公众号二维码保存
     *
     * @param wxQrcodeManager
     */
    @Override
    public void save(WxQrcodeManager wxQrcodeManager) {
        //首先查询之前有没有 有的话就提示 没有的话就创建
        if(null == wxQrcodeManager.getId()){
            EntityWrapper<WxQrcodeManager> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("APP_ID", wxQrcodeManager.getAppId()).eq("CHANNEL_NAME", wxQrcodeManager.getChannelName());
            if(null != selectOne(entityWrapper)){ //如果该公众号下有此渠道名
                throw  new WxException("该公众号下已经存在此渠道了");
            };
        }
        String appId = wxQrcodeManager.getAppId();
        String replyOneType = wxQrcodeManager.getReplyOneType();
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        //如果回复1是图片消息或者图文消息
        if(WechatMessageUtil.MESSAGE_IMG.equals(replyOneType) || WechatMessageUtil.MESSAGE_MIN_PAGE.equals(replyOneType)){
            String mediaId = getMediaId(appId, wxQrcodeManager.getReplyOneImg());
            String replyOneContent = wxQrcodeManager.getReplyOneContent();
            JSONObject replyJson = JSON.parseObject(replyOneContent);
            if(WechatMessageUtil.MESSAGE_IMG.equals(replyOneType)){ //图文消息
                replyJson.getJSONObject("image").put("media_id", mediaId);
            }else { //小程序消息
                replyJson.getJSONObject("miniprogrampage").put("thumb_media_id", mediaId);
            }
            wxQrcodeManager.setReplyOneContent(replyJson.toJSONString());
        }
        String replyTwoType = wxQrcodeManager.getReplyTwoType();
        //如果回复2是图片消息或者图文消息
        if(WechatMessageUtil.MESSAGE_IMG.equals(replyTwoType) || WechatMessageUtil.MESSAGE_MIN_PAGE.equals(replyTwoType)){
            String mediaId = getMediaId(appId, wxQrcodeManager.getReplyTwoImg());
            String replyTwoContent = wxQrcodeManager.getReplyTwoContent();
            JSONObject replyJson = JSON.parseObject(replyTwoContent);
            if(WechatMessageUtil.MESSAGE_IMG.equals(replyTwoType)){ //图文消息
                replyJson.getJSONObject("image").put("media_id", mediaId);
            }else { //小程序消息
                replyJson.getJSONObject("miniprogrampage").put("thumb_media_id", mediaId);
            }
            wxQrcodeManager.setReplyTwoContent(replyJson.toJSONString());
        }
        String replyThridType = wxQrcodeManager.getReplyThridType();
        //如果回复3是图片消息或者图文消息
        if(WechatMessageUtil.MESSAGE_IMG.equals(replyThridType) || WechatMessageUtil.MESSAGE_MIN_PAGE.equals(replyThridType)){
            String mediaId = getMediaId(appId, wxQrcodeManager.getReplyThridImg());
            String replyThridContent = wxQrcodeManager.getReplyThridContent();
            JSONObject replyJson = JSON.parseObject(replyThridContent);
            if(WechatMessageUtil.MESSAGE_IMG.equals(replyOneType)){ //图文消息
                replyJson.getJSONObject("image").put("media_id", mediaId);
            }else { //小程序消息
                replyJson.getJSONObject("miniprogrampage").put("thumb_media_id", mediaId);
            }
            wxQrcodeManager.setReplyThridContent(replyJson.toJSONString());
        }
        //设置更新人信息
        wxQrcodeManager.setUpdateTime(new Date());
        wxQrcodeManager.setUpdateId(userDetails.getUserId());
        wxQrcodeManager.setUpdateName(userDetails.getUserFullName());
        Integer id = wxQrcodeManager.getId();
        //设置创建人信息
        if(null == id){
            wxQrcodeManager.setCreateTime(new Date());
            wxQrcodeManager.setCreateId(userDetails.getUserId());
            wxQrcodeManager.setCreateName(userDetails.getUserFullName());
        }
        JSONObject params = new JSONObject();
        params.put("appid", wxQrcodeManager.getAppId());
        params.put("sceneStr", WxQrcodeRecordMpMessageService.eventKeyPrefix + wxQrcodeManager.getQrcodeName() + "_" + wxQrcodeManager.getChannelName());
        String image = wxImageService.createQrcode(params);
        wxQrcodeManager.setPath(image);
        insertOrUpdate(wxQrcodeManager);
    }

    /**
     * 获取公众号对应的图片素材
     * @param appid
     * @param img
     * @return
     */
    private String getMediaId(String appid, String img){
        WxMedia wxMedia = new WxMedia();
        wxMedia.setAppid(appid);
        wxMedia.setBusinessName("公众号二维码管理");
        wxMedia.setMediaType("forever");
        wxMedia.setMediaUrl(img);
        WxMedia upload = wxMediaService.upload(wxMedia);
        return upload.getMediaId();
    }

    /**
     * 分页查询公众号二维码列表信息
     *
     * @param wxQrcodeManager
     * @return
     */
    @Override
    public Page<WxQrcodeManager> queryPage(WxQrcodeManager wxQrcodeManager) {
        EntityWrapper<WxQrcodeManager> wxQrcodeManagerEntityWrapper = new EntityWrapper<>();
        wxQrcodeManagerEntityWrapper.orderBy("UPDATE_TIME desc");
        String qrcodeName = wxQrcodeManager.getQrcodeName();
        if(StringUtils.isNotEmpty(qrcodeName)){ //二维码名称
            wxQrcodeManagerEntityWrapper.eq("QRCODE_NAME", qrcodeName);
        }
        Integer id = wxQrcodeManager.getId();
        if(null != id){ //二维码id
            wxQrcodeManagerEntityWrapper.eq("ID", id);
        }
        String appId = wxQrcodeManager.getAppId();
        if(StringUtils.isNotEmpty(appId)){ //公众号
            wxQrcodeManagerEntityWrapper.eq("APP_ID", appId);
        }
        String channelId = wxQrcodeManager.getChannelId();
        if(null != channelId){//渠道id
            wxQrcodeManagerEntityWrapper.eq("CHANNEL_ID", channelId);
        }
        Page<WxQrcodeManager> page = new Page<>( wxQrcodeManager.getCurrentPage(), wxQrcodeManager.getPageSize());
        page = selectPage(page, wxQrcodeManagerEntityWrapper);
        return page;
    }

    /**
     * 根据id查询公众号二维码信息
     *
     * @param id
     * @return
     */
    @Override
    public WxQrcodeManager query(String id) {
        return selectById(id);
    }
}
