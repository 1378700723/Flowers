package com.flower.admin.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.guyou.web.server.HibernateSessionFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

import com.flower.admin.AdminLoginHelper;
import com.flower.tables.Activity;
import com.flower.tables.FlowerTemplate;
import com.flower.tables.GoodsTemplate;

public class ActivityDao {
	private static final Logger log = Logger.getLogger(ActivityDao.class);
	
	public static void update(Activity activity) {
		try{
			log.info("开始修改活动 "+activity); 
			Session session = HibernateSessionFactory.getSession();
	    	Transaction t = session.beginTransaction();
			session.update(activity);
			t.commit();
			log.info("修改 活动 结束 "+activity);
		}catch(Exception e){
			log.error("修改活动出错");
			e.printStackTrace();
		}
	}

	public static void save(Activity activity) {
		try{
			log.info("开始增加 活动 "+activity); 
			Session session = HibernateSessionFactory.getSession();
	    	Transaction t = session.beginTransaction();
			session.save(activity);
			t.commit();
			log.info("增加 活动 结束 "+activity);
		}catch(Exception e){
			log.error("增加活动出错");
			e.printStackTrace();
		}
	}

	public static Object getActivity(int id) {
		log.info("检索活动 "+id);
		Session session = HibernateSessionFactory.getSession();
		Query query = session.createSQLQuery("select * from "+Activity.class.getSimpleName()+" where id =?").setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setParameter(0,id);
		return query.uniqueResult();
	}

	public static List getActivityList() {
		Session db = HibernateSessionFactory.getSession();
 		String sql ="select * from "+Activity.class.getSimpleName()+" where isDelete=0";
		Query query = db.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

	public static Map<String, Object> delActivity(String id) {
		log.info("活动 放到回收站"+id);
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			Session session = HibernateSessionFactory.getSession();
			Transaction t = session.beginTransaction();
			Query query =  session.createSQLQuery("update "+Activity.class.getSimpleName()+" set isDelete =1 where id="+id+"");
				  query.executeUpdate();
				  t.commit();
		   log.info("活动 放到回收站 结束");
		   result.put("state",1);
		}catch(Exception e){
			e.printStackTrace();
			 result.put("state",0);
		}
		
		return result;
	}

}
