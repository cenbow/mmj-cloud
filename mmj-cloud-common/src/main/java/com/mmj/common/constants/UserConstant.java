package com.mmj.common.constants;

public interface UserConstant {

    int STATUS_DEL = -1;

    int STATUS_NORMAL = 1;

    int STATUS_LOCK = 0;
    
    /**
     * 游客的用户ID，给微信外H5用的
     */
    long USER_VISITOR_ID = 95L;

    String IS_OLD_USER = "USER:OLD_USER:";

    String ISMEMBERONGOING = "USER:ISMEMBERONGOING:";

}
