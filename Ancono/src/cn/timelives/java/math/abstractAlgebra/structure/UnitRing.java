/**
 * 2018-02-28
 */
package cn.timelives.java.math.abstractAlgebra.structure;

import cn.timelives.java.math.abstractAlgebra.calculator.UnitRingCalculator;

/**
 * A unit ring is a ring in which the multiplication has an unit element: {@code 1}.
 * @author liyicheng
 * 2018-02-28 18:47
 *
 */
public interface UnitRing<T,R extends UnitRing<T, R>> extends Ring<T,R> {
	/**
	 * Gets the unit element of this ring.
	 * @return {@code 1}
	 */
	T unit();
	
	/**
	 * Returns the unit ring's calculator.
	 */
	@Override
	UnitRingCalculator<T> getCalculator();
}
