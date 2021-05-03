/**
 *
 */
package cn.ancono.math

/**
 *
 * Describes a holder of [MathCalculator], which uses the MathCalculator to do operations.
 * @author liyicheng
 */
interface MathCalculatorHolder<T> : CalculatorHolder<T, MathCalculator<T>> {

    override val mathCalculator: MathCalculator<T>

}
