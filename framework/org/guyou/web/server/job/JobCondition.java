/**
 * 
 */
package org.guyou.web.server.job;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.guyou.util.DateUtil;

/**
 * @author 朱施健
 *
 */
public class JobCondition implements Externalizable{
	
	private static final Logger log = Logger.getLogger(JobCondition.class);
	
	//年集合
	private List<Integer> yearList = new ArrayList<Integer>();
	//月份集合
	private List<Integer> monthList = new ArrayList<Integer>();
	//日期集合
	private List<Integer> dayList = new ArrayList<Integer>();
	//星期序列集合
	private List<Integer> weekNumList = new ArrayList<Integer>();
	//星期集合
	private List<Integer> dayInWeekList = new ArrayList<Integer>();
	//触发时间点
	private String doTimePoint = "";
	
	private Object[] delayTimes;
	
	public JobCondition(){}
	
	/**
	 * 比较时间限制
	 * @param condition
	 * @return
	 */
	public boolean equalsTimeLimit(JobCondition condition){
		if(condition==null) return false;
		String str1 = yearList.toString() + monthList.toString() + dayList.toString() + weekNumList.toString() + dayInWeekList.toString();
		String str2 = condition.yearList.toString() + condition.monthList.toString() + condition.dayList.toString() + condition.weekNumList.toString() + condition.dayInWeekList.toString();
		return str1.equals(str2);
	}
	/**
	 * 构造函数
	 * @param months 月份
	 * @param days 天
	 * @param weekNums 月份中第几个星期集合
	 * @param daysInWeek 星期几集合
	 * @param doTime 执行时间点
	 */
	public JobCondition(int[] years,int[] months,int[] days,int[] weekNums,int[] daysInWeek,String doTime){
		if(null!=years && years.length>0){
			for(Integer year:years){
				yearList.add(year);
			}
		}
		if(null!=months && months.length>0){
			for(Integer month:months){
				monthList.add(month);
			}
		}
		if(null!=days && days.length>0){
			for(Integer day:days){
				dayList.add(day);
			}
		}
		if(null!=weekNums && weekNums.length>0){
			for(Integer weekNum:weekNums){
				weekNumList.add(weekNum);
			}
		}
		if(null!=daysInWeek && daysInWeek.length>0){
			for(Integer dayInWeek:daysInWeek){
				dayInWeekList.add(dayInWeek);
			}
		}
		this.doTimePoint = (doTime!=null && doTime.length()>0)?doTime.trim():"";
	}
	
	public JobCondition(int[] years,int[] months,int[] days,int[] weekNums,int[] daysInWeek,String doTime,long[] delays){
		this(years, months, days, weekNums, daysInWeek, doTime);
		if(delays!=null && delays.length>0){
			this.delayTimes = new Long[delays.length];
			for (int i = 0; i < delays.length; i++) {
				this.delayTimes[i] = delays[i];
			}
		}
	}
	
	public JobCondition(int[] years,int[] months,int[] days,int[] weekNums,int[] daysInWeek,String doTime,String[] otherDoTimes){
		this(years, months, days, weekNums, daysInWeek, doTime);
		if(otherDoTimes!=null && otherDoTimes.length>0){
			this.delayTimes = new String[otherDoTimes.length];
			for (int i = 0; i < otherDoTimes.length; i++) {
				this.delayTimes[i] = otherDoTimes[i];
			}
		}
	}
	
