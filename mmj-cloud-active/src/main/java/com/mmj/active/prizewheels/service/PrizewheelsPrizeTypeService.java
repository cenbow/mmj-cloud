package com.mmj.active.prizewheels.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.prizewheels.model.PrizewheelsPrizeType;

/**
 * <p>
 * 转盘活动 - 奖励配置表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
public interface PrizewheelsPrizeTypeService extends IService<PrizewheelsPrizeType> {
	
	/**
	 * 查询所有奖品
	 * @return
	 */
	List<PrizewheelsPrizeType> loadAllPrize();

}
