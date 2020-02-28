package cn.ancono.math.numberModels.addableSet;

import cn.ancono.math.property.Mergeable;

import java.util.Set;

/**
 * A mergeable set keeps a set of elements that are mutually non-mergeable.
 *
 * @param <E>
 */
public interface MergeableSet<E extends Mergeable<E>> extends Set<E> {

    /**
     * @param e
     * @return
     */
    boolean containsMergeable(E e);

    E getMergeable(E e);

    /**
     * Adds the element to this set and if there is one element that is mergeable to
     * e, merge the two elements and replace the original one.
     *
     * @param e
     * @return
     */
    @Override
    boolean add(E e);
}
