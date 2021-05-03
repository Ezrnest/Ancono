package cn.ancono.math.algebra.abs;

import java.util.function.BiPredicate;

/**
 * Describes the relation in math. The relation must be consistent.
 */
public interface Relation<T> extends BiPredicate<T, T> {

    /**
     * Determines whether the two ordered elements satisfies the
     * relation.
     */
    @Override
    boolean test(T x, T y);
}
