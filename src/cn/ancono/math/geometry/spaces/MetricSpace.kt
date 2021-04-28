package cn.ancono.math.geometry.spaces


/**
 * A metric space is composed of a set **M** and a function d: **M** × **M** -> **R**,
 * where the function d satisfies:
 * 1. Non-negative:
 *     > d(x,y) >= 0 and d(x,y) = 0 if and only if x = y
 * 2. Symmetry:
 *     > d(x,y) = d(y,x)
 * 3. Triangle inequality:
 *     > d(x,y) + d(y,z) >= d(x,z)
 *
 *
 * See : [Metric space](https://en.wikipedia.org/wiki/Metric_space)
 *
 *
 * Created at 2018/11/29 16:17
 * @author  liyicheng
 */
interface MetricSpace<in T, out R> {
    /**
     * Returns the distance defined in this metric space.
     */
    fun distance(x: T, y: T): R
}


