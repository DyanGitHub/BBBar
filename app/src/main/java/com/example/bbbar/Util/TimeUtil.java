package com.example.bbbar.Util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	//根据毫秒数获取年月日信息
    public static String getYearAndMonth(long l)
    {
    	 
    	  Date date = new Date(l);
    	  Calendar cal = Calendar.getInstance();
    	  cal.setTime(date);
    	  int year=cal.get(Calendar.YEAR);
    	  int month=cal.get(Calendar.MONTH) + 1;// 月份计算是从0作为1开始的。
    	  int day=cal.get(Calendar.DATE);
    	  return year+"年"+month+"月"+day+"日";
//    	  System.out.println(cal.get(Calendar.HOUR));
//    	  System.out.println(cal.get(Calendar.MINUTE));
//    	  System.out.println(cal.get(Calendar.SECOND));
    }
}
