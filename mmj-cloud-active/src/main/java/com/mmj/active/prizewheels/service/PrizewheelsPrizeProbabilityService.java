package com.mmj.active.prizewheels.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.prizewheels.model.PrizewheelsPrizeProbability;

/**
 * <p>
 * 奖品概率配置表，必须保证每个区间下的各个奖励之和为100 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
public interface PrizewheelsPrizeProbabilityService extends IService<PrizewheelsPrizeProbability> {
	
	/**
	 * 根据账户余额获取转盘所有奖品的概率
	 * @param balance
	 * @return
	 */
	List<PrizewheelsPrizeProbability> loadPrizeRangeList(Double userRedpacketBalance);

}
