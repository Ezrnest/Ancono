/**
 * 2017-11-04
 */
package cn.timelives.java.utilities.structure;

import static cn.timelives.java.utilities.Printer.print;

import java.util.AbstractList;
import java.util.ArrayList;

/**
 * A circular array list buffer uses an ArrayList to store data. It provide limited 
 * size of buffer and methods of adding and getting. <p>
 * When an element is added,
 * @author liyicheng
 * 2017-11-04 20:53
 *
 */
public final class CicularArrayListBuffer<E> extends AbstractList<E>{

	private final int bufSize;
	private final ArrayList<E> list;
	private int index;
	/**
	 * @param bufSize must be at lease 1.
	 */
	public CicularArrayListBuffer(int bufSize) {
		if(bufSize < 1) {
			throw new IllegalArgumentException("bufSize<1");
		}
		this.bufSize = bufSize;
		list = new ArrayList<>(bufSize);
		for(int i=0;i<bufSize;i++) {
			list.add(null);
		}
	}
	
	/**
	 * Get the element in exact index, but actually returns 
	 * {@code list.get(index & bufSize)}
	 */
	@Override
	public E get(int index) {
		return list.get(index % bufSize);
	}
	/**
	 * Add the element to the last of this buffer.
	 */
	@Override
	public boolean add(E e) {
		list.set(index, e);
		index ++;
		if(index ==bufSize) {
			index = 0;
		}
		return true;
	}
	
	
	/*
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size() {
		return list.size();
	}
	
	/**
	 * Gets the previous element. {@code getPrevious(0)} returns the latest element added.
	 * If the element is out of buffer, then {@code null} will be returned. 
	 * @param n a non-negative number
	 * @return
	 */
	public E getPrevious(int n) {
		if(n<0) {
			throw new IllegalArgumentException();
		}
		if(n>=bufSize) {
			return null;
		}
		int i = (index-1-n) % bufSize;
		if(i<0) {
			i+=bufSize;
		}
		return list.get(i);
	}
	
	public static void main(String[] args) {
		CicularArrayListBuffer<Integer> b = new CicularArrayListBuffer<>(16);
		b.add(123);
		print(b.getPrevious(0));
	}

}
