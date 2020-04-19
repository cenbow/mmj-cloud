package com.mmj.user.userFocus.constants;

public interface UserFocusConstants {

    //关注状态
    String FOCUS_STATUS = "OFFICIAL:FOCUS:STATUS:";
    String FOCUS_OFFICIAL_MEDIA = "OFFICIAL:FOCUS:OFFICIAL_IMAGE:";
    String FOCUS_GROUP_MEDIA = "OFFICIAL:FOCUS:GROUP_MEDIA:";
    //群二维码图片  module_type
    String FOCUS_GROUP_IMAGE = "OFFICIAL:FOCUS:GROUP_IMAGE:";

    interface FocusStatus{
        //状态(0 未关注 1 已关注 2 已取消 3 取消后再关注 4 未授权已关注)
        Integer NO_FOCUS = 0;
        Integer FOCUS = 1;
        Integer CANCEL_FOCUS = 2;
        Integer RE_FOCUS = 3;
        Integer BEFORE_FOCUS = 4;
    }

    interface FocusReward{
        //奖励获取状态(0 未获取 1 已获取 2 已使用)
        Integer NOT_GET = 0;
        Integer GOT = 1;
        Integer USED = 2;
    }

    interface FocusForm{
        Integer GROUP = 0;      //群
        Integer OFFICIAL = 1;   //公众号
    }

    interface mediaType{
        //素材类型
        String FOREVER = "forever";
        String TEMPORARY = "temporary";
    }
}
