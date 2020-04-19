package com.mmj.active.prizewheels.service.impl;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.prizewheels.mapper.PrizewheelsAccountMapper;
import com.mmj.active.prizewheels.model.PrizewheelsAccount;
import com.mmj.active.prizewheels.service.PrizewheelsAccountService;
import com.mmj.common.utils.DoubleUtil;

/**
 * <p>
 * 转盘活动 - 账户表，包含买买币余额、红包余额 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
@Slf4j
@Service
public class PrizewheelsAccountServiceImpl extends ServiceImpl<PrizewheelsAccountMapper, PrizewheelsAccount> implements PrizewheelsAccountService {
	
	@Autowired
	private PrizewheelsAccountMapper mapper;

	@Override
	@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
	public PrizewheelsAccount updateCoinsBalance(Long userId, int increaseCoins, boolean add) {
		PrizewheelsAccount account = this.selectById(userId);
		int coinsBalance = account.getCoinsBalance();
		int oldBalance = coinsBalance;
		if (add) {
			coinsBalance += increaseCoins;
		} else {
			coinsBalance -= increaseCoins;
		}
		account.setCoinsBalance(coinsBalance);
		account.setUpdateTime(new Date());
		this.updateById(account);
		log.info("-->updateCoinsBalance-->更新用户{}的买买币余额，isAdd：{}，变动数为： {}，更改前的余额为：{}, 更改后买买币余额为：{}", 
				userId, add, increaseCoins, oldBalance, coinsBalance);
		return account;
	}

	@Override
	@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
	public PrizewheelsAccount updateRedpacketBalance(Long userId,
			Double increaseMoney, boolean add) {
		PrizewheelsAccount account = this.selectById(userId);
		Double redpacketBalance = account.getRedpacketBalance() == null ? 0d : account.getRedpacketBalance();
		Double oldBalance = account.getRedpacketBalance();
		if (add) {
			redpacketBalance = DoubleUtil.add(redpacketBalance, increaseMoney, DoubleUtil.SCALE_3);
		} else {
			redpacketBalance = DoubleUtil.sub(redpacketBalance, increaseMoney, DoubleUtil.SCALE_3);
		}
		account.setRedpacketBalance(redpacketBalance);
		account.setUpdateTime(new Date());
		this.updateById(account);
		log.info("-->updateRedpacketBalance-->更新用户{}的红包余额，isAdd：{}，变动数为： {}，更改前的余额为：{}, 更改后余额为：{}", 
				userId, add, increaseMoney, oldBalance, redpacketBalance);
		return account;
	}

	@Override
	public void updateUserId(long oldUserId, long newUserId) {
		mapper.updateUserId(oldUserId, newUserId);
	}

}
