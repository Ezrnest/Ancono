package cn.ancono.math.numeric

import cn.ancono.math.numberModels.ComplexD
import cn.ancono.math.numberModels.ComplexD.*
import cn.ancono.math.numberModels.api.*
import cn.ancono.utilities.SNFSupport
import kotlin.math.expm1
import kotlin.math.ln
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
            676.5203681218851, -1259.1392167224028, 771.32342877765313, -176.61502916214059, 12.507343278686905, -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7
    )
    private val SQRT_2PI = sqrt(2 * Math.PI)


    private const val DROP_IM_THRESHOLD = 1e-07
    private fun dropIm(z: ComplexD): ComplexD {
        return if (z.im() < DROP_IM_THRESHOLD) {
            real(z.re())
        } else z
    }


    /**
     * Computes the value of the Gamma function.
     *
     */
    fun gamma(z: ComplexD): ComplexD {
        // this algorithm is based on https://en.wikipedia.org/wiki/Lanczos_approximation
        val y: ComplexD
        if (z.re() < 0.5) {
            y = PI / sin(PI * z) * gamma(ONE - z)
        } else {
            var x = real(0.99999999999980993)
            for ((i, v) in GAMMA_COEFFICIENTS.withIndex()) {
                x += (z + real(i.toDouble())).reciprocal().multiply(v)
            }
            val t = z + real(GAMMA_COEFFICIENTS.size - 1.5)
            y = t.pow(z - real(0.5)) * exp(-t) * x.multiply(SQRT_2PI)
        }
        return dropIm(y)
    }

    fun gamma(x: Double): Double {
        return gamma(real(x)).re()
    }

    /*
    TODO: implement beta function, zeta function and ...

     */

    /**
     * Cumulative density function for standard normal distribution, usually denoted as `Φ(x)`.
     */
    fun normalCDF(x: Double): Double {
        val a = 0.647 - 0.021 * x
        val t = -a * (x * x)
        /*
        Reference:
        https://www.hrpub.org/download/20181230/MS1-13412146.pdf
         */
        return 0.5 * (1 + sqrt(-expm1(t)))
    }

    /**
     * The inverse of cdf of standard normal distribution.
     */
    fun normalPPF(x: Double): Double {

        require(x in 0.0..1.0)
        /*
        Reference:
        https://www.johndcook.com/blog/normal_cdf_inverse/
         */
        val c0 = 2.515517
        val c1 = 0.802853
        val c2 = 0.010328
        val d1 = 1.432788
        val d2 = 0.189269
        val d3 = 0.001308
        fun rationalApprox(x: Double): Double {
            val t = sqrt(-2 * ln(x))
            val top = (c0 + t * (c1 + t * c2))
            val bottom = (1 + t * (d1 + t * (d2 + t * d3)))
            return t - top / bottom
        }
        return if (x < 0.5) {
            -rationalApprox(x)
        } else {
            rationalApprox(1 - x)
        }
    }

}


fun main() {
    for (x in 0 until 11) {
        print(SNFSupport.format(x * 0.4 + 0.2))
        println(":  ")
        val phiX = SpecialFunctions.normalCDF(x * 0.4 + 0.2)
        println(phiX)
        println(SpecialFunctions.normalPPF(phiX))
        println()
    }
    println(SpecialFunctions.normalPPF(0.5))

}