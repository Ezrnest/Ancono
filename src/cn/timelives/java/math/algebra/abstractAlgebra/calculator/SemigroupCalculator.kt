/**
 * 2018-02-27
 */
package cn.timelives.java.math.algebra.abstractAlgebra.calculator

import cn.timelives.java.math.function.MathBinaryOperator
import cn.timelives.java.utilities.ModelPatterns

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
}
