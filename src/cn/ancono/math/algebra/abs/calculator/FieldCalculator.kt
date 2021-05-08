/**
 * 2018-02-28
 */
package cn.ancono.math.algebra.abs.calculator

import cn.ancono.math.numberModels.Fraction
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

    /**
     * A field is always commutative.
     */
    override val isCommutative: Boolean
        get() = true

    /**
     * Gets the characteristic of this field.
     */
    val characteristic: Long


    /**
     * Returns the value that is equal to `one * x.numerator / x.denominator`.
     *
     * Note: If this field is of characteristic zero, then this method is the injection from Q to the field.
     */
    fun of(x: Fraction): T {
        return divideLong(of(x.numerator), x.denominator)
    }

}
