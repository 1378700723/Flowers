/**
 * create by 朱施健
 */
package com.flower.customer;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.guyou.web.server.AbstractHttpHelper;
import org.guyou.web.server.HttpListening;
import org.guyou.web.server.Response;
import org.guyou.web.server.SessionKeyEnum;

/**
 * @author 朱施健
 * 别名相关Helper
 */
public class CustomerFriendCircleHelper extends AbstractHttpHelper {
	
	private static final Logger log = Logger.getLogger(CustomerFriendCircleHelper.class);

	@HttpListening(urlPattern="/customer/send_to_friendcircle.do",isCheckSession=true)
	public Response checkAuthcodeHandler(HttpServletRequest request,HttpServletResponse response,PrintWriter out) throws Exception{
		request.getSession().setAttribute("zhusj", "zhusj");
		String sid = (String) request.getSession().getAttribute(SessionKeyEnum.USER_DEFINED_SESSION_ID);
		System.out.println(sid);
		return null;
	}
}
