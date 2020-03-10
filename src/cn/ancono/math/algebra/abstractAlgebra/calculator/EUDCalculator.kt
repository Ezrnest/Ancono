package cn.ancono.math.algebra.abstractAlgebra.calculator

import cn.ancono.math.component1
import cn.ancono.math.component2
import cn.ancono.math.exceptions.ExceptionUtil


/*
 * Created by liyicheng at 2020-03-09 19:32
 */
/**
 * Describes a calculator for an Euclidean domain. The fundamental operation of this type of calculator is
 * [divideAndRemainder].
 *
 *
 * See [EuclideanDomain](https://mathworld.wolfram.com/EuclideanDomain.html) for more information.
 */
interface EUDCalculator<T : Any> : UFDCalculator<T> {

    /**
     * Returns a pair of two elements containing {@code (a / b)}
     * followed by {@code (a % b)}.
     *
     * @param
     */
    fun divideAndRemainder(a: T, b: T): cn.ancono.utilities.structure.Pair<T, T>

    /**
     * Returns the quotient part of [divideAndRemainder].
     *
     * @param a the dividend
     * @param b the divisor
     * @return {@code a \ b}
     * @see divideAndRemainder
     */
    @JvmDefault
    fun divideToInteger(a: T, b: T): T = divideAndRemainder(a, b).first

    /**
     * Returns the remainder part of [divideAndRemainder].
     *
     * @param a the dividend
     * @param b the divisor
     * @return {@code a \ b}
     * @see divideAndRemainder
     */
    @JvmDefault
    fun remainder(a: T, b: T): T = divideAndRemainder(a, b).second

    @JvmDefault
    override fun exactDivide(x: T, y: T): T {
        val (q, r) = divideAndRemainder(x, y)
        if (!isZero(r)) {
            ExceptionUtil.notExactDivision(x, y)
        }
        return q
    }

    @JvmDefault
    override fun isExactDivide(a: T, b: T): Boolean {
        return isZero(remainder(a, b))
    }

    @JvmDefault
    override fun gcd(a: T, b: T): T {
        var x = a
        var y = b
        var t: T
        while (!isZero(y)) {
            t = y
            y = remainder(x, y)
            x = t
        }
        return a
    }

    /**
     * Returns the greatest common divisor of two numbers and a pair of number (u,v) such that
     * <pre>ua+vb=gcd(a,b)</pre>
     * The returned greatest common divisor is the same as [gcd].
     * Note that the pair of `u` and `v` returned is not unique and different implementation
     * may return differently when a,b is the same.<P></P>
     * The default implementation is based on the Euclid's algorithm.
     *
     * @return a tuple of `(gcd(a,b), u, v)`.
     */
    @JvmDefault
    fun gcdUV(a: T, b: T): Triple<T, T, T> {
        //trivial cases
        if (isZero(a)) {
            return Triple(b, zero, one)
        }
        if (isZero(b)) {
            return Triple(a, one, zero)
        }
        /*
        Euclid's Extended Algorithms:
        Refer to Henri Cohen 'A course in computational algebraic number theory' Algorithm 1.3.6

        Explanation of the algorithm:
        we want to maintain the following equation while computing the gcd using the Euclid's algorithm
        let d0=a, d1=b, d2, d3 ... be the sequence of remainders in Euclid's algorithm,
        then we have
            a*1 + b*0 = d0
            a*0 + b*1 = d1
        let
            u0 = 1, v0 = 0
            u1 = 0, v1 = 1
        then we want to build a sequence of u_i, v_i such that
            a*u_i + b*v_i = d_i,
        when we find the d_n = gcd(a,b), the corresponding u_n and v_n is what we want.
        We have:
            d_i = q_i * d_{i+1} + d_{i+2}        (by Euclid's algorithm
        so
            a*u_i + b*v_i = q_i * (a*u_{i+1} + b*v_{i+1}) + (a*u_{i+2} + b*v_{i+2})
            u_i - q_i * u_{i+1} = u_{i+2}
            v_i - q_i * v_{i+1} = v_{i+2}
        but it is only necessary for us to record u_i, since v_i can be calculated from the equation
            a*u_i + b*v_i = d_i
         */
        var d0 = a
        var d1 = b
        var u0: T = one
        var u1: T = zero
        while (!isZero(d1)) {
            val (q, d2) = divideAndRemainder(d0, d1)
            d0 = d1
            d1 = d2
            val u2 = subtract(u0, multiply(q, u1))
            u0 = u1
            u1 = u2
        }
        val v: T = exactDivide(d0 - a * u0, b)
        return Triple(d0, u0, v)
    }
}