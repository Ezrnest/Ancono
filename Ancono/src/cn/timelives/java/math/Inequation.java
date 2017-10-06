/**
 * 2017-10-06
 */
package cn.timelives.java.math;

import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * Describes inequation in math.
 * @author liyicheng
 * 2017-10-06 08:49
 *
 */
public abstract class Inequation<T> extends FlexibleMathObject<T> {
	enum Type{
		GREATER,
		GREATER_OR_EQUAL,
		LESS,
		LESS_OR_EQUAL;
	}
	
	/**
	 * @param mc
	 */
	protected Inequation(MathCalculator<T> mc) {
		super(mc);
	}
	
	
	/**
	 * Determines whether the given list of variables is the solution of this inequation.
	 * The size of the list should be equal to the number of the variables and the order is 
	 * considered.
	 * @param x a list of variable
	 * @return {@code true} if {@code x} is solution.
	 */
	public abstract boolean isSolution(List<T> x);
	
	/**
	 * Get the number of variables in this equation.
	 * @return the number of variables.
	 */
	public abstract int getVariableCount();
	/**
	 * Gets the type of this inequation.
	 * @return the type of this inequation.
	 */
	public abstract Type getInquationType();
	
	@Override
	public abstract <N> Inequation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
}
