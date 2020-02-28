/**
 * 2018-02-27
 */
package cn.ancono.math.algebra.abstractAlgebra.calculator

import cn.ancono.math.function.MathBinaryOperator
import cn.ancono.utilities.ModelPatterns

/**
 * A semigroup calculator is a calculator specialized for semigroup.
 * @author liyicheng
 * 2018-02-27 17:31
 */
interface SemigroupCalculator<T : Any> : EqualPredicate<T>, MathBinaryOperator<T> {


    /**
     * Applies the operation defined in the semigroup.
     * @param x
     * @param y
     * @return
     */
    override fun apply(x: T, y: T): T


    /**
     * Determines whether the operation is commutative. It is false by default.
     */
    val isCommutative: Boolean
        get() = false

    /**
     * Determines whether the two elements are equal.
     * @param x
     * @param y
     * @return
     */
    override fun isEqual(x: T, y: T): Boolean

    /**
     * Returns `x^n=x*x*x....*x` defined in the semigroup.
     * @param x
     * @param n a positive number
     * @return
     */
    fun gpow(x: T, n: Long): T {
        return ModelPatterns.binaryProduce(n, x) { a, b -> this.apply(a, b) }
    }

    /**
     * Operator function of add for [T].
     * @see apply
     */
    operator fun T.plus(y: T) = apply(this, y)

    /**
     * Operator function for [T].
     * @see gpow
     */
    operator fun Long.times(x: T) = gpow(x, this)

    /**
     * Operator function for [T].
     * @see gpow
     */
    operator fun T.times(n: Long) = n * this
}


inline fun <T : Any, C : SemigroupCalculator<T>, R> C.eval(block: C.() -> R): R = this.run(block)