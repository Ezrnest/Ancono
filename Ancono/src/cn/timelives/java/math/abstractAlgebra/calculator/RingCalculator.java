/**
 * 2018-02-28
 */
package cn.timelives.java.math.abstractAlgebra.calculator;

import cn.timelives.java.utilities.ModelPatterns;

/**
 * The ring calculator defines some basic operations in a group. 
 * @author liyicheng
 * 2018-02-28 18:28
 *
 */
public interface RingCalculator<T> extends GroupCalculator<T> {
	/**
	 * Gets the zero element in this ring, which is the identity element of the addition group.
	 * @return {@code 0}
	 */
	T getZero();
	
	/**
	 * Returns the result of {@code x+y}.
	 * @param x
	 * @param y
	 * @return {@code x+y}
	 */
	T add(T x, T y);
	/**
	 * Returns the negate of this number.
	 * 
	 * @param x
	 *            
	 * @return {@code -x}
	 */
	T negate(T x);
	
	/**
	 * Returns the result of {@code x-y}, which is equal to {@code x+negate(y)}.
	 * @param x
	 * @param y
	 * @return {@code x-y}
	 */
	T subtract(T x,T y);
	
	/**
	 * Returns the result of {@code x*y}. This operation may be not commutative.
	 * @param x
	 * @param y
	 * @return {@code x*y}
	 */
	T multiply(T x,T y);
	
	/**
	 * Return {@code x ^ n} as defined in the multiplicative semigroup.
	 * 
	 * @param x
	 *            
	 * @param n a positive number
	 * @return {@code x ^ n}
	 */
	default T pow(T x, long n) {
		return ModelPatterns.binaryReduce(n, x, this::multiply);
	}
	
	/**
	 * Return the result of {@code n * p}, which is equal to applying addition to 
	 * {@code x} for {@code n} times.
	 * @param x
	 * @param n a long 
	 * @return
	 */
	default T multiplyLong(T x,long n) {
		return gpow(x, n);
	}
	
	/**
	 * @deprecated use {@link #getZero()} instead for more clarity.
	 */
	@Deprecated
	@Override
	default T getIdentity() {
		return getZero();
	}
	
	/**
	 * @deprecated use {@link #add(Object, Object)} instead for more clarity.
	 */
	@Deprecated
	@Override
	default T apply(T x, T y) {
		return add(x,y);
	}
	
	/**
	 * @deprecated use {@link #negate(Object)} instead for more clarity.
	 */
	@Deprecated
	@Override
	default T inverse(T x) {
		return negate(x);
	}
	
	/**
	 * @deprecated use {@link #multiplyLong(Object, long)} instead for more clarity.
	 */
	@Deprecated
	@Override
	default T gpow(T x, long n) {
		return GroupCalculator.super.gpow(x, n);
	}
}
