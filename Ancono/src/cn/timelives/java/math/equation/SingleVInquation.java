/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation;

import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.function.SVFunction;
import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * @author liyicheng
 * 2017-10-06 09:22
 *
 */
public abstract class SingleVInquation<T> extends Inequation<T>implements SVCompareStructure<T> {
	
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
	
	/*
	 * @see cn.timelives.java.math.equation.SVCompareStructure#isSolution(java.lang.Object)
	 */
	@Override
	public boolean isSolution(T x) {
		return op.matches(compareZero(compute(x)));
	}
	
	public abstract <N> SingleVInquation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}
