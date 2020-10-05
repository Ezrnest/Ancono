package cn.ancono.math.geometry.analytic.space

import cn.ancono.math.MathCalculatorHolder
import cn.ancono.math.function.BiMathFunction


/*
 * Created by liyicheng at 2020-10-05 11:51
 */
typealias BiVectorFunction<T> = BiMathFunction<T, T, SVector<T>>

/**
 * Describes a parametric surface in `R^3` as a two-variable function `r(u,v)`
 */
interface SpaceParametricSurface<T : Any> : BiVectorFunction<T>, MathCalculatorHolder<T> {

    /**
     * Computes the value `r(u,v)`.
     */
    override fun apply(u: T, v: T): SVector<T>


}