/**
 * 2018-02-28
 */
package cn.timelives.java.math.abstractAlgebra.calculator;

/**
 * @author liyicheng
 * 2018-02-28 19:05
 *
 */
public interface UnitRingCalculator<T> extends RingCalculator<T> {
	/**
	 * Gets the multiplicative unit of the unit ring.
	 * @return {@code 1}
	 */
	T getOne();
	
	/**
	 * Return {@code x ^ n} as defined in the multiplicative monoid.
	 */
	@Override
	default T pow(T x, long n) {
		if(n==0) {
			return getOne();
		}
		return RingCalculator.super.pow(x, n);
	}
}
