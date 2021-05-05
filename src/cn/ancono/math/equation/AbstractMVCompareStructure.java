/**
 * 2017-10-06
 */
package cn.ancono.math.equation;

import cn.ancono.math.numberModels.api.RealCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

/**
 *
 */
public abstract class AbstractMVCompareStructure<T> extends AbstractCompareStructure<T, List<T>>
        implements MVCompareStructure<T> {
    /**
     * @param mc
     * @param op the operation type
     */
    protected AbstractMVCompareStructure(RealCalculator<T> mc, Type op) {
        super(mc, op);
    }

    /**
     * Compute whether the given list {@code x} is the list of solution of this equation.
     * The size of the list should be equal to the number of the variables and the order is
     * considered.
     *
     * @param x a list of number
     * @return {@code true} if {@code x} is the list of solution of this equation.
     */
    @Override
    public abstract boolean isSolution(List<T> x);


    @NotNull
    public abstract <N> AbstractMVCompareStructure<N> mapTo(@NotNull RealCalculator<N> newCalculator, @NotNull Function<T, N> mapper);

}
