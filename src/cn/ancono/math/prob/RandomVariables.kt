package cn.ancono.math.prob

import cn.ancono.math.numeric.NumericSup
import java.util.*
import java.util.function.DoubleUnaryOperator
import kotlin.math.sqrt


typealias DoubleRV = RandomVariable<Double>
typealias IntRV = RandomVariable<Int>


/*
 * Created by liyicheng at 2020-06-02 17:14
 */

operator fun DoubleRV.plus(y: DoubleRV): DoubleRV {
    return MappedRV.binary(this, y, Double::plus)
}

operator fun DoubleRV.minus(y: DoubleRV): DoubleRV {
    return MappedRV.binary(this, y, Double::minus)
}

operator fun DoubleRV.times(y: DoubleRV): DoubleRV {
    return MappedRV.binary(this, y, Double::times)
}

operator fun DoubleRV.div(y: DoubleRV): DoubleRV {
    return MappedRV.binary(this, y, Double::div)
}

fun <T, R> RandomVariable<T>.map(f: (T) -> R): RandomVariable<R> {
    return UnaryMappedRV(this, f)
}


operator fun DoubleRV.unaryMinus(): DoubleRV {
    return this.map(Double::unaryMinus as (Double) -> Double)
//    return this.map
}


/**
 * Provides methods of constructing random variables.
 */
object RandomVariables {

    /**
     * Returns the given constant as a random variable.
     */
    fun <T : Any> constant(c: T): RandomVariable<T> {
        return ConstantRV(c)
    }

    /**
     * Returns a random variable of uniform distribution.
     */
    fun uniform(lower: Double = 0.0, upper: Double = 1.0): SimpleRV<Double, Double> {
        return IdentityRV(IntervalSpace(lower, upper))
    }

    /**
     * Returns a random variable of Bernoulli distribution:
     *
     *     P{ X = 0 } = p
     *     P{ X = 1 } = 1-p
     */
    fun bernoulli(p: Double = 0.5): SimpleRV<Int, Int> {
        return IdentityRV(BernoulliSpace(p))
    }

    /**
     * Returns a random variable of geometry distribution:
     *
     *     P{ X = n } = q^(n-1) * p    ( n > 0 )
     */
    fun geometry(p: Double = 0.5): SimpleRV<Int, Int> {
        return IdentityRV(GeomSpace(p))
    }


    /**
     * Returns a random variable of binomial distribution:
     *
     *     P{ X = k } = C(n,k) * p^k * q^(n-k)    ( 0<= k <= n )
     */
    fun binomial(n: Int, p: Double = 0.5): SimpleRV<Int, Int> {
        if (n == 1) {
            return bernoulli(p)
        }
        return IdentityRV(BinomialSpace(p, n))
    }

    /**
     * Returns a random variable of pascal distribution:
     *
     *     P{ X = k } = C(k-1,n-1) * p^n * q^(k-n)    ( k >= n )
     */
    fun pascal(n: Int, p: Double = 0.5): SimpleRV<Int, Int> {
        if (n == 1) {
            return geometry(p)
        }
        return IdentityRV(PascalSpace(p, n))
    }

    /**
     * Returns a random variable of normal distribution.
     *
     * @param a the expectation
     * @param sigma the square root of the variance
     */
    fun normal(a: Double = 0.0, sigma: Double = 1.0): SimpleRV<Double, Double> {
        return NormalRV(a, sigma, StandardNormalDistSpace())
    }

    /**
     * Returns a random variable of poisson distribution.
     *
     *     P{ X = n } = k^n / n! * e^(-k)     (n>=0)
     */
    fun poisson(k: Double = 1.0): SimpleRV<Int, Int> {
        return IdentityRV(PoissonSpace(k))
    }

    /**
     * Returns a random variable of exponent distribution.
     *
     *     p(x) = ke^(-kx)    ( x > 0 )
     */
    fun exponent(k: Double = 1.0): SimpleRV<Double, Double> {
        return ExpRV(k, StandardExpSpace())
    }

    /**
     * Returns a random variable of logarithmic normal distribution.
     *
     * If `X ~ N(a,sigma)`, then `e^X ~ logNormal(a,sigma)`
     */
    fun logNormal(a: Double = 0.0, sigma: Double = 1.0): DoubleRV {
        return normal(a, sigma).map(Math::exp)
    }

    /**
     * Returns a random variable of chi-square distribution.
     *
     * If `X_i ~ N(0,1)` iid, and
     *
     *     X = (X_1)^2 + ... + (X_n)^2
     *
     * then `X ~ Χ^2(n)`
     */
    fun chiSquare(n: Int): DoubleRV {
        require(n > 0)
        val ns = (0 until n).map { normal() }
        return map(ns) { list ->
            list.sumByDouble { it * it }
        }
    }

