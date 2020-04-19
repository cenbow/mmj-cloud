package com.mmj.active.prizewheels.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mmj.active.prizewheels.dto.MyCoinsChangeDetail;
import com.mmj.active.prizewheels.dto.MyPrizeDto;

public interface PrizewheelsFacadeService {
	
	/**
	 * 检查是否新用户，是则发新人红包以及买买币
	 * @return
	 */
	Map<String, Object> checkNewUser();
	
	/**
	 * 获取转盘页面初始化数据
	 * @return
	 */
	Map<String, Object> loadPrizewheelsInitData();
	
	/**
	 * 红包 - 放入我的余额
	 * @param from
	 * @return
	 */
	Map<String, Object> addRedpacketBalance(String from);
	
	/**
	 * 买买币 - 放入我的余额
	 * @param from
	 * @return
	 */
	Map<String, Object> addCoinsBalance(String from);
	
	/**
	 * 签到
	 * @return
	 */
	Map<String, Object> sign();
	
	/**
	 * 获取预提现信息
	 * @return
	 */
	Map<String, Object> prepareWithdraw();
	
	/**
	 * 提现 - 初始门槛100元，每提现一次门槛翻倍，最高400元
	 * 
	 * 100和400在数据库表中配置
	 * 
	 * @param withdrawMoney
	 * @return
	 */
	Map<String, Object> withdraw(Double withdrawMoney);
	
	/**
	 * 转盘抽奖
	 * @return
	 */
	Map<String, Object> prizeDraw();
	
	/**
	 * 获取我的奖品数据
	 * @return
	 */
	List<MyPrizeDto> getMyPrizes();
	
	/**
	 * 获取我的买买币变动详情
	 * @return
	 */
	List<MyCoinsChangeDetail> getMyCoinsDetail();
	
	/**
	 * 获取我的转盘任务数据
	 * @return
	 */
	Map<String, Object> getMyTaskData();
	
	/**
	 * 获取提现记录在页面滚动显示
	 * @return
	 */
	Set<String> getWithdrawRecord();
	
	/**
	 * 是否有参与转盘活动(必须是在转盘活动开启的情况下)
	 * @param userId
	 * @return
	 */
	Boolean hasParticipateInPrizewheels(Long userId);
	
	/**
	 * 点击分享进入商详页给原分享人增加买买币
	 * @param userId
	 * @param shareUserUserid
	 * @param goodId
	 */
	void clickGoodsShare(Long userId, Long shareUserId, Integer goodId);
	
	void preClickFriendShare(Long shareUserId);
	
	/**
	 * 点击好友分享的转盘抽奖活动给好友增加买买币
	 * @param userId
	 * @param shareUserUserid
	 */
	void clickFriendShare(long userId, long shareUserUserid);
	
	/**
	 * 抽到10元红包后如果关注公众号，再送一个随机红包
	 * @param userId
	 */
	void sendDoubleRewardOfTenYuan(Long userId);
	
	/**
	 * 返回当天没有签到的用户，以前之前连续签到了多少天
	 * @param useridList
	 * @return
	 */
	Map<Long, Integer> getSignStatus(Set<Long> useridSet);
	
	/**
	 * 公众号回复增加买买币
	 * @param appid
	 * @param openId
	 * @param keyword
	 */
	void officialAccountsReply(String appid, String openId, String keyword);
	
	/**
	 * 处理最近访问转盘的用户的买买币自增
	 */
	void autoIncrementCoins();

	/**
	 * 切换userId
	 * @param oldUserId
	 * @param newUserId
	 */
	void updateUserId(long oldUserId, long newUserId);
	
	/**
	 * 发送签到提醒
	 */
	void sendSignNotice();
	
}
