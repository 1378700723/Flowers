package com.flower.customer;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.guyou.util.StringUtil;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;
import org.guyou.web.server.SessionKeyEnum;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

import com.flower.customer.beans.CustomerUser;
import com.flower.enums.OrderState;
import com.flower.enums.ResultState;
import com.flower.tables.Customer;
import com.flower.tables.DeliveryAddress;
import com.flower.tables.FlowerTemplate;
import com.flower.tables.GoodsOrder;
import com.flower.tables.GoodsTemplate;
import com.flower.util.UUIDUtil;

/**
 * @author 王雪冬
 * 购买商品Helper
 */
public class CustomerBuyHelper extends AbstractHttpHelper{
	
	private static final Logger log = Logger.getLogger(CustomerBuyHelper.class);
	
	
	@HttpListening(urlPattern="/customer/getGoodsOrderByCustomerId.do",isCheckSession=true)
	public Response getGoodsOrderByCustomerId(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		log.info("开始查询当前用户的交易记录");
		//获取当前用户
	    CustomerUser user = (CustomerUser)request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
	    Customer customer=user.customer;
	    Session session = HibernateSessionFactory.getSession();
		StringBuilder sql = new StringBuilder();
		sql.append("select g.*,(select gtm.name from "+GoodsTemplate.class.getSimpleName()+" gtm where id  in (g.goodsIds)) goodName from "+GoodsOrder.class.getSimpleName()+" g left join "+Customer.class.getSimpleName()+" c on g.uid=c.uid where g.uid=? ");
		Query query =session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setString(0, customer.uid);
        result.put("state", ResultState.Z_正常.state);
        result.put("data", query.list());
		return Response.stationary(result);
	}
	@HttpListening(urlPattern="/customer/buyProduct.do",isCheckSession=true)
	public Response buyProduct(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
 		log.info("点击购买 开始生成订单");
		String goodId  = request.getParameter("goodId");
		String goodsTemplateName  = request.getParameter("goodsTemplateName");
 		int goodsCount = Integer.parseInt(request.getParameter("goodsCount")); //购买数量 
 		int useFlowerSeedCount =0;
 		String s_useFlowerSeedCount =request.getParameter("useFlowerSeedCount");
 		if(s_useFlowerSeedCount !=null ){
 			useFlowerSeedCount=Integer.parseInt(s_useFlowerSeedCount) ;//所使用花籽
 		}
		float totalPrice = Float.parseFloat(request.getParameter("totalPrice")); //总金额
		float unitPrice  =Float.parseFloat(request.getParameter("unitPrice"));//单价
		float payMoney  =Float.parseFloat(request.getParameter("payMoney"));//支付金额
		float deductibleMoney  =Float.parseFloat(request.getParameter("deductibleMoney"));//花籽抵押金额
		//获取当前用户
	    CustomerUser user = (CustomerUser)request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
	    Customer customer=user.customer;
	    //生成订单表
	    GoodsOrder order =new GoodsOrder();
	    String[] goodIds = new String[goodsCount];
	    if(goodsCount>1){
	    	 for(int i=0;i<goodsCount;i++){//生成多个商品实例
	    		 goodIds[i] =goodId;
	    	 }
	    }else{
	    	goodIds[0]=goodId;
	    }
	    order.goodsIds = goodIds;
	    order.uid = customer.getUid();
	    order.goodsCount = goodsCount;
	    order.useFlowerSeedCount = useFlowerSeedCount;
	    order.totalPrice = totalPrice;
	    order.unitPrice = unitPrice;
	    order.payMoney = payMoney;
	    order.deductibleMoney = deductibleMoney;
	    SimpleDateFormat date =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String nowDate = date.format(new Date()).toString();
	    order.orderTime = nowDate;
	    order.payTime = nowDate;
	    order.state = OrderState.getEnum(1);
	    order.orderid = UUIDUtil.订单ID.id();
	    Session session = HibernateSessionFactory.getSession();
		Transaction t = session.beginTransaction();
		session.save(order);
		t.commit();
        log.info("创建订单成功    当前订单编号 "+order.orderid+" 开始微信支付下单");
        String ip = CustomerWxHelper.getIp(request);
        TreeMap<String, String> resultMap = CustomerWxHelper.unifiedorder(goodsTemplateName, ip,totalPrice+"", order.orderid);
        if(resultMap==null){
        	resultMap.put("state", ResultState.E_异常+"");
        	resultMap.put("message", "微信下单失败");
        }
        resultMap.put("orderId", order.orderid);
        resultMap.put("state", ResultState.Z_正常.state+"");
		return Response.stationary(resultMap);
	}
	@HttpListening(urlPattern="/customer/addDeliveryAddress.do",isCheckSession=true)
	public Response addDeliveryAddress(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		log.info("添加收货地址");
		Map<String,Object> result = new HashMap<String,Object>();
		String receiver = StringUtil.getNotNull(request.getParameter("receiver")).trim();
		String address = StringUtil.getNotNull(request.getParameter("address")).trim();
		String phone = StringUtil.getNotNull(request.getParameter("phone")).trim();
 		CustomerUser user = (CustomerUser)request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
 		//Customer tom =new Customer();
 		//tom.uid="55";
 		DeliveryAddress deliveryAddress = new DeliveryAddress();
 		deliveryAddress.receiver = receiver;
 		deliveryAddress.address = address;
 		deliveryAddress.phone = phone;
 		deliveryAddress.customer = user.customer;
 		Session session = HibernateSessionFactory.getSession();
		Transaction t = session.beginTransaction();
		session.save(deliveryAddress);
		t.commit();
		result.put("info", "添加收货地址完成");
        result.put("state", ResultState.Z_正常.state);
        log.info("完成添加收货地址");
		return Response.stationary(result);
	}
	@HttpListening(urlPattern="/customer/confirmIndent.do",isCheckSession=true)
	public Response confirmIndent(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		log.info("确认订单页");
        //String uId = request.getParameter("uId");
	    String goodId = request.getParameter("goodId");
		Map<String,Object> result = new HashMap<String,Object>();
	   //获取当前用户
	   CustomerUser user = (CustomerUser)request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
	   Customer customer=user.customer;
	   Object deliveryAddress  =getDeliveryAddress(customer.getUid());
		//本用户是否有收货地址
	    if(deliveryAddress == null ){
	    	result.put("state",0);
	    	result.put("info", "没有收货地址请先添加收货地址");
	    	return Response.stationary(result);
	    }
	    Object product = getGoodTemplate(goodId);
        result.put("product",product);
        result.put("deliveryAddress",deliveryAddress);
        result.put("state", ResultState.Z_正常.state);
	   return Response.stationary(result);
	}

	private Object getGoodTemplate(String goodId) {
 		Session session = HibernateSessionFactory.getSession();
		StringBuilder sql = new StringBuilder();
		sql.append("select g.id goodTemplateId,g.name,g.curPrice,g.delivery,f.icon  ");
		sql.append("from "+GoodsTemplate.class.getSimpleName()+" g left join "+FlowerTemplate.class.getSimpleName()+" f on g.flowerid=f.id  where  g.id =?");
		Query query =session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setString(0, goodId);
		return  query.uniqueResult();
	}

	private Object getDeliveryAddress(String uId) {
 		Session session = HibernateSessionFactory.getSession();
		StringBuilder sql = new StringBuilder();
		sql.append("select * from "+DeliveryAddress.class.getSimpleName()+" where uid=?");
 		Query query =session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
 		query.setString(0, uId);
		return query.uniqueResult();
	}	
}
