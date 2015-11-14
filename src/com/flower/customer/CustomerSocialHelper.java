/**
 * create by 朱施健
 */
package com.flower.customer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.guyou.util.StringUtil;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;
import org.guyou.web.server.SessionKeyEnum;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

import com.easemob.server.jersey.EasemobManager;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flower.customer.beans.CustomerUser;
import com.flower.customer.vo.ApplyerIndexVo;
import com.flower.customer.vo.CustomerIndexVo;
import com.flower.customer.vo.CustomerOpenInfoVo;
import com.flower.enums.ResultState;
import com.flower.tables.Blacklist;
import com.flower.tables.Customer;
import com.flower.tables.FriendApply;
import com.flower.tables.FriendApply.FriendApplyId;
import com.flower.tables.FriendRelation;

/**
 * @author 朱施健
 * 社交相关Helper
 */
public class CustomerSocialHelper extends AbstractHttpHelper {
	
	private static final Logger log = Logger.getLogger(CustomerSocialHelper.class);
	
	/**
	 * 搜索用户
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 * @throws Exception
	 */
	@HttpListening(urlPattern="/customer/search_customer.do",isCheckSession=true)
	public Response searchCustomerHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String phone = StringUtil.getNotNull(request.getParameter("phone")).trim();
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		
		String query_sql = "select uid,phone,nickname,ifnull(sex,0) sex,email,headIcon,likeFlower,signature,(select count(*) from "+FriendRelation.class.getSimpleName()+" where (me,friend)=(?,uid)) isFriend"
				+ " from "+Customer.class.getSimpleName()
				+ " where phone=? and uid<>? and canSearchByPhone=1 "
				+ " and not exists (select * from "+Blacklist.class.getSimpleName()+" where me=uid and blacker=?)";
		@SuppressWarnings("unchecked")
		Map<String,Object> map = (Map<String, Object>) HibernateSessionFactory.getSession().createSQLQuery(query_sql)
				.setString(0,user.customer.uid)
				.setString(1, phone)
				.setString(2, user.customer.uid)
				.setString(3, user.customer.uid)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.uniqueResult();
		
