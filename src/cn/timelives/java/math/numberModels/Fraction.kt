package cn.timelives.java.math.numberModels

import cn.timelives.java.math.MathUtils
import cn.timelives.java.math.exceptions.ExceptionUtil
import cn.timelives.java.math.exceptions.UnsupportedCalculationException
import cn.timelives.java.math.algebra.linearAlgebra.Matrix
import cn.timelives.java.math.numberModels.api.DivisionRingNumberModel
import cn.timelives.java.math.numberModels.api.Simplifier
import cn.timelives.java.utilities.ArraySup
import java.io.Serializable

import java.util.ArrayList
import java.util.regex.Pattern


/**
 * A simple class that provides fractional calculation which means unless either numerator or denominator
 * is out of range of long, no precision will be lost.This class provides some math calculation with satisfying
 * results,as well normal time-performance.This class is used by [Matrix] as number's format.
 * @author lyc
 */
class Fraction
//numerator,denominator
/**
 * A constructor without checking num and den.
 * @param numerator the numerator
 * @param denominator the denominator
 * @param signum the signum
 */
internal constructor(
        /**
         * The numerator and denominator of this fraction,
         * which must be each-prime.
         * Also make sure that denominator != 0
         */
        /**
         * Gets the numerator of this Fraction.
         * @return numerator
         */
        val numerator: Long,
        /**
         * Gets the denominator of this Fraction.
         * @return denominator
         */
        val denominator: Long,
        /**
         * The sign number of this fraction,1 for positive,
         * 0 for and only for zero,and -1 for negative.
         */
        /**
         * Gets the sign number of this Fraction.
         * @return sign number
         */
        val signum: Int) : Number(), DivisionRingNumberModel<Fraction>, Comparable<Fraction>, Serializable {

    /**
     * Determines whether this fraction is an integer.
     * @return `true` if the fraction is an integer.
     */
    val isInteger: Boolean
        get() = denominator == 1L

    val isNegative: Boolean
        get() = signum < 0
    val isPositive: Boolean
        get() = signum > 0

    val isZero: Boolean
        get() = signum == 0

    init {
        if (denominator == 0L)
            throw IllegalArgumentException("Zero for denominator")
    }


    override fun toInt(): Int {
        return signum * (numerator / denominator).toInt()
    }

    override fun toLong(): Long {
        if (signum == 0) {
            return 0L
        }
        val value = numerator / denominator
        return if (signum > 0)
            value
        else
            -value
    }

    override fun toFloat(): Float {
        if (signum == 0) {
            return 0f
        }
        val value = numerator.toFloat() / denominator.toFloat()
        return if (signum > 0)
            value
        else
            -value
    }

    override fun toDouble(): Double {
        if (signum == 0) {
            return 0.0
        }
        val value = numerator.toDouble() / denominator.toDouble()
        return if (signum > 0)
            value
        else
            -value
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
     * Return the value of `this * num`
     * @param num multiplier
     * @return `this * num`
     */
    fun multiply(num: Long): Fraction {
        var num1 = num
        if (num1 == 0L) {
            return ZERO
        }
        var signum = this.signum
        if (num1 < 0) {
            num1 = -num1
            signum = -signum
        }


        //to prevent potential overflow,simplify num and den
        val dAn = gcdNumAndDen(denominator, num1)
        val nNum = dAn[1] * numerator
        //new numerator
        return Fraction(nNum, dAn[0], signum)
    }


    /**
     * Return the value of `this / num`
     * @param num divider,zero is not allowed.
     * @return `this / num`
     * @throws IllegalArgumentException if fra == 0.
     */
    fun divide(num: Long): Fraction {
        var num1 = num
        if (num1 == 0L) {
            throw IllegalArgumentException("Divide by zero :  / 0")
        }
        var signum = this.signum
        if (num1 < 0) {
            num1 = -num1
            signum = -signum
        }


        //to prevent potential overflow, simplify num and den
        val nAn = gcdNumAndDen(numerator, num1)
        val nDen = nAn[1] * denominator
        //new numerator
        return Fraction(nAn[0], nDen, signum)
    }

    /**
     * Return the value of `-this `
     * @return `-this `
     */
    override fun negate(): Fraction {
        return if (this.signum == 0) ZERO else Fraction(this.numerator, this.denominator, -this.signum)
    }

    /**
     * Return the value of `1/this`
     * @return `1/this`
     * @throws IllegalArgumentException if this == 0.
     */
    override fun reciprocal(): Fraction {
        if (this.signum == 0) {
            throw ArithmeticException("Zero to reciprocal")
        }
        return Fraction(this.denominator, this.numerator, this.signum)
    }

    /**
     * Return the value of `this * fra`
     * @param y another fraction
     * @return `this * fra`
     */
    override fun multiply(y: Fraction): Fraction {
        if (this.signum == 0 || y.signum == 0) {
            return ZERO
        }

        val n1D2 = gcdNumAndDen(this.numerator, y.denominator)
        val n2D1 = gcdNumAndDen(y.numerator, this.denominator)
        return Fraction(
                n1D2[0] * n2D1[0],
                n1D2[1] * n2D1[1],
                if (this.signum == y.signum) 1 else -1)
    }

    /**
     * Return the value of `this / fra`
     * @param y divider
     * @return `this / fra`
     * @throws IllegalArgumentException if fra == 0.
     */
    override fun divide(y: Fraction): Fraction {
        if (y.signum == 0) {
            ExceptionUtil.divideByZero()
        }
        if (this.signum == 0) {
            return ZERO
        }
        //exchange fra's numerator and denominator .
        val n1D2 = gcdNumAndDen(this.numerator, y.numerator)
        val n2D1 = gcdNumAndDen(y.denominator, this.denominator)
        return Fraction(
                n1D2[0] * n2D1[0],
                n1D2[1] * n2D1[1],
                if (this.signum == y.signum) 1 else -1)
    }

    /**
     * Return the value of `this + num`
     * @param num a number
     * @return `this + num`
     */
    fun add(num: Long): Fraction {
        var num1 = num
        if (num1 > 0 && signum < 0 || num1 < 0 && signum > 0) {
            num1 = -num1
        }
        var nNum = numerator + num1 * denominator
        var signum = this.signum
        if (nNum < 0) {
            signum = -signum
            nNum = -nNum
        }
        return Fraction(nNum, denominator, signum)
    }

    /**
     * Return the value of `this - num`
     * @param num a number
     * @return `this - num`
     */
    operator fun minus(num: Long): Fraction {
        var num1 = num
        if (num1 < 0 && signum < 0 || num1 > 0 && signum > 0) {
            num1 = -num1
        }
        var nNum = numerator + num1 * denominator
        var signum = this.signum
        if (nNum < 0) {
            signum = -signum
            nNum = -nNum
        }
        return Fraction(nNum, denominator, signum)
    }

    /**
     * Return the value of `this + frac`
     * @param y a fraction
     * @return `this + frac`
     */
    override fun add(y: Fraction): Fraction {
        val den = MathUtils.lcm(denominator, y.denominator)
        var num = this.signum.toLong() * this.numerator * den / this.denominator +
                y.signum.toLong() * y.numerator * den / y.denominator
        if (num == 0L) {
            return ZERO
        }
        val signum: Int
        if (num < 0) {
            num = -num
            signum = -1
        } else {
            signum = 1
        }
        val nAd = gcdNumAndDen(num, den)
        return Fraction(nAd[0], nAd[1], signum)
    }


    /**
     * Return the value of `this - frac`
     * @param frac a fraction
     * @return `this - frac`
     */
    operator fun minus(frac: Fraction): Fraction {
        var num = this.signum.toLong() * this.numerator * frac.denominator - frac.signum.toLong() * frac.numerator * this.denominator
        if (num == 0L) {
            return ZERO
        }
        val den = this.denominator * frac.denominator
        val signum: Int
        if (num < 0) {
            num = -num
            signum = -1
        } else {
            signum = 1
        }
        val nAd = gcdNumAndDen(num, den)
        return Fraction(nAd[0], nAd[1], signum)
    }


    operator fun times(y: Long) = multiply(y)

    operator fun times(y : Fraction) = multiply(y)

    operator fun div(y: Long) = divide(y)

    operator fun div(y : Fraction) = divide(y)

    operator fun plus(y: Long) = add(y)

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
    fun pow(n: Int): Fraction? {
        if (signum == 0) {
            return if (n == 0) {
                ExceptionUtil.zeroExponent()
            } else {
                Fraction.ZERO
            }
        } else {
            if (n == 0) {
                return Fraction.ONE
            }
            val sign: Int
            val deno: Long
            val nume: Long
            if (n < 0) {
                sign = if (n and 1 == 1) -1 else 1

                //exchange two
                deno = MathUtils.power(numerator, n)
                nume = MathUtils.power(denominator, n)
            } else {
                sign = 1
                nume = MathUtils.power(numerator, n)
                deno = MathUtils.power(denominator, n)
            }

            return Fraction(nume, deno, sign)
        }
    }

    /**
     * Returns `this^exp`.`exp` can have a denominator ,which means
     * the method will calculate the n-th root of `this`,but this method will
     * only return the positive root if there are two roots.
     *
     *
     * This method will throw ArithmeticException if such
     * operation cannot be done in Fraction.
     * @param exp an exponent
     * @return the result of `this^exp`
     */
    fun exp(exp: Fraction): Fraction? {

        if (exp.signum == 0) {
            if (this.signum == 0) {
                ExceptionUtil.zeroExponent()
            }
            return Fraction.ONE

        }
        if (this.signum == 0) {
            return Fraction.ZERO
        } else if (this.numerator == 1L && this.denominator == 1L) {
            // +- 1
            if (this.signum == 1) {
                return Fraction.ONE
            } else {
                if (exp.denominator % 2 == 0L) {
                    ExceptionUtil.negativeSquare()
                }
                return Fraction.NEGATIVE_ONE
            }
        }
        var signum = 1
        if (this.signum < 0) {
            if (exp.denominator % 2 == 0L)
                ExceptionUtil.negativeSquare()
            signum = -1
        }

        var swap = false
        if (exp.signum < 0) {
            swap = true
        }
        //we first check whether the Fraction b has a denominator
        if (exp.numerator > Integer.MAX_VALUE || exp.denominator > Integer.MAX_VALUE) {
            throw ArithmeticException("Too big in exp")
        }
        val bn = exp.numerator.toInt()
        val bd = exp.denominator.toInt()
        //try it
        var an = this.numerator
        var ad = this.denominator

        an = MathUtils.rootN(an, bd)
        ad = MathUtils.rootN(ad, bd)
        if (an == -1L || ad == -1L) {
            throw ArithmeticException("Cannot Find Root")
        }
        an = MathUtils.power(an, bn)
        ad = MathUtils.power(ad, bn)
        return if (swap) {
            Fraction(ad, an, signum)
        } else Fraction(an, ad, signum)
    }

    /**
     * Return `this^2`.The fastest and most convenient way to do this
     * calculation.
     * @return this^2
     */
    fun squareOf(): Fraction {
        return if (signum == 0) {
            Fraction.ZERO
        } else Fraction(numerator * numerator,
                denominator * denominator, 1)
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
        if (signum == 0) {
            return ZERO
        }
        val re = this.divide(divisor)
        return Fraction.valueOf(re.toLong())
    }

    /**
     * Returns the largest (closest to positive infinity) value that is
     * less than or equal to the argument and is equal to a mathematical integer. Special cases:
     * If the argument value is already equal to a mathematical integer, then the result is the same as the argument.
     */
    fun floor(): Long {
        if (isInteger) {
            return when {
                signum == 0 -> 0L
                signum > 0 -> numerator
                else -> -numerator
            }
        }
        val value = numerator / denominator
        return if (signum > 0)
            value
        else
            -value - 1
    }

    /**
     * Returns the smallest (closest to negative infinity) value that is
     * greater than or equal to the argument and is equal to a mathematical integer. Special cases:
     * If the argument value is already equal to a mathematical integer, then the result is the same as the argument.
     */
    fun ceil(): Long {
        if (isInteger) {
            return when {
                signum == 0 -> 0L
                signum > 0 -> numerator
                else -> -numerator
            }
        }
        val value = numerator / denominator
        return if (signum > 0)
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
        if (signum == 0) {
            return "0"
        }
        if (denominator == 1L) {
            return if (signum < 0)
                "-" + java.lang.Long.toString(numerator)
            else
                java.lang.Long.toString(numerator)
        }
        val sb = StringBuilder()
        if (signum < 0)
            sb.append('-')
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
        if (signum == 0) {
            return "0"
        }
        if (isInteger) {
            return if (signum < 0)
                "-" + java.lang.Long.toString(numerator)
            else
                java.lang.Long.toString(numerator)
        }
        val sb = StringBuilder("(")
        if (signum < 0)
            sb.append('-')
        sb.append(numerator).append('/').append(denominator)
        sb.append(')')
        return sb.toString()
    }

    override fun hashCode(): Int {
        var hash = signum * denominator
        hash = hash * 31 + numerator
        return (hash.ushr(32) xor hash).toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (other is Fraction) {
            val f = other as Fraction?
            //			if(f.signum == 0 && this.signum == 0) {
            //				return true;
            //			}
            return f!!.signum == this.signum &&
                    f.numerator == this.numerator &&
                    f.denominator == this.denominator
        }
        return false
    }

    /**
     * Compare two fractions , return -1 if this fraction is smaller than f,0 if equal,or 1
     * if this fraction is bigger than f.The method is generally equal to return `sgn(this-frac)`
     * @return -1,0 or 1 if this is smaller than,equal to or bigger than f.
     */
    override fun compareTo(other: Fraction): Int {
        val num = this.signum.toLong() * this.numerator * other.denominator - other.signum.toLong() * other.numerator * this.denominator
        return when {
            num > 0 -> 1
            num == 0L -> 0
            else -> -1
        }

    }


    class FractionCalculator : MathCalculatorAdapter<Fraction>() {
        override val numberClass: Class<*> = Fraction::class.java

        override fun isEqual(x: Fraction, y: Fraction): Boolean {
            return x == y
        }

        override fun compare(para1: Fraction, para2: Fraction): Int {
            return para1.compareTo(para2)
        }

        override val isComparable: Boolean = true

        override fun add(x: Fraction, y: Fraction): Fraction {
            return x.add(y)
        }

        override fun negate(x: Fraction): Fraction {
            return x.negate()
        }

        override fun abs(para: Fraction): Fraction {
            return Fraction(para.numerator, para.denominator, 1)
        }

        override fun subtract(x: Fraction, y: Fraction): Fraction {
            return x.minus(y)
        }

        override val zero: Fraction = ZERO

        override fun isZero(para: Fraction): Boolean {
            return ZERO == para
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
            if (x.signum == 0) {
                return Fraction.ZERO
            } else if (x.signum > 0) {
                val noe = MathUtils.squareRootExact(x.numerator)
                val deo = MathUtils.squareRootExact(x.denominator)
                if (noe != -1L && deo != -1L) {
                    return Fraction(noe, deo, 1)
                }
            }

            throw UnsupportedCalculationException()
        }

        override fun pow(x: Fraction, n: Long): Fraction {
            var exp1 = n
            if (x.signum == 0) {
                return if (exp1 == 0L) Fraction.ONE else Fraction.ZERO
            }
            val signum = if (exp1 % 2 == 0L) 1 else x.signum
            if (x.denominator == 1L && x.numerator == 1L) {
                return if (signum == x.signum) x else x.negate()
            }
            if (exp1 == 0L) {
                return Fraction.ONE
            }
            var no: Long
            var de: Long
            if (exp1 < 0) {
                exp1 = -exp1
                no = x.denominator
                de = x.numerator
            } else {
                no = x.numerator
                de = x.denominator
            }
            var noR = 1L
            var deR = 1L
            while (exp1 != 0L) {
                if (exp1 and 1 != 0L) {
                    noR *= no
                    deR *= de
                }
                no *= no
                de *= de
                exp1 = exp1 shr 1
            }


            return Fraction(noR, deR, signum)
        }

        override fun constantValue(name: String): Fraction {
            throw UnsupportedCalculationException("No constant value avaliable")
        }

        override fun exp(a: Fraction, b: Fraction): Fraction {

            if (b.signum == 0) {
                if (a.signum == 0) {
                    throw ArithmeticException("0^0")
                }
                return Fraction.ONE

            }
            if (a.signum == 0) {
                return Fraction.ZERO
            } else if (a.numerator == 1L && a.denominator == 1L) {
                // +- 1
                return if (a.signum == 1) {
                    Fraction.ONE
                } else {
                    if (b.denominator % 2 == 0L) {
                        throw ArithmeticException("Negative in Square")
                    }
                    Fraction.NEGATIVE_ONE
                }
            }
            var signum = 1
            if (a.signum < 0) {
                if (b.denominator % 2 == 0L)
                    throw ArithmeticException("Negative in Square")
                signum = -1
            }

            var swap = false
            if (b.signum < 0) {
                swap = true
            }
            //we first check whether the Fraction b has a denominator
            if (b.numerator > Integer.MAX_VALUE || b.denominator > Integer.MAX_VALUE) {
                ExceptionUtil.valueTooBig("exp")
            }
            val bn = b.numerator.toInt()
            val bd = b.denominator.toInt()
            //try it
            var an = a.numerator
            var ad = a.denominator

            an = MathUtils.rootN(an, bd)
            ad = MathUtils.rootN(ad, bd)
            if (an == -1L || ad == -1L) {
                throw UnsupportedCalculationException("Cannot Find Root")
            }
            an = MathUtils.power(an, bn)
            ad = MathUtils.power(ad, bn)
            return if (swap) {
                Fraction(ad, an, signum)
            } else Fraction(an, ad, signum)
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
            val signs = IntArray(len)
            var i = 0
            val it = numbers.listIterator()
            while (it.hasNext()) {
                val f = it.next()
                numes[i] = f.numerator
                denos[i] = f.denominator
                signs[i] = f.signum
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
            if (signs[0] == -1) {
                i = 0
                while (i < len) {
                    signs[i] = -signs[i]
                    i++
                }
            }
            val list = ArrayList<Fraction>(len)
            i = 0
            while (i < len) {
                list.add(Fraction(numes[i], 1L, signs[i]))
                i++
            }
            return list
        }

    }

    companion object {
        /**
         *
         */
        private const val serialVersionUID = -8236721041547778971L

        /**
         * A Fraction representing `0` with zero as numerator ,
         * one as denominator and zero for sign number.
         */
        val ZERO = Fraction(0, 1, 0)
        /**
         * A Fraction representing `1`.
         */
        val ONE = Fraction(1, 1, 1)
        /**
         * A Fraction representing `-1`
         */
        val NEGATIVE_ONE = Fraction(1, 1, -1)


        private fun gcdNumAndDen(num: Long, den: Long): LongArray {
            val re = LongArray(2)
            val g = MathUtils.gcd(num, den)
            re[0] = num / g
            re[1] = den / g
            return re
        }

        fun valueOf(number: Long): Fraction {
            return when {
                number == 0L -> ZERO
                number == 1L -> ONE
                number == -1L -> NEGATIVE_ONE
                number > 0 -> Fraction(number, 1, 1)
                else -> Fraction(-number, 1, -1)
            }
        }

        /**
         * Return a fraction representing the value of numerator/denominator,proper reduction
         * will be done.
         * @param numerator the numerator of the fraction
         * @param denominator the denominator of the fraction, non-zero
         * @return a new fraction
         */
        fun valueOf(numerator: Long, denominator: Long): Fraction {
            var numerator1 = numerator
            var denominator1 = denominator
            if (numerator1 == 0L) {
                return ZERO
            }
            var signum = 1
            if (numerator1 < 0) {
                numerator1 = -numerator1
                signum = -signum
            }
            if (denominator1 < 0) {
                denominator1 = -denominator1
                signum = -signum
            }

            val nAd = gcdNumAndDen(numerator1, denominator1)
            return Fraction(nAd[0], nAd[1], signum)
        }

        private val maxPrecision = Math.log10(java.lang.Long.MAX_VALUE.toDouble()).toInt() - 1

        /**
         * Return a fraction that is closet to the value of `d` but is small than `d`,
         * the returned fraction's both numerator and denominator are smaller than
         * 10<sup>`precision`</sup>.
         * @param d a number
         * @return a fraction
         */
        fun valueOfDouble(d: Double, precision: Int): Fraction {
            var d1 = d
            if (precision <= 0 || precision > maxPrecision) {
                throw IllegalArgumentException("Bad precision:$precision")
            }
            val signum: Int
            when {
                d1 > 0 -> signum = 1
                d1 == 0.0 -> return ZERO
                else -> {
                    signum = -1
                    d1 = -d1
                }
            }
            val deno = MathUtils.power(10L, precision - 1)
            //		deno*= 10L;
            while (d1 < deno.toDouble()) {
                d1 *= 10.0
            }
            val nume = d1.toLong()
            val nAd = gcdNumAndDen(nume, deno)
            return Fraction(nAd[0], nAd[1], signum)
        }

        /**
         * Returns the best approximate fraction of the double number. The numerator and
         * the denominator of the fraction are both smaller than `bound`.
         * @param x a number
         * @param bound the bound of the fraction, must be at least one.
         * @return a fraction that is the best approximate
         */
        fun bestApproximate(x: Double, bound: Long): Fraction {
            var x1 = x
            if (bound < 1) {
                throw IllegalArgumentException("Bad bound: $bound")
            }
            val signum: Int
            when {
                x1 > 0 -> signum = 1
                x1 == 0.0 -> return ZERO
                else -> {
                    signum = -1
                    x1 = -x1
                }
            }
            var es = LongArray(4)
            var f: LongArray? = null
            var m: Long = 1
            var y = 1.0
            var i = 0
            while (true) {
                val reminder = x1 % y
                val l = Math.round((x1 - reminder) / y)
                x1 = y
                y = reminder

                val t = m * l
                if (t > bound || t < 0 || java.lang.Double.isNaN(y)) {
                    break
                }
                m = t
                es = ArraySup.ensureCapacityAndAdd(es, l, i)
                val ft = computeContinuousFraction(es, i)
                if (Math.max(ft[0], ft[1]) > bound || ft[0] < 0 || ft[1] < 0) {
                    break
                }
                i++
                f = ft
            }
            return if (f == null) {
                Fraction.ZERO
            } else Fraction(f[0], f[1], signum)
        }

        private fun computeContinuousFraction(array: LongArray, index: Int): LongArray {
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
        val EXPRESSION_PATTERN: Pattern = Pattern.compile("[+\\-]?\\d+(/\\d+)?")
        // *([\\+\\-]?\\d+(\\/\\d+)?) * another replacement which
        /**
         * Return a fraction representing the value of the given expression.The text given should be like :
         * `"[\\+\\-]?\\d+(\\/\\d+)?"` as regular expression
         * @param expr the expression
         * @return
         */
        fun valueOf(expr: String): Fraction {
            val m = EXPRESSION_PATTERN.matcher(expr)
            if (m.matches()) {
                val nAd = expr.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val l1 = java.lang.Long.parseLong(nAd[0])
                return if (nAd.size > 1) {

                    val l2 = java.lang.Long.parseLong(nAd[1])
                    valueOf(l1, l2)
                } else {
                    valueOf(l1)
                }
            }
            throw NumberFormatException("Illegal Fraction:$expr")

        }


        /**
         * Return 1 number , 0 , -1 number if the given fraction is bigger than , equal to or smaller than `n`.
         * @param f a number as Fraction
         * @param n a number
         * @return a positive number , 0 , a negative number if the given fraction is bigger than , equal to or smaller than `n`.
         */
        fun compareFraction(f: Fraction, n: Long): Int {
            return java.lang.Long.signum(f.signum * f.numerator - f.denominator * n)
        }

        /**
         * Return 1 , 0 , -1 if the given fraction is bigger than , equal to or smaller than `n`.
         * @param f a number as Fraction
         * @param n a number
         * @return a positive number , 0 , a negative number if the given fraction is bigger than , equal to or smaller than `n`.
         */
        fun compareFraction(f: Fraction, n: Double): Int {
            val d = f.toDouble() - n
            return if (d < 0) -1 else if (d == 0.0) 0 else 1
        }

        fun valueOf(signum: Int, numerator: Long, denominator: Long): Fraction {
            if (signum == 0) {
                return ZERO
            }
            return valueOf(if (signum > 0) {
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
         * Get the calculator of the class Fraction,the calculator ignores overflow.
         *
         * The calculator does not have any constant values.
         * @return a fraction calculator
         */
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
        val fractionSimplifier: Simplifier<Fraction>
            get() = fs

        internal val fs = FractionSimplifier()
    }


}

fun main(args: Array<String>) {
    val f1 = Fraction.valueOf(-4, 3)
    println(f1.floor())
    println(f1.ceil())
}
