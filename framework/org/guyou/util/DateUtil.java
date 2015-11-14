package org.guyou.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class DateUtil {

	private static Logger log = Logger.getLogger(DateUtil.class);
	
	private static final String a = "yyyy-MM-dd HH:mm:ss:SSS";
	private static final String b = "yyyyMMdd";
	private static final String c = "yyyyMMddHHmmss";
	private static final String d = "yyyyMMddHHmmssSSS";
	private static final String e = "yyyy-MM-dd HH:mm:ss";
	private static final String f = "yyyy-MM-dd";
	
	//月份中第几个星期集合
	private static final int[] cmap = new int[] { 0, 1, 2, 3, 4, 5, 6};
	//星期几集合
	private static final int[] imap = new int[] { 0, 7, 1, 2, 3, 4, 5, 6 };
	
	private static final ThreadLocal<Map<String,SimpleDateFormat>> threadLocal = new ThreadLocal<Map<String,SimpleDateFormat>>();
	
	private static SimpleDateFormat getDateFormat(String format){
		Map<String,SimpleDateFormat> map = threadLocal.get();
		if (map == null) {
			map = new HashMap<String,SimpleDateFormat>(); 
			threadLocal.set(map);
		}
		SimpleDateFormat f = map.get(format);
		if(f==null){
			f = new SimpleDateFormat(format);
			map.put(format, f);
		}
		return f;
	}

	public static String yyyyMMddHHmmss() {
		return getDateFormat(c).format(new Date());
	}

	public static String yyyyMMddHHmmssSSS() {
		return getDateFormat(d).format(new Date());
	}

	public static String format() {
		return getDateFormat(a).format(new Date());
	}

	public static String time() {
		return getDateFormat(e).format(new Date());
	}
	
	public static String yyyyMMdd() {
		return getDateFormat(b).format(new Date());
	}
	
	public static String yyyyMMdd(long now) {
		return getDateFormat(b).format(new Date(now));
	}

	public static String yyyyMMdd(Date date) {
		return getDateFormat(b).format(date);
	}

	public static String yyyy_MM_dd() {
		return getDateFormat(f).format(new Date());
	}

	public static String yyyy_MM_dd(long now) {
		return getDateFormat(f).format(new Date(now));
	}

	public static String yyyy_MM_dd(Date date) {
		return getDateFormat(f).format(date);
	}

	public static String format(String s) {
		return getDateFormat(s).format(new Date());
	}

	public static String format(Calendar calendar) {
		return getDateFormat(a).format(calendar.getTime());
	}

	public static String format(Calendar calendar, String s) {
		return getDateFormat(s).format(calendar.getTime());
	}

	public static String format(Date date) {
		return getDateFormat(e).format(date);
	}

	public static String formatInYMDHMS(Date date) {
		return getDateFormat(e).format(date);
	}

	public static String format(Date date, String s) {
		return getDateFormat(s).format(date);
	}

	public static Date getDate() {
		return a(Calendar.getInstance()).getTime();
	}

	public static Date getDate(Date date) {
		Calendar calendar;
		(calendar = Calendar.getInstance()).setTime(date);
		return a(calendar).getTime();
	}
	
	public static long getTodayZero(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
	}
	
	public static long getZero(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
	}

	public static Date getDate(String date) throws ParseException {
		return getDateFormat(e).parse(date);
	}
	
	public static Date getDate(String date,String format) throws ParseException {
		return getDateFormat(format).parse(date);
	}

	public static Calendar getCalendarBefore(Date date, int i) {
		Calendar calendar;
		(calendar = Calendar.getInstance()).setTime(date);
		return getCalendarBefore(calendar, i);
	}

	public static Calendar getCalendarBefore(int i) {
		return getCalendarBefore(Calendar.getInstance(), i);
	}

	public static Calendar getCalendarBefore(Calendar calendar, int i) {
		calendar.add(5, -1 * i);
		return a(calendar);
	}

	private static Calendar a(Calendar calendar) {
		calendar.set(11, 0);
		calendar.clear(12);
		calendar.clear(13);
		calendar.clear(14);
		return calendar;
	}

	public static java.math.BigDecimal changeNumbericToBigDecimal(int x) {
		return new java.math.BigDecimal(x);
	}

	public static java.math.BigDecimal changeNumbericToBigDecimal(float x) {
		return new java.math.BigDecimal(x);
	}

	public static java.math.BigDecimal changeNumbericToBigDecimal(double x) {
		return new java.math.BigDecimal(x);
	}

	/**
	 * 增加小时
	 * 
	 * @param hour
	 *            小时,负值为减
	 * @return
	 */
	public static Date addHour(int hour) {
		return addTime(Calendar.HOUR, hour);
	}

	/**
	 * 增加分钟
	 * 
	 * @param minute
	 *            分钟,负值为减
	 * @return
	 */
	public static Date addMinute(int minute) {
		return addTime(Calendar.MINUTE, minute);
	}

	/**
	 * 增加秒
	 * 
	 * @param hour
	 *            秒,负值为减
	 * @return
	 */
	public static Date addSecond(int second) {
		return addTime(Calendar.SECOND, second);
	}

	/**
	 * 添加时间
	 * 
	 * @param timeUnit
	 *            时间单位,Calendar的常量
	 * @param addTimeNum
	 *            时间
	 * @return
	 */
	private static Date addTime(int timeUnit, int addTimeNum) {
		Calendar cal = Calendar.getInstance();
		cal.add(timeUnit, addTimeNum);
		return cal.getTime();
	}

	/**
	 * 判断是否同一天
	 * 
	 * @param lastUpgradeTime
	 * @return
	 */
	public static boolean isSameDay(Date lastUpgradeTime) {
		Calendar now = Calendar.getInstance();
		Calendar lastUpgrade = Calendar.getInstance();
		lastUpgrade.setTime(lastUpgradeTime);
		if (now.get(Calendar.DATE) == lastUpgrade.get(Calendar.DATE)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否同一天
	 * 
	 * @param lastUpgradeTime
	 * @return
	 */
	public static boolean isSameDay(long lastUpgradeTime) {
		Calendar now = Calendar.getInstance();
		Calendar lastUpgrade = Calendar.getInstance();
		lastUpgrade.setTimeInMillis(lastUpgradeTime);
		return now.get(Calendar.DATE) == lastUpgrade.get(Calendar.DATE);
	}

	public static String afterDays(String time,String format, int days) throws ParseException {
		return format(new Date(getDate(time,format).getTime() + days * 24 * 60 * 60 * 1000L),format);
	}

	public static String beforeDays(String time,String format, int days)
			throws ParseException {
		return format(new Date(getDate(time,format).getTime() - days * 24 * 60 * 60 * 1000L),format);
	}

	public static String formatYYYYMMDD(String yyyMMdd) throws ParseException {
		return yyyy_MM_dd(DateUtil.getDate(yyyMMdd + " 12:00:00").getTime());
	}

	// 取得传入yyyy-mm-dd日期和向后几天、向前几天，返回一个日期集合
	public static String[] getDatesBetweenTwoDay(String sdate, int days) {
		String flag = "next";
		if (days < 0)
			flag = "pre";
		days = Math.abs(days);
		String[] str = new String[days];
		String strtemp = "";
		for (int i = 0; i < days; i++) {

			if (i == 0)
				strtemp = sdate;
			str[i] = getDate(strtemp,f,flag);
			strtemp = str[i];
		}
		return str;
	}
	
	// 取得2个日期之间差几天
	public static int getDaysBetweenTwoDay(String sdate1, String sdate2) {
		try {
			Date date1 = getDateFormat(f).parse(sdate1);
			java.util.Calendar c1 = new java.util.GregorianCalendar();
			c1.setTime(date1);

			Date date2 = getDateFormat(f).parse(sdate2);
			java.util.Calendar c2 = new java.util.GregorianCalendar();
			c2.setTime(date2);
			return (int) ((c1.getTimeInMillis() - c2.getTimeInMillis()) / (1000 * 60 * 60 * 24));
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static int getDaysBetweenTwoDay(long sdate1, long sdate2) {
		long sdata1_z = getZero(sdate1);
		long sdata2_z = getZero(sdate2);
		return (int) ((sdata2_z - sdata1_z)/24/3600/1000);
	}

	// sdate:日期yyyy-mm-dd;i:next-向后取 pre-向前取
	private static String getDate(String sdate,String format, String sflag) {
		try {
			SimpleDateFormat df = getDateFormat(format);
			Date newdate = df.parse(sdate);
			java.util.Calendar cal = new java.util.GregorianCalendar();
			cal.setTime(newdate);
			if (sflag.equals("next"))
				cal.add(Calendar.DATE, 1);
			else
				cal.add(Calendar.DATE, -1);
			Date dt = cal.getTime();
			return df.format(dt);
		} catch (Exception e) {
			log.error("getDate",e);
			return "";
		}
	}
	
	public static String polishing(String date) throws ParseException{
		date = date.trim();
		int hg_count = 0,mh_count=0,kg_count=0;
		int len = date.length();
		for ( int i = 0 ; i < len ; i++ ) {
			char c = date.charAt(i);
			if(c=='-') hg_count += 1;
			if(c==':') mh_count += 1;
			if(c==' ') kg_count += 1;
		}
		if(hg_count!=2){
			throw new IllegalStateException("日期格式错误!!");
		}
		if(kg_count>1) date = date.replace("  "," ");
		String r = date;
		//2014-05-04
		if(hg_count==2 && kg_count==0 && mh_count==0) r = date + " 00:00:00";
		//2014-05-04 10
		if(hg_count==2 && kg_count>0 && mh_count==0) r = date + ":00:00";
		//2014-05-04 10:10
		if(hg_count==2 && kg_count>0 && mh_count==1) r = date + ":00";
		
		if(r==null){
			throw new IllegalStateException("时间为空");
		}
		if(r.length()!=19){
			r = format(getDate(r));
		}
		return r;
	}
	
	/**
	 * [当前年,当前月,当前日,当前是第几个星期,当前是星期几]
	 * @param nowTime
	 * @return
	 */
	public static int[] getTimeInfo(long nowTime){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(nowTime);
		int[] r = new int[]{cal.get(Calendar.YEAR),cal.get(Calendar.MONTH) + 1,cal.get(Calendar.DAY_OF_MONTH),0,0};
		r[3] = cmap[cal.get(Calendar.WEEK_OF_MONTH)];
		r[4] = imap[cal.get(Calendar.DAY_OF_WEEK)];
		
		if(r[4]==7 && r[3]>1){
			r[3] -=1;
		}
		return r;
	}
	
	public static long now_relative(String HHmmss){
		String[] resetTimePoint = HHmmss.split(":");
		long resetTimePointValue = (Long.valueOf(resetTimePoint[0])*60*60 + Long.valueOf(resetTimePoint[1])*60 + Long.valueOf(resetTimePoint[2])) * 1000L;
		return System.currentTimeMillis() - resetTimePointValue;
	}
	
	public static long now_relative(long now,String HHmmss){
		String[] resetTimePoint = HHmmss.split(":");
		long resetTimePointValue = (Long.valueOf(resetTimePoint[0])*60*60 + Long.valueOf(resetTimePoint[1])*60 + Long.valueOf(resetTimePoint[2])) * 1000L;
		return now - resetTimePointValue;
	}

	public static void main(String[] args) throws ParseException {
//		Date d1 = getDate("2013-11-11");
//		Date d2 = getDate("2013-11-12");
//		System.out.println(getDaysBetweenTwoDay(d1.getTime(),d2.getTime()));
		System.out.println(getDaysBetweenTwoDay("2013-11-15","2013-11-12"));
	}
}