    /**
     * Returns a random variable of t-distribution.
     *
     * If `X ~ N(0,1), Y ~ Χ^2(n)`, and
     *
     *     Z = X / sqrt(Y/n)
     *
     * then `Z ~ t(n)`
     */
    fun tDist(n: Int): DoubleRV {
        require(n > 0)
        val X = normal()
        val Y = chiSquare(n)
        val sqrtN = sqrt(n.toDouble())
        return map2(X, Y) { x, y ->
            sqrtN * x / sqrt(y)
        }
    }

    /**
     * Returns a random variable of F-distribution.
     *
     * If `X ~ Χ^2(m), Y ~ Χ^2(n)`, and
     *
     *     Z = (X / m) / (Y / n)
     *
     * then `Z ~ F(m,n)`
     */
    fun fDist(m: Int, n: Int): DoubleRV {
        require(m > 0 && n > 0)
        val X = chiSquare(m)
        val Y = chiSquare(n)
        val k = n.toDouble() / m
        return map2(X, Y) { x, y ->
            k * x / y
        }
    }
//
//    fun beta()


    /**
     * Returns a list of independent identically distributed random variables.
     */
    fun <T, E : Any> iid(x: SimpleRV<E, T>, n: Int): List<SimpleRV<List<E>, T>> {
        val space = ProductSpace(Collections.nCopies(n, x.space))

        return (0 until n).map { i ->
            object : SimpleRV<List<E>, T>() {
                override val space: ProbSpace<List<E>> = space

                override fun fromPoint(e: List<E>): T {
                    return x.fromPoint(e[i])
                }
            }
        }
    }

    /**
     * Returns a new simple random variable that has the same distribution as the given random variable, and the
     * new one is based on a independent probability space.
     */
    fun <T> copyToSimple(x: RandomVariable<T>): SimpleRV<Event, T> {
        val space = BundledSpace(x.spaces)
        return object : SimpleRV<Event, T>() {
            override val space: ProbSpace<Event> = space

            override fun fromPoint(e: Event): T {
                return x.fromEvent(e)
            }
        }
    }

    /**
     * Returns a sequence(not limited) of independent identically distributed random variables.
     */
    fun <T> iid(x: RandomVariable<T>): Sequence<RandomVariable<T>> = generateSequence {
        copyToSimple(x)
    }

    /**
     * Returns a list of independent identically distributed random variables.
     */
    fun <T> iid(x: RandomVariable<T>, n: Int): List<RandomVariable<T>> = (0 until n).map {
        copyToSimple(x)
    }

    /**
     * Maps the given random variables to a new random variable.
     */
    fun <T, R> map(rvs: List<RandomVariable<T>>, f: (List<T>) -> R): RandomVariable<R> {
        return MappedRV(rvs, f)
    }

    /**
     * Maps the given random variables to a new random variable.
     */
    fun <T, S, R> map2(x: RandomVariable<T>, y: RandomVariable<S>, f: (T, S) -> R): RandomVariable<R> {
        return MappedRV.binary(x, y, f)
    }

    fun sum(rvs: List<DoubleRV>): DoubleRV {
        return map(rvs) { it.sum() }
    }

    fun sumInt(rvs: List<IntRV>): IntRV {
        return map(rvs) { it.sum() }
    }

    fun average(rvs: List<DoubleRV>): DoubleRV {
        return map(rvs) { it.average() }
    }

    /**
     * Estimates the expectation of the random variable `x` basing on the law of large number.
     */
    fun estimateExpectation(x: DoubleRV, times: Int = 1000000): Double {
        return x.getAsSequence().take(times).average()
    }

    fun estimateDensity(x: DoubleRV,
                        from: Double, to: Double, blockCount: Int = 100,
                        times: Int = 1000000): DoubleUnaryOperator {
        require(to > from)
        val interval = from..to
        val counting = IntArray(blockCount)
        val d = (to - from) / blockCount

        repeat(times) {
            val a = x.get()
            if (a in interval) {
                val i = ((a - from) / d).toInt()
                counting[i]++
            }
        }
        val data = DoubleArray(counting.size) { i -> counting[i].toDouble() / times / d }

        return NumericSup.linearInterpolate(from, to, data, d)
    }


    fun estimateDist(x: IntRV, from: Int, to: Int, times: Int = 1000000): DoubleArray {
        require(to >= from)
        val length = to - from + 1
        val counting = IntArray(length)
        repeat(times) {
            val a = x.get()
            val i = a - from
            if (i < length) {
                counting[i]++
            }
        }
        val data = DoubleArray(counting.size) { i -> counting[i].toDouble() / times }
        return data
    }

}