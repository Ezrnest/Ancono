/**
 * 2018-02-28
 */
package cn.timelives.java.math.algebra.abstractAlgebra.structure;

import cn.timelives.java.math.set.MathSet;

/**
 * An abelian group is a group with a commutative operation. This interface is a marker interface.
 * @author liyicheng
 * 2018-02-28 17:47
 *
 */
public interface AbelianGroup<T> extends AbelianSemigroup<T>, Group<T> {
}
