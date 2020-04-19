package com.mmj.active.prizewheels.service;

import com.mmj.active.prizewheels.model.PrizewheelsAccount;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 转盘活动 - 账户表，包含买买币余额、红包余额 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
public interface PrizewheelsAccountService extends IService<PrizewheelsAccount> {
	
	/**
	 * 更新买买币余额
	 * @param userId
	 * @param increaseCoins
	 * @param add 是否加法运算
	 * @return 
	 */
	PrizewheelsAccount updateCoinsBalance(Long userId, int increaseCoins, boolean add);
	
	/**
	 * 更新红包余额
	 * @param userId
	 * @param increaseMoney
	 * @param add 是否加法运算
	 * @return
	 */
	PrizewheelsAccount updateRedpacketBalance(Long userId, Double increaseMoney, boolean add);
	
	/**
	 * 修改userId
	 * @param oldUserId
	 * @param newUserId
	 */
	void updateUserId(long oldUserId, long newUserId);
	
}
