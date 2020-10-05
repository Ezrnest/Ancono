package cn.ancono.math.algebra.abs.calculator

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
     */
    override fun apply(x: T, y: T): T

    /**
     * Determines whether the operation is commutative. It is false by default.
     */
    @JvmDefault
    val isCommutative: Boolean
        get() = false

    @JvmDefault
    fun gpow(x: T, n: Long): T {
        return ModelPatterns.binaryProduce(n, x, { a: T, b: T -> this.apply(a, b) })
    }


    /**
     * Operator function of add for [T].
     * @see apply
     */
    @JvmDefault
    operator fun T.plus(y: T): T = apply(this, y)

    /**
     * Operator function for [T].
     * @see gpow
     */
    @JvmDefault
    operator fun Long.times(x: T): T = gpow(x, this)

    /**
     * Operator function for [T].
     * @see gpow
     */
    @JvmDefault
    operator fun T.times(n: Long) = n * this
}

inline fun <T : Any, C : SemigroupCalculator<T>, R> C.eval(block: C.() -> R): R = this.run(block)