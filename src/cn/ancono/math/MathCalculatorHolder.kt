/**
 *
 */
package cn.ancono.math

import cn.ancono.math.numberModels.api.RealCalculator

/**
 *
 * Describes a holder of [RealCalculator], which uses the MathCalculator to do operations.
 * @author liyicheng
 */
interface MathCalculatorHolder<T> : CalculatorHolder<T, RealCalculator<T>> {

    override val calculator: RealCalculator<T>

}
