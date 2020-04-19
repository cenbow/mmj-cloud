package com.mmj.active.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "weChatTmpId")
public class WeChatTmpMsgConfig {

    //小程序appid
    private String minAppid;

    //公众号appid  购值购便宜  买买家
    private String officialAppid;

    /**
     * 关注成功模板消息
     */

    //秒杀-订阅秒杀提醒
    private String seckillBook;

    //砍价-订阅砍价进度
    private String cutBook;

    //抽奖-开奖区开启抽奖提醒
    private String lotteryOpenBook;

    //抽奖-开奖公示区开启开奖提醒
    private String lotteryPublicBook;

    //抽奖-领取优惠券
    private String lotteryCouponBook;

    //转盘-转盘签到订阅提醒
    private String prizewheelsSignBook;

    //转盘-转盘十元结果翻倍
    private String prizewheelsTenBook;

    //十元三件-关注后获得购买机会
    private String tenSaleBook;

    //签到-订阅签到提醒
    private String signBook;

    //签到-签到后获得一次签到机会
    private String signChanceBook;

    //店铺订单-订阅物流进度提醒
    private String orderExpressBook;


    /**
     * 关注后推送模板消息
     */

    //秒杀-活动开启时提醒
    private String seckillAfterOpen;

    //砍价-有好友帮砍价时
    private String cutAfterHelp;

    //抽奖-每周一次
    private String lotteryAfterOnceWeek;

    //抽奖-参与的抽奖活动开奖时
    private String lotteryAfterOpen;

    //转盘-转盘签到标签用户 当天未签到用户 每天10点进行提醒
    private String prizewheelsAfterRemind;

    //十元三件-已经重新获购买资格
    private String tenAfterRefresh;

    //签到-签到提醒标签用户 当天未签到用户 每天9点进行提醒
    private String signAfterRemind;

    //签到-好友帮签到成功时
    private String signAfterHelp;

    //店铺订单-物流状态更新时
    private String orderAfterExpressRefresh;



    public String getMinAppid() {
        return minAppid;
    }

    public void setMinAppid(String minAppid) {
        this.minAppid = minAppid;
    }

    public String getOfficialAppid() {
        return officialAppid;
    }

    public void setOfficialAppid(String officialAppid) {
        this.officialAppid = officialAppid;
    }

    public String getSeckillBook() {
        return seckillBook;
    }

    public void setSeckillBook(String seckillBook) {
        this.seckillBook = seckillBook;
    }

    public String getCutBook() {
        return cutBook;
    }

    public void setCutBook(String cutBook) {
        this.cutBook = cutBook;
    }

    public String getLotteryOpenBook() {
        return lotteryOpenBook;
    }

    public void setLotteryOpenBook(String lotteryOpenBook) {
        this.lotteryOpenBook = lotteryOpenBook;
    }

    public String getLotteryPublicBook() {
        return lotteryPublicBook;
    }

    public void setLotteryPublicBook(String lotteryPublicBook) {
        this.lotteryPublicBook = lotteryPublicBook;
    }

    public String getLotteryCouponBook() {
        return lotteryCouponBook;
    }

    public void setLotteryCouponBook(String lotteryCouponBook) {
        this.lotteryCouponBook = lotteryCouponBook;
    }

    public String getPrizewheelsSignBook() {
        return prizewheelsSignBook;
    }

    public void setPrizewheelsSignBook(String prizewheelsSignBook) {
        this.prizewheelsSignBook = prizewheelsSignBook;
    }

    public String getPrizewheelsTenBook() {
        return prizewheelsTenBook;
    }

    public void setPrizewheelsTenBook(String prizewheelsTenBook) {
        this.prizewheelsTenBook = prizewheelsTenBook;
    }

    public String getTenSaleBook() {
        return tenSaleBook;
    }

    public void setTenSaleBook(String tenSaleBook) {
        this.tenSaleBook = tenSaleBook;
    }

    public String getSignBook() {
        return signBook;
    }

    public void setSignBook(String signBook) {
        this.signBook = signBook;
    }

    public String getSignChanceBook() {
        return signChanceBook;
    }

    public void setSignChanceBook(String signChanceBook) {
        this.signChanceBook = signChanceBook;
    }

    public String getOrderExpressBook() {
        return orderExpressBook;
    }

    public void setOrderExpressBook(String orderExpressBook) {
        this.orderExpressBook = orderExpressBook;
    }

    public String getSeckillAfterOpen() {
        return seckillAfterOpen;
    }

    public void setSeckillAfterOpen(String seckillAfterOpen) {
        this.seckillAfterOpen = seckillAfterOpen;
    }

    public String getCutAfterHelp() {
        return cutAfterHelp;
    }

    public void setCutAfterHelp(String cutAfterHelp) {
        this.cutAfterHelp = cutAfterHelp;
    }

    public String getLotteryAfterOnceWeek() {
        return lotteryAfterOnceWeek;
    }

    public void setLotteryAfterOnceWeek(String lotteryAfterOnceWeek) {
        this.lotteryAfterOnceWeek = lotteryAfterOnceWeek;
    }

    public String getLotteryAfterOpen() {
        return lotteryAfterOpen;
    }

    public void setLotteryAfterOpen(String lotteryAfterOpen) {
        this.lotteryAfterOpen = lotteryAfterOpen;
    }

    public String getPrizewheelsAfterRemind() {
        return prizewheelsAfterRemind;
    }

    public void setPrizewheelsAfterRemind(String prizewheelsAfterRemind) {
        this.prizewheelsAfterRemind = prizewheelsAfterRemind;
    }

    public String getTenAfterRefresh() {
        return tenAfterRefresh;
    }

    public void setTenAfterRefresh(String tenAfterRefresh) {
        this.tenAfterRefresh = tenAfterRefresh;
    }

    public String getSignAfterRemind() {
        return signAfterRemind;
    }

    public void setSignAfterRemind(String signAfterRemind) {
        this.signAfterRemind = signAfterRemind;
    }

    public String getSignAfterHelp() {
        return signAfterHelp;
    }

    public void setSignAfterHelp(String signAfterHelp) {
        this.signAfterHelp = signAfterHelp;
    }

    public String getOrderAfterExpressRefresh() {
        return orderAfterExpressRefresh;
    }

    public void setOrderAfterExpressRefresh(String orderAfterExpressRefresh) {
        this.orderAfterExpressRefresh = orderAfterExpressRefresh;
    }
}
