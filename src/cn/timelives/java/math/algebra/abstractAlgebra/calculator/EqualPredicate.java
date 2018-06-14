/**
 * 2018-02-28
 */
package cn.timelives.java.math.algebra.abstractAlgebra.calculator;

import cn.timelives.java.math.algebra.abstractAlgebra.EqualRelation;

/**
 * A predicate that tests whether two objects are equal. EqualPredicate
 * is the same with {@link EqualRelation} but the method's name is "isEqual".
 * @author liyicheng
 * 2018-02-28 17:33
 *
 */
public interface EqualPredicate<T>{
	/**
	 * Evaluates whether the two objects are equal.
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any non-null reference value {@code x},
	 * {@code test(x,x)} should return {@code true}.
	 * <li>It is <i>symmetric</i>: for any non-null reference values {@code x} and
	 * {@code y}, {@code
	 * test(x,y)} should return {@code true} if and only if {@code test(y,x)}
	 * returns {@code true}.
	 * <li>It is <i>transitive</i>: for any non-null reference values {@code x},
	 * {@code y}, and {@code z}, if {@code test(x,y)} returns {@code true} and
	 * {@code test(y,z)} returns {@code true}, then {@code
	 * test(x,z)} should return {@code true}.
	 * <li>It is <i>consistent</i>: for any non-null reference values {@code x} and
	 * {@code y}, multiple invocations of {@code test(x,y)} consistently return
	 * {@code true} or consistently return {@code false}, provided no information
	 * used in {@code test} comparisons on the objects is modified.
	 * </ul>
	 * 
	 * @throws NullPointerException
	 *             if {@code x==null || y == null}
	 */
	boolean isEqual(T x,T y);
}
