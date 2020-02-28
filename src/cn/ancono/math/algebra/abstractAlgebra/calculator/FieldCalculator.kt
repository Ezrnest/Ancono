/**
 * 2018-02-28
 */
package cn.ancono.math.algebra.abstractAlgebra.calculator

/**
 * A calculator for a field.
 * @author liyicheng
 * 2018-02-28 19:29
 */
interface FieldCalculator<T : Any> : DivisionRingCalculator<T> {
    /**
     * Returns the result of `x*y`, which should be commutative.
     */
    override fun multiply(x: T, y: T): T

    override val multiplyIsCommutative: Boolean
        get() = true

}
