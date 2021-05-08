package cn.ancono.math.algebra.abs.calculator

/*
 * Created by liyicheng at 2020-03-07 10:25
 */
/**
 * A ring calculator is the combination of an abelian group calculator of addition `+` and
 * a multiplicative group calculator of multiplication `*`.
 *
 * The operation satisfies:
 *
 * 1. Abelian group of `+`.
 * 2. Semi-group of `*`.
 * 3. Distribution law:
 *
 *     x * (y+z) = x * y + x * z
 *
 *     (y + z) * x = y * x + z * x
 *
 *
 * @author liyicheng
 * 2018-02-28 18:28
 * @see cn.ancono.math.algebra.abs.structure.Ring
 */
interface RingCalculator<T> : AbelGroupCal<T>, MulSemigroupCal<T> {

    /**
     * Gets the zero element in this ring, which is the identity element of the additive group.
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
}


//fun <T> RingCalculator<T>.asSemigroupCalculator() = GroupCalculators.asSemigroupCalculator(this)