package cn.ancono.math.numberModels

import cn.ancono.math.MathUtils
import cn.ancono.math.exceptions.ExceptionUtil
import cn.ancono.math.exceptions.UnsupportedCalculationException
import cn.ancono.math.numberModels.api.FieldNumberModel
import cn.ancono.math.numberModels.api.Simplifier
import cn.ancono.math.numberTheory.NTUtils
import cn.ancono.utilities.ArraySup
import java.io.Serializable
import java.util.regex.Pattern
import kotlin.math.*


/**
 * A simple class that provides fractional calculation, which means unless either numerator or denominator
 * is out of range of long, no precision will be lost. This class provides some math calculation with satisfying
 * results,as well normal time-performance.
 *
 * A fraction is composed of two co-prime integers [numerator] and the [denominator].
 * The sign of the numerator represents the sign of this fraction and
 * the denominator is always positive.
 * @author lyc
 */
data class Fraction
//numerator,denominator
/**
 * A constructor without checking num and den.
 * @param numerator the numerator
 * @param denominator the denominator
 */
internal constructor(
        /**
         * The numerator and denominator of this fraction,
         * which must be co-prime.
         * Also make sure that denominator != 0
         */
        /**
         * Gets the numerator of this Fraction, the numerator may be positive, zero or negative.
         * @return numerator
         */
        val numerator: Long,
        /**
         * Gets the denominator of this Fraction, it is a positive integer.
         * @return denominator
         */
        val denominator: Long,
        /**
         * The sign number of this fraction,1 for positive,
         * 0 for and only for zero,and -1 for negative.
         */
) : Number(), FieldNumberModel<Fraction>, Comparable<Fraction>, Serializable {

    /**
     * Determines whether this fraction is an integer.
     * @return `true` if the fraction is an integer.
     */
    val isInteger: Boolean
        get() = denominator == 1L

    val isNegative: Boolean
        get() = numerator < 0
    val isPositive: Boolean
        get() = numerator > 0

    /**
     * The sign number of this fraction, `1` if `this > 0`, `0` if `this = 0` and `-1` if `this < 0`.
     */
    val signum: Int
        get() = numerator.compareTo(0)

    /**
     * Gets the absolute value of the [numerator].
     */
    val numeratorAbs: Long
        get() = numerator.absoluteValue

    override fun isZero(): Boolean {
        return numerator == 0L
    }

    init {
        if (denominator == 0L)
            throw IllegalArgumentException("Zero for denominator")
    }

    /**
     * Returns the int value corresponding to this fraction if it can be expressed using an int,
     * otherwise an exception will be thrown.
     */
    override fun toInt(): Int {
        if (!isInteger) {
            throw ArithmeticException("Not an integer: $this")
        }
        return Math.toIntExact(numerator)
    }

    /**
     * Returns the long value corresponding to this fraction if it can be expressed using an int,
     * otherwise an exception will be thrown.
     */
    override fun toLong(): Long {
        if (!isInteger) {
            throw ArithmeticException("Not an integer: $this")
        }
        return numerator
    }

    override fun toFloat(): Float {
        return numerator.toFloat() / denominator.toFloat()
    }

    override fun toDouble(): Double {
        return numerator.toDouble() / denominator.toDouble()
    }

    override fun toByte(): Byte {
        return toInt().toByte()
    }

    override fun toChar(): Char {
        return toInt().toChar()
    }

    override fun toShort(): Short {
        return toInt().toShort()
    }

    /**
     * Returns the absolute value of this fraction.
     */
    fun abs(): Fraction {
        return if (isNegative) {
            this.negate()
        } else {
            this
        }
    }

    /**
     * Return the value of `this * num`
     * @param n multiplier
     * @return `this * k`
     */
    override fun multiply(n: Long): Fraction {
        if (n == 0L) {
            return ZERO
        }


        //to prevent potential overflow,simplify num and den
        val dAn = gcdReduce(n, denominator)
        val nNum = dAn[0] * numerator
        //new numerator
        return Fraction(nNum, dAn[1])
    }

    /**
     * Return the value of `this / num`
     * @param k divider,zero is not allowed.
     * @return `this / num`
     * @throws IllegalArgumentException if num == 0.
     */
    fun divide(k: Long): Fraction {
        if (k == 0L) {
            throw IllegalArgumentException("Divide by zero :  / 0")
        }

        //to prevent potential overflow, simplify num and den
        val nAn = gcdReduce(numerator, k)
        val nDen = nAn[1] * denominator
        //new numerator
        return Fraction(nAn[0], nDen)
    }

    /**
     * Return the value of `-this `
     * @return `-this `
     */
    override fun negate(): Fraction {
        return if (this.isZero()) {
            ZERO
        } else {
            Fraction(-numerator, denominator)
        }
    }

    /**
     * Return the value of `1/this`
     * @return `1/this`
     * @throws IllegalArgumentException if this == 0.
     */
    override fun reciprocal(): Fraction {
        if (this.isZero()) {
            throw ArithmeticException("Zero to reciprocal")
        }
        return adjustSign(denominator, numerator)
    }

    /**
     * Return the value of `this * y`
     * @param y another fraction
     * @return `this * y`
     */
    override fun multiply(y: Fraction): Fraction {
        if (isZero() || y.isZero()) {
            return ZERO
        }

        val n1D2 = gcdReduce(this.numerator, y.denominator)
        val n2D1 = gcdReduce(y.numerator, this.denominator)
        return Fraction(
                n1D2[0] * n2D1[0],
                n1D2[1] * n2D1[1])
    }

    /**
     * Return the value of `this / y`
     * @param y divider
     * @return `this / y`
     * @throws IllegalArgumentException if y == 0.
     */
    override fun divide(y: Fraction): Fraction {
        if (y.isZero()) {
            ExceptionUtil.dividedByZero()
        }
        if (this.isZero()) {
            return ZERO
        }
        //exchange y's numerator and denominator .
        val n1D2 = gcdReduce(this.numerator, y.numerator)
        val n2D1 = gcdReduce(y.denominator, this.denominator)
        return adjustSign(
                n1D2[0] * n2D1[0],
                n1D2[1] * n2D1[1])
    }

    /**
     * Return the value of `this + num`
     * @param num a number
     * @return `this + num`
     */
    fun add(num: Long): Fraction {
        val nNum = numerator + num * denominator
        if (nNum == 0L) {
            return ZERO
        }
        return Fraction(nNum, denominator)
    }


    /**
     * Return the value of `this - num`
     * @param num a number
     * @return `this - num`
     */
    operator fun minus(num: Long): Fraction {
        val nNum = numerator - num * denominator
        if (nNum == 0L) {
            return ZERO
        }
        return Fraction(nNum, denominator)
    }

    /**
     * Return the value of `this + y`
     * @param y a fraction
     * @return `this + y`
     */
    override fun add(y: Fraction): Fraction {
        // a/b + c/d =
        // (a * lcm / b) / lcm + (c * lcm / d) / lcm = (a * d / gcd + c * b / gcd) / lcm
        // (a * d1 + c * b1) / lcm
        val gcd = MathUtils.gcd(denominator, y.denominator)
        val b1 = denominator / gcd
        val d1 = y.denominator / gcd
        val lcm = b1 * y.denominator
        val num = this.numerator * d1 + y.numerator * b1
        return of(num, lcm)
    }


    /**
     * Return the value of `this - y`
     * @param y a fraction
     * @return `this - y`
     */
    operator fun minus(y: Fraction): Fraction {
        // a/b + c/d =
        // (a * lcm / b) / lcm - (c * lcm / d) / lcm = (a * d / gcd - c * b / gcd) / lcm
        // (a * d1 - c * b1) / lcm
        val gcd = MathUtils.gcd(denominator, y.denominator)
        val b1 = denominator / gcd
        val d1 = y.denominator / gcd
        val lcm = b1 * y.denominator
        val num = this.numerator * d1 - y.numerator * b1
        return of(num, lcm)
    }

    operator fun unaryMinus() = negate()

    operator fun times(y: Long) = multiply(y)

    operator fun times(y: Fraction) = multiply(y)

    operator fun div(y: Long) = divide(y)

    operator fun div(y: Fraction) = divide(y)

    operator fun plus(y: Long) = add(y)

    operator fun plus(y: Fraction) = add(y)

    operator fun rem(y: Fraction) = remainder(y)


    /**
     * Return the value of this^n while n is an integer.This method is generally faster
     * than using [.multiply] because no GCD calculation will be done.
     *
     * **Attention:** this method does NOT check underflow or overflow , so please notice the range of `n`
     * @param n the power
     * @return `this^n`
     * @throws ArithmeticException if this == 0 and n <=0
     */
    fun pow(n: Int): Fraction {
        if (isZero()) {
            return if (n == 0) {
                ExceptionUtil.zeroExponent()
            } else {
                ZERO
            }
        }
        if (n == 0) {
            return ONE
        }
        val p = abs(n)
        val nume: Long = MathUtils.pow(numerator, p)
        val deno: Long = MathUtils.pow(denominator, p)
        return if (n > 0) {
            Fraction(nume, deno)
        } else {
            adjustSign(deno, nume)
        }
    }

    /**
     * Returns `this^exp`.`exp` can have a denominator, which means
     * the method will calculate the n-th root of `this`,but this method will
     * only return the positive root if there are two roots.
     *
     *
     * This method will throw ArithmeticException if such
     * operation cannot be done in Fraction.
     * @param exp an exponent
     * @return the result of `this^exp`
     */
    fun exp(exp: Fraction): Fraction {

        if (exp.isZero()) {
            if (this.isZero()) {
                ExceptionUtil.zeroExponent()
            }
            return ONE

        }
        if (this.isZero()) {
            return ZERO
        }
        if (this.denominator == 1L) {
            // +- 1
            if (numerator == 1L) {
                return ONE
            }
            if (numerator == -1L) {
                if (exp.denominator % 2 == 0L) {
                    ExceptionUtil.sqrtForNegative()
                }
                return NEGATIVE_ONE
            }
        }
        if (this.isNegative) {
            if (exp.denominator % 2 == 0L)
                ExceptionUtil.sqrtForNegative()
        }
        //we first check whether the Fraction b has a denominator
        if (exp.numerator > Integer.MAX_VALUE || exp.denominator > Integer.MAX_VALUE) {
            throw ArithmeticException("Too big in exp")
        }
        val bn = exp.numerator.toInt().absoluteValue
        val bd = exp.denominator.toInt()

        //try it
        var an = this.numerator.absoluteValue
        var ad = this.denominator

        an = MathUtils.rootN(an, bd)
        ad = MathUtils.rootN(ad, bd)
        if (an == -1L || ad == -1L) {
            throw ArithmeticException("Cannot Find Root")
        }
        if (this.isNegative) {
            an = -an
        }
        an = MathUtils.pow(an, bn)
        ad = MathUtils.pow(ad, bn)
        return if (exp.isNegative) {
            adjustSign(ad, an)
        } else {
            adjustSign(an, ad)
        }
    }

    /**
     * Return `this^2`. The fastest and most convenient way to do this
     * calculation.
     * @return this^2
     */
    fun squareOf(): Fraction {
        return if (isZero()) {
            ZERO
        } else Fraction(numerator * numerator,
                denominator * denominator)
    }

    /**
     * Returns a `Fraction` whose value is the integer part
     * of the quotient `(this / divisor)` rounded down.
     *
     * @param  divisor value by which this `Fraction` is to be divided.
     * @return The integer part of `this / divisor`.
     * @throws ArithmeticException if `divisor==0`
     */
    fun divideToIntegralValue(divisor: Fraction): Fraction {
        if (isZero()) {
            return ZERO
        }
        val re = this.divide(divisor)
        return of(re.toLong())
    }

    /**
     * Returns the largest (closest to positive infinity) integer value that is
     * less than or equal to the this fraction.
     */
    fun floor(): Long {
        return Math.floorDiv(numerator, denominator)
//        if (isInteger) {
//            return numerator
//        }
//        val value = numerator.absoluteValue / denominator
//        return if (isPositive)
//            value
//        else
//            -value - 1
    }

    /**
     * Returns the smallest (closest to negative infinity) value that is
     * greater than or equal to the argument and is equal to a mathematical integer. Special cases:
     * If the argument value is already equal to a mathematical integer, then the result is the same as the argument.
     */
    fun ceil(): Long {
        if (isInteger) {
            return numerator
        }
        val value = numerator / denominator
        return if (isPositive)
            value + 1
        else
            -value
    }


    fun divideAndRemainder(divisor: Fraction): Array<Fraction> {
        val result0 = this.divideToIntegralValue(divisor)
        val result1 = this.minus(result0.multiply(divisor))
        return arrayOf(result0, result1)
    }

    /**
     * Returns a `Fraction` whose value is `(this % divisor)`.
     *
     *
     * The remainder is given by
     * `this.subtract(this.divideToIntegralValue(divisor).multiply(divisor))`.
     * Note that this is *not* the modulo operation (the result can be
     * negative).
     *
     * @param  divisor value by which this `Fraction` is to be divided.
     * @return `this % divisor`.
     * @throws ArithmeticException if `divisor==0`
     */
    fun remainder(divisor: Fraction): Fraction {
        val divrem = this.divideAndRemainder(divisor)
        return divrem[1]
    }

    /**
     * Return the String expression of this fraction.
     */
    override fun toString(): String {
        if (denominator == 1L) {
            return numerator.toString()
        }
        val sb = StringBuilder()
        sb.append(numerator).append('/').append(denominator)
        return sb.toString()
    }

    /**
     * Returns a String representation of this fraction, adds brackets if this
     * fraction is not an integer. This method can be used to eliminate confusion
     * when this fraction is a part of an expression.
     * @return a string
     */
    fun toStringWithBracket(): String {
        if (denominator == 1L) {
            return numerator.toString()
        }
        val sb = StringBuilder("(")
        sb.append(numerator).append('/').append(denominator)
        sb.append(')')
        return sb.toString()
    }

    fun toLatexString(): String {
        if (denominator == 1L) {
            return numerator.toString()
        }
        val sb = StringBuilder()
        if (isNegative) {
            sb.append('-');
        }
        sb.append("\\frac{").append(numerator.absoluteValue).append("}{").append(denominator).append('}')
        return sb.toString()
    }


    /**
     * Compare two fractions , return -1 if this fraction is smaller than f,0 if equal,or 1
     * if this fraction is bigger than f. The method is generally equal to return `sgn(this-frac)`
     * @return -1,0 or 1 if this is smaller than,equal to or bigger than f.
     */
    override fun compareTo(other: Fraction): Int {
//        val comp = signum.compareTo(other.signum)
//        if (comp != 0) {
//            return comp
//        }
//        Math.multiplyExact()
//
//        val num = this.numerator * other.denominator - other.numerator * this.denominator
//        return num.sign * signum
        return (this - other).signum
    }


    class FractionCalculator : MathCalculatorAdapter<Fraction>() {
        override val numberClass: Class<Fraction> = Fraction::class.java

        override fun isEqual(x: Fraction, y: Fraction): Boolean {
            return x == y
        }

        override fun compare(x: Fraction, y: Fraction): Int {
            return x.compareTo(y)
        }

        override val isComparable: Boolean = true

        override fun add(x: Fraction, y: Fraction): Fraction {
            return x.add(y)
        }

        override fun negate(x: Fraction): Fraction {
            return x.negate()
        }

        override fun abs(x: Fraction): Fraction {
            return Fraction(x.numerator.absoluteValue, x.denominator)
        }

        override fun subtract(x: Fraction, y: Fraction): Fraction {
            return x.minus(y)
        }

        override val zero: Fraction = ZERO

        override fun isZero(x: Fraction): Boolean {
            return ZERO == x
        }

        override fun multiply(x: Fraction, y: Fraction): Fraction {
            return x.multiply(y)
        }

        override fun divide(x: Fraction, y: Fraction): Fraction {
            return x.divide(y)
        }

        override val one: Fraction = ONE

        override fun reciprocal(x: Fraction): Fraction {
            return x.reciprocal()
        }

        override fun multiplyLong(x: Fraction, n: Long): Fraction {
            return x.multiply(n)
        }

        override fun divideLong(x: Fraction, n: Long): Fraction {
            return x.divide(n)
        }

        override fun squareRoot(x: Fraction): Fraction {
            if (x.isZero()) {
                return ZERO
            } else if (x.isPositive) {
                val noe = MathUtils.squareRootExact(x.numerator)
                val deo = MathUtils.squareRootExact(x.denominator)
                if (noe != -1L && deo != -1L) {
                    return Fraction(noe, deo)
                }
            }

            throw UnsupportedCalculationException()
        }

        override fun pow(x: Fraction, n: Long): Fraction {
            return x.pow(Math.toIntExact(n))
        }

        override fun constantValue(name: String): Fraction {
            throw UnsupportedCalculationException("No constant value available")
        }

        override fun exp(a: Fraction, b: Fraction): Fraction {
            return a.exp(b)
        }

        override fun of(x: Long): Fraction {
            return Fraction.of(x)
        }

        override fun of(x: Fraction): Fraction {
            return x
        }

        companion object {
            internal val cal = FractionCalculator()
        }
    }

    internal class FractionSimplifier internal constructor() : Simplifier<Fraction> {

        override fun simplify(numbers: List<Fraction>): List<Fraction> {
            //first find the GCD of numerator and LCM of denominator.
            val len = numbers.size
            val numes = LongArray(len)
            val denos = LongArray(len)
            var i = 0
            val it = numbers.listIterator()
            while (it.hasNext()) {
                val f = it.next()
                numes[i] = f.numerator
                denos[i] = f.denominator
                i++
            }
            val gcd = MathUtils.gcd(*numes)
            val lcm = MathUtils.lcm(*denos)
            //			Printer.print(lcm);
            i = 0
            while (i < len) {
                numes[i] = numes[i] / gcd * (lcm / denos[i])
                i++
            }
            //denos are all set to one.
            val list = ArrayList<Fraction>(len)
            i = 0
            while (i < len) {
                list.add(adjustSign(numes[i], 1L))
                i++
            }
            return list
        }

    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = -8236721042317778971L

        /**
         * A Fraction representing `0` with zero as numerator ,
         * one as denominator and zero for sign number.
         */
        @JvmField
        val ZERO = Fraction(0, 1)

        /**
         * A Fraction representing `1`.
         */
        @JvmField
        val ONE = Fraction(1, 1)

        /**
         * A Fraction representing `-1`
         */
        @JvmField
        val NEGATIVE_ONE = Fraction(-1, 1)

        @JvmField
        val TWO = Fraction(2, 1)

        @JvmField
        val HALF = Fraction(1, 2)

        private fun gcdReduce(num: Long, den: Long): LongArray {
            val re = LongArray(2)
            val g = MathUtils.gcd(num, den)
            re[0] = num / g
            re[1] = den / g
            return re
        }

        private fun adjustSign(num: Long, den: Long): Fraction {
            return if (den < 0) {
                Fraction(-num, -den)
            } else {
                Fraction(num, den)
            }
        }

        /**
         * Returns a fraction from a long.
         */
        @JvmStatic
        fun of(number: Long): Fraction {
            return when (number) {
                0L -> ZERO
                1L -> ONE
                -1L -> NEGATIVE_ONE
                else -> Fraction(number, 1)
            }
        }

        /**
         * Return a fraction representing the value of numerator/denominator,proper reduction
         * will be done.
         * @param numerator the numerator of the fraction
         * @param denominator the denominator of the fraction, non-zero
         * @return a new fraction
         */
        @JvmStatic
        fun of(numerator: Long, denominator: Long): Fraction {
            require(denominator != 0L)
            if (numerator == 0L) {
                return ZERO
            }
            val nAd = gcdReduce(numerator, denominator)
            return adjustSign(nAd[0], nAd[1])
        }

        private val maxPrecision = log10(java.lang.Long.MAX_VALUE.toDouble()).toInt() - 1

        /**
         * Return a fraction that is closet to the value of `d` but is small than `d`,
         * the returned fraction's both numerator and denominator are smaller than
         * 10<sup>`precision`</sup>.
         * @param d a number
         * @return a fraction
         */
        @JvmStatic
        fun ofDouble(d: Double, precision: Int): Fraction {
            if (precision <= 0 || precision > maxPrecision) {
                throw IllegalArgumentException("Bad precision:$precision")
            }
            if (d == 0.0) {
                return ZERO
            }
            var d1 = d.absoluteValue
            val deno = MathUtils.pow(10L, precision - 1)
            //		deno*= 10L;
            while (d1 < deno.toDouble()) {
                d1 *= 10.0
            }
            val nume = d1.toLong()
            val nAd = gcdReduce(nume, deno)
            return Fraction(MathUtils.signum(d) * nAd[0], nAd[1])
        }

        /**
         * Returns the best approximate fraction of the double number. The numerator and
         * the denominator of the fraction are both smaller than `bound`.
         * @param x a number
         * @param bound the bound of the fraction, must be at least one.
         * @return a fraction that is the best approximate
         */
        @JvmStatic
        fun bestApproximate(x: Double, bound: Long = 10000_0000, conFraLenBound: Int = 16): Fraction {
            if (bound < 1) {
                throw IllegalArgumentException("Bad bound: $bound")
            }
            if (x == 0.0) {
                return ZERO
            }
            var x1 = x.absoluteValue
            var es = LongArray(4)
            var f: LongArray? = null
            var m: Long = 1
            var y = 1.0
            var i = 0
            while (true) {
                val reminder = x1 % y
                val l = ((x1 - reminder) / y).roundToLong()
                x1 = y
                y = reminder

                val t = m * l
                if (t > bound || t < 0 || java.lang.Double.isNaN(y)) {
                    break
                }
                m = t
                es = ArraySup.ensureCapacityAndAdd(es, l, i)
                val ft = computeContinuousFraction(es, i)
                if (max(ft[0], ft[1]) > bound || ft[0] < 0 || ft[1] < 0) {
                    break
                }
                i++
                f = ft
                if (i >= conFraLenBound) {
                    break
                }
            }
            return if (f == null) {
                ZERO
            } else Fraction(MathUtils.signum(x) * f[0], f[1])
        }

        @JvmStatic
        fun continuousFraction(x: Double, len: Int): LongArray {
            return NTUtils.continuousFractionReduce(x, len)
        }

        /**
         * Computes the result of the continuous fraction stored in the array and
         * returns an array of the numerator and denominator.
         * @param index the highest element in the array to compute from
         */
        @JvmStatic
        fun computeContinuousFraction(array: LongArray, index: Int = array.lastIndex): LongArray {
            var index1 = index
            var nume = array[index1]
            var deno: Long = 1

            index1--
            while (index1 > -1) {
                val nn = array[index1] * nume + deno
                val nd = nume
                nume = nn
                deno = nd
                index1--
            }
            return longArrayOf(nume, deno)
        }

        /**
         * Identify the given expression
         */
        @JvmStatic
        val EXPRESSION_PATTERN: Pattern = Pattern.compile("([+\\-]?\\d+)(/(\\d+))?")

        // *([\\+\\-]?\\d+(\\/\\d+)?) * another replacement which
        val DECIMAL_PATTERN: Pattern = Pattern.compile("([+\\-]?\\d+)\\.(\\d+)")

        /**
         * Return a fraction representing the value of the given expression. The input can be either in fraction way
         * like `3/5` or in decimal way like `3.14`.
         * @param expr the expression
         */
        @JvmStatic
        fun of(expr: String): Fraction {
            var m = EXPRESSION_PATTERN.matcher(expr)
            if (m.matches()) {
                val n = m.group(1).toLong()
                return try {
                    val d = m.group(3).toLong()
                    of(n, d)
                } catch (e: Exception) {
                    of(n)
                }
            }
            m = DECIMAL_PATTERN.matcher(expr)
            if (m.matches()) {
                val n1 = m.group(1).toLong()
                val n2 = m.group(2)
                val digits = n2.length
                val deno = MathUtils.pow(10L, digits)
                val nume = n1 * deno + n2.toLong()
                return of(nume, deno)
            }

            throw NumberFormatException("Illegal Fraction:$expr")

        }


        /**
         * Return 1 number , 0 , -1 number if the given fraction is bigger than , equal to or smaller than `n`.
         * @param f a number as Fraction
         * @param n a number
         * @return a positive number , 0 , a negative number if the given fraction is bigger than , equal to or smaller than `n`.
         */
        @JvmStatic
        fun compareFraction(f: Fraction, n: Long): Int {
            return java.lang.Long.signum(f.numerator - f.denominator * n)
        }

        /**
         * Return 1 , 0 , -1 if the given fraction is bigger than , equal to or smaller than `n`.
         * @param f a number as Fraction
         * @param n a number
         * @return a positive number , 0 , a negative number if the given fraction is bigger than , equal to or smaller than `n`.
         */
        @JvmStatic
        fun compareFraction(f: Fraction, n: Double): Int {
            val d = f.toDouble() - n
            return if (d < 0) -1 else if (d == 0.0) 0 else 1
        }

        /**
         * Returns a fraction according to the given [signum], [numerator] and [denominator].
         */
        @JvmStatic
        fun of(signum: Int, numerator: Long, denominator: Long): Fraction {
            if (signum == 0) {
                return ZERO
            }
            return of(if (signum > 0) {
                numerator
            } else {
                -numerator
            }, denominator)
        }


        //	public static void main(String[] args) {
        ////		print(computeContinuousFraction(new long[] {2,3,3,11,2}, 4));
        ////		Fraction f = bestApproximate(M,10);
        //		print(f);
        ////		print((double)f.numerator/f.denominator);
        //	}

        /**
         * Get the calculator of the class Fraction, the calculator ignores overflow.
         *
         * The calculator does not have any constant values.
         * @return a fraction calculator
         */
        @JvmStatic
        val calculator: FractionCalculator
            get() = FractionCalculator.cal

        /**
         * Get the Simplifier of the class Fraction,this simplifier will take the input numbers
         * as coefficient of a equation and multiply or divide them with a factor that makes them
         * all become integer values.The first fraction will be ensure to be positive.
         *
         * This simplifier will ignore overflows.
         * @return a simplifier
         */
        @JvmStatic
        val fractionSimplifier: Simplifier<Fraction> = FractionSimplifier()
    }


}

fun Long.toFrac(): Fraction = Fraction.of(this)

fun Int.toFrac(): Fraction = Fraction.of(this.toLong())

//fun main(args: Array<String>) {
////    val f1 = Fraction.valueOf(-4, 3)
////    println(f1.floor())
////    println(f1.ceil())
//    val t = Math.sqrt(1.7)
//    for(len in 1 .. 5){
//        val frac = Fraction.bestApproximate(t, conFraLenBound = len)
//        println(frac)
//        val diff = Math.abs(t-frac.toDouble())
//        for(f in NaiveNumberTheory.fareySequence(frac.denominator-1)){
//            if(Math.abs(f.toDouble()-(t-Math.floor(t)))<diff){
//                println("! $f")
//            }
//        }
//    }
//
//}
