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

import com.flower.tables.FlowerTemplate;
import com.flower.tables.GoodsTemplate;

public class GoodsTemplateDao {

	private static final Logger log = Logger.getLogger(FlowersDao.class);

	public static List getFlowerList() {
		Session db = HibernateSessionFactory.getSession();
		String sql = "select id,name flowerName from "+FlowerTemplate.class.getSimpleName()+" ";
		Query query = db.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

	public static void addGoods(GoodsTemplate good) {
		// TODO Auto-generated method stub
		try{
			log.info("开始增加 商品模板"+good); 
			Session session = HibernateSessionFactory.getSession();
	    	Transaction t = session.beginTransaction();
			session.save(good);
			t.commit();
			log.info("增加 商品 结束 "+good);
		}catch(Exception e){
			log.error("增加商品模板出错");
			e.printStackTrace();
		}
	}

	public static FlowerTemplate getFlower(int id) {
		// TODO Auto-generated method stub
		log.info("检索商品模板 "+id);
		Session session = HibernateSessionFactory.getSession();
		return  (FlowerTemplate)session.get(FlowerTemplate.class, id);
	}

	public static List getGoodsTemplateList() {
		// TODO Auto-generated method stub
		Session db = HibernateSessionFactory.getSession();
 		String sql ="select g.*,f.name flowerName from "+GoodsTemplate.class.getSimpleName()+" g left join "+FlowerTemplate.class.getSimpleName()+" f on g.flowerid=f.id ";
		Query query = db.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

	public static Map<String, Object> delGoodsTemplate(String id) {
		// TODO Auto-generated method stub
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			log.info("查看商品模板是否有收藏  "+id);
			Session session = HibernateSessionFactory.getSession();
			Query query =  session.createSQLQuery("select goods_template_id  from Relations_Customer_GoodsTemplate  where goods_template_id =?").setParameter(0,Integer.parseInt(id));
			List list = query.list(); 
			if(list.size() >0){
				result.put("state", 0);
				return result;
			}
			log.info("删除  商品模板 "+id);
	    	Transaction t = session.beginTransaction();
			query =  session.createSQLQuery("delete from "+GoodsTemplate.class.getSimpleName()+" where id = ?")
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
	public static Object getGoodsTemplateById(int id) {
		// TODO Auto-generated method stub
			log.info("检索花品 "+id);
			Session session = HibernateSessionFactory.getSession();
			Query query = session.createSQLQuery("select g.*,f.name flowerName from "+GoodsTemplate.class.getSimpleName()+" g left join "+FlowerTemplate.class.getSimpleName()+" f on g.flowerid=f.id  where g.id =?").setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			query.setParameter(0,id);
			return query.uniqueResult();
	 
	}

	public static void updateGoodsTemplate(GoodsTemplate good) {
		// TODO Auto-generated method stub
			try{
				log.info("开始修改 商品模板 "+good); 
				Session session = HibernateSessionFactory.getSession();
		    	Transaction t = session.beginTransaction();
				session.update(good);
				t.commit();
				log.info("修改 商品 结束 "+good);
			}catch(Exception e){
				log.error("修改商品 出错");
				e.printStackTrace();
			}
	}
	 
}
