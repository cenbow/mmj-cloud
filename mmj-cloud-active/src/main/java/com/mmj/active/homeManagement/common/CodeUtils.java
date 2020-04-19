package com.mmj.active.homeManagement.common;

import java.util.Date;

public class CodeUtils {
    public static String getCode(){
        return String.valueOf(new Date().getTime());
    }
}
