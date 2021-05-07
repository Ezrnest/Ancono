package cn.ancono.math.algebra.abs.calculator

/*
 * Created by liyicheng at 2020-03-07 10:25
 */
/**
 * The ring calculator defines some basic operations in a group.
 *
 * @author liyicheng
 * 2018-02-28 18:28
 */
interface RingCalculator<T> : AbelGroupCal<T>, MulSemigroupCal<T> {

    /**
     * Gets the zero element in this ring, which is the identity element of the addition group.
     * @return `0`
     */
    override val zero: T

    /**
     * Returns the result of `x+y`.
     * @return `x+y`
     */
    override fun add(x: T, y: T): T

    /**
     * Returns the negate of this number.
     *
     * @return `-x`
     */
    override fun negate(x: T): T

    /**
     * Returns the result of `x*y`. This operation may be not commutative.
     * @return `x*y`
     */
    override fun multiply(x: T, y: T): T

    /**
     * Return `x ^ n` as defined in the multiplicative semigroup.
     *
     * @param n a positive number
     * @return `x ^ n`
     */
    override fun pow(x: T, n: Long): T {
        return super.pow(x, n)
    }

    /**
     * Return the result of `n * p`, which is equal to applying addition to
     * `x` for `n` times. This method can be implemented for better performance.
     * @param n a long
     */
    override fun multiplyLong(x: T, n: Long): T {
        return super.multiplyLong(x, n)
    }



}


//fun <T> RingCalculator<T>.asSemigroupCalculator() = GroupCalculators.asSemigroupCalculator(this)