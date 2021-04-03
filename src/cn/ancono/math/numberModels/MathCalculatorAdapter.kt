package cn.ancono.math.numberModels

import cn.ancono.math.MathCalculator
import cn.ancono.math.exceptions.UnsupportedCalculationException


/**
 * An adapter for MathCalculator, all methods are implemented by throwing UnsupportedOperationException.
 * This class also provides some basic calculators for the frequently-used number classes.
 * @author lyc
 *
 * @param T the type of number to deal with
 *
 */
abstract class MathCalculatorAdapter<T : Any> : MathCalculator<T> {

    override val one: T
        get() {
            throwFor()
        }

    override val zero: T
        get() {
            throwFor()
        }

    @Throws(UnsupportedCalculationException::class)
    private fun throwFor(): Nothing {
        throw UnsupportedCalculationException("Adapter")
    }

    override fun isZero(x: T): Boolean {
        return isEqual(x, zero)
    }

    override fun isEqual(x: T, y: T): Boolean {
        throwFor()
    }

    override fun compare(x: T, y: T): Int {
        throwFor()
    }

    override val isComparable: Boolean = false

    override fun add(x: T, y: T): T {
        throwFor()
    }

    override fun negate(x: T): T {
        throwFor()
    }

    override fun abs(para: T): T {
        throwFor()
    }

    override fun subtract(x: T, y: T): T {
        throwFor()
    }

    override fun multiply(x: T, y: T): T {
        throwFor()
    }

    override fun divide(x: T, y: T): T {
        throwFor()
    }

    override fun multiplyLong(x: T, n: Long): T {
        throwFor()
    }

    override fun divideLong(x: T, n: Long): T {
        throwFor()
    }

    override fun reciprocal(x: T): T {
        throwFor()
    }

    override fun squareRoot(x: T): T {
        throwFor()
    }

    override fun exp(a: T, b: T): T {
        return exp(multiply(ln(a), b))
    }

    override fun log(a: T, b: T): T {
        return divide(ln(b), ln(a))
    }

    override fun cos(x: T): T {
        return squareRoot(subtract(one, multiply(x, x)))
    }

    override fun tan(x: T): T {
        return divide(sin(x), cos(x))
    }

    override fun arccos(x: T): T {
        return subtract(divideLong(constantValue(MathCalculator.STR_PI)!!, 2L), arcsin(x))
    }


    override fun arctan(x: T): T {
        return arcsin(divide(x, squareRoot(add(one, multiply(x, x)))))
    }

    /**
     * @see MathCalculator.nroot
     */
    override fun nroot(x: T, n: Long): T {
        throwFor()
    }


    override fun constantValue(name: String): T? {
        throwFor()
    }

    /* (non-Javadoc)
	 * @see cn.ancono.math.number_models.MathCalculator#exp(java.lang.Object)
	 */
    override fun exp(x: T): T {
        throwFor()
    }


    /* (non-Javadoc)
	 * @see cn.ancono.math.number_models.MathCalculator#ln(java.lang.Object)
	 */
    override fun ln(x: T): T {
        throwFor()
    }

    /* (non-Javadoc)
	 * @see cn.ancono.math.number_models.MathCalculator#sin(java.lang.Object)
	 */
    override fun sin(x: T): T {
        throwFor()
    }

    /* (non-Javadoc)
	 * @see cn.ancono.math.number_models.MathCalculator#arcsin(java.lang.Object)
	 */
    override fun arcsin(x: T): T {
        throwFor()
    }

    @Suppress("DEPRECATION")
    @Deprecated("use {@link #add(Object, Object)} instead for more clarity.", ReplaceWith("add(x, y)"))
    override fun apply(x: T, y: T): T {
        return super.apply(x, y)
    }

    @Suppress("DEPRECATION")
    @Deprecated("use {@link #negate(Object)} instead for more clarity.", ReplaceWith("negate(x)"))
    override fun inverse(x: T): T {
        return super.inverse(x)
    }

    @Suppress("DEPRECATION")
    @Deprecated("use {@link #multiplyLong(Object, long)} instead for more clarity.", ReplaceWith("super@GroupCalculator.gpow(x, n)"))
    override fun gpow(x: T, n: Long): T {
        return super.gpow(x, n)
    }

    override val numberClass: Class<*>
        get() = zero.javaClass


    override fun T.div(y: T): T {
        return divide(this, y)
    }

    override fun T.div(y: Long): T {
        return divideLong(this, y)
    }

    override fun T.times(y: T): T {
        return multiply(this, y)
    }

    override fun Long.times(x: T): T {
        return multiplyLong(x, this)
    }

    override fun T.times(n: Long): T {
        return multiplyLong(this, n)
    }

    override fun T.unaryMinus(): T {
        return negate(this)
    }

    override fun T.minus(y: T): T {
        return subtract(this, y)
    }

    override fun T.plus(y: T): T {
        return add(this, y)
    }

    override fun T.compareTo(y: T): Int {
        return compare(this, y)
    }
}
