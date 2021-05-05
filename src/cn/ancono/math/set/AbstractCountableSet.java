/**
 *
 */
package cn.ancono.math.set;

import cn.ancono.math.numberModels.api.RealCalculator;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.function.Function;

/**
 *
 *
 */
public abstract class AbstractCountableSet<T> extends AbstractMathSet<T> implements CountableSet<T> {


    /**
     * @param mc
     */
    protected AbstractCountableSet(RealCalculator<T> mc) {
        super(mc);
    }


    @Override
    public BigInteger sizeAsBigInteger() {
        return BigInteger.valueOf(size());
    }

    @NotNull
    @Override
    public abstract <N> AbstractCountableSet<N> mapTo(@NotNull RealCalculator<N> newCalculator, @NotNull Function<T, N> mapper);
}
