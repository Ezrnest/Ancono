package cn.ancono.math.geometry.spaces

import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.numberModels.api.RealCalculator


/*
 * Created by liyicheng at 2020-09-26 12:20
 */


/**
 * Norm is a function `|·|`: **V** -> **R** such that:
 * 1. `|x| >= 0` and `|x| = 0 <=> x = 0`
 * 2. `|kx| = |k||x|`, where `|k|` is the absolute value of `k`.
 * 3. `|x+y| <= |x| + |y|`
 *
 *
 *
 *
 *
 * @param T the type of **V**
 * @param R the type representing real numbers
 */
@FunctionalInterface
fun interface Norm<in T, out R> {
    //Created by lyc at 2020-09-26 12:15
    /**
     * Computes the norm of `x`.
     */
    fun apply(x: T): R

    operator fun invoke(x: T): R = apply(x)

    companion object {
        /**
         * Returns the canonical norm defined by the absolute value of
         */
        fun <T> fromAbs(mc: RealCalculator<T>): Norm<T, T> {
            return Norm { x ->
                mc.abs(x)
            }
        }

        fun <T> matrixPNorm(p: T): Norm<Matrix<T>, T> {
            return Norm { m -> m.norm(p) }
        }

        fun <T> matrixInfNorm(): Norm<Matrix<T>, T> {
            return Norm { m -> m.normInf() }
        }

    }
}

