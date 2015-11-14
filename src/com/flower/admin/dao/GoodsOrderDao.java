package com.flower.admin.dao;

import java.util.List;

import org.guyou.web.server.HibernateSessionFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import com.flower.tables.Customer;
import com.flower.tables.FlowerTemplate;
import com.flower.tables.GoodsOrder;
import com.flower.tables.GoodsTemplate;

public class GoodsOrderDao {

	public static List getGoodsOrderList() {
		// TODO Auto-generated method stub
		Session db = HibernateSessionFactory.getSession();
 		String sql ="select g.*,c.phone,c.nickName,(select gtm.name from "+GoodsTemplate.class.getSimpleName()+" gtm where id  in (g.goodsIds)) goodName  from "+GoodsOrder.class.getSimpleName()+" g left join  "+Customer.class.getSimpleName()+" c on g.uid =c.uid";
		Query query = db.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.list();
	}

}
