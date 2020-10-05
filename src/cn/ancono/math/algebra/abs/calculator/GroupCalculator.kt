package cn.ancono.math.algebra.abs.calculator


/**
 * A GroupCalculator is a calculator specialized for group.
 * @author liyicheng
 * 2018-02-27 17:41
 */
interface GroupCalculator<T : Any> : MonoidCalculator<T> {
    /**
     * Returns the inverse of the element x.
     * @param x an element
     */
    fun inverse(x: T): T

    /**
     * Returns the result of `x-y`, which is equal to `x+inverse(y)`.
     * @return `x-y`
     */
    @JvmDefault
    fun subtract(x: T, y: T): T {
        return apply(x, inverse(y))
    }

    @JvmDefault
    override fun gpow(x: T, n: Long): T {
        if (n == 0L) {
            return identity
        }
        val t: T = super<MonoidCalculator>.gpow(x, n)
        return if (n > 0) {
            t
        } else {
            inverse(t)
        }
    }


    /**
     * Operator function inverse.
     * @see inverse
     */
    @JvmDefault
    operator fun T.unaryMinus(): T = inverse(this)

    /**
     * Operator function subtract.
     * @see subtract
     */
    @JvmDefault
    operator fun T.minus(y: T): T = subtract(this, y)

}


/**
 * Returns -x+a+x.
 */
fun <T : Any> GroupCalculator<T>.conjugateBy(a: T, x: T) = eval { (-x) + a + x }

/**
 * Returns the commutator of [a] and [b]: `[a,b]` = `a^-1*b^-1*a*b`
 */
fun <T : Any> GroupCalculator<T>.commutator(a: T, b: T) = eval { -a - b + a + b }