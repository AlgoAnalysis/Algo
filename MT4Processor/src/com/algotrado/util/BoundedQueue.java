package com.algotrado.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
/**
 * Sum Bounded Queue Holds a number of elements
 * @author ohad
 *
 * @param <E>
 */
@SuppressWarnings("serial")
public abstract class BoundedQueue<E> extends ArrayList<E> implements Queue<E> {

	protected int maxSize;

    public BoundedQueue(int size){
        this.maxSize = size;
    }
    
	@Override
	public int size() {
		return super.size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		return super.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return super.iterator();
	}

	@Override
	public Object[] toArray() {
		return super.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return super.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		return super.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return super.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return super.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return super.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return super.retainAll(c);
	}

	@Override
	public void clear() {
		super.clear();
	}

	@Override
	public boolean add(E e) {
		if (size() == maxSize) {
			remove();
		}
		return super.add(e);
	}
	
	public E addElement(E e) {
		E removedElement = null;
		if (size() == maxSize) {
			removedElement = remove();
		}
		super.add(e);
		return removedElement;
	}

	@Override
	public boolean offer(E e) {
		return add(e);
	}

	@Override
	public E remove() {
		return super.remove(0);
	}

	@Override
	public E poll() {
		if (size() == 0) {
			return null;
		}
		return remove();
	}

	@Override
	public E element() {
		return get(0);
	}

	@Override
	public E peek() {
		if (size() == 0) {
			return null;
		}
		return get(0);
	}
	
}