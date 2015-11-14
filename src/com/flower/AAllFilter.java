/**
 * create by 朱施健
 */
package com.flower;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guyou.util.ServletUtil;
import org.guyou.util.ServletUtil.ClientType;
import org.guyou.web.server.HeaderKeyEnum;
import org.guyou.web.servlet.AppHttpServletRequest;

/**
 * @author 朱施健
 *
 */
@WebFilter(urlPatterns = { "*.jsp","*.do" },asyncSupported = true)
public class AAllFilter implements Filter {
	
	@Override
	public void destroy() {
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		ClientType client_type = ServletUtil.getHttpClientType(req,req.getHeader(HeaderKeyEnum.USER_AGENT));
		if(client_type==ClientType.MOBILE_APPLICATION){
			req = new AppHttpServletRequest(req,resp,req.getHeader(HeaderKeyEnum.USER_SESSIONID));
		}
		chain.doFilter(req, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
