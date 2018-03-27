/**
 * 
 */
package cn.timelives.java.math;

import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * 
 * Describes a holder of {@link MathCalculator}, which uses the MathCalculator to do operations.
 * @author liyicheng
 *
 */
public interface MathCalculatorHolder<T> extends CalculatorHolder<T, MathCalculator<T>> {
	
	/**
	 * Return the calculator this object is using.
	 * @return a calculator
	 */
	public MathCalculator<T> getMathCalculator();
	
}
