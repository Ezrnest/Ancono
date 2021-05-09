package cn.ancono.math.prob

import cn.ancono.math.discrete.combination.CombUtils
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.ComplexD
import cn.ancono.math.numberModels.api.*
import cn.ancono.math.numeric.SpecialFunctions
import cn.ancono.math.set.FiniteInterval
import cn.ancono.math.set.Interval
import cn.ancono.math.set.MathSet
import cn.ancono.math.set.MathSets
import cn.ancono.math.times
import java.util.*
import kotlin.math.*


/*
 * Created by liyicheng at 2021-05-08 20:19
 */

internal object RandomNumbersImpl {
    fun generatePoisson(rd: Random, lambda: Double, expNLambda: Double): Int {
        val d = rd.nextDouble()
        var total = expNLambda
        var current = expNLambda
        var i = 0
        while (total < d) {
            i++
            current = current * lambda / i
            total += current
        }
        return i
    }

    fun generateBinomial(rd: Random, n: Int, p: Double): Int {
        var i = 0
        repeat(n) {
            if (rd.nextDouble() <= p) {
                i++
            }
        }
        return i
    }

    fun generatePascal(rd: Random, r: Int, p: Double): Int {
        var i = 0
        repeat(r) {
            do {
                i++
            } while (rd.nextDouble() > p)
        }
        return i
    }

}

/**
 * The normal distribution `N(mu,sigma^2)`. The density function is
 *
 *     (2\pi)^{-1/2} exp( -(x-\mu)^2 / (2\sigma^2) )
 *
 *
 *
 */
class NormalDist(val mu: Double = 0.0, val sigma: Double = 1.0) : AbstractContinuousDist(), ContinuousDist {
    init {
        require(sigma > 0)
    }

    override val mean: Double
        get() = mu

    override val variance: Double = sigma * sigma

    override val name: String = "N($mu,$variance)"


    override fun randomVariable(): RandomVariable<Double> {
        return RandomVariables.normal(mu, sigma)
    }


    override fun sample0(rd: Random): Double {
        return (rd.nextGaussian() + mu) * sigma
    }


    override fun density(x: Double): Double {
        var t: Double = (x - mu) / sigma
        t = t * t / 2.0
        return exp(-t) / (sqrt(2 * PI) * sigma)
    }

    override fun cdf(x: Double): Double {
        return SpecialFunctions.normalCDF((x - mu) / sigma)
    }

    override fun ppf(y: Double): Double {
        return SpecialFunctions.normalPPF(y) * sigma + mu
    }

    /**
     * The characteristic function:
     *
     *     f(t) = exp(iat - \sigma^2 t^2 / 2)
     */
    override fun chf(t: ComplexD): ComplexD {
        val p = t * (ComplexD.imag(mu) - ComplexD.real(0.5 * variance) * t)
        return ComplexD.exp(p)
    }


    override val support: Interval<Double>
        get() = Interval.universe(Calculators.doubleCal())
}

class UniformDist(val a: Double = 0.0, val b: Double = 1.0) : AbstractContinuousDist() {
    init {
        require(a < b)

    }


    val d = 1 / (b - a)

    override fun randomVariable(): RandomVariable<Double> {
        return RandomVariables.uniform(a, b)
    }

    override val name: String
        get() = "U($a,$b)"
    override val support: MathSet<Double> = Interval.openInterval(a, b, Calculators.doubleDev())

    override fun cdf(x: Double): Double {
        if (x <= a) {
            return 0.0
        }
        if (x >= b) {
            return 1.0
        }
        return (x - a) * d
    }

    override val mean: Double
        get() = (a + b) / 2
    override val variance: Double
        get() = (b - a).pow(2) / 12

    override fun chf(t: ComplexD): ComplexD {
        val n = ComplexD.expIt(t.multiply(b)) - ComplexD.expIt(t.multiply(a))
        val d = ComplexD.I * t.multiply(b - a)
        return n / d
    }

    override fun sample0(rd: Random): Double {
        return rd.nextDouble() * (b - a) + a
    }

    override fun density(x: Double): Double {
        if (x in a..b) {
            return d
        }
        return 0.0
    }

