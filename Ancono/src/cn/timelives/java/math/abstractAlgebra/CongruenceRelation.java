/**
 * 2018-01-31
 */
package cn.timelives.java.math.abstractAlgebra;

/**
 * Congruence relation is a special kind of equivalent relation.
 * <p>
 * Assume that a two-arity operation <b>*</b> is defined on a set of objects,
 * and there is a equivalent relation <b>~</b>. If for any {@literal a,b,c,d}
 * that fit {@literal a~b} and {@code c~d}, {@code ac ~ bd}
 * is always true, then the relation <b>~</b> is called a congruence relation.
 * <p>
 * 
 * @author liyicheng 2018-01-31 17:27
 *
 */
public interface CongruenceRelation<T> extends EqualRelation<T>{

}
