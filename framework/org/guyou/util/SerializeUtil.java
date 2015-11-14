/**
 * created by zhusj
 */
package org.guyou.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
//import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
//import java.util.Map.Entry;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
//import com.thoughtworks.xstream.XStream;
//import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
//import com.thoughtworks.xstream.io.xml.DomDriver;
//import com.thoughtworks.xstream.mapper.Mapper;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 序列化工具
 * @author zhusj
 */
public class SerializeUtil {

	private static final Logger log = Logger.getLogger(SerializeUtil.class);
	
	private final static byte[] hex = "0123456789ABCDEF".getBytes();
	
	// 整数到字节数组转换
	public static byte[] intToByte(int n) {
		byte[] ab = new byte[4];
		ab[0] = (byte) (0xff & n);
		ab[1] = (byte) ((0xff00 & n) >> 8);
		ab[2] = (byte) ((0xff0000 & n) >> 16);
		ab[3] = (byte) ((0xff000000 & n) >> 24);
		return ab;
	}

	// 字节数组到整数的转换
	public static int byteToInt(byte b[]) {
		int s = 0;
		s = ((((b[0] & 0xff) << 8 | (b[1] & 0xff)) << 8) | (b[2] & 0xff)) << 8
				| (b[3] & 0xff);
		return s;
	}

	// 字节转换到字符
	public static char byteToChar(byte b) {
		return (char) b;
	}

	private static int parse(char c) {
		if (c >= 'a')
			return (c - 'a' + 10) & 0x0f;
		if (c >= 'A')
			return (c - 'A' + 10) & 0x0f;
		return (c - '0') & 0x0f;
	}

	// 从字节数组到十六进制字符串转换
	public static String byteToHexString(byte[] b) {
		byte[] buff = new byte[2 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
			buff[2 * i + 1] = hex[b[i] & 0x0f];
		}
		return new String(buff);
	}

