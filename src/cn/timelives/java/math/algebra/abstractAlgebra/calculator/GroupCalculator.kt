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
     * Returns the result of `x-y`, which is equal to `x+inverse(y)`.
     * @param x
     * @param y
     * @return `x-y`
     */
    fun subtract(x: T, y: T): T = apply(x,inverse(y))

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

    /**
     * Operator function inverse.
     * @see inverse
     */
    operator fun T.unaryMinus() = inverse(this)
    /**
     * Operator function subtract.
     * @see subtract
     */
    operator fun T.minus(y : T) = subtract(this,y)

}

/**
 * Returns -x+a+x.
 */
fun <T:Any> GroupCalculator<T>.conjugateBy(a : T, x : T) = eval { (-x)+a+x }

/**
 * Returns the commutator of [a] and [b]: `[a,b]` = `a^-1*b^-1*a*b`
 */
fun <T:Any> GroupCalculator<T>.commutator(a : T, b : T) = eval { -a-b+a+b }