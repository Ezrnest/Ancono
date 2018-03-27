/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation;

import cn.timelives.java.math.numberModels.MathCalculator;

import java.util.function.Function;

/**
 * @author liyicheng
 * 2017-10-06 09:22
 *
 */
public abstract class SVInquation<T> extends Inequation<T,T> implements SVCompareStructure<T> {
	
	/**
	 * @param mc
	 * @param op
	 */
	protected SVInquation(MathCalculator<T> mc, Type op) {
		super(mc, op);
	}
	
	/*
	 * @see cn.timelives.java.math.equation.SVCompareStructure#isSolution(java.lang.Object)
	 */
	@Override
	public boolean isSolution(T x) {
		return op.matches(compareZero(compute(x)));
	}
	
	public abstract <N> SVInquation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}
