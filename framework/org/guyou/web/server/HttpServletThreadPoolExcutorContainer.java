/**
 * create by 朱施健
 */
package org.guyou.web.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


import org.apache.log4j.Logger;
import org.guyou.event.Event;
import org.guyou.event.EventDispatcher;
import org.guyou.event.IEventHandler;
import org.guyou.util.ClassUtil;
import org.guyou.util.DynamicCompileUtil;
import org.guyou.util.DynamicCompileUtil.DynamicEngine;
import org.guyou.util.FileUtil;
import org.guyou.util.MyThread;
import org.guyou.util.MyThreadPoolExecutorContainer;
import org.guyou.util.MyThreadPoolExecutorContainer.ThreadPoolConfig;
import org.guyou.util.ServletUtil;
import org.guyou.util.SystemUtil;
import org.guyou.web.server.job.JobWorker;
import org.guyou.web.servlet.AppHttpServletRequest;

/**
 * @author 朱施健
 *
 */
public class HttpServletThreadPoolExcutorContainer {
	
	private static final Logger log = Logger.getLogger(HttpServletThreadPoolExcutorContainer.class);
	
	public static enum ThreadPoolType{
		TIMER_THREADPOOL,
		LOGIC_THREADPOOL,
		;
	}
	
	//线程池容器
	private static MyThreadPoolExecutorContainer<Event>  _threadPoolContainer;
	private static ServletContext _servletContext;
	
	private static AsyncListener _asyncListener = new AsyncListener() {
		public void onComplete(AsyncEvent arg0) throws IOException {
			HttpServletRequest req = (HttpServletRequest) arg0.getSuppliedRequest();
			long t = System.nanoTime();
			String url = req.getRequestURI();
			long excuteTime = (long) req.getAttribute(RequestKeyEnum.REQUEST_EXCUTE_TIME);
			long triggerTime = (long) req.getAttribute(RequestKeyEnum.REQUEST_TRIGGER_TIME);
			arg0.getSuppliedResponse().getWriter().close();
			log.error(url+"  总耗时:"+(t-triggerTime)/1000000.0+"毫秒   执行耗时:"+(t-excuteTime)/1000000.0+"毫秒");
			if(req instanceof AppHttpServletRequest){
				((AppHttpServletRequest)req).destroy();
			}
		}
		public void onError(AsyncEvent arg0) throws IOException {
			log.error("<"+ServletUtil.getUrl((HttpServletRequest)arg0.getSuppliedRequest())+">异步处理异常");
		}
		public void onStartAsync(AsyncEvent arg0) throws IOException {
		}
		public void onTimeout(AsyncEvent arg0) throws IOException {
			log.error("<"+ServletUtil.getUrl((HttpServletRequest)arg0.getSuppliedRequest())+">异步处理超时");
		}
	};
	
	private static IEventHandler<Event> _doNothingHandler = new AbstractHttpHandler() {
		@Override
		public Response excuteAction(HttpServletRequest request,
				HttpServletResponse response, PrintWriter out) throws Exception{
			response.setStatus(404);
			out.write("HTTP Status 404");
			log.error(request.getRequestURI() + " HTTP Status 404");
			return null;
		}
	};
	
	private HttpServletThreadPoolExcutorContainer(){}
	
	public static void configure(ServletContext context,String packages,ThreadPoolConfig... configs) throws Exception{
		_servletContext = context;
		_threadPoolContainer = MyThreadPoolExecutorContainer.addThreadPools(configs);
		_threadPoolContainer.setEventDispatcher(new EventDispatcher<Event>(){
			@Override
			public boolean dispatchEvent(Event event, boolean isRightNow) throws Exception{
				if(isRightNow){
					IEventHandler<Event> handler = getEventHandler(event.getType());
					if(handler!=null){
						long beginNano = System.nanoTime();
						handler.doExecute(event);
						long useTime = System.nanoTime() - beginNano;
					}else{
						_doNothingHandler.doExecute(event);
					}
				}else{
					_threadPoolContainer.execute(MyThread.currentThread().getThreadPoolName(),event);
				}
				return true;
			}
		});
		_servletContext.setAttribute(ServletContextKeyEnum.THREADPOOL_EXECUTOR_CONTAINER, _threadPoolContainer);
		_servletContext.setAttribute(ServletContextKeyEnum.LOGIC_THREADPOOL_NAME, ThreadPoolType.LOGIC_THREADPOOL.name());
		Set<Class<?>> allClass = ClassUtil.getClasses(packages);
		sessionListening(allClass);
		addEventListeners(allClass);
		webStartListening(allClass);
	}
	
