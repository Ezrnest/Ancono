package cn.ancono.math.algebra.abs.calculator

import cn.ancono.math.exceptions.ExceptionUtil


/*
 * Created by liyicheng at 2020-03-09 19:32
 */
/**
 * Describes a calculator for an Euclidean domain. The fundamental operation of this type of calculator is
 * [divideAndRemainder].
 *
 *
 * All calculators for integers are `EUDCalculator`.
 *
 * See [EuclideanDomain](https://mathworld.wolfram.com/EuclideanDomain.html) for more information.
 */
interface EUDCalculator<T> : UFDCalculator<T> {

    /**
     * Returns a pair of `(q, r)` such that
     *
     *     a = qb + r
     *
     * where `q` is the quotient and `r` the remainder.
     */
    fun divideAndRemainder(a: T, b: T): Pair<T, T>

    /**
     * Returns the quotient part of [divideAndRemainder].
     *
     * @param a the dividend
     * @param b the divisor
     * @return the quotient `q` of `a = qb + r`.
     * @see divideAndRemainder
     */
    fun divideToInteger(a: T, b: T): T = divideAndRemainder(a, b).first

    /**
     * Returns the remainder part of [divideAndRemainder].
     *
     * @param a the dividend
     * @param b the divisor
     * @return the remainder `r` of `a = qb + r`.
     * @see divideAndRemainder
     */
    fun remainder(a: T, b: T): T = divideAndRemainder(a, b).second


    /**
     * Returns `a mod b`, which is generally the same as [remainder].
     *
     * Note that the result may differ with respect to a unit in the ring.
     */
    fun mod(a: T, b: T): T = remainder(a, b)

    override fun exactDivide(x: T, y: T): T {
        val (q, r) = divideAndRemainder(x, y)
        if (!isZero(r)) {
            ExceptionUtil.notExactDivision(x, y)
        }
        return q
    }

    override fun isExactDivide(a: T, b: T): Boolean {
        return isZero(remainder(a, b))
    }

    /**
     * Returns the greatest common divisor of [a] and [b].
     *
     * The default implementation uses Euclidean algorithm.
     */
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
     * Returns the greatest common divisor of two numbers and a pair of number `(u,v)` such that
     *
     *     ua + vb = gcd(a, b)
     *
     * The returned greatest common divisor is the same as [gcd].
     * Note that the pair of `u` and `v` returned is not unique and different implementation
     * may return differently when `a, b` is the same.
     *
     * The default implementation is based on the extended Euclid's algorithm.
     *
     * @return a tuple of `(gcd(a,b), u, v)`.
     */
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
        val v: T = divideToInteger(d0 - a * u0, b) // discard the possible remainder caused by numeric imprecision
        return Triple(d0, u0, v)
    }

    /**
     * Returns the modular inverse of `a` with respect to `p`, that is, find the element `b` such
     * that `ab = 1 (mod p)`.
     *
     * The default implementation is based on [gcdUV].
     *
     * @return the modular inverse of `a`
     *
     */
    fun modInverse(a: T, p: T): T {
        val (g, u, _) = gcdUV(a, p)
        if (!isUnit(g)) {
            throw ArithmeticException("$a is not invertible with respect to $p")
        }
        return u
    }


    /**
     * Returns `(a^n) mod m`, where `n` is a non-negative integer.
     *
     *
     * This method is mathematically equivalent to `mod(pow(x, n), m)`.
     *
     * @param x a number
     * @param n the power, a non-negative number.
     * @param m the modular.
     */
    fun powMod(x: T, n: Long, m: T): T {
        var a = x
        require(n >= 0) { "n must be non-negative!" }
        if (isEqual(m, one)) {
            return zero
        }
        if (isEqual(a, one)) {
            return one
        }
        var ans = one
        var p = n
        a = mod(a, m)
        while (p > 0) {
            if (p and 1 == 1L) {
                ans = mod(multiply(a, ans), m)
            }
            a = mod(multiply(a, a), m)
            p = p shr 1
        }
        return ans
    }


    /**
     * Returns the result `x` such that
     *
     *     x = remainders[i] (mod mods[i])
     *
     * @param mods a list of the modular, they must be co-prime
     *
     */
    fun chineseRemainder(mods: List<T>, remainders: List<T>): T {
//        val prod: T = mods.reduce(this::multiply)
//        var x: T = zero
//        for (i in mods.indices) {
//            val m: T = mods[i]
//            val r: T = remainders[i]
//            val t = exactDivide(prod,m)
//            val inv = this.modInverse(t, m)
//            x += r * t * inv
//            x = mod(x,prod)
//        }
//        return x
        //Created by lyc at 2021-04-20 20:31
        var m: T = mods[0]
        var x: T = remainders[0]
        for (i in 1 until mods.size) {
            val (_, u, v) = gcdUV(m, mods[i])
            x = u * m * remainders[i] + v * mods[i] * x
            m *= mods[i]
            x = mod(x, m)
        }
        return x
    }

    companion object {

        /**
         * Creates a quotient field calculator with a prime element [prime].
         */
        fun <T> quotientFieldCalculator(cal: EUDCalculator<T>, prime: T): FieldCalculator<T> {
            return QuotientFieldCal(cal, prime)
        }


        class QuotientFieldCal<T>(val cal: EUDCalculator<T>, val p: T) : FieldCalculator<T> {

            override fun reciprocal(x: T): T {
                return cal.modInverse(x, p)
            }

            override fun isEqual(x: T, y: T): Boolean {
                return cal.eval {
                    isZero(mod(x - y, p))
                }
            }

            override fun multiply(x: T, y: T): T {
                return cal.eval {
                    mod(multiply(x, y), p)
                }
            }

            override val characteristic: Long
                get() = throw UnsupportedOperationException()
            override val zero: T
                get() = cal.zero

            override fun add(x: T, y: T): T {
                return cal.eval {
                    mod(add(x, y), p)
                }
            }

            override fun negate(x: T): T {
                return cal.negate(x)
            }

            override val one: T
                get() = cal.one

            override fun multiplyLong(x: T, n: Long): T {
                return cal.eval { mod(multiplyLong(x, n), p) }
            }


            override fun of(n: Long): T {
                return cal.eval { mod(of(n), p) }
            }

            override fun subtract(x: T, y: T): T {
                return cal.eval { mod(subtract(x, y), p) }
            }

            override val isCommutative: Boolean
                get() = cal.isCommutative
        }

    }

}
