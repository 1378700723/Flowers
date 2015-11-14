/**
 * 
 */
package org.guyou.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 朱施健
 *
 */
public class MyLazyList<E> {
	private int _size;
	private List<E> _list;
	private ObjectFactory<E> _vFactory;
	
	public MyLazyList(int size,ObjectFactory<E> vFactory){
		if(size<=0){
			throw new IllegalStateException("数组长度必须大于0");
		}
		_size = size;
		_list = new ArrayList<E>(size); 
		_vFactory = vFactory;
	}
	
	public E get(int index){
		if(index<0 || index>=_size){
			throw new IndexOutOfBoundsException("Index: "+index+", Size: "+_size);
		}
		E e = _list.get(index);
		if(e==null){
			_list.set(index, e=_vFactory.createElement());
		}
		return e;
	}
	
	public List<E> array(){
		return _list;
	}
	
	public int size(){
		return _size;
	}
}
