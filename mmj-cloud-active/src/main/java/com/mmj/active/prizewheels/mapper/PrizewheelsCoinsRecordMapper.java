package com.mmj.active.prizewheels.mapper;

import org.apache.ibatis.annotations.Param;

import com.mmj.active.prizewheels.model.PrizewheelsCoinsRecord;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 转盘活动 - 用户买买币变更记录表 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
public interface PrizewheelsCoinsRecordMapper extends BaseMapper<PrizewheelsCoinsRecord> {
	
	int queryUserCurrentDaySignCount(@Param("userId")Long userId);
	
	int getContinuousSigninDays(@Param("userId")Long userId, @Param("endDate")String endDate);
	
	void updateUserId(@Param("oldUserId")long oldUserId, @Param("newUserId")long newUserId);

}
