package cn.ancono.math.numberModels.api

import cn.ancono.math.algebra.abs.calculator.FieldCalculator

//Created by lyc at 2021-03-31 22:53


/**
 * A function calculator is a calculator for functions with named variables.
 */
interface FunctionCalculator<F> : FieldCalculator<F> {


    /**
     * Returns the differential of a function
     */
    fun differential(f: F, variable: String = "x", order: Int = 1): F
}