package cn.ancono.math.geometry.analytic.space.shape;

import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.numberModels.api.RealCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public abstract class RightPrism<T> extends PePrism<T> {

    protected RightPrism(RealCalculator<T> mc, long p) {
        super(mc, p);
    }

    @NotNull
    @Override
    public abstract <N> RightPrism<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper);
}
