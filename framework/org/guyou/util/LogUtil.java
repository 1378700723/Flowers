/**
 * 
 */
package org.guyou.util;

import java.io.File;
import java.io.FileWriter;
import java.util.Enumeration;

import org.apache.log4j.Logger;

/**
 * @author 朱施健
 *
 */
public class LogUtil {
	
	public static File getLog4jFile(){
		Enumeration<?> appenders = Logger.getRootLogger().getAllAppenders();
		while (appenders.hasMoreElements()) {
			Object appender = appenders.nextElement();
			if(appender instanceof org.apache.log4j.FileAppender){
				return new File(((org.apache.log4j.FileAppender)appender).getFile());
			}
		}
		return null;
	}
	
	public static synchronized void writeToFile(File file,String log){
		try {
			FileWriter fileWriter=new FileWriter(file,true);
			fileWriter.write(log);
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void writeToFile(File file,boolean append,String log){
		try {
			FileWriter fileWriter=new FileWriter(file,append);
			fileWriter.write(log);
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void printStackTrace(String msg,Throwable throwable){
		StringBuilder str = new StringBuilder(throwable.getClass().getName()+" : "+throwable.getMessage()+"("+msg+")\n");
		str.append(stackTrace(throwable.getStackTrace()));
		System.err.println(str.toString());
		Throwable cause = throwable.getCause();
		if(cause!=null){
			printStackTrace(cause.getMessage(),cause);
		}
	}
	
	public static void printStackTrace(String msg,StackTraceElement[] stackTrace){
		StringBuilder str = new StringBuilder(msg+"\n");
		str.append(stackTrace(stackTrace));
		System.err.println(str.toString());
		str = null;
	}
	
	public static String stackTrace(StackTraceElement[] stackTrace){
		StringBuilder str = new StringBuilder("");
		for ( StackTraceElement s : stackTrace ) {
			str.append("\t").append(s.getClassName()+"."+ s.getMethodName()+ "(" +s.getFileName()+":"+ s.getLineNumber()+ ")" + "\n");
		}
		return str.toString();
	}
	
	public static String stackTrace(String msg,Throwable throwable){
		StringBuilder str = new StringBuilder(throwable.getMessage()+" : "+msg+"\n");
		str.append(stackTrace(throwable.getStackTrace()));
		return str.toString();
	}
	
	public static void printStackTrace(String msg){
		System.err.println(msg);
	}
}
