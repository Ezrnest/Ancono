/**
 * 2018-02-27
 */
package cn.timelives.java.math.algebra.abstractAlgebra.calculator

/**
 * A GroupCalculator is a calculator specialized for group.
 * @author liyicheng
 * 2018-02-27 17:41
 */
interface GroupCalculator<T : Any> : MonoidCalculator<T> {

    /**
     * Returns the inverse of the element x.
     * @param x an element
     * @return
     */
    fun inverse(x: T): T

    /**
     * Returns `x^n`, which is well defined.
     *
     *
     *
     *  * If `n=0`, returns the identity element.
     *  * If `n>0`, returns the identity result as defined in semigroup.
     * <lI>If `n<0`, returns `inverse(gpow(x,-n))`.
     *
    </lI> */
    override fun gpow(x: T, n: Long): T {
        if (n == 0L) {
            return identity
        }
        return if (n > 0) {
            super.gpow(x, n)
        } else {
            val t = super.gpow(x, -n)
            inverse(t)
        }
    }


}
