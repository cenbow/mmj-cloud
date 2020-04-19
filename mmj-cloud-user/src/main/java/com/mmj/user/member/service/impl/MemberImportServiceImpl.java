package com.mmj.user.member.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.constants.MemberConstant;
import com.mmj.common.constants.UserConstant;
import com.mmj.common.exception.CustomException;
import com.mmj.common.model.MobileCode;
import com.mmj.common.utils.CommonUtil;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.common.feigin.NoticeFeignClient;
import com.mmj.user.manager.model.UserLogin;
import com.mmj.user.manager.service.UserLoginService;
import com.mmj.user.member.dto.SaveUserMemberDto;
import com.mmj.user.member.mapper.MemberImportMapper;
import com.mmj.user.member.model.MemberImport;
import com.mmj.user.member.model.UserMember;
import com.mmj.user.member.service.MemberConfigService;
import com.mmj.user.member.service.MemberImportService;
import com.mmj.user.member.service.UserMemberSendService;
import com.mmj.user.member.service.UserMemberService;

/**
 * <p>
 * 第三方导入会员的记录表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-12
 */
@Slf4j
@Service
public class MemberImportServiceImpl extends ServiceImpl<MemberImportMapper, MemberImport> implements MemberImportService {
	
	@Autowired
	private UserLoginService userLoginService;
	
	@Autowired
	private MemberConfigService memberConfigService;
	
	@Autowired
	private UserMemberService userMemberService;
	
	@Autowired
	private UserMemberSendService userMemberSendService;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private NoticeFeignClient noticeFeignClient;
	
	private static final String TRY_LATER = "请稍后重试";
	private static final String CODE_INPUT = "请输入验证码";
	private static final String CODE_ERROR = "验证码错误";
	private static final String CODE_INVALID = "验证码错误，可能已过期";
	
	private static final String CACHE_KEY_PREFIX = "MEMBER:IMPORT:AMOUNT";
	private static final String USER_ID = "USER_ID";
	private static final String ACTIVE = "ACTIVE";
	private static final String MOBILE = "MOBILE";

	@Override
	public String bindInfo() {
		long userId = SecurityUserUtil.getUserDetails().getUserId();
		Wrapper<MemberImport> wrapper = new EntityWrapper<MemberImport>();
		wrapper.eq(USER_ID, userId);
		wrapper.eq(ACTIVE, 1);
		List<MemberImport> list = this.selectList(wrapper);
		if(!list.isEmpty()) {
			String mobile = list.get(0).getMobile();
			log.info("-->用户{}绑定的手机号：{}", userId, mobile);
			return mobile;
		}
		log.info("-->用户{}未绑定的手机号", userId);
		return null;
	}

