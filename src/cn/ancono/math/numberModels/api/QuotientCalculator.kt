package cn.ancono.math.numberModels.api

import cn.ancono.math.algebra.abs.calculator.OrderedFieldCal

/**
 * Calculator for Quotient numbers.
 */
interface QuotientCalculator<T> : OrderedFieldCal<T> {

    /**
     * The characteristic of quotient number field is zero.
     */
    override val characteristic: Long
        get() = 0

    /**
     * Returns the number value corresponding to the integer.
     */
    val Int.v
        get() = of(this.toLong())

    /**
     * Returns the number value corresponding to the integer.
     */
    val Long.v
        get() = of(this)

}