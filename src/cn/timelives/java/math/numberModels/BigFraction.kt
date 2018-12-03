
package cn.timelives.java.math.numberModels

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.exceptions.ExceptionUtil
import cn.timelives.java.math.numberModels.BigFraction.Companion.ONE
import cn.timelives.java.math.numberModels.BigFraction.Companion.TWO
import cn.timelives.java.math.numberModels.BigFraction.Companion.ZERO
import cn.timelives.java.math.numberModels.BigFraction.Companion.valueOf
import cn.timelives.java.math.numberModels.Fraction.Companion.EXPRESSION_PATTERN
import cn.timelives.java.math.numberModels.api.FieldNumberModel
import cn.timelives.java.math.numberModels.api.minus
import cn.timelives.java.math.numberModels.api.plus
import cn.timelives.java.math.numberModels.api.unaryMinus
import java.io.Serializable
import java.math.BigInteger


/**
 * A fraction which uses BigInteger as its numerator and denominator.
 * Created at 2018/10/16 12:30
 * @author  liyicheng
 */
@Suppress("NOTHING_TO_INLINE")
class BigFraction
internal constructor(val signum: Int, val numerator: BigInteger, val denominator: BigInteger) :
        FieldNumberModel<BigFraction>,
        Comparable<BigFraction>,
        Serializable {
    init {
        require(denominator != BigInteger.ZERO)
    }

    operator fun component1(): Int = signum

    operator fun component2(): BigInteger = numerator

    operator fun component3(): BigInteger = denominator

    fun isInteger() = denominator == BigInteger.ONE

    fun isZero() = signum == 0

    fun isPositive() = signum > 0

    fun isNegative() = signum < 0

    fun ndPairWithSign(): Pair<BigInteger, BigInteger> {
        return if (signum < 0) {
            -numerator
        } else {
            numerator
        } to denominator
    }

    fun abs() : BigFraction{
        if(isZero()){
            return ZERO
        }
        return if(signum>0){
            this
        }else{
            negate()
        }
    }

    override fun add(y: BigFraction): BigFraction {
        if (isZero()) {
            return y
        }
        if (y.isZero()) {
            return this
        }
        val (n1, d1) = this.ndPairWithSign()
        val (n2, d2) = y.ndPairWithSign()
        return add0(n1, d1, n2, d2)
    }

    override fun subtract(y: BigFraction): BigFraction {
        if (isZero()) {
            return -y
        }
        if (y.isZero()) {
            return this
        }
        val (n1, d1) = this.ndPairWithSign()
        val n2: BigInteger = if (y.isNegative()) {
            y.numerator
        } else {
            -y.numerator
        }
        return add0(n1, d1, n2, y.denominator)
    }


    override fun negate(): BigFraction {
        if (signum == 0) {
            return ZERO
        }
        return BigFraction(-signum, numerator, denominator)
    }

    fun multiply(k: Long): BigFraction {
        return multiply(k.toBigInteger())
    }

    fun multiply(k: BigInteger): BigFraction {
        val sign = signum * k.signum()
        if (sign == 0) {
            return ZERO
        }
        val (n, d) = gcdNumAndDen(k.abs(), denominator)
        return BigFraction(sign, n * numerator, d)
    }

    fun divide(k: Long): BigFraction {
        return divide(k.toBigInteger())
    }

    fun divide(k: BigInteger): BigFraction {
        if (k.signum() == 0) {
            ExceptionUtil.divideByZero()
        }
        if (isZero()) {
            return ZERO
        }
        val sign = signum * k.signum()
        val (n, d) = gcdNumAndDen(numerator, k.abs())
        return BigFraction(sign, n, d * denominator)
    }


    override fun multiply(y: BigFraction): BigFraction {
        val (s1, n1, d1) = this
        val (s2, n2, d2) = y
        return multiply0(s1, n1, d1, s2, n2, d2)
    }

    override fun divide(y: BigFraction): BigFraction {
        if (y.isZero()) {
            ExceptionUtil.divideByZero()
        }
        if (isZero()) {
            return ZERO
        }
        val (s1, n1, d1) = this
        val (s2, d2, n2) = y //reciprocal
        return multiply0(s1, n1, d1, s2, n2, d2)
    }

    override fun reciprocal(): BigFraction {
        if (isZero()) {
            ExceptionUtil.divideByZero()
        }
        return BigFraction(signum, denominator, numerator)
    }

    inline operator fun plus(y: BigFraction) = add(y)

    inline operator fun minus(y: BigFraction) = subtract(y)

    inline operator fun unaryMinus() = negate()

    inline operator fun times(y: BigInteger) = multiply(y)
    inline operator fun times(y: Long) = multiply(y)
    inline operator fun times(y: BigFraction) = multiply(y)

    inline operator fun div(y: Long) = divide(y)
    inline operator fun div(y: BigInteger) = divide(y)
    inline operator fun div(y: BigFraction) = divide(y)


    override operator fun compareTo(other: BigFraction): Int {
        val comp = signum.compareTo(other.signum)
        if (comp != 0) {
            return comp
        }
        val tn = numerator * other.denominator - other.numerator * denominator
        return signum * tn.signum()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BigFraction) return false

        if (signum != other.signum) return false
        if (numerator != other.numerator) return false
        if (denominator != other.denominator) return false

        return true
    }

    override fun hashCode(): Int {
        var result = signum
        result = 31 * result + numerator.hashCode()
        result = 31 * result + denominator.hashCode()
        return result
    }

    override fun toString(): String = buildString {
        if (signum < 0) {
            append('-')
        }
        append(numerator)
        if (denominator != BigInteger.ONE) {
            append('/')
            append(denominator)
        }
    }


    companion object {
        private fun gcdNumAndDen(num: BigInteger, den: BigInteger): Pair<BigInteger, BigInteger> {
            val g = num.gcd(den)
            return (num / g) to (den / g)
        }

        private fun add0(n1: BigInteger, d1: BigInteger, n2: BigInteger, d2: BigInteger): BigFraction {
            var tn = n1 * d2 + n2 * d1
            val td = d1 * d2
            val signum: Int = when {
                tn > BigInteger.ZERO -> 1
                tn == BigInteger.ZERO -> return ZERO
                else -> {
                    tn = -tn
                    -1
                }
            }
            val (nume, deno) = gcdNumAndDen(tn, td)
            return BigFraction(signum, nume, deno)
        }

        private fun multiply0(s1: Int, n1: BigInteger, d1: BigInteger, s2: Int, n2: BigInteger, d2: BigInteger): BigFraction {
            val sign = s1 * s2
            if (sign == 0) {
                return ZERO
            }
            val (tN1, tD1) = gcdNumAndDen(n1, d2)
            val (tN2, tD2) = gcdNumAndDen(n2, d1)
            val nume = tN1 * tN2
            val deno = tD1 * tD2
            return BigFraction(sign, nume, deno)
        }

        /**
         * The constant value zero.
         */
        @JvmField
        val ZERO = BigFraction(0, BigInteger.ZERO, BigInteger.ONE)
        @JvmField
        val ONE = BigFraction(1, BigInteger.ONE, BigInteger.ONE)
        @JvmField
        val TWO = BigFraction(1, BigInteger.TWO, BigInteger.ONE)
        @JvmField
        val NEGATIVE_ONE = BigFraction(-1, BigInteger.ONE, BigInteger.ONE)
        @JvmField
        val HALF = BigFraction(1, BigInteger.ONE, BigInteger.TWO)

        @JvmStatic
        fun valueOf(number: Long): BigFraction {
            return when {
                number == 0L -> ZERO
                number == 1L -> ONE
                number == -1L -> NEGATIVE_ONE
                number > 0 -> BigFraction(1, number.toBigInteger(), BigInteger.ONE)
                else -> BigFraction(-1, (-number).toBigInteger(), BigInteger.ONE)
            }
        }

        @JvmStatic
        fun valueOf(number: BigInteger): BigFraction {
            return when (number) {
                BigInteger.ZERO -> ZERO
                BigInteger.ONE -> ONE
                else -> BigFraction(number.signum(), number.abs(), BigInteger.ONE)
            }
        }

        /**
         * Return a fraction representing the value of numerator/denominator, proper reduction
         * will be done.
         * @param numerator the numerator of the fraction
         * @param denominator the denominator of the fraction, non-zero
         * @return a new fraction
         */
        @JvmStatic
        fun valueOf(numerator: BigInteger, denominator: BigInteger): BigFraction {
            var numerator1 = numerator
            var denominator1 = denominator
            if (numerator1 == BigInteger.ZERO) {
                return ZERO
            }
            var signum = 1
            if (numerator1.signum() < 0) {
                numerator1 = -numerator1
                signum = -signum
            }
            if (denominator1.signum() < 0) {
                denominator1 = -denominator1
                signum = -signum
            }

            val (nume, deno) = gcdNumAndDen(numerator1, denominator1)
            return BigFraction(signum, nume, deno)
        }

        @JvmStatic
        fun fromFraction(f : Fraction) : BigFraction{
            if(f.isZero){
                return ZERO
            }
            return BigFraction(f.signum,f.numerator.toBigInteger(),f.denominator.toBigInteger())
        }
        /**
         * Return a fraction representing the value of the given expression.The text given should be like :
         * `"[\\+\\-]?\\d+(\\/\\d+)?"` as regular expression
         * @param expr the expression
         * @return
         */
        @JvmStatic
        fun valueOf(expr: String): BigFraction {
            val m = EXPRESSION_PATTERN.matcher(expr)
            if (m.matches()) {
                val nAd = expr.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val l1 = BigInteger(nAd[0])
                return if (nAd.size > 1) {
                    val l2 = BigInteger(nAd[1])
                    valueOf(l1, l2)
                } else {
                    valueOf(l1)
                }
            }
            throw NumberFormatException("Illegal Fraction:$expr")
        }


        /**
         * The calculator for BigFraction.
         */
        @JvmStatic
        val calculator : BigFractionCalculator = BigFractionCalculator
    }
}

