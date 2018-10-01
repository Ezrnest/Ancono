/**
 * 2018-02-28
 */
package cn.timelives.java.math.algebra.abstractAlgebra.calculator

import cn.timelives.java.utilities.ModelPatterns

/**
 * The ring calculator defines some basic operations in a group.
 * @author liyicheng
 * 2018-02-28 18:28
 */
interface RingCalculator<T : Any> : GroupCalculator<T> {
    /**
     * Returns `true` because a ring is always an abelian group by its
     * addition.
     */
    override val isCommutative: Boolean
        get() = true

    /**
     * Determines whether the multiplication is commutative. It is false by default.
     */
    val multiplyIsCommutative : Boolean
        get() = false

    /**
     * Gets the zero element in this ring, which is the identity element of the addition group.
     * @return `0`
     */
    val zero: T

    /**
     * Returns the result of `x+y`.
     * @param x
     * @param y
     * @return `x+y`
     */
    fun add(x: T, y: T): T

    /**
     * Returns the negate of this number.
     *
     * @param x
     *
     * @return `-x`
     */
    fun negate(x: T): T

    /**
     * Returns the result of `x-y`, which is equal to `x+negate(y)`.
     * @param x
     * @param y
     * @return `x-y`
     */
    fun subtract(x: T, y: T): T

    /**
     * Returns the result of `x*y`. This operation may be not commutative.
     * @param x
     * @param y
     * @return `x*y`
     */
    fun multiply(x: T, y: T): T

    /**
     * Return `x ^ n` as defined in the multiplicative semigroup.
     *
     * @param x
     *
     * @param n a positive number
     * @return `x ^ n`
     */
    fun pow(x: T, n: Long): T {
        return ModelPatterns.binaryProduce(n, x) { a, b -> this.multiply(a, b) }
    }

    /**
     * Return the result of `n * p`, which is equal to applying addition to
     * `x` for `n` times. This method can be implemented for better performance.
     * @param x
     * @param n a long
     * @return
     */
    fun multiplyLong(x: T, n: Long): T {
        return super.gpow(x, n)
    }


    @get:Deprecated("use {@link #zero} instead for more clarity.", ReplaceWith("zero"))
    override val identity: T
        get() = zero


    @Deprecated("use {@link #add(Object, Object)} instead for more clarity.", ReplaceWith("add(x, y)"))
    override fun apply(x: T, y: T): T {
        return add(x, y)
    }


    @Deprecated("use {@link #negate(Object)} instead for more clarity.", ReplaceWith("negate(x)"))
    override fun inverse(x: T): T {
        return negate(x)
    }

    @Deprecated("use {@link #multiplyLong(Object, long)} instead for more clarity.", ReplaceWith("super@GroupCalculator.gpow(x, n)"))
    override fun gpow(x: T, n: Long): T {
        return super.gpow(x, n)
    }
}
