/**
 * 2018-02-28
 */
package cn.timelives.java.math.abstractAlgebra.structure;

/**
 * An abelian group is a group with a commutative operation.
 * @author liyicheng
 * 2018-02-28 17:47
 *
 */
public interface AbelianGroup<T,G extends AbelianGroup<T, G>> extends AbelianSemigroup<T>, Group<T,G> {

}
