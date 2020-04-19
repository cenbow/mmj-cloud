package com.mmj.active.common.config;

import com.alibaba.fastjson.JSONObject;
import com.mmj.active.common.feigin.WxMessageFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class GzhNotifyConfig {

    Logger logger = LoggerFactory.getLogger(GzhNotifyConfig.class);
    
    @Autowired
    private WeChatTmpMsgConfig weChatTmpMsgConfig;

    @Autowired
    private WxMessageFeignClient wxMessageFeignClient;

    //小程序appid
    private String min_appid;

    //公众号appid
    private String official_appid;

    @PostConstruct
    public void init(){
        min_appid = weChatTmpMsgConfig.getMinAppid();
        official_appid = weChatTmpMsgConfig.getOfficialAppid();
    }


    /**
     * 秒杀-订阅秒杀提醒
     * <p>
     * 标题
     * 预约成功通知
     * 详细内容
     * {{first.DATA}}
     * 商品名称：{{keyword1.DATA}}
     * 抢购开始时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * eg:
     * 预约成功通知
     * 秒杀活动提醒预约成功
     * 商品名称：读取商品名称
     * 抢购开始时间：读取抢购开始时间
     * 将于秒杀活动开始前5分钟进行提醒。点击回小程序继续购物
     * <p>
     * to:限时秒杀活动页
     *
     * @param openId    用户id
     * @param startTime 开始时间
     * @param goodName  商品名称
     */
    public String bookSend1(String openId, String startTime, String goodName) {
        Map<String, Object> map = new HashMap<>();
        map.put("appid", official_appid);
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getSeckillBook());

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgTimeSeckill/main");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "秒杀活动提醒预约成功");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", goodName);
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();
        keyword2.put("value", startTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>将于秒杀活动开始前5分钟进行提醒。点击回小程序继续购物");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        return json.toJSONString();
    }


    /**
     * 砍价-订阅砍价进度(首次砍价进度模板消息进入页面)
     * <p>
     * 标题
     * 开通成功通知
     * 详细内容
     * {{first.DATA}}
     * 服务名称：{{keyword1.DATA}}
     * 开通时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * 开通成功通知
     * 砍价进度服务已订阅，您发起的砍价每次有人帮砍时，您将收到砍价进度通知
     * 服务名称：砍价进度通知
     * 开通时间：2019-03-05
     * 点击回小程序继续购物
     * <p>
     * to:砍价详情页
     *
     * @param openId    用户id
     * @param startTime 开始时间
     * @param orderId   砍价id
     */
    public String bookSend2(String openId, String startTime, String orderId) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getCutBook());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgMarkdownFreeGet/cutProduct/main?cutNo=" + orderId + "&selfCut=1");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "砍价进度服务已订阅，您发起的砍价每次有人帮砍时，您将收到砍价进度通知");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", "砍价进度通知");
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();
        keyword2.put("value", startTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击回小程序继续购物");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        return json.toJSONString();
    }

    /**
     * 抽奖-开奖区开启抽奖提醒
     * <p>
     * 标题
     * 开通成功通知
     * 详细内容
     * {{first.DATA}}
     * 服务名称：{{keyword1.DATA}}
     * 开通时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * eg:
     * 开通成功通知
     * 恭喜您订阅大额奖品通知成功，我们将在有iPhone等大额奖品时通知您参与活动
     * 服务名称：大额奖品通知
     * 开通时间：2019-03-05
     * 点击回小程序继续购物
     * <p>
     * to:抽奖活动页
     *
     * @param openId
     * @param startTime
     */
    public String bookSend3(String openId, String startTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getLotteryOpenBook());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgLottery/lottery/main?activeTab=1&ed=1");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "恭喜您订阅大额奖品通知成功，我们将在有iPhone等大额奖品时通知您参与活动");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", "大额奖品通知");
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();
        keyword2.put("value", startTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击回小程序继续购物");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        return json.toJSONString();
    }

    /**
     * 抽奖-开奖公示区开启开奖提醒
     * <p>
     * 标题
     * 开通成功通知
     * 详细内容
     * {{first.DATA}}
     * 服务名称：{{keyword1.DATA}}
     * 开通时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * eg:
     * 开通成功通知
     * 恭喜您订阅开奖结果通知成功，我们将在您参与抽奖活动开奖时，进行开奖结果通知
     * 服务名称：开奖结果通知
     * 开通时间：2019-03-05
     * 点击回小程序继续购物
     * <p>
     * to:抽奖活动页
     *
     * @param openId
     * @param startTime
     */
    public String bookSend4(String openId, String startTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getLotteryPublicBook());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgLottery/lottery/main?activeTab=1&ed=1");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "恭喜您订阅开奖结果通知成功，我们将在您参与抽奖活动开奖时，进行开奖结果通知");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", "开奖结果通知");
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();
        keyword2.put("value", startTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击回小程序继续购物");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        return json.toJSONString();
    }

    /**
     * 抽奖-领取优惠券
     * <p>
     * 标题
     * 抽奖结果通知
     * 详细内容
     * {{first.DATA}}
     * 奖品名称：{{keyword1.DATA}}
     * 中奖时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * 抽奖结果通知
     * 恭喜您抽中二等奖，将会为您全额退款，并获得一张XX元优惠券
     * 奖品名称：XX元优惠券
     * 中奖时间：2019-03-05
     * 点击回小程序去使用优惠券
     * <p>
     * to:我的优惠券
     *
     * @param openId
     * @param couponName
     * @param startTime
     */
    public String bookSend5(String openId, String couponName, String startTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getLotteryCouponBook());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pages/index/main?redirect=%2FpkgOrder%2FmyCoupons%2Fmain");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "恭喜您抽中二等奖，将会为您全额退款，并获得一张" + couponName);
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", couponName);
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();// 开始时间
        keyword2.put("value", startTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击回小程序去使用优惠券");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        return json.toJSONString();
    }


    /**
     * 转盘-转盘签到订阅提醒
     * <p>
     * 标题
     * 开通成功通知
     * 详细内容
     * {{first.DATA}}
     * 服务名称：{{keyword1.DATA}}
     * 开通时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * eg:
     * 开通成功通知
     * 恭喜您订阅签到提醒成功，我们将在每天10点进行签到提醒，每天获得买买币大奖转不停
     * 服务名称：签到提醒
     * 开通时间：2019-03-05
     * 点击回小程序继续参与转盘大抽奖
     * <p>
     * to:转盘活动页
     *
     * @param openId    用户id
     * @param startTime 开通时间
     */
    public String bookSend6(String openId, String startTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getPrizewheelsSignBook());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pages/index/main?redirect=%2FpkgTurntable%2Fmain");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "恭喜您订阅签到提醒成功，我们将在每天10点进行签到提醒，每天获得买买币大奖转不停");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", "签到提醒");
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();
        keyword2.put("value", startTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击回小程序继续参与转盘大抽奖");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        return json.toJSONString();
    }

    /**
     * 转盘-转盘十元结果翻倍
     * <p>
     * 标题
     * 申请提交成功通知
     * 详细内容
     * {{first.DATA}}
     * 服务类型：{{keyword1.DATA}}
     * 提交时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * 申请提交成功通知
     * 您通过转盘获得的十元红包金额已经增加
     * 服务类型：转盘红包金额翻倍机会
     * 提交时间：2019-03-05
     * 点击回小程序查看红包增加金额
     * <p>
     * to:转盘活动页-展示翻倍后的弹窗
     *
     * @param openId
     * @param startTime
     */
    public String bookSend7(String openId, String startTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getPrizewheelsTenBook());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pages/index/main?redirect=%2FpkgTurntable%2Fmain");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "您通过转盘获得的十元红包金额已经增加");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", "转盘红包金额翻倍机会");
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();
        keyword2.put("value", startTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击回小程序查看红包增加金额");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        return json.toJSONString();

    }

    /**
     * 十元三件-关注后获得购买机会
     * <p>
     * 标题
     * 申请提交成功通知
     * 详细内容
     * {{first.DATA}}
     * 服务类型：{{keyword1.DATA}}
     * 提交时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * 申请提交成功通知
     * 恭喜您成功获取一次十元三件购买机会
     * 服务类型：十元三件购买机会
     * 提交时间：2019-03-05
     * 回小程序去选购十元三件商品
     * <p>
     * to:十元三件活动页
     *
     * @param openId    用户id
     * @param startTime 提交时间
     */
    public String bookSend8(String openId, String startTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getTenSaleBook());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pages/index/main?redirect=%2FpkgProbationFree%2Fmain");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "恭喜您成功获取一次十元三件购买机会");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", "十元三件购买机会");
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();
        keyword2.put("value", startTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>回小程序去选购十元三件商品");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        return json.toJSONString();
    }


    /**
     * 签到-订阅签到提醒
     * <p>
     * 标题
     * 开通成功通知
     * 详细内容
     * {{first.DATA}}
     * 服务名称：{{keyword1.DATA}}
     * 开通时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * 开通成功通知
     * 恭喜您订阅签到提醒成功，我们将在每天9点进行签到提醒，不错过每一次赚签到金机会
     * 服务名称：签到提醒
     * 开通时间：2019-03-05
     * 点击回小程序继续购物
     * <p>
     * to:签到活动页
     *
     * @param openId
     * @param startTime
     */
    public String bookSend9(String openId, String startTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getSignBook());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgSignInGetCash/siginIn/main");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "恭喜您订阅签到提醒成功，我们将在每天9点进行签到提醒，不错过每一次赚签到金机会");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", "签到提醒");
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();
        keyword2.put("value", startTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击回小程序继续购物");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        return json.toJSONString();
    }


    /**
     * 签到-签到后获得一次签到机会
     * <p>
     * 标题
     * 签到成功通知
     * 详细内容
     * {{first.DATA}}
     * 签到内容：{{keyword1.DATA}}
     * 签到用户：{{keyword2.DATA}}
     * 签到时间：{{keyword3.DATA}}
     * {{remark.DATA}}
     * <p>
     * 签到成功通知
     * 签到成功，获得额外签到金XX元
     * 签到内容：签到金XX元
     * 签到用户：用户微信昵称
     * 签到时间：2019-03-05
     * 点击回小程序使用签到机会
     * <p>
     * to:签到活动页
     *
     * @param openId   用户id
     * @param userName 用户名称
     * @param signTime 签到时间
     */
    public String bookSend10(String openId, String userName, String signTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getSignChanceBook());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgSignInGetCash/siginIn/main");


        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "关注公众号签到成功，获得一次额外签到机会");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", "获得一次签到机会");
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();
        keyword2.put("value", userName);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> keyword3 = new HashMap<>();
        keyword3.put("value", signTime);
        keyword3.put("color", "#173177");
        data.put("keyword3", keyword3);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击回小程序使用签到机会");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        return json.toJSONString();
    }

    /**
     * 店铺订单-订阅物流进度提醒
     * <p>
     * 标题
     * 开通成功通知
     * 详细内容
     * {{first.DATA}}
     * 服务名称：{{keyword1.DATA}}
     * 开通时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * 开通成功通知
     * 恭喜您订阅物流进度提醒成功，我们将在您订单有物流更新时进行提醒，实时掌握您物品状态
     * 服务名称：物流进度提醒
     * 开通时间：2019-03-05
     * 点击回小程序继续购物
     * <p>
     * to：订单详情页
     *
     * @param openId
     * @param startTime
     * @param orderNo
     */
    public String bookSend11(String openId, String startTime, String orderNo) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getOrderExpressBook());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pages/index/main?redirect=%2FpkgOrder%2ForderDetail%2Fmain%3ForderNo%" + orderNo);

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "恭喜您订阅物流进度提醒成功，我们将在您订单有物流更新时进行提醒，实时掌握您物品状态");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", "物流进度提醒");
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();
        keyword2.put("value", startTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击回小程序继续购物");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        return json.toJSONString();
    }


    /**
     * 秒杀-活动开启时提醒
     * <p>
     * 标题
     * 预约成功通知
     * 详细内容
     * {{first.DATA}}
     * 商品名称：{{keyword1.DATA}}
     * 抢购开始时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * 标题
     * 活动开启时提醒
     * 预约成功通知
     * 秒杀活动提醒预约成功
     * 商品名称：读取商品名称
     * 抢购开始时间：读取抢购开始时间
     * 您关注的秒杀活动还有5分钟就要开始啦。点击回小程序去抢购
     * <p>
     * to:限时秒杀活动页，即将开始页面
     *
     * @param openId    用户id
     * @param startTime 抢购开始时间
     * @param goodName  商品名称
     */
    public String sendAfter1(String openId, String startTime, String goodName) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getSeckillAfterOpen());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgTimeSeckill/main&ed=1");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "秒杀活动提醒预约成功");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();// 商品名称
        keyword1.put("value", goodName);
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();// 抢购开始时间
        keyword2.put("value", startTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", "您关注的秒杀活动还有5分钟就要开始啦。点击回小程序去抢购");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);
        String s = json.toJSONString();
        logger.error("--------秒杀活动提醒预约:{}", s);
        wxMessageFeignClient.sendTemplate(s);
        return s;
    }


    /**
     * 砍价-有好友帮砍价时(还剩10%)
     * <p>
     * 编号
     * OPENTM410292733
     * 标题
     * 砍价成功提醒
     * {{first.DATA}}
     * 商品名称：{{keyword1.DATA}}
     * 底价：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * 标题
     * 砍价成功提醒
     * 您的好友【好友微信昵称】帮您砍了一刀，砍价金额2.22元。还差111元即可砍到底价
     * 商品名称：读取当前砍价商品名称
     * 底价：读取当前砍价底价
     * 点击继续邀请好友帮忙砍价
     * <p>
     * to:砍价详情页
     *
     * @param openId       用户id
     * @param orderId      砍价id
     * @param userName     用户名
     * @param bargainMoney 本次砍价金额
     * @param surplusMoney 剩余金额
     * @param goodName     商品名称
     * @param lowestMoney  底价
     */
    public String sendAfter2(String openId, String orderId, String userName, String bargainMoney, String surplusMoney, String goodName, String lowestMoney) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getCutAfterHelp());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgMarkdownFreeGet/cutProduct/main?cutNo=" + orderId + "&selfCut=1");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "您的好友" + userName + "帮您砍了一刀，砍价金额" + bargainMoney + "元。还差" + surplusMoney + "元即可砍到底价");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();// 商品名称
        keyword1.put("value", goodName);
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();// 底价
        keyword2.put("value", lowestMoney);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击继续邀请好友帮忙砍价");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        String s = json.toJSONString();
        wxMessageFeignClient.sendTemplate(s);
        return s;
    }


    /**
     * 抽奖-活动上新(每周一次)
     * <p>
     * 标题
     * 开奖结果通知
     * 详细内容
     * {{first.DATA}}
     * 活动奖品：{{keyword1.DATA}}
     * 开奖时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * 开奖结果通知
     * 抽奖活动上新了大额抽奖商品，邀请您参与抽大奖
     * 活动奖品：商品名称
     * 开奖时间：2019-04-01
     * 点击即可参与商品名称的抽奖，把大奖带回家
     * <p>
     * to:抽奖活动页
     *
     * @param openId    用户id
     * @param goodName  活动奖品
     * @param awardTime 开奖时间
     */
    public String sendAfter3(String openId, String goodName, String awardTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getLotteryAfterOnceWeek());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgLottery/lottery/main?activeTab=1&ed=1");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "抽奖活动上新了大额抽奖商品，邀请您参与抽大奖");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();// 商品名称
        keyword1.put("value", goodName);
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();// 开奖时间
        keyword2.put("value", awardTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击即可参与商品名称的抽奖，把大奖带回家");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        String s = json.toJSONString();
        wxMessageFeignClient.sendTemplate(s);
        return s;
    }


    /**
     * 抽奖-参与的抽奖活动开奖时(未中奖)
     * <p>
     * 标题
     * 开奖结果通知
     * 详细内容
     * {{first.DATA}}
     * 活动奖品：{{keyword1.DATA}}
     * 开奖时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * 开奖结果通知
     * 您参与的抽奖活动已经开奖，您未中奖，将全额为您退款，并获得XX元优惠券
     * 活动奖品：商品名称
     * 开奖时间：2019-04-01
     * 点击领取XX元优惠券
     * <p>
     * to:我的优惠券
     *
     * @param openId     用户id
     * @param goodName   活动奖品
     * @param awardTime  开奖时间
     * @param couponName 优惠券
     */
    public String sendAfter4(String openId, String goodName, String awardTime, String couponName) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getLotteryAfterOpen());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgLottery/lottery/main?activeTab=2&ed=1");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "您参与的抽奖活动已经开奖，您未中奖，将全额为您退款，并获得" + couponName + "元优惠券");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();// 商品名称
        keyword1.put("value", goodName);
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();// 开奖时间
        keyword2.put("value", awardTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击领取" + couponName + "元优惠券");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        String s = json.toJSONString();
        wxMessageFeignClient.sendTemplate(s);
        return s;
    }

    /**
     * 转盘-转盘签到标签用户 当天未签到用户 每天10点进行提醒
     * <p>
     * 标题
     * 签到提醒
     * 详细内容
     * {{first.DATA}}
     * 活动名称：{{keyword1.DATA}}
     * 活动时间：{{keyword2.DATA}}
     * 报名信息：{{keyword3.DATA}}
     * 签到信息：{{keyword4.DATA}}
     * 签到时间：{{keyword5.DATA}}
     * {{remark.DATA}}
     * <p>
     * 签到提醒
     * 该签到啦，快去签到吧
     * 活动名称：转盘签到
     * 活动时间：永久
     * 报名信息：已开启签到提醒
     * 签到信息：当天未签到
     * 签到时间：已连续签到0天
     * 点击即可签到赚买买币
     * <p>
     * to:转盘活动页
     *
     * @param openId 用户id
     * @param days   连续签到天数
     */
    public String sendAfter5(String openId, String days) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getPrizewheelsAfterRemind());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgTurntable/main");


        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "该签到啦，快去签到吧");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", "转盘签到");
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();
        keyword2.put("value", "永久");
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> keyword3 = new HashMap<>();
        keyword3.put("value", "已开启签到提醒");
        keyword3.put("color", "#173177");
        data.put("keyword3", keyword3);

        Map<String, Object> keyword4 = new HashMap<>();
        keyword4.put("value", "当天未签到");
        keyword4.put("color", "#173177");
        data.put("keyword4", keyword4);

        Map<String, Object> keyword5 = new HashMap<>();
        keyword5.put("value", "已连续签到" + days + "天");
        keyword5.put("color", "#173177");
        data.put("keyword5", keyword5);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击即可签到赚买买币");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        String s = json.toJSONString();
        wxMessageFeignClient.sendTemplate(s);
        return s;
    }


    /**
     * 十元三件-已经重新获购买资格
     * <p>
     * {{first.DATA}}
     * 服务名称：{{keyword1.DATA}}
     * 开通时间：{{keyword2.DATA}}
     * {{remark.DATA}}
     * <p>
     * 恭喜，已经重新获得十元三件购买资格！
     * 服务名称：十元三件
     * 开通时间：2018年3月15日
     * 马上去选购
     * <p>
     * to:十元三件活动页
     *
     * @param openId    用户id
     * @param startTime 开始时间 2018年3月15日
     */
    public String sendAfter6(String openId, String startTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getTenAfterRefresh());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pages/index/main?redirect=%2FpkgProbationFree%2Fmain");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "恭喜，已经重新获得十元三件购买资格！");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", "十元三件");
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();// 开始时间
        keyword2.put("value", startTime);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>马上去选购");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        String s = json.toJSONString();
        wxMessageFeignClient.sendTemplate(s);
        return s;
    }


    /**
     * 签到-签到提醒标签用户 当天未签到用户 每天9点进行提醒
     * <p>
     * 标题
     * 签到提醒
     * 详细内容
     * {{first.DATA}}
     * 活动名称：{{keyword1.DATA}}
     * 活动时间：{{keyword2.DATA}}
     * 报名信息：{{keyword3.DATA}}
     * 签到信息：{{keyword4.DATA}}
     * 签到时间：{{keyword5.DATA}}
     * {{remark.DATA}}
     * <p>
     * 签到提醒
     * 该签到啦，快去签到吧
     * 活动名称：签到赚签到金
     * 活动时间：永久
     * 报名信息：已开启签到提醒
     * 签到信息：当天未签到
     * 签到时间：已连续签到0天
     * 点击即可签到赚签到金
     * <p>
     * to:签到活动页
     *
     * @param openId 用户id
     * @param days   连续签到天数
     */
    public String sendAfter7(String openId, String days) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getSignAfterRemind());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgSignInGetCash/siginIn/main");

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "该签到啦，快去签到吧");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();// 服务名称
        keyword1.put("value", "签到赚签到金");
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();// 服务名称
        keyword2.put("value", "永久");
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> keyword3 = new HashMap<>();// 服务名称
        keyword3.put("value", "已开启签到提醒");
        keyword3.put("color", "#173177");
        data.put("keyword3", keyword3);

        Map<String, Object> keyword4 = new HashMap<>();// 服务名称
        keyword4.put("value", "当天未签到");
        keyword4.put("color", "#173177");
        data.put("keyword4", keyword4);

        Map<String, Object> keyword5 = new HashMap<>();// 开始时间
        keyword5.put("value", "已连续签到" + days + "天");
        keyword5.put("color", "#173177");
        data.put("keyword5", keyword5);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击即可签到赚签到金");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        String s = json.toJSONString();
        wxMessageFeignClient.sendTemplate(s);
        return s;
    }


    /**
     * 签到-好友帮签到成功时
     * <p>
     * 签到成功通知
     * {{first.DATA}}
     * 签到内容：{{keyword1.DATA}}
     * 签到用户：{{keyword2.DATA}}
     * 签到时间：{{keyword3.DATA}}
     * {{remark.DATA}}
     * <p>
     * 签到成功通知
     * 签到内容：好友帮您签到成功，各自获得签到金15.55元
     * 签到用户：微信昵称
     * 签到时间：2019年7月21日 18:36
     * 分享给从未参与的用户可以获得更多签到金哦，快去分享吧！
     * <p>
     * to:签到页
     *
     * @param openId    用户id
     * @param userName  用户名
     * @param startTime 签到时间
     * @param signMoney 签到金额
     */
    public String sendAfter8(String openId, String userName, String startTime, String signMoney) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getSignAfterHelp());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgSignInGetCash/siginIn/main");


        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "签到成功通知");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();// 服务名称
        keyword1.put("value", "好友帮您签到成功，各自获得签到金" + signMoney + "元");
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();// 签到用户
        keyword2.put("value", userName);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);

        Map<String, Object> keyword3 = new HashMap<>();// 签到时间
        keyword3.put("value", startTime);
        keyword3.put("color", "#173177");
        data.put("keyword3", keyword3);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", "分享给从未参与的用户可以获得更多签到金哦，快去分享吧！");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        String s = json.toJSONString();
        wxMessageFeignClient.sendTemplate(s);
        return s;
    }


    /**
     * 店铺订单-物流状态更新时
     * <p>
     * 标题
     * 物流状态更新通知
     * 详细内容
     * {{first.DATA}}
     * 商品摘要：{{keyword1.DATA}}
     * 发货单号：{{keyword2.DATA}}
     * 物流商：{{keyword3.DATA}}
     * {{remark.DATA}}
     * <p>
     * 物流状态更新通知
     * 您的订单物流状态有更新，请查阅详情
     * 商品摘要：商品名称
     * 发货单号：9887767577
     * 物流商：韵达快递
     * 点击回小程序查看订单详情
     * <p>
     * to:订单详情页
     *
     * @param openId        用户id
     * @param goodName      商品名称
     * @param courierNumber 发货单号
     * @param courierName   快递公司
     */
    public String sendAfter9(String openId, String goodName, String courierNumber, String courierName, String orderNo) {
        Map<String, Object> map = new HashMap<>();
        map.put("touser", openId);
        map.put("template_id", weChatTmpMsgConfig.getOrderAfterExpressRefresh());
        map.put("appid", official_appid);

        Map<String, Object> miniprogram = new HashMap<>();
        miniprogram.put("appid", min_appid);
        miniprogram.put("page", "pkgOrder/orderDetail/main?orderNo=" + orderNo);

        Map<String, Object> data = new HashMap<>();

        Map<String, Object> first = new HashMap<>();
        first.put("value", "您的订单物流状态有更新，请查阅详情");
        first.put("color", "#173177");
        data.put("first", first);

        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", goodName);
        keyword1.put("color", "#173177");
        data.put("keyword1", keyword1);

        Map<String, Object> keyword2 = new HashMap<>();
        keyword2.put("value", courierNumber);
        keyword2.put("color", "#173177");
        data.put("keyword2", keyword2);


        Map<String, Object> keyword3 = new HashMap<>();
        keyword3.put("value", courierName);
        keyword3.put("color", "#173177");
        data.put("keyword3", keyword3);

        Map<String, Object> remark = new HashMap<>();
        remark.put("value", ">>>点击回小程序查看订单详情");
        remark.put("color", "#173177");
        data.put("remark", remark);

        map.put("data", data);
        map.put("miniprogram", miniprogram);

        JSONObject json = new JSONObject(map);

        String s = json.toJSONString();
        wxMessageFeignClient.sendTemplate(s);
        return s;
    }


}
