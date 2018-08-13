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

    /**
     * @see MathCalculator.nroot
     */
    override fun nroot(x: T, n: Long): T {
        throwFor()
    }


    override fun constantValue(name: String): T {
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

    override val numberClass: Class<*>
        get() = zero.javaClass


}
