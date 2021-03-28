package cn.ancono.math.geometry.analytic.space.shape;

import cn.ancono.math.MathCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class RightPrism<T> extends PePrism<T> {

    protected RightPrism(MathCalculator<T> mc, long p) {
        super(mc, p);
    }

    @NotNull
    @Override
    public abstract <N> RightPrism<N> mapTo(@NotNull MathCalculator<N> newCalculator, @NotNull Function<T, N> mapper);
}
