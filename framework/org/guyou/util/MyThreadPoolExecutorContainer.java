/**
 * @author 朱施健
 */
package org.guyou.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.guyou.event.Event;
import org.guyou.event.EventDispatcher;

/**
 * @author 朱施健
 * 
 */
public class MyThreadPoolExecutorContainer<E extends Event>{
	
	private static Logger log = Logger.getLogger(MyThreadPoolExecutorContainer.class);
	
	private static MyThreadPoolExecutorContainer<? extends Event> _instance = null;
	private Map<String,ThreadPoolExecutor> _poolList = new HashMap<String, ThreadPoolExecutor>();
	private EventDispatcher<Event> _eventDispatcher = null;
	private TerminatedHandler _terminatedHandler = null;
	private volatile int shutdownCount;
	private String _userDefinedThreadPoolName;
	
	private static MyThreadPoolExecutorContainer<? extends Event> getInstance(){
		if(_instance==null){
			_instance = new MyThreadPoolExecutorContainer();
		}
		return _instance;
	}
	
	public static MyThreadPoolExecutorContainer addThreadPool(ThreadPoolConfig config){
		MyThreadPoolExecutorContainer<? extends Event> ec = getInstance();
		ec.initThreadPool(config);
		return ec;
	}
	
	public static MyThreadPoolExecutorContainer addThreadPools(ThreadPoolConfig[] configs){
		MyThreadPoolExecutorContainer<? extends Event> ec = getInstance();
		for ( ThreadPoolConfig config : configs ) {
			ec.initThreadPool(config);
		}
		return ec;
	}
	
	public void setEventDispatcher(EventDispatcher<Event> dispatcher){
		if(this._eventDispatcher==null){
			this._eventDispatcher = dispatcher;
		}
	}
	
	public void addTerminatedHandler(TerminatedHandler handler){
		this._terminatedHandler = handler;
	}
	
	public EventDispatcher<Event> getEventDispatcher(){
		return this._eventDispatcher;
	}
	
	public void addUserDefinedThreadPoolName(String userDefinedThreadPoolName){
		this._userDefinedThreadPoolName = userDefinedThreadPoolName;
	}

