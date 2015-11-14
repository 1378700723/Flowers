package org.guyou.util;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;


public class MD5 {

	private static Logger log = Logger.getLogger(MD5.class);
	
	private static final char[] hexChar = { '0', '1', '2', '3', '4', '5', '6','7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static MessageDigest md5 = null;

	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			log.error("MD5初始化异常!!",e);
		}
	}

	public static String getStringHash(String source) {
		String hash = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(source.getBytes());
			hash = getStreamHash(in);
			in.close();
		} catch (Exception e) {
			log.error("getStringHash",e);
		}
		return hash;
	}
	
	public static String getStringHash_16(String source) {
		return getStringHash(source).substring(8, 24);
	}

	public static String getFileHash(String file) {
		File f = new File(file);
		String result = "";
		if(f.exists()){
			result = getFileHash(f);
		}
		else{
			try {
				Thread.currentThread().getContextClassLoader();
				InputStream in = ClassLoader.getSystemResourceAsStream(file);
				if(in!=null){
					result = getStreamHash(in);
					in.close();
				}
			} catch (IOException e) {
				log.error("getFileHash",e);
			}
		}
		return result;
	}
	
	public static String getFileHash_16(String file) {
		return getFileHash(file).substring(8, 24); 
	}
	
	public static String getFileHash(File file) {
		String hash = null;
		try {
			FileInputStream in = new FileInputStream(file);
			hash = getStreamHash(in);
			in.close();
		} catch (Exception e) {
			log.error("getFileHash",e);
		}
		return hash;
	}
	
	public static String getFileHash_16(File file) {
		return getFileHash(file).substring(8, 24); 
	}

	public static String getStreamHash(InputStream stream) {
		String hash = null;
		byte[] buffer = new byte[1024];
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(stream);
			int numRead = 0;
			while ((numRead = in.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			in.close();
			hash = toHexString(md5.digest());
		} catch (Exception e) {
			log.error("getStreamHash",e);
			if (in != null){
				try {
					in.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return hash;
	}
	
	public static String getBytesHash(byte[] bytes){
		return getStreamHash(new ByteArrayInputStream(bytes));
	}
	
	public static String getStreamHash_16(InputStream stream) {
		return getStreamHash(stream).substring(8, 24); 
	}

	private static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}
	
	public static void main(String[] params) {
		String aaa = "D:\\eclipse3.7\\workspace\\framework\\src\\org\\guyou\\util\\MD5.java";
		System.out.println(getBytesHash(new byte[]{1,1,2,2,3,3,4,4}));
	}
}
