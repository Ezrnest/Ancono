/**
 * 2018-03-05
 */
package cn.ancono.math

import cn.ancono.math.algebra.abstractAlgebra.calculator.EqualPredicate

/**
 * @author liyicheng
 * 2018-03-05 20:25
 */
interface CalculatorHolder<T : Any, S : EqualPredicate<T>> {

    /**
     * Return the calculator this object is using.
     *
     * @return a calculator
     */
    val mathCalculator: S

}
