/**
 * 2018-01-31
 */
package cn.timelives.java.math.algebra.abstractAlgebra;

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.EqualPredicate;
import cn.timelives.java.math.set.MathSet;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * EqualRelation describes the equivalence relation between two non-null objects.
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
public interface EqualRelation<T> extends Relation<T>{
	
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
	 */
	static <T> EqualRelation<T> fromEqualPredicate(EqualPredicate<T> p){
		return p::isEqual;
	}

	EqualRelation<?> BY_EQUAL = Object::equals;

    /**
     * Returns an equivalence relation that represents {@link Object#equals(Object)}.
     */
	@SuppressWarnings("unchecked")
    static <T> EqualRelation<T> objectEquals(){
	    return (EqualRelation<T>) BY_EQUAL;
    }

    /**
     * Returns an equivalence relation that considers all the elements in the same sets are the same.
     * @param sets a series of sets, it is required the sets are mutually independent.
     */
    @SafeVarargs
    @SuppressWarnings("Duplicates")
    static <T> EqualRelation<T> byPartition(MathSet<T>...sets){
        return (x, y) -> {
            for(var set : sets){
                if(set.contains(x) && set.contains(y)){
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Returns an equivalence relation that considers all the elements in the same sets are the same.
     * @param sets a series of sets, it is required the sets are mutually independent.
     */
    @SuppressWarnings("Duplicates")
    static <T> EqualRelation<T> byPartition(List<MathSet<T>> sets){
        return (x, y) -> {
            for(var set : sets){
                if(set.contains(x) && set.contains(y)){
                    return true;
                }
            }
            return false;
        };
    }
}
