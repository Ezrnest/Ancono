package cn.ancono.math.algebra.abs.calculator

import cn.ancono.math.algebra.linear.MatrixImpl.inverse
import cn.ancono.math.function.MathBinaryOperator
import cn.ancono.utilities.ModelPatterns
import kotlin.math.abs


inline fun <T, C : EqualPredicate<T>, R> C.eval(block: C.() -> R): R = this.run(block)


/**
 * A semigroup calculator defines an associative operation [apply].
 *
 * The elements form a semi-group with respect to this calculator.
 *
 *
 * @author liyicheng
 * 2018-02-27 17:31
 *
 * @see AbelSemigroupCal
 * @see MulSemigroupCal
 */
interface SemigroupCalculator<T> : EqualPredicate<T>, MathBinaryOperator<T> {

    /**
     * Applies the operation defined in the semigroup.
     *
     * The operation is associative:
     * > apply(x, apply(y, z)) = apply(apply(x, y), z)
     */
    override fun apply(x: T, y: T): T

    /**
     * Determines whether the operation is commutative. It is `false` by default.
     */
    val isCommutative: Boolean
        get() = false

    /**
     * Returns the result of applying the operation to [x] for [n] times.
     *
     * Formally we have
     *
     *    gpow(x, 1) = x
     *    gpow(x, n+1) = apply(x, gpow(x, n))
     *
     *
     * @param x an element
     * @param n a positive integer
     */
    fun gpow(x: T, n: Long): T {
        return ModelPatterns.binaryProduce(n, x) { a: T, b: T -> this.apply(a, b) }
    }
}

/**
 * A monoid calculator provides the [identity] element for a semigroup calculator.
 *
 * The elements form a monoid with respect to this calculator.
 *
 * @author liyicheng
 * 2018-02-27 17:40
 */
interface MonoidCalculator<T> : SemigroupCalculator<T> {

/*
 * Created by liyicheng at 2020-03-06 22:14
 */

    /**
     * Returns the identity element of the semigroup.
     *
     * It satisfies that
     * > apply(identity, x) = apply(x, identity) = x
     */
    val identity: T

    /**
     * Returns the result of applying the operation to [x] for [n] times, where [n] is a non-negative integer.
     *
     * Formally we have
     *
     *    gpow(x, 0) = identity
     *    gpow(x, n+1) = apply(x, gpow(x, n))
     *
     *
     * @param x an element
     * @param n a non-negative integer
     */
    override fun gpow(x: T, n: Long): T {
        return if (n == 0L) {
            identity
        } else {
            super.gpow(x, n)
        }
    }

    override val numberClass: Class<T>
        @Suppress("UNCHECKED_CAST")
        get() = (identity as Any).javaClass as Class<T>
}


/**
 * A GroupCalculator defines a binary operation [apply], an [identity] element with respect to the operation and
 * a unary operation [inverse] that satisfies:
 *
 * 1. Associative:
 *
 *     apply(x, apply(y, z)) = apply(apply(x, y), z)
 *
 * 2. Identity:
 *
 *     apply(identity, x) = apply(x, identity) = x
 *
 * 3. Inverse:
 *
 *     apply(x, inverse(x)) = apply(inverse(x), x) = identity
 *
 *
 * @author liyicheng
 * 2018-02-27 17:41
 * @see AbelGroupCal
 * @see MulGroupCal
 */
interface GroupCalculator<T> : MonoidCalculator<T> {
    /**
     * Returns the inverse of the element x.
     *
     *     apply(x, inverse(x)) = apply(inverse(x), x) = identity
     *
     * @param x an element
     */
    fun inverse(x: T): T

    /**
     * Returns the result of `apply(x, inverse(y))`.
     * @return `apply(x, inverse(y))`
     */
    fun applyInv(x: T, y: T): T {
        return apply(x, inverse(y))
    }

    /**
     * Returns the result of applying the operation to [x] for [n] times, where [n] is an integer.
     *
     * It can be formally defined as
     *
     *    gpow(x, 0) = identity
     *    gpow(x, n+1) = apply(x, gpow(x, n))
     *    gpow(x, -n) = gpow(inverse(x), n)
     *
     * And it satisfies the following properties:
     *
     *     gpow(x, 1) = x
     *     gpow(x, 2) = apply(x, x)
     *     gpow(x, n+m) = apply(gpow(x, n), gpow(x,m))
     *     gpow(x, -n) = inverse(gpow(x, n))
     *
     * @param x an element
     * @param n a non-negative integer
     */
    override fun gpow(x: T, n: Long): T {
        if (n == 0L) {
            return identity
        }
        val t: T = ModelPatterns.binaryProduce(abs(n), x, this::apply)
        return if (n > 0) {
            t
        } else {
            inverse(t)
        }
    }


}

