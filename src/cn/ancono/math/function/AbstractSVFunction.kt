/**
 * 2017-10-06
 */
package cn.ancono.math.function

import cn.ancono.math.AbstractMathObjectReal
import cn.ancono.math.MathObject
import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.calculus.Derivable
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.api.NumberFormatter
import cn.ancono.math.numberModels.api.RealCalculator
import cn.ancono.math.set.Interval
import cn.ancono.math.set.IntervalUnion
import java.util.function.Function

/**
 * This class provides some basic functions such as `pow`, `exp`, `log`.
 *
 *
 * @author liyicheng
 * 2017-10-06 10:02
 */
abstract class AbstractSVFunction<T>
protected constructor(mc: RealCalculator<T>) : AbstractMathObjectReal<T>(mc), SVFunction<T> {

    /**
     * Returns the String representation of this function, the prefix 'f(x)='
     * should not be included.
     */
    abstract override fun toString(nf: NumberFormatter<T>): String


    /*
	 * @see cn.ancono.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.math.numberModels.api.MathCalculator)
	 */
    abstract override fun <N> mapTo(
            newCalculator: EqualPredicate<N>,
            mapper: Function<T, N>
    ): AbstractSVFunction<N>


    companion object {


        fun <T> constant(c: T, mc: RealCalculator<T>): AbstractSVPFunction.ConstantFunction<T> {
            return AbstractSVPFunction.ConstantFunction(mc, c)
        }

        /**
         * Returns the function : e^x
         * @param mc a [RealCalculator]
         * @return e^x
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> naturalExp(mc: RealCalculator<T>): Ex<T> {
            return Ex(mc)
        }

        /**
         * Returns the function : ln(x)
         * @param mc a [RealCalculator]
         * @return ln(x)
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> naturalLog(mc: RealCalculator<T>): Ln<T> {
            return Ln(mc)
        }

        /**
         * Returns the exponential function:
         * <pre>c*a^x</pre>
         * where `c!=0 && a > 0 && a!=1`
         * @param mc a [RealCalculator]
         * @return <pre>c*a^x</pre>
         */
        fun <T> exp(a: T, c: T, mc: RealCalculator<T>): Exp<T> {
            if (mc.isZero(c)) {
                throw IllegalArgumentException("c == 0")
            }
            if (mc.compare(a, mc.zero) <= 0 || mc.isEqual(mc.one, a)) {
                throw IllegalArgumentException("a <= 0 || a==1")
            }
            return Exp(mc, c, a)
        }

        /**
         * Returns the exponential function:
         * <pre>a^x</pre>
         * where `a > 0 && a!=1`
         * @param mc a [RealCalculator]
         * @return <pre>a^x</pre>
         */
        fun <T> exp(a: T, mc: RealCalculator<T>): Exp<T> {
            if (mc.compare(a, mc.zero) <= 0 || mc.isEqual(mc.one, a)) {
                throw IllegalArgumentException("a <= 0 || a==1")
            }
            return Exp(mc, mc.one, a)
        }

        /**
         * Returns the function:
         * <pre>log<sup>a</sup>x</pre>
         * where a&gt;0 &amp;&amp; a!=1
         * @param a the base
         * @param mc a [RealCalculator]
         * @return <pre>log<sup>a</sup>x</pre>
         */
        fun <T> log(a: T, mc: RealCalculator<T>): Log<T> {
            if (mc.compare(a, mc.zero) <= 0 || mc.isEqual(mc.one, a)) {
                throw IllegalArgumentException("a <= 0 || a==1")
            }
            return Log(mc, a)
        }

        /**
         * Returns the power function:
         * <pre>a*x^n</pre>
         * where n is a rational number.
         * @param a
         * @param n
         * @param mc a [RealCalculator]
         * @return <pre>a*x^n</pre>
         */
        fun <T> pow(a: T, n: Fraction, mc: RealCalculator<T>): Power<T> {
            return if (mc.isZero(a)) {
                Power(mc)
            } else Power(mc, a, n)
        }

        /**
         * Returns the function `x^(1/2)`.
         */
        fun <T> sqrt(mc: RealCalculator<T>): Power<T> {
            return pow(Fraction.HALF, mc)
        }

        /**
         * Returns the power function:
         * <pre>x^n</pre>
         * where n is a rational number.
         * @param n
         * @param mc a [RealCalculator]
         * @return <pre>x^n</pre>
         */
        fun <T> pow(n: Fraction, mc: RealCalculator<T>): Power<T> {
            return Power(mc, n)
        }
    }

}

