/**
 * 2017-10-08
 */
package cn.ancono.math.equation;

import cn.ancono.math.MathCalculatorHolder;
import cn.ancono.math.equation.inequation.Inequation;
import cn.ancono.math.function.MathFunction;
import cn.ancono.math.numberModels.api.RealCalculator;
import cn.ancono.math.property.SolutionPredicate;

/**
 * A CompareStructure is the super class of {@link Equation} and {@link Inequation}, which
 * is composed of a function and an operator : <pre>f(x) <i>op</i> 0</pre> where
 * the operation is one of the {@link Type}
 *
 * @param <T> the {@link RealCalculator} type
 * @param <S> the input of the compare structure\
 * @author liyicheng
 * 2017-10-08 11:27
 * @see Type
 */
public interface CompareStructure<T, S>
        extends MathCalculatorHolder<T>, SolutionPredicate<S> {
    /**
     * Gets the MathFunction of the left part of the compare structure.
     *
     * @return
     */
    MathFunction<S, T> asFunction();

    /**
     * Returns the type of the operation.
     *
     * @return the type
     */
    Type getOperationType();

    /**
     * Determines whether the give variable {@code x} is one of the
     * solution of this compare structure.
     */
    @Override
    boolean isSolution(S x);
}
