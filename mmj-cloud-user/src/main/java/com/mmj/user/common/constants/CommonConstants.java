package com.mmj.user.common.constants;

public interface CommonConstants {
    interface WeCat{
        String MSG_PREFIX = "qrscene_";
    }

    interface WeCatEvent{
        //关注
        String SUBSCRIBE = "subscribe";
        //取消关注
        String UNSUBSCRIBE = "unsubscribe";
        //已经关注
        String SCAN = "SCAN";
        //进入会话事件
        String USER_ENTER_TEMPSESSION = "user_enter_tempsession";
    }

    interface WeCatMsgType{
        /**
         * 消息类型
         *  公众号:
         *      文本     text
         *      图片     image
         *      语音     voice
         *      视频     video
         *      小视频   shortvideo
         *      地理位置 location
         *      事件     event
         *
         *  小程序
         *      文本消息    text
         *      图片消息    image
         *      小程序卡片消息 miniprogrampage
         *      进入会话事件  event
         */
        String TEXT = "text";
        String IMAGE = "image";
        String VOICE = "voice";
        String VIDEO = "video";
        String SHORTVIDEO = "shortvideo";
        String LOCATION = "location";
        String EVENT = "event";
        String MINIPROGRAMPAGE = "miniprogrampage";

        //流量池消息
        String FLOW_POOL_FP = "FP1902_";
        String FLOW_POOL_GP = "GP1902_";
    }
}
