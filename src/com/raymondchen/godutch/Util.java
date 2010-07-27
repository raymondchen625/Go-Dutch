package com.raymondchen.godutch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Util {
	/**
	 * 
	 * @param dateString 第二个参数为格式字符串，不存在时为默认格式: yyyyMMdd HH:mm:ss ; 第一个参数为日期字符串。参数传null或者空串返回当前时间
	 * @return
	 */
	public static Date getDateFromString(String ... dateString) throws ParseException {
		if (dateString == null || dateString.length==0) {
			return new Date();
		}
		String formatString="yyyy-MM-dd HH:mm:ss";
		if (dateString.length>1) {
			formatString=dateString[1];
		}
		SimpleDateFormat sdf=new SimpleDateFormat(formatString);
		return sdf.parse(dateString[0]);
	}
}
