/**
 * @author 朱施健
 */
package org.guyou.util;

import java.util.concurrent.locks.ReentrantLock;

public interface MyObjectPool<E> {
	/**
	 * 容量
	 * @return
	 */
	public int capacity();
	/**
	 * 借对象
	 * @return
	 */
	public E borrowObject();
	/**
	 * 还对象
	 * @param e
	 */
	public void returnObject(E e);
	
	/**
	 * 普通对象池
	 * @author 朱施健
	 * @param <E>
	 */
	public static class MyNormalObjectPool<E> implements MyObjectPool<E>{
		private int _poolSize;
		
		private Object[] _borrowList = null;
		private int _borrowCount;
		private int _borrowLeftCount;
		
		private Object[] _returnList = null;
		private int _returnCount;
		private int _returnLeftCount;
		
		public MyNormalObjectPool(int size,Class<?> clazz){
			_poolSize = size;

			_borrowList = new Object[_poolSize];
			_borrowCount = 0;
			_borrowLeftCount = _poolSize;
			
			_returnList = new Object[_poolSize];
			_returnCount = 0;
			_returnLeftCount = _poolSize;
			
			for ( int i = 0 ; i < size ; i++ ) {
				try {
					_borrowList[i] = clazz.newInstance();
				} catch ( InstantiationException e ) {
					e.printStackTrace();
				} catch ( IllegalAccessException e ) {
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public int capacity(){
			return _poolSize;
		}
		
		@Override
		public E borrowObject(){
			E r = null;
			//队列交换
			if(_borrowLeftCount==0){
				if(_returnCount==0){
					throw new NullPointerException("没有可借的对象!");
				}
				Object[] temp = _borrowList;
				_borrowList = _returnList;
				_returnList = temp;
				
				_borrowCount = 0;
				_borrowLeftCount = _returnCount;
				
				_returnCount = 0;
				_returnLeftCount = _poolSize;
			}
			r = (E) _borrowList[_borrowCount];
			_borrowList[_borrowCount] = null;
			_borrowCount +=1;
			_borrowLeftCount -=1;
			return r;
		}
		
		@Override
		public void returnObject(E e){
			_returnList[_returnCount] = e;
			_returnCount +=1;
			_returnLeftCount -=1;
		}
	}
	
	/**
	 * 带锁对象池
	 * @author 朱施健
	 * @param <E>
	 */
	public static class MyBlockingObjectPool<E> extends MyNormalObjectPool<E>{
		ReentrantLock lock = new ReentrantLock();
		
		public MyBlockingObjectPool(int size, Class<?> clazz) {
			super(size, clazz);
		}
		
		@Override
		public int capacity(){
			return super.capacity();
		}
		
		@Override
		public E borrowObject(){
			E r = null;
			lock.lock();
			try{
				r = super.borrowObject();
			}finally{
				lock.unlock();
			}
			return r;
		}
		
		@Override
		public void returnObject(E e){
			lock.lock();
			try{
				super.returnObject(e);
			}finally{
				lock.unlock();
			}
		}
	}
}

