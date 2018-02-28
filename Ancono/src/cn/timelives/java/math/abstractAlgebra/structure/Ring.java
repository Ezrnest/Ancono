/**
 * 2018-02-28
 */
package cn.timelives.java.math.abstractAlgebra.structure;

import cn.timelives.java.math.abstractAlgebra.calculator.RingCalculator;
import cn.timelives.java.math.abstractAlgebra.calculator.SemigroupCalculator;

/**
 * <h3>Definition</h3>
 * Ring is one of the fundamental algebraic structures used in abstract algebra.
 * A ring is an abelian group with a second binary operation that is
 * associative, is distributive over the abelian group operation, and has an
 * identity element.
 * <p>
 * Assume R is a ring, and the operation of the abelian group is "add"("+") and
 * the other one is "multiply"("*"), the following is required:
 * <ul>
 * 1.
 * 
 * R is an abelian group under addition, meaning that:
 * <ul>
 * <li>{@code (a + b) + c = a + (b + c)} for all a, b, c in R (that is, + is
 * associative).
 * <li>{@code a + b = b + a} for all a, b in R (that is, + is commutative).
 * <li>There is an element 0 in R such that a + 0 = a for all a in R (that is, 0
 * is the additive identity).
 * <li>For each a in R there exists −a in R such that {@code a + (−a) = 0} (that is, −a
 * is the additive inverse of a).
 * </ul>
 * 2.
 * R is a monoid under multiplication, meaning that:
 * <ul>
 * 
 * <li>{@code (a · b) · c = a · (b · c)} for all a, b, c in R (that is, · is
 * associative).
 * <li>There is an element 1 in R such that {@code a · 1 = a} and {@code 1 · a = a} for all a in
 * R (that is, 1 is the multiplicative identity).
 * </ul>
 * 3.
 * Multiplication is distributive with respect to addition:
 * <ul>
 * 
 * <li>{@code a ⋅ (b + c) = (a · b) + (a · c)} for all a, b, c in R (left
 * distributivity).
 * <li>{@code (b + c) · a = (b · a) + (c · a)} for all a, b, c in R (right
 * distributivity).
 * </ul>
 * </ul>
 * <h3>Implementation notice</h3>
 * The interface Ring extends {@link AbelianGroup}, and therefore the method {@link #identity()}
 * returns the identity element of addition, namely {@code 0}. <p>  
 * The calculator returned by method {@link #getCalculator()} is a {@link RingCalculator}.
 * @author liyicheng 2018-02-28 17:40
 *
 */
public interface Ring<T> extends AbelianGroup<T> {
	/**
	 * Returns the calculator of this ring.
	 */
	@Override
	RingCalculator<T> getCalculator();
}
