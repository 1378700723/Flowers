/**
 * create by 朱施健
 */
package org.guyou.web.server;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.guyou.event.Event;
import org.guyou.util.MathUtil;

/**
 * @author 朱施健
 *
 */
public class HttpEvent implements Event {
	
	public final String urlPattern;
	public final AsyncContext context;
	
	public HttpEvent(String urlPattern,HttpServletRequest request){
		this.urlPattern = urlPattern;
		this.context = request.startAsync();
	}
	
	@Override
	public int hashCode(){
		HttpSession s = ((HttpServletRequest)context.getRequest()).getSession(false);
		if(s==null) return MathUtil.nextInt();
		return s.getId().hashCode();
	}
	
	@Override
	public AsyncContext getSource() {
		return context;
	}

	@Override
	public String getType() {
		return urlPattern;
	}
	
	@Override
	public void run() {}
}
