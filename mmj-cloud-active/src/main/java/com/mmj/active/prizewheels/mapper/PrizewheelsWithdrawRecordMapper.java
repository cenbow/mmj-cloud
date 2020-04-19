package com.mmj.active.prizewheels.mapper;

import org.apache.ibatis.annotations.Param;

import com.mmj.active.prizewheels.model.PrizewheelsWithdrawRecord;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 转盘活动 - 用户提现记录表 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
public interface PrizewheelsWithdrawRecordMapper extends BaseMapper<PrizewheelsWithdrawRecord> {

	void updateUserId(@Param("oldUserId")long oldUserId, @Param("newUserId")long newUserId);
	
}