    override fun ppf(y: Double): Double {
        require(y in 0.0..1.0)
        return y * (b - a) + a
    }
}

/**
 * Gamma distribution, the density is
 *
 *     p(x) = k^r / Gamma(r) * x^{r-1} e^{-kx}
 */
abstract class GammaDist(
        /**
         * The scale parameter.
         */
        val k: Double,
        /**
         * The shape parameter.
         */
        val r: Double) : AbstractContinuousDist() {
    init {
        require(k > 0)
        require(r > 0)
    }

    override val name: String
        get() = "Gamma($k,$r)"


    override val support: Interval<Double> = Interval.positive(Calculators.doubleCal())


    override fun cdf(x: Double): Double {
        TODO()
    }

    override val mean: Double
        get() = r / k
    override val variance: Double
        get() = r / (k * k)

    override fun chf(t: ComplexD): ComplexD {
        return (ComplexD.ONE - t.divide(k) * ComplexD.I).pow(-ComplexD.real(r))
    }

    private val c: Double = k.pow(r) * SpecialFunctions.gamma(r)

    /**
     *     k^r / Gamma(r) * x^{r-1} e^{-k x}
     */
    override fun density(x: Double): Double {
        if (x <= 0) {
            return 0.0
        }
        return c * x.pow(r - 1) * exp(-k * x)
    }


    override fun ppf(y: Double): Double {
        TODO("Not yet implemented")
    }
}

class ExpDist(lambda: Double) : GammaDist(lambda, 1.0) {

    override fun randomVariable(): RandomVariable<Double> {
        return RandomVariables.exponent(k)
    }


    override val name: String = "Exp($lambda)"

    override fun density(x: Double): Double {
        if (x < 0.0) {
            return 0.0
        }
        //k e^{k x}
        return exp(-x * k) * k
    }

    override fun cdf(x: Double): Double {
        if (x < 0) {
            return 0.0
        }
        return 1 - exp(-x * k)
    }

    override val mean: Double
        get() = 1 / k
    override val variance: Double
        get() = 1 / (k * k)

    override fun sample0(rd: Random): Double {
        return -ln(rd.nextDouble()) / k
    }


    override fun ppf(y: Double): Double {
        return -ln(1 - y) / k
    }
}

class GammaIntDist(k: Double, r: Int) : GammaDist(k, r.toDouble()) {

    val rInt: Int = r

    override fun sample0(rd: Random): Double {
        return (0 until rInt).sumOf { -ln(rd.nextDouble()) / k }
    }

    override fun randomVariable(): RandomVariable<Double> {
        val exp = RandomVariables.exponent(k)
        return RandomVariables.sum(RandomVariables.iid(exp, rInt))
    }


}

class ChiSquareDist(val n: Int) : GammaDist(0.5, n / 2.0) {
    override fun randomVariable(): RandomVariable<Double> {
        return RandomVariables.chiSquare(n)
    }

    override val mean: Double
        get() = n.toDouble()

    override val variance: Double
        get() = 2.0 * n

    override fun sample0(rd: Random): Double {
        return (0 until n).sumOf {
            val t = rd.nextGaussian()
            t * t
        }
    }

    override val name: String
        get() = "χ2($n)"
}

class CauchyDist(val lambda: Double, val mu: Double) : AbstractContinuousDist() {
    override fun randomVariable(): RandomVariable<Double> {
        return RandomVariables.cauchy(lambda, mu)
    }

    override val name: String
        get() = "Cauchy($lambda, $mu)"
    override val support: Interval<Double>
        get() = Interval.universe(Calculators.doubleCal())

    /**
     *     arctan((x-mu)/lambda) / pi + 1/2
     */
    override fun cdf(x: Double): Double {
        return atan((x - mu) / lambda) / PI + 0.5
    }

    override val mean: Double
        get() = throw ArithmeticException("Mean of Cauchy dist doesn't exist")
    override val variance: Double
        get() = throw ArithmeticException("Variance of Cauchy dist doesn't exist")

