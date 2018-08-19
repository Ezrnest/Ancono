package cn.timelives.java.math.numberModels

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.exceptions.UnsupportedCalculationException


/**
 * An adapter for MathCalculator, all methods are implemented by throwing UnsupportedOperationException.
 * This class also provides some basic calculators for the frequently-used number classes.
 * @author lyc
 *
 * @param <T> the type of number to deal with
</T> */
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

    override fun isZero(para: T): Boolean {
        return isEqual(para, zero)
    }

    override fun isEqual(x: T, y: T): Boolean {
        throwFor()
    }

    override fun compare(para1: T, para2: T): Int {
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

    override fun divideLong(x: T, n: Long): T {
        throwFor()
    }

    override fun multiplyLong(x: T, n: Long): T {
        throwFor()
    }

    override fun reciprocal(x: T): T {
        throwFor()
    }

    override fun squareRoot(x: T): T {
        throwFor()
    }

    override fun pow(x: T, n: Long): T {
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
	 * @see cn.timelives.java.math.number_models.MathCalculator#exp(java.lang.Object)
	 */
    override fun exp(x: T): T {
        throwFor()
    }


    /* (non-Javadoc)
	 * @see cn.timelives.java.math.number_models.MathCalculator#ln(java.lang.Object)
	 */
    override fun ln(x: T): T {
        throwFor()
    }

    /* (non-Javadoc)
	 * @see cn.timelives.java.math.number_models.MathCalculator#sin(java.lang.Object)
	 */
    override fun sin(x: T): T {
        throwFor()
    }

    /* (non-Javadoc)
	 * @see cn.timelives.java.math.number_models.MathCalculator#arcsin(java.lang.Object)
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


}
