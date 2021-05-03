/**
 * 2018-02-28
 */
package cn.ancono.math.algebra.abs.calculator

import cn.ancono.math.algebra.abs.GroupCalculators

/**
 * Describes the calculator for a unit ring, where multiplicative identity exists.
 *
 *
 * @author liyicheng
 * 2018-02-28 19:05
 */
interface UnitRingCalculator<T> : RingCalculator<T> {

    /**
     * The multiplicative identity element
     */
    val one: T

    /**
     * Return `x ^ n` as defined in the multiplicative monoid.
     */
    @JvmDefault
    override fun pow(x: T, n: Long): T {
        return if (n == 0L) {
            one
        } else super.pow(x, n)
    }

    /**
     * Determines whether the given element is a unit, namely invertible with respect to multiplication.
     *
     * This method is optional.
     *
     * @exception UnsupportedOperationException if this method is not implemented.
     */
    @JvmDefault
    fun isUnit(x: T): Boolean {
        throw UnsupportedOperationException("Not supported")
    }


}

fun <T> UnitRingCalculator<T>.asMonoidCalculator() = GroupCalculators.asMonoidCalculator(this)