/**
 * @author 朱施健
 */
package org.guyou.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

/**
 * @author 朱施健
 * 
 */
public class MyThreadFactory implements ThreadFactory {
	private static final Logger log = Logger.getLogger(MyThreadFactory.class);
	
	private String threadPoolName;
	private Map<String, MyThread>	threadList		= new ConcurrentHashMap<String, MyThread>();
	private List<String>			threadNameList	= new ArrayList<String>();
	private String					threadBaseName;
	private AtomicLong 				atomicInteger = new AtomicLong(1);
	private ObjectFactory<?>		contextFactory = null;
	public MyThreadFactory(String threadPoolName,String threadBaseName) {
		this.threadPoolName = threadPoolName;
		this.threadBaseName = threadBaseName;
		this.threadNameList	= new ArrayList<String>(0);
	}
	
	public MyThreadFactory(String threadPoolName,String threadBaseName, int threadCount) {
		this.threadPoolName = threadPoolName;
		this.threadBaseName = threadBaseName;
		this.threadNameList	= new ArrayList<String>(threadCount);
		for ( int i = 1 ; i <= threadCount ; i++ ) {
			threadNameList.add(this.threadBaseName +"-"+ i);
		}
	}
	
	public MyThreadFactory(String threadPoolName,String threadBaseName, int threadCount,ObjectFactory<?> contextFactory) {
		this(threadPoolName,threadBaseName, threadCount);
		this.contextFactory = contextFactory;
	}

	@Override
	public Thread newThread(Runnable r) {
		ThreadGroup tg = new ThreadGroup("ThreadGroup") {
			@Override
			public void uncaughtException(Thread t, Throwable throwable) {
				log.error("Thread \"" + t.getName() + "\" died, exception was:", throwable);
				((MyThread)t).threadContext = null;
				threadList.remove(t.getName());
			}
		};
		String name = "";
		if(threadNameList.size()>0){
			for ( String tn : threadNameList ) {
				if ( !threadList.containsKey(tn) ) {
					name = tn;
					break;
				}
			}
		}else{
			name = this.threadBaseName +"-"+ atomicInteger.getAndIncrement();
		}
		MyThread t = new MyThread(tg, r, name);
		if(this.contextFactory!=null){
			t.threadContext = this.contextFactory.createElement();
		}
		t.threadPoolName = threadPoolName;
		threadList.put(t.getName(), t);
		return t;
	}

	public Collection<MyThread> getThreadList() {
		return threadList.values();
	}
	
	public String getThreadPoolName(){
		return this.threadPoolName;
	}
}
