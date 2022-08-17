package com.shiku.mianshi.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtil {

	public static Long getTodayStart() {
		Calendar cal = new GregorianCalendar();
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    System.out.println(cal.getTime());
	    Date date = cal.getTime();
	    
		return date.getTime() / 1000;
		
	   
	}

	public static Long getTodayEnd() {
		 Calendar calendar = Calendar.getInstance();
	     calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),23,59,59);
	     long tt = calendar.getTime().getTime()/1000;
	    // System.out.println(tt);
	     return tt;
	
	}
	
}
