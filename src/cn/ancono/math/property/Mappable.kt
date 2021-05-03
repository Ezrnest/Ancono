package cn.ancono.math.property

import cn.ancono.math.function.Bijection

/**
 * Describes the property of mappable.
 * @see [cn.ancono.math.function.SVFunction]
 */
interface Mappable<T> {
    /**
     * Maps this to a new type [N].
     * @param mapper a bijection between type [T] and type [N]
     */
    fun <N> mapTo(mapper: Bijection<T, N>): Mappable<N>
}