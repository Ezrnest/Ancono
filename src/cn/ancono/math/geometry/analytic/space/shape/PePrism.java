package cn.ancono.math.geometry.analytic.space.shape;

import cn.ancono.math.numberModels.api.RealCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Perpendicular
 *
 * @param <T>
 * @param <S>
 * @author liyicheng
 */
public abstract class PePrism<T> extends Prism<T> {

    protected PePrism(RealCalculator<T> mc, long p) {
        super(mc, p);
    }

    @NotNull
    @Override
    public abstract <N> PePrism<N> mapTo(@NotNull RealCalculator<N> newCalculator, @NotNull Function<T, N> mapper);

}
