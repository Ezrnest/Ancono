/**
 * 2018-02-28
 */
package cn.ancono.math.algebra.abs.calculator

/**
 * Describes the calculator for a unit ring, where multiplicative identity exists.
 *
 *
 * @author liyicheng
 * 2018-02-28 19:05
 *
 * @see RingCalculator
 */
interface UnitRingCalculator<T> : RingCalculator<T>, MulMonoidCal<T> {

    /**
     * The multiplicative identity element
     */
    override val one: T


    /**
     * Determines whether the given element is a unit, namely invertible with respect to multiplication.
     *
     * This method is optional.
     *
     * @exception UnsupportedOperationException if this method is not supported.
     */
    fun isUnit(x: T): Boolean


    /**
     * Performs the exact division `x / y`.
     *
     * This method is optional.
     *
     * @exception UnsupportedOperationException if this method is not supported.
     */
    fun exactDivide(x: T, y: T): T

    override val numberClass: Class<T>
        get() = super<RingCalculator>.numberClass


    /**
     * Returns the value of adding [one] for [n] times, which is equivalent to `multiplyLong(one, x)`.
     */
    fun of(n: Long): T {
        return multiplyLong(one, n)
    }

}

//fun <T> UnitRingCalculator<T>.asMonoidCalculator() = GroupCalculators.asMonoidCalculator(this)