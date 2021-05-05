/**
 * 2017-10-06
 */
package cn.ancono.math.equation;

import cn.ancono.math.numberModels.api.RealCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author liyicheng
 * 2017-10-06 19:21
 */
public abstract class AbstractSVCompareStructure<T> extends AbstractCompareStructure<T, T>
        implements SVCompareStructure<T> {


    /**
     * @param mc
     * @param op
     */
    protected AbstractSVCompareStructure(RealCalculator<T> mc, Type op) {
        super(mc, op);
    }

    /**
     * Determines whether the given value {@code x} is the solution of
     * this.
     *
     * @param x a number
     * @return {@code true} if {@code x} is the solution
     */
    public boolean isSolution(T x) {
        return op.matches(compareZero(compute(x)));
    }

    /*
     * @see cn.ancono.math.equation.AbstractCompareStructure#mapTo(java.util.function.Function, cn.ancono.math.numberModels.api.MathCalculator)
     */
    @NotNull
    @Override
    public abstract <N> AbstractSVCompareStructure<N> mapTo(@NotNull RealCalculator<N> newCalculator, @NotNull Function<T, N> mapper);
}
