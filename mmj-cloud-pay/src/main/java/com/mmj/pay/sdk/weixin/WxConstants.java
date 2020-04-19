package com.mmj.pay.sdk.weixin;

public class WxConstants {

    /**
     * 菜单栏客服咨询点击key值
     */
    public static String WX_CLICK_KEY_KFZX = "kfzx";
    
    /**
     * 公众号类型:Official Accounts
     */
    public static String OA = "OA";

    /**
     * 买买家公众号的touser
     */
    public static String WX_NO_MMJ = "gh_3c17885b942b";

    /**
     * 买买发公众号的touser
     */
    public static String WX_NO_MMF = "gh_1ce0dcb2bfe5";

    /**
     * token过期
     */
    public static String CODE_INVALID_TOKEN = "40001";

    /**
     * 微信接口成功返回的key值
     */
    public static String CODE_WX_SUCCESS_KEY = "errcode";

    /**
     * 微信接口成功返回的value值
     */
    public static String CODE_WX_SUCCESS_VALUE = "0";

    /**
     * 获取已关注的用户信息
     */
    public static String URL_GET_USERINFO_FOLLOW = "https://api.weixin.qq.com/cgi-bin/user/info";

    /**
     * 获取未关注的用户信息
     */
    public static String URL_GET_USERINFO_NO_FOLLOW = "https://api.weixin.qq.com/sns/userinfo";

    /**
     * 微信下单接口
     */
    public static String URL_GET_UNIFIEDORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 微信关闭接口
     */
    public static String URL_GET_CLOSEORDER = "https://api.mch.weixin.qq.com/pay/closeorder";

    /**
     * 下载对账单
     */
    public static String URL_GET_DOWNLOADBILL = "https://api.mch.weixin.qq.com/pay/downloadbill";

    /**
     * 查询订单
     */
    public static String URL_GET_ORDERQUERY = "https://api.mch.weixin.qq.com/pay/orderquery";

    /**
     * 获取api_token
     */
    public static String URL_GET_ACCESSTOKEN = "https://api.weixin.qq.com/cgi-bin/token";

    /**
     * 获取页面凭证
     */
    public static String URL_GET_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

    /**
     * 获取用户openid
     */
    public static String URL_GET_ACCESSTOKEN_USER = "https://api.weixin.qq.com/sns/oauth2/access_token";

    /**
     * 下载资金账单
     */
    public static String URL_GET_DOWNLOADFUNDFLOW = "https://api.mch.weixin.qq.com/pay/downloadfundflow";

    /**
     * 发送红包
     */
    public static String URL_GET_SENDREDPACK= "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";


    /**
     * 发送客服消息
     */
    public static String URL_GET_CUSTOMSEND ="https://api.weixin.qq.com/cgi-bin/message/custom/send";


    /**
     * 发送到零钱
     */
    public static String URL_GET_TRANSFERS = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

    /**
     * 创建菜单栏
     */
    public static String URL_CREATE_MENU_MOREN = "https://api.weixin.qq.com/cgi-bin/menu/create";

    /**
     * 获取菜单
     */
    public static String URL_GET_MENU = "https://api.weixin.qq.com/cgi-bin/menu/get";

    /**
     * 个性化菜单
     */
    public static String URL_CREATE_MENU = "https://api.weixin.qq.com/cgi-bin/menu/addconditional";


    /**
     * 删除菜单
     */

    public static String URL_DELETE_MENU = "https://api.weixin.qq.com/cgi-bin/menu/delete";


    /**
     * 创建用户标签
     */
    public static String URL_CREATE_TAGS = "https://api.weixin.qq.com/cgi-bin/tags/create";


    /**
     * 获取用户标签
     */
    public static String URL_GET_TAGS = "https://api.weixin.qq.com/cgi-bin/tags/get";

    /**
     * 批量修改用户标签
     */
    public static String URL_TAGS_BATCHTAG = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging";

    /**
     * 获取用户列表
     */
    public static String URL_USER_LIST = "https://api.weixin.qq.com/cgi-bin/user/get";


    /**
     * 取消用户标签
     */
    public static String URL_TAGS_MEMBERS = "https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging";


    /**
     * 给用户打标签
     */
    public static String URL_TAGS_BATCHTAGGING = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging";


    /**
     * 公众号添加材质
     */
    public static String URL_GET_ADD_MATERIAL= "https://api.weixin.qq.com/cgi-bin/material/add_material";


    /**
     * 小程序添加材质
     */
    public static String URL_GET_ADD_MEDIA= "https://api.weixin.qq.com/cgi-bin/media/upload";


    /**
     * 获取用户标签
     */
    public static String URL_GET_TAG = "https://api.weixin.qq.com/cgi-bin/tags/get";

    /**
     * 创建标签
     */
    public static String URL_CREATE_TAG = "https://api.weixin.qq.com/cgi-bin/tags/create";

    /**
     * 给用户批量打标签
     */
    public static String URL_BATCH_TAG = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging";

    /**
     * 公众号模板消息
     */
    public static String URL_SEND_MP_MSG = "https://api.weixin.qq.com/cgi-bin/message/template/send";

    /**
     * 获取用户列表
     */
    public static String URL_USER_GET = "https://api.weixin.qq.com/cgi-bin/user/get";

    /**
     * 公众号菜单栏创建
     */
    public static String URL_MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create";

    /**
     * 获取标签下的用户id
     */
    public static String URL_USER_TAG = "https://api.weixin.qq.com/cgi-bin/user/tag/get";
}
