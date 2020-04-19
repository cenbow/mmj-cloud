package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.exception.WxException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.HttpTools;
import com.mmj.common.utils.MD5Util;
import com.mmj.common.utils.StringUtils;
import com.mmj.notice.common.utils.WxTokenUtils;
import com.mmj.notice.feigin.WxMessageFeignClient;
import com.mmj.notice.mapper.WxMediaMapper;
import com.mmj.notice.model.WxConstants;
import com.mmj.notice.model.WxMedia;
import com.mmj.notice.model.WxMediaEx;
import com.mmj.notice.service.WxImageService;
import com.mmj.notice.service.WxMediaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 微信素材表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-22
 */
@Service
@Slf4j
public class WxMediaServiceImpl extends ServiceImpl<WxMediaMapper, WxMedia> implements WxMediaService {

    @Autowired
    HttpTools httpTools;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    WxMessageFeignClient wxMessageFeignClient;

    @Autowired
    WxTokenUtils wxTokenUtils;

    @Autowired
    WxImageService wxImageService;

    /**
     * 微信素材上传
     *
     * @param wxMedia
     * @return
     */
    @Override
    public WxMedia upload(WxMedia wxMedia) {
        String mediaType = wxMedia.getMediaType();
        if(WxMediaEx.mediaType.forever.name().equals(mediaType)){ //永久素材
            //先根据businessId和businessName查询是否有素材 有的话直接返回 没有的话就上传
            String businessId = MD5Util.MD5Encode(JSONObject.toJSONString(wxMedia), "utf-8");
            String businessName = wxMedia.getBusinessName();
            Wrapper<WxMedia> wxMediaWrapper = new EntityWrapper<>();
            wxMediaWrapper.eq("BUSINESS_ID", businessId).eq("BUSINESS_NAME", businessName).eq("MEDIA_TYPE", wxMedia.getMediaType())
                    .eq("APPID", wxMedia.getAppid());
            WxMedia storageWxMedia = selectOne(wxMediaWrapper);
            if(null != storageWxMedia){ //之前有素材 直接返回
                return storageWxMedia;
            }else {//之前没有素材 那么就新增
               doUpload(wxMedia);
            }
        }else if(WxMediaEx.mediaType.temporary.name().equals(mediaType)){ //临时素材 就只管新增
            doUpload(wxMedia);
        }
        wxMedia.setCreateTime(new Date());
        insert(wxMedia);
        return wxMedia;
    }

    /**
     * 执行上传素材操作
     * @param wxMedia
     * @return
     */
    private WxMedia doUpload(WxMedia wxMedia){
        String token = redisTemplate.opsForValue().get("access_token_" + wxMedia.getAppid());
        String mediaType = wxMedia.getMediaType();
        String url = "";
        if(WxMediaEx.mediaType.forever.name().equals(mediaType)){//永久素材
            url = WxConstants.URL_GET_ADD_MATERIAL;
        } else if(WxMediaEx.mediaType.temporary.name().equals(mediaType)){ //临时素材
            url = WxConstants.URL_GET_ADD_MEDIA;
        }
        JSONObject result = httpTools.postFile(url + "?access_token=" + token + "&type=image", wxMedia.getMediaUrl(), "image");
        if(WxConstants.CODE_INVALID_TOKEN.equals(result.getString("errcode"))){ //token失效 获取最新的token再次请求
            ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByAppId(wxMedia.getAppid());
            if(null != wxConfigReturnData && null != wxConfigReturnData.getData()){
                WxConfig wxConfig = wxConfigReturnData.getData();
                token = wxTokenUtils.reloadToken(wxConfig.getAppId(), wxConfig.getSecret());
            }
            result = httpTools.postFile(url + "?access_token=" + token + "&type=image", wxMedia.getMediaUrl(), "image");
        }
        String mediaId = result.getString("media_id");
        if(StringUtils.isEmpty(mediaId)){ //上传素材错误 抛出异常
            throw  new WxException(result.toJSONString());
        }
        wxMedia.setMediaId(mediaId);
        return wxMedia;
    }

    /**
     * 微信素材查询
     *
     * @param wxMedia
     * @return
     */
    @Override
    public WxMedia query(WxMedia wxMedia) {
        Wrapper<WxMedia> wxMediaWrapper = new EntityWrapper<>();
        wxMediaWrapper.eq("BUSINESS_ID", wxMedia.getBusinessId()).eq("BUSINESS_NAME", wxMedia.getBusinessName()).eq("MEDIA_TYPE", wxMedia.getMediaType())
                .eq("APPID", wxMedia.getAppid()).orderBy("CREATE_TIME desc");
        WxMedia storageWxMedia = selectOne(wxMediaWrapper);
        return storageWxMedia;
    }

    /**
     * 创建小程序里面的公众号二维码素材
     *
     * @param jsonObject
     * @return
     */
    @Override
    public WxMedia createQrcode(JSONObject jsonObject) {
        JSONObject mpJson = new JSONObject();
        mpJson.put("appid", jsonObject.getString("mpAppid")); //公众号的appid
        mpJson.put("sceneStr", jsonObject.getString("sceneStr")); //二维码上带的参数
        String qrcode = wxImageService.createQrcode(mpJson);
        WxMedia wxMedia = new WxMedia();
        String appid = jsonObject.getString("minAppid"); //小程序appid
        String businessId = jsonObject.getString("businessId"); //业务id
        String businessName = jsonObject.getString("businessName"); //业务名称
        String mediaType = "temporary"; //临时存储
        String mediaUrl = qrcode; //原始路径
        wxMedia.setAppid(appid);
        wxMedia.setBusinessId(businessId);
        wxMedia.setBusinessName(businessName);
        wxMedia.setMediaType(mediaType);
        wxMedia.setMediaUrl(mediaUrl);
        wxMedia = upload(wxMedia);
        return wxMedia;
    }

    /**
     * 删除大于三天的临时素材
     */
    @Override
    public void del() {
        EntityWrapper<WxMedia> entityWrapper = new EntityWrapper<>();
        Date beforeByMinute = DateUtils.getBeforeByMinute(2880); //删除大于两天的
        entityWrapper.eq("MEDIA_TYPE", "temporary").gt("CREATE_TIME", beforeByMinute);
        delete(entityWrapper);
    }
}
