/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation;

import cn.timelives.java.math.numberModels.MathCalculator;

import java.util.function.Function;

/**
 * 
 * @author liyicheng
 * 2017-10-06 19:21
 *
 */
public abstract class AbstractSVCompareStructure<T> extends AbstractCompareStructure<T, T>
implements SVCompareStructure<T>{
	
	
	
	/**
	 * @param mc
	 * @param op
	 */
	protected AbstractSVCompareStructure(MathCalculator<T> mc, Type op) {
		super(mc, op);
	}
	
	/**
	 * Determines whether the given value {@code x} is the solution of 
	 * this.
	 * @param x a number
	 * @return {@code true} if {@code x} is the solution
	 */
	public boolean isSolution(T x) {
		return op.matches(compareZero(compute(x)));
	}
	
	/*
	 * @see cn.timelives.java.math.equation.AbstractCompareStructure#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
	 */
	@Override
	public abstract <N> AbstractSVCompareStructure<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}
