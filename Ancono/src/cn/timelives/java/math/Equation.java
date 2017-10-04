package cn.timelives.java.math;

import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * Equation is an abstract class for all kind of equations in math.
 * @author lyc
 *
 * @param <T>
 * @param <S>
 */
public abstract class Equation<T> extends FlexibleMathObject<T> {

	protected Equation(MathCalculator<T> mc) {
		super(mc);
	}
	/**
	 * Get the number of variables in this equation.
	 * @return the number of variables.
	 */
	public abstract int getVariableCount();
	/**
	 * Compute whether the given list {@code so} is the list of solution of this equation.
	 * The size of the list should be equal to the number of the variables and the order is 
	 * considered.
	 * @param so a list of number
	 * @return {@code true} if {@code so} is the list of solution of this equation.
	 */
	public abstract boolean isSolution(List<T> so);
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
	 */
	@Override
	public abstract <N> Equation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
}
