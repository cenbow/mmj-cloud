package com.mmj.active.prizewheels.mapper;

import org.apache.ibatis.annotations.Param;

import com.mmj.active.prizewheels.model.PrizewheelsPrizeRecord;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 转盘 - 我的奖品记录表 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
public interface PrizewheelsPrizeRecordMapper extends BaseMapper<PrizewheelsPrizeRecord> {
	
	void updateUserId(@Param("oldUserId")long oldUserId, @Param("newUserId")long newUserId);

}