		Map<String,Object> result = new HashMap<String, Object>();
		if(map==null){
			result.put("isHaveUser", false);
		}else{
			result.put("isHaveUser", true);
			CustomerOpenInfoVo vo = new CustomerOpenInfoVo();
			vo.uid = (String)map.get("uid");
			vo.phone = (String)map.get("phone");
			vo.nickname = (String)map.get("nickname");
			String images = StringUtil.getNotNull((String)map.get("images"));
			vo.images = StringUtil.isNullValue(images)?new String[0]:images.split(",");
			vo.headIcon = (String)map.get("headIcon");
			vo.sex = ((Number)map.get("sex")).byteValue();
			vo.email = (String)map.get("email");
			vo.likeFlower = (String)map.get("likeFlower");
			vo.signature  = (String)map.get("signature");
			vo.isFriend = ((Number)map.get("isFriend")).intValue() >0 ;
			result.put("userinfo", vo);
		}
		return Response.stationary(result);
	}
	
	/**
	 * 通讯录列表
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 * @throws Exception
	 */
	@HttpListening(urlPattern="/customer/contacts_list.do",isCheckSession=true)
	public Response contactsListHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		
		Map<String,Object> result = new HashMap<String, Object>();
		
		String query_sql = "select uid,phone,nickname,headIcon,ifnull(sex,0) sex from "+FriendRelation.class.getSimpleName()+" left join "+Customer.class.getSimpleName()+" on friend=uid where me=?";
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> friends =  HibernateSessionFactory.getSession().createSQLQuery(query_sql)
				.setString(0,user.customer.uid)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
		List<CustomerIndexVo> rList = new ArrayList<CustomerIndexVo>();
		for (Map<String,Object> map : friends) {
			CustomerIndexVo vo = new CustomerIndexVo();
			vo.uid = (String)map.get("uid");
			vo.phone = (String)map.get("phone");
			vo.nickname = (String)map.get("nickname");
			vo.headIcon =(String)map.get("headIcon");
			vo.sex = ((Number)map.get("sex")).byteValue();
			rList.add(vo);
		}
		result.put("state", ResultState.Z_正常.state);
		result.put("contacts", rList);
		return Response.stationary(result);
	}
	
	/**
	 * 申请添加好友
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 * @throws Exception
	 */
	@HttpListening(urlPattern="/customer/apply_friend.do",isCheckSession=true)
	public Response applyFriendHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		String target = StringUtil.getNotNull(request.getParameter("target")).trim();
		String msg = StringUtil.getNotNull(request.getParameter("msg")).trim();
		
		Session session = HibernateSessionFactory.getSession();
		Map<String,Object> result = new HashMap<String, Object>();
		
		boolean isExist = ((Number)session.createSQLQuery("select count(*) from "+Customer.class.getSimpleName()+" where uid=?").setString(0, target).uniqueResult()).intValue()>0;
		
		if(!isExist){
			result.put("state", ResultState.Y_用户不存在.state);
			Response.stationary(result);
		}
		boolean isFriend = ((Number)session.createSQLQuery("select count(*) from "+FriendRelation.class.getSimpleName()+" where (me,friend)=(?,?)").setString(0, user.customer.uid).setString(1, target).uniqueResult()).intValue()>0;
		if(isFriend){
			result.put("state", ResultState.Y_已是好友.state);
			Response.stationary(result);
		}
		
		FriendApplyId fid = new FriendApplyId(user.customer.uid,target);
		FriendApply apply = (FriendApply) session.createQuery("from "+FriendApply.class.getName()+" where id=:id")
				.setParameter("id", fid)
				.uniqueResult();
		Transaction t = session.beginTransaction();
		if(null==apply){
			apply = new FriendApply();
			apply.id = fid;
			apply.applyMsg = msg;
			session.save(apply);
		}else{
			apply.applyMsg = msg;
			session.update(apply);
		}
		t.commit();
		result.put("state", ResultState.Z_正常.state);
		return Response.stationary(result);
	}
	
	@HttpListening(urlPattern="/customer/friend_apply_list.do",isCheckSession=true)
	public Response friendApplyListHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		
		String sql = "select uid,phone,nickname,headIcon,ifnull(sex,0) sex,applyMsg,applyState from "+FriendApply.class.getSimpleName()+" left join "+Customer.class.getSimpleName()+" on me=uid where applyTarget=?";
		
		Map<String,Object> result = new HashMap<String, Object>();
		
		Session session = HibernateSessionFactory.getSession();
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> list = session.createSQLQuery(sql)
				.setString(0,user.customer.uid)
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
				.list();
		List<ApplyerIndexVo> rList = new ArrayList<ApplyerIndexVo>();
		for (Map<String,Object> map : list) {
			ApplyerIndexVo vo = new ApplyerIndexVo();
			vo.uid = (String)map.get("uid");
			vo.phone = (String)map.get("phone");
			vo.nickname = (String)map.get("nickname");
			vo.headIcon =(String)map.get("headIcon");
			vo.sex = ((Number)map.get("sex")).byteValue();
			vo.applyMsg = (String)map.get("applyMsg");
			vo.applyState = ((Number)map.get("applyState")).byteValue();
			rList.add(vo);
		}
		result.put("state", ResultState.Z_正常.state);
		result.put("applys", rList);
		return Response.stationary(rList);
	}
	
	/**
	 * 确认好友申请
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 * @throws Exception
	 */
	@HttpListening(urlPattern="/customer/confirm_apply.do",isCheckSession=true)
	public Response confirmFriendHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		String applyer = StringUtil.getNotNull(request.getParameter("applyer")).trim();
		//1[通过] | 2[拒绝]
		String operate = StringUtil.getNotNull(request.getParameter("operate")).trim();
		
		Map<String,Object> result = new HashMap<String, Object>();
		
		Session session = HibernateSessionFactory.getSession();
		Transaction t = session.beginTransaction();
		if("1".equals(operate)){
			ObjectNode node = EasemobManager.addFriend(user.customer.uid, applyer);
			if(node.get("statusCode").asInt()==200){
				FriendRelation relation1 = new FriendRelation(user.customer.uid,applyer);
				FriendRelation relation2 = new FriendRelation(applyer,user.customer.uid);
				session.save(relation1);
				session.save(relation2);
			}else{
				result.put("state", ResultState.I_IM异常.state);
				return Response.stationary(result);
			}
		}
		session.createSQLQuery("update "+FriendApply.class.getSimpleName()+" set applyState=? where me=? and applyTarget=?")
			.setInteger(0, Integer.valueOf(operate))
			.setString(1,applyer)
			.setString(2, user.customer.uid)
			.executeUpdate();
		t.commit();
		
		result.put("state", ResultState.Z_正常.state);
		result.put("applyState", Integer.valueOf(operate));
		return Response.stationary(result);
	}
	
	@HttpListening(urlPattern="/customer/delete_friend.do",isCheckSession=true)
	public Response deleteFriendHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		String friend = StringUtil.getNotNull(request.getParameter("friend")).trim();
	
		Map<String,Object> result = new HashMap<String, Object>();
		//删除IM好友
		EasemobManager.deleteFriend(user.customer.uid, friend);

		Session session = HibernateSessionFactory.getSession();
		Transaction t = session.beginTransaction();
		session.createSQLQuery("delete from "+FriendRelation.class.getSimpleName()+" where (me,friend)=(?,?) or (me,friend)=(?,?)")
			.setString(0, user.customer.uid)
			.setString(1, friend)
			.setString(2, friend)
			.setString(3, user.customer.uid)
			.executeUpdate();
		session.createSQLQuery("update "+FriendApply.class.getName()+" set applyState=2 where me=? and applyTarget=?")
			.setString(0,friend)
			.setString(1, user.customer.uid)
			.executeUpdate();
		t.commit();
	
		result.put("state", ResultState.Z_正常.state);
		return Response.stationary(result);
	}
	
	/**
	 * 加入黑名单
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 * @throws Exception
	 */
	@HttpListening(urlPattern="/customer/add_to_blacklist.do",isCheckSession=true)
	public Response addToBlacklistHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		String blacker = StringUtil.getNotNull(request.getParameter("blacker")).trim();
		
		Session session = HibernateSessionFactory.getSession();
		Transaction t = session.beginTransaction();
		int rows = session.createSQLQuery("delete from "+FriendRelation.class.getSimpleName()+" where (me,friend)=(?,?) or (me,friend)=(?,?)")
					.setString(0, user.customer.uid)
					.setString(1, blacker)
					.setString(2, blacker)
					.setString(3, user.customer.uid)
					.executeUpdate();
		//原来是好友关系
		if(rows !=0 ){
			//删除IM好友
			EasemobManager.deleteFriend(user.customer.uid, blacker);
			session.createSQLQuery("update "+FriendApply.class.getName()+" set applyState=2 where me=? and applyTarget=?")
				.setString(0,blacker)
				.setString(1, user.customer.uid)
				.executeUpdate();
		}
		session.createSQLQuery("insert into "+Blacklist.class.getSimpleName()+" (me,blacker) select ?,? from dual where not exists (select * from "+Blacklist.class.getSimpleName()+" where me=? and blacker=?)")
			.setString(0,user.customer.uid)
			.setString(1,blacker)
			.setString(2,user.customer.uid)
			.setString(3,blacker)
			.executeUpdate();
		t.commit();
		return Response.stationary("{\"state\":"+ResultState.Z_正常.state+"}");
	}
}
