/**
 * @author 朱施健
 */
package org.guyou.util;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 朱施健 自定义线程，加入当前线程锁的管理
 */
public class MyThread extends Thread {
	//全局锁
	private static ReentrantLock[] _lockList; 
	// 当前线程拥有锁的集合
	private CurrentThreadLockList _curThreadLockList	= new CurrentThreadLockList();
	//线程池名称
	protected String threadPoolName;
	
	protected Object threadContext = null;
	
	public Action action = null;
	
	static{
		initLocks(2048);
	}
	
	public MyThread() {
		super();
	}

	public MyThread(Runnable target) {
		super(target);
	}

	public MyThread(ThreadGroup group, Runnable target) {
		super(group, target);
	}

	public MyThread(String name) {
		super(name);
	}

	public MyThread(ThreadGroup group, String name) {
		super(group, name);
	}

	public MyThread(Runnable target, String name) {
		super(target, name);
	}

	public MyThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
	}

	public MyThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
	}

	public static MyThread currentThread() {
		return (MyThread) Thread.currentThread();
	}
	
	/**
	 * 获得线程池名称
	 * @return
	 */
	public String getThreadPoolName(){
		return threadPoolName;
	}

	/**
	 * 初始化一定数目的锁
	 * 
	 * @param lockCount
	 */
	protected static void initLocks(int concurrencyLevel) {
		if (concurrencyLevel < 0 || concurrencyLevel > 0x40000000)
			throw new IllegalArgumentException("Illegal concurrencyLevel: " + concurrencyLevel);
		int capacity = 1;
		while (capacity < concurrencyLevel){
			capacity <<= 1;
		}
		_lockList = new ReentrantLock[capacity];
		for (int i = 0; i < capacity; ++i){
			_lockList[i] = new ReentrantLock();
		}
	}
	
	private static int indexFor(int h, int length) {
		return h & (length - 1);
	}
	
	private static int hash(int h) {
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	/**
	 * 锁定对象
	 * 
	 * @param obj
	 */
	public static void lockObject(Object obj) {
		if ( obj == null ) {
			throw new NullPointerException("锁定对象为空!");
		}
		int hashCode = SystemUtil.getHashCode(obj);
		ReentrantLock lock = _lockList[indexFor(hash(hashCode), _lockList.length)];
		if ( !lock.isHeldByCurrentThread() ) {
			currentThread()._curThreadLockList.put(hashCode, lock);
			lock.lock();
		}
	}

	/**
	 * 锁定对象集合
	 * 
	 * @param objs
	 */
	public static void lockObjects(Object... objs) {
		if ( objs.length == 0 ) {
			throw new IllegalStateException("MyThread.lockObjects(Object... objs)方法的参数长度错误!");
		}
		for ( Object obj : objs ) {
			if ( obj == null ) {
				throw new NullPointerException("锁定对象数组中有对象为空!");
			}
		}
		int[] hashCodeList = getHashCodeList(objs);
		ReentrantLock[] lockList = new ReentrantLock[hashCodeList.length];
		
		for ( int i = 0 ; i < hashCodeList.length ; i++ ) {
			lockList[i] = _lockList[indexFor(hash(hashCodeList[i]), _lockList.length)];
		}
		MyThread thread = currentThread();
		for ( int i = 0 ; i < lockList.length ; i++ ) {
			if ( !lockList[i].isHeldByCurrentThread() ) {
				thread._curThreadLockList.put(hashCodeList[i], lockList[i]);
				lockList[i].lock();
			}
		}
	}

	/**
	 * 解锁对象
	 * 
	 * @param obj
	 */
	public static void unlockObject(Object obj) {
		if ( obj == null ) {
			throw new NullPointerException("解锁对象为空");
		}
		int hashCode = SystemUtil.getHashCode(obj);
		MyThread thread = currentThread();
		LockIndex lockIndex = thread._curThreadLockList.get(hashCode);
		if ( lockIndex != null ) {
			if ( lockIndex.index < thread._curThreadLockList._arrayIndex ) {
				throw new IllegalStateException("解锁应该按照倒序方式依次解锁,现解锁对象后面还有未解锁的对象!");
			}
		}
		unlock(hashCode,thread._curThreadLockList.remove(hashCode));
	}

	/**
	 * 解锁对象集合
	 * 
	 * @param obj
	 */
	public static void unlockObjects(Object... objs) {
		if ( objs.length == 0 ) {
			throw new IllegalStateException("MyThread.unlockObjects(Object... objs)方法的参数长度错误!");
		}
		for ( Object obj : objs ) {
			if ( obj == null ) {
				throw new NullPointerException("锁定对象数组中有对象为空!");
			}
		}
		MyThread thread = currentThread();
		int[] hashCodeList = getHashCodeList(objs);
		for ( int i = hashCodeList.length - 1 ; i > -1 ; i-- ) {
			LockIndex lockIndex = thread._curThreadLockList.get(hashCodeList[i]);
			if ( lockIndex != null ) {
				if ( lockIndex.index < thread._curThreadLockList._arrayIndex ) {
					throw new IllegalStateException("解锁应该按照倒序方式依次解锁,现解锁对象后面还有未解锁的对象!");
				}
			}
			unlock(hashCodeList[i], thread._curThreadLockList.remove(hashCodeList[i]));
		}
	}

	private static void unlock(int hashCode, LockIndex lock) {
		if ( lock != null && lock.lock.isHeldByCurrentThread() ) {
			lock.lock.unlock();
		}
	}

	/**
	 * 全部解锁
	 */
	public static void unlockAll() {
		currentThread().clearAllLocks();
	}
	
	public void clearAllLocks() {
		if ( _curThreadLockList._arrayIndex != -1 ) {
			int i = _curThreadLockList._arrayIndex;
			for ( ; i > -1 ; i-- ) {
				Integer hashCode = (Integer) _curThreadLockList._keyList[i];
				if ( hashCode != null ) {
					unlock(hashCode, _curThreadLockList.remove(hashCode));
				}
				else {
					_curThreadLockList._arrayIndex -= 1;
				}
			}
		}
	}
	
	public <T> T getContext(){
		return (T) this.threadContext;
	}
	
	public <T> T replaceContext(T t){
		T result = (T) this.threadContext;
		this.threadContext = t;
		return result;
	}

	private static int[] getHashCodeList(Object[] objs) {
		int[] r = new int[objs.length + 1];
		for ( int i = 1 ; i < r.length ; i++ ) {
			r[i] = SystemUtil.getHashCode(objs[i - 1]);
		}
		for ( int i = 1 ; i < r.length - 1 ; i++ ) {
			for ( int j = i + 1 ; j < r.length ; j++ ) {
				if ( r[i] > r[j] ) {
					int temp = r[i];
					r[i] = r[j];
					r[j] = temp;
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		for ( int i = 1 ; i < r.length ; i++ ) {
			sb.append(r[i]);
		}
		r[0] = sb.toString().hashCode();
		return r;
	}

	private static class CurrentThreadLockList extends HashMap<Integer, LockIndex> {
		private Object[]	_keyList	= new Object[200];
		private int			_arrayIndex	= -1;

		@Override
		public LockIndex remove(Object key) {
			LockIndex lock = super.remove(key);
			if ( lock != null && lock.index != -1 ) {
				_keyList[lock.index] = null;
				if ( lock.index == _arrayIndex ) {
					_arrayIndex -= 1;
				}
				lock.index = -1;
			}
			return lock;
		}

		public LockIndex put(Integer key, ReentrantLock lock) {
			if ( _arrayIndex == _keyList.length - 1 ) {
				throw new ArrayIndexOutOfBoundsException("锁键个数超过最大值("+_keyList.length+")!");
			}
			LockIndex newIndex = new LockIndex(lock);
			LockIndex oldIndex = super.put(key, newIndex);
			if ( oldIndex != null ) {
				oldIndex.index = -1;
			}
			newIndex.index = ++_arrayIndex;
			_keyList[_arrayIndex] = key;
			return oldIndex;
		}
	}
	
	
	private static class LockIndex{
		ReentrantLock lock;
		int index = -1;
		
		LockIndex(ReentrantLock lock){
			this.lock = lock;
		}
	}
}
