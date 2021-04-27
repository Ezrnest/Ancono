@file:Suppress("NOTHING_TO_INLINE")

package cn.ancono.math.numberModels.api

import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.algebra.abs.calculator.GroupCalculator
import cn.ancono.math.algebra.abs.calculator.RingCalculator


interface MonoidNumberModel<T : MonoidNumberModel<T>> {
    fun add(y: T): T
}

interface MulMonoidNumberModel<T : MulMonoidNumberModel<T>> {
    fun multiply(y: T): T
}


/**
 * Describes a number model which is suitable for a group. The operations are named as addition group.
 */
interface GroupNumberModel<T : GroupNumberModel<T>> : MonoidNumberModel<T> {
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

/**
 * Describes a number model which is suitable for a group. The operations are named as multiplication group.
 */
interface MulGroupNumberModel<T : MulGroupNumberModel<T>> : MulMonoidNumberModel<T> {
    /**
     * Returns `this * y` as the operation defined in the group.
     */
    override fun multiply(y: T): T

    /**
     * Returns the reciprocal of `this`, that is, the element `e` such that `e * this = this * e = 1`
     */
    fun reciprocal(): T

    /**
     * Returns `this - y`, which should be equal to `add(negate(y))`.
     */
    fun divide(y: T): T = multiply(y.reciprocal())
}

inline operator fun <T : MonoidNumberModel<T>> MonoidNumberModel<T>.plus(y: T): T = add(y)

inline operator fun <T : GroupNumberModel<T>> GroupNumberModel<T>.unaryMinus(): T = negate()
inline operator fun <T : GroupNumberModel<T>> GroupNumberModel<T>.minus(y: T): T = subtract(y)

inline operator fun <T : MulMonoidNumberModel<T>> MulMonoidNumberModel<T>.times(y: T): T = multiply(y)
inline operator fun <T : MulGroupNumberModel<T>> MulGroupNumberModel<T>.div(y: T): T = divide(y)


/**
 * Describes a number model which is suitable for a ring.
 */
interface RingNumberModel<T : RingNumberModel<T>> : GroupNumberModel<T>, MulMonoidNumberModel<T> {
    override fun multiply(y: T): T

    fun isZero(): Boolean
}

//inline operator fun <T : RingNumberModel<T>> RingNumberModel<T>.times(y: T): T = multiply(y)

/**
 * Describes a number model which is suitable for a division ring.
 * @see cn.ancono.math.algebra.abs.structure.DivisionRing
 */
interface DivisionRingNumberModel<T : DivisionRingNumberModel<T>> : RingNumberModel<T>, MulGroupNumberModel<T> {
    override fun reciprocal(): T
    override fun divide(y: T): T = multiply(y.reciprocal())
}

//inline operator fun <T : DivisionRingNumberModel<T>> DivisionRingNumberModel<T>.div(y: T): T = divide(y)
/**
 * Describes a number model which is suitable for a field.
 * @see cn.ancono.math.algebra.abs.structure.Field
 */
interface FieldNumberModel<T : FieldNumberModel<T>> : DivisionRingNumberModel<T>

/**
 * Describes the number model of a (left) module.
 */
interface ModuleModel<R, V : ModuleModel<R, V>> : GroupNumberModel<V> {


    /**
     * Performs the scalar multiplication.
     */
    fun multiply(k: R): V


}

/**
 * Describe the number model for a linear space,
 */
interface VectorModel<K, V : VectorModel<K, V>> : ModuleModel<K, V> {
    /**
     * Performs the scalar multiplication.
     */
    override fun multiply(k: K): V

    /**
     * Performs the scalar division.
     */
    fun divide(k: K): V

    fun isLinearRelevant(v: V): Boolean {
        throw UnsupportedOperationException()
    }
}

inline operator fun <K, V : VectorModel<K, V>> VectorModel<K, V>.times(k: K) = multiply(k)
inline operator fun <K, V : VectorModel<K, V>> VectorModel<K, V>.div(k: K) = divide(k)
inline operator fun <K, V : ModuleModel<K, V>> K.times(v: ModuleModel<K, V>) = v.multiply(this)


interface AlgebraModel<K, V : AlgebraModel<K, V>> : VectorModel<K, V>, RingNumberModel<V> {
    override fun multiply(y: V): V
}

inline operator fun <K, V : AlgebraModel<K, V>> AlgebraModel<K, V>.times(y: V) = multiply(y)

object NumberModels {
    /**
     * Gets a group calculator on the GroupNumberModel.
     */
    fun <T : GroupNumberModel<T>> groupCalculator(identity: T): GroupCalculator<T> {
        return object : GroupCalculator<T> {
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

    fun <T : RingNumberModel<T>> ringCalculator(zero: T) = object : RingCalculator<T> {
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

    fun <T : FieldNumberModel<T>> fieldCalculator(zero: T, one: T, ch: Long) = object : FieldCalculator<T> {

        override val characteristic: Long = ch

        override fun multiply(x: T, y: T): T {
            return x * y
        }

        override fun reciprocal(x: T): T {
            return x.reciprocal()
        }

        override val one: T
            get() = one
        override val zero: T
            get() = zero

        override fun add(x: T, y: T): T {
            return x + y
        }

        override fun negate(x: T): T {
            return -x
        }

        override fun isEqual(x: T, y: T): Boolean {
            return x == y
        }

    }
}