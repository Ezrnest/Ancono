/**
 * 2018-02-28
 */
package cn.timelives.java.math.abstractAlgebra.structure;

import cn.timelives.java.math.abstractAlgebra.calculator.FieldCalculator;
import cn.timelives.java.math.abstractAlgebra.calculator.RingCalculator;

/**
 * A field is a division ring in which the multiplication is commutative.
 * @author liyicheng
 * 2018-02-28 18:50
 *
 */
public interface Field<T> extends IntegralDomain<T>,DivisionRing<T> {
	/**
	 * Returns the field's calculator.
	 */
	@Override
	FieldCalculator<T> getCalculator();
}
