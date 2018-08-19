package cn.timelives.java.math.geometry.analytic.spaceAG.shape;

import cn.timelives.java.math.MathCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class RightPrism<T> extends PePrism<T> {

	protected RightPrism(MathCalculator<T> mc, long p) {
		super(mc, p);
	}

    @NotNull
    @Override
    public abstract <N> RightPrism<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator);
}
