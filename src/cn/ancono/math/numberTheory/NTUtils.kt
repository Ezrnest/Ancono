package cn.ancono.math.numberTheory

import cn.ancono.math.MathUtils
import cn.ancono.math.discrete.combination.CombUtils
import cn.ancono.math.numberModels.Fraction
import kotlin.math.abs
import kotlin.random.Random

/*
 * Created by liyicheng at 2020-03-02 17:52
 */

/**
 * Contains utility functions for number theory.
 *
 * @author liyicheng
 */
object NTUtils {
    /**
     * Reduces a positive number to a continuous fraction series.
     */
    fun continuousFractionReduce(x: Double, length: Int = 8): LongArray {
        require(x > 0)
        require(length > 0)
        var x1 = x
        val es = LongArray(length)
        var y = 1.0
        var i = 0
        while (i < length) {
            val reminder = x1 % y
            val l = Math.round((x1 - reminder) / y)
            x1 = y
            y = reminder
            es[i] = l
            i++
        }
        return es
    }

    /**
     * Returns a list containing [n]-th farey sequence.
     */
    fun fareySequenceList(n: Long): List<Fraction> {
        /*
        A property of farey sequence:
        1. Neighbouring terms a/b and c/d in a Farey sequence, with a/b < c/d,
        then c/d - a/b = 1/bd, which is equivalent to that bc-ad = 1, and the converse is also true.
        2. If p/q has neighbours a/b, c/d in some Farey sequence, with a/b < p/q < c/d,
        then p/q = (a+c)/(b+d)


        To compute the farey sequence, we use the property 2.
         */
        val list = ArrayList<Fraction>()
        // a/b, c/d, p/q
        //p = kc -a, q = kd -b
        //k = (n+b)/d
        var a = 0L
        var b = 1L
        var c = 1L
        var d = n
        list.add(Fraction.ZERO)
        while (c <= n) {
            val k = (n + b) / d
            val p = k * c - a
            val q = k * d - b
            a = c
            b = d
            c = p
            d = q
            list.add(Fraction.of(a, b))
        }
        return list

    }

    /**
     * Returns a list containing [n]-th farey sequence.
     */
    fun fareySequence(n: Long): Sequence<Fraction> = sequence {
        /*
        A property of farey sequence:
        1. Neighbouring terms a/b and c/d in a Farey sequence, with a/b < c/d,
        then c/d - a/b = 1/bd, which is equivalent to that bc-ad = 1, and the converse is also true.
        2. If p/q has neighbours a/b, c/d in some Farey sequence, with a/b < p/q < c/d,
        then p/q = (a+c)/(b+d)


        To compute the farey sequence, we use the property 2.
         */
        // a/b, c/d, p/q
        //p = kc -a, q = kd -b
        //k = (n+b)/d
        var a = 0L
        var b = 1L
        var c = 1L
        var d = n
        yield(Fraction.ZERO)
        while (c <= n) {
            val k = (n + b) / d
            val p = k * c - a
            val q = k * d - b
            a = c
            b = d
            c = p
            d = q
            yield(Fraction.of(a, b))
        }

    }

    /**
     * A number n is called abundant if the sum of its proper divisors exceeds n.
     */
    fun isAbundant(n: Int) = isAbundant(n.toLong())

    /**
     * A number n is called abundant if the sum of its proper divisors exceeds n.
     */
    fun isAbundant(n: Long): Boolean = MathUtils.factorSum(n) > 2 * n

    /**
     * Factorizes [x] as
     *
     *     x = 2^p q
     * where `q` is an odd number.
     *
     * @return a pair of `(p,q)`
     */
    fun factor2s(x: Long): Pair<Int, Long> {
        val p = x.countTrailingZeroBits()
        return p to x.shr(p)
    }

    /**
     * Returns
     *
     *     (-1)^{ (a^2-1) / 8}
     */
    private fun powN1OddA1(a: Long): Int {
        val t = (((a - 1) / 2) % 4).toInt()
        return if (t == 0 || t == 3) {
            1
        } else {
            -1
        }
    }

    /**
     * Returns
     *
     *     (-1)^{ (a-1)(b-1) / 4}
     */
    private fun powN1MulOddAB(a: Long, b: Long): Int {
        return if (a.and(b).and(2) == 0L) {
            1
        } else {
            -1
        }
    }

    private fun kroneckerRemove2(a: Long, y: Long): Pair<Int, Long> {
        var v = 0
        var b = y
        while (b % 2 == 0L) {
            v += 1
            b /= 2
        }
        return if (v % 2 == 0) {
            1
        } else {
            powN1OddA1(a)
        } to b
    }

    /**
     * Kronecker symbol when [y] is positive and odd.
     */
    private fun kroneckerOdd(x: Long, y: Long): Int {
        var a = x
        var b = y
        var k = 1
        while (true) {
            if (a == 0L) {
                return if (b > 1) {
                    0
                } else {
                    k
                }
            }
            val (k1, a1) = kroneckerRemove2(b, a)
            k *= k1
            a = a1
            k *= powN1MulOddAB(a, b)
            val r = abs(a)
            a = b % r
            b = r
        }
    }

    /**
     * Computes the Kronecker symbol `(x / y)`.
     *
     */
    fun kroneckerSymbol(x: Long, y: Long): Int {
        //Created by lyc at 2021-04-24 18:47
        /*
        Reference: Algorithm 1.4.10, page 29 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen
         */
        if (y == 0L) {
            return if (abs(x) == 1L) {
                1
            } else {
                0
            }
        }
        if (x % 2 == 0L && y % 2 == 0L) {
            return 0
        }
        var (k, b) = kroneckerRemove2(x, y)
        if (b < 0) {
            b = -b
            if (x < 0) {
                k = -k
            }
        }
        return k * kroneckerOdd(x, b)
    }