/**
 * An abelian semigroup calculator defines an associative and commutative operation [add], which
 * we usually denote as `+`.
 *
 * The elements form a semi-group with respect to this calculator.
 *
 * This interface is generally isomorphic to [SemigroupCalculator] where the operation is commutative,
 * but the method names differ and extra operator functions are provided.
 *
 * @author liyicheng
 * 2021-05-07 18:36
 *
 * @see SemigroupCalculator
 */
interface AbelSemigroupCal<T> : EqualPredicate<T> {
    //Created by lyc at 2021-05-03 22:09

    /**
     * Applies the operation of addition defined in the semigroup.
     *
     * The operation is associative and commutative:
     *
     *     x + (y + z) = (x + y) + z := x + y + z
     *     x + y = y + x
     */
    fun add(x: T, y: T): T

    operator fun T.plus(y: T): T = add(this, y)

    /**
     * Returns the result of adding [x] for [n] times, which we usually denote as `n * x` or simply `nx`.
     *
     * It is defined as:
     *
     *     1 * x = x
     *     (n+1) * x = (n * x) + x
     *
     *
     * @param n a positive integer
     */
    fun multiplyLong(x: T, n: Long): T {
        require(n > 0)
        return ModelPatterns.binaryProduce(n, x, this::add)
    }

    /**
     * Returns the sum of all the elements in the given list.
     *
     * @param ps a non-empty list
     */
    fun sum(ps: List<T>): T {
        require(ps.isNotEmpty())
        return ps.reduce(this::add)
    }

    /**
     * Operator function for [T].
     * @see multiplyLong
     */
    operator fun Long.times(x: T): T = multiplyLong(x, this)

    /**
     * Operator function for [T].
     * @see multiplyLong
     */
    operator fun T.times(n: Long) = multiplyLong(this, n)


}

/**
 * A monoid calculator provides [zero], the identity element, for an abelian semigroup calculator.
 *
 *
 * @author liyicheng 2021-05-07 18:44
 * @see MonoidCalculator
 */
interface AbelMonoidCal<T> : AbelSemigroupCal<T> {

    /**
     * The zero element, which we often denote as `0`.
     *
     * It satisfies that:
     *
     *     0 + x = x + 0 = x
     */
    val zero: T

    /**
     * Determines whether [x] is zero.
     *
     * This method is the same as `isEqual(zero, x)`.
     */
    fun isZero(x: T) = isEqual(zero, x)

    /**
     * Returns the result of adding [x] for [n] times, which we usually denote as `n * x` or simply `nx`.
     *
     * It is defined as:
     *
     *     0 * x = zero
     *     (n+1) * x = (n * x) + x
     *
     *
     * @param n a non-negative integer
     */
    override fun multiplyLong(x: T, n: Long): T {
        if (n == 0L) {
            return zero
        }
        return super.multiplyLong(x, n)
    }


    /**
     * Returns the sum of all the elements in the given list,
     * if the list is empty, then [zero] will be returned.
     *
     * @param ps a list
     */
    override fun sum(ps: List<T>): T {
        if (ps.isEmpty()) {
            return zero
        }
        return super.sum(ps)
    }

    /**
     * Returns the class of the number.
     */
    override val numberClass: Class<T>
        @Suppress("UNCHECKED_CAST")
        get() = (zero as Any).javaClass as Class<T>
}

/**
 * A GroupCalculator defines a binary operation [add] (which we denote as `+`)
 * an [zero] element (`0`) with respect to the operation and
 * a unary operation [negate] (`-`) that satisfies:
 *
 * 1. Associative:
 *
 *     x + (y + z) = (x + y) + z
 *
 * 2. Commutative:
 *
 *     x + y = y + x
 *
 * 3. Identity:
 *
 *     x + 0 = 0 + x = 0
 *
 * 4. Inverse:
 *
 *     x + (- x) = 0
 *
 * We often denote `x + (-y)` as `x - y`, and the corresponding method is [subtract].
 *
 * This interface is generally isomorphic to [GroupCalculator] where the operation is commutative,
 * but the method names differ and extra operator functions are provided.
 *
 * @author liyicheng 2021-05-07 18:45
 * @see GroupCalculator
 */
interface AbelGroupCal<T> : AbelMonoidCal<T> {

    /**
     * Returns `-x`, the inverse with respect to addition of [x].
     */
    fun negate(x: T): T

    /**
     * Returns `x - y`, which is defined as `x + (-y)`.
     */
    fun subtract(x: T, y: T): T {
        return add(x, negate(y))
    }

    /**
     * Returns the result of adding [x] for [n] times, which we usually denote as `n * x` or simply `nx`.
     *
     * It is defined as:
     *
     *     0 * x = zero
     *     (n+1) * x = (n * x) + x
     *     (-n) * x = - (n * x)
     *
     *
     * @param n an integer
     */
    override fun multiplyLong(x: T, n: Long): T {
        if (n == 0L) {
            return zero
        }
        val t = ModelPatterns.binaryProduce(abs(n), x, this::add)
        return if (n > 0) {
            t
        } else {
            negate(t)
        }
    }


