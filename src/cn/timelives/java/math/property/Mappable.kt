package cn.timelives.java.math.property

import cn.timelives.java.math.function.Bijection

/**
 * Describes the property of mappable.
 * @see [cn.timelives.java.math.function.SVFunction]
 */
interface Mappable<T : Any> {
    /**
     * Maps this to a new type [N].
     * @param mapper a bijection between type [T] and type [N]
     */
    fun <N : Any> mapTo(mapper: Bijection<T, N>): Mappable<N>
}