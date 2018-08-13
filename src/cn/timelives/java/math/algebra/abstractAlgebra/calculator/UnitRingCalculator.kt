/**
 * 2018-02-28
 */
package cn.timelives.java.math.algebra.abstractAlgebra.calculator

/**
 * @author liyicheng
 * 2018-02-28 19:05
 */
interface UnitRingCalculator<T : Any> : RingCalculator<T> {
    /**
     * Gets the multiplicative unit of the unit ring.
     * @return `1`
     */
    val one: T

    /**
     * Return `x ^ n` as defined in the multiplicative monoid.
     */
    override fun pow(x: T, n: Long): T {
        return if (n == 0L) {
            one
        } else super.pow(x, n)
    }
}
