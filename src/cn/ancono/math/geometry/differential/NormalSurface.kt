package cn.ancono.math.geometry.differential

import cn.ancono.math.function.NDerivableFunction
import cn.ancono.math.function.invoke
import cn.ancono.math.function.partial1
import cn.ancono.math.function.partial2
import cn.ancono.math.geometry.analytic.space.SVector
import cn.ancono.math.geometry.analytic.space.SpaceParametricSurface
import cn.ancono.math.numberModels.api.RealCalculator

/**
 * Binary derivative vector function
 */
typealias BDVFunction<T> = NDerivableFunction<T, SVector<T>>

/**
 * Describes the normal surface `r(u,v)`.
 */
abstract class NormalSurface<T> : SpaceParametricSurface<T>, BDVFunction<T> {

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
        DifferentialUtil.innerProduct(calculator, ru, ru)
    }

    /**
     * The invariant `F` in the first fundamental form, which is the inner product of [ru] and [rv].
     *
     *     F = (ru, rv)
     */
    open val F: NDerivableFunction<T, T> by lazy {
        DifferentialUtil.innerProduct(calculator, ru, rv)
    }

    /**
     * The invariant `G` in the first fundamental form, which is the inner product of [rv] and [rv].
     *
     *     G = (rv, rv)
     */
    open val G: NDerivableFunction<T, T> by lazy {
        DifferentialUtil.innerProduct(calculator, rv, rv)
    }

    open val normalVector: BDVFunction<T> by lazy {
        DifferentialUtil.outerProduct(ru, rv)
    }

    open val unitNormalVector: BDVFunction<T> by lazy {
        DifferentialUtil.unitVectorSpace(calculator, normalVector)
    }


//    open val I : DifferentialForm
    /**
     * The invariant `L` in the second fundamental form, which is the inner product of [unitNormalVector] `n`
     * and `r_uu = ∂r_u/∂u`.
     *
     *     L = (n, r_uu) = -(n_u,r_u)
     */
    open val L: NDerivableFunction<T, T> by lazy {
        // (n,r_uu)
        DifferentialUtil.innerProduct(calculator, unitNormalVector, ru.partial1)
    }

    /**
     * The invariant `M` in the second fundamental form, which is the inner product of [unitNormalVector] `n`
     * and `r_uv = ∂r_u/∂v`.
     *
     *     M = (n, r_uv) = -(n_u,r_v) = -(n_v,r_u)
     */
    open val M: NDerivableFunction<T, T> by lazy {
        // (n,r_uv)
        DifferentialUtil.innerProduct(calculator, unitNormalVector, ru.partial2)
    }

    /**
     * The invariant `N` in the second fundamental form, which is the inner product of [unitNormalVector] `n`
     * and `r_vv = ∂r_v/∂v`.
     *
     *     L = (n, r_vv) = -(n_v,r_v)
     */
    open val N: NDerivableFunction<T, T> by lazy {
        // (n,r_vv)
        DifferentialUtil.innerProduct(calculator, unitNormalVector, rv.partial2)
    }

//    open val wingartenTransform

    /**
     * The Gauss curvature `K`, which is
     */
    open val K: NDerivableFunction<T, T> by lazy<NDerivableFunction<T, T>> {
        TODO()
    }

    open val H: NDerivableFunction<T, T> by lazy<NDerivableFunction<T, T>> {
        TODO()
    }


    companion object {
        fun <T> fromFunctionXYZ(
                a: NDerivableFunction<T, T>,
                b: NDerivableFunction<T, T>,
                c: NDerivableFunction<T, T>,
                mc: RealCalculator<T>
        ): NormalSurface<T> {
            return NormalSurfaceXYZ(a, b, c, mc)
        }

    }
}

internal class NormalSurfaceXYZ<T>(
        val x: NDerivableFunction<T, T>,
        val y: NDerivableFunction<T, T>,
        val z: NDerivableFunction<T, T>,
        override val calculator: RealCalculator<T>
) : NormalSurface<T>() {
    override val ru: BDVFunction<T> by lazy {
        NDerivableFunction.mergeOf3(x.partial1, y.partial1, z.partial1) { x, y, z ->
            SVector.valueOf(x, y, z, calculator)
        }
    }

    override val rv: BDVFunction<T> by lazy {
        NDerivableFunction.mergeOf3(x.partial2, y.partial2, z.partial2) { x, y, z ->
            SVector.valueOf(x, y, z, calculator)
        }
    }

    override fun apply(u: T, v: T): SVector<T> {
        val p = listOf(u, v)
        return SVector.valueOf(x(p), y(p), z(p), calculator)
    }

    override fun partial(i: Int): NDerivableFunction<T, SVector<T>> {
        return when (i) {
            0 -> ru
            1 -> rv
            else -> throw IllegalArgumentException()
        }
    }
}