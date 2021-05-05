/**
 * 2018-03-05 20:19
 */
package cn.ancono.math

import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import java.util.function.Function

/**
 * Describes a flexible (computational) math object which holds a subclass of EqualPredicate as it 'calculator' for
 * computational purpose.
 *
 *
 *
 *
 * @author liyicheng
 * @see MathObject
 */
interface FlexibleMathObject<T, S : EqualPredicate<T>> : CalculatorHolder<T, S> {


    override val calculator: S


//    /**
//     * The equals method describes the equivalence in program of two math objects instead of the equal in math.
//     * If the type of number is different, then `false` will be returned.
//     */
//    override fun equals(other: Any?): Boolean
//
//    /**
//     * A good `hashCode` method is recommended for every subclass extends the FlexibleMathObject, and
//     * this method should be implemented whenever `equals()` is implemented.
//     */
//    override fun hashCode(): Int

    /**
     * Returns a String representing this object, the [NumberFormatter] should
     * be used whenever a number is presented.
     * @param nf a number formatter
     * @return
     * @see FlexibleNumberFormatter
     */
    fun toString(nf: FlexibleNumberFormatter<T>): String

    /**
     * Returns a String representing this object, it is recommended that
     * the output of the number model should be formatted
     * through [NumberFormatter.format].
     * @return
     */
    override fun toString(): String

}

interface FMathObject<T, S : EqualPredicate<T>> : FlexibleMathObject<T, S> {
    fun valueEquals(obj: FMathObject<T, S>): Boolean

    fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): FMathObject<N, *>
}
