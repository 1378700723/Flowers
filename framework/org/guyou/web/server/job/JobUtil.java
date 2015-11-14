/**
 * 
 */
package org.guyou.web.server.job;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.guyou.util.MathUtil;
import org.guyou.util.StringUtil;

/**
 * @author 朱施健
 *
 */
public class JobUtil {
	
	private static final Logger log = Logger.getLogger(JobUtil.class);
	
	private static final int[] MAX_MONTH = new int[]{1,3,5,7,8,10,12};
	private static final int[] MIN_MONTH = new int[]{4,6,9,11};

	
	/**
	 *  “HH:mm:ss” ”yyyy-mm-dd“ ”mm-dd“ ”mm-dd HH:mm:ss“ ”dd HH:mm:ss“
	 */
	//static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 构建执行时间属性
	 * @param excuteTime 执行时间 (如需要 mm-dd这样的格式 则 后面必须追加00:00:00)
	 * @return
	 */
	static String[] buildExcuteTime(String excuteTime) {

		String[] objs = new String[6];

		// 默认时间模板
		String template = "****-**-** **:**:**";
		// 执行时间转换为 可读时间
		StringBuilder result = new StringBuilder(convert(convert(template)
				.replace(convert(clearNum(excuteTime)), convert(excuteTime))));
		// 获取执行时间年份
		String yearTemp = StringUtil.deleteString(result,0, result.indexOf("-"));
		StringUtil.deleteStartsMark(result, "-");

		String monthTemp = StringUtil.deleteString(result,0, result.indexOf("-"));
		StringUtil.deleteStartsMark(result,"-");

		String dayTemp = StringUtil.deleteString(result,0, result.indexOf(" "));
		StringUtil.deleteStartsMark(result," ");

		String hourTemp = StringUtil.deleteString(result,0, result.indexOf(":"));
		StringUtil.deleteStartsMark(result,":");

		String minuteTemp = StringUtil.deleteString(result,0, result.indexOf(":"));
		StringUtil.deleteStartsMark(result,":");

		String secondsTemp = result.toString();

		objs[0] = yearTemp;
		objs[1] = monthTemp.contains("*") ? monthTemp : Integer.parseInt(monthTemp) - 1 + "";
		objs[2] = dayTemp;
		objs[3] = hourTemp;
		objs[4] = minuteTemp;
		objs[5] = secondsTemp;

		return objs;
	}

	/**
	 * 构建参照时间属性
	 * @param c
	 * @return
	 */
	static int[] buildCzTime(Calendar c) {

		int[] objs = new int[6];

		objs[0] = c.get(Calendar.YEAR);
		objs[1] = c.get(Calendar.MONTH);
		objs[2] = c.get(Calendar.DAY_OF_MONTH);
		objs[3] = c.get(Calendar.HOUR_OF_DAY);
		objs[4] = c.get(Calendar.MINUTE);
		objs[5] = c.get(Calendar.SECOND);

		return objs;
	}

	/**
	 * 获取下一次要执行的时间
	 * 
	 * @param czTime
	 *            参照时间
	 * @param excuteTime
	 *            执行时间
	 * @return 下一次要执行的时间戳 (如果返回0则代表出现异常)
	 */
	public static long getNextExcuteTimeForString(long czTime, String excuteTime) {
		// 参照时间戳
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(czTime);
		
		String[] exeObjs = buildExcuteTime(excuteTime);

		int[] czObjs = buildCzTime(c);
		
		int index = 0;
		
		long czTimeMillis = 0;
		
		long exeTimemillis = 0;
		
		int exeYear = exeObjs[0].contains("*") ? czObjs[0] : Integer.parseInt(exeObjs[0]);
		int exeMonth = exeObjs[1].contains("*") ? czObjs[1] : Integer.parseInt(exeObjs[1]);
		
		for(int i = exeObjs.length - 1; i >= 0 ; i--) {
			
			if(exeObjs[i].contains("*")){
				index = i + 1;
				break;
			}
			
			int czValue = czObjs[i];
			int exeValue = Integer.parseInt(exeObjs[i].toString());
			
			czTimeMillis += czValue * timeFactory(i, czObjs[0], czObjs[1]);
			
			exeTimemillis += exeValue * timeFactory(i,exeYear, exeMonth);
			
		}
		
		// 如果参照时间小于 模板时间 则返回模板时间
		if (exeTimemillis > czTimeMillis) {
			index = -1;
		}
		
		return result(index, czObjs, exeObjs).getTimeInMillis();
	}
	
	/**
	 * 获取月份中的天数
	 * @param year
	 * @param month
	 * @return
	 */
	public static int daysInMonth(int year,int month){
		boolean isRunNian = (year % 4 == 0 && year % 100 != 0 || year % 400 == 0);
		if(ArrayUtils.contains(MAX_MONTH,month)) return 31;
		if(ArrayUtils.contains(MIN_MONTH,month)) return 30;
		return isRunNian?29:28;
	}
	

