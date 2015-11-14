/**
 * create by 朱施健
 */
package org.guyou.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.guyou.util.StringUtil;
import org.guyou.web.server.ServletContextKeyEnum;

/**
 * @author 朱施健
 *
 */
public final class AppHttpServletRequest implements HttpServletRequest {
	
	private static final Map<String, AppHttpSession> _sessions = new ConcurrentHashMap<String, AppHttpSession>();
	
	private static boolean _isStartCleanSessionTask = false;
	
	private HttpServletRequest _request;
	private HttpServletResponse _response;
	private AppHttpSession _session = null;
	private String _clientSessionId = null;
	
	public AppHttpServletRequest(HttpServletRequest request,HttpServletResponse response,String clientSessionId){
		this._request = request;
		this._response = response;
		this._clientSessionId = clientSessionId;
	}

	@Override
	public AsyncContext getAsyncContext() {
		return _request.getAsyncContext();
	}

	@Override
	public Object getAttribute(String arg0) {
		return _request.getAttribute(arg0);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return _request.getAttributeNames();
	}

	@Override
	public String getCharacterEncoding() {
		return _request.getCharacterEncoding();
	}

	@Override
	public int getContentLength() {
		return _request.getContentLength();
	}

	@Override
	public String getContentType() {
		return _request.getContentType();
	}

	@Override
	public DispatcherType getDispatcherType() {
		return _request.getDispatcherType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return _request.getInputStream();
	}

	@Override
	public String getLocalAddr() {
		return _request.getLocalAddr();
	}

	@Override
	public String getLocalName() {
		return _request.getLocalName();
	}

	@Override
	public int getLocalPort() {
		return _request.getLocalPort();
	}

	@Override
	public Locale getLocale() {
		return _request.getLocale();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return _request.getLocales();
	}

	@Override
	public String getParameter(String arg0) {
		return _request.getParameter(arg0);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _request.getParameterMap();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return _request.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return _request.getParameterValues(arg0);
	}

	@Override
	public String getProtocol() {
		return _request.getProtocol();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return _request.getReader();
	}

	@Override
	@Deprecated
	public String getRealPath(String arg0) {
		return _request.getRealPath(arg0);
	}

	@Override
	public String getRemoteAddr() {
		return _request.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return _request.getRemoteHost();
	}

	@Override
	public int getRemotePort() {
		return _request.getRemotePort();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return _request.getRequestDispatcher(arg0);
	}

	@Override
	public String getScheme() {
		return _request.getScheme();
	}

	@Override
	public String getServerName() {
		return _request.getServerName();
	}

	@Override
	public int getServerPort() {
		return _request.getServerPort();
	}

	@Override
	public ServletContext getServletContext() {
		return _request.getServletContext();
	}

	@Override
	public boolean isAsyncStarted() {
		return _request.isAsyncStarted();
	}

	@Override
	public boolean isAsyncSupported() {
		return _request.isAsyncSupported();
	}

	@Override
	public boolean isSecure() {
		return _request.isSecure();
	}

	@Override
	public void removeAttribute(String arg0) {
		_request.removeAttribute(arg0);
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		_request.setAttribute(arg0, arg1);
	}

	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		_request.setCharacterEncoding(arg0);
	}

	@Override
	public AsyncContext startAsync() {
		return _request.startAsync(this, this._response);
	}

	@Override
	public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) {
		return _request.startAsync(arg0,arg1);
	}

	@Override
	public boolean authenticate(HttpServletResponse arg0) throws IOException,
			ServletException {
		
		return false;
	}

	@Override
	public String getAuthType() {
		return _request.getAuthType();
	}

	@Override
	public String getContextPath() {
		return _request.getContextPath();
	}

	@Override
	public Cookie[] getCookies() {
		return _request.getCookies();
	}

	@Override
	public long getDateHeader(String arg0) {
		return _request.getDateHeader(arg0);
	}

	@Override
	public String getHeader(String arg0) {
		return _request.getHeader(arg0);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return _request.getHeaderNames();
	}

	@Override
	public Enumeration<String> getHeaders(String arg0) {
		return _request.getHeaders(arg0);
	}

	@Override
	public int getIntHeader(String arg0) {
		return _request.getIntHeader(arg0);
	}

	@Override
	public String getMethod() {
		return _request.getMethod();
	}

	@Override
	public Part getPart(String arg0) throws IOException, IllegalStateException,
			ServletException {
		return _request.getPart(arg0);
	}

	@Override
	public Collection<Part> getParts() throws IOException,
			IllegalStateException, ServletException {
		return _request.getParts();
	}

	@Override
	public String getPathInfo() {
		return _request.getPathInfo();
	}

	@Override
	public String getPathTranslated() {
		return _request.getPathTranslated();
	}

	@Override
	public String getQueryString() {
		return _request.getQueryString();
	}

	@Override
	public String getRemoteUser() {
		return _request.getRemoteUser();
	}

	@Override
	public String getRequestURI() {
		return _request.getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL() {
		return _request.getRequestURL();
	}

	@Override
	public String getRequestedSessionId() {
		return _request.getRequestedSessionId();
	}

	@Override
	public String getServletPath() {
		return _request.getServletPath();
	}

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	@Override
	public HttpSession getSession(boolean arg0) {
		if(_session==null){
			boolean isNull = StringUtil.isNullValue(this._clientSessionId);
			_session = isNull ? null :_sessions.get(this._clientSessionId);
			if(arg0){
				if(_session==null){
					if(isNull){
						_session = new AppHttpSession(this._request,null);
						this._clientSessionId = _session.getId();
					}else{
						_session = new AppHttpSession(this._request,this._clientSessionId);
					}
					_sessions.put(_session.getId(), _session);
				}
				if(!_isStartCleanSessionTask){
					_startCleanSessionTask(this.getServletContext());
					_isStartCleanSessionTask = true;
				}
			}
		}
		return _session;
	}
	
	private static void _startCleanSessionTask(ServletContext servletContext){
		ScheduledExecutorService timer = Executors.newScheduledThreadPool(1, new ThreadFactory() {
			public Thread newThread(Runnable r) {
				return new Thread(r,"AppSession-Clean-Task");
			}
		});
		timer.scheduleAtFixedRate(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				for (Iterator<AppHttpSession> iterator = _sessions.values().iterator(); iterator.hasNext();) {
					AppHttpSession as = iterator.next();
					if(as.isTimeOut(now)){
						iterator.remove();
						as.dispatchDestroyedEvent();
					}
				}
			}
		}, AppHttpSession.TIME_OUT/2,AppHttpSession.TIME_OUT/2,TimeUnit.MILLISECONDS);
		servletContext.setAttribute(ServletContextKeyEnum.USER_DEFINED_SESSION_CLEAN_TIMER, timer);
	}

	@Override
	public Principal getUserPrincipal() {
		return _request.getUserPrincipal();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return _request.isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return _request.isRequestedSessionIdFromURL();
	}

	@Override
	@Deprecated
	public boolean isRequestedSessionIdFromUrl() {
		return _request.isRequestedSessionIdFromUrl();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return _request.isRequestedSessionIdValid();
	}

	@Override
	public boolean isUserInRole(String arg0) {
		return _request.isUserInRole(arg0);
	}

	@Override
	public void login(String arg0, String arg1) throws ServletException {
		_request.login(arg0,arg1);
	}

	@Override
	public void logout() throws ServletException {
		_request.logout();
	}
	
	public void destroy(){
		this._request = null;
		this._response = null;
		this._session = null;
		this._clientSessionId = null;
	}
}
