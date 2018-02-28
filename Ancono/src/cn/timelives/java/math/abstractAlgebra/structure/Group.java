/**
 * 2018-02-27
 */
package cn.timelives.java.math.abstractAlgebra.structure;

import cn.timelives.java.math.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.abstractAlgebra.calculator.MonoidCalculator;

/**
 * A group is a algebraic structure consisting of a set of elements and an operation.<p>
 * Assume the operation is "*", then
 * <ul>
 * <li>It is <i>associative</i>: {@code (a*b)*c = a*(b*c)}
 * <li>There exist an identity element e: {@code e*a = a*e = a}
 * <li>For every element a, it inverse element  <text>a<sup>-1</sup></text> exists, and 
 *  	<text> a*a<sup>-1</sup> = a<sup>-1</sup>*a = e</text>
 * </ul>
 * @author liyicheng
 * 2018-02-27 17:32
 *
 */
public interface Group<T> extends Monoid<T> {
	
	/**
	 * Returns the group's calculator.
	 */
	@Override
	GroupCalculator<T> getCalculator();
}
