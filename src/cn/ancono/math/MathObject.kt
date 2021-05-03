package cn.ancono.math

import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import cn.ancono.math.numberModels.api.NumberFormatter

import java.util.function.Function

/**
 * Describes an object that is used in math and is flexible for all type of number models.
 * The corresponding math calculator should be given when such an object is created and the calculator may
 * be used.
 *
 * Almost all classes in this module are subclasses of it.
 * For example, `Interval` extends this abstract class and the type of it bound can be switched from
 * Integer to Double, or other kinds of numbers.
 * `Matrix` is also a subclass of `MathObject`, the number stored in it can be fraction, real number or others.
 *
 * @author lyc
 * @param T the type of the number model used
 * @see MathCalculator
 */
interface MathObject<T>
    : FlexibleMathObject<T, MathCalculator<T>>, MathCalculatorHolder<T> {
    /**
     * Gets the `MathCalculator` kept by this math object.
     */
    override val mathCalculator: MathCalculator<T>

    /**
     * Map this object using the number type `T` to a new object using the number type `N`. This
     * method is a core method of [MathObject]. The subclasses can always changes the return
     * type to it instead of just returning a FlexibleMathObject.
     * @param newCalculator a new calculator of type `N`
     * @param mapper the function used in mapping.
     * @param <N> the new number type.
     * @return a new MathObject of type N
    </N> */
    fun <N> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): MathObject<N>

    /**
     * The equals method describes the equivalence in program of two math objects instead of the equality in math.
     *
     * If the type of number is different, then `false` will be returned.
     */
    override fun equals(other: Any?): Boolean

    /**
     * A good `hashCode` method is recommended for every subclass extends the FlexibleMathObject, and
     * this method should be implemented whenever `equals()` is implemented.
     */
    override fun hashCode(): Int


    /**
     * Determines whether the two objects using the identity number type is the identity. In this method,
     * [MathCalculator.isEqual] is used instead of `Object.equals()` method.
     *
     * @param obj another FlexibleMathObject
     * @return `true` if this is equal to obj , else `false`.
     * @throws ClassCastException if `obj` is not using number type `T`
     */
    fun valueEquals(obj: MathObject<T>): Boolean


    /**
     * Determines whether the two objects are the identity according to the given mapper and the calculator.This
     * method is based on math definition so this method should not simply use `equal()` method , instead,
     * [MathCalculator.isEqual] should be used when comparing two numbers. This method
     * provides the equality in math.
     *
     * @param obj another object, type is the identity as this
     * @param mapper a function
     * @param N another type of number
     * @return `true` if this is equal to obj , else `false`.
     * @throws ClassCastException if `obj` is not using number type `N`
     */
    fun <N> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean

    /**
     * Returns a String representing this object, the [NumberFormatter] should
     * be used whenever a number is presented.
     * @param nf a number formatter
     */
    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String

    /**
     * Returns a String representing this object, it is recommended that
     * the output of the number model should be formatted
     * through [NumberFormatter.format].
     */
    override fun toString(): String
}

abstract class AbstractMathObject<T>(protected val mc: MathCalculator<T>) : MathObject<T> {
    override fun <N> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean {
        return valueEquals(obj.mapTo(mathCalculator, mapper))
    }

    override val mathCalculator: MathCalculator<T>
        get() = mc

    override fun toString(): String {
        return toString(FlexibleNumberFormatter.defaultFormatter())
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }
}

abstract class AbstractFlexibleMathObject<T, S : EqualPredicate<T>>(override val mathCalculator: S) : FMathObject<T, S> {

    override fun toString(): String {
        return toString(FlexibleNumberFormatter.defaultFormatter())
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }


}

//object MathObjects{
//    fun <T1:Any,S1:MathObject<T1>, T2 :Any, S2 : MathObject<T2>> mapTo2(obj : MathObject<S1>, )
//}