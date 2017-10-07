package cn.timelives.java.math.equation;

import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.function.SVFunction;
import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * A single variable equation.The number of variable is one.
 * @author lyc
 *
 */
public abstract class SingleVEquation<T> extends Equation<T> implements SVCompareStructure<T>{

	protected SingleVEquation(MathCalculator<T> mc) {
		super(mc);
	}

	@Override
	public final int getVariableCount() {
		return 1;
	}
	/**
	 * Determines whether {@code x} is the solution of this equation.
	 * @param x a number
	 * @return {@code true} if x is the solution of this equation.
	 */
	public boolean isSolution(T x) {
		return mc.isZero(compute(x));
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Equation#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
	 */
	@Override
	public abstract <N> SingleVEquation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}
