package com.mmj.order.utils.constant;

/**
 * 全局通用工具类
 */
public interface CommonConstant {
	
	String REPLACE_INDEX_0 = "{0}";
	
	String REPLACE_INDEX_1 = "{1}";
	
	String REPLACE_INDEX_2 = "{2}";
	
	String OPENID = "openid";
	
	String USERID = "userid";

	/**
	 * 满3件免运费
	 */
	Integer FREIGHT_COUNT_LIMIT = 3;

	/**
	 * 满30元免邮
	 */
	Double FREIGHT_TOTAL_PRICE_LIMIT = 30.0;
	
	Double FREIGHT_PRICE = 10.0d;
	
	/**
	 * 常用符号
	 * @author shenfuding
	 *
	 */
	interface Symbol {
		
		String COMMA = ",";
		
		String ENUMERATION_COMMA = "、";
		
		String PERIOD = ".";
				
		String COLON = ":";
		
		String SEMICOLON = ";";
		
		String EXCLAMATION = "!";
		
		String QUESTION_MARK = "?";

		String UNDERLINE = "_";

		String SPRIT = "/";
	}
	
}
