/**
 * 2018-02-28
 */
package cn.timelives.java.math.algebra.abstractAlgebra.structure;

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.FieldCalculator;

/**
 * A field is a division ring in which the multiplication is commutative.
 * @author liyicheng
 * 2018-02-28 18:50
 * @see <a href="https://en.wikipedia.org/wiki/Field_(mathematics)">Field</a>
 *
 */
public interface Field<T,F extends Field<T, F>> extends IntegralDomain<T,F>, DivisionRing<T,F> {
	/**
	 * Returns the field's calculator.
	 */
	@Override
	FieldCalculator<T> getCalculator();
}
