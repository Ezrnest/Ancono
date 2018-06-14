/**
 * 2018-02-28
 */
package cn.timelives.java.math.algebra.abstractAlgebra.calculator;

/**
 * A field calculator is 
 * @author liyicheng
 * 2018-02-28 19:29
 *
 */
public interface FieldCalculator<T> extends DivisionRingCalculator<T> {
	/**
	 * Returns the result of {@code x*y}, which should be commutative.
	 */
	@Override
	T multiply(T x, T y);
	
	
}
