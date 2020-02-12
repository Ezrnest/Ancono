/**
 * 2018-02-28
 */
package cn.timelives.java.math.algebra.abstractAlgebra.calculator

import cn.timelives.java.math.algebra.abstractAlgebra.EqualRelation

import java.util.Objects

/**
 * A predicate that tests whether two objects are equal. EqualPredicate
 * is the identity with [EqualRelation] but the method's name is "isEqual".
 * @author liyicheng
 * 2018-02-28 17:33
 */
@FunctionalInterface
interface EqualPredicate<T : Any> {
    /**
     * Evaluates whether the two objects are equal.
     *
     *  * It is *reflexive*: for any non-null reference value `x`,
     * `test(x,x)` should return `true`.
     *  * It is *symmetric*: for any non-null reference values `x` and
     * `y`, `test(x,y)` should return `true` if and only if `test(y,x)`
     * returns `true`.
     *  * It is *transitive*: for any non-null reference values `x`,
     * `y`, and `z`, if `test(x,y)` returns `true` and
     * `test(y,z)` returns `true`, then `test(x,z)` should return `true`.
     *  * It is *consistent*: for any non-null reference values `x` and
     * `y`, multiple invocations of `test(x,y)` consistently return
     * `true` or consistently return `false`, provided no information
     * used in `test` comparisons on the objects is modified.
     *
     *
     * @throws NullPointerException
     * if `x==null || y == null`
     */
    fun isEqual(x: T, y: T): Boolean

    companion object {

        private val NATURAL_EQUAL: EqualPredicate<*> = object : EqualPredicate<Any> {
            override fun isEqual(x: Any, y: Any): Boolean {
                return x == Objects.requireNonNull<Any>(y)
            }
        }

        private val REF_EQUAL : EqualPredicate<*> = object : EqualPredicate<Any>{
            override fun isEqual(x: Any, y: Any): Boolean {
                return x === y
            }
        }

        /**
         * Returns a equal predicate that considers two elements are equal if and only if
         * the two objects are equal by [Any.equals].
         */
        @Suppress("UNCHECKED_CAST")
        fun <T : Any> naturalEqual(): EqualPredicate<T> {
            return NATURAL_EQUAL as EqualPredicate<T>
        }

        /**
         * Returns a equal predicate that considers two elements are equal if and only if
         * the two objects are equal by reference.
         */
        @Suppress("UNCHECKED_CAST")
        fun <T:Any> referenceEqual() : EqualPredicate<T>{
            return REF_EQUAL as EqualPredicate<T>
        }
    }
}
