/**
 * 2017-09-09
 */
package cn.ancono.math.set;

import cn.ancono.math.AbstractMathObject;
import cn.ancono.math.MathObjectReal;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * An abstract class for math sets, which extends the super class {@link MathObjectReal}.
 *
 * @author liyicheng
 * 2017-09-09 20:26
 */
public abstract class AbstractMathSet<T> extends AbstractMathObject<T, EqualPredicate<T>> implements MathSet<T> {

    /**
     * @param mc
     */
    protected AbstractMathSet(EqualPredicate<T> mc) {
        super(mc);
    }

    @NotNull
    @Override
    public abstract <N> AbstractMathSet<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper);


}
