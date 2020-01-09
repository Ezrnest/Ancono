@file:Suppress("NOTHING_TO_INLINE")

package cn.timelives.java.math.numberModels.api

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.RingCalculator
import java.lang.UnsupportedOperationException


interface MonoidNumberModel<T : MonoidNumberModel<T>>{
    fun add(y : T) : T
}

/**
 * Describes a number model which is suitable for a group.
 */
interface GroupNumberModel<T : GroupNumberModel<T>> : MonoidNumberModel<T>{
    /**
     * Returns `this + y` as the operation defined in the group.
     */
    override fun add(y: T): T

    /**
     * Returns the negate of `this`.
     */
    fun negate(): T

    /**
     * Returns `this - y`, which should be equal to `add(negate(y))`.
     */
    fun subtract(y: T): T = add(y.negate())
}

inline operator fun <T : GroupNumberModel<T>> GroupNumberModel<T>.plus(y: T): T = add(y)

inline operator fun <T : GroupNumberModel<T>> GroupNumberModel<T>.unaryMinus(): T = negate()
inline operator fun <T : GroupNumberModel<T>> GroupNumberModel<T>.minus(y : T): T = subtract(y)

/**
 * Describes a number model which is suitable for a ring.
 */
interface RingNumberModel<T : RingNumberModel<T>> : GroupNumberModel<T> {
    fun multiply(y: T): T

    fun isZero() : Boolean
}

inline operator fun <T : RingNumberModel<T>> RingNumberModel<T>.times(y: T): T = multiply(y)

/**
 * Describes a number model which is suitable for a division ring.
 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.DivisionRing
 */
interface DivisionRingNumberModel<T : DivisionRingNumberModel<T>> : RingNumberModel<T> {
    fun reciprocal(): T
    fun divide(y: T): T = multiply(y.reciprocal())
}

inline operator fun <T : DivisionRingNumberModel<T>> DivisionRingNumberModel<T>.div(y: T): T = divide(y)
/**
 * Describes a number model which is suitable for a field.
 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Field
 */
interface FieldNumberModel<T : FieldNumberModel<T>> : DivisionRingNumberModel<T>

/**
 * Describe the number model for a linear space,
 */
interface VectorModel<K, V : VectorModel<K,V>> : GroupNumberModel<V>{
    /**
     * Performs the scalar multiplication.
     */
    fun multiply(k : K) : V

    fun isLinearRelevant(v : V) : Boolean{
        throw UnsupportedOperationException()
    }
}

inline operator fun <K, V : VectorModel<K,V>> VectorModel<K,V>.times(k: K) = multiply(k)
inline operator fun <K, V : VectorModel<K,V>> K.times(v : VectorModel<K,V>) = v.multiply(this)



interface AlgebraModel<K, V : AlgebraModel<K,V>> : VectorModel<K,V>,RingNumberModel<V>{
    override fun multiply(y : V) : V
}

inline operator fun <K, V : AlgebraModel<K,V>> AlgebraModel<K,V>.times(y : V) = multiply(y)

object NumberModels{
    /**
     * Gets a group calculator on the GroupNumberModel.
     */
    fun <T: GroupNumberModel<T>> groupCalculator(identity : T) : GroupCalculator<T>{
        return object : GroupCalculator<T>{
            override fun inverse(x: T): T {
                return x.negate()
            }

            override val identity: T
                get() = identity

            override fun apply(x: T, y: T): T {
                return x.add(y)
            }

            override fun isEqual(x: T, y: T): Boolean {
                return x == y
            }
        }
    }

    fun <T: RingNumberModel<T>> ringCalculator(zero : T) = object : RingCalculator<T>{
        override val zero: T
        get() = zero

        override fun add(x: T, y: T): T {
            return x.add(y)
        }

        override fun negate(x: T): T {
            return x.negate()
        }

        override fun multiply(x: T, y: T): T {
            return x.multiply(y)
        }

        override fun isEqual(x: T, y: T): Boolean {
            return x == y
        }
    }

}