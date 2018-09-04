package cn.timelives.java.math

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.FieldCalculator
import cn.timelives.java.math.exceptions.UnsupportedCalculationException
import cn.timelives.java.math.function.Bijection
import cn.timelives.java.math.function.invoke

import java.util.Comparator

/**
 * Describe a calculator that can calculator the basic operations for
 * number, this interface is create to give some math-based objects full
 * flexibility to all kind of numbers.
 *
 *
 * All methods in a math calculator should be consistent.No change should be
 * done to the number when any method is called.
 *
 *
 * All methods in this calculator may not be operational because of the limit of
 * number's format and so other reasons,so if necessary, an
 * [UnsupportedCalculationException] can be thrown.For some special
 * operations, exceptional arithmetic condition may occur, so an
 * [ArithmeticException] may be thrown.
 *
 *
 * It is highly recommended that you should only create one instance of the math
 * calculator and pass it all through the calculation. This can keep the
 * calculation result from being different and in some FlexibleMathObject,such
 * as Triangle,may contain other FlexibleMathObject,and some calculation is not
 * strongly made sure that only the calculator from the Triangle itself is
 * used(which means the calculator in Point may be used), so there may be
 * potential safety problems. Therefore, in a multiple-number-type task, you
 * should always be careful with the math calculator.
 *
 *
 * A MathCalculator naturally deals with numbers, so it is a subclass of [FieldCalculator].
 * However, it is not strictly required all the operations(addition, multiplication..)
 * must return a number and throwing exceptions is acceptable.
 *
 * @param <T> the type of number to deal with
 * @author lyc
</T> */
interface MathCalculator<T : Any> : FieldCalculator<T>, Comparator<T> {

    /**
     * Determines whether this calculator supports `compare()` method.
     *
     * @return `true` if compare method is available.
     */
    val isComparable: Boolean

    /**
     * Return the value zero in this kind of number type.The returned number should
     * be equal to `this.subtract(t,t)`.
     *
     * @return 0
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     */
    override val zero: T

    /**
     * Return the value one in this kind of number type. The returned number should
     * be equal to `this.divide(t,t)`.
     *
     * @return 1
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     */
    override val one: T

    /**
     * Returns the class object of the number type operated by this MathCalculator.
     *
     * @return the class
     */
    val numberClass: Class<*>

    /**
     * Compare the two numbers and determines whether these two numbers are the
     * identity.
     *
     * ** For any calculator, this method should be implemented.**
     *
     * @param x a number
     * @param y another number
     * @return `true` if `para1 == para2`,otherwise `false`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     */
    override fun isEqual(x: T, y: T): Boolean

    /**
     * Compare the two numbers, return -1 if `para1 < para2 `, 0 if
     * `para1==para2` , or 1 if `para1 > para2`.This method is
     * recommended to be literally the identity to the method `compareTo()` if the
     * object `T` is comparable.
     *
     * @param x a number
     * @param y another number
     * @return -1 if `para1 < para2 `, 0 if `para1==para2` , or 1 if
     * `para1 > para2`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     */
    override fun compare(x: T, y: T): Int

    /**
     * Add two parameters, this method is required to be commutative, so is it
     * required that `add(t1,t2)=add(t2,t1)`.
     *
     * @param x a number
     * @param y another number
     * @return `para1 + para2`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    override fun add(x: T, y: T): T

    /**
     * Add the parameters,this method is equal to:
     *
     * <pre>
     * T sum = getZero();
     * for (Object t : ps) {
     * sum = add(sum, (T) t);
     * }
     * return sum;
    </pre> *
     *
     *
     * The Object-type input array is to fit genetic types.
     *
     * @param ps an array of numbers to add
     * @return the sum of `ps`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun addX(vararg ps: Any): T {
        var sum = zero
        for (t in ps) {
            @Suppress("UNCHECKED_CAST")
            sum = add(sum, t as T)
        }
        return sum
    }

    /**
     * Returns the negate of this number.
     *
     * @param x a number
     * @return `-para`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    override fun negate(x: T): T

    /**
     * Returns the absolute value of this number.
     *
     * @param para a number
     * @return `|para|`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun abs(para: T): T

    /**
     * Returns the result of `para1-para2`,this method should return the identity
     * result with `add(para1,this.negate(para2))`.
     *
     * @param x a number
     * @param y another number
     * @return `para1-para2`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    override fun subtract(x: T, y: T): T

    /**
     * Determines whether the given parameter is zero.This method is set because the
     * high frequency of testing whether the number is zero in most math
     * calculations.
     *
     * @return `true` if `para==zero`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     */
    fun isZero(para: T): Boolean {
        return isEqual(zero, para)
    }

