/**
 * 
 */
package org.guyou.web.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * @author 朱施健
 *
 */
@WebServlet(urlPatterns = {"*.do"}, asyncSupported = true)
public class BaseHttpServlet extends HttpServlet {

	private static final Logger log = Logger.getRootLogger();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doServlet(request,response,true);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doServlet(request,response,false);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param isGet
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doServlet(HttpServletRequest request, HttpServletResponse response,boolean isGet)
			throws ServletException, IOException {
//		<Connector port="8080" protocol="HTTP/1.1"
//        connectionTimeout="20000"
//        redirectPort="8443"
//        URIEncoding="UTF-8" />
//		<Connector port="8009" protocol="AJP/1.3" redirectPort="8443" URIEncoding="UTF-8" />
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		request.setAttribute(RequestKeyEnum.REQUEST_TRIGGER_TIME, System.nanoTime());
		HttpServletThreadPoolExcutorContainer.execute(new HttpEvent(request.getServletPath(), request));
	}
}
