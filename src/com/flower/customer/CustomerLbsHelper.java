package com.flower.customer;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;
import org.guyou.web.server.SessionKeyEnum;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import com.flower.customer.beans.CustomerUser;
import com.flower.enums.ResultState;
import com.flower.tables.Customer;
import com.flower.tables.UserPoi;
/**
 * @author 王雪冬
 * 附近的人Helper
 */
public class CustomerLbsHelper extends AbstractHttpHelper{

	private static final Logger log = Logger.getLogger(CustomerLbsHelper.class);
	
	private static final String geotable_id = "113676";
	private static final String coord_type = "1";
 	
	@HttpListening(urlPattern="/customer/createLocation.do",isCheckSession=true)
	public Response createLocation(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		log.info("开始更新我的位置");
		try{
			 CustomerUser user = (CustomerUser)request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
			 Customer customer=user.customer;
 		    String longitude = request.getParameter("longitude").toString();
		    String latitude = request.getParameter("latitude").toString();
		    Session session = HibernateSessionFactory.getSession();
			StringBuilder sql = new StringBuilder();
			sql.append("select * from "+UserPoi.class.getSimpleName()+" where userId = ?");
			Query query =session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).setString(0,customer.uid);
			UserPoi userPoi = (UserPoi)query.uniqueResult();
			if(userPoi==null){//存储
				CustomerBaiduApiHelper.createPoi(latitude, longitude, coord_type, geotable_id, customer.uid);
			}else{//更新
				int poiId = userPoi.getPoiId();
				CustomerBaiduApiHelper.updatePoi(poiId+"", latitude, longitude, coord_type, geotable_id, customer.uid);
 			}
			result.put("state", ResultState.Z_正常.state);
			result.put("message","更新成功");
		}catch(Exception e){
			result.put("state", ResultState.E_异常.state);
			result.put("message","更新失败");
			log.info("更新位置信息失败");
			e.printStackTrace();
		}
		return Response.stationary(result);
	}
	@HttpListening(urlPattern="/customer/nearBy.do",isCheckSession=true)
	public Response nearBy(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		log.info("开始查询附近的人");
		try{
			 CustomerUser user = (CustomerUser)request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
			 Customer customer=user.customer;
			 Session session = HibernateSessionFactory.getSession();
			 StringBuilder sql = new StringBuilder();
		   	 sql.append("select * from "+UserPoi.class.getSimpleName()+" where userId = ?");
			 Query query =session.createSQLQuery(sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).setString(0,customer.uid);
			 UserPoi userPoi = (UserPoi)query.uniqueResult();
			 JSONObject nearby = CustomerBaiduApiHelper.nearby(userPoi.poiId+"", geotable_id);
			 result.put("state", ResultState.Z_正常.state);
			 result.put("datas",nearby.getString("contents"));
		}catch(Exception e){
			 result.put("state", ResultState.E_异常.state);
			 result.put("message","查询失败");
			 log.info("查询附近的人信息失败");
			  e.printStackTrace();
		}
		return Response.stationary(result);
	}
}
