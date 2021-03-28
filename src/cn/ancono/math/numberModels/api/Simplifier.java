package cn.ancono.math.numberModels.api;

import cn.ancono.math.MathObject;
import cn.ancono.utilities.structure.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * A simplifier is used for simplify a number or a set of numbers.
 * The simplifier may
 * have different performance while simplifying, and the actual process should
 * be specific when any Simplifier is required. <p>
 *
 * @author lyc
 * @see Simplifiable
 */
@FunctionalInterface
public interface Simplifier<T> {
    /**
     * Input a list of numbers to simplify, and return a list of simplified numbers,
     * the number and order of numbers should be the identity.<p>
     * This simplify method should be equal to multiply a non-zero value to all
     * of the numbers to make the result simpler. For example, for the input {@code [2,4,6]}, the
     * simplifier may  multiply number {@code 1/2} so the result will be {@code [1,2,3]}, which
     * should be simpler than the input.
     *
     * @param numbers arguments, the content may be changed.
     * @return a list of numbers.
     */
    List<T> simplify(List<T> numbers);

    /**
     * Simplifies a single number. This simplification operation is optional so
     * a default implement is applied.
     *
     * @param x a number
     * @return the result
     */
    default T simplify(T x) {
        return x;
    }

    /**
     * Simplifies two numbers. This simplification operation is optional so
     * a default implement is applied.
     *
     * @param a a number
     * @param b another number
     * @return the result as a pair of numbers
     */
    default Pair<T, T> simplify(T a, T b) {
        var list = simplify(Arrays.asList(a, b));
        return new Pair<>(list.get(0), list.get(1));
    }

    @SuppressWarnings("unchecked")
    public static <T, S extends MathObject<T>> S singleSimplify(Simplifier<T> s, S x) {
        return (S) x.mapTo(x.getMathCalculator(), s::simplify);
    }


}
