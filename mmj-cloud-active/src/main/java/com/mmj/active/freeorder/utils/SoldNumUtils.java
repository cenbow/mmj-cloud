package com.mmj.active.freeorder.utils;

import org.apache.commons.lang.StringUtils;

public class SoldNumUtils {

    private static final Integer virualNum = 1000;

    public static Integer genSoldNum(String spu,Integer qty){
        try {
            if (null == qty)
                qty = 0;
            if (StringUtils.isBlank(spu))
                return 1000;
            String spuCnt = spu.substring(spu.length()-4);
            return (virualNum + Integer.parseInt(spuCnt) + qty);
        }catch (Exception e){
            return 1000;
        }
    }
}
