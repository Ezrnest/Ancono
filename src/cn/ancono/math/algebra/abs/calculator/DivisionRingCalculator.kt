/**
 * 2018-02-28
 */
package cn.ancono.math.algebra.abs.calculator


import cn.ancono.math.algebra.abs.GroupCalculators
import cn.ancono.math.exceptions.ExceptionUtil

/**
 *
 *
 * @author liyicheng
 * 2018-02-28 19:01
 */
interface DivisionRingCalculator<T : Any> : UnitRingCalculator<T> {

    /**
     * Returns the multiplicative inverse of element `x`.
     * @param x a number
     * @return x<sup>-1</sup>
     */
    fun reciprocal(x: T): T

    /**
     * Returns the result of `x / y`, which is equal to
     * `x * reciprocal(y) `
     * @param x a number
     * @param y another number
     * @return `x / y`
     */
    @JvmDefault
    fun divide(x: T, y: T): T = multiply(x, reciprocal(y))

    /**
     * Returns the result of `x / n`. The default implement is
     * `divide(x,multiplyLong(getOne(),n))`
     * @param x a number
     * @param n a non-zero long
     * @return `x / n`
     */
    @JvmDefault
    fun divideLong(x: T, n: Long): T {
        if (n == 0L) {
            ExceptionUtil.dividedByZero()
        }
        return divide(x, multiplyLong(one, n))
    }

    /*
	 * @see cn.ancono.math.algebra.abstractAlgebra.calculator.UnitRingCalculator#pow(java.lang.Object, long)
	 */
    @JvmDefault
    override fun pow(x: T, n: Long): T {
        if (n == 0L) {
            return one
        }
        return if (n > 0) {
            super.pow(x, n)
        } else {
            val t = super.pow(x, -n)
            reciprocal(t)
        }
    }

    /**
     * Operator function divide.
     * @see divide
     */
    @JvmDefault
    operator fun T.div(y: T) = divide(this, y)

    /**
     * Operator function divideLong.
     * @see divideLong
     */
    @JvmDefault
    operator fun T.div(y: Long) = divideLong(this, y)

//    @JvmDefault
//    override fun isUnit(x: T): Boolean {
//        // non-zero element in a division ring is always invertible
//        return !isZero(x)
//    }
}


fun <T : Any> DivisionRingCalculator<T>.asGroupCalculator() = GroupCalculators.asGroupCalculator(this)