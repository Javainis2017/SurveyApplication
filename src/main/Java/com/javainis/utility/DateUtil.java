package com.javainis.utility;

import javax.enterprise.context.ApplicationScoped;
import java.util.Calendar;
import java.util.Date;

@ApplicationScoped
public class DateUtil
{
    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
}
