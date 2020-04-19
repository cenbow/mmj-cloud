package com.mmj.active.prizewheels.mapper;

import com.mmj.active.prizewheels.model.PrizewheelsPrizeProbability;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 奖品概率配置表，必须保证每个区间下的各个奖励之和为100 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
public interface PrizewheelsPrizeProbabilityMapper extends BaseMapper<PrizewheelsPrizeProbability> {

}
