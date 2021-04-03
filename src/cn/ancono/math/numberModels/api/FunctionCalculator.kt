package cn.ancono.math.numberModels.api

import cn.ancono.math.MathCalculator

//Created by lyc at 2021-03-31 22:53


/**
 * A function calculator is a calculator for functions with named variables.
 */
interface FunctionCalculator<F : Any> : MathCalculator<F> {


    /**
     * Returns the differential of a function
     */
    fun differential(f: F, variable: String = "x", order: Int = 1): F
}