/**
 * create by 朱施健
 */
package com.flower;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.guyou.web.server.SessionKeyEnum;
import org.guyou.web.servlet.AppHttpSession;

import com.flower.customer.beans.CustomerUser;

/**
 * @author 朱施健
 *
 */
@WebListener
public class AppSessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		HttpSession session = arg0.getSession();
		if(session instanceof AppHttpSession){
			CustomerUser user = (CustomerUser) session.getAttribute(SessionKeyEnum.CUSTOMER_USER_DATA);
			if(user==null){
			}
			((AppHttpSession)session).destroyed();
		}
	}
}
