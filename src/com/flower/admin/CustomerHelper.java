package com.flower.admin;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import com.flower.tables.Customer;
import com.flower.tables.FlowerTemplate;
import com.flower.tables.GoodsEntity;
import com.flower.tables.GoodsOrder;
import com.flower.tables.GoodsTemplate;
/**
 * @author 王雪冬
 *用户后台管理
 */
public class CustomerHelper  extends AbstractHttpHelper{

	private static final Logger log = Logger.getLogger(FlowersHelper.class);
	
	
	/**
	 * 用户详情
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 * @throws Exception
	 */
	@HttpListening(urlPattern="/admin/customerHelper/getCustomerDetails.do",isCheckSession=true)
	public Response getCustomerDetails(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String uid = request.getParameter("uid");
		//查询总消费金额
		Double sumMony = getSumMoney(uid);
		request.setAttribute("sumMony", sumMony);
		//查询所有交易记录
		List goodsOrderList = getGoodsOrderAll(uid);
		request.setAttribute("goodsOrderList", goodsOrderList);
		//用户花仓
		List goodsEntityList = getGoodsEntity(uid);
		request.setAttribute("goodsEntityList", goodsEntityList);
		//用户详情
		Object customer = getCustomer(uid);
		request.setAttribute("customer", customer);
		return Response.forward("/admin/customer_sets/customerDetails.jsp");
	}
	private Object getCustomer(String uid) {
		Session session = HibernateSessionFactory.getSession();
		Query query = session.createSQLQuery("select * from "+Customer.class.getSimpleName()+" where uid =?").setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).setString(0,uid);
		return query.uniqueResult();
	}
	private List getGoodsEntity(String uid) {
		Session db =  HibernateSessionFactory.getSession();
		String sql = "select ge.actualpay,ge.gainTime,ge.state,gt.name from "+GoodsEntity.class.getSimpleName()+" ge left join "+GoodsTemplate.class.getSimpleName()+" gt on ge.goods_template_id=gt.id where ge.uid =?";
		Query query = db.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).setString(0,uid);
	    return query.list();
	}
	private List getGoodsOrderAll(String uid) {
		Session db =  HibernateSessionFactory.getSession();
		String sql = "select go.state,go.payTime,go.payMoney,go.goodsIds,gt.name from "+GoodsOrder.class.getSimpleName()+" go left join "+GoodsTemplate.class.getSimpleName()+" gt on gt.id in(go.goodsIds) where go.uid=?";
		Query query = db.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).setString(0,uid);
	    return query.list();
	}
	private Double getSumMoney(String uid) {
		Session db =  HibernateSessionFactory.getSession();
		String sql = "select sum(payMoney) from "+GoodsOrder.class.getSimpleName()+" where state!=1 and uid=?";
		Query query = db.createSQLQuery(sql).setString(0,uid);
		return (Double)query.uniqueResult();
	}
	/**
	 * 用户检索
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 * @throws Exception
	 */
	@HttpListening(urlPattern="/admin/customerHelper/getCustomerList.do",isCheckSession=true)
	public Response getFlowersList(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{ 
		String keyword = request.getParameter("keyword");
		List customerList = getCustomerList(keyword);
		request.setAttribute("customerList", customerList);
		return Response.forward("/admin/customer_sets/customerList.jsp");
	}
	private List getCustomerList(String keyword) {
        Session db =  HibernateSessionFactory.getSession();
		String sql = "select uid,phone,email,nickname,sex,flowerSeed from "+Customer.class.getSimpleName()+" where phone like '%"+keyword+"%'";
		Query query = db.createSQLQuery(sql).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
	    return query.list();
	}
}
