/**
 * create by 朱施健
 */
package com.flower.admin;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;
import org.guyou.web.server.SessionKeyEnum;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.flower.enums.ResultState;
import com.flower.tables.Administrator;

/**
 * @author 朱施健
 *
 */
public class AdminLoginHelper extends AbstractHttpHelper {
	
	private static final Logger log = Logger.getLogger(AdminLoginHelper.class);
	
	@HttpListening(urlPattern="/admin/login.do")
	public Response loginHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		
		String username = request.getParameter("username");
		String passwd = request.getParameter("passwd");
		Session session = HibernateSessionFactory.getSession();
		Administrator user = (Administrator) session.get(Administrator.class,username);
		//用户不存在
		if(user==null){
			result.put("state", ResultState.Y_用户不存在.state);
		}
		//密码错误
		else if(!user.getPasswd().equals(passwd)){
			result.put("state", ResultState.M_密码错误.state);
		}
		//成功
		else {
			request.getSession().setAttribute(SessionKeyEnum.ADMIN_USER_DATA, user);
			result.put("state", ResultState.Z_正常.state);
		}
		return Response.stationary(result);
	}
	@HttpListening(urlPattern="/admin/editPwd.do",isCheckSession=true)
	public Response editPwd(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		String original_pwd=request.getParameter("original_pwd");
		String new_pwd=request.getParameter("new_pwd");
		
		Administrator user =(Administrator)request.getSession().getAttribute(SessionKeyEnum.ADMIN_USER_DATA);
		String passwd = user.getPasswd();
		
		if(!passwd.equals(original_pwd)){
			result.put("state", 0);
		}else{
			log.info("开始修改密码"+user);
			Session session = HibernateSessionFactory.getSession();
			user.setPasswd(new_pwd);
			Transaction t = session.beginTransaction();
			session.update(user);
			t.commit();
			result.put("state", 1);
			log.info("修改密码结束");
		}
		return Response.stationary(result);
	}
}
