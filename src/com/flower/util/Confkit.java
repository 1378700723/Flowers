package com.flower.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Confkit {

	private static Properties props = new Properties();

	public static void loadProps(String url){
		try {
			File file = new File(url);
	    	FileInputStream fileInputStream = new FileInputStream(file);
	    	props.load(fileInputStream);
	    	//play框架下要用这种方式加载
			//props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("/wx.properties"));
			//props.load(ConfKit.class.getResourceAsStream("/wechat.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String get(String key) {
		return props.getProperty(key);
	}

    public static void setProps(Properties p){
        props = p;
    }
}