    /**
     * Returns the result of `para1 * para2`.
     *
     * @param x a number
     * @param y another number
     * @return `para1 * para2`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    override fun multiply(x: T, y: T): T

    /**
     * Multiply the parameters,this method is equal to:
     *
     * <pre>
     * T re = getOne();
     * for (T t : ps) {
     * re = multiply(re, t);
     * }
     * return re;
    </pre> *
     *
     * @param ps an array of numbers to multiply
     * @return the result
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun multiplyX(vararg ps: Any): T {
        var re = one
        for (t in ps) {
            @Suppress("UNCHECKED_CAST")
            re = multiply(re, t as T)
        }
        return re
    }

    /**
     * Returns the result of `para1 / para2`.
     *
     * @param x a number as dividend
     * @param y another number as divisor
     * @return `para1 / para2`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    override fun divide(x: T, y: T): T

    /**
     * Return the value of `1/p`. This method should be equal to
     * `this.divide(this.getOne,p)`.
     *
     * @param x a number
     * @return `1/p`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    override fun reciprocal(x: T): T

    /**
     * Return the result of `l*p`, this method is provided because this is
     * equals to `add(p,p)` for `l` times. This method expects a better
     * performance.
     *
     * @param x a number
     * @param n another number of long
     * @return `p*l`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    @Suppress("RedundantOverride")//override to reduce unnecessary override of subclasses written in java.
    override fun multiplyLong(x: T, n: Long): T {
        return super.multiplyLong(x, n)
    }

    /**
     * Return the result of `p / n` , throws exception if necessary.
     *
     * @param x a number as dividend
     * @param n another number of long as divisor
     * @return `p / n`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    override fun divideLong(x: T, n: Long): T

    /**
     * Return the square root of `x`. This method should return the positive
     * square of `x`.
     *
     * @param x a number
     * @return `x ^ 0.5`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun squareRoot(x: T): T

    /**
     * Return the n-th root of `x`. This method should return a positive
     * number if `n` is even.
     *
     * @param x a number
     * @return `x ^ (1/n)`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun nroot(x: T, n: Long): T

    /**
     * Return `p ^ exp`.This method should be equal to calling
     * `this.multiply(p,p)` for many times if `exp > 0` , or
     * `this.divide(p,p)` if `exp < 0 `, or return `getOne()` if
     * `exp == 0`.Notice that this calculator may not throw an
     * ArithmeticException if `p == 0 && exp <= 0`,whether to throw exception
     * is determined by the implementation.
     *
     * @param x   a number
     * @param n the exponent
     * @return `p ^ exp`.
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    override fun pow(x: T, n: Long): T

    /**
     * Gets a constant value from the calculator, the constant value is got by its
     * name as a String. It is recommended that the string should be case
     * insensitive in case of spelling mistakes. The name of the constant value should be
     * specified wherever the value is needed. <br></br>
     * Some common constants are list below:
     *
     *  * <tt>Pi</tt> :the ratio of the circumference of a circle to its
     * diameter.See:[Math.PI]
     *  * <tt>e</tt> :the base of the natural logarithms.See:[Math.E]
     *  * <tt>i</tt> :the square root of `-1`.
     *
     *
     * @param name the name of the constant value,case insensitive
     * @return a number that represents the constant value.
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     */
    fun constantValue(name: String): T?

    /**
     * Returns the result of `a^b`. <br></br>
     * This method provides a default implement by computing:
     * `exp(multiply(ln(a), b))`.
     *
     * @param a a number
     * @param b the exponent
     * @return `a^b`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun exp(a: T, b: T): T {
        return exp(multiply(ln(a), b))
    }

    /**
     * Returns the result of `e^x`, where `e` is the base of the natural
     * logarithm.
     *
     * @param x the exponent
     * @return `e^x`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun exp(x: T): T

    /**
     * Returns result of
     *
     * <pre>
     * log<sub>a</sub>b
    </pre> *
     *
     * <br></br>
     * This method provides a default implement by computing:
     * `divide(ln(b),ln(a))`.
     *
     * @param a a number
     * @param b another number
     * @return <pre>
     * log<sub>a</sub>b
    </pre> *
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun log(a: T, b: T): T {
        return divide(ln(b), ln(a))
    }

    /**
     * Returns result of
     *
     * <pre>
     * ln(x)
    </pre> *
     *
     *
     * or the natural logarithm (base e).
     *
     * @param x a number
     * @return <pre>
     * ln(x)
    </pre> *
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun ln(x: T): T

    /**
     * Returns the result of `sin(x)`
     *
     * @param x a number
     * @return `sin(x)`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun sin(x: T): T

    /**
     * Returns the result of `cos(x)`. <br></br>
     * This method provides a default implement by computing:
     * `squareRoot(subtract(getOne(), multiply(x, x)))`. If a better implement
     * is available, subclasses should always override this method.
     *
     * @param x a number
     * @return `cos(x)`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun cos(x: T): T {
        return squareRoot(subtract(one, multiply(x, x)))
    }

    /**
     * Returns the result of `tan(x)`. <br></br>
     * This method provides a default implement by computing:
     * `divide(sin(x),cos(x))`. If a better implement is available,
     * subclasses should always override this method.
     *
     * @param x a number
     * @return `tan(x)`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun tan(x: T): T {
        return divide(sin(x), cos(x))
    }

    /**
     * Returns the result of `arcsin(x)`.
     *
     * @param x a number
     * @return `arcsin(x)`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun arcsin(x: T): T

    /**
     * Returns the result of `arccos(x)`. <br></br>
     * This method provides a default implement by computing:
     * `subtract(divideLong(constantValue(STR_PI), 2l), arcsin(x))`. If a
     * better implement is available, subclasses should always override this method.
     *
     * @param x a number
     * @return `arccos(x)`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun arccos(x: T): T {
        return subtract(divideLong(constantValue(STR_PI)!!, 2L), arcsin(x))
    }

    /**
     * Returns the result of `arctan(x)`. <br></br>
     * This method provides a default implement by computing:
     * `arcsin(divide(x,squareRoot(add(getOne(), multiply(x, x)))))`. If a
     * better implement is available, subclasses should always override this method.
     *
     * @param x a number
     * @return `arctan(x)`
     * @throws UnsupportedCalculationException if this operation can not be done.(optional)
     * @throws ArithmeticException             if this operation causes an exceptional arithmetic condition.
     */
    fun arctan(x: T): T {
        return arcsin(divide(x, squareRoot(add(one, multiply(x, x)))))
    }


