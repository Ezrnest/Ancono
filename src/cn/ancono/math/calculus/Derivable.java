/**
 * 2017-10-07
 */
package cn.ancono.math.calculus;

import cn.ancono.math.function.MathFunction;
import org.jetbrains.annotations.NotNull;

/**
 * A derivable function is a single variable function that
 * provides method derive
 *
 * @author liyicheng
 * 2017-10-07 14:39
 */
public interface Derivable<T, R, S extends MathFunction<T, R>> extends MathFunction<T, R> {

    /**
     * Returns the derivative of this function as
     * a SVFunction
     *
     * @return {@literal f'(x)}
     */
    @NotNull
    S derive();

}
