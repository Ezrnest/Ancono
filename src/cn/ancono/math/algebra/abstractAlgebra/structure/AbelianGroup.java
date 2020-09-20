/**
 * 2018-02-28
 */
package cn.ancono.math.algebra.abstractAlgebra.structure;

import cn.ancono.math.set.MathSet;
import org.jetbrains.annotations.NotNull;

/**
 * An abelian group is a group with a commutative operation. This interface is a marker interface.
 *
 * @author liyicheng
 * 2018-02-28 17:47
 */
public interface AbelianGroup<T> extends AbelianSemigroup<T>, Group<T> {
    @Override
    default boolean isConjugate(T g1, T g2) {
        return getCalculator().isEqual(g1, g2);
    }

    @Override
    default MathSet<? extends Group<T>> getNormalSubgroups() {
        return getSubgroups();
    }

    @Override
    default boolean isNormalSubgroup(@NotNull Group<T> g) {
        return isSubgroup(g);
    }

    @Override
    default boolean isConjugate(@NotNull Group<T> h1, @NotNull Group<T> h2) {
        return h1.equals(h2);
    }

    @Override
    default Group<T> normalizer(@NotNull Group<T> h) {
        return this;
    }

    @Override
    default Group<T> centralizer(T a) {
        return this;
    }

    @Override
    default Group<T> centralizer(@NotNull Group<T> h) {
        return this;
    }

    @Override
    default Group<T> conjugateSubgroup(@NotNull Group<T> h, T x) {
        return h;
    }
}
