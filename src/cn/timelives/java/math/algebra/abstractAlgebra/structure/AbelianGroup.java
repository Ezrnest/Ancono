/**
 * 2018-02-28
 */
package cn.timelives.java.math.algebra.abstractAlgebra.structure;

import cn.timelives.java.math.set.MathSet;

/**
 * An abelian group is a group with a commutative operation.
 * @author liyicheng
 * 2018-02-28 17:47
 *
 */
public interface AbelianGroup<T> extends AbelianSemigroup<T>, Group<T> {
    @Override
    MathSet<? extends AbelianGroup<T>> getNormalSubgroups();

    @Override
    MathSet<? extends AbelianGroup<T>> getSubgroups();

    @Override
    default Coset<T, ? extends AbelianGroup<T>> getCoset(T x, boolean isLeft){
        return getCoset(x,this,isLeft);
    }

    @Override
    Coset<T, ? extends AbelianGroup<T>> getCoset(T x, Group<T> subGroup, boolean isLeft);
}
