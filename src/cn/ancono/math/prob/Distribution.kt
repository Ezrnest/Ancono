package cn.ancono.math.prob

import cn.ancono.math.numberModels.ComplexD
import cn.ancono.math.set.MathSet
import java.util.*
import kotlin.math.sqrt


/*
 * Created by liyicheng at 2021-04-22 22:00
 */

/**
 * Describes a probability distribution.
 */
interface Distribution<T> {

    /**
     * Gets a new random variable from this distribution.
     */
    fun randomVariable(): RandomVariable<T>

    /**
     * Gets the name of this distribution
     */
    val name: String


    /**
     * Generates a random sample from this distribution. An instance of random
     * can be provided.
     */
    fun sample(rd: Random?): T

    /**
     * Generates a random sample from this distribution.
     */
    fun sample(): T {
        return sample(null)
    }

    /**
     * Generates [n] random samples from this distribution as a list. An instance of random
     * can be provided.
     */
    fun sample(n: Int, rd: Random?): List<T>

    /**
     * Generates [n] random samples from this distribution as a list.
     */
    fun sample(n: Int): List<T> {
        return sample(n, null)
    }

    /**
     * Returns a sequence of i.i.d. samples from this distribution. An instance of random
     * can be provided.
     */
    fun samples(rd: Random?): Sequence<T>

    /**
     * Returns a sequence of i.i.d. samples from this distribution.
     */
    fun samples(): Sequence<T> {
        return samples(null)
    }

    /**
     * Returns the support of this distribution.
     */
    val support: MathSet<T>

    /**
     * Returns the value of the cumulative distribution function (cdf) at [x], that is
     *
     *     F(x) = P { X < x }
     *
     * where `X` is a random variable that follows this distribution.
     */
    fun cdf(x: T): Double

    /**
     * Returns the mean of this distribution.
     */
    val mean: Double

    /**
     * Returns the variance of this distribution.
     */
    val variance: Double

    /**
     * The characteristic function of this distribution, that is `f(t) = E exp(itX)`.
     */
    fun chf(t: ComplexD): ComplexD

    /**
     * Returns the standard error of this distribution, which is the square root of the variance.
     */
    val std: Double
        get() = sqrt(variance)


}

abstract class AbstractDistribution<T> : Distribution<T> {
    private val rd: Random = Random()

    protected abstract fun sample0(rd: Random): T

    protected open fun sample0(n: Int, rd: Random): List<T> {
        return (0 until n).map { sample0(rd) }
    }

    protected open fun samples0(rd: Random): Sequence<T> {
        return generateSequence { sample0(rd) }
    }

    override fun sample(rd: Random?): T {
        return sample0(rd ?: this.rd)
    }

    override fun sample(n: Int, rd: Random?): List<T> {
        return sample0(n, rd ?: this.rd)
    }

    override fun samples(rd: Random?): Sequence<T> {
        return samples0(rd ?: Random())
    }

    override fun toString(): String {
        return name
    }
}

interface ContinuousDist : Distribution<Double> {

    /**
     * Returns the value of point density function at [x].
     *
     * This method is the same as [pdf].
     */
    fun density(x: Double): Double

    /**
     * Returns the value of point density function (ppf) at [x].
     *
     * This method is the same as [density].
     */
    fun pdf(x: Double) = density(x)

    /**
     * Returns the value of percent point function (inverse of cdf) at [y].
     *
     * @see cdf
     */
    fun ppf(y: Double): Double
}

abstract class AbstractContinuousDist : AbstractDistribution<Double>(), ContinuousDist {
    override fun sample0(n: Int, rd: Random): List<Double> {
        return DoubleArray(n) {
            sample0(rd)
        }.asList()
    }
}

interface DiscreteDist : Distribution<Int> {

    /**
     * Returns the value of point mass function (pmf) at [x], which is the probability of the
     * distribution taking value `x`.
     */
    fun pmf(x: Int): Double

}
















