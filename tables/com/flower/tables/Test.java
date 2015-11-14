package com.flower.tables;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.guyou.util.FileUtil;
import org.guyou.util.SerializeUtil;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.WebStartListening;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import com.flower.tables.FriendRelation.FriendRelationId;
import com.flower.util.UUIDUtil;

//@WebStartListening
public class Test {

	public static void init(){
//		try {
//			Session session = HibernateSessionFactory.getSession();
//			Transaction t = session.beginTransaction();
//			byte[] bytes = FileUtil.readFile(new File("D:/朱施健.JPG"));
//			Images img = new Images();
//			img.name = UUIDUtil.imageName()+".jpg";
//			img.datas = SerializeUtil.byteCompress(bytes);
//			session.save(img);
//			t.commit();
//			session.close();
//			
//			session = HibernateSessionFactory.getSession();
//			
//			Images img2 = (Images) session.get(Images.class, img.name);
//			System.out.println();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	} 
	
	public static void main(String[] args) {
		byte[] bytes = FileUtil.readFile(new File("D:/花卉/0625_花卉卉UI设计/2-0-注册.png"));
		bytes = FileUtil.readFile(new File("D:/花卉/0628_切图_IOS/切图ios/6-2-聊天详情/icon_face@3x.png"));
		System.out.println(bytes.length);
		byte[] bytes2 = SerializeUtil.byteCompress(bytes);
		System.out.println(bytes2.length);
		Configuration cfg = new Configuration().configure(); 
	    SchemaExport export = new SchemaExport(cfg); 
	    export.create(true, true); 
		try {
			URL localURL = new URL("http://127.0.0.1:8080/flowers/index.jsp");
			HttpURLConnection httpURLConnection = (HttpURLConnection)localURL.openConnection();
	        //httpURLConnection.setRequestProperty("user-agent", "mobile_app");
			String zhusj = httpURLConnection.getHeaderField("zhusj");
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
	            String tempLine;
	            while ((tempLine = reader.readLine()) != null) {
	            	System.out.println(tempLine);
	            }
	            
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
