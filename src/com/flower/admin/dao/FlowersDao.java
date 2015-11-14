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

import com.flower.admin.FlowersHelper;
import com.flower.admin.beans.PagerInfo;
import com.flower.enums.ResultState;
import com.flower.tables.FlowerTemplate;
import com.flower.tables.GoodsTemplate;
/**
 * @author 王雪冬
 *
 */
public class FlowersDao {
	private static final Logger log = Logger.getLogger(FlowersDao.class);
	
	public static PagerInfo LoadAllFlowersBySearch(int page) {
 		log.info("开始查询花品列表  "+page);
		PagerInfo pi = new PagerInfo();
		Session db =  HibernateSessionFactory.getSession();
 		String sql = "select count(*) cnt from "+FlowerTemplate.class.getSimpleName()+" where 1=1";
 		Query query = db.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
 		Map<String, Object> info = (Map<String, Object>)query.list().get(0);
		int totalResult = Integer.parseInt(info.get("cnt").toString());
		pi.CaclePager(totalResult, PagerInfo.pageSize, page);
 		sql = "select * from "+FlowerTemplate.class.getSimpleName()+" where 1=1";
 		query = db.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
 		query.setFirstResult((pi.getCurrentPage()-1) * PagerInfo.pageSize);
		query.setMaxResults(PagerInfo.pageSize);
		pi.setDataList(query.list());
		log.info("查询花品列表结束 "+pi);
		return pi;
 	}
	
	public static void addFlowers(FlowerTemplate flower){
		try{
			log.info("开始增加 花品 "+flower); 
			Session session = HibernateSessionFactory.getSession();
	    	Transaction t = session.beginTransaction();
			session.save(flower);
			t.commit();
			log.info("增加 花品 结束 "+flower);
		}catch(Exception e){
			log.error("增加花出错");
			e.printStackTrace();
		}
	}

	public static Map<String,Object> delFlower(String id) {
		Map<String,Object> result = new HashMap<String,Object>();
 		try{
			Session session = HibernateSessionFactory.getSession();
			log.info("查询花品是否绑定了商品模板");
			Query query =  session.createSQLQuery("select id from "+GoodsTemplate.class.getSimpleName()+" g  where g.flowerid =?").setParameter(0, Integer.parseInt(id));
             List goodList = query.list();
             if(goodList.size() > 0){
            	 result.put("state", 0);
            	 return result;
             }
			log.info("删除  花品 "+id);
	    	Transaction t = session.beginTransaction();
		    query =  session.createSQLQuery("delete from "+FlowerTemplate.class.getSimpleName()+" where id = ?")
			 .setParameter(0,Integer.parseInt(id));
			query.executeUpdate();
			t.commit();
			log.info("删除   结束 ");
			result.put("state",1);
		}catch(Exception e){
			log.error("出错");
			e.printStackTrace();
			result.put("state", 0);
		}
		return result;
	}

	public static Object getFlower(int id) {
 		log.info("检索花品 "+id);
		Session session = HibernateSessionFactory.getSession();
		Query query = session.createSQLQuery("select * from "+FlowerTemplate.class.getSimpleName()+" where id =?").setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		query.setParameter(0,id);
		return query.uniqueResult();
	}

	public static void updateFlowers(FlowerTemplate flower) {
 		try{
			log.info("开始修改 花品 "+flower); 
			Session session = HibernateSessionFactory.getSession();
	    	Transaction t = session.beginTransaction();
			session.update(flower);
			t.commit();
			log.info("修改 花品 结束 "+flower);
		}catch(Exception e){
			log.error("修改花出错");
			e.printStackTrace();
		}
	}
}
