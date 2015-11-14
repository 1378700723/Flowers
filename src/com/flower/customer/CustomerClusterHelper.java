/**
 * create by 朱施健
 */
package com.flower.customer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.guyou.util.DateUtil;
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
import com.flower.customer.vo.CustomerIndexVo;
import com.flower.enums.ResultState;
import com.flower.tables.Cluster;
import com.flower.tables.ClusterMember;
import com.flower.tables.Customer;

/**
 * @author 朱施健
 * 群相关Helper
 */
public class CustomerClusterHelper extends AbstractHttpHelper {
	
	private static final Logger log = Logger.getLogger(CustomerClusterHelper.class);
	
	
	@HttpListening(urlPattern="/customer/create_group.do",isCheckSession=true)
	public Response createGroupHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String name = StringUtil.getNotNull(request.getParameter("name")).trim();
		String desc = StringUtil.getNotNull(request.getParameter("desc")).trim();
		String members = StringUtil.getNotNull(request.getParameter("members")).trim();
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
	
		Session session = HibernateSessionFactory.getSession();
		
		List<CustomerIndexVo> rList = new ArrayList<CustomerIndexVo>();
		List<String> member_phone_list = new ArrayList<String>();
		if(!StringUtil.isNullValue(members)){
			StringBuilder query_phone_sql = new StringBuilder("select uid,phone,nickname,headIcon,sex from "+Customer.class.getSimpleName()+" where uid in (");
			for (String uid : members.split(",")) {
				if(uid.equalsIgnoreCase(user.customer.uid)) continue;
				query_phone_sql.append("'"+uid+"',");
			}
			StringUtil.deleteEndsMark(query_phone_sql, ",").append(")");
			List<Map<String,Object>> list = session.createSQLQuery(query_phone_sql.toString()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
			
			for (Map<String,Object> map : list) {
				CustomerIndexVo vo = new CustomerIndexVo();
				vo.uid = (String)map.get("uid");
				vo.phone = (String)map.get("phone");
				vo.nickname = (String)map.get("nickname");
				vo.headIcon = (String)map.get("headIcon");
				vo.sex = ((Number)map.get("sex")).byteValue();
				rList.add(vo);
				member_phone_list.add(vo.phone);
			}
		}
		
		//{"action":"post","application":"f90baaf0-1324-11e5-b913-a5090b1fe5b5","uri":"https://a1.easemob.com/huahuahui/huahuahui","entities":[],"data":{"groupid":"143445012842806"},"timestamp":1434450128037,"duration":38,"organization":"huahuahui","applicationName":"huahuahui","statusCode":200}
		ObjectNode dataObjectNode = EasemobManager.createGroup(user, name, desc,member_phone_list);
		
		Map<String,Object> result = new HashMap<String,Object>();
		
		if(dataObjectNode==null){
			result.put("state",ResultState.I_IM异常.state);
		}else if(dataObjectNode.has("statusCode") && dataObjectNode.get("statusCode").asInt()==200){
			String groupid = dataObjectNode.get("data").get("groupid").asText();
			Cluster group = new Cluster();
			group.gid = groupid;
			group.creater = user.customer.uid;
			group.createTime = DateUtil.time();
			group.name = name;
			group.description = desc;
			group.members = new HashSet<ClusterMember>();
			//自己
			ClusterMember member = new ClusterMember();
			member.uid = user.customer.uid;
			member.cluster = group;
			group.members.add(member);
			//成员
			for (int i = 0; i < rList.size(); i++) {
				ClusterMember memberi = new ClusterMember();
				memberi.uid = rList.get(i).uid;
				memberi.cluster = group;
				group.members.add(memberi);
			}
			
			Transaction t = session.beginTransaction();
			session.save(group);
			t.commit();
			
			rList.add(0,user.customer.toCustomerIndexVo());
			
			result.put("state",ResultState.Z_正常.state);
			result.put("groupid", groupid);
			result.put("members", rList);
		}
		return Response.stationary(result);
	}
	
	/**
	 * 加入群
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 * @throws Exception
	 */
	@HttpListening(urlPattern="/customer/join_group.do",isCheckSession=true)
	public Response joinGroupHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		String groupid = StringUtil.getNotNull(request.getParameter("groupid")).trim();
		String target = StringUtil.getNotNull(request.getParameter("target")).trim();

		Map<String,Object> result = new HashMap<String,Object>();
		
		ObjectNode dataObjectNode = EasemobManager.joinGroup(groupid, target);
		
