/**
 * create by 朱施健
 */
package com.flower.customer;

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

import com.flower.customer.beans.CustomerUser;
import com.flower.enums.ResultState;

/**
 * @author 朱施健
 *
 */
@WebFilter(urlPatterns = {"/customer/*" },asyncSupported = true)
public class CustomerFilter implements Filter {
	
	private Set<String> CHECK_SESSION_URI_LIST;
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		CHECK_SESSION_URI_LIST = (Set<String>) arg0.getServletContext().getAttribute(ServletContextKeyEnum.CHECK_SESSION_URI_LIST);
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		if((uri.endsWith(".jsp") || uri.endsWith(".do")) 
				&& CHECK_SESSION_URI_LIST.contains(uri)){
			CustomerUser user = (CustomerUser) req.getSession().getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
			if(user==null){
				resp.getWriter().write("{\"state\":"+ResultState.H_会话超时.state+"}");
				resp.getWriter().close();
			}else{
				chain.doFilter(request, response);
			}
		}else{
			chain.doFilter(request, response);
		}
	}
	
	@Override
	public void destroy() {
	}
}