	private void initThreadPool(ThreadPoolConfig config){
		if(config.threadFactory == null) config.threadFactory = Executors.defaultThreadFactory();
		//定时线程池
		if(config.kind==ThreadPoolKindEnum.SCHEDULE){
			_poolList.put(config.threadPoolName,new ScheduledThreadPoolExecutor(config.corePoolSize,config.threadFactory));
		}
		//非公平线程池
		else if(config.kind==ThreadPoolKindEnum.UNFAIR || config.maximumPoolSize==1){
			if(config.corePoolSize == config.maximumPoolSize){
				_poolList.put(config.threadPoolName,new ThreadPoolExecutor(config.corePoolSize, config.corePoolSize,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),config.threadFactory){
					@Override
					protected void beforeExecute(Thread t, Runnable r) {
						_instance.beforeExecuteInside(t, r);
					}
					@Override
					protected void afterExecute(Runnable r, Throwable t) { 
						_instance.afterExecuteInside(r, t);
					}
					@Override
					protected void terminated() {
						_instance.shutdownCount += 1;
						if(_instance.shutdownCount == _instance.size()){
							_instance.terminated();
						}
					}
				});
			}else{
				_poolList.put(config.threadPoolName,new ThreadPoolExecutor(config.corePoolSize,config.maximumPoolSize,20L,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(5000),config.threadFactory,new AbortPolicy()){
					@Override
					protected void beforeExecute(Thread t, Runnable r) {
						_instance.beforeExecuteInside(t, r);
					}
					@Override
					protected void afterExecute(Runnable r, Throwable t) { 
						_instance.afterExecuteInside(r, t);
					}
					@Override
					protected void terminated() {
						_instance.shutdownCount += 1;
						if(_instance.shutdownCount == _instance.size()){
							_instance.terminated();
						}
					}
				});
			}
		}
		//公平线程池
		else{
			if(config.corePoolSize == config.maximumPoolSize){
				_poolList.put(config.threadPoolName,new FairThreadPoolExecutor(512,config.corePoolSize, config.corePoolSize,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),config.threadFactory){
					@Override
					protected void beforeExecute(Thread t, Runnable r) {
						super.beforeExecute(t, r);
						_instance.beforeExecuteInside(t, r);
					}
					@Override
					protected void afterExecute(Runnable r, Throwable t) {
						super.afterExecute(r, t);
						_instance.afterExecuteInside(r, t);
					}
					@Override
					protected void terminated() {
						_instance.shutdownCount += 1;
						if(_instance.shutdownCount == _instance.size()){
							_instance.terminated();
						}
					}
				});
			}else{
				_poolList.put(config.threadPoolName,new FairThreadPoolExecutor(512,config.corePoolSize,config.maximumPoolSize,20L,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(5000),config.threadFactory,new AbortPolicy()){
					@Override
					protected void beforeExecute(Thread t, Runnable r) {
						super.beforeExecute(t, r);
						_instance.beforeExecuteInside(t, r);
					}
					@Override
					protected void afterExecute(Runnable r, Throwable t) { 
						super.afterExecute(r, t);
						_instance.afterExecuteInside(r, t);
					}
					@Override
					protected void terminated() {
						_instance.shutdownCount += 1;
						if(_instance.shutdownCount == _instance.size()){
							_instance.terminated();
						}
					}
				});
			}
		}
	}
	
	public void execute(String threadPoolName,E command) {
		ExecutorService service = getThreadPoolExecutor(threadPoolName);
		if(service!=null){
			service.execute(new AbstractEvent(command,_eventDispatcher));
		}
	}
	
	private void beforeExecuteInside(Thread t, Runnable r) {
		beforeExecute(t,(E)r);
	}
	
	protected void beforeExecute(Thread t, E r) {
	
	}
	
	private void afterExecuteInside(Runnable r, Throwable t) {
		afterExecute((E)r,t);
	}
	
	protected void afterExecute(E r, Throwable t) {
		
	}
	
	public int size(){
		return _poolList.size();
	}
	
	public ThreadPoolExecutor getThreadPoolExecutor(String threadPoolName){
		return _poolList.get(threadPoolName);
	}
	
	public Map<String,ThreadPoolExecutor> getThreadPoolExecutorList(){
		return _poolList;
	}
	
	public boolean containsThreadPoolExecutor(String threadPoolName){
		return _poolList.containsKey(threadPoolName);
	}
	
	public void removeThreadPoolExecutor(String threadPoolName){
		ExecutorService service = _poolList.get(threadPoolName);
		if(service!=null){
			service.shutdownNow();
		}
	}

	public synchronized void shutdown() {
		for ( ExecutorService pool : _poolList.values()) {
			pool.shutdown();
		}
	}
	
	public synchronized List<E> shutdownNow() {
		List<E> list = new ArrayList<E>();
		for ( ExecutorService pool : _poolList.values() ) {
			for ( Runnable r : pool.shutdownNow() ) {
				list.add((E)r);
			}
		}
		return list;
	}
	
	protected synchronized void terminated() {
		if(_terminatedHandler!=null){
			_terminatedHandler.destroy();
		}
	}
	
	/**
	 * 线程类型
	 * @author 朱施健
	 *
	 */
	public static enum ThreadPoolKindEnum{
		//定时线程池
		SCHEDULE,
		//公平线程池
		FAIR,
		//非公平线程池
		UNFAIR;
	}
	
	/**
	 * 线程池配置
	 * @author 朱施健
	 *
	 */
	public static class ThreadPoolConfig {
		public String 					threadPoolName;
		public ThreadPoolKindEnum 		kind;
		public int						corePoolSize;
		public int						maximumPoolSize;
		public ThreadFactory 			threadFactory;

		public ThreadPoolConfig() {
		}
		
		public ThreadPoolConfig(String threadPoolName,ThreadPoolKindEnum kind) {
			this.threadPoolName = threadPoolName;
			this.kind = kind;
		}

		public ThreadPoolConfig(String threadPoolName,ThreadPoolKindEnum kind,int corePoolSize, int maximumPoolSize,ThreadFactory threadFactory) {
			this.threadPoolName = threadPoolName;
			this.kind = kind;
			this.corePoolSize = corePoolSize;
			this.maximumPoolSize = maximumPoolSize;
			this.threadFactory = threadFactory;
		}
	}
	
	/**
	 * 公平线程池
	 * @author 朱施健
	 *
	 */
	private static class FairThreadPoolExecutor extends ThreadPoolExecutor{
		private LinkedBlockingList<Runnable>[] _serials; 
		
		public FairThreadPoolExecutor(int concurrencyLevel,int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
			initQueueArray(concurrencyLevel);
		}
		public FairThreadPoolExecutor(int concurrencyLevel,int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
				RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
			initQueueArray(concurrencyLevel);
		}
		
		private void initQueueArray(int concurrencyLevel){
			if (concurrencyLevel < 0 || concurrencyLevel > 0x40000000)
				throw new IllegalArgumentException("Illegal concurrencyLevel: " + concurrencyLevel);
			int capacity = 1;
			while (capacity < concurrencyLevel){
				capacity <<= 1;
			}
			this._serials = new LinkedBlockingList[capacity];
			for (int i = 0; i < capacity; ++i){
				this._serials[i] = new LinkedBlockingList<Runnable>();
			}
		}
		
		@Override
		public void execute(Runnable command) {
			LinkedBlockingList<Runnable> queue = this._serials[indexFor(hash(command.hashCode()), this._serials.length)];
			queue.lock.lock();
			try {
				queue.offer(command);
				if(queue.size()==1){
					Runnable event = queue.peek();
					super.execute(event);
					queue.isDoingEvent = System.identityHashCode(event);
				}
			} finally{
				queue.lock.unlock();
			}
		}
		
		@Override
		protected void afterExecute(Runnable r, Throwable t) { 
			LinkedBlockingList<Runnable> queue = this._serials[indexFor(hash(r.hashCode()), this._serials.length)];
			assert( r == queue.peek() );
			queue.lock.lock();
			try {
				queue.poll();
				queue.isDoingEvent = 0;
				if(queue.size()>=1){
					Runnable event = queue.peek();
					super.execute(event);
					queue.isDoingEvent = System.identityHashCode(event);
				}
			} finally{
				queue.lock.unlock();
			}
		}
		
		@Override
		public void shutdown() {
			synchronized ( this._serials ) {
				for ( LinkedBlockingList<Runnable> queue : this._serials ) {
					queue.lock.lock();
					try {
						while ( queue.size()>0 ) {
							Runnable event = queue.poll();
							if(queue.isDoingEvent!=System.identityHashCode(event)){
								super.execute(queue.peek());
							}
						}
					} finally{
						queue.lock.unlock();
					}
				}
				super.shutdown();
			}
		}
		
		private static int indexFor(int h, int length) {
			return h & (length - 1);
		}
		
		private static int hash(int h) {
			h ^= (h >>> 20) ^ (h >>> 12);
			return h ^ (h >>> 7) ^ (h >>> 4);
		}
		
		private static class LinkedBlockingList<E> extends LinkedList<E>{
			final ReentrantLock lock = new ReentrantLock();
			int isDoingEvent = 0;
		}
	}
	
	/**
	 * @author 朱施健
	 *
	 */
	private static class AbstractEvent implements Event {
		
		// 事件名称
		private String _type;
		// 源数据
		private Object _source;
		
		private int _hashCode;
		
		private EventDispatcher<Event> _eventDispatcher;
		
		AbstractEvent(Event event,EventDispatcher<Event> eventDispatcher) {
			this._type = event.getType();
			this._source = event.getSource();
			this._hashCode = event.hashCode();
			this._eventDispatcher = eventDispatcher;
		}
		
		@Override
		public void run() {
			try {
				this._eventDispatcher.dispatchEvent(this, true);
			} catch ( Exception e ) {
				log.error("执行事件异常!",e);
			} catch (Error e) {
				log.error("执行事件错误!",e);
			}finally{
				this._eventDispatcher = null;
				this._type = null;
				this._source = null;
				MyThread.unlockAll();
			}
		}

		@Override
		public Object getSource() {
			return this._source;
		}

		@Override
		public String getType() {
			return this._type;
		}
		
		@Override
		public int hashCode(){
			return this._hashCode;
		}
	}
	
	/**
	 * 终止回调
	 * @author 朱施健
	 *
	 */
	public static interface TerminatedHandler{
		public void destroy();
	}
}
