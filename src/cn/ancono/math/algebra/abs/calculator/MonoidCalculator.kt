package cn.ancono.math.algebra.abs.calculator

/*
 * Created by liyicheng at 2020-03-06 22:14
 */
/**
 * A MonoidCalculator is a calculator specialized for monoid.
 * @author liyicheng
 * 2018-02-27 17:40
 */
interface MonoidCalculator<T : Any> : SemigroupCalculator<T> {
    /**
     * Returns the identity element of the semigroup.
     */
    val identity: T

    @JvmDefault
    override fun gpow(x: T, n: Long): T {
        return if (n == 0L) {
            identity
        } else {
            super<SemigroupCalculator>.gpow(x, n)
        }
    }

    /**
     * Returns the class of the number.
     */
    @JvmDefault
    val numberClass: Class<T>
        get() = identity.javaClass
}