package cn.timelives.java.math.geometry.analytic.spaceAG.shape;

import cn.timelives.java.math.MathCalculator;

import java.util.function.Function;
/**
 * Perpendicular
 * @author liyicheng
 *
 * @param <T>
 * @param <S>
 */
public abstract class PePrism<T> extends Prism<T> {

	protected PePrism(MathCalculator<T> mc, long p) {
		super(mc, p);
	}
	
	@Override
	public abstract <N> PePrism<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
}
