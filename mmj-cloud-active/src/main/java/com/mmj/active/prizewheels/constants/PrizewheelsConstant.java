package com.mmj.active.prizewheels.constants;

/**
 * 大转盘常量类
 * @author shenfuding
 *
 */
public interface PrizewheelsConstant {
	
	/**
	 * 活动标识
	 */
	String PRIZEWHEELS = "PRIZEWHEELS";
	
	/**
	 * 奖品类型
	 * @author shenfuding
	 *
	 */
	interface PrizeType {
		
		/**
		 * 奖品类型：红包
		 */
		String REDPACKET = "REDPACKET";
		
		/**
		 * 奖品类型：优惠券
		 */
		String COUPON = "COUPON";
		
		/**
		 * 奖品类型：买买币
		 */
		String COINS = "COINS";
		
	}
	
	/**
	 * 奖品编码
	 * @author shenfuding
	 *
	 */
	interface PrizeCode {
		
		/**
		 * 奖品：5元固定红包
		 */
		String FIXED_REDPACKET_5 = "FIXED_REDPACKET_5";
		
		/**
		 * 奖品：10元固定红包
		 */
		String FIXED_REDPACKET_10 = "FIXED_REDPACKET_10";
		
		/**
		 * 奖品：100元固定红包
		 */
		String FIXED_REDPACKET_100 = "FIXED_REDPACKET_100";
		
		/**
		 * 奖品：随机红包
		 */
		String RANDOM_REDPACKET = "RANDOM_REDPACKET";
		
		/**
		 * 奖品：3元无门槛优惠券
		 */
		String COUPON_3 = "COUPON_3";
		
		/**
		 * 奖品：5元无门槛优惠券
		 */
		String COUPON_5 = "COUPON_5";
		
		/**
		 * 奖品：一袋买买币
		 */
		String COINS_BAG = "COINS_BAG";
		
		/**
		 * 奖品：一箱买买币
		 */
		String COINS_BOX = "COINS_BOX";
		
	}

	/**
	 * 红包获得方式
	 * @author shenfuding
	 *
	 */
	interface RedpacketGotWays {
		
		/**
		 *  新人获得
		 */
		String NEW_USER = "NEW_USER";
		
		/**
		 * 转盘抽奖获得的5元固定红包
		 */
		String FIXED_REDPACKET_5 = "FIXED_REDPACKET_5";
		
		/**
		 * 转盘抽奖获得的10元固定红包
		 */
		String FIXED_REDPACKET_10 = "FIXED_REDPACKET_10";
		
		/**
		 * 转盘抽奖获得的100元固定红包
		 */
		String FIXED_REDPACKET_100 = "FIXED_REDPACKET_100";
		
		/**
		 * 转盘抽奖获得的随机金额红包
		 */
		String RANDOM_REDPACKET = "RANDOM_REDPACKET";
		
		/**
		 * 完成邀请好友数量达标的任务获得随机红包
		 */
		String INVITE = "INVITE";
		
		/**
		 * 完成分享商品达标的任务获得随机红包
		 */
		String SHARE = "SHARE";
		
		/**
		 * 提现
		 */
		String WITHDRAW = "WITHDRAW";
		
		/**
		 * 抽到10元红包后，红包数据翻倍，即额外得到一个随机红包 - 流量池
		 */
		String DOUBLE_REWARD_REDPACKET = "DOUBLE_REWARD_REDPACKET";
	}
	
	/**
	 * 红包/金币领取状态
	 * @author shenfuding
	 *
	 */
	interface GotStatus {
		
		/**
		 * 待领取
		 */
		String PENDING = "PENDING";
		
		/**
		 * 已领取
		 */
		String GOT = "GOT";
		
		/**
		 * 已被消耗
		 */
		String CONSUMED = "CONSUMED";
		
		/**
		 * 已提现
		 */
		String WITHDRAW = "WITHDRAW";
	}
	
	/**
	 * 买买币获得方式
	 * @author shenfding
	 *
	 */
	interface CoinsGotWays {
		
		/**
		 * 新人获得
		 */
		String NEW_USER = "NEW_USER";
		
		/**
		 * 转盘抽奖获得一袋买买币
		 */
		String COINS_BAG = "COINS_BAG";
		
		/**
		 * 转盘抽奖获得一箱买买币
		 */
		String COINS_BOX = "COINS_BOX";
		
		/**
		 * 自增获得
		 */
		String INCREMENT = "INCREMENT";
		
		/**
		 * 签到获得
		 */
		String SIGN = "SIGN";
		
		/**
		 * 邀请好友获得
		 */
		String INVITE = "INVITE";
		
		/**
		 * 分享商品获得
		 */
		String SHARE_GOODS = "SHARE_GOODS";
		
		/**
		 * 转盘抽奖消耗
		 */
		String CLICK_DRAW = "CLICK_DRAW";
		
		/**
		 * 公众号回复获得
		 */
		String REPLY = "REPLY";
				
	}
	
	/**
	 * 签到状态
	 * @author shenfding
	 *
	 */
	interface SignStatus {
		
		/**
		 * 未签到
		 */
		String NO_SIGN = "NO_SIGN";
		
		/**
		 * 已完成
		 */
		String DONE = "DONE";
	}
	
	/**
	 * 任务按钮状态
	 * @author shenfding
	 *
	 */
	interface Task {
		
		String SHARE = "SHARE";
		
		String REDPACKET = "REDPACKET";
		
		String PROGRESS = "PROGRESS";
		
		String DONE = "DONE";
		
		String COLLECT = "COLLECT";
	}
	
	/**
	 * 翻倍奖励
	 * @author shenfuding
	 *
	 */
	interface DoubleReward {
		
		/**
		 * 抽到10元红包后,等待关注公众号
		 */
		int GOT_10_YUAN = 0;
		
		/**
		 * 关注了公众号,等待用户领取红包
		 */
		int OFFICIALED_ACCOUNTS = 1;
		
		/**
		 * 创建账号时为初始化(历史数据为空)
		 */
		int INIT = 2;
		
		/**
		 * 前端已提醒
		 */
		int REMINDED = 3;
	}
}
