/**
 * 2017-09-09
 */
package cn.ancono.math.set;

import cn.ancono.math.AbstractMathObject;
import cn.ancono.math.MathObject;
import cn.ancono.math.numberModels.api.RealCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * An abstract class for math sets, which extends the super class {@link MathObject}.
 *
 * @author liyicheng
 * 2017-09-09 20:26
 */
public abstract class AbstractMathSet<T> extends AbstractMathObject<T> implements MathSet<T> {

    /**
     * @param mc
     */
    protected AbstractMathSet(RealCalculator<T> mc) {
        super(mc);
    }

    @NotNull
    @Override
    public abstract <N> AbstractMathSet<N> mapTo(@NotNull RealCalculator<N> newCalculator, @NotNull Function<T, N> mapper);


}