object BigFractionCalculator : MathCalculatorAdapter<BigFraction>() {
    override val one: BigFraction
        get() = ONE
    override val zero: BigFraction
        get() = ZERO

    override fun isZero(para: BigFraction): Boolean {
        return para.isZero()
    }

    override fun isEqual(x: BigFraction, y: BigFraction): Boolean {
        return x == y
    }

    override fun compare(x: BigFraction, y: BigFraction): Int {
        return x.compareTo(y)
    }

    override val isComparable: Boolean
        get() = true

    override fun add(x: BigFraction, y: BigFraction): BigFraction {
        return x.add(y)
    }

    override fun negate(x: BigFraction): BigFraction {
        return x.negate()
    }

    override fun abs(para: BigFraction): BigFraction {
        return para.abs()
    }

    override fun subtract(x: BigFraction, y: BigFraction): BigFraction {
        return x.subtract(y)
    }

    override fun multiply(x: BigFraction, y: BigFraction): BigFraction {
        return x.multiply(y)
    }

    override fun divide(x: BigFraction, y: BigFraction): BigFraction {
        return x.divide(y)
    }

    override fun divideLong(x: BigFraction, n: Long): BigFraction {
        return x.divide(n)
    }

    override fun multiplyLong(x: BigFraction, n: Long): BigFraction {
        return x.multiply(n)
    }

    override fun reciprocal(x: BigFraction): BigFraction {
        return x.reciprocal()
    }

    override val numberClass: Class<*>
        get() = BigFraction::class.java
}
