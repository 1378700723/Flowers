/**
 * create by 朱施健
 */
package com.flower.customer;

import java.io.PrintWriter;
import java.util.List;

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

import com.flower.customer.beans.CustomerUser;
import com.flower.enums.ResultState;
import com.flower.tables.Alias;

/**
 * @author 朱施健
 * 别名相关Helper
 */
public class CustomerAliasHelper extends AbstractHttpHelper {
	
	private static final Logger log = Logger.getLogger(CustomerAliasHelper.class);
	
	/**
	 * 别名列表
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 * @throws Exception
	 */
	@HttpListening(urlPattern="/customer/alias_list.do",isCheckSession=true)
	public Response aliasListHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		List<Alias> list = HibernateSessionFactory.getSession().createQuery("from "+Alias.class.getName()+" where uid=?").setString(0,user.customer.uid).list();
		return Response.stationary(list);
	}
	
	/**
	 * 修改别名
	 * @param request
	 * @param response
	 * @param out
	 * @return
	 * @throws Exception
	 */
	@HttpListening(urlPattern="/customer/modify_alias.do",isCheckSession=true)
	public Response modifyAliasHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		CustomerUser user = (CustomerUser) request.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
		String dstUid = StringUtil.getNotNull(request.getParameter("dstUid")).trim();
		String alias = StringUtil.getNotNull(request.getParameter("alias")).trim();
		//是否删除别名
		boolean isDelete = "".equals(alias);
		Session session = HibernateSessionFactory.getSession();
		Transaction t = session.beginTransaction();
		if(isDelete){
			session.createSQLQuery("delete from "+Alias.class.getSimpleName()+" where (uid,dstUid)=(?,?)")
				.setString(0,user.customer.uid)
				.setString(1, dstUid)
				.executeUpdate();
		}else{
			session.createSQLQuery("update "+Alias.class.getSimpleName()+" set alias=? where (uid,dstUid)=(?,?)")
				.setString(0,alias)
				.setString(1,user.customer.uid)
				.setString(2, dstUid)
				.executeUpdate();
		}
		t.commit();
		return Response.stationary("{\"state\":"+ResultState.Z_正常.state+"}");
	}
}
