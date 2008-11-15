package se.peertv.peertvsim.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Utils {

	public static int min(int a, int b){
			return a < b ?  a : b;  
	}
	
	public static String TimeStamp(){
		Calendar cal 	= new GregorianCalendar();
		int hour24 		= cal.get(Calendar.HOUR_OF_DAY); // 0..23
		int min 			= cal.get(Calendar.MINUTE); // 0..9
		int sec 			= cal.get(Calendar.SECOND); // 0..59
		int year 			= cal.get(Calendar.YEAR); // 2002
		int month 		= cal.get(Calendar.MONTH)+1; // 0=Jan, 1=Feb, ...
		int day 			= cal.get(Calendar.DAY_OF_MONTH); // 1...

		return(year+"_"+pad(month)+"_"+pad(day)+"__"+pad(hour24)+ "_" + pad(min) + "_" + pad(sec));
	}
	
	private  static String pad(int i){
		String s = i+"";
		if (s.length()== 1)
			return("0"+s);
		return(s);
	}

}