    /**
     * Computes the Legendre symbol `(x / y)`.
     */
    fun legendreSymbol(x: Long, y: Long): Int {
        require(y > 0 && (y % 2) == 1L)
        return kroneckerOdd(x, y)
    }

    /**
     * @param a a positive integer.
     */
    private fun sqrtModPRandom(a: Long, p: Long): Long {
        /*
        Reference: Algorithm 1.5.1, page 33 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen

        Note: this method requires that a is positive.
         */
        val (e, q) = factor2s(p - 1)
        var n = Random.nextLong(p)
        while (kroneckerOdd(n, p) != -1) {
            n = Random.nextLong(p)
        }
        val z = MathUtils.powMod(n, q, p)
        var y = z
        var r = e
        var x = MathUtils.powMod(a, (q - 1) / 2, p)
        var b = (a * x * x) % p
        x = (a * x) % p
        fun findMinPower2(b: Long): Int {
            var t = b
            var m = 0
            do {
                t = (t * t) % p
                m++
            } while (t != 1L)
            return m
        }
        while (b != 1L) {
            val m = findMinPower2(b)
            if (m == r) {
                throw ArithmeticException("$a is not a quadratic residue mod $p.")
            }
            val pow = 1L shl (r - m - 1)
            val t = MathUtils.powMod(y, pow, p)
            y = (t * t) % p
            r = m
            x = (x * t) % p
            b = (b * y) % p
        }
        return x
    }

    /**
     * Finds the square root of [a] modulo [p], that is, finds `x` such that
     *
     *     x^2 = a (mod p)
     * The result will always be in `[0,p)`, and
     * this method will throw an exception if such square root does not exist.
     * @param a an integer
     * @param p a prime number
     * @throws ArithmeticException if such square root does not exist.
     */
    fun sqrtModP(a: Long, p: Long): Long {
        //Created by lyc at 2021-04-24 21:41
        if (p == 2L) {
            return a % 2
        }
        require(p > 2)
        if (a == 0L || a == 1L) {
            return a
        }
        if (p % 4 == 3L) {
            return MathUtils.powMod(a, (p + 1) / 4, p)
        }
        if (p % 8 == 5L) {
            val t = MathUtils.powMod(a, (p - 1) / 4, p)
            if (t == 1L) {
                return MathUtils.powMod(a, (p + 3) / 8, p)
            }
            val b = (4 * a) % p
            val r = (2 * a) * MathUtils.powMod(b, (p - 5) / 8, p)
            return r % p
        }
        return sqrtModPRandom(MathUtils.mod(a, p), p)
    }


    fun solveDiophantine(d: Long, p: Long): Pair<Long, Long>? {
        require(d in 1 until p)
        val k = kroneckerSymbol(-d, p)
        if (k == -1) {
            return null
        }
        fun adjustToRange(x: Long): Long {
            return if (x * 2 > p) {
                x
            } else {
                p - x
            }
        }

        val x0 = adjustToRange(sqrtModP(-d, p))
        var a = p
        var b = x0
        val l = MathUtils.sqrtInt(p)
        while (b > l) {
            val r = a % b
            a = b
            b = r
        }
        val t = (p - b * b)
        if (t % d != 0L) {
            return null
        }
        val c = t / d
        val y = MathUtils.squareRootExact(c)
        if (y < 0) {
            return null
        }
        return b to y

    }

    /**
     * Factor a number `n = pq` where `p` and `q` are close.
     */
    fun factorFermat(n: Long): Pair<Long, Long> {
        val s = MathUtils.sqrtInt(n)
        for (b in s until n) {
            val t = b * b - n
            if (t < 0) {
                continue
            }
            val a = MathUtils.sqrtInt(t)
            if (a * a != t) {
                continue
            }
            val p = b + a
            val q = b - a
            return p to q
        }
        return n to 1
    }

    /**
     * Factor a number.
     */
    @Suppress("LocalVariableName")
    fun pollard(n: Long, a: Long = 2L, B: Int = 5): Pair<Long, Long>? {
        val M = CombUtils.factorial(B)
        val b_B = MathUtils.powMod(a, M, n)
        val d = MathUtils.gcd(b_B - 1, n)
        if (d < n) {
            return d to n / d
        }
        val (s, m) = factor2s(M)
        for (i in (s - 1) downTo 0) {
            val pow = (1 shl i) * m
            val r = MathUtils.powMod(a, pow, n)
            if (r == -1L) {
                return null
            }
            if (r == 1L) {
                val p = MathUtils.gcd(r + 1, n)
                val q = MathUtils.gcd(r - 1, n)
                return p to q
            }
        }
        return null
    }

    fun pollardRho(n: Long, x1: Long = 2, c: Long = 1): Pair<Long, Long>? {
        var a = x1
        fun phi(x: Long): Long {
            return MathUtils.mod(x * x + c, n)
        }

        var b = phi(x1)
        while (true) {
            val d = MathUtils.gcd(a - b, n)
            if (d in 2 until n) {
                return d to n / d
            }
            if (d == n) {
                return null
            }
            a = phi(a)
            b = phi(phi(b))
        }
    }

//    @JvmStatic
//    fun main(args: Array<String>) {
////        println(kroneckerOdd(-2,97))
////        println(kroneckerOdd(95,97))
//    }
}

