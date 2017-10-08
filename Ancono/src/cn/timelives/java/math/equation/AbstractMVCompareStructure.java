/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation;

import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

/**
 *
 *
 */
public abstract class AbstractMVCompareStructure<T> extends AbstractCompareStructure<T,List<T>>
implements MVCompareStructure<T>{
	/**
	 * @param mc
	 * @param op the operation type
	 */
	protected AbstractMVCompareStructure(MathCalculator<T> mc,Type op) {
		super(mc,op);
	}
	
	/**
	 * Compute whether the given list {@code x} is the list of solution of this equation.
	 * The size of the list should be equal to the number of the variables and the order is 
	 * considered.
	 * @param x a list of number
	 * @return {@code true} if {@code x} is the list of solution of this equation.
	 */
	@Override
	public abstract boolean isSolution(List<T> x) ;
	
	
	public abstract <N> AbstractMVCompareStructure<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
}
