/**
 * 2018-02-27
 */
package cn.timelives.java.math.algebra.abstractAlgebra.calculator

/**
 * A MonoidCalculator is a calculator specialized for monoid.
 * @author liyicheng
 * 2018-02-27 17:40
 */
interface MonoidCalculator<T : Any> : SemigroupCalculator<T> {


    /**
     * Returns the identity element of the semigroup.
     * @return
     */
    val identity: T


    /**
     * @param n a non-negative number
     */
    override fun gpow(x: T, n: Long): T {
        return if (n == 0L) {
            identity
        } else super.gpow(x, n)
    }
}
