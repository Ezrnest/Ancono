/**
 * 2018-02-28
 */
package cn.ancono.math.algebra.abs.calculator

import cn.ancono.math.numberModels.api.FieldNumberModel

/**
 * A calculator for a field.
 *
 * which is commutative
 *
 * @see [cn.ancono.math.algebra.abs.structure.Field]
 * @see [FieldNumberModel]
 * @author liyicheng
 * 2018-02-28 19:29
 */
interface FieldCalculator<T : Any> : DivisionRingCalculator<T> {
    /**
     * Returns the result of `x*y`, which should be commutative.
     */
    override fun multiply(x: T, y: T): T


    @JvmDefault
    override val isMultiplyCommutative: Boolean
        get() = true

}