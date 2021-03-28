/**
 * 2018-03-05
 */
package cn.ancono.math

import cn.ancono.math.algebra.abs.calculator.EqualPredicate

/**
 * @author liyicheng
 * 2018-03-05 20:25
 */
interface CalculatorHolder<T : Any, S : EqualPredicate<T>> {

    /**
     * Return the calculator used by this object.
     *
     * @return a calculator
     */
    val mathCalculator: S

}