	@Override
	public void sendValidateCode(String mobile) {
		// 校验手机号
		if(!CommonUtil.checkMobile(mobile)) {
			throw new CustomException("手机号格式不正确");
		}
		
		// 判断系统中是否有用户绑定了该手机号，不管是不是当前用户自己
		UserLogin userLogin = userLoginService.getUserLoginInfoByUserName(mobile);
		if(userLogin != null) {
			throw new CustomException("手机号码已存在，请联系客服处理");
		}
		// 判断会员导入记录表中是否已有该手机号的记录，有才允许发送验证码
		Wrapper<MemberImport> wrapper = new EntityWrapper<MemberImport>();
		wrapper.eq(MOBILE, mobile);
		wrapper.eq(ACTIVE, 1);
		List<MemberImport> list = this.selectList(wrapper);
		if(list.isEmpty()) {
			throw new CustomException("你的手机号不符合条件，请联系客服处理");
		}
		
		// 检查距离上次发送的时间是否在1分钟内，是则不允许发送
        long maxTimeInterval = 1 * 60 * 1000;// 单位毫秒，1分钟
        String cacheKey = getMobileVerificationCodeCacheKey(mobile);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if(!StringUtils.isBlank(cacheValue)) {
        	long cacheTimestamp = Long.valueOf(cacheValue.split("_")[1]);
        	long currentTime = System.currentTimeMillis();
        	if(currentTime - cacheTimestamp <= maxTimeInterval) {
        		log.error("-->当前时间:{}距离上次缓存的时间:{}不到1分钟，程序返回.", currentTime, cacheTimestamp);
        		throw new CustomException(TRY_LATER);
        	}
        }
		
		// 生成验证码
		String validateCode = CommonUtil.genMobileValidateCode();
		log.info("-->sendValidateCode-->给手机号{}生成验证码{}", mobile, validateCode);
		
		// 验证码放入Redis缓存，有效期5分钟，如果之前的缓存还未过期，则相当于更新缓存以及缓存有效期
		cacheValue = validateCode + "_" + System.currentTimeMillis();
		redisTemplate.opsForValue().set(cacheKey, cacheValue, 10, TimeUnit.MINUTES);
		log.info("-->sendValidateCode-->给手机号{}的验证码设置缓存:{}", mobile, cacheValue);
		
		String msgContent = "买买家感谢您的注册,您的验证码是{0},请于10分钟内输入您的验证码,请勿泄露,退订回T【买买家】";
		msgContent = msgContent.replace("{0}", validateCode);
		
		JSONObject json = new JSONObject();
        json.put("mobiles", mobile);
        json.put("msg", msgContent);
        noticeFeignClient.sendSms(JSONObject.toJSONString(json));
		
	}
	private String getMobileVerificationCodeCacheKey(String mobile) {
		return "SMS:MEMBER:" + mobile;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void validate(MobileCode mobileCode) {
		String mobile = mobileCode.getMobile();
		// 校验短信验证码是否正确
		String validateCode = mobileCode.getCode();
		if(StringUtils.isBlank(validateCode)) {
			log.error("-->validate-->手机号{}未输入验证码：{}", mobile, validateCode);
			throw new CustomException(CODE_INPUT);
		}
		if(validateCode.length() != 6) {
			log.error("-->validate-->手机号{}携带输入的验证码{}长度不符合", mobile, validateCode);
			throw new CustomException(CODE_ERROR);
		}
		try {
			Integer.valueOf(validateCode);
		} catch (Exception e) {
			log.error("-->validate-->手机号{}携带输入的验证码{}错误", mobile, validateCode);
			throw new CustomException(CODE_ERROR);
		}
		//程序运行到此处表明验证码有值，并且是有效的6位数字，开始校验是否和缓存中的匹配
		String cacheKey = getMobileVerificationCodeCacheKey(mobile);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if(StringUtils.isBlank(cacheValue)) {
        	// 缓存中没有验证码，表示验证码早已过期，超过了设定的5分钟
        	log.error("-->validate-->手机号{}在缓存中没有验证码", mobile, validateCode);
        	throw new CustomException(CODE_INVALID);
        }
        String cacheCode = cacheValue.split("_")[0]; 
        if(!validateCode.equals(cacheCode)) {
        	log.error("-->validate-->手机号{}携带输入的验证码{}和缓存中的不匹配", mobile, validateCode);
        	throw new CustomException(CODE_ERROR);
        }
        // 在用户输入的验证码和缓存中的验证码匹配的情况下，检查时间间隔是否已超过了5分钟
        // 由于缓存中的验证码有效期就是5分钟，所以只要缓存中有，并且匹配上了，即表示该验证码没有过期，但为了防止时间出现问题，还是加一层时间戳的检查，相当于双重校验
        long maxTimeInterval = 5 * 60 * 1000;// 单位毫秒，5分钟
        long cacheTimestamp = Long.valueOf(cacheValue.split("_")[1]);
        long currentTime = System.currentTimeMillis();
    	if(currentTime - cacheTimestamp > maxTimeInterval) {
    		log.error("-->validate-->手机号{}登录当前时间:{}距离上次缓存的时间:{}超过了5分钟，程序返回.", mobile, currentTime, cacheTimestamp);
    		throw new CustomException(CODE_INVALID);
    	}
    	// 此时校验全部成功，删除缓存中的验证码
    	redisTemplate.opsForValue().getOperations().delete(cacheKey);
    	log.info("-->validate-->手机号{}验证成功，删除验证码缓存", mobile);
		
    	// 再校验该手机号是否可绑定
    	Wrapper<MemberImport> wrapper = new EntityWrapper<MemberImport>();
		wrapper.eq(MOBILE, mobile);
		wrapper.eq(ACTIVE, 1);
		List<MemberImport> list = this.selectList(wrapper);
		if(list.isEmpty()) {
			throw new CustomException("你的手机号不符合条件，请联系客服处理");
		}
		if(list.get(0).getUserId() != null) {
			throw new CustomException("当前手机号已绑定过该平台！");
		}
		// 判断金额
		double cumulativeConsumption = memberConfigService.getMmjMemberCumulativeConsumption();
		if(list.get(0).getAmount() < cumulativeConsumption) {
			throw new CustomException("消费金额未满50元，无法成为会员");
		}
		
		// 判断是否已经是会员
		long userId = SecurityUserUtil.getUserDetails().getUserId();
		UserMember member = userMemberService.queryUserMemberInfoByUserId(userId);
		if(member != null && member.getActive()) {
			log.info("-->用户{}当前已经是会员", userId);
			throw new CustomException("您当前已是会员");
		} 
		SaveUserMemberDto dto = new SaveUserMemberDto();
		dto.setBeMemberType(MemberConstant.BE_MEMBER_TYPE_IMPORT);
		userMemberService.save(dto);
		
		cacheKey = UserConstant.IS_OLD_USER + userId;
		redisTemplate.opsForValue().set(cacheKey, String.valueOf(true));
		
		MemberImport mi = new MemberImport();
		mi.setId(list.get(0).getId());
		mi.setUserId(userId);
		mi.setUpdateTime(new Date());
		this.updateById(mi);
		log.info("-->更新用户ID：{}", userId);
		
		cacheKey = CACHE_KEY_PREFIX + userId;
		redisTemplate.delete(cacheKey);
	}

	@Override
	public double getImportMemberConsumptionAmount(long userId) {
		String cacheKey = CACHE_KEY_PREFIX + userId;
		String cacheValue = redisTemplate.opsForValue().get(cacheKey);
		if(StringUtils.isNotBlank(cacheValue)) {
			return Double.valueOf(cacheValue);
		} 
		double amount = 0;
		Wrapper<MemberImport> wrapper = new EntityWrapper<MemberImport>();
		wrapper.eq(USER_ID, userId);
		wrapper.eq(ACTIVE, 1);
		List<MemberImport> list = this.selectList(wrapper);
		if(!list.isEmpty()) {
			amount = list.get(0).getAmount();
		}
		redisTemplate.opsForValue().set(cacheKey, String.valueOf(amount));
		log.info("-->获取用户{}的第三方平台消费金额为:{}", userId, amount);
		return amount;
	}

}
