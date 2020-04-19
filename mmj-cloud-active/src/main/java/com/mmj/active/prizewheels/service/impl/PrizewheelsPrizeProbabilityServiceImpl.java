package com.mmj.active.prizewheels.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.prizewheels.mapper.PrizewheelsPrizeProbabilityMapper;
import com.mmj.active.prizewheels.model.PrizewheelsPrizeProbability;
import com.mmj.active.prizewheels.service.PrizewheelsPrizeProbabilityService;
import com.mmj.common.exception.CustomException;

/**
 * <p>
 * 奖品概率配置表，必须保证每个区间下的各个奖励之和为100 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
@Slf4j
@Service
public class PrizewheelsPrizeProbabilityServiceImpl extends ServiceImpl<PrizewheelsPrizeProbabilityMapper, PrizewheelsPrizeProbability> implements PrizewheelsPrizeProbabilityService {

	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	
	private static final String PROBABILITY_CACHE_KEY_PREFIX = "PRIZEWHEELS:PROBABILITY";
	
	@Override
	public List<PrizewheelsPrizeProbability> loadPrizeRangeList(Double userRedpacketBalance) {
		// 先查询出所有的区间概率数据
		List<PrizewheelsPrizeProbability> probabilityList = null;
		String cacheValue = redisTemplate.opsForValue().get(PROBABILITY_CACHE_KEY_PREFIX);
		if (StringUtils.isNotBlank(cacheValue)) {
			probabilityList = JSONArray.parseArray(cacheValue, PrizewheelsPrizeProbability.class);
		} else {
			probabilityList = this.selectList(new EntityWrapper<PrizewheelsPrizeProbability>());
			if(probabilityList.isEmpty()) {
				throw new CustomException("转盘配置错误");
			}
			String jsonString = JSONObject.toJSONString(probabilityList, SerializerFeature.WriteMapNullValue);
			redisTemplate.opsForValue().set(PROBABILITY_CACHE_KEY_PREFIX, jsonString, 7, TimeUnit.DAYS);
		}
		
		int useRangeId = this.getProbabilityRangeId(userRedpacketBalance, probabilityList);
		
		List<PrizewheelsPrizeProbability> resultList = new ArrayList<PrizewheelsPrizeProbability>();
		for(PrizewheelsPrizeProbability p : probabilityList) {
			if(useRangeId == p.getRangeId()) {
				resultList.add(p);
			}
		}
		return resultList;
	}
	
	/**
	 * 用户转盘抽奖区的各奖品获得概率是根据区间配置的<br/>
	 * 此方法根据用户的转盘账户余额来匹配到对应的概率区间，从而确定该用户使用什么概率
	 * @param userRedpacketBalance
	 * @param probabilityList
	 * @return
	 */
	private int getProbabilityRangeId(Double userRedpacketBalance, List<PrizewheelsPrizeProbability> probabilityList) {
		int useRangeId = 1; // 默认第一区间
		Set<ProbabilityRange> set = new HashSet<ProbabilityRange>();
		ProbabilityRange range = null;
		for(PrizewheelsPrizeProbability p : probabilityList) {
			range = new ProbabilityRange(p.getRangeId(), p.getBalanceRangeLeft(), p.getBalanceRangeRight());
			set.add(range);
		}
		
		for(ProbabilityRange r : set) {
			if(r.getBalanceRangeRight() == null) {
				// 判断最大值
				if(userRedpacketBalance >= r.getBalanceRangeLeft()) {
					useRangeId = r.getRangeId();
					break;
				}
			} else {
				// 判断区间
				if(userRedpacketBalance >= r.getBalanceRangeLeft() && userRedpacketBalance < r.getBalanceRangeRight()) {
					useRangeId = r.getRangeId();
					break;
				}
			}
		}
		log.info("-->转盘抽奖-->当前用户余额{}，使用第{}区间的概率配置", userRedpacketBalance, useRangeId);
		return useRangeId;
	}
	
	class ProbabilityRange {
		
		private Integer rangeId;
		
		private Double balanceRangeLeft;
		
		private Double balanceRangeRight;
		
		public ProbabilityRange(Integer rangeId, Double balanceRangeLeft, Double balanceRangeRight) {
			this.rangeId = rangeId;
			this.balanceRangeLeft = balanceRangeLeft;
			this.balanceRangeRight = balanceRangeRight;
		}
		
		public Integer getRangeId() {
			return rangeId;
		}

		public void setRangeId(Integer rangeId) {
			this.rangeId = rangeId;
		}

		public Double getBalanceRangeLeft() {
			return balanceRangeLeft;
		}

		public void setBalanceRangeLeft(Double balanceRangeLeft) {
			this.balanceRangeLeft = balanceRangeLeft;
		}

		public Double getBalanceRangeRight() {
			return balanceRangeRight;
		}

		public void setBalanceRangeRight(Double balanceRangeRight) {
			this.balanceRangeRight = balanceRangeRight;
		}

		@Override
		public int hashCode() {
			int result = rangeId.hashCode();
			result = result * 31 + balanceRangeLeft.hashCode();
			result = result * 31 + (balanceRangeRight != null ? balanceRangeRight.hashCode() : 100);
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null) {
				return false;
			}
			ProbabilityRange p = (ProbabilityRange) obj;
			if(this.rangeId == p.getRangeId() && this.balanceRangeLeft == p.getBalanceRangeLeft()
					&& this.balanceRangeRight == p.getBalanceRangeRight()) {
				return true;
			}
			return false;
		}
	}

}
