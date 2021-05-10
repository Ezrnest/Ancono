package cn.ancono.math.calculus

import cn.ancono.math.AbstractMathObject
import cn.ancono.math.IMathObject
import cn.ancono.math.MathUtils
import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.discrete.combination.CombUtils
import cn.ancono.math.numberModels.BigFraction
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.api.*
import cn.ancono.math.numberModels.structure.Polynomial
import cn.ancono.math.set.Interval
import java.util.function.Function

/**
 * Describes a family of polynomials on an interval with respect to a non-negative weight function.
 * The polynomials are orthogonal under the inner product induced by integrating the product
 * of two functions and the weight functions.
 *
 * Assume the weight function is `w`, then the inner product is
 *
 *     (f,g) = ∫f(x)g(x)w(x)dx
 *
 * Denote the polynomials as `p_0,p_1,...,p_n,...`, then the degree of `p_n` is `n`,
 * and `(p_n,p_n) = normSq(n)`, `(p_i,p_j) = 0, i!=j`.
 *
 * See: [Orthogonal polynomials](https://en.wikipedia.org/wiki/Orthogonal_polynomials)
 *
 *
 * @author
 * Created by lyc at 2021-03-31 12:29
 */
abstract class OrthPolynomials<T>(val name: String, mc: QuotientCalculator<T>)
    : AbstractMathObject<T, QuotientCalculator<T>>(mc) {

    /**
     * The domain on which the polynomials are defined.
     */
    abstract val domain: Interval<T>

    /**
     * The weight function
     */
    abstract fun weight(x: T): T

    /**
     * Returns the sequence of the orthogonal polynomials.
     */
    abstract val sequence: Sequence<Polynomial<T>>

    /**
     * The norm of the `n-th` orthogonal polynomial.
     *
     * @param n the index of polynomial, starting from zero.
     */
    @Suppress("UNCHECKED_CAST")
    open fun norm(n: Int): T {
        return (calculator as RealCalculator<T>).squareRoot(normSq(n))
    }

    /**
     * The square of the norm of the `n-th` orthogonal polynomial.
     */
    abstract fun normSq(n: Int): T


    override fun toString(nf: NumberFormatter<T>): String {
        return "$name Polynomials"
    }

    override fun valueEquals(obj: IMathObject<T>): Boolean {
        return this == obj
    }


    val sequenceUnitized: Sequence<Polynomial<T>>
        get() = sequence.mapIndexed { n, p -> p.divide(norm(n)) }
}

/**
 * The family of Legendre orthogonal polynomials: `L_n`.
 *
 * * Domain: `[-1,1]`
 * * Weight: `w(x) = 1`
 * * Norm Square: `(L_n,L_n) = 2/(2n+1)`.
 * * Recurrence relation: `(n+1)T_{n+1}(x) = (2n+1)T_n(x) - nT_{n-1}(x)`
 *
 * First several polynomials:
 *
 *     L_0(x) = 1
 *     L_1(x) = x
 *     L_2(x) = (3x^2 - 1)/2
 *     L_3(x) = (5x^3 - 3x)/2
 */
class LegendreOrthPoly<T>(mc: QuotientCalculator<T>)
    : OrthPolynomials<T>("Legendre", mc) {
    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): LegendreOrthPoly<N> {
        return LegendreOrthPoly(newCalculator as QuotientCalculator)
    }


    override fun weight(x: T): T {
        return calculator.one
    }

    override fun normSq(n: Int): T {
        return calculator.eval { of(2L) / of(2 * n + 1L) }
    }


    override val domain: Interval<T> = Interval.closedInterval(mc.negate(mc.one), mc.one, mc)

    override val sequence: Sequence<Polynomial<T>> = sequence {
        var p0 = Polynomial.one(mc)
        val x = Polynomial.oneX(mc)
        var p1 = x
        yield(p0)
        yield(p1)
        var n = 1
        while (true) {
            var p2 = mc.eval { (2 * n + 1).v * p1.shift(1) - n.v * p0 }
            p2 = p2.divideLong(n + 1L)
            yield(p2)
            p0 = p1
            p1 = p2
            n += 1
        }
    }
}

/**
 * The family of Tchebychev orthogonal polynomials: `T_n`.
 *
 * * Domain: `[-1,1]`
 * * Weight: `w(x) = 1/sqrt(1-x^2)`
 * * Norm Square: `(T_0,T_0) = pi; (T_n,T_n) = pi/2, n > 0`.
 * * Recurrence relation: `T_{n+1}(x) = 2xT_n(x) - T_{n-1}(x)`
 *
 * First several polynomials:
 *
 *     T_0(x) = 1
 *     T_1(x) = x
 *     T_2(x) = 2x^2 - 1
 *     T_3(x) = 4x^3 - 3x
 *
 *
 * Implementation Note: The norm requires constant value pi, calculators must support the constant value.
 *
 * See: [Tchebychev orthogonal polynomials](https://en.wikipedia.org/wiki/Chebyshev_polynomials)
 */
