/**
 * 2018-03-05
 */
package cn.ancono.math

import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import cn.ancono.math.numberModels.api.NumberFormatter
import java.util.*

/**
 * Describes a flexible math object which holds a subclass of EqualPredicate as it 'calculator'. Almost
 * all classes in this module is a subclass of it.
 * @author liyicheng
 * at 2018-03-05 20:19
 */
abstract class FlexibleMathObject<T : Any, S : EqualPredicate<T>>
(mc: S) : CalculatorHolder<T, S> {

    protected val mc: S = Objects.requireNonNull(mc, "Calculator must not be null!")

    override val mathCalculator: S
        get() = mc


    /**
     * The equals method describes the equivalence in program of two math objects instead of the equal in math.
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
     * Returns a String representing this object, the [NumberFormatter] should
     * be used whenever a number is presented.
     * @param nf a number formatter
     * @return
     * @see FlexibleNumberFormatter
     */
    abstract fun toString(nf: FlexibleNumberFormatter<T, S>): String

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