		if(dataObjectNode==null){
			result.put("state",ResultState.I_IM异常.state);
		}else if(dataObjectNode.has("statusCode") && dataObjectNode.get("statusCode").asInt()==200){
			String insert_sql = "insert into "+ClusterMember.class.getSimpleName()+" (uid,isOpenMsgAlert,gid) select ?,?,? from dual where not exists (select * from "+ClusterMember.class.getSimpleName()+" where uid=? and gid=?)";
			Session session = HibernateSessionFactory.getSession();
			Transaction t = session.beginTransaction();
			int r = session.createSQLQuery(insert_sql)
						.setString(0, target)
						.setInteger(1, 1)
						.setString(2, groupid)
						.setString(3, target)
						.setString(4, groupid)
						.executeUpdate();
			t.commit();
			if(r!=0){
				result.put("state",ResultState.Z_正常.state);
				result.put("groupid", groupid);
				result.put("members",getGroupMemberList(groupid));
			}else{
				result.put("state",ResultState.Y_已在群中.state);
			}
		}
		return Response.stationary(result);
	}
	
	
	@HttpListening(urlPattern="/customer/quit_group.do",isCheckSession=true)
	public Response quitGroupHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		
		String groupid = StringUtil.getNotNull(request.getParameter("groupid")).trim();
		String quiter = StringUtil.getNotNull(request.getParameter("quiter")).trim();
		
		Map<String,Object> result = new HashMap<String,Object>();
		ObjectNode dataObjectNode = EasemobManager.joinGroup(groupid, quiter);
		
		if(dataObjectNode==null){
			result.put("state",ResultState.I_IM异常.state);
		}else if(dataObjectNode.has("statusCode") && dataObjectNode.get("statusCode").asInt()==200){
			String delete_sql = "delete from "+ClusterMember.class.getSimpleName()+" where uid=? and gid=?)";
			Session session = HibernateSessionFactory.getSession();
			Transaction t = session.beginTransaction();
			int r = session.createSQLQuery(delete_sql)
						.setString(0, quiter)
						.setString(1, groupid)
						.executeUpdate();
			t.commit();
			if(r!=0){
				result.put("state",ResultState.Z_正常.state);
				result.put("groupid", groupid);
				result.put("members",getGroupMemberList(groupid));
			}else{
				result.put("state",ResultState.Y_已在群中.state);
			}
		}
		return Response.stationary(result);
	}
	
	/**
	 * 设置群消息提醒
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 * @throws Exception
	 */
	@HttpListening(urlPattern="/customer/group_msg_alert_setting.do",isCheckSession=true)
	public Response setGroupMsgAlertHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		String groupid = StringUtil.getNotNull(request.getParameter("groupid")).trim();
		//1[开启消息提醒] | 0[关闭消息提醒]
		String isOpenMsgAlert = StringUtil.getNotNull(request.getParameter("isOpenMsgAlert")).trim();
		
		String update_sql = "update "+ClusterMember.class.getSimpleName()+" set isOpenMsgAlert=? where uid=? and gid=?)";
		Session session = HibernateSessionFactory.getSession();
		Transaction t = session.beginTransaction();
		session.createSQLQuery(update_sql).setInteger(0,"1".equals(isOpenMsgAlert)?1:0).setString(1, user.customer.uid).setString(2, groupid).executeUpdate();
		t.commit();
		return Response.stationary("{\"state\":1,\"groupid\":\""+groupid+"\"}");
	}
	
	private static List<CustomerIndexVo> getGroupMemberList(String groupid){
		List<CustomerIndexVo> rList = new ArrayList<CustomerIndexVo>();
		String query_sql = "select u.uid,phone,nickname,headIcon,sex from "+ClusterMember.class.getSimpleName()+" m left join "+Customer.class.getSimpleName()+" u on m.uid=u.uid where gid=?";
		List<Map<String,Object>> members = HibernateSessionFactory.getSession().createSQLQuery(query_sql).setString(0,groupid).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
		for (Map<String,Object> map : members) {
			CustomerIndexVo vo = new CustomerIndexVo();
			vo.uid = (String)map.get("uid");
			vo.phone = (String)map.get("phone");
			vo.nickname = (String)map.get("nickname");
			vo.headIcon = (String)map.get("headIcon");
			vo.sex = ((Number)map.get("sex")).byteValue();
			rList.add(vo);
		}
		return rList;
	}
}
