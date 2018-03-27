/**
 * 2018-02-27
 */
package cn.timelives.java.math.abstractAlgebra.calculator;

/**
 * A MonoidCalculator is a calculator specialized for monoid.
 * @author liyicheng
 * 2018-02-27 17:40
 *
 */
public interface MonoidCalculator<T> extends SemigroupCalculator<T> {
	
	
	/**
	 * Returns the identity element of the semigroup.
	 * @return
	 */
	public T getIdentity();
	
	
	/**
	 * @param n a non-negative number
	 */
	@Override
	default T gpow(T x, long n) {
		if(n==0) {
			return getIdentity();
		}
		return SemigroupCalculator.super.gpow(x, n);
	}
}