	/**
	 * 结果时间戳 日期进位
	 * 
	 * @param index
	 *            时间类型索引(按照以年开头的有序索引)
	 * @param czObjs
	 *            执行时间参考数据
	 * @param exeObjs
	 *            模板时间参考数据
	 * @return 进位之后的结果时间戳
	 */
	static Calendar result(int index, int[] czObjs,String[] exeObjs) {
		
		for(int i = 0 ; i < czObjs.length ; i++) {
			
			if(exeObjs[i].contains("*")) {
				exeObjs[i] = String.valueOf(czObjs[i]);
			}
		}
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(exeObjs[0].toString()));
		c.set(Calendar.MONTH, Integer.parseInt(exeObjs[1].toString()));
		c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(exeObjs[2].toString()));
		c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(exeObjs[3].toString()));
		c.set(Calendar.MINUTE, Integer.parseInt(exeObjs[4].toString()));
		c.set(Calendar.SECOND, Integer.parseInt(exeObjs[5].toString()));

		if(index == 0) {
			return null;
		}
		else if (index == 1 ) {
			c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
		} else if (index == 2) {
			c.setTimeInMillis(c.getTimeInMillis()
					+ timeFactory(index - 1, c.get(Calendar.YEAR),
							c.get(Calendar.MONTH)));
		} else if (index == 3) {
			c.setTimeInMillis(c.getTimeInMillis()
					+ timeFactory(index - 1, c.get(Calendar.YEAR),
							c.get(Calendar.MONTH)));
		} else if (index == 4) {
			c.setTimeInMillis(c.getTimeInMillis()
					+ timeFactory(index - 1, c.get(Calendar.YEAR),
							c.get(Calendar.MONTH)));
		} else if (index == 5) {
			c.setTimeInMillis(c.getTimeInMillis()
					+ timeFactory(index - 1, c.get(Calendar.YEAR),
							c.get(Calendar.MONTH)));
		}

		return c;
	}

	/**
	 * 根据不同过的类型获取 对应的时间毫秒数
	 * 
	 * @param index
	 *           时间类型索引(按照以年开头的有序索引)
	 * @param year
	 *            当前时间戳的年限(用于验证是平年还是闰年)
	 * @param month
	 *            当前时间戳的月份 (用于返回月份时间戳的时候寻找当前月有多少天)
	 * @return
	 */
	static long timeFactory(int index, int year, int month) {

		if (index == 0) {
			return yearMillis(year + 1);
		} else if (index == 1) {
			return monthMillis(year, month);
		} else if (index == 2) {
			return dayMillis();
		} else if (index == 3) {
			return hourMillis();
		} else if (index == 4) {
			return minuteMillis();
		} else if (index == 5) {
			return secondsMillis();
		}

		return 0;
	}

	public static long yearMillis(int year) {
		if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
			return (long) 1000 * 60 * 60 * 24 * 366;
		} else {
			return (long) 1000 * 60 * 60 * 24 * 365;
		}
	}

	/**
	 * 月份对应的毫秒数
	 * 
	 * @param year
	 *            年份
	 * @param monthNum
	 *            月份
	 * @return
	 */
	public static long monthMillis(int year, int monthNum) {

		if (monthNum == 13) {
			year += 1;
			monthNum = 0;
		}

		monthNum += 1;

		if (monthNum == 2) {
			if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
				return (long) 1000 * 60 * 60 * 24 * 29;
			} else {
				return (long) 1000 * 60 * 60 * 24 * 28;
			}
		} else if (monthNum == 1 || monthNum == 3 || monthNum == 5
				|| monthNum == 7 || monthNum == 8 || monthNum == 10
				|| monthNum == 12) {
			return (long) 1000 * 60 * 60 * 24 * 31;
		} else {
			return (long) 1000 * 60 * 60 * 24 * 30;
		}
	}

	/**
	 * 字符串颠倒转换
	 * 
	 * @param str
	 *            要转换的字符串
	 * @return 转换后的字符串
	 */
	static String convert(String str) {

		String result = "";

		for (int i = str.length() - 1; i >= 0; i--) {
			result += str.charAt(i);
		}
		return result;
	}

	/**
	 * 将执行字符串 清空为 0000-00-00 00:00:00的格式
	 * 
	 * @param s
	 * @return
	 */
	static String clearNum(String s) {
		String temp = "";

		for (int i = 0; i < s.length(); i++) {

			char ch = s.charAt(i);
			temp += isNaN(ch) ? "*" : ch;
		}
		return temp;
	}

	/**
	 * 天对应的毫秒数
	 * 
	 * @return
	 */
	public static long dayMillis() {
		return 1000l * 60 * 60 * 24;
	}

	/**
	 * 小时对应的毫秒数
	 * 
	 * @return
	 */
	public static long hourMillis() {
		return 1000l * 60 * 60;
	}

	/**
	 * 分钟对应的毫秒数
	 * 
	 * @return
	 */
	public static long minuteMillis() {
		return 1000l * 60;
	}

	/**
	 * 秒对应的毫秒数
	 * 
	 * @return
	 */
	public static long secondsMillis() {
		return 1000l;
	}

	/**
	 * 是否是一个数字
	 * 
	 * @param ch
	 *            字符
	 * @return
	 */
	public static boolean isNaN(char ch) {
		if (ch >= '0' && ch <= '9') {
			return true;
		}
		return false;
	}
	
	/**
	 * 获得指定时间单位的数量
	 * @param millis 毫秒数
	 * @param timeUnit 时间单位
	 * @return long 数量
	 */
	public static long timeUnitsFromMillis(long millis,TimeUnit timeUnit){
		long r = 0;
		if(timeUnit == TimeUnit.MILLISECONDS){
			r = millis;
		}else if(timeUnit == TimeUnit.SECONDS){
			r = Math.round(millis/1000.0);
		}else if(timeUnit == TimeUnit.MINUTES){
			r = Math.round(millis/1000.0/60.0);
		}else if(timeUnit == TimeUnit.HOURS){
			r = Math.round(millis/1000.0/60.0/60.0);
		}else if(timeUnit == TimeUnit.DAYS){
			r = Math.round(millis/1000.0/60.0/60.0/24.0);
		}
		return r;
	}
	
	/**
	 * 判断某个时间点是否在两个条件之间
	 * @param now
	 * @param condition1
	 * @param condition2
	 * @return
	 */
	public static boolean isBetweenCondition(long now,JobCondition condition1,JobCondition condition2){
		if(condition1.isMeetCondition(now) && condition2.isMeetCondition(now) && condition1.equalsTimeLimit(condition2)){
			long[] points1 = condition1.getExcuteTimePointList(now);
			long[] points2 = condition2.getExcuteTimePointList(now);
			return points1[0]>points2[0] ? (now<=points1[0] && now>=points2[0]) : (now>=points1[0] && now<=points2[0]);
		}
		return false;
	}
	
	/**
	 * 构建Job条件
	 * @param conditionValue
	 * @return
	 */
	public static JobCondition buildJobCondition(String conditionValue) {
		if(conditionValue == null || conditionValue.equals("") || conditionValue.equals("0")){
			log.error("JobCondition数据不能为空!!");
			return null;
		}
		String[] values = conditionValue.split("\\|");
		if(values.length!=6){
			log.error("JobCondition有5个时间级和时间出发点,形如\"*|*|*|*|*|04:00:00\",现个数错误!!");
			return null;
		}
		
		int[] years = values[0].equals("*")?null:strArrayToIntArray(values[0].split("\\&"));
		int[] months = values[1].equals("*")?null:strArrayToIntArray(values[1].split("\\&"));
		int[] days = values[2].equals("*")?null:strArrayToIntArray(values[2].split("\\&"));
		int[] weekNums = values[3].equals("*")?null:strArrayToIntArray(values[3].split("\\&"));
		int[] daysInWeeks = values[4].equals("*")?null:strArrayToIntArray(values[4].split("\\&"));
		
		JobCondition c = null;
		if(values[5].indexOf("&")==-1){
			c = new JobCondition(years, months, days, weekNums, daysInWeeks, values[5]);
		}else{
			String[] ps = values[5].split("\\&");
			boolean isNumber = true;
			for(int i=1;i<ps.length;i++){
				if(!MathUtil.isNumber(ps[i])){
					isNumber = false;
					break;
				}
			}
			if(isNumber){
				long[] v = new long[ps.length-1];
				for(int i=1;i<ps.length;i++){
					v[i-1]=Long.valueOf(ps[i])*1000;
				}
				c = new JobCondition(years, months, days, weekNums, daysInWeeks, ps[0],v);
			}else{
				String[] v = new String[ps.length-1];
				for(int i=1;i<ps.length;i++){
					v[i-1]=ps[i];
				}
				c = new JobCondition(years, months, days, weekNums, daysInWeeks, ps[0],v);
			}
		}
		return c;
	}
	
	/**
	 * 字符串数组转换为 int数组
	 * 
	 * @param strArray
	 *            字符串数组
	 * @return int数组
	 */
	private static int[] strArrayToIntArray(String[] strArray) {
		int[] intArray = new int[strArray.length];
		for (int i = 0; i < strArray.length; i++) {
			intArray[i] = Integer.valueOf(strArray[i]);
		}
		return intArray;
	}
}
