/**
 * 2017-10-06
 */
package cn.timelives.java.math;

import java.util.function.Function;

import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * @author liyicheng
 * 2017-10-06 09:22
 *
 */
public abstract class SingleVInquation<T> extends Inequation<T> {
	
	/**
	 * @param mc
	 * @param op
	 */
	protected SingleVInquation(MathCalculator<T> mc, Type op) {
		super(mc, op);
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
	
	public abstract <N> SingleVInquation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}
