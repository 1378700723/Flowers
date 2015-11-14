package com.flower.common;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
public class SnCal {

    public static String sendGet(Map paramsMap,String afterUrl,String sk,String httpUrl) throws Exception {
         
         String paramsStr = toQueryString(paramsMap);

         String wholeStr = new String(afterUrl+"?"+paramsStr +sk);

         String tempStr = URLEncoder.encode(wholeStr, "UTF-8");

         String sn = MD5(tempStr);

         HttpClient client = new DefaultHttpClient();
         HttpGet httpget = new HttpGet(httpUrl+sn);
         HttpResponse response = client.execute(httpget);
         InputStream is = response.getEntity().getContent();
        return inStream2String(is);
     }

    public static  String sendPost(LinkedHashMap<String, String> paramsMap ,String afterUrl,String sk,String httpUrl,List<NameValuePair> params) throws Exception {

        // post请求是按字母序填充，对上面的paramsMap按key的字母序排列
        Map<String, String> treeMap = new TreeMap<String, String>(paramsMap);
        String paramsStr = toQueryString(treeMap);

        String wholeStr = new String(afterUrl+"?"+paramsStr+sk);
        String tempStr = URLEncoder.encode(wholeStr, "UTF-8");
        // 调用下面的MD5方法得到sn签名值
        String sn = MD5(tempStr);
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(
        		httpUrl+afterUrl);
        params.add(new BasicNameValuePair("sn", sn));
       
        HttpEntity formEntity = new UrlEncodedFormEntity(params);
        post.setEntity(formEntity);
        HttpResponse response = client.execute(post);
        InputStream is = response.getEntity().getContent();
        
        return inStream2String(is);
    }

    // 对Map内所有value作utf8编码，拼接返回结果
    public static String toQueryString(Map<?, ?> data)
            throws UnsupportedEncodingException, URIException {
        StringBuffer queryString = new StringBuffer();
        for (Entry<?, ?> pair : data.entrySet()) {
            queryString.append(pair.getKey() + "=");
            queryString.append(URIUtil.encodeQuery((String) pair.getValue(),
                    "UTF-8") + "&");
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
    }
    
    // MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    // 将输入流转换成字符串
    private static String inStream2String(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        return new String(baos.toByteArray(), "UTF-8");
    }
}
