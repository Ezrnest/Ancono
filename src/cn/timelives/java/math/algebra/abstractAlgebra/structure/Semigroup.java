/**
 * 2018-02-27
 */
package cn.timelives.java.math.algebra.abstractAlgebra.structure;

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.SemigroupCalculator;
import cn.timelives.java.math.set.MathSet;

/**
 * 
 * Semigroup is the base of almost all the algebraic structure in abstract algebra.<p>
 * A semigroup is composed of a set of elements and an operation defined in the set. 
 * Assume the operation is "*".
 * <ul>
 * <li>It is <i>associative</i>: (a*b)*c = a*(b*c)
 * </ul>
 * 
 * @author liyicheng
 * 2018-02-27 17:09
 *
 */
public interface Semigroup<T> {
	
	/**
	 * Gets the set of this group.
	 * @return a MathSet of all its elements
	 */
	public MathSet<T> getSet();
	
	/**
	 * Gets the calculator of this semigroup.
	 * @return
	 */
	public SemigroupCalculator<T> getCalculator();
	
}
