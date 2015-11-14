/**
 * create by 朱施健
 */
package com.flower.admin;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guyou.web.server.ServletContextKeyEnum;
import org.guyou.web.server.SessionKeyEnum;

import com.flower.tables.Administrator;

/**
 * @author 朱施健
 *
 */
@WebFilter(urlPatterns = { "/admin/*" },asyncSupported = true)
public class AdminFilter implements Filter {

	private Set<String> CHECK_SESSION_URI_LIST;
	
	private static final String NO_FILTER_PATH_1 = "/admin/admin-login.jsp";
	private static final String NO_FILTER_PATH_2 = "/admin/login.do";
	

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		CHECK_SESSION_URI_LIST = (Set<String>) arg0.getServletContext().getAttribute(ServletContextKeyEnum.CHECK_SESSION_URI_LIST);
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		if((uri.endsWith(".jsp") || uri.endsWith(".do")) 
				&& CHECK_SESSION_URI_LIST.contains(uri)){
			Administrator admin = (Administrator) req.getSession().getAttribute(SessionKeyEnum.ADMIN_USER_DATA);
			if(admin==null){
				resp.sendRedirect(req.getContextPath()+NO_FILTER_PATH_1);
			}else{
				chain.doFilter(request, response);
			}
		}else{
			chain.doFilter(request, response);
		}
	}
}