	// 从十六进制字符串到字节数组转换
	public static byte[] HexStringToByte(String hexstr) {
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			b[i] = (byte) ((parse(c0) << 4) | parse(c1));
		}
		return b;
	}

	/**
	 * 将byte[]转换成string
	 * 
	 * @param butBuffer
	 */
	public static String byteToString(byte[] b) {
		StringBuilder stringBuffer = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			stringBuffer.append((char) b[i]);
		}
		return stringBuffer.toString();
	}
	
	public static String byteToUTFString(byte[] b) {
		ByteArrayInputStream bais = null;
		DataInputStream dis = null;
		String result = null;
		try {
			bais = new ByteArrayInputStream(b);
			dis = new DataInputStream(bais);
			result = dis.readUTF();
		} catch (Exception e) {
			log.error("byteToUTFString",e);
			result ="";
		} finally {
			try {
				if (dis != null) {
					dis.reset();
					dis.close();
					dis = null;
				}
				if (bais != null) {
					bais.reset();
					bais.close();
					bais = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 获取修改过的值
	 * 
	 * @param backValue
	 *            二级缓存值
	 * @param curValue
	 *            一级缓存值
	 * @return
	 */
	public static HashMap getModifyedValue(Map backValue, Map curValue) {
		HashMap result = new HashMap();
		if(backValue==null || backValue.size()==0){
			result.putAll(curValue);
			return result;
		}
		for (Object key : backValue.keySet()) {
			Object old = backValue.get(key);
			Object cur = curValue.get(key);
			if (old == null) {
				if (cur != null) {
					result.put(key, cur);
				}
			} else if(cur!=null){
				Class<?> oldC = old.getClass();
				Class<?> curC = cur.getClass();
				if(oldC==curC && oldC.isArray() && curC.isArray()){
					if(old instanceof String[]){
						String[] oldStr = (String[])old;
						String[] curStr = (String[])cur;
						if(oldStr.length!=curStr.length){
							result.put(key,cur);
						}else{
							for(int i=0;i<oldStr.length;i++){
								if(!oldStr[i].equals(curStr[i])){
									result.put(key,cur);
									break;
								}
							}
						}
					}else if(old instanceof byte[]){
						byte[] oldInt = (byte[])old;
						byte[] curInt = (byte[])cur;
						if(oldInt.length!=curInt.length){
							result.put(key,cur);
						}else{
							for(int i=0;i<oldInt.length;i++){
								if(oldInt[i]!=curInt[i]){
									result.put(key,cur);
									break;
								}
							}
						}
					}else if(old instanceof short[]){
						short[] oldInt = (short[])old;
						short[] curInt = (short[])cur;
						if(oldInt.length!=curInt.length){
							result.put(key,cur);
						}else{
							for(int i=0;i<oldInt.length;i++){
								if(oldInt[i]!=curInt[i]){
									result.put(key,cur);
									break;
								}
							}
						}
					}else if(old instanceof int[]){
						int[] oldInt = (int[])old;
						int[] curInt = (int[])cur;
						if(oldInt.length!=curInt.length){
							result.put(key,cur);
						}else{
							for(int i=0;i<oldInt.length;i++){
								if(oldInt[i]!=curInt[i]){
									result.put(key,cur);
									break;
								}
							}
						}
					}else if(old instanceof long[]){
						long[] oldInt = (long[])old;
						long[] curInt = (long[])cur;
						if(oldInt.length!=curInt.length){
							result.put(key,cur);
						}else{
							for(int i=0;i<oldInt.length;i++){
								if(oldInt[i]!=curInt[i]){
									result.put(key,cur);
									break;
								}
							}
						}
					}else if(old instanceof float[]){
						float[] oldInt = (float[])old;
						float[] curInt = (float[])cur;
						if(oldInt.length!=curInt.length){
							result.put(key,cur);
						}else{
							for(int i=0;i<oldInt.length;i++){
								if(oldInt[i]!=curInt[i]){
									result.put(key,cur);
									break;
								}
							}
						}
					}else if(old instanceof double[]){
						double[] oldInt = (double[])old;
						double[] curInt = (double[])cur;
						if(oldInt.length!=curInt.length){
							result.put(key,cur);
						}else{
							for(int i=0;i<oldInt.length;i++){
								if(oldInt[i]!=curInt[i]){
									result.put(key,cur);
									break;
								}
							}
						}
					}else if(old instanceof boolean[]){
						boolean[] oldInt = (boolean[])old;
						boolean[] curInt = (boolean[])cur;
						if(oldInt.length!=curInt.length){
							result.put(key,cur);
						}else{
							for(int i=0;i<oldInt.length;i++){
								if(oldInt[i]!=curInt[i]){
									result.put(key,cur);
									break;
								}
							}
						}
					}else if(old instanceof HashMap[]){
						HashMap[] oldHashMap = (HashMap[])old;
						HashMap[] curHashMap = (HashMap[])cur;
						if(oldHashMap.length!=curHashMap.length){
							result.put(key,cur);
						}else{
							for(int i=0;i<oldHashMap.length;i++){
								if(!oldHashMap[i].toString().equals(curHashMap[i].toString())){
									result.put(key,cur);
									break;
								}
							}
						}
					}
				}else{
					if(!old.equals(cur)){
						result.put(key,cur);
					}
				}
			}
		}
		return result;
	}

	public static byte[] byteEncrypt(byte[] buf) {
		int bufLen = buf.length;
		int i = 0;
		for (i = 0; i < bufLen / 2; i++) {
			byte b = buf[i];
			buf[i] = buf[bufLen - i - 1];
			buf[bufLen - i - 1] = b;
		}
		int yu = bufLen % 2;
		int subLen = bufLen / 2;
		int bigp = 0;
		if (yu == 0) {
			bigp = subLen;
		} else {
			bigp = subLen + 1;
		}
		for (i = 0; i < subLen / 2; i++) {
			byte b = buf[i];
			buf[i] = buf[subLen - i - 1];
			buf[subLen - i - 1] = b;
		}
		for (i = bigp; i < bigp + subLen / 2; i++) {
			byte b = buf[i];
			buf[i] = buf[bufLen - (i - bigp) - 1];
			buf[bufLen - (i - bigp) - 1] = b;
		}
		for (i = 0; i < bufLen; i++) {
			buf[i] = (byte) (buf[i] + i % 10);
		}
		return buf;
	}

	public static byte[] byteDecrypt(byte[] encryptBuf) {
		int bufLen = encryptBuf.length;
		int i = 0;
		int yu = bufLen % 2;
		int subLen = bufLen / 2;
		int bigp = 0;
		for (i = 0; i < bufLen; i++) {
			encryptBuf[i] = (byte) (encryptBuf[i] - i % 10);
		}
		if (yu == 0) {
			bigp = subLen;
		} else {
			bigp = subLen + 1;
		}
		for (i = 0; i < subLen / 2; i++) {
			byte b = encryptBuf[i];
			encryptBuf[i] = encryptBuf[subLen - i - 1];
			encryptBuf[subLen - i - 1] = b;
		}
		for (i = bigp; i < bigp + subLen / 2; i++) {
			byte b = encryptBuf[i];
			encryptBuf[i] = encryptBuf[bufLen - (i - bigp) - 1];
			encryptBuf[bufLen - (i - bigp) - 1] = b;
		}
		for (i = 0; i < bufLen / 2; i++) {
			byte b = encryptBuf[i];
			encryptBuf[i] = encryptBuf[bufLen - i - 1];
			encryptBuf[bufLen - i - 1] = b;
		}
		return encryptBuf;
	}

	/**
	 * 对象转字节数组
	 * @param obj
	 * @param isMyselfSerializable 是否是自定义编码
	 * @return
	 */
	public static byte[] objectToByte(Object obj) {
		return objectToByte_ObjectOutputStream(obj);
	}
	
	/**
	 * 对象转字节数组
	 * @param obj
	 * @param isMyselfSerializable 是否是自定义编码
	 * @param isBytesCompress 是否压缩字节数组
	 * @return
	 */
	public static byte[] objectToByte(Object obj,boolean isBytesCompress){
		byte[] bytes = objectToByte(obj);
		return isBytesCompress ? byteCompress(bytes) : bytes;
	}
	
//	/**
//	 * 对象转xml
//	 * @param obj
//	 * @return
//	 */
//	public static String objectToXml(Object obj){
//		return xsList[0].toXML(obj);
//	}
//	
//	public static Object xmlToObject(String xml){
//		return xsList[0].fromXML(xml);
//	}
	
	static byte[] objectToByte_ObjectOutputStream(Object obj) {
		ByteArrayOutputStream bo=null;
		ObjectOutputStream oo = null;
		byte[] bytes=null;
		try {
			bo = new ByteArrayOutputStream();
			oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			oo.flush();
			bo.flush();
			bytes = bo.toByteArray();
		} catch (IOException ex) {
			log.error("对象转字节数字错误!!",ex);
			bytes=null;
		} finally {
			try {
				if(bo!=null){
					bo.reset();
					bo.close();
					bo=null;
				}
				if(oo!=null){
					oo.reset();
					oo.close();
					oo=null;
				}
			} catch (IOException ex1) {
				ex1.printStackTrace();
			}
		}
		return bytes;
	}
	
	private static final SerializerFeature[] JSON_SERIALIZERFEATURES_RUN = new SerializerFeature[]{
		SerializerFeature.WriteNullListAsEmpty,
		SerializerFeature.WriteNullStringAsEmpty,
		SerializerFeature.WriteNullNumberAsZero,
		SerializerFeature.WriteNullBooleanAsFalse,
		SerializerFeature.WriteMapNullValue,
		SerializerFeature.DisableCircularReferenceDetect
	};
	private static final SerializerFeature[] JSON_SERIALIZERFEATURES_DEBUG = ArrayUtils.add(JSON_SERIALIZERFEATURES_RUN, SerializerFeature.PrettyFormat);
	public static String objectToJsonString(Object obj){
		if(obj==null) return "";
		try {
			return JSON.toJSONString(obj,SystemUtil.isDebug()?JSON_SERIALIZERFEATURES_DEBUG:JSON_SERIALIZERFEATURES_RUN);
		} catch ( JSONException e ) {
			log.error("Json转化异常  "+obj.getClass(),e);
			throw e;
		}
	}
	
	public static String arrayToJsonString(Object obj){
		if(obj==null) return "";
		try {
			return JSON.toJSONString(obj);
		} catch ( JSONException e ) {
			log.error("Json转化异常  "+obj.getClass(),e);
			throw e;
		}
	}
	
	public static <T> T jsonStringToObject(String text,Class<T> cls){
		if(text==null || text.equals("")) return null;
		try {
			return JSON.parseObject(text, cls);
		} catch ( JSONException e ) {
			log.error("Json转化异常  "+text,e);
			throw e;
		}
	}
	
	public static Object jsonStringToObject(String text){
		if(text==null || text.equals("")) return null;
		try {
			return JSON.parse(text);
		} catch ( JSONException e ) {
			log.error("Json转化异常  "+text,e);
			throw e;
		}
	}
	
	public static <T> List<T> jsonStringToArray(String text,Class<T> cls){
		if(text==null || text.equals("")) return null;
		try {
			return JSON.parseArray(text, cls);
		} catch ( JSONException e ) {
			log.error("Json转化异常  "+text,e);
			throw e;
		}
	}
	
	/**
	 * Compress
	 * @param bytes
	 * @return
	 */
	public static byte[] byteCompress(byte[] bytes) {
		if(bytes==null || bytes.length==0){
			return null;
		}
		Deflater compressor = new Deflater();
		compressor.reset(); 
		compressor.setLevel(Deflater.DEFAULT_COMPRESSION);
		compressor.setInput(bytes);
		compressor.finish();
		ByteArrayOutputStream zbos = new ByteArrayOutputStream(bytes.length);
		byte[] b = new byte[1024];
		while (!compressor.finished()) {
			int count = compressor.deflate(b);
			zbos.write(b, 0, count);
		}
		byte[] result=null;
		try {
			result=zbos.toByteArray();
			compressor.end();  
			compressor=null;
			if(zbos!=null){
				zbos.close();
				zbos.reset();
				zbos=null;
			}
		} catch (IOException e) {
			log.error("byteCompress",e);
		}
		return result;
	}

	public static byte[] byteUncompress(byte[] bytes) {
		if(bytes==null || bytes.length==0){
			return null;
		}
		byte[] output = new byte[0];  
		  
        Inflater decompresser = new Inflater();  
        decompresser.reset();  
        decompresser.setInput(bytes);  
  
        ByteArrayOutputStream o = new ByteArrayOutputStream(bytes.length);  
        try {  
            byte[] buf = new byte[1024];  
            while (!decompresser.finished()) {  
                int i = decompresser.inflate(buf); 
                if(i==0){
                	log.error("解压数据错误！！");
                	break;
                }
                o.write(buf, 0, i);  
            }  
            output = o.toByteArray();  
        } catch (Exception e) {  
            output = bytes;  
            log.error("byteUncompress",e);
        } finally {  
            try {  
                o.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        decompresser.end();  
		return output;
	}
	
	
	/**
	 * 字节数组转对象
	 * @param bytes
	 * @param isMyselfSerializable 是否是自定义解码
	 * @return
	 */
	public static Object byteToObject(byte[] bytes){
		if(bytes==null || bytes.length==0){
			return null;
		}
		return byteToObject_ObjectInputStream(bytes);
	}
	
	/**
	 * 字节数组转对象
	 * @param bytes
	 * @param isMyselfSerializable 是否是自定义解码
	 * @param isBytesUncompress 是否解压字节数组
	 * @return
	 */
	public static Object byteToObject(byte[] bytes,boolean isBytesUncompress){
		return byteToObject(isBytesUncompress?byteUncompress(bytes):bytes);
	}
	
	static Object byteToObject_ObjectInputStream(byte[] bytes){
		Object obj = null;
		ByteArrayInputStream bi=null;
		ObjectInputStream oi = null;
		try {
			bi = new ByteArrayInputStream(bytes);
			oi = new ObjectInputStream(bi);
			obj = oi.readObject();
		} catch (ClassNotFoundException ex) {
			log.error("byteToObject错误!!",ex);
		} catch (IOException ex1) {
			log.error("byteToObject错误!!",ex1);
		} finally {
			try {
				if(bi!=null){
					bi.reset();
					bi.close();
					bi=null;
				}
				if(oi!=null){
					oi.close();
					oi=null;
				}
			} catch (IOException e) {
				log.error("关闭流异常!!",e);
			}
		}
		return obj;
	}
	
	//图片转化成base64字符串
    public static String bytesToBase64String(byte[] bytes) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes);//返回Base64编码过的字节数组字符串
    }
    
    //base64字符串转化成图片
    public static byte[] base64StringToBytes(String base64String) throws IOException {   //对字节数组字符串进行Base64解码并生成图片
    	BASE64Decoder decoder = new BASE64Decoder();
    	byte[] b = decoder.decodeBuffer(base64String);
	    for(int i=0;i<b.length;++i){
	        if(b[i]<0){//调整异常数据
	            b[i]+=256;
	        }
	    }
	    return b;
    }
}
