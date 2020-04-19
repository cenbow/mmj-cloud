package com.mmj.active.homeManagement.common;

import java.util.Date;

public class TimeUtils {
    public static boolean havaSame(Date startA, Date endA, Date startB, Date endB)
    {

        if ((endA.getTime() < startB.getTime()) || startA.getTime() > endB.getTime())
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
