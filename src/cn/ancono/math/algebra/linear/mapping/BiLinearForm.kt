package cn.ancono.math.algebra.linear.mapping


/**
 * Describe binary linear form in linear algebra. A binary linear form is a function:
 * >  **U** × **V** -> **K**
 *
 * where **U**, **V** are linear space over **K**.
 * The function is required to be bi-linear, that is,
 * > f(x+y,z) = f(x,z) + f(y,z)
 *
 * > f(kx,z) = kf(x,z)
 *
 * > f(x,z+w) = f(x,z) + f(x,w)
 *
 * > f(x,kz) = kf(x,z)
 *
 *
 *
 * Created at 2019/5/29 18:44
 * @author  liyicheng
 */
interface IBiLinearForm<K : Any, U : Any, V : Any> {
    fun apply(u: U, v: V)
}

open class BiLinearForm<K : Any, U : Any, V : Any> {

}