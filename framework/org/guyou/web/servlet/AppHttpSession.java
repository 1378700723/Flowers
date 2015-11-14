/**
 * create by 朱施健
 */
package org.guyou.web.servlet;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.log4j.Logger;
import org.guyou.event.Event;
import org.guyou.util.MyThreadPoolExecutorContainer;
import org.guyou.util.StringUtil;
import org.guyou.web.server.ServletContextKeyEnum;
import org.guyou.web.server.SessionEvent;

/**
 * @author 朱施健
 *
 */
public final class AppHttpSession implements HttpSession {

	private static final Logger log = Logger.getLogger(AppHttpSession.class);
	
	// 超时时间(秒)
	public final static long TIME_OUT = 10 * 60 * 1000L;

	private final static Sequence _sessionSeq = new Sequence();
	private static ServletContext _servletContext = null;
	
	private String _sessionid;
	private long _creationTime;
	private long _timeout;
	private Map<String, Object> _attributes = new ConcurrentHashMap<String, Object>();
	private long _lastVisitTime;

	AppHttpSession(HttpServletRequest request,String clientSessionId){
		_sessionid = StringUtil.isNullValue(clientSessionId) ? _sessionSeq.getSeq() : clientSessionId;
		long now = System.currentTimeMillis();
		_creationTime = now;
		_timeout = TIME_OUT;
		visit(now);
		if(_servletContext==null){
			_servletContext = request.getServletContext();
		}
		dispatchCreatedEvent();
	}
	
	void visit(long time){
		this._lastVisitTime = time;
	}
	
	@Override
	public Object getAttribute(String arg0) {
		visit(System.currentTimeMillis());
		return _attributes.get(arg0);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		visit(System.currentTimeMillis());
		return new Enumeration<String>() {
			Iterator<String> ____it = _attributes.keySet().iterator();

			@Override
			public boolean hasMoreElements() {
				return ____it.hasNext();
			}

			@Override
			public String nextElement() {
				return ____it.next();
			}
		};
	}

	@Override
	public long getCreationTime() {
		visit(System.currentTimeMillis());
		return _creationTime;
	}

	@Override
	public String getId() {
		visit(System.currentTimeMillis());
		return _sessionid;
	}

	@Override
	public long getLastAccessedTime() {
		visit(System.currentTimeMillis());
		return _lastVisitTime;
	}

	@Override
	public int getMaxInactiveInterval() {
		throw new IllegalStateException("此方法在["+this.getClass().getName()+"]中无效");
	}

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	@Deprecated
	public HttpSessionContext getSessionContext() {
		throw new IllegalStateException("此方法在["+this.getClass().getName()+"]中无效");
	}

	@Override
	public Object getValue(String arg0) {
		throw new IllegalStateException("此方法在["+this.getClass().getName()+"]中无效");
	}

	@Override
	public String[] getValueNames() {
		throw new IllegalStateException("此方法在["+this.getClass().getName()+"]中无效");
	}

	@Override
	public void invalidate() {
		throw new IllegalStateException("此方法在["+this.getClass().getName()+"]中无效");
	}

	@Override
	public boolean isNew() {
		throw new IllegalStateException("此方法在["+this.getClass().getName()+"]中无效");
	}

	@Override
	public void putValue(String arg0, Object arg1) {
		throw new IllegalStateException("此方法在["+this.getClass().getName()+"]中无效");
	}

	@Override
	public void removeAttribute(String arg0) {
		visit(System.currentTimeMillis());
		_attributes.remove(arg0);
	}

	@Override
	public void removeValue(String arg0) {
		throw new IllegalStateException("此方法在["+this.getClass().getName()+"]中无效");
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		visit(System.currentTimeMillis());
		_attributes.put(arg0, arg1);
	}

	@Override
	public void setMaxInactiveInterval(int arg0) {
		_timeout = arg0 * 1000L;
	}
	
	boolean isTimeOut(long now){
		return _lastVisitTime + _timeout < now ;
	}
	
	void dispatchCreatedEvent(){
		MyThreadPoolExecutorContainer<Event> executorContainer = (MyThreadPoolExecutorContainer<Event>) _servletContext.getAttribute(ServletContextKeyEnum.THREADPOOL_EXECUTOR_CONTAINER);
		try {
			executorContainer.getEventDispatcher().dispatchEvent(new SessionEvent(SessionEvent.SessionEventType.created,this), true);
		} catch (Exception e) {
			log.error("派发AppSession创建事件异常",e);
		}
	}

	void dispatchDestroyedEvent() {
		MyThreadPoolExecutorContainer<Event> executorContainer = (MyThreadPoolExecutorContainer<Event>) _servletContext.getAttribute(ServletContextKeyEnum.THREADPOOL_EXECUTOR_CONTAINER);
		try {
			executorContainer.execute((String)_servletContext.getAttribute(ServletContextKeyEnum.LOGIC_THREADPOOL_NAME), new SessionEvent(SessionEvent.SessionEventType.created,this));
		} catch (Exception e) {
			log.error("派发AppSession销毁事件异常",e);
		}
	}
	
	public void destroyed(){
		this._sessionid = null;
		this._attributes.clear();
		this._attributes = null;
	}
	
	private static class Sequence{
		private static final int last = 9999;
		
		int __seq;
		StringBuilder __buf = new StringBuilder();
		
		String getSeq(){
			String str = null;
			synchronized (this) {
				if (__seq >= last) {
					__seq = 0;
				}
				__seq +=1;
				__buf.delete(0, __buf.length());
				__buf.append("appsessionid").append(System.currentTimeMillis()).append(Integer.toString(__seq));
				str = __buf.toString();
			}
			return str;
		}
	}
}
