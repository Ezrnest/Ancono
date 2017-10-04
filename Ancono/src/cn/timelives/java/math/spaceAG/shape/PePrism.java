package cn.timelives.java.math.spaceAG.shape;

import java.util.function.Function;

import cn.timelives.java.math.numberModels.MathCalculator;
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
