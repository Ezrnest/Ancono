/**
 * 2017-10-06
 */
package cn.timelives.java.math;

import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * A CompareStructure is the super class of {@link Equation} and {@link Inequation}, which 
 * is composed of two functions: left and right and a method to determine the result.
 * @author liyicheng
 * 2017-10-06 09:56
 *
 */
public abstract class CompareStructure<T> extends FlexibleMathObject<T> {

	/**
	 * @param mc
	 */
	protected CompareStructure(MathCalculator<T> mc) {
		super(mc);
	}
	public abstract int getVariableCount();
	
	public abstract boolean isSolution(List<T> x);
	/**
	 * 
	 * @return
	 */
	public abstract MathFunction<List<T>,T> getLeft();
	
	/**
	 * 
	 * @return
	 */
	public abstract MathFunction<List<T>,T> getRight();
	
	public abstract <N> CompareStructure<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}
