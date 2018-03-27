/**
 * 2018-02-27
 */
package cn.timelives.java.math.abstractAlgebra.calculator;

import cn.timelives.java.math.function.MathBinaryOperator;
import cn.timelives.java.utilities.ModelPatterns;

/**
 * A semigroup calculator is a calculator specialized for semigroup.
 * @author liyicheng
 * 2018-02-27 17:31
 *
 */
public interface SemigroupCalculator<T> extends EqualPredicate<T>,MathBinaryOperator<T>{
	
	
	/**
	 * Applies the operation defined in the semigroup.
	 * @param x 
	 * @param y
	 * @return
	 */
	public T apply(T x,T y);
	
	/**
	 * Determines whether the two elements are equal.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isEqual(T x,T y);
	
	/**
	 * Returns {@code x^n=x*x*x....*x} defined in the semigroup.
	 * @param x 
	 * @param n a positive number
	 * @return
	 */
	public default T gpow(T x,long n) {
		return ModelPatterns.binaryReduce(n, x, this::apply);
	}
}
