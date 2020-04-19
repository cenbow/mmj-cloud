package com.mmj.notice.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.notice.model.WxMedia;

/**
 * <p>
 * 微信素材表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-22
 */
public interface WxMediaService extends IService<WxMedia> {

    /**
     * 微信素材上传
     * @param wxMedia
     * @return
     */
    WxMedia upload(WxMedia wxMedia);

    /**
     * 微信素材查询
     * @param wxMedia
     * @return
     */
    WxMedia query(WxMedia wxMedia);

    /**
     * 创建小程序里面的公众号二维码素材
     * @param jsonObject
     * @return
     */
    WxMedia createQrcode(JSONObject jsonObject);

    /**
     * 删除大于三天的临时素材
     */
    void del();
}
