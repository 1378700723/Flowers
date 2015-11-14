package org.guyou.util;

import java.util.ArrayList;
import java.util.Collection;

public class MyArrayList<E> extends ArrayList<E> {
	private static final long serialVersionUID = 7480930314001616713L;

	public MyArrayList() {
		super();
	}

	public MyArrayList(int initialCapacity) {
		super(initialCapacity);
	}
	
	public MyArrayList(Collection<? extends E> c) {
		super(c);
	}
	
	public MyArrayList(E... e) {
		this();
		for ( int i = 0 ; i < e.length ; i++ ) {
			add(e[i]);
		}
	}

	@Override
	public E remove(int index) {
		if (index == -1) {
			return null;
		}
		return super.remove(index);
	}
}
