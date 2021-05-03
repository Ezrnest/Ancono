/**
 * 2018-02-28
 */
package cn.ancono.math.algebra.abs.calculator

import cn.ancono.math.numberModels.api.FieldNumberModel

/**
 * A calculator for a field.
 *
 *
 * @see [cn.ancono.math.algebra.abs.structure.Field]
 * @see [FieldNumberModel]
 * @author liyicheng
 * 2018-02-28 19:29
 */
interface FieldCalculator<T> : DivisionRingCalculator<T> {
    /**
     * Returns the result of `x*y`, which should be commutative.
     */
    override fun multiply(x: T, y: T): T

    @JvmDefault
    override val isCommutative: Boolean
        get() = true

    /**
     * Gets the characteristic of this field.
     */
    val characteristic: Long


}
