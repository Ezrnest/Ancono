package cn.ancono.math.geometry.analytic.space.shape;

import cn.ancono.math.MathCalculator;
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

    protected PePrism(MathCalculator<T> mc, long p) {
        super(mc, p);
    }

    @NotNull
    @Override
    public abstract <N> PePrism<N> mapTo(@NotNull MathCalculator<N> newCalculator, @NotNull Function<T, N> mapper);

}
