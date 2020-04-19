package com.mmj.active.homeManagement.constant;

public interface RedisKey {

    /**
     * 顶部大图版本号(新用户)
     */
    String NEW_WEB_TOP_CODE = "newWebTopCode";

    /**
     * 顶部大图版本号(老用户非会员)
     */
    String OLD_WEB_TOP_CODE = "oldWebTopCode";

    /**
     * 顶部大图版本号(老用户会员)
     */
    String MEMBER_WEB_TOP_CODE = "memberWebTopCode";


    /**
     * 顶部大图缓存key
     */
    String WEB_TOP_KEY = "webTop";


    /**
     * 营销管理版本号(新用户)
     */
    String NEW_WEB_MAKETING_CODE = "newWebMaketingCode";

    /**
     * 营销管理版本号(老用户非会员)
     */
    String OLD_WEB_MAKETING_CODE = "oldWebMaketingCode";

    /**
     * 营销管理版本号(老用户会员)
     */
    String MEMBER_WEB_MAKETING_CODE = "memberWebMaketingCode";

    /**
     * 营销管理缓存key
     */
    String WEB_MAKETING_key = "webMaketing";


    /**
     * 橱窗管理版本号(新用户)
     */
    String NEW_WEB_SHOWCASE_CODE = "newWebShowcaseCode";

    /**
     * 橱窗管理版本号(老用户非会员)
     */
    String OLD_WEB_SHOWCASE_CODE = "oldWebShowcaseCode";

    /**
     * 橱窗管理版本号(老用户会员)
     */
    String MEMBER_WEB_SHOWCASE_CODE = "memberWebShowcaseCode";

    /**
     * 橱窗管理缓存key
     */
    String WEB_SHOWCASE_KEY = "webShowcase";

    /**
     * 橱窗管理关联商品缓存
     */
    String SHOWCASE_GOOD = "showcaseGOOD:";

    /**
     * 首页弹窗(小程序缓存)
     */
      String WEBALERT_REDIS_KEY = "webAlertRedisKey";

}
