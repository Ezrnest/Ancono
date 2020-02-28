/**
 * 2018-02-27
 */
package cn.ancono.math.algebra.abstractAlgebra.structure;

import cn.ancono.math.algebra.abstractAlgebra.calculator.MonoidCalculator;

/**
 * Monoid is a semigroup with an identity element.
 *
 * @author liyicheng
 * 2018-02-27 17:20
 */
public interface Monoid<T> extends Semigroup<T> {
    /**
     * Gets the identity element of this semigroup.
     *
     * @return the identity element of this monoid.
     */
    public T identity();

    /**
     * Gets the calculator of this Monoid.
     */
    @Override
    MonoidCalculator<T> getCalculator();

}
