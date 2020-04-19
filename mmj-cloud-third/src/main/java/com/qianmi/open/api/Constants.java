package com.qianmi.open.api;

/**
 * 常量类。
 */
public abstract class Constants {

	/** 默认时间格式 **/
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/** Date默认时区 **/
	public static final String DATE_TIMEZONE = "GMT+8";

	/** UTF-8字符集 **/
	public static final String CHARSET_UTF8 = "UTF-8";

	/** GBK字符集 **/
	public static final String CHARSET_GBK = "GBK";

	/** JSON 应格式 */
	public static final String FORMAT_JSON = "json";
	/** MD5签名方式 */
	public static final String SIGN_METHOD_MD5 = "md5";
	/** HMAC签名方式 */
	public static final String SIGN_METHOD_HMAC = "hmac";
	/** 授权地址 */
	public static final String PRODUCT_AUTH_URL = "http://oauth.bm001.com/token";

	/** SDK版本号 */
	public static final String SDK_VERSION = "qianmi-open-sdk-java-20181227";

	/** 返回的错误码 */
	public static final String ERROR_RESPONSE = "error_response";
	public static final String ERROR_CODE = "code";
	public static final String ERROR_MSG = "msg";
	public static final String ERROR_SUB_CODE = "sub_code";
	public static final String ERROR_SUB_MSG = "sub_msg";
}
