package cn.ancono.math.equation;

import cn.ancono.math.numberModels.api.RealCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * A single variable equation.The number of variable is one.
 *
 * @author lyc
 */
public abstract class SVEquation<T> extends Equation<T, T> implements SVCompareStructure<T> {

    protected SVEquation(RealCalculator<T> mc) {
        super(mc);
    }

    /**
     * Determines whether {@code x} is the solution of this equation.
     *
     * @param x a number
     * @return {@code true} if x is the solution of this equation.
     */
    public boolean isSolution(T x) {
        return getMc().isZero(compute(x));
    }


    /* (non-Javadoc)
     * @see cn.ancono.math.Equation#mapTo(java.util.function.Function, cn.ancono.math.number_models.MathCalculator)
     */
    @NotNull
    @Override
    public abstract <N> SVEquation<N> mapTo(@NotNull RealCalculator<N> newCalculator, @NotNull Function<T, N> mapper);
}
