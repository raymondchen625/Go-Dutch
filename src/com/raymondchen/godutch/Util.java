package com.raymondchen.godutch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Util {
	/**
	 * 
	 * @param dateString �ڶ�������Ϊ��ʽ�ַ�����������ʱΪĬ�ϸ�ʽ: yyyy-MM-dd HH:mm:ss ; ��һ������Ϊ�����ַ�����������null���߿մ����ص�ǰʱ��
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
