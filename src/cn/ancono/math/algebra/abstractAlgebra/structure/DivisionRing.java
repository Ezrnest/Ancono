/**
 * 2018-02-28
 */
package cn.ancono.math.algebra.abstractAlgebra.structure;

import cn.ancono.math.algebra.abstractAlgebra.calculator.DivisionRingCalculator;

/**
 * A division ring is a ring in which division is possible, and
 * for each non-zero element, its multiplicative inverse exists.
 * <p>
 * For each non-zero element {@code a}, its multiplicative inverse, or namely its
 * reciprocal exists, which is written as(a<sup>-1</sup>). It satisfies that
 * <text> a*a<sup>-1</sup> = a<sup>-1</sup>*a = e</text>
 *
 * @author liyicheng
 * 2018-02-28 18:57
 */
public interface DivisionRing<T> extends UnitRing<T> {
    /**
     * Returns the division ring's calculator.
     */
    @Override
    DivisionRingCalculator<T> getCalculator();
}
