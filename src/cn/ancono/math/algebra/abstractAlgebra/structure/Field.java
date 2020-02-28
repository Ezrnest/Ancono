/**
 * 2018-02-28
 */
package cn.ancono.math.algebra.abstractAlgebra.structure;

import cn.ancono.math.algebra.abstractAlgebra.calculator.FieldCalculator;

/**
 * A field is a division ring in which the multiplication is commutative.
 *
 * @author liyicheng
 * 2018-02-28 18:50
 * @see <a href="https://en.wikipedia.org/wiki/Field_(mathematics)">Field</a>
 */
public interface Field<T> extends IntegralDomain<T>, DivisionRing<T> {
    /**
     * Returns the field's calculator.
     */
    @Override
    FieldCalculator<T> getCalculator();
}
