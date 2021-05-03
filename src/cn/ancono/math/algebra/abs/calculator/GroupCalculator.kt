package cn.ancono.math.algebra.abs.calculator

import cn.ancono.math.algebra.linear.MatrixImpl.inverse
import cn.ancono.math.function.MathBinaryOperator
import cn.ancono.utilities.ModelPatterns
import kotlin.math.abs

interface ICalculator<T> : EqualPredicate<T> {
    /**
     * Returns the class of the number.
     */
    @JvmDefault
    val numberClass: Class<T>
}

inline fun <T, C : ICalculator<T>, R> C.eval(block: C.() -> R): R = this.run(block)

interface AbelSemiGroupCal<T> : ICalculator<T> {
    //Created by lyc at 2021-05-03 22:09


    fun add(x: T, y: T): T


    @JvmDefault
    operator fun T.plus(y: T): T = add(this, y)

    @JvmDefault
    fun multiplyLong(x: T, n: Long): T {
        require(n > 0)
        return ModelPatterns.binaryProduce(n, x, this::add)
    }

    /**
     * Operator function for [T].
     * @see multiplyLong
     */
    @JvmDefault
    operator fun Long.times(x: T): T = multiplyLong(x, this)

    /**
     * Operator function for [T].
     * @see multiplyLong
     */
    @JvmDefault
    operator fun T.times(n: Long) = multiplyLong(this, n)

}

interface AbelMonoidCal<T> : AbelSemiGroupCal<T> {

    /**
     * Gets the zero element.
     */
    val zero: T

    /**
     * Determines whether [x] is zero.
     *
     * This method is the same as `isEqual(zero, x)`.
     */
    @JvmDefault
    fun isZero(x: T) = isEqual(zero, x)


    @JvmDefault
    override fun multiplyLong(x: T, n: Long): T {
        if (n == 0L) {
            return zero
        }
        return super.multiplyLong(x, n)
    }

    /**
     * Returns the class of the number.
     */
    @JvmDefault
    override val numberClass: Class<T>
        @Suppress("UNCHECKED_CAST")
        get() = (zero as Any).javaClass as Class<T>
}


interface AbelGroupCal<T> : AbelMonoidCal<T> {

    @JvmDefault
    override fun multiplyLong(x: T, n: Long): T {
        if (n == 0L) {
            return zero
        }
        val b = if (n < 0) {
            negate(x)
        } else {
            x
        }
        return ModelPatterns.binaryProduce(abs(n), b, this::add)
    }

    fun negate(x: T): T

    @JvmDefault
    fun subtract(x: T, y: T): T {
        return add(x, negate(y))
    }


    /**
     * Operator function inverse.
     * @see inverse
     */
    @JvmDefault
    operator fun T.unaryMinus(): T = negate(this)

    /**
     * Operator function subtract.
     * @see subtract
     */
    @JvmDefault
    operator fun T.minus(y: T): T = subtract(this, y)
}


/**
 * A semigroup calculator is a calculator specialized for semigroup.
 * @author liyicheng
 * 2018-02-27 17:31
 */
interface MulSemiGroupCal<T> : ICalculator<T> {
    /**
     * Applies the operation defined in the semigroup.
     */
    fun multiply(x: T, y: T): T

    /**
     * Determines whether the operation is commutative. It is false by default.
     */
    @JvmDefault
    val isCommutative: Boolean
        get() = false

    @JvmDefault
    fun pow(x: T, n: Long): T {
        return ModelPatterns.binaryProduce(n, x, this::multiply)
    }


    /**
     * Operator function of add for [T].
     * @see apply
     */
    @JvmDefault
    operator fun T.times(y: T): T = multiply(this, y)


}


/*
 * Created by liyicheng at 2020-03-06 22:14
 */
/**
 * A MonoidCalculator is a calculator specialized for monoid.
 * @author liyicheng
 * 2018-02-27 17:40
 */
interface MulMonoidCal<T> : MulSemiGroupCal<T> {
    /**
     * Returns the identity element of the semigroup.
     */
    val one: T

    @JvmDefault
    override fun pow(x: T, n: Long): T {
        return if (n == 0L) {
            one
        } else {
            super.pow(x, n)
        }
    }

    /**
     * Returns the class of the number.
     */
    @JvmDefault
    override val numberClass: Class<T>
        @Suppress("UNCHECKED_CAST")
        get() = (one as Any).javaClass as Class<T>
}


/**
 * A GroupCalculator is a calculator specialized for group.
 * @author liyicheng
 * 2018-02-27 17:41
 */
interface MulGroupCal<T> : MulMonoidCal<T> {
    /**
     * Returns the inverse of the element x.
     * @param x an element
     */
    fun reciprocal(x: T): T

