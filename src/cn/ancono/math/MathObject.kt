/**
 * 2018-03-05 20:19
 */
package cn.ancono.math

import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.numberModels.api.NumberFormatter
import java.util.function.Function

/**
 * Describes a (computational) math object which holds a subclass of `EqualPredicate` as it 'calculator' for
 * computational purpose.
 *
 *
 *
 *
 * @author liyicheng
 * @see MathObjectReal
 */
interface MathObject<T, S : EqualPredicate<T>> : CalculatorHolder<T, S> {

    /**
     * Gets the calculator of this math object.
     */
    override val calculator: S


    /**
     * Returns a String representing this object, the [NumberFormatter] should
     * be used whenever a number is presented.
     * @param nf a number formatter
     * @return
     * @see NumberFormatter
     */
    fun toString(nf: NumberFormatter<T>): String

//    /**
//     * Returns a String representing this object, it is recommended that
//     * the output of the number model should be formatted
//     * through [NumberFormatter.format].
//     * @return
//     */
//    override fun toString(): String

    fun valueEquals(obj: MathObject<T, S>): Boolean

    /**
     * Maps this math object to use a new calculator.
     *
     * @param newCalculator a calculator that is of the same type as `S` but with generic parameter `N`.
     */
    fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): MathObject<N, *>

}

