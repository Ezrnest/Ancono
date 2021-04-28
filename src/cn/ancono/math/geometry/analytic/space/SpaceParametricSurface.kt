package cn.ancono.math.geometry.analytic.space

import cn.ancono.math.MathCalculatorHolder
import cn.ancono.math.function.NMathFunction


/*
 * Created by liyicheng at 2020-10-05 11:51
 */
typealias BiVectorFunction<T> = NMathFunction<T, SVector<T>>

//fun <T> BiVectorFunction<T>.apply(u: T, v: T): SVector<T> = this.apply(u to v)
//operator fun <T> BiVectorFunction<T>.invoke(u: T, v: T): SVector<T> = this.apply(u, v)


/**
 * Describes a parametric surface in `R^3` as a two-variable function `r(u,v)`
 */
interface SpaceParametricSurface<T> : BiVectorFunction<T>, MathCalculatorHolder<T> {

    override fun apply(vararg ts: T): SVector<T> {
        return apply(ts[0], ts[1])
    }

    override fun apply(x: List<T>): SVector<T> {
        return apply(x[0], x[1])
    }

    override val paramLength: Int
        get() = 2

    /**
     * Computes the value `r(u,v)`.
     */
    fun apply(u: T, v: T): SVector<T>
}