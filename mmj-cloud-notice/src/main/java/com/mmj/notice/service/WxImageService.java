package com.mmj.notice.service;

import com.alibaba.fastjson.JSONObject;

public interface WxImageService {

    /**
     * 绘图
     * @param params
     * @return
     */
    String createImage(JSONObject params);

    /**
     * 创建小程序码
     * @param params
     * @return
     */
    String createQrcodeM(JSONObject params);

    /**
     * 会员返现图合成
     * @return
     */
    String memberFx();

    /**
     * 创建公众号码
     * @param parseObject
     * @return
     */
    String createQrcode(JSONObject parseObject);

    /**
     * 会员商品推荐图合成
     * @param id
     * @return
     */
    String memberRecmond(String id);

    /**
     * 物流箱专属红包码生成
     * @param openCode
     * @param nickName
     * @param headImg
     * @return
     */
    String createBoxOpenCode(String openCode, String nickName, String headImg);

    /**
     * 免费送图片合成
     * @param url
     * @return
     */
    String freeGoodsCompose(String url);
}