class TchebychevOrthPoly<T>(mc: QuotientCalculator<T>)
    : OrthPolynomials<T>("Tchebychev", mc) {

    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): TchebychevOrthPoly<N> {
        return TchebychevOrthPoly(newCalculator as QuotientCalculator)
    }

    override fun weight(x: T): T {
        return (calculator as RealCalculator).eval { one / squareRoot(one - x * x) }
    }

    override fun normSq(n: Int): T {
        val mc = calculator as RealCalculator
        val pi = mc.constantValue("pi")!!
        return if (n == 0) {
            pi
        } else {
            mc.divideLong(pi, 2)
        }
    }


    override val domain: Interval<T> = Interval.closedInterval(mc.negate(mc.one), mc.one, mc)

    override val sequence: Sequence<Polynomial<T>> = sequence {
        var p0 = Polynomial.one(mc)
        val x = Polynomial.oneX(mc)
        var p1 = x
        yield(p0)
        yield(p1)
        var n = 1
        while (true) {
            val p2 = mc.eval { p1.shift(1).multiply(2L) - p0 }
            yield(p2)
            p0 = p1
            p1 = p2
            n += 1
        }
    }
}

/**
 * The family of Laguerre polynomials, `L_n(x)`
 *
 * * Domain: `[0,+∞)`
 * * Weight: `w(x) = exp(-x)`
 * * Norm Square: `(L_n,L_n) = (n!)^2`.
 * * Recurrence relation: `(n+1)L_{n+1}(x) = (2n+1 - x)He_n(x) - nHe_{n-1}'(x)`
 *
 * The first several polynomials are
 *
 *     L_0(x) = 1
 *     L_1(x) = -x+1
 *     L_2(x) = (x^2 - 4x + 2)/2
 *     L_3(x) = （-x^3 + 9x^2 - 18x + 6）/6
 *
 *
 *
 * Implementation Note:
 * Norm values are extremely large, overflow will happen for big `n`.
 */
class LaguerreOrthPoly<T>(mc: QuotientCalculator<T>)
    : OrthPolynomials<T>("Laguerre", mc) {


    override val domain: Interval<T> = Interval.toPositiveInf(mc.zero, true, mc)


    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): LaguerreOrthPoly<N> {
        return LaguerreOrthPoly(newCalculator as QuotientCalculator)
    }

    override fun weight(x: T): T {
        val mc = calculator as RealCalculator
        return mc.exp(mc.negate(x))
    }

    override fun normSq(n: Int): T {
        val mc = calculator
        return mc.pow(norm(n), 2L)
    }

    override fun norm(n: Int): T {
        return calculator.of(CombUtils.factorial(n))
    }


    override val sequence: Sequence<Polynomial<T>> = sequence {
        var p0 = Polynomial.one(mc)
        val n1 = mc.negate(mc.one)
        var p1 = Polynomial.linear(mc, n1, mc.one)
        yield(p0)
        yield(p1)
        var n = 1
        while (true) {
            var p2 = mc.eval {
                Polynomial.linear(mc, n1, (2 * n + 1).v) * p1 - n.v * p0
            }
            p2 = p2.divideLong(n + 1L)
            yield(p2)
            p0 = p1
            p1 = p2
            n += 1
        }
    }

}


/**
 * The family of probabilist's Hermite polynomials, `He_n(x)`
 *
 * * Domain: `(-∞,+∞)`
 * * Weight: `w(x) = exp(-x^2/2)`
 * * Norm Square: `(He_n,He_n) = sqrt(2pi)n!`.
 * * Recurrence relation: `He_{n+1}(x) = xHe_n(x) - He_{n-1}'(x)`
 *
 * The first several polynomials are
 *
 *     He_0(x) = 1
 *     He_1(x) = x
 *     He_2(x) = x^2 -1
 *     He_3(x) = x^3 - 3x
 *     He_4(x) = x^4 - 6x^2 + 3
 *
 *
 * Implementation Note: The norm requires constant value pi, calculators must support the constant value.
 * Norm values are extremely large, overflow will happen for big `n`.
 */
class HermiteOrthPoly<T>(mc: QuotientCalculator<T>)
    : OrthPolynomials<T>("Hermite", mc) {

    override val domain: Interval<T> = Interval.universe(mc)

    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): HermiteOrthPoly<N> {
        return HermiteOrthPoly(newCalculator as QuotientCalculator)
    }


    override fun weight(x: T): T {
        val mc = calculator as RealCalculator
        return mc.eval { exp(-x * x / 2L) }
    }

    override fun normSq(n: Int): T {
        val mc = calculator as RealCalculator
        return mc.eval {
            squareRoot(2.v * constantValue(RealCalculator.STR_PI)!!) *
                    CombUtils.factorial(n)
        }
    }

    override val sequence: Sequence<Polynomial<T>> = sequence {
        yield(Polynomial.one(mc))
        var p = Polynomial.oneX(mc)
        yield(p)
        while (true) {
            val p2 = mc.eval { p.shift(1) - p.derivative() }
            yield(p2)
            p = p2
        }
    }

}

/**
 * Created at 2019/12/26 18:59
 * @author  lyc
 */
object DefinedPolynomials {


