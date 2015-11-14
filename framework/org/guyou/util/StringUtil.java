package org.guyou.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class StringUtil {
	
	public static final String EMPTY_STR = "";
	
	public static String getNotNull(String sRet) {
		if(sRet==null) return EMPTY_STR;
		return sRet;
	}
	
	public static boolean isNullValue(String sRet){
		return sRet==null || EMPTY_STR.equals(sRet);
	}
	
	public static <T> T[] toArray(String[] strArray,Class<T[]> clazz){
		Class<?> clazz_sub = clazz.getComponentType();
		if(clazz_sub==String.class){
			return (T[])strArray;
		}
		Object[] t = (Object[]) Array.newInstance(clazz_sub, strArray.length);
		for (int i = 0; i < strArray.length; i++) {
			if(clazz_sub==byte.class || clazz_sub==Byte.class){
				t[i] = isNullValue(strArray[i]) ? (byte)0 : Byte.valueOf(strArray[i]);
			}
			else if(clazz_sub==short.class || clazz_sub==Short.class){
				t[i] = isNullValue(strArray[i]) ? (short)0 : Short.valueOf(strArray[i]);
			}
			else if(clazz_sub==int.class || clazz_sub==Integer.class){
				t[i] = isNullValue(strArray[i]) ? 0 : Integer.valueOf(strArray[i]);
			}
			else if(clazz_sub==float.class || clazz_sub==Float.class){
				t[i] = isNullValue(strArray[i]) ? 0f : Float.valueOf(strArray[i]);
			}
			else if(clazz_sub==double.class || clazz_sub==double.class){
				t[i] = isNullValue(strArray[i]) ? 0d : Double.valueOf(strArray[i]);
			}
			else if(clazz_sub==long.class || clazz_sub==Long.class){
				t[i] = isNullValue(strArray[i]) ? 0L : Long.valueOf(strArray[i]);
			}
			else if(clazz_sub==boolean.class || clazz_sub==Boolean.class){
				t[i] = isNullValue(strArray[i]) ? false : Boolean.valueOf(strArray[i]);
			}
		}
		return (T[])t;
	}
	
	public static String[] toStringArray(String str,String mark){
		if(str==null) return null;
		if("".equals(str)) return new String[0];
		StringBuilder sb = new StringBuilder(str);
		int index = sb.indexOf(mark);
		if(index==-1) return new String[]{str};
		List<String> lst = new ArrayList<String>();
		while(true){
			if(index==-1){
				lst.add(sb.toString());
				break;
			}else{
				lst.add(deleteString(sb, 0, index));
				sb.delete(0, mark.length());
			}
			index = sb.indexOf(mark);
		}
		return lst.toArray(new String[0]);
	}
	
	public static StringBuilder deleteEndsMark(StringBuilder sb,String mark) {
		if (mark == null || mark.equals(EMPTY_STR)) {
			throw new NullPointerException("mark is no value!");
		}
		if(endsWith(sb.toString(),mark)){
			sb.delete(sb.length()-mark.length(), sb.length());
		}
		return sb;
	}
	
	public static StringBuffer deleteEndsMark(StringBuffer sb,String mark) {
		if (mark == null || mark.equals(EMPTY_STR)) {
			throw new NullPointerException("mark is no value!");
		}
		if(endsWith(sb.toString(),mark)){
			sb.delete(sb.length()-mark.length(), sb.length());
		}
		return sb;
	}
	
	public static String deleteString(StringBuilder sb,int start, int end) {
		String result = sb.substring(start, end);
		sb.delete(start, end);
		return result;
	}
	
	public static String deleteString(StringBuffer sb,int start, int end) {
		String result = sb.substring(start, end);
		sb.delete(start, end);
		return result;
	}
	
	public static StringBuilder deleteStartsMark(StringBuilder sb,String mark) {
		if (mark == null || mark.equals(EMPTY_STR)) {
			throw new NullPointerException("mark is no value!");
		}
		if(startsWith(sb.toString(),mark,0)){
			sb.delete(0, mark.length()); 
		}
		return sb;
	}
	
	public static StringBuffer deleteStartsMark(StringBuffer sb,String mark) {
		if (mark == null || mark.equals(EMPTY_STR)) {
			throw new NullPointerException("mark is no value!");
		}
		if(startsWith(sb.toString(),mark,0)){
			sb.delete(0, mark.length()); 
		}
		return sb;
	}
	
	private static boolean endsWith(String str,String mark) {
		return startsWith(str,mark, str.length() - mark.length());
	}
	
	private static boolean startsWith(String str,String mark, int paramInt) {
		char[] value = str.toCharArray(); 
		if ((paramInt < 0) || paramInt > value.length) {
			return false;
		}
		int k = mark.length();
		char[] chars = mark.toCharArray();
		while (--k >= 0) {
			if (value[paramInt+k] != chars[k]) {
				return false;
			}
		}
		return true;
	}
}
