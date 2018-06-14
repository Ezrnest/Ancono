/**
 * 2018-02-28
 */
package cn.timelives.java.math.algebra.abstractAlgebra.calculator;


/**
 * @author liyicheng
 * 2018-02-28 19:01
 *
 */
public interface DivisionRingCalculator<T> extends UnitRingCalculator<T> {
	
	/**
	 * Returns the multiplicative inverse of element {@code x}.
	 * @param x
	 * @return x<sup>-1</sup>
	 */
	T reciprocal(T x);
	
	/**
	 * Returns the result of {@code x / y}, which is equal to 
	 * {@code x * reciprocal(y) }
	 * @param x
	 * @param y
	 * @return {@code x / y}
	 */
	T divide(T x,T y);
	
	/*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.calculator.UnitRingCalculator#pow(java.lang.Object, long)
	 */
	@Override
	default T pow(T x, long n) {
		if(n == 0) {
			return getOne();
		}
		if(n>0) {
			return UnitRingCalculator.super.pow(x, n);
		}else {
			T t = UnitRingCalculator.super.pow(x, -n);
			return reciprocal(t);
		}
	}
	
}
