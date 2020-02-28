/**
 * 2017-10-05
 */
package cn.ancono.math.set;

import cn.ancono.math.property.Intersectable;

/**
 * A set that is "intersectable". The implementor must ensure that
 * if {@code x.contains(e)==false}, then for any {@code y}, {@code x.intersect(y).contains(e)==false}.
 * a set implements this
 *
 * @author liyicheng
 * 2017-10-05 13:07
 * @see UnionableSet
 */
public interface IntersectableSet<T, S extends IntersectableSet<T, S>> extends MathSet<T>, Intersectable<S> {
    /**
     * Intersects {@code this} and another set.
     *
     * @throws UnsupportedOperationException if the result cannot be returned.
     */
    S intersect(S x);
}
