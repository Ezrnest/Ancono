package cn.timelives.java.math.equation;

import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * Equation is an abstract class for all kind of equations in math. 
 * An equation can be presented as <pre><i>f(x)</i> = 0</pre>, where 
 * <i>f(x)</i> is a MathFunction, and {@link MathCalculator#isZero(Object)} is 
 * used to determine the solution.
 * @author lyc
 *
 * @param <T>
 */
public abstract class Equation<T> extends AbstractCompareStructure<T> {

	protected Equation(MathCalculator<T> mc) {
		super(mc,Type.EQUAL);
	}
	
	
	/**
	 * Compute whether the given list {@code x} is the list of solution of this equation.
	 * The size of the list should be equal to the number of the variables and the order is 
	 * considered.
	 * @param x a list of number
	 * @return {@code true} if {@code x} is the list of solution of this equation.
	 */
	public boolean isSolution(List<T> x) {
		return mc.isZero(getFunction().apply(x));
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
	 */
	@Override
	public abstract <N> Equation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
}
