package cn.ancono.math.numberModels.api

import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.algebra.abs.calculator.TotalOrderPredicate

/**
 * Calculator for Quotient numbers.
 */
interface QCalculator<T> : FieldCalculator<T>, TotalOrderPredicate<T> {


    override val characteristic: Long
        get() = 0

    fun abs(x: T): T

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