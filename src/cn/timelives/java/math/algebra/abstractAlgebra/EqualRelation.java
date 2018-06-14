/**
 * 2018-01-31
 */
package cn.timelives.java.math.algebra.abstractAlgebra;

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.EqualPredicate;

import java.util.function.BiPredicate;

/**
 * EqualRelation describes the equivalent relation between two non-null objects.
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
 * <p>
 * 
 * @author liyicheng 2018-01-31 17:20
 *
 */
public interface EqualRelation<T> extends BiPredicate<T, T>{
	
	/**
	 * Evaluates whether the two objects have this equal relation.
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
	@Override
	boolean test(T x, T y);
	
	/**
	 * Returns an EqualRelation based on the EqualPredicate.
	 * @param p
	 * @return
	 */
	public static <T> EqualRelation<T> fromEqualPredicate(EqualPredicate<T> p){
		return p::isEqual;
	}
}