    /**
     * Operator function inverse.
     * @see inverse
     */
    operator fun T.unaryMinus(): T = negate(this)

    /**
     * Operator function subtract.
     * @see subtract
     */
    operator fun T.minus(y: T): T = subtract(this, y)
}

/**
 * An multiplicative semigroup calculator defines an associative operation [multiply], which
 * we usually denote as `*`.
 *
 * The elements form a semi-group with respect to this calculator.
 *
 * This interface is generally isomorphic to [SemigroupCalculator],
 * but the method names differ and extra operator functions are provided.
 *
 * @author liyicheng
 * 2021-05-07 19:02
 *
 * @see SemigroupCalculator
 */
interface MulSemigroupCal<T> : EqualPredicate<T> {
    /**
     * Applies the operation of multiplication defined in the semigroup.
     */
    fun multiply(x: T, y: T): T

    /**
     * Determines whether the operation is commutative. It is false by default.
     */
    val isCommutative: Boolean
        get() = false

    fun pow(x: T, n: Long): T {
        return ModelPatterns.binaryProduce(n, x, this::multiply)
    }

    fun product(ps: List<T>): T {
        return ps.reduce(this::multiply)
    }


    /**
     * Operator function of add for [T].
     * @see apply
     */
    operator fun T.times(y: T): T = multiply(this, y)


}


/**
 * A monoid calculator provides [one], the identity element, for an multiplicative semigroup calculator.
 *
 *
 * @author liyicheng 2021-05-07 19:02
 * @see MonoidCalculator
 */
interface MulMonoidCal<T> : MulSemigroupCal<T> {
    /*
     * Created by liyicheng at 2020-03-06 22:14
     */


    /**
     * The zero element, which we often denote as `1`.
     *
     * It satisfies that:
     *
     *     1 * x = x * 1 = x
     */
    val one: T

    /**
     * Returns the result of multiplying [x] for [n] times, which we usually denote as `x^n`.
     *
     * It is defined as:
     *
     *     x^0 = 1
     *     x^(n+1) = (x^n) * x
     *
     *
     * @param n a non-negative integer
     */
    override fun pow(x: T, n: Long): T {
        return if (n == 0L) {
            one
        } else {
            super.pow(x, n)
        }
    }

    override fun product(ps: List<T>): T {
        return ps.fold(one, this::multiply)
    }

    /**
     * Returns the class of the number.
     */
    override val numberClass: Class<T>
        @Suppress("UNCHECKED_CAST")
        get() = (one as Any).javaClass as Class<T>
}


/**
 * A GroupCalculator defines a binary operation [multiply] (which we denote as `*`)
 * an [one] element (`1`) with respect to the operation and
 * a unary operation [reciprocal] (`^-1`) that satisfies:
 *
 * 1. Associative:
 *
 *     x * (y * z) = (x * y) * z
 *
 * 2. Commutative:
 *
 *     x * y = y * x
 *
 * 3. Identity:
 *
 *     x * 1 = 1 * x = 1
 *
 * 4. Inverse:
 *
 *     x * (x^-1) = 1
 *
 * We often denote `x * (y^-1)` as `x / y`, and the corresponding method is [divide].
 *
 * This interface is generally isomorphic to [GroupCalculator],
 * but the method names differ and extra operator functions are provided.
 *
 *
 * @author liyicheng 2021-05-07 19:05
 * @see GroupCalculator
 */
interface MulGroupCal<T> : MulMonoidCal<T> {
    /**
     * Returns `x^-1`, the multiplicative inverse of the element [x].
     *
     * Special note: Some implementation of this interface may throw an [ArithmeticException]
     * if there is no inverse for `x`. For example in FieldCalculator when `x = 0`.
     *
     * @param x an element
     */
    fun reciprocal(x: T): T

    /**
     * Returns the result of `x / y := x * y^{-1}`, which is equal to `multiply(x, inverse(y))`.
     * @return `x / y`
     */
    fun divide(x: T, y: T): T {
        return multiply(x, reciprocal(y))
    }


    /**
     * Returns the result of multiplying [x] for [n] times, which we usually denote as `x^n`.
     *
     * It is defined as:
     *
     *     x^0 = 1
     *     x^(n+1) = (x^n) * x
     *     x^(-n) = (x^n)^-1
     *
     * @param n an integer
     */
    override fun pow(x: T, n: Long): T {
        if (n == 0L) {
            return one
        }
        val t = ModelPatterns.binaryProduce(abs(n), x, this::multiply)
        return if (n > 0) {
            t
        } else {
            reciprocal(t)
        }
    }


    /**
     * Operator function of division.
     * @see divide
     */
    operator fun T.div(y: T): T = divide(this, y)

}


/**
 * Returns the conjugation of [a] by [x], which is defined to be
 *
 *      apply(apply(inverse(x), a), x)
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