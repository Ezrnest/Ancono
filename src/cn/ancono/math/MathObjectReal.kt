package cn.ancono.math

import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.numberModels.api.NumberFormatter
import cn.ancono.math.numberModels.api.RealCalculator

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
 * @see RealCalculator
 */
interface MathObjectReal<T>
    : MathObject<T, RealCalculator<T>> {
    /**
     * Gets the `MathCalculator` kept by this math object.
     */
    override val calculator: RealCalculator<T>

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
     * Returns a String representing this object, the [NumberFormatter] should
     * be used whenever a number is presented.
     * @param nf a number formatter
     */
    override fun toString(nf: NumberFormatter<T>): String

    /**
     * Returns a String representing this object, it is recommended that
     * the output of the number model should be formatted
     * through [NumberFormatter.format].
     */
    override fun toString(): String
}

abstract class AbstractMathObjectReal<T>(protected val mc: RealCalculator<T>) : MathObjectReal<T> {
//    override fun <N> valueEquals(obj: MathObjectReal<N>, mapper: Function<N, T>): Boolean {
//        return valueEquals(obj.mapTo(calculator, mapper))
//    }

    override val calculator: RealCalculator<T>
        get() = mc

    override fun toString(): String {
        return toString(NumberFormatter.defaultFormatter())
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }
}

abstract class AbstractMathObject<T, S : EqualPredicate<T>>(override val calculator: S) : MathObject<T, S> {


    override fun toString(): String {
        return toString(NumberFormatter.defaultFormatter())
    }


}

//object MathObjects{
//    fun <T1:Any,S1:MathObject<T1>, T2 :Any, S2 : MathObject<T2>> mapTo2(obj : MathObject<S1>, )
//}