package com.mmj.common.utils;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
	
	 public static final String MOBILE_REGEX = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[1|8|9]))\\d{8}$";
	
	/**
	 * 生成UUID
	 * @return
	 */
    public static String getRandomUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
    /**
     * 生成短信验证码
     * @return
     */
    public static String genMobileValidateCode() {
    	return String.valueOf(new Random().nextInt(899999) + 100000);
    }
    
    /**
     * 校验手机号格式是否正确
     * @param mobile
     * @return
     */
    public static boolean checkMobile(String mobile) {
    	if(org.apache.commons.lang.StringUtils.isBlank(mobile)) {
    		return false;
    	}
    	Pattern pattern = Pattern.compile(MOBILE_REGEX);
        Matcher m = pattern.matcher(mobile);
        return m.matches();
    }

}
