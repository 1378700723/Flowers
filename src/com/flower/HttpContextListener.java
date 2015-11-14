/**
 * 
 */
package com.flower;

import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.guyou.util.ConfigUtil;
import org.guyou.util.MyThreadFactory;
import org.guyou.util.MyThreadPoolExecutorContainer.ThreadPoolConfig;
import org.guyou.util.MyThreadPoolExecutorContainer.ThreadPoolKindEnum;
import org.guyou.web.server.HibernateSessionFactory;
import org.guyou.web.server.HttpServletThreadPoolExcutorContainer;
import org.guyou.web.server.HttpServletThreadPoolExcutorContainer.ThreadPoolType;
import org.guyou.web.server.ServletContextKeyEnum;

/**
 * @author 朱施健
 *
 */
@WebListener
public class HttpContextListener implements ServletContextListener {

	private static final Logger log = Logger.getLogger(ServletContextListener.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		HttpServletThreadPoolExcutorContainer.shutdown();
		HibernateSessionFactory.shutdown();
		ScheduledExecutorService cleanSessionTimer = (ScheduledExecutorService) event.getServletContext().getAttribute(ServletContextKeyEnum.USER_DEFINED_SESSION_CLEAN_TIMER);
		if(cleanSessionTimer!=null) cleanSessionTimer.shutdown();
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			//初始化配置
			ConfigUtil.configure(event.getServletContext().getRealPath("/"),event.getServletContext().getRealPath("/WEB-INF/config/web-config.txt"));
			//初始化日志
			PropertyConfigurator.configure(event.getServletContext().getRealPath("/WEB-INF/config/log4j.properties"));
			//初始化Application
			Application.configure(event.getServletContext());
			//初始化数据库
			HibernateSessionFactory.configure(event.getServletContext().getRealPath("/WEB-INF/config/hibernate.cfg.xml"));
			//初始化业务线程池
			HttpServletThreadPoolExcutorContainer.configure(
					event.getServletContext(),
					"com.flower",
					new ThreadPoolConfig(ThreadPoolType.TIMER_THREADPOOL.name(),ThreadPoolKindEnum.SCHEDULE,1,0,new MyThreadFactory(ThreadPoolType.TIMER_THREADPOOL.name(),"时间线程", 1)),
					new ThreadPoolConfig(ThreadPoolType.LOGIC_THREADPOOL.name(),ThreadPoolKindEnum.FAIR,5,5,new MyThreadFactory(ThreadPoolType.LOGIC_THREADPOOL.name(),"逻辑线程", 5))
			);
		} catch (Exception e) {
			log.error("初始化Servlet容器异常",e);
		}
	}
}
