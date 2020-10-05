package cn.ancono.math.algebra.abs.calculator

import cn.ancono.math.algebra.abs.GroupCalculators
import cn.ancono.utilities.ModelPatterns

/*
 * Created by liyicheng at 2020-03-07 10:25
 */
/**
 * The ring calculator defines some basic operations in a group.
 * @author liyicheng
 * 2018-02-28 18:28
 */
interface RingCalculator<T : Any> : GroupCalculator<T> {
    /**
     * Returns `true` because a ring is always an Abelian group by its
     * addition.
     */
    @JvmDefault
    override val isCommutative: Boolean
        @get:JvmName("isCommutative")
        get() = true

    /**
     * Determines whether the multiplication is commutative. It is false by default.
     */
    @JvmDefault
    val isMultiplyCommutative: Boolean
        @get:JvmName("isMultiplyCommutative")
        get() = false

    /**
     * Gets the zero element in this ring, which is the identity element of the addition group.
     * @return `0`
     */
    val zero: T

    /**
     * Determines whether `x` is equal to [zero]. This method is added for convenience.
     */
    @JvmDefault
    fun isZero(x: T): Boolean {
        return isEqual(x, zero)
    }

    /**
     * Returns the result of `x+y`.
     * @return `x+y`
     */
    fun add(x: T, y: T): T

    /**
     * Returns the negate of this number.
     *
     * @return `-x`
     */
    fun negate(x: T): T

    /**
     * Returns the result of `x*y`. This operation may be not commutative.
     * @return `x*y`
     */
    fun multiply(x: T, y: T): T

    /**
     * Return `x ^ n` as defined in the multiplicative semigroup.
     *
     * @param n a positive number
     * @return `x ^ n`
     */
    @JvmDefault
    fun pow(x: T, n: Long): T {
        return ModelPatterns.binaryProduce(n, x, { a: T, b: T -> multiply(a, b) })
    }

    /**
     * Return the result of `n * p`, which is equal to applying addition to
     * `x` for `n` times. This method can be implemented for better performance.
     * @param n a long
     */
    @JvmDefault
    fun multiplyLong(x: T, n: Long): T {
        return super<GroupCalculator>.gpow(x, n)
    }

    @JvmDefault
    @Deprecated("use {@link #add(Object, Object)} instead", ReplaceWith("add(x, y)"))
    override fun apply(x: T, y: T): T {
        return add(x, y)
    }

    @JvmDefault
    @Deprecated("use {@link #negate(Object)} instead", ReplaceWith("negate(x)"))
    override fun inverse(x: T): T {
        return negate(x)
    }

    @JvmDefault
    @get:Deprecated("use {@link #getZero()} instead", ReplaceWith("zero"))
    override val identity: T
        get() = zero

    @JvmDefault
    @Deprecated("use {@link #multiplyLong(Object, long)} instead.", ReplaceWith("multiplyLong(x,n)"))
    override fun gpow(x: T, n: Long): T {
        return super<GroupCalculator>.gpow(x, n)
    }

    /**
     * Operator function multiply.
     * @see multiply
     */
    @JvmDefault
    operator fun T.times(y: T) = multiply(this, y)

    @JvmDefault
    override fun Long.times(x: T): T {
        return multiplyLong(x, this)
    }

    @JvmDefault
    override fun T.times(n: Long): T {
        return multiplyLong(this, n)
    }

}


fun <T : Any> RingCalculator<T>.asSemigroupCalculator() = GroupCalculators.asSemigroupCalculator(this)