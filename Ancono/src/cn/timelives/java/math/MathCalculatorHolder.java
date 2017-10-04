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
public interface MathCalculatorHolder<T> {
	
	/**
	 * Return the MathCalculator this object is using.
	 * @return a MathCalculator
	 */
	public MathCalculator<T> getMathCalculator();
	
}
