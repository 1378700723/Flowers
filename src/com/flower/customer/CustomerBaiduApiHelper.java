package com.flower.customer;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

 








import javax.servlet.ServletContext;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.flower.common.SnCal;
import com.flower.tables.UserPoi;
/**
 * @author 王雪冬
 * 百度API接口
 */
public class CustomerBaiduApiHelper extends AbstractHttpHelper{
 
	private static final Logger log = Logger.getLogger(CustomerBaiduApiHelper.class);
	private static ServletContext _servletContext;	
	
	public static JSONObject nearby(String poiId,String geotable_id){
		log.info("开始查询附近信息 ");
		JSONObject json = null;
		try{
		        //得到当前人坐标
				JSONObject poi_json = CustomerBaiduApiHelper.selectPoi(poiId,geotable_id);
				JSONObject pois_jsonObject = poi_json.getJSONObject("poi");
				JSONArray location = pois_jsonObject.getJSONArray("location");
 		        String str_location="";
 		        str_location += location.getDouble(0)+","+location.getDouble(1);
			    Map<String,String> map =getProperties("Nearby");
				String ak = map.get("ak").toString();
				String sk = map.get("sk").toString();
				String httpUrl = map.get("httpUrl").toString();
				String afterUrl = map.get("afterUrl").toString();
				Map<String, String> paramsMap = new LinkedHashMap<String, String>();
		        paramsMap.put("ak", ak);
		        paramsMap.put("geotable_id", geotable_id);
		        paramsMap.put("location",str_location);
		        httpUrl += afterUrl+"?ak="+ak+"&geotable_id="+geotable_id+"&location="+str_location+"&sn=";
		        String  result = SnCal.sendGet(paramsMap, afterUrl, sk, httpUrl);
		        json = new JSONObject(result);
		        int status = json.getInt("status");
		        String contents = json.getString("contents");
			    log.info(" status: "+status+" contents: "+contents);
 		  }catch(Exception e){
			  e.printStackTrace();
			  log.error("查询附近信息异常");
		  }
		 return json;
	}
	public static JSONObject selectPoi(String id,String geotable_id){
		  log.info("开始poi数据");
		  JSONObject json =null;
		  try{
			   Map<String,String> map =getProperties("Detail_poi");
				String ak = map.get("ak").toString();
				String sk = map.get("sk").toString();
				String httpUrl = map.get("httpUrl").toString();
				String afterUrl = map.get("afterUrl").toString();
				Map<String, String> paramsMap = new LinkedHashMap<String, String>();
		        paramsMap.put("geotable_id",geotable_id);
		        paramsMap.put("id",id);
		        paramsMap.put("ak", ak);
		        httpUrl += afterUrl+"?geotable_id="+geotable_id+"&id="+id+"&ak="+ak+"&sn=";
		        String  result = SnCal.sendGet(paramsMap, afterUrl, sk, httpUrl);
		      
		        json = new JSONObject(result);
		        int status = json.getInt("status");
		        String message = json.getString("message");
				log.info(" status: "+status+" message: "+message);
				System.out.println("CustomerBaiduApiHelper.selectPoi()"+json);
 		  }catch(Exception e){
			  e.printStackTrace();
			  log.error("查询poi数据结束");
		  }
		  return json;
	}
	 
