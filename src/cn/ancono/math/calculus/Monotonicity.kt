package cn.ancono.math.calculus

import cn.ancono.math.MathCalculator
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.Fraction


enum class MonoType {

}

/*
 * Created at 2018/10/23 18:11
 * @author  liyicheng
 */
object Monotonicity {

    /**
     * Determines the monotonicity of a power function, `x^pow` in a infinitesimal neighbourhood
     * represented by the limit result. Return `1` for increasing, `0` for stable and
     * `-1` for decreasing.
     */
    fun <T : Any> power(x: LimitResult<T>, pow: Fraction, mc: MathCalculator<T>): Int {
        if (pow.isZero()) {
            return 0
        }
        if (pow.isNegative) {
            return -power(x, -pow, mc)
        }
        val even = (pow.numerator % 2 == 0L)
        return if (even) {
            x.signum(mc)
        } else {
            1
        }
    }

    /**
     * Determines the monotonicity of `sin(x)`, it is required the given
     * limit result is finite.
     */
    fun sin(x: LimitResult<Double>): Int {
        if (!x.isFinite) {
            throw IllegalArgumentException()
        }
        val pi = Math.PI
        val t = x.value.value % (2 * pi)
        fun determineInPi(x: Double, d: LimitDirection): Int {
            val halfPi = pi / 2
            return when {
                x < halfPi -> 1
                x > halfPi -> -1
                else -> {
                    when (d) {
                        LimitDirection.LEFT -> 1
                        LimitDirection.RIGHT -> 1
                        else -> 0
                    }
                }
            }
        }
        return if (t >= pi) {
            -determineInPi(t - pi, x.direction)
        } else {
            determineInPi(t, x.direction)
        }
    }

    /**
     * Determines the monotonicity of `cos(x)`, it is required the given
     * limit result is finite.
     */
    fun cos(x: LimitResult<Double>): Int {
        return sin(Limit.addConst(x, Calculators.doubleCal()) { Math.PI / 2 })
    }


}