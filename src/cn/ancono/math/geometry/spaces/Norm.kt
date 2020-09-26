package cn.ancono.math.geometry.spaces

import cn.ancono.math.MathCalculator
import cn.ancono.math.algebra.linearAlgebra.Matrix
import java.util.function.Function


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
fun interface Norm<in T : Any, out R : Any> {
    //Created by lyc at 2020-09-26 12:15
    /**
     * Computes the norm of `x`.
     */
    fun apply(x: T): R

    @JvmDefault
    operator fun invoke(x: T): R = apply(x)

    companion object {
        /**
         * Returns the canonical norm defined by the absolute value of
         */
        fun <T : Any> fromAbs(mc: MathCalculator<T>): Norm<T, T> {
            return Norm { x ->
                mc.abs(x)
            }
        }

        fun <T : Any> matrixPNorm(p: T): Norm<Matrix<T>, T> {
            return Norm { m -> m.normP(p) }
        }

        fun <T : Any> matrixInfNorm(): Norm<Matrix<T>, T> {
            return Norm { m -> m.normInf() }
        }

    }
}
