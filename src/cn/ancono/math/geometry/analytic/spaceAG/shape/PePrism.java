package cn.ancono.math.geometry.analytic.spaceAG.shape;

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
    public abstract <N> PePrism<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator);

}