/**
 * Describe the function:
 * <pre>ln(x)</pre>
 * @author liyicheng
 * 2017-10-10 18:37
 *
 * @param <T>
</T> */
class Ln<T>
/**
 * @param mc
 */
internal constructor(mc: RealCalculator<T>) : AbstractSVFunction<T>(mc), Derivable<T, T, Power<T>> {

    /*
     * @see cn.ancono.math.function.SVFunction#apply(java.lang.Object)
     */
    override fun apply(x: T): T {
        return mc.ln(x)
    }

    /*
     * @see cn.ancono.math.algebra.calculus.Derivable#derive()
     */
    override fun derive(): Power<T> {
        return Power(mc, mc.one, Fraction.NEGATIVE_ONE)
    }

    /*
     * @see cn.ancono.math.function.MathFunction#domain()
     */
    override fun domain(): Interval<T> {
        return Interval.positive(mc)
    }

    /*
     * @see cn.ancono.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.ancono.math.numberModels.api.MathCalculator)
     */
    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): Ln<N> {
        return Ln(newCalculator as RealCalculator<N>)
    }

    /*
     * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
     */
    override fun valueEquals(obj: MathObject<T, RealCalculator<T>>): Boolean {
        if (obj is Log<*>) {
            return mc.isEqual((obj as Log<T>).a, mc.constantValue(RealCalculator.STR_E)!!)
        }
        return obj is Ln<*>
    }

    /*
     * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.numberModels.api.NumberFormatter)
     */
    override fun toString(nf: NumberFormatter<T>): String {
        return "ln(x)"
    }
}

/**
 * Describe the function:
 * <pre>log<sup>a</sup>x</pre>
 * where a&gt;0 &amp;&amp; a!=1
 * @author liyicheng
 * 2017-10-10 18:37
 * @param <T>
</T> */
class Log<T>
/**
 * @param mc
 */
(mc: RealCalculator<T>, val a: T) : AbstractSVFunction<T>(mc), Derivable<T, T, Power<T>> {
    /*
     * @see cn.ancono.math.function.SVFunction#apply(java.lang.Object)
     */
    override fun apply(x: T): T {
        return mc.log(a, x)
    }

    /*
     * @see cn.ancono.math.algebra.calculus.Derivable#derive()
     */
    override fun derive(): Power<T> {
        return Power(mc, mc.reciprocal(mc.ln(a)), Fraction.NEGATIVE_ONE)
    }

    /*
     * @see cn.ancono.math.function.MathFunction#domain()
     */
    override fun domain(): Interval<T> {
        return Interval.positive(mc)
    }

    /*
     * @see cn.ancono.math.function.AbstractSVFunction#toString(cn.ancono.math.numberModels.api.NumberFormatter)
     */
    override fun toString(nf: NumberFormatter<T>): String {
        return "log(" + nf.format(a) + ",x)"
    }

    /*
     * @see cn.ancono.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.ancono.math.numberModels.api.MathCalculator)
     */
    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): Log<N> {
        return Log(newCalculator as RealCalculator, mapper.apply(a))
    }

    /*
     * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
     */
    override fun valueEquals(obj: MathObject<T, RealCalculator<T>>): Boolean {
        if (obj is Ln<*>) {
            return (obj as Ln<T>).valueEquals(this)
        }
        if (obj !is Log<*>) {
            return false
        }
        val log = obj as Log<T>
        return mc.isEqual(a, log.a)
    }


}

