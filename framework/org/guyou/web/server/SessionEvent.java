/**
 * create by 朱施健
 */
package org.guyou.web.server;

import javax.servlet.http.HttpSession;

import org.guyou.event.Event;

/**
 * @author 朱施健
 *
 */
public class SessionEvent implements Event {
	
	public static final String SESSION_EVENT_NAME = "SESSION_EVENT_NAME_"+System.currentTimeMillis();
	
	private final Object[] _session_and_Type;
	
	public SessionEvent(SessionEventType type,HttpSession session){
		_session_and_Type = new Object[]{type,session};
	}
	
	@Override
	public int hashCode(){
		return ((HttpSession)_session_and_Type[1]).getId().hashCode();
	}
	
	@Override
	public Object[] getSource() {
		return _session_and_Type;
	}

	@Override
	public String getType() {
		return SESSION_EVENT_NAME;
	}
	
	@Override
	public void run() {}
	
	public static enum SessionEventType{
		created,destroyed;
	}
}
