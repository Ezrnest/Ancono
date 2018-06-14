package cn.timelives.java.math.geometry.analytic.spaceAG.shape;

import cn.timelives.java.math.MathCalculator;

import java.util.function.Function;

public abstract class RightPrism<T> extends PePrism<T> {

	protected RightPrism(MathCalculator<T> mc, long p) {
		super(mc, p);
	}
	
	@Override
	public abstract <N> RightPrism<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}