	private static void addEventListeners(Set<Class<?>> allClass) throws Exception{
		String httpTemplate = new String(FileUtil.readFile(FileUtil.getResourceAsStream(AbstractHttpHandler.class.getPackage().getName().replace(".","/")+"/TemplateHttpHandler.java.template")),SystemUtil.DEFAULT_CHARSET);
		DynamicEngine dynamicEngine = DynamicCompileUtil.getDynamicEngine();
		
		Set<Class<?>> helperClassList = ClassUtil.getSubClassList(AbstractHttpHelper.class,allClass);
		Map<String,Object[]> urlPatternList = new HashMap<String,Object[]>();
		Set<String> checkSessionUriList = new HashSet<String>();
		for (Class<?> clazz : helperClassList) {
			try {
				AbstractHttpHelper helper = (AbstractHttpHelper) ClassUtil.newInstance(clazz);
				Method[] methods = clazz.getDeclaredMethods();
				for (Method method : methods) {
					String method_str = method.toString();
					if(!method_str.startsWith("public") || method_str.contains(" static ")){
						continue;
					}
					HttpListening e = method.getAnnotation(HttpListening.class);
					if(e!=null){
						if(!method_str.contains("Response")){
							log.error("事件监听器错误:HTTP事件监听函数必须抛出返回org.guyou.web.server.Forward对象",new IllegalStateException("事件监听器错误"));
							System.exit(1);
						}
						if(!method_str.contains("throws") || !method_str.contains("Exception")){
							log.error("事件监听器错误:HTTP事件监听函数必须抛出Exception",new IllegalStateException("事件监听器错误"));
							System.exit(1);
						}
						if(!e.urlPattern().endsWith(".do")){
							log.error("事件名称不合法:<"+e.urlPattern()+">必须以\".do\"结尾",new IllegalStateException("事件名称不合法"));
							System.exit(1);
						}
						if(urlPatternList.containsKey(e.urlPattern())){
							Object[] objs = urlPatternList.get(e.urlPattern());
							if(objs[0]==helper){
								log.error("事件名称重复:<"+e.urlPattern()+">已经在当前类"+helper.getClass().getName()+"."+objs[1]+"(HttpServletRequest request,HttpServletResponse response,PrintWriter out)方法上监听过了。",new IllegalStateException("监听重复"));
							}else{
								log.error("事件名称重复:<"+e.urlPattern()+">同时在"+helper.getClass().getName()+"."+objs[1]+"(HttpServletRequest request,HttpServletResponse response,PrintWriter out)和"+clazz.getName()+"."+method.getName()+"(HttpServletRequest request,HttpServletResponse response,PrintWriter out)监听",new IllegalStateException("监听重复"));
							}
							System.exit(1);
						}else{
							urlPatternList.put(e.urlPattern(), new Object[]{helper,method.getName()});
							String className = "HttpHandler_"+e.urlPattern().replace("/","_").replace(".", "_");
							AbstractHttpHandler handler = (AbstractHttpHandler) dynamicEngine.javaCodeToObject(AbstractHttpHandler.class.getPackage().getName()+"."+className, httpTemplate.replace("${className}", className).replace("${helper}", clazz.getName()).replace("${function}", method.getName()));
							ClassUtil.setFieldValue(handler, "helper", helper);
							_threadPoolContainer.getEventDispatcher().addEventListener(e.urlPattern(), handler);
							log.error("注册<"+e.urlPattern()+">监听成功");
							if(e.isCheckSession())checkSessionUriList.add(_servletContext.getContextPath()+e.urlPattern());
						}
					}
				}
			} catch (Exception e) {
				log.error("初始化["+clazz.getName()+"]异常!!",e);
				System.exit(1);
			}
		}
		_servletContext.setAttribute(ServletContextKeyEnum.CHECK_SESSION_URI_LIST, checkSessionUriList);
		urlPatternList.clear();
	}
	
