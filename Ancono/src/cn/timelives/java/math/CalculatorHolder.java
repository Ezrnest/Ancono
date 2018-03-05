/**
 * 2018-03-05
 */
package cn.timelives.java.math;

import cn.timelives.java.math.abstractAlgebra.calculator.EqualPredicate;

/**
 * @author liyicheng
 * 2018-03-05 20:25
 *
 */
public interface CalculatorHolder<T, S extends EqualPredicate<T>> {

	/**
	 * Return the calculator this object is using.
	 * 
	 * @return a calculator
	 */
	public S getMathCalculator();
}
