package org.guyou.util;

import java.util.Collection;
import java.util.Vector;

public class MyVector<E> extends Vector<E> {

	private static final long serialVersionUID = -1035703821597149885L;

	public MyVector() {
		super();
	}

	public MyVector(int initialCapacity) {
		super(initialCapacity);
	}
	
	public MyVector(Collection<? extends E> c) {
		super(c);
	}
	
	@Override
	public synchronized E remove(int index) {
		if (index == -1) {
			return null;
		}
		return super.remove(index);
	}
}
