package cn.ancono.math.geometry.differential

import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.calculus.DifferentialForm
import cn.ancono.math.function.*
import cn.ancono.math.geometry.analytic.space.SVector
import cn.ancono.math.geometry.analytic.space.SpaceParametricSurface
import java.util.function.BiFunction
import java.util.function.Function

/**
 * Binary derivative vector function
 */
typealias BDVFunction<T> = NDerivableFunction<T, SVector<T>>

/**
 * Describes the normal surface `r(u,v)`.
 */
abstract class NormalSurface<T : Any> : SpaceParametricSurface<T>, BDVFunction<T> {

    /**
     * The partial derivative of `r` to `u`: `∂r/∂u = r_u`
     */
    open val ru: BDVFunction<T>
        get() = this.partial1

    /**
     * The partial derivative of `r` to `u`: `∂r/∂v = r_v`
     */
    open val rv: BDVFunction<T>
        get() = this.partial2

    /**
     * The invariant `E` in the first fundamental form, which is the inner product of [ru] and [ru].
     *
     *     E = (ru, ru)
     */
    open val E: NDerivableFunction<T, T> by lazy {
        DifferentialUtil.innerProduct(mathCalculator, ru, ru)
    }

    /**
     * The invariant `F` in the first fundamental form, which is the inner product of [ru] and [rv].
     *
     *     F = (ru, rv)
     */
    open val F: NDerivableFunction<T, T> by lazy {
        DifferentialUtil.innerProduct(mathCalculator, ru, rv)
    }

    /**
     * The invariant `G` in the first fundamental form, which is the inner product of [rv] and [rv].
     *
     *     G = (rv, rv)
     */
    open val G: NDerivableFunction<T, T> by lazy {
        DifferentialUtil.innerProduct(mathCalculator, rv, rv)
    }


//    open val I : DifferentialForm

    open val L: BiDerivableFunction<T, T>
        get() = TODO()

    open val M: BiDerivableFunction<T, T>
        get() = TODO()

    open val N: BiDerivableFunction<T, T>
        get() = TODO()
}