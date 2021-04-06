package cn.ancono.math.numeric

import cn.ancono.math.numberModels.ComplexI
import cn.ancono.math.numberModels.ComplexI.*
import cn.ancono.math.numberModels.api.*
import kotlin.math.sqrt

/*
 * Created by liyicheng at 2020-06-02 20:46
 */
/**
 * Contains numeric implementations of several special functions.
 *
 * @author liyicheng
 */
object SpecialFunctions {
    /*

    
     */
    private val GAMMA_COEFFICIENTS = doubleArrayOf(
        676.5203681218851, -1259.1392167224028
            , 771.32342877765313
            , -176.61502916214059
            , 12.507343278686905
            , -0.13857109526572012
            , 9.9843695780195716e-6
            , 1.5056327351493116e-7
    )
    private val SQRT_2PI = sqrt(2 * Math.PI)


    private const val DROP_IM_THRESHOLD = 1e-07
    private fun dropIm(z: ComplexI): ComplexI {
        return if (z.im() < DROP_IM_THRESHOLD) {
            real(z.re())
        } else z
    }


    /**
     * Computes the value of the Gamma function.
     *
     */
    fun gamma(z: ComplexI): ComplexI {
        // this algorithm is based on https://en.wikipedia.org/wiki/Lanczos_approximation
        val y: ComplexI
        if (z.re() < 0.5) {
            y = PI / sinZ(PI * z) * gamma(ONE - z)
        } else {
            var x = real(0.99999999999980993)
            for ((i, v) in GAMMA_COEFFICIENTS.withIndex()) {
                x += (z + real(i.toDouble())).reciprocal().multiply(v)
            }
            val t = z + real(GAMMA_COEFFICIENTS.size - 1.5)
            y = t.exp(z - real(0.5)) * expZ(-t) * x.multiply(SQRT_2PI)
        }
        return dropIm(y)
    }

    fun gamma(x: Double): Double {
        return gamma(real(x)).re()
    }

    /*
    TODO: implement CDF of normal and other distribution, beta function, zeta function and ...

     */

    /**
     * Cumulative density function for standard normal distribution, usually denoted as `Φ(x)`.
     */
    fun normalCDF(x: Double): Double {
        TODO()
    }

    /**
     * The inverse of cdf of standard normal distribution.
     */
    fun normalPPF(x: Double): Double {
        TODO()
    }


}