    companion object {

        /**
         * The string representation of pi.
         */
        const val STR_PI = "Pi"
        /**
         * The string representation of e.
         */
        const val STR_E = "e"
        /**
         * The string representation of i, the square root of -1.
         * This constant value may not be available.
         */
        const val STR_I = "i"

        /**
         * Returns a [MathCalculator] based on the given [mc] and the [Bijection] [f]. It is assured that [mappedCalculator(mc,Bijection.identity()]
         * returns the identity value of all methods as [mc].
         */
        @Suppress("unused")
        fun <T : Any, S : Any> mappedCalculator(mc: MathCalculator<T>, f: Bijection<T, S>): MathCalculator<S> {
            return object : MathCalculator<S> {
                override val isComparable: Boolean = mc.isComparable
                override val zero: S = f(mc.zero)
                override val one: S = f(mc.one)
                override val numberClass: Class<*> = zero::class.java

                override fun isEqual(x: S, y: S): Boolean = mc.isEqual(f.deply(x), f.deply(y))

                override fun compare(para1: S, para2: S): Int {
                    return mc.compare(f.deply(para1), f.deply(para2))
                }

                override fun add(x: S, y: S): S {
                    return f(mc.add(f.deply(x), f.deply(y)))
                }

                override fun negate(x: S): S {
                    return f(mc.negate(f.deply(x)))
                }

                override fun abs(para: S): S {
                    return f(mc.abs(f.deply(para)))
                }

                override fun subtract(x: S, y: S): S = f(mc.subtract(f.deply(x), f.deply(y)))

                override fun multiply(x: S, y: S): S {
                    return f(mc.multiply(f.deply(x), f.deply(y)))
                }

                override fun divide(x: S, y: S): S {
                    return f(mc.divide(f.deply(x), f.deply(y)))
                }

                override fun reciprocal(x: S): S {
                    return f(mc.reciprocal(f.deply(x)))
                }

                override fun multiplyLong(x: S, n: Long): S {
                    return f(mc.multiplyLong(f.deply(x), n))
                }

                override fun divideLong(x: S, n: Long): S {
                    return f(mc.divideLong(f.deply(x), n))
                }

                override fun squareRoot(x: S): S {
                    return f(mc.squareRoot(f.deply(x)))
                }

                override fun nroot(x: S, n: Long): S {
                    return f(mc.nroot(f.deply(x), n))
                }

                override fun pow(x: S, n: Long): S {
                    return f(mc.pow(f.deply(x), n))
                }

                override fun constantValue(name: String): S? {
                    val t = mc.constantValue(name)
                    return if (t == null) {
                        null
                    } else {
                        f(t)
                    }
                }

                override fun exp(x: S): S {
                    return f(mc.exp(f.deply(x)))
                }

                override fun ln(x: S): S {
                    return f(mc.ln(f.deply(x)))
                }

                override fun sin(x: S): S {
                    return f(mc.sin(f.deply(x)))
                }

                override fun arcsin(x: S): S {
                    return f(mc.arcsin(f.deply(x)))
                }

                override fun isZero(para: S): Boolean {
                    return mc.isZero(f.deply(para))
                }

                override fun exp(a: S, b: S): S {
                    return f(mc.exp(f.deply(a), f.deply(b)))
                }

                override fun log(a: S, b: S): S {
                    return f(mc.log(f.deply(a), f.deply(b)))
                }

                override fun cos(x: S): S {
                    return f(mc.cos(f.deply(x)))
                }

                override fun tan(x: S): S {
                    return f(mc.tan(f.deply(x)))
                }

                override fun arccos(x: S): S {
                    return f(mc.arccos(f.deply(x)))
                }

                override fun arctan(x: S): S {
                    return f(mc.arctan(f.deply(x)))
                }
            }
        }
    }

}