/**
 * Describes the power function:
 * <pre>a*x^n</pre>
 * where n is a rational number.
 * @author liyicheng
 * 2017-10-10 19:04
 *
 * @param <T>
</T> */
class Power<T>
/**
 * @param mc
 */
@JvmOverloads internal constructor(mc: RealCalculator<T>,
                                   /**
                                    * Gets the a:<pre>a*x^n</pre>
                                    * @return the a
                                    */
                                   val a: T = mc.zero,
                                   /**
                                    * Gets the n:<pre>a*x^n</pre>
                                    * @return the n as a fraction
                                    */
                                   val n: Fraction = Fraction.ONE) : AbstractSVFunction<T>(mc), DerivableSVFunction<T> {

    private var domain: IntervalUnion<T>? = null

    /**
     * @param mc
     */
    internal constructor(mc: RealCalculator<T>, n: Fraction) : this(mc, mc.one, n) {}

    /*
     * @see cn.ancono.math.function.SVFunction#apply(java.lang.Object)
     */
    override fun apply(x: T): T {
        if (n.signum == 0) {
            return a
        }
        if (mc.isZero(a)) {
            return mc.zero
        }
        val p = n.abs()
        var t: T
        t = if (p.denominator == 1L) {
            mc.pow(x, p.numerator)
        } else if (p.numerator == 1L) {
            mc.nroot(x, p.denominator)
        } else {
            mc.nroot(mc.pow(x, p.numerator), p.denominator)
        }
        if (n.isNegative) {
            t = mc.reciprocal(t)
        }
        return mc.multiply(a, t)
    }

    override val derivative: Power<T> by lazy {
        if (mc.isZero(a)) {
            return@lazy this
        }
        if (n.signum == 0) {
            return@lazy Power(mc, mc.zero, Fraction.ONE)
        }
        val _n = n.minus(Fraction.ONE)
        val _a = mc.divideLong(mc.multiplyLong(a, n.numerator), n.denominator)
        return@lazy Power(mc, _a, _n)
    }

    override fun derive(): Power<T> {
        return derivative
    }

    /*
         * @see cn.ancono.math.function.MathFunction#domain()
         */
    override fun domain(): IntervalUnion<T> {
        if (domain == null) {
            domain = if (n.signum >= 0) {
                if (n.denominator % 2 == 1L) {
                    IntervalUnion.universe(mc)
                } else {
                    IntervalUnion.valueOf(Interval.toPositiveInf(mc.zero, true, mc))
                }
            } else {
                if (n.denominator % 2 == 1L) {
                    IntervalUnion.except(mc.zero, mc)
                } else {
                    IntervalUnion.valueOf(Interval.positive(mc))
                }
            }
        }
        return domain!!

    }

    /*
     * @see cn.ancono.math.function.AbstractSVFunction#toString(cn.ancono.math.numberModels.api.NumberFormatter)
     */
    override fun toString(nf: NumberFormatter<T>): String {
        if (mc.isZero(a)) {
            return "0"
        } else if (n.signum == 0) {
            return nf.format(a)
        }
        val sb = StringBuilder()
        if (!mc.isEqual(a, mc.one)) {
            sb.append(nf.format(a))
        }
        sb.append("x")
        if (n.denominator == 1L && n.signum > 0) {
            if (n.numerator != 1L) {
                sb.append("^").append(n.numerator)
            }
        } else {
            sb.append("^(").append(n.toString()).append(")")
        }
        return sb.toString()
    }

    /*
     * @see cn.ancono.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.ancono.math.numberModels.api.MathCalculator)
     */
    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): Power<N> {
        return Power(newCalculator as RealCalculator, mapper.apply(a), n)
    }

    /*
     * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
     */
    override fun valueEquals(obj: MathObject<T, RealCalculator<T>>): Boolean {
        if (obj !is Power<*>) {
            return false
        }
        val p = obj as Power<T>
        return if (mc.isZero(a)) {
            mc.isZero(p.a)
        } else mc.isEqual(a, p.a) && n == p.n
    }

}
/**
 *
 */
