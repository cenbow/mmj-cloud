package com.mmj.active.common.constants;

/**
 * 优惠券相关常量定义
 * 
 * @author shenfuding
 *
 */
public interface CouponConstants {

	String OVERPLUS_COUNT_ZERO = "0";

	/**
	 * 表示优惠券数量不限制
	 */
	Integer UNLIMITED = -1;

	String UNLIMITED_STR = "无限制";
	
	String CONDITION_MONEY_STR = "满{0}可用";
	
	String CONDITION_COUNT_STR = "满{0}件可用";
	
	/**
	 * 优惠主体类型 ->对应表coupon_template->字段preferential
	 */
	interface Preferential {

		/**
		 * 商品金额
		 */
		Integer GOODS_MONEY = 1;

		/**
		 * 订单金额
		 */
		Integer ORDER_MONEY = 2;

		/**
		 * 运费金额 （注：暂未使用）
		 */
		Integer FREIGHT_MONEY = 3;

	}

	/**
	 * 优惠券使用条件类型->对应表coupon_condition->字段conditionname
	 * 
	 * @author george
	 *
	 */
	interface Condition {

		/**
		 * 无限制
		 */
		Integer UNLIMITED = 1;

		/**
		 * 满多少元才可使用
		 */
		Integer MONEY = 2;

		/**
		 * 满多少件才可使用（注：暂未使用）
		 */
		@Deprecated
		Integer COUNT = 3;

	}

	/**
	 * 优惠金额类型->对应表coupon_money->字段coupontype
	 */
	interface PreferentialMoney {

		/**
		 * 减具体的金额
		 */
		Integer MINUS_MONEY = 1;

		/**
		 * 折扣
		 */
		Integer DISCOUNT = 2;

	}

	/**
	 * 优惠券使用范围 ->对应表coupon_template->字段couponrange
	 */
	interface UseRange {

		/**
		 * 无限制
		 */
		Integer UNLIMITED = 1;

		/**
		 * 部分商品可用
		 */
		Integer PART_GOODS_CAN_USE = 2;

		/**
		 * 部分商品不可用
		 */
		Integer PART_GOODS_CANNOT_USE = 3;

		/**
		 * 指定分类可用
		 */
		Integer SPECIFY_CATEGORY_CAN_USE = 4;

	}

	/**
	 * 优惠券使用范围 ->对应表coupon_goods_range->字段rangeType
	 */
	interface RangeType {

		/**
		 * 可用商品，表示指定商品可以使用
		 */
		Integer CAN_USE = 1;

		/**
		 * 不可用商品，表示除了指定商品外都可使用
		 */
		Integer CAN_NOT_USE = 2;
	}

	/**
	 * 适用价格类型 ->对应表coupon_template->字段pricetype
	 */
	interface ApplyPriceType {

		/**
		 * 普通价格
		 */
		Integer COMMON_PRICE = 1;

		/**
		 * 拼团价
		 */
		Integer LOTTERY_PRICE = 2;

		/**
		 * 拼团价和普通价格都适用
		 */
		Integer COMMON_AND_LOTTTERY = 3;

		/**
		 * 限时秒杀价（注：暂未使用）
		 */
		@Deprecated
		Integer SECKILL = 4;

	}

	/**
	 * 优惠券有效期类型
	 */
	interface PeriodOfValidity {

		/**
		 * 固定有效期
		 */
		Integer FIXED_VALIDITY_PERIOD = 1;

		/**
		 * 领取后的选项
		 */
		Integer AFTER_RECEIVE_PREFERENTIAL = 2;

		/**
		 * 领取后的选项 - 时间单位的类型(具体表示指定时间之前有效)
		 */
		interface TimeUnitsType {

			/**
			 * 分钟
			 */
			String MINUTES = "MINUTES";

			/**
			 * 小时
			 */
			String HOURS = "HOURS";

			/**
			 * 天
			 */
			String DAYS = "DAYS";

			/**
			 * 日期
			 */
			String DATE = "DATE";

		}

	}

	/**
	 * 优惠券使用状态的常量
	 */
	interface UseStauts {

		/**
		 * 已使用
		 */
		Integer USED = 1;

		/**
		 * 未使用
		 */
		Integer NOT_USED = 0;
	}

	/**
	 * 优惠券在前端界面显示的状态
	 */
	interface ShowStatus {

		String HAVE_NOT_VALID = "未生效";

		String HAS_INVALID = "已失效";

		String USED = "已使用";

		String NOT_MATCH_CONDITION = "不符合使用条件";

	}

	/**
	 * 优惠券排序条件
	 */
	interface OrderByCondition {

		/**
		 * 按领取时间排
		 */
		String ORDER_BY_CREATETIME = "CREATETIME";

		/**
		 * 按金额排
		 */
		String ORDER_BY_MONEY = "COUPONMONEY";

	}

	/**
	 * 领取/发送|优惠券的|模块/来源
	 * @author george
	 *
	 */
	interface CouponSource {
		
		/**
		 * 系统发送
		 */
		String SYSTEM_SEND = "SYSTEM";
		
		/**
		 * 专题页领取
		 */
		String TOPIC_SEND = "TOPIC";
		
		/**
		 * 刷一刷获取
		 */
		String BRUSH_SEND = "BRUSH";
		
		/**
		 * 弹出优惠券领用
		 */
		String INDEX_SEND = "INDEX";
		
		/**
		 * 二维码领取
		 */
		String QR_RECIEVE = "QR";
		
		/**
		 * 拼团抽奖活动
		 */
		String LOTTERY = "LOTTERY";

		/**
		 * 现金签到
		 */
		String SIGN = "SIGN";
		
		/**
		 * 转盘活动
		 */
		String PRIZEWHEELS = "PRIZEWHEELS";
		
		/**
		 * 添加小程序领取
		 */
		String ADD_MINI_APPS = "ADD_MINI_APPS";

		/**
		 * 用户授权
		 */
		String ACCREDIT = "ACCREDIT";

		/**
		 * 会员买多少送多少活动
		 */
		String BUY_GIVE = "BUY_GIVE";

		/**
		 * 会员日领券活动
		 */
		String MEMBER_DAY = "MEMBER_DAY";

		/**
		 * 商详页领券
		 */
		String GOODS_DETAILS = "GOODS_DETAILS";
	}

	/**
	 * 优惠券关联的活动
	 * @author shenfuding
	 *
	 */
	interface Activity {

		/**
		 * 会员买多少送多少活动
		 */
		String BUY_GIVE = "BUY_GIVE";

		/**
		 * 会员日领券活动
		 */
		String MEMBER_DAY = "MEMBER_DAY";
	}

}