    override fun chf(t: ComplexD): ComplexD {
        return ComplexD.exp(ComplexD.I * mu * t - t.modAsC() * lambda)
    }

    override fun sample0(rd: Random): Double {
        return ppf(rd.nextDouble())
    }

    override fun density(x: Double): Double {
        val t = (x - mu) / lambda
        return 1.0 / (PI * lambda * (1 + t * t))
    }

    override fun ppf(y: Double): Double {
        return mu + lambda * tan(PI * (y - 0.5))
    }
}

class BinomialDist(val n: Int, val p: Double) : AbstractDistribution<Int>(), DiscreteDist {
    init {
        require(p in 0.0..1.0)
        require(n > 0)
    }

    /**
     * `q = 1-p`.
     */
    val q: Double
        get() = 1 - p


    override fun randomVariable(): RandomVariable<Int> {
        return RandomVariables.binomial(n, p)
    }

    override val name: String = "B($n,$p)"


    override val support: FiniteInterval<Int> = Interval.rangeOf(0, n + 1)

    override fun sample0(rd: Random): Int {
        return RandomNumbersImpl.generateBinomial(rd, n, p)
    }

    override fun pmf(x: Int): Double {
        if (x !in support) {
            return 0.0
        }
        val c = CombUtils.combination(n, x)
        return c * p.pow(x) * q.pow(n - x)
    }

    override fun cdf(x: Int): Double {
        return (0 until x).sumOf { pmf(it) }
    }

    override val mean: Double
        get() = n * p
    override val variance: Double
        get() = n * p * q


    override fun chf(t: ComplexD): ComplexD {
        return (ComplexD.real(q) + ComplexD.real(p) * ComplexD.expIt(t)).pow(n.toLong())
    }
}

class PascalDist(val r: Int, val p: Double) : AbstractDistribution<Int>(), DiscreteDist {

    val q: Double
        get() = p

    override fun sample0(rd: Random): Int {
        return RandomNumbersImpl.generatePascal(rd, r, p)
    }

    override fun randomVariable(): RandomVariable<Int> {
        return RandomVariables.pascal(r, p)
    }

    override val name: String
        get() = "Pascal($r, $p)"
    override val support: MathSet<Int>
        get() = MathSets.NaturalNumbers

    override fun cdf(x: Int): Double {
        return (r until x).sumOf { pmf(it) }
    }

    override val mean: Double
        get() = r / p

    override val variance: Double
        get() = r * q / (p * p)

    /**
     *     [pe^{it} / (1 - qe^{it})]^r
     */
    override fun chf(t: ComplexD): ComplexD {
        val eit = ComplexD.expIt(t)
        return (eit * p / (ComplexD.ONE - eit * q)).pow(r.toLong())
    }

    override fun pmf(x: Int): Double {
        if (x < r) {
            return 0.0
        }
        return CombUtils.combination(x - 1, r - 1) * p.pow(r) * q.pow(x - r)
    }

}

/**
 *
 *
 *
 */
class PoissonDist(val lambda: Double) : AbstractDistribution<Int>(), DiscreteDist {


    override val name: String
        get() = "Poisson($lambda)"

    override val support: MathSet<Int>
        get() = MathSets.NaturalNumbers

    override fun randomVariable(): RandomVariable<Int> {
        return RandomVariables.poisson(lambda)
    }

    private val expNLambda = exp(-lambda)

    /**
     * lambda^x / x! * e^{-lambda}
     */
    override fun pmf(x: Int): Double {
        if (x < 0) {
            return 0.0
        }
        return lambda.pow(x) / CombUtils.factorial(x) * expNLambda
    }

    override fun cdf(x: Int): Double {
        return (0 until x).sumOf { pmf(it) }
    }

    override val mean: Double
        get() = lambda
    override val variance: Double
        get() = lambda

    override fun chf(t: ComplexD): ComplexD {
        val eit = ComplexD.expIt(t)
        return ComplexD.exp((eit - ComplexD.ONE).multiply(lambda))
    }

    override fun sample0(rd: Random): Int {
        return RandomNumbersImpl.generatePoisson(rd, lambda, expNLambda)
    }

}

