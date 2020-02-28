package cn.timelives.java.math

import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter
import cn.timelives.java.math.numberModels.api.NumberFormatter

import java.util.function.Function

/**
 * Describes an object that is used in math and is flexible for all type of number models which are used in math.
 * The corresponding math calculator should be given when such an object is created and the calculator may
 * be used. For example, an interval may extend this abstract class and the type of bound can be switched from
 * Integer to Double , or other kind of math number.
 * @author lyc
 * @param T the kind of object used, usually a subclass of number
 * @see MathCalculator
 */
abstract class MathObject<T : Any>
/**
 * Create a flexible math object with the given MathCalculator, the MathCalculator should not
 * be null.
 * @param mc
 */
protected constructor(mc: MathCalculator<T>) : FlexibleMathObject<T, MathCalculator<T>>(mc),MathCalculatorHolder<T> {
    override val mathCalculator: MathCalculator<T>
        get() = super.mathCalculator
    /**
     * Map this object using the number type `T` to a new object using the number type `N`. This
     * method is a core method of [MathObject]. The subclasses can always changes the return
     * type to it instead of just returning a FlexibleMathObject.
     * @param newCalculator a new calculator of type `N`
     * @param mapper the function used in mapping.
     * @param <N> the new number type.
     * @return a new MathObject of type N
    </N> */
    abstract fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): MathObject<N>

    /**
     * The equals method describes the equivalence in program of two math objects instead of the equal in math.
     * However
     * If the type of number is different, then `false` will be returned.
     */
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    /**
     * A good `hashCode` method is recommended for every subclass extends the FlexibleMathObject, and
     * this method should be implemented whenever `equals()` is implemented.
     */
    override fun hashCode(): Int {
        return super.hashCode()
    }


    /**
     * Determines whether the two objects using the identity number type is the identity. In this method,
     * [MathCalculator.isEqual] is used instead of `Object.equals()` method.
     * This method is basically equal to [.valueEquals] as
     * `this.valueEquals(obj,x -> x)`
     *
     * @param obj another FlexibleMathObject
     * @return `true` if this is equal to obj , else `false`.
     * @throws ClassCastException if `obj` is not using number type `T`
     */
    abstract fun valueEquals(obj: MathObject<T>): Boolean


    /**
     * Determines whether the two objects are the identity according to the given mapper and the calculator.This
     * method is based on math definition so this method should not simply use `equal()` method , instead,
     * [MathCalculator.isEqual] should be used when comparing two numbers. This method
     * provides the equality in math.
     * @param obj another object, type is the identity as this
     * @param mapper a function
     * @param <N> another type of number
     * @return `true` if this is equal to obj , else `false`.
     * @throws ClassCastException if `obj` is not using number type `N`
    </N> */
    open fun <N : Any> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean {
        return valueEquals(obj.mapTo(mapper, mc))
    }

    /**
     * Returns a String representing this object, the [NumberFormatter] should
     * be used whenever a number is presented.
     * @param nf
     * @return
     */
    abstract override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String

    /**
     * Returns a String representing this object, it is recommended that
     * the output of the number model should be formatted
     * through [NumberFormatter.format].
     * @return
     */
    override fun toString(): String {
        return toString(FlexibleNumberFormatter.getToStringFormatter())
    }
}

//object MathObjects{
//    fun <T1:Any,S1:MathObject<T1>, T2 :Any, S2 : MathObject<T2>> mapTo2(obj : MathObject<S1>, )
//}