	public static void createPoi(String latitude,String longitude,String coord_type,String geotable_id,String user_Id){
		log.info("开始创建数据");
	    try {
			Map<String,String> map =getProperties("Create_poi");
			String ak = map.get("ak").toString();
			String sk = map.get("sk").toString();
			String httpUrl = map.get("httpUrl").toString();
			String afterUrl = map.get("afterUrl").toString();
				
			LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
 	        paramsMap.put("latitude",latitude);
	        paramsMap.put("longitude",longitude);
	        paramsMap.put("coord_type",coord_type);
	        paramsMap.put("geotable_id",geotable_id);
	        paramsMap.put("user_Id",user_Id);
 	        paramsMap.put("ak",ak);
	        
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
 	        params.add(new BasicNameValuePair("latitude",latitude));
	        params.add(new BasicNameValuePair("longitude",longitude));
	        params.add(new BasicNameValuePair("coord_type",coord_type));
	        params.add(new BasicNameValuePair("geotable_id",geotable_id));
	        params.add(new BasicNameValuePair("user_Id",user_Id));
 	        params.add(new BasicNameValuePair("ak",ak));

	        String result = SnCal.sendPost(paramsMap, afterUrl, sk, httpUrl, params);
		        JSONObject json = new JSONObject(result);
	        int status = json.getInt("status");
	        String message = json.getString("message");
	        if(status == 0){
	        	UserPoi userPoi = new UserPoi();
	        	int poiId = json.getInt("id");
	        	userPoi.userId = Integer.parseInt(user_Id);
	        	userPoi.poiId  =  poiId ;
	        	Session session = HibernateSessionFactory.getSession();
	   			Transaction t = session.beginTransaction();
	   			session.save(userPoi);
	   			t.commit();
	        }
			log.info(" status: "+status+" message: "+message);
			System.out.println(" json: "+json);
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("创建数据出错");
		} 
	}
	public static void updatePoi(String id,String latitude,String longitude,String coord_type,String geotable_id,String user_Id){
		log.info("开始修改POI数据");
	    try {
			Map<String,String> map =getProperties("Update_poi");
			String ak = map.get("ak").toString();
			String sk = map.get("sk").toString();
			String httpUrl = map.get("httpUrl").toString();
			String afterUrl = map.get("afterUrl").toString();
				
			LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
			paramsMap.put("id",id);
			paramsMap.put("latitude",latitude);
	        paramsMap.put("longitude",longitude);
	        paramsMap.put("coord_type",coord_type);
	        paramsMap.put("geotable_id",geotable_id);
	        paramsMap.put("user_Id",user_Id);
 	        paramsMap.put("ak",ak);
	        
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
			paramsMap.put("id",id);
	        params.add(new BasicNameValuePair("latitude",latitude));
	        params.add(new BasicNameValuePair("longitude",longitude));
	        params.add(new BasicNameValuePair("coord_type",coord_type));
	        params.add(new BasicNameValuePair("geotable_id",geotable_id));
	        params.add(new BasicNameValuePair("user_Id",user_Id));
 	        params.add(new BasicNameValuePair("ak",ak));

	        String result = SnCal.sendPost(paramsMap, afterUrl, sk, httpUrl, params);
		    JSONObject json = new JSONObject(result);
	        int status = json.getInt("status");
	        String message = json.getString("message");
			log.info(" status: "+status+" message: "+message);
			System.out.println(" json: "+json);
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("更新表出错");
		} 
	}
	public static void selectTable(String tableName){
		  log.info("开始查询表信息");
		  try{
			   Map<String,String> map =getProperties("List_geotable");
				String ak = map.get("ak").toString();
				String sk = map.get("sk").toString();
				String httpUrl = map.get("httpUrl").toString();
				String afterUrl = map.get("afterUrl").toString();
				Map<String, String> paramsMap = new LinkedHashMap<String, String>();
		        paramsMap.put("name", tableName);
		        paramsMap.put("ak", ak);
		        httpUrl += afterUrl+"?name="+tableName+"&ak="+ak+"&sn=";
		        String  result = SnCal.sendGet(paramsMap, afterUrl, sk, httpUrl);
 		        JSONObject json = new JSONObject(result);
		        int status = json.getInt("status");
		        String message = json.getString("message");
 				log.info(" status: "+status+" message: "+message);
 				System.out.println(" json: "+json);
		  }catch(Exception e){
			  e.printStackTrace();
			  log.error("查询表信息异常");
		  }
	}
	public static void selectColumn(String geotable_id){
		  log.info("开始查询表信息");
		  try{
			   Map<String,String> map =getProperties("List_column");
				String ak = map.get("ak").toString();
				String sk = map.get("sk").toString();
				String httpUrl = map.get("httpUrl").toString();
				String afterUrl = map.get("afterUrl").toString();
				Map<String, String> paramsMap = new LinkedHashMap<String, String>();
		        paramsMap.put("geotable_id",geotable_id);
		        paramsMap.put("ak", ak);
		        httpUrl += afterUrl+"?geotable_id="+geotable_id+"&ak="+ak+"&sn=";
		        String  result = SnCal.sendGet(paramsMap, afterUrl, sk, httpUrl);
		        JSONObject json = new JSONObject(result);
		        int status = json.getInt("status");
		        String message = json.getString("message");
				log.info(" status: "+status+" message: "+message);
				System.out.println(" json: "+json);
		  }catch(Exception e){
			  e.printStackTrace();
			  log.error("查询表信息异常");
		  }
	}
	public static void updateColumn(){
		log.info("开始创建列");
	    try {
			//Map<String,String> map =getProperties("Update_column");
	    	Map<String,String> map =getProperties("Delete_coulum");
			String ak = map.get("ak").toString();
			String sk = map.get("sk").toString();
			String httpUrl = map.get("httpUrl").toString();
			String afterUrl = map.get("afterUrl").toString();
				
			LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
			paramsMap.put("id", "153186");
			//paramsMap.put("name", "userId");
	        //paramsMap.put("key", "user_Id");
	        //paramsMap.put("type", "3");
	        //paramsMap.put("max_length", "20");
	        //paramsMap.put("is_sortfilter_field", "0");
	        //paramsMap.put("is_search_field", "0");
	        //paramsMap.put("is_index_field", "1");
	        //paramsMap.put("is_unique_field", "1");
	        paramsMap.put("geotable_id", "113554");
	        paramsMap.put("ak",ak);

	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("id", "153186"));
	       //params.add(new BasicNameValuePair("name", "userId"));
	       //params.add(new BasicNameValuePair("key", "user_Id"));
	       //params.add(new BasicNameValuePair("type", "3"));
	       //params.add(new BasicNameValuePair("max_length", "20"));
	       //params.add(new BasicNameValuePair("is_sortfilter_field", "0"));
	       //params.add(new BasicNameValuePair("is_search_field", "0"));
	       //params.add(new BasicNameValuePair("is_index_field", "1"));
	       //params.add(new BasicNameValuePair("is_unique_field", "1"));
	        params.add(new BasicNameValuePair("geotable_id", "113554"));
	        params.add(new BasicNameValuePair("ak", ak));

	        String result = SnCal.sendPost(paramsMap, afterUrl, sk, httpUrl, params);
		    JSONObject json = new JSONObject(result);
	        int status = json.getInt("status");
	        String message = json.getString("message");
			log.info(" status: "+status+" message: "+message);
			System.out.println(" json: "+json);
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("创建表出错");
		} 
	}
	public static void createColumn(String name,String key,String type,String max_length,String is_sortfilter_field,String is_search_field,String is_index_field,String is_unique_field,String geotable_id){
		log.info("开始创建列");
	    try {
			Map<String,String> map =getProperties("Create_column");
			String ak = map.get("ak").toString();
			String sk = map.get("sk").toString();
			String httpUrl = map.get("httpUrl").toString();
			String afterUrl = map.get("afterUrl").toString();
				
			LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
	        paramsMap.put("name",name);
	        paramsMap.put("key", key);
	        paramsMap.put("type",type);
	        paramsMap.put("max_length",max_length);
 	        paramsMap.put("is_sortfilter_field", is_sortfilter_field);
	        paramsMap.put("is_search_field", is_search_field);
	        paramsMap.put("is_index_field", is_index_field);
	        paramsMap.put("is_unique_field", is_unique_field);
	        paramsMap.put("geotable_id", geotable_id);
	        paramsMap.put("ak",ak);

	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair("name",name));
	        params.add(new BasicNameValuePair("key", key));
	        params.add(new BasicNameValuePair("type", type));
	        params.add(new BasicNameValuePair("max_length",max_length));
 	        params.add(new BasicNameValuePair("is_sortfilter_field", is_sortfilter_field));
	        params.add(new BasicNameValuePair("is_search_field",is_search_field));
	        params.add(new BasicNameValuePair("is_index_field", is_index_field));
	        params.add(new BasicNameValuePair("is_unique_field", is_unique_field));
	        params.add(new BasicNameValuePair("geotable_id",geotable_id));
	        params.add(new BasicNameValuePair("ak", ak));

	        String result = SnCal.sendPost(paramsMap, afterUrl, sk, httpUrl, params);
		    JSONObject json = new JSONObject(result);
	        int status = json.getInt("status");
	        String message = json.getString("message");
			log.info(" status: "+status+" message: "+message);
			System.out.println(" json: "+json);
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("创建表出错");
		} 
	}
	
	public static  void  createTable(String tableName,String geotype,String is_published) {
		log.info("开始创建表");
		    try {
				Map<String,String> map =getProperties("Create_geotable");
				String ak = map.get("ak").toString();
				String sk = map.get("sk").toString();
				String httpUrl = map.get("httpUrl").toString();
				String afterUrl = map.get("afterUrl").toString();
 				
				LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
		        paramsMap.put("geotype",geotype);
		        paramsMap.put("ak",ak);
		        paramsMap.put("name",tableName);
		        paramsMap.put("is_published", is_published);
		        
		        List<NameValuePair> params = new ArrayList<NameValuePair>();
		        params.add(new BasicNameValuePair("geotype", geotype));
		        params.add(new BasicNameValuePair("ak", ak));
		        params.add(new BasicNameValuePair("name", tableName));
		        params.add(new BasicNameValuePair("is_published", is_published));
		        
		        String result = SnCal.sendPost(paramsMap, afterUrl, sk, httpUrl, params);
 		        JSONObject json = new JSONObject(result);
		        int status = json.getInt("status");
		        String message = json.getString("message");
				log.info(" status: "+status+" message: "+message);
 				System.out.println(" json: "+json);
 			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("创建表出错");
			} 
	}
	
	public static Map<String,String > getProperties(String key){
		Map<String,String> map =new HashMap();
		try {
		File file = new File(_servletContext.getRealPath("/WEB-INF/config/lbs.properties"));
    	FileInputStream fileInputStream = new FileInputStream(file);
		Properties prop = new Properties();
		prop.load(fileInputStream);
		String ak = prop.getProperty("AK").trim();
		String sk = prop.getProperty("SK").trim();
		String httpUrl = prop.getProperty("httpUrl").trim();
		String afterUrl = prop.getProperty(key).trim();
		map.put("ak", ak);
		map.put("sk", sk);
		map.put("httpUrl", httpUrl);
		map.put("afterUrl", afterUrl);
		}catch(Exception e){
		   e.printStackTrace();
		}
		return map;
	}
	
	public static void main(String[] args) {
	   String geotable_id ="113676";
	   //CustomerBaiduApiHelper.createTable("表1","1","1");
	   //CustomerBaiduApiHelper.selectTable("huahuahui");
	   //CustomerBaiduApiHelper.createColumn("userId","user_Id","1","10","1","0","1","1","113676");
	   //CustomerBaiduApiHelper.selectColumn(geotable_id);
	   //CustomerBaiduApiHelper.updateColumn();
	   //CustomerBaiduApiHelper.createPoi("40.0456176","116.3246","1",geotable_id,"10");
	   //CustomerBaiduApiHelper.selectPoi("1054190750",geotable_id);
 	   CustomerBaiduApiHelper.nearby("1054190750",geotable_id);
	}
}