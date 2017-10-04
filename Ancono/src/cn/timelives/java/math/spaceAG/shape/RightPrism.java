package cn.timelives.java.math.spaceAG.shape;

import java.util.function.Function;

import cn.timelives.java.math.numberModels.MathCalculator;

public abstract class RightPrism<T> extends PePrism<T> {

	protected RightPrism(MathCalculator<T> mc, long p) {
		super(mc, p);
	}
	
	@Override
	public abstract <N> RightPrism<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}
