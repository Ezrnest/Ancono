package cn.timelives.java.math;

import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * A single variable equation.The number of variable is one.
 * @author lyc
 *
 */
public abstract class SingleVEquation<T> extends Equation<T> {

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
	public abstract boolean isSolution(T x) ;
	
	@Override
	public final boolean isSolution(List<T> so){
		if(so.size()!=1){
			throw new IllegalArgumentException("Number doesn't match");
		}
		return isSolution(so.get(0));
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Equation#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
	 */
	@Override
	public abstract <N> SingleVEquation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}
