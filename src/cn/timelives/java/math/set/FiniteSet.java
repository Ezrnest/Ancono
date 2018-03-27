/**
 * 2017-09-10
 */
package cn.timelives.java.math.set;

import java.math.BigInteger;
import java.util.ListIterator;

/**
 * @author liyicheng 2017-09-10 14:38
 *
 */
public interface FiniteSet<T> extends CountableSet<T>{
	/**
	 * Gets an elements from this set. The method should always returns the same
	 * value.
	 * 
	 * @param index
	 *            the index of the element.
	 * @return a number
	 */
	T get(long index);

	/**
	 * Gets an elements from this set. The method should always returns the same
	 * value.
	 * 
	 * @param index
	 *            the index of the element.
	 * @return a number
	 */
	T get(BigInteger index);

	/**
	 * Returns a list iterator which iterates this limited set.
	 * 
	 * @return a list iterator
	 */
	public abstract ListIterator<T> listIterator();
	
	/**
	 * @see cn.timelives.java.math.set.CountableSet#isFinite()
	 */
	@Override
	default boolean isFinite() {
		return true;
	}
	
	
}