	private static void webStartListening(Set<Class<?>> allClass) throws Exception {
		if(_threadPoolContainer.containsThreadPoolExecutor(ThreadPoolType.TIMER_THREADPOOL.name())){
			try {
				//初始化JobWorker
				Class<?> jobContainerClazz = Thread.currentThread().getContextClassLoader().loadClass(JobWorker.class.getName()+"$JobContainer");
				Object jobContainer = ClassUtil.getFieldValue(JobWorker.class, jobContainerClazz);
				if(jobContainer==null){
					Constructor<?> cst = jobContainerClazz.getDeclaredConstructor(_threadPoolContainer.getClass());
					cst.setAccessible(true);
					ClassUtil.setFieldValue(JobWorker.class, jobContainerClazz, cst.newInstance(_threadPoolContainer));
				}
			} catch (Exception e) {
				log.error("初始化JobWorker异常", e);
				System.exit(1);
			}
		}
		
		//需要初始化类的集合
		List<Class<?>> initClasses = new ArrayList<Class<?>>(ClassUtil.getAnnotationClassList(WebStartListening.class, allClass));
		Collections.sort(initClasses, new Comparator<Class<?>>(){
			@Override
			public int compare(Class<?> arg0, Class<?> arg1) {
				//1. 类型
				if(arg0.isEnum()&& !arg1.isEnum()) return 1;
				else if(!arg0.isEnum()&& arg1.isEnum()) return -1;
				
				//加载级别
				WebStartListening an0 = arg0.getAnnotation(WebStartListening.class);
				WebStartListening an1 = arg1.getAnnotation(WebStartListening.class);
				
				if(an0.load_on_startup() > an1.load_on_startup()) return 1;
				else if(an0.load_on_startup() < an1.load_on_startup()) return -1;
				else return 0;
			}
		});
		
		for (Class<?> c : initClasses) {
			log.error("开始初始化["+c.getName()+"]...");
			try {
				Class.forName(c.getName());
				Method mt = c.getDeclaredMethod("init");
				mt.setAccessible(true);
				mt.invoke(c);
			} catch (Exception e) {
				try {
					Method mt = c.getDeclaredMethod("getInstance");
					mt.setAccessible(true);
					mt.invoke(c);
				} catch (Exception e1) {
					if(!c.isEnum() && !c.isInterface()){
						try {
							c.newInstance();
						} catch (Exception e2) {
							log.error("实例化["+c.getName()+"]异常,此类没有默认构造函数", e);
							System.exit(1);
						}
					}
				}
			}
		}
	}
	
	private static void sessionListening(Set<Class<?>> allClass) throws Exception{
		Set<Class<?>> sessionListenerList = ClassUtil.getSubClassList(HttpSessionListener.class,allClass);
		if(sessionListenerList.size()==0) return ;
		final List<HttpSessionListener> listeners = new ArrayList<HttpSessionListener>(sessionListenerList.size());
		for (Class<?> clazz:sessionListenerList) {
			listeners.add((HttpSessionListener) ClassUtil.newInstance(clazz));
		}
		_threadPoolContainer.getEventDispatcher().addEventListener(SessionEvent.SESSION_EVENT_NAME, new IEventHandler<Event>(){
			public void doExecute(Event e) throws Exception {
				Object[] _session_and_Type = (Object[]) e.getSource();
				HttpSessionEvent he = new HttpSessionEvent((HttpSession)_session_and_Type[1]);
				for (HttpSessionListener listener : listeners) {
					if(((SessionEvent.SessionEventType)_session_and_Type[0])==SessionEvent.SessionEventType.created){
						listener.sessionCreated(he);
					}else{
						listener.sessionDestroyed(he);
					}
				}
			}
		});
	}
	
	public static void execute(HttpEvent event) {
		event.context.setTimeout(6000000000L);
		event.context.addListener(_asyncListener);
		_threadPoolContainer.execute(ThreadPoolType.LOGIC_THREADPOOL.name(), event);
	}
	
	public static void shutdown() {
		_servletContext.removeAttribute(ServletContextKeyEnum.THREADPOOL_EXECUTOR_CONTAINER);
		_servletContext.removeAttribute(ServletContextKeyEnum.LOGIC_THREADPOOL_NAME);
		ScheduledExecutorService cleanSessionTimer = (ScheduledExecutorService) _servletContext.getAttribute(ServletContextKeyEnum.USER_DEFINED_SESSION_CLEAN_TIMER);
		if(cleanSessionTimer!=null) {
			cleanSessionTimer.shutdown();
		}
		_servletContext.removeAttribute(ServletContextKeyEnum.USER_DEFINED_SESSION_CLEAN_TIMER);
		_threadPoolContainer.shutdownNow();
	}
}
