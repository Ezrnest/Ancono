/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation.inequation;

import cn.timelives.java.math.equation.SVCompareStructure;
import cn.timelives.java.math.equation.Type;
import cn.timelives.java.math.MathCalculator;
import org.jetbrains.annotations.NotNull;

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

    public abstract <N> SVInquation<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator);
}
