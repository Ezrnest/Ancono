package cn.timelives.java.math.calculus

import cn.timelives.java.math.MathUtils
import cn.timelives.java.math.numberModels.Fraction
import cn.timelives.java.math.numberModels.structure.Polynomial
import cn.timelives.java.math.numberTheory.combination.CombUtils
import kotlin.coroutines.experimental.buildSequence


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
        val coe = Fraction.valueOf(MathUtils.powMinusOne(n - i), CombUtils.combination(n, i), 1L)
        return a.multiply(coe).shift(i)
    }

    fun bernsteinPolyList(n: Int): List<Polynomial<Fraction>> {
        val coes = CombUtils.binomialsOf(n)
        val t_1 = Polynomial.valueOf(Fraction.calculator, Fraction.ONE, Fraction.NEGATIVE_ONE)
        var base = Polynomial.powerX(n, Fraction.calculator)
        val re = ArrayList<Polynomial<Fraction>>(n + 1)
        for (i in (0..n)) {
            val coe = coes[(n - i).toLong()]
            re.add(base.multiply(Fraction.valueOf(coe)))
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

    fun legendrePolySeq(): Sequence<Polynomial<Fraction>> = buildSequence {
        var l0 = Polynomial.constant(Fraction.calculator, Fraction.ONE)
        var l1 = Polynomial.oneX(Fraction.calculator)
        yield(l0)
        yield(l1)
        var n = 2L
        while (true) {
            // nP_n (x) - (2n - 1)xP_{n-1} (x) + (n - 1)P_{n-2} (x)= 0
            // P_n (x) = ((2n - 1)xP_{n-1} (x) - (n - 1)P_{n-2} (x) ) / n
            val a = Fraction.valueOf(2 * n - 1, n)
            val b = Fraction.valueOf(-1, n - 1, n)
            val next = l1.shift(1).multiply(a).add(l0.multiply(b))
            yield(next)
            l0 = l1
            l1 = next
            n++
        }
    }

    fun hermitePoly(n: Int): Polynomial<Fraction> {
        TODO()
    }

    fun laguerrePoly(n: Int): Polynomial<Fraction> {
        TODO()
    }

    fun chebyshevPoly(n: Int): Polynomial<Fraction> {
        TODO()
    }

    fun bernoulliPoly(n: Int): Polynomial<Fraction> {
        TODO()
    }
}

fun main(args: Array<String>) {
//    DefinedPolynomials.legendrePolySeq().take(15).forEach { println(it) }
//    val re = DefinedPolynomials.bernsteinPolyList(5).reduce { a, b -> a.add(b) }
//    println(re)

}