    /**
     * Returns the i-th Bernstein polynomial of degree n.
     *
     * See [Bernstein Polynomial](http://mathworld.wolfram.com/BernsteinPolynomial.html)
     */
    fun bernsteinPoly(n: Int, i: Int): Polynomial<Fraction> {
        // B_{n,i} = C_n^i * t^i * (1-t)^{n-i}
        val a = Polynomial.binomialPower(Fraction.NEGATIVE_ONE, n - i, Fraction.calculator)
        val coe = Fraction.of(MathUtils.powOfMinusOne(n - i), CombUtils.combination(n, i), 1L)
        return a.multiply(coe).shift(i)
    }

    fun bernsteinPolyList(n: Int): List<Polynomial<Fraction>> {
        val coes = CombUtils.binomialsOf(n)
        val t_1 = Polynomial.of(Fraction.calculator, Fraction.ONE, Fraction.NEGATIVE_ONE)
        var base = Polynomial.powerX(n, Fraction.calculator)
        val re = ArrayList<Polynomial<Fraction>>(n + 1)
        for (i in (0..n)) {
            val coe = coes[(n - i).toLong()]
            re.add(base.multiply(Fraction.of(coe)))
            base = base.shift(-1).multiply(t_1)
        }
        re.reverse()
        return re
    }


    /**
     * Returns the n-th Legendre polynomial.
     *
     * See [Legendre Polynomial](http://mathworld.wolfram.com/LegendrePolynomial.html)
     */
    fun legendrePoly(n: Int): Polynomial<Fraction> {
        return legendrePolySeq().elementAt(n)
    }

    fun legendrePolySeq(): Sequence<Polynomial<Fraction>> = sequence {
        var l0 = Polynomial.constant(Fraction.calculator, Fraction.ONE)
        var l1 = Polynomial.oneX(Fraction.calculator)
        yield(l0)
        yield(l1)
        var n = 2L
        while (true) {
            // nP_n (x) - (2n - 1)xP_{n-1} (x) + (n - 1)P_{n-2} (x)= 0
            // P_n (x) = ((2n - 1)xP_{n-1} (x) - (n - 1)P_{n-2} (x) ) / n
            val a = Fraction.of(2 * n - 1, n)
            val b = Fraction.of(-1, n - 1, n)
            val next = l1.shift(1).multiply(a).add(l0.multiply(b))
            yield(next)
            l0 = l1
            l1 = next
            n++
        }
    }


    fun bernoulliPoly(n: Int): Polynomial<Fraction> {
        require(n >= 0)
        val comb = CombUtils.binomialsOf(n)
        val list = (0..n).map { k ->
            CombUtils.numBernoulli(n - k).multiply(comb.get(k.toLong()))
        }
        return Polynomial.of(Fraction.calculator, list)
    }

    /**
     * Returns the n-th Bernoulli polynomial.
     */
    @JvmStatic
    fun polynomialBernoulliBig(n: Int): Polynomial<BigFraction> {
        require(n >= 0)
        val list = arrayOfNulls<BigFraction>(n + 1)
        val comb = CombUtils.binomialsBigOf(n)
        val evenBernoulli = CombUtils.numBernoulliEvenBig(n / 2 + 1)
        for (k in 0..n) {
            val i = n - k
            if (i % 2 == 1) {
                list[k] = if (i == 1) {
                    BigFraction.fromFraction(CombUtils.numBernoulli(1)).multiply(comb.get(1L))
                } else {
                    BigFraction.ZERO
                }
            } else {
                val b = evenBernoulli.get(i / 2L)
                list[k] = b.multiply(comb.get(k.toLong()))
            }
        }
        return Polynomial.of(BigFraction.calculator, *list)
    }

    /**
     * The generalized Laguerre polynomials.
     * The recurrence relation is
     *
     *     (n+1)L_{n+1}(x) = (2n+1+α - x)L_n(x) - (n+α)L_{n-1}'(x)
     *
     *
     * The first several terms are:
     *
     *     L_0(x) = 0
     *     L_1(x) = -x + 1 + α
     *
     */
    fun <T> generalizedLaguerrePoly(alpha: T, mc: RealCalculator<T>): Sequence<Polynomial<T>> {
        return sequence {
            var p0 = Polynomial.one(mc)
            val n1 = mc.negate(mc.one)
            var p1 = Polynomial.linear(mc, n1, mc.eval { one + alpha })
            yield(p0)
            yield(p1)
            var n = 1
            while (true) {
                val t1 = mc.eval { (2 * n + 1).v + alpha }
                val t2 = mc.eval { n.v + alpha }
                var p2 = Polynomial.linear(mc, n1, t1) * p1 - t2 * p0
                p2 = p2.divideLong(n + 1L)
                yield(p2)
                p0 = p1
                p1 = p2
                n += 1
            }
        }
    }
}

//fun main() {
////    DefinedPolynomials.legendrePolySeq().take(15).forEach { println(it) }
////    val re = DefinedPolynomials.bernsteinPolyList(5).reduce { a, b -> a.add(b) }
////    println(re)
//    DefinedPolynomials.legendrePolySeq().take(5).forEach { println(it) }
//}