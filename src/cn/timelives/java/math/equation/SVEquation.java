package cn.timelives.java.math.equation;

import cn.timelives.java.math.MathCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * A single variable equation.The number of variable is one.
 * @author lyc
 *
 */
public abstract class SVEquation<T> extends Equation<T,T> implements SVCompareStructure<T>{

	protected SVEquation(MathCalculator<T> mc) {
		super(mc);
	}

	/**
	 * Determines whether {@code x} is the solution of this equation.
	 * @param x a number
	 * @return {@code true} if x is the solution of this equation.
	 */
	public boolean isSolution(T x) {
        return getMc().isZero(compute(x));
	}
	
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Equation#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
	 */
	@Override
    public abstract <N> SVEquation<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator);
}