    /**
     * Returns the result of `x * y^{-1}`, which is equal to `multiply(x,inverse(y))`.
     * @return `x * y^{-1}`
     */
    @JvmDefault
    fun divide(x: T, y: T): T {
        return multiply(x, reciprocal(y))
    }

    @JvmDefault
    override fun pow(x: T, n: Long): T {
        if (n == 0L) {
            return one
        }
        val b = if (n < 0) {
            reciprocal(x)
        } else {
            x
        }
        return ModelPatterns.binaryProduce(abs(n), b, this::multiply)
    }


//    /**
//     * Operator function inverse.
//     * @see inverse
//     */
//    @JvmDefault
//    operator fun T.unaryMinus(): T = inverse(this)

    /**
     * Operator function subtract.
     * @see subtract
     */
    @JvmDefault
    operator fun T.div(y: T): T = divide(this, y)

}


/**
 * A semigroup calculator is a calculator specialized for semigroup.
 * @author liyicheng
 * 2018-02-27 17:31
 */
interface SemigroupCalculator<T> : ICalculator<T>, MathBinaryOperator<T> {
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
        return ModelPatterns.binaryProduce(n, x) { a: T, b: T -> this.apply(a, b) }
    }
}

/*
 * Created by liyicheng at 2020-03-06 22:14
 */
/**
 * A MonoidCalculator is a calculator specialized for monoid.
 * @author liyicheng
 * 2018-02-27 17:40
 */
interface MonoidCalculator<T> : SemigroupCalculator<T> {
    /**
     * Returns the identity element of the semigroup.
     */
    val identity: T

    @JvmDefault
    override fun gpow(x: T, n: Long): T {
        return if (n == 0L) {
            identity
        } else {
            super.gpow(x, n)
        }
    }

    /**
     * Returns the class of the number.
     */
    @JvmDefault
    override val numberClass: Class<T>
        @Suppress("UNCHECKED_CAST")
        get() = (identity as Any).javaClass as Class<T>
}


/**
 * A GroupCalculator is a calculator specialized for group.
 * @author liyicheng
 * 2018-02-27 17:41
 */
interface GroupCalculator<T> : MonoidCalculator<T> {
    /**
     * Returns the inverse of the element x.
     * @param x an element
     */
    fun inverse(x: T): T

    /**
     * Returns the result of `x-y`, which is equal to `x+inverse(y)`.
     * @return `x-y`
     */
    @JvmDefault
    fun applyInv(x: T, y: T): T {
        return apply(x, inverse(y))
    }

    @JvmDefault
    override fun gpow(x: T, n: Long): T {
        if (n == 0L) {
            return identity
        }
        val t: T = super<MonoidCalculator>.gpow(x, n)
        return if (n > 0) {
            t
        } else {
            inverse(t)
        }
    }


}

/**
 * Returns -x+a+x.
 */
fun <T> GroupCalculator<T>.conjugateBy(a: T, x: T) = eval { apply(apply(inverse(x), a), x) }

/**
 * Returns the commutator of [a] and [b]: `[a,b]` = `a^-1*b^-1*a*b`
 */
fun <T> GroupCalculator<T>.commutator(a: T, b: T) = eval {
    apply(apply(apply(inverse(a), inverse(b)), a), b)
}

fun <T> AbelGroupCal<T>.asGroupCal(): GroupCalculator<T> {
    val cal = this
    return object : GroupCalculator<T> {
        override fun isEqual(x: T, y: T): Boolean {
            return cal.isEqual(x, y)
        }

        override fun apply(x: T, y: T): T {
            return cal.add(x, y)
        }

        override val identity: T
            get() = cal.zero

        override fun inverse(x: T): T {
            return cal.negate(x)
        }

        override fun applyInv(x: T, y: T): T {
            return cal.subtract(x, y)
        }

        override val isCommutative: Boolean
            get() = true
        override val numberClass: Class<T>
            get() = cal.numberClass

        override fun gpow(x: T, n: Long): T {
            return cal.multiplyLong(x, n)
        }
    }
}

fun <T> GroupCalculator<T>.asAbelGroupCal(): AbelGroupCal<T> {
    require(this.isCommutative)
    val cal = this
    return object : AbelGroupCal<T> {
        override fun isEqual(x: T, y: T): Boolean {
            return cal.isEqual(x, y)
        }


        override val numberClass: Class<T>
            get() = cal.numberClass

        override fun add(x: T, y: T): T {
            return apply(x, y)
        }

        override val zero: T
            get() = identity

        override fun negate(x: T): T {
            return inverse(x)
        }

        override fun multiplyLong(x: T, n: Long): T {
            return gpow(x, n)
        }

        override fun subtract(x: T, y: T): T {
            return applyInv(x, y)
        }
    }
}