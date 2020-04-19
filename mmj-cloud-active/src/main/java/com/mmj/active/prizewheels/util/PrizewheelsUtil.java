package com.mmj.active.prizewheels.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.time.DateUtils;

import com.mmj.active.prizewheels.constants.PrizewheelsConstant;
import com.mmj.active.prizewheels.dto.WithdrawRecordDto;
import com.mmj.active.prizewheels.model.PrizewheelsPrizeProbability;
import com.mmj.active.prizewheels.model.PrizewheelsPrizeType;
import com.mmj.common.exception.CustomException;
import com.mmj.common.utils.DoubleUtil;

/**
 * 大转盘工具类
 * @author shenfuding
 */
@Slf4j
public class PrizewheelsUtil {
	
	private static final Random RANDOM = new Random();
	
	private PrizewheelsUtil() {}

	public static Double getRandomMoney(String range) {
		log.info("-->转盘随机红包范围：{}", range);
		String minStr = range.split("-")[0];
		String maxStr = range.split("-")[1];
		double min = Double.valueOf(minStr);
		double max = Double.valueOf(maxStr);
		double random = nextDouble(min, max);
		BigDecimal bd = new BigDecimal(random);
		random = bd.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		return random;
	}
	
	 private static double nextDouble(final double startInclusive, final double endInclusive) {
        if(endInclusive < startInclusive) {
        	throw new CustomException("转盘配置有误");
        }
        if (startInclusive == endInclusive) {
            return startInclusive;
        }
        
        return startInclusive + ((endInclusive - startInclusive) * RANDOM.nextDouble());
    }
	
	/**
	 * 用户首次抽奖获得10元固定红包，其它情况根据概率获取奖品
	 * @param prizeList
	 * @param isFirstPrizeDraw
	 * @return
	 */
	public static PrizewheelsPrizeType getRandomPrize(List<PrizewheelsPrizeType> prizeList, 
				List<PrizewheelsPrizeProbability> prizeProbabilityList, boolean isFirstPrizeDraw) {
		if(isFirstPrizeDraw) {
			for(PrizewheelsPrizeType prize : prizeList) {
				if(PrizewheelsConstant.PrizeCode.FIXED_REDPACKET_10.equals(prize.getPrizeCode())) {
					return prize;
				}
			}
		}
		int prizeIndex = getPrizeIndex(prizeProbabilityList);
		PrizewheelsPrizeType prizeType = prizeList.get(prizeIndex);
		PrizewheelsPrizeProbability probability = prizeProbabilityList.get(prizeIndex);
		prizeType.setRandomRedpacketRange(probability.getRandomRedpacketRange());
		prizeType.setProbability(probability.getProbability());
		return prizeType;
	}

	/**
	 * 根据Math.random()产生一个double型的随机数，判断每个奖品出现的概率
	 * 
	 * @param prizes
	 * @return random：奖品列表prizes中的序列（prizes中的第random个就是抽中的奖品）
	 */
	private static int getPrizeIndex(List<PrizewheelsPrizeProbability> prizes) {
		int random = -1;
		try {
			// 计算总权重
			Double sumWeight = 0d;
			for (PrizewheelsPrizeProbability p : prizes) {
				sumWeight = DoubleUtil.add(p.getProbability(), sumWeight);
			}
			if (!sumWeight.equals(100d)) {
				throw new IllegalArgumentException("转盘抽奖各项奖品设置概率百分比之和必须为100%");
			}

			// 产生随机数
			double randomNumber;
			randomNumber = Math.random();

			// 根据随机数在所有奖品分布的区域并确定所抽奖品
			double d1 = 0;
			double d2 = 0;
			for (int i = 0; i < prizes.size(); i++) {
				d2 += Double.valueOf(String.valueOf(prizes.get(i).getProbability())) / sumWeight;
				if (i == 0) {
					d1 = 0;
				} else {
					d1 += Double.parseDouble(String.valueOf(prizes.get(i - 1).getProbability())) / sumWeight;
				}
				if (randomNumber >= d1 && randomNumber <= d2) {
					random = i;
					break;
				}
			}
		} catch (Exception e) {
			log.error("生成抽奖随机数出错，出错原因：", e);
		}
		return random;
	}

	public static int getTargetCount(int currentCount, int condition, boolean hasMarked) {
		int inviteTargetCount = 0;
		if(currentCount < condition) {
			inviteTargetCount = condition;
		} else {
			if(currentCount % condition == 0) {
				inviteTargetCount = hasMarked ? (currentCount + condition) : currentCount;
			} else {
				inviteTargetCount = (currentCount -(currentCount % condition)) + condition;
			}
		}
		return inviteTargetCount;
	}
	
	/**
	 * 51个虛拟用户
	 */
	private static final String[] virtualWithdrawUserNickname = {"黑暗之神","张扬","风一阵","刘星","会会","小怪兽","阿毛","flying","张云","老周",
		"李老师","谢文东","果果","小鱼儿","三妹儿","疯子","洪洋","老李头","张先森","刘据",
		"李震","王军","夏梦","沈伟","光复","文攀","Kim","Jenney","Rush","玉洪",
		"江小鱼","新宇","小猪猪","吻别","亚娜","饶先生","老田","老高","小李","杏子",
		"李运","小祖宗","桢哥","冰冰","齐齐","有味儿","超哥","大光","炎炎","俊哥","刘华"};
	
	private static int getRandomIndex(Set<Integer> tempSet) {
		int index = new Random().nextInt(50);
		if(tempSet.contains(index)) {
			return getRandomIndex(tempSet);
		} else {
			tempSet.add(index);
		}
		return index;
	}
	
	private static int getWithdrawRandomMoney() {
		int index = new Random().nextInt(2);
		if(index == 0) {
			++index;
		}
		return index * 100 + (new Random().nextInt(99));
		
	}
	
	public static List<String> getRandomNicknameSet(int count) {
		List<String> nameList = new ArrayList<String>();
		Set<Integer> tempSet = new HashSet<Integer>();
		int index = 0;
		for(int i=0;i<count;i++) {
			index = getRandomIndex(tempSet);
			nameList.add(virtualWithdrawUserNickname[index]);
		}
		return nameList;
	}
	