	public boolean checkParam(){
		//00:00:00
		if(doTimePoint.length()!=8 || doTimePoint.indexOf(":")!=2 || doTimePoint.lastIndexOf(":")!=5){
			log.error("时间出发点的格式必须为\"hh:mm:ss\"!!");
			return false;
		} 
		
		if(dayList.size()>0 && (weekNumList.size()>0 || dayInWeekList.size()>0)){
			log.error("限定日期时,指定的星期无效!!");
			return false;
		}
		
		if(delayTimes.length>0){
			for(Object d : delayTimes){
				if(d instanceof String){
					String v = (String)d;
					if(v.length()!=8 || v.indexOf(":")!=2 || v.lastIndexOf(":")!=5){
						log.error("时间出发点的格式必须为\"hh:mm:ss\"!!");
						return false;
					} 
				}else if(d instanceof Long){
					long v = (Long)d;
					if(v<0){
						log.error("延迟的时间参数都必须大于0!!");
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public long[] getExcuteTimePointList(long nowTime){
		String yymmdd = (yearList.size()==1 && monthList.size()==1 && dayList.size()==1) ? (yearList.get(0)+"-"+monthList.get(0)+"-"+dayList.get(0)) : DateUtil.yyyy_MM_dd(nowTime);
		try {
			long[] r = new long[1+delayTimes.length];
			r[0] = DateUtil.getDate(yymmdd+" "+doTimePoint).getTime();
			for(int i=1;i<r.length;i++){
				Object d = delayTimes[i-1];
				if(d instanceof String){
					r[i]=DateUtil.getDate(yymmdd+" "+(String)delayTimes[i-1]).getTime();
				}else if(d instanceof Long){
					r[i]=r[0]+(Long)delayTimes[i-1];
				}
			}
			return r;
		} catch (ParseException e) {
			log.error("获得执行时间点异常",e);
			return new long[0];
		}
	}
	
	public Object[] getExcuteTimePointList(){
		Object[] objs = new Object[1+delayTimes.length];
		objs[0] = doTimePoint;
		for(int i=1;i<objs.length;i++){
			objs[i]=delayTimes[i-1];
		}
		return objs;
	}
	
	/**
	 * 是否满足条件
	 * @param nowTime 当前进时间
	 */
	public boolean isMeetCondition(long nowTime){
		boolean result = true;
		int[] timeInfo = DateUtil.getTimeInfo(nowTime);
		//是否是指定日期
		boolean isdata = dayList.size()>0;
		//是否是指定星期
		boolean isweek = weekNumList.size()>0 || dayInWeekList.size()>0;
		if(isdata && isweek){
			return false;
		}
		boolean isMeetYear = yearList.size()==0?true:(yearList.indexOf(timeInfo[1])!=-1);
		if(isdata){
			boolean isMeetMonth = monthList.size()==0?true:(monthList.indexOf(timeInfo[1])!=-1);
			boolean isMeetDay = dayList.size()==0?true:(dayList.indexOf(timeInfo[2])!=-1);
			result = isMeetYear && isMeetMonth && isMeetDay;
		}
		if(isweek){
			boolean isMeetMonth = monthList.size()==0?true:(monthList.indexOf(timeInfo[1])!=-1);
			boolean isMeetWeeknum = weekNumList.size()==0?true:(weekNumList.indexOf(timeInfo[3])!=-1);
			boolean isMeetDayInWeek = dayInWeekList.size()==0?true:dayInWeekList.indexOf(timeInfo[4])!=-1;
			result = isMeetYear && isMeetMonth && isMeetWeeknum && isMeetDayInWeek;
		}
		return result;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		int len = yearList.size();
		out.writeByte(len);
		for(int i : yearList) out.writeByte(i);
		
		len = monthList.size();
		out.writeByte(len);
		for(int i : monthList) out.writeByte(i);
		
		len = dayList.size();
		out.writeByte(len);
		for(int i : dayList) out.writeByte(i);
		
		len = weekNumList.size();
		out.writeByte(len);
		for(int i : weekNumList) out.writeByte(i);
		
		len = dayInWeekList.size();
		out.writeByte(len);
		for(int i : dayInWeekList) out.writeByte(i);
		
		out.writeUTF(doTimePoint);
		
		len = delayTimes==null ? 0 : delayTimes.length;
		for (int i = 0; i < len; i++) out.writeObject(delayTimes[i]);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		int len = in.readByte();
		for (int i = 0; i < len; i++)  yearList.add((int)in.readByte());
		
		len = in.readByte();
		for (int i = 0; i < len; i++)  monthList.add((int)in.readByte());
		
		len = in.readByte();
		for (int i = 0; i < len; i++)  dayList.add((int)in.readByte());
		
		len = in.readByte();
		for (int i = 0; i < len; i++)  weekNumList.add((int)in.readByte());
		
		len = in.readByte();
		for (int i = 0; i < len; i++)  dayInWeekList.add((int)in.readByte());
		
		doTimePoint = in.readUTF();
		
		len = in.readByte();
		delayTimes = new Object[len];
		for (int i = 0; i < len; i++)  delayTimes[i]=in.readObject();
	}
}
