package com.mmj.common.properties;

public interface SecurityConstants {

  String JWT_SIGNING_KEY = "mmj@2019#2A";

  String BASE_ROLE = "ROLE_USER";

  int EXCEPTION_CODE = -1;

  int FAIL_CODE = 0;

  int SUCCESS_CODE = 1;

  String STATUS_DEL = "-1";

  String STATUS_NORMAL = "1";

  String STATUS_LOCK = "2";

  String HEAD_AUTH = "Authorization";

  String HEAD_GRAY = "grayflag";

  String APP_TYPE = "appType";

  String DEFAULT_PARAMETER_NAME_CODE_IMAGE = "image";

  String DEFAULT_PARAMETER_NAME_CODE_SMS = "sms";

  String DEFAULT_PARAMETER_NAME_MOBILE = "mobile";

  String DEFAULT_LOGIN_PROCESSING_URL_FORM = "/oauth/token";

  String DEFAULT_LOGIN_PROCESSING_URL_MOBILE = "/login/mobile";

  String GRAY_INFO_KEY = "gray:info:grayinfo";

  String THREAD_LOCAL_GRAY_KEY = "threadLocalGrayKey";

  String ROUTE_KEY = "route:dynamic:key";

  String SHARDING_KEY = "shardingKey";

  String SHARDING_GROUP_KEY = "shardingGroupKey";

  String DEFAULT_PARAMETER_NAME_OPENID = "openId";

  String DEFAULT_PARAMETER_NAME_PROVIDERID = "providerId";

  String DEFAULT_LOGIN_PROCESSING_URL_OPENID = "/login/openid";

  String PRODUCE_FAIL_ORDER_NO = "PRODUCE_FAIL_ORDER_NO";
  
  String TOKEN_USER = "TOKEN_USER";
  
  String USER_ID = "userId";
  
}