/**
 * Describes the exponential function:
 * <pre>c*a^x</pre>
 * where `c!=0 && a > 0 && a!=1`
 * @author liyicheng
 * 2017-10-10 19:04
 *
 * @param <T>
 */
class Exp<T>
internal constructor(mc: RealCalculator<T>,
                     /**
                      * Gets the c:<pre>c*a^x</pre>
                      * @return the c
                      */
                     val c: T,
                     /**
                      * Gets the a:<pre>c*a^x</pre>
                      * @return the a
                      */
                     val a: T) : AbstractSVFunction<T>(mc), DerivableSVFunction<T> {
    /*
     * @see cn.ancono.math.function.SVFunction#apply(java.lang.Object)
     */
    override fun apply(x: T): T {
        return mc.multiply(c, mc.exp(a, x))
    }

    override val derivative: DerivableSVFunction<T> by lazy {
        Exp(mc, mc.multiply(c, mc.ln(a)), a)
    }


    /*
     * @see cn.ancono.math.function.AbstractSVFunction#toString(cn.ancono.math.numberModels.api.NumberFormatter)
     */
    override fun toString(nf: NumberFormatter<T>): String {
        val sb = StringBuilder()
        if (!mc.isEqual(mc.one, a)) {
            sb.append(nf.format(a))
        }
        val `as` = nf.format(a)
        if (`as`.length == 1) {
            sb.append(`as`)
        } else {
            sb.append('(').append(`as`).append(')')
        }
        sb.append("^x")

        return sb.toString()
    }

    /*
     * @see cn.ancono.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.ancono.math.numberModels.api.MathCalculator)
     */
    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): Exp<N> {
        return Exp(newCalculator as RealCalculator, mapper.apply(c), mapper.apply(a))
    }

    /*
     * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
     */
    override fun valueEquals(obj: MathObject<T, RealCalculator<T>>): Boolean {
        if (obj is Ex<*>) {
            return (obj as Ex<T>).valueEquals(this)
        }
        if (obj !is Exp<*>) {
            return false
        }
        val exp = obj as Exp<T>
        return mc.isEqual(a, exp.a) && mc.isEqual(c, exp.c)
    }
}

/**
 * Returns the power function:
 * <pre>e^x</pre>
 * where `e` is the natural base of logarithm.
 * @author liyicheng
 * 2017-10-10 19:04
 *
 * @param <T>
</T> */
class Ex<T>
/**
 * @param mc
 */
internal constructor(mc: RealCalculator<T>) : AbstractSVFunction<T>(mc), DerivableSVFunction<T> {
    /*
     * @see cn.ancono.math.function.SVFunction#apply(java.lang.Object)
     */
    override fun apply(x: T): T {
        return mc.exp(x)
    }

    override val derivative: DerivableSVFunction<T>
        get() = this


    /*
     * @see cn.ancono.math.function.AbstractSVFunction#toString(cn.ancono.math.numberModels.api.NumberFormatter)
     */
    override fun toString(nf: NumberFormatter<T>): String {
        return "e^x"
    }

    /*
     * @see cn.ancono.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.ancono.math.numberModels.api.MathCalculator)
     */
    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): Ex<N> {
        return Ex(newCalculator as RealCalculator)
    }

    /*
     * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
     */
    override fun valueEquals(obj: MathObject<T, RealCalculator<T>>): Boolean {
        if (obj is Exp<*>) {
            val exp = obj as Exp<T>
            return mc.isEqual(mc.one, exp.c) && mc.isEqual(mc.constantValue(RealCalculator.STR_E)!!, exp.a)
        }
        return obj is Ex<*>
    }
}
