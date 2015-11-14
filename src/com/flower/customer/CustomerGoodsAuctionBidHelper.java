package com.flower.customer;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

import com.flower.enums.GoodsBidState;
import com.flower.enums.ResultState;
import com.flower.tables.FlowerTemplate;
import com.flower.tables.GoodsAuction;
import com.flower.tables.GoodsBid;
import com.flower.tables.GoodsEntity;
import com.flower.tables.GoodsTemplate;
import com.flower.util.UUIDUtil;
/**
 * @author 王雪冬
 * 拍卖Helper
 */
public class CustomerGoodsAuctionBidHelper extends AbstractHttpHelper{

	private static final Logger log = Logger.getLogger(CustomerGoodsAuctionBidHelper.class);

	@HttpListening(urlPattern="/customer/createGoodsAuction.do",isCheckSession=true)
	public Response createGoodsAuction(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		String entityId = StringUtil.getNotNull(request.getParameter("entityId")).trim();//拍卖商品Id
		String auctionPrice = StringUtil.getNotNull(request.getParameter("auctionPrice")).trim();//拍卖额
		Session session = HibernateSessionFactory.getSession();
		GoodsEntity goodsEntity =(GoodsEntity)session.get(GoodsEntity.class, entityId);
		if(goodsEntity == null){
			result.put("message","没有相关商品");
			return Response.stationary(result);
		}
		GoodsAuction goodsAuction = new GoodsAuction();
	    SimpleDateFormat date =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowDate = date.format(new Date()).toString();
		goodsAuction.createTime  = nowDate ;
		goodsAuction.auctionPrice = Integer.parseInt(auctionPrice);
		goodsAuction.goodsEntity = goodsEntity;
		goodsAuction.uid =goodsEntity.uid;
		Transaction t = session.beginTransaction();
		session.save(goodsAuction);
		t.commit();
		result.put("state", ResultState.Z_正常.state);
		result.put("message","拍卖成功");
		return Response.stationary(result);
	}
	@HttpListening(urlPattern="/cust/getGoodsAuctionList.do")
	public Response getGoodsAuctionList(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		log.info("开始查询拍卖商品");
		String ptype = StringUtil.getNotNull(request.getParameter("ptype")).trim();//根据商品类型查询
		StringBuilder sql = new StringBuilder();
		sql.append("select  ga.id goodsAuctionId,ga.auctionPrice,ge.actualPay,gt.name,gt.id goodTemplateId,ft.icon,ga.createTime from  "+GoodsAuction.class.getSimpleName()+" ga ");
		sql.append("left join "+GoodsEntity.class.getSimpleName()+" ge on ga.goodsEntityId = ge.id ");
		sql.append("left join "+GoodsTemplate.class.getSimpleName()+" gt on ge.goods_template_id =gt.id ");
		sql.append("left join "+FlowerTemplate.class.getSimpleName ()+" ft on gt.flowerid = ft.id where ft.ptype =? ");
		sql.append("order by ga.createTime DESC,ga.auctionPrice-ge.actualPay ASC, ga.auctionPrice ASC");
		Session session = HibernateSessionFactory.getSession();
		Query query =session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setString(0,ptype);
        result.put("state", ResultState.Z_正常.state);
        result.put("data", query.list());
		return Response.stationary(result);
	}
	@HttpListening(urlPattern="/customer/createGoodsBid.do",isCheckSession=true)
	public Response createGoodsBid(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		log.info("存入竞拍商品");
		Map<String,Object> result = new HashMap<String,Object>();
		String bidPrice = StringUtil.getNotNull(request.getParameter("bidPrice")).trim();//竞拍价格
		String uid = StringUtil.getNotNull(request.getParameter("uid")).trim();//竞拍者
		String goodsAuctionId = StringUtil.getNotNull(request.getParameter("goodsAuctionId")).trim();//竞拍商品
		SimpleDateFormat date =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowDate = date.format(new Date()).toString();
		Session session = HibernateSessionFactory.getSession();
		GoodsAuction goodsAuction =(GoodsAuction)session.get(GoodsAuction.class,Integer.parseInt(goodsAuctionId));
		if(goodsAuction == null){
			result.put("message","没有拍卖商品");
			return Response.stationary(result);
		}
		GoodsBid goodsBid = new GoodsBid();
		goodsBid.bidid = UUIDUtil.竞价ID.id();
		goodsBid.bidPrice = Integer.parseInt(bidPrice);
		goodsBid.createTime = nowDate;
		goodsBid.goodsAcution = goodsAuction;
		goodsBid.uid =  uid;
		goodsBid.state = GoodsBidState.getEnum(1);
		goodsBid.isPay = false;
		Transaction t = session.beginTransaction();
		session.save(goodsBid);
		t.commit();
		result.put("state", ResultState.Z_正常.state);
		result.put("message","竞拍成功请等待竞拍结果");
		return Response.stationary(result);
	}
	@HttpListening(urlPattern="/customer/informCustomer.do",isCheckSession=true)
	public Response informCustomer(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		log.info("开始通知用户进行付款");
		Map<String,Object> result = new HashMap<String,Object>();
		log.info("查出所有竞拍中的商品");
		Session session = HibernateSessionFactory.getSession();
		//所有竞拍信息
		Query query =session.createSQLQuery("select * from  "+GoodsBid.class.getSimpleName()+"  where state =1 order by bidPrice desc ").setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);;
		List<GoodsBid> goodsBidList = query.list();
		//竞拍商品分组
	    query =session.createSQLQuery("select  goodsAcutionId  from  "+GoodsBid.class.getSimpleName()+"  where state  =1 group by goodsAcutionId");
	    List list = query.list();
	    List resultList = new ArrayList();
        //将竞价这归属到统一个竞价商品
	    for(int i=0;i<list.size();i++){
        	String entityId = list.get(i).toString();
        	//竞价同一商品
        	List<Map> bidList =new ArrayList<Map>();
        	int entity_Id = 0;
        	for(int j=0;j<goodsBidList.size();j++){
        		 Map map_goodsBid  =(Map)goodsBidList.get(j);
        		 entity_Id =  Integer.parseInt(map_goodsBid.get("goodsAcutionId").toString()) ;
        		 if(Integer.parseInt(entityId) == entity_Id){
      			    bidList.add(map_goodsBid);
     		     }
        	 }
        	if(bidList !=null && bidList.size()>0){
        		 Date _date = new Date();
        		 Date prev_date = new Date();
            	 for(int z=0;z<bidList.size();z++){
            		  Map bidMap =  bidList.get(z);
            		  //付款金额最多用户
            		  SimpleDateFormat date =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            		  GoodsBid goodsbid = new GoodsBid();
           			  if(z == 0){
           				  goodsbid.bidid = bidMap.get("id").toString();
           				  goodsbid.state =GoodsBidState.getEnum(2);
            			  updateBid(goodsbid);
            			  bidMap.put("state", GoodsBidState.竞拍成功);
            			  String nowDate = date.format(_date);
            			  bidMap.put("informTime",nowDate);
            			  resultList.add(bidMap);
            		  }else{
            			  goodsbid.bidid = bidMap.get("id").toString();
           				  goodsbid.state =GoodsBidState.getEnum(3);
            			  updateBid(goodsbid);
            			  bidMap.put("state", GoodsBidState.竞拍失败);
            			  Date now_10 = new Date(prev_date.getTime() + 600000);
            			  prev_date = now_10;
            			  String nowDate = date.format(prev_date);
            			  bidMap.put("informTime",nowDate);
            			  resultList.add(bidMap);
            		  }
            	 }
        	}
        }
	    result.put("state", ResultState.Z_正常.state);
	    result.put("data",resultList);
		return Response.stationary(result);
	}
	private void updateBid(GoodsBid goodsbid) {
		Session session = HibernateSessionFactory.getSession();
		Transaction t = session.beginTransaction();
		session.update(goodsbid);
		t.commit();
	}
	public static Map<String,Object> getEntityIdAndUid(String bidId){
		Session session = HibernateSessionFactory.getSession();
		String find_sql ="select gd.id ,ga.goodsEntityId from "+GoodsBid.class.getSimpleName()+" gd left join "+GoodsAuction.class.getSimpleName()+" ga on gd.goodsAcutionId = ga.id  where gd.bidid=?";
		Map<String,Object> productDetails = (Map<String, Object>) session.createSQLQuery(find_sql.toString()).setString(0, bidId).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).uniqueResult();
	   return productDetails;
	}
	@HttpListening(urlPattern="/customer/confirmPay.do",isCheckSession=true)
	public Response confirmPay(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		log.info("用户付款");
	    TreeMap<String, String> resultMap = new TreeMap<String, String>();
		String id = StringUtil.getNotNull(request.getParameter("id")).trim();//竞拍Id
		String bidPrice = StringUtil.getNotNull(request.getParameter("bidPrice")).trim();//竞拍价格
		String uid = StringUtil.getNotNull(request.getParameter("uid")).trim();//竞拍者
		String goodsAuctionId = StringUtil.getNotNull(request.getParameter("goodsAuctionId")).trim();//竞拍商品
		Session session = HibernateSessionFactory.getSession();
		GoodsBid goodsBid =(GoodsBid)session.get(GoodsBid.class,Integer.parseInt(id));
		if(!goodsBid.isPay()){
			 //说明当前竞拍没有支付   本用户可以支付  调用第三方支付接口
			 String ip = CustomerWxHelper.getIp(request);
		     resultMap = CustomerWxHelper.unifiedorder(uid, ip,bidPrice,id);
			 if(resultMap==null){
				  resultMap.put("state", ResultState.E_异常+"");  
				  resultMap.put("message", "微信支付异常");
				  return Response.stationary(resultMap);
			 }
		     //如果支付成功 将竞拍的商品实体 uid 改为当前竞拍者
		     resultMap.put("state", ResultState.Z_正常.state+"");  
		     resultMap.put("message", "恭喜您竞拍成功 请到我的花仓查看");
		}else{
			resultMap.put("state", ResultState.Z_正常.state+"");  
			resultMap.put("message", "当前商品已被别人付款，请在看看其他商品");
		}
		return Response.stationary(resultMap);
	}
	public static void main(String[] args) {
		String id = UUIDUtil.竞价ID.id();
		id = id.substring(0,1);
		System.out.println("竞价Id "+id);
		String id2 = UUIDUtil.订单ID.id();
		id2 = id2.substring(0,1);
		System.out.println("订单Id "+id2);
	}
}