	public static List<WithdrawRecordDto> getVirtualWithdrawRecord() {
		List<WithdrawRecordDto> list = new ArrayList<WithdrawRecordDto>();
		
		// 获取10条随机的用户昵称（不重复）
		int count = 10;
		List<String> nicknameList = getRandomNicknameSet(count);
		Date now = new Date();
		
		WithdrawRecordDto wrd0 = new WithdrawRecordDto();
		wrd0.setNickname(nicknameList.get(0));
		wrd0.setWithdrawMoney(String.valueOf(getWithdrawRandomMoney()));
		wrd0.setCreateTime(DateUtils.addMinutes(now, -1));// 2分钟前
		list.add(wrd0);
		
		WithdrawRecordDto wrd1 = new WithdrawRecordDto();
		wrd1.setNickname(nicknameList.get(1));
		wrd1.setWithdrawMoney(String.valueOf(getWithdrawRandomMoney()));
		wrd1.setCreateTime(DateUtils.addMinutes(now, -10));// 10分钟前
		list.add(wrd1);
		
		WithdrawRecordDto wrd2 = new WithdrawRecordDto();
		wrd2.setNickname(nicknameList.get(2));
		wrd2.setWithdrawMoney(String.valueOf(getWithdrawRandomMoney()));
		wrd2.setCreateTime(DateUtils.addMinutes(now, -30));// 30分钟前
		list.add(wrd2);
		
		WithdrawRecordDto wrd3 = new WithdrawRecordDto();
		wrd3.setNickname(nicknameList.get(3));
		wrd3.setWithdrawMoney(String.valueOf(getWithdrawRandomMoney()));
		wrd3.setCreateTime(DateUtils.addMinutes(now, -44));// 45分钟前
		list.add(wrd3);
		
		WithdrawRecordDto wrd4 = new WithdrawRecordDto();
		wrd4.setNickname(nicknameList.get(4));
		wrd4.setWithdrawMoney(String.valueOf(getWithdrawRandomMoney()));
		wrd4.setCreateTime(DateUtils.addHours(now, -1));// 1小时前
		list.add(wrd4);
		
		WithdrawRecordDto wrd5 = new WithdrawRecordDto();
		wrd5.setNickname(nicknameList.get(5));
		wrd5.setWithdrawMoney(String.valueOf(getWithdrawRandomMoney()));
		wrd5.setCreateTime(DateUtils.addHours(now, -2));// 2小时前
		list.add(wrd5);
		
		WithdrawRecordDto wrd6 = new WithdrawRecordDto();
		wrd6.setNickname(nicknameList.get(6));
		wrd6.setWithdrawMoney(String.valueOf(getWithdrawRandomMoney()));
		wrd6.setCreateTime(DateUtils.addHours(now, -3));// 3小时前
		list.add(wrd6);
		
		WithdrawRecordDto wrd7 = new WithdrawRecordDto();
		wrd7.setNickname(nicknameList.get(7));
		wrd7.setWithdrawMoney(String.valueOf(getWithdrawRandomMoney()));
		wrd7.setCreateTime(DateUtils.addHours(now, -3));// 3小时前
		list.add(wrd7);
		
		WithdrawRecordDto wrd8 = new WithdrawRecordDto();
		wrd8.setNickname(nicknameList.get(8));
		wrd8.setWithdrawMoney(String.valueOf(getWithdrawRandomMoney()));
		wrd8.setCreateTime(DateUtils.addHours(now, -4));// 4小时前
		list.add(wrd8);
		
		WithdrawRecordDto wrd9 = new WithdrawRecordDto();
		wrd9.setNickname(nicknameList.get(9));
		wrd9.setWithdrawMoney(String.valueOf(getWithdrawRandomMoney()));
		wrd9.setCreateTime(DateUtils.addHours(now, -50));// 5小时前
		list.add(wrd9);
		
		return list;
	}
	
	public static String getTimeIntervalStr(Date date) {
		Date now = new Date();
		long ms = now.getTime() - date.getTime();
		long seconds = ms / 1000;
		if(seconds < 60) {
			return seconds + "秒";
		}
		long minutes = seconds / 60;
		if(minutes >=1 && minutes < 60) {
			return minutes + "分钟";
		}
		long hours = minutes / 60;
		if(hours >=1 && hours < 24) {
			return hours + "小时";
		}
		long day = hours / 24;
		return day + "天";
	}
	
	public static boolean isExceedOneWeek(Date date) {
		Date now = new Date();
		long ms = now.getTime() - date.getTime();
		Double day = DoubleUtil.divide(Double.valueOf(ms), Double.valueOf(1000 * 60 * 60 * 24));
		return day > 7 ? true : false;
	}
	
	public static String getMoneyToShow(String withdrawMoney) {
		if(withdrawMoney.endsWith(".0")) {
			withdrawMoney = withdrawMoney.substring(0, withdrawMoney.indexOf(".0"));
		}
		return withdrawMoney;
	}
	
	public static void main(String[] args) {
//		String range = "0.1000-1.000";
		String range = "0.10-0.5";
		String minStr = range.split("-")[0];
		String maxStr = range.split("-")[1];
		System.out.println(minStr+"  "+maxStr);
		double min = Double.valueOf(minStr);
		double max = Double.valueOf(maxStr);
		System.out.println(min+"  "+max);
		
		for(int i=0;i<1000;i++) {
			double random = nextDouble(min, max);
			BigDecimal bd = new BigDecimal(random);
			random = bd.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
			System.out.println(random);
		}
		
	}
}
