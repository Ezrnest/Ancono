package cn.ancono.math.numberModels.structure

import cn.ancono.math.algebra.abstractAlgebra.calculator.UFDCalculator
import cn.ancono.math.numberModels.api.minus
import cn.ancono.math.numberModels.api.plus
import cn.ancono.math.numberModels.api.times
import cn.ancono.utilities.structure.Pair

//Created by lyc at 2020-03-01 13:59
/**
 * Provides utility algorithms for polynomials on a ring and an UFD.
 */
object PolynomialSup {
    /**
     * Performs the pseudo division of two polynomials on a ring. This algorithm finds `Q` and `R` such that
     * `d^(A.degree - B.degree + 1) A = BQ + R` and `R.degree < B.degree`. It is required that `B` is not zero and
     * `A.degree >= B.degree`.
     *
     * @param T the math calculator for [T] should at least be a ring calculator.
     */
    @JvmStatic
    fun <T : Any> pseudoDivision(A: Polynomial<T>, B: Polynomial<T>): Pair<Polynomial<T>, Polynomial<T>> {
        /*
        See Algorithm 3.1.2, page 112 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen
        Created by lyc at 2020-03-01 14:25
         */
        val m = A.degree
        val n = B.degree
        require(!B.isZero())
        require(m >= n)
        val mc = A.mathCalculator

        val d = B.first()
        var R = A
        var Q = Polynomial.zero(mc)
        var e = m - n + 1
        while (!R.isZero() && R.degree >= B.degree) {
            val S = Polynomial.powerX(R.degree - B.degree, R.first(), mc)
            Q = d * Q + S
            R = d * R - S * B
            e -= 1
        }
        val q = mc.pow(d, e.toLong())
        Q = Q.multiply(q)
        R = R.multiply(q)
        return Pair(Q, R)
    }

    /**
     * Performs the pseudo division of two polynomials on a ring and returns only the remainder.
     * This algorithm finds `Q` and `R` such that
     * `d^(A.degree - B.degree + 1) A = BQ + R` and `R.degree < B.degree`. It is required that `B` is not zero and
     * `A.degree >= B.degree`.
     *
     * @param T the math calculator for [T] should at least be a ring calculator.
     */
    @JvmStatic
    fun <T : Any> pseudoDivisionR(A: Polynomial<T>, B: Polynomial<T>): Polynomial<T> {
        /*
        See Algorithm 3.1.2, page 112 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen
        Created by lyc at 2020-03-01 14:25
         */
        require(!B.isZero())
        val m = A.degree
        val n = B.degree
        if (m < n) {
            return A
        }
        val mc = A.mathCalculator
        val d = B.first()
        var R = A
        var e = m - n + 1
        while (!R.isZero() && R.degree >= B.degree) {
            val S = Polynomial.powerX(R.degree - B.degree, R.first(), mc)
            R = d * R - S * B
            e -= 1
        }
        val q = mc.pow(d, e.toLong())
        R = R.multiply(q)
        return R
    }


    /**
     * Computes the GCD of two polynomials on an UFD.
     *
     * It is required that the calculator of [f] is an instance of [UFDCalculator].
     *
     * @see [subResultantGCD]
     */
    @JvmStatic
    fun <T : Any> primitiveGCD(f: Polynomial<T>, g: Polynomial<T>): Polynomial<T> {
        if (f.isZero()) {
            return g
        }
        if (g.isZero()) {
            return f
        }
        /*
        See Algorithm 3.2.10, page 117 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen
        Created by lyc at 2020-03-01 16:02
         */
        val mc = f.mathCalculator
        @Suppress("UNCHECKED_CAST")
        val rc = mc as UFDCalculator<T>
        val a = f.cont()
        val b = g.cont()
        val d = rc.gcd(a, b)
        var A = f.divide(a)
        var B = g.divide(b)
        while (true) {
            val R = pseudoDivisionR(A, B)
            if (R.isZero()) {
                break
            }
            if (R.isConstant) {
                B = Polynomial.one(mc)
                break
            }
            A = B
            B = R.toPrimitive()
        }
        return d * B
    }

    /**
     * Computes the GCD of two polynomials on an UFD using sub-resultant method.
     *
     *
     *
     * @see [primitiveGCD]
     */
    @JvmStatic
    fun <T : Any> subResultantGCD(f: Polynomial<T>, g: Polynomial<T>): Polynomial<T> {
        /*
        See Algorithm 3.3.1, page 118 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen
        Created by lyc at 2020-03-01 16:02
         */
        var A: Polynomial<T>
        var B: Polynomial<T>
        if (f.degree > g.degree) {
            A = f
            B = g
        } else {
            A = g
            B = f
        }
        if (B.isZero()) {
            return A
        }
        val mc = f.mathCalculator

        @Suppress("UNCHECKED_CAST")
        val rc = mc as UFDCalculator<T>
        val a = A.cont()
        val b = B.cont()
        val d = rc.gcd(a, b)
        var g1 = mc.one
        var h1 = mc.one
        while (true) {
            val t = (A.degree - B.degree).toLong()
            val R = pseudoDivisionR(A, B)
            if (R.isZero()) {
                break
            }
            if (R.isConstant) {
                B = Polynomial.one(mc)
                break
            }
            A = B
            B = R.divide(mc.multiply(g1, mc.pow(h1, t)))
            g1 = A.first()
            h1 = mc.multiply(h1, mc.pow(mc.divide(g1, h1), t))
        }
        return d * B.toPrimitive()
    }


}

//fun main(args: Array<String>) {
//    val mc = Calculators.getCalInteger()
//    val f = Polynomial.valueOf(mc, 1, 2, 1)
//    val g = Polynomial.valueOf(mc, 1, 1)
//    val (q,r) = PolynomialOnRing.pseudoDivision(f, g)
//    println("$q,  $r")
//    println(g * q + r)
//    println(PolynomialOnRing.subResultantGCD(f,g))
//}
