/**
 * 2017-10-06
 */
package cn.ancono.math.function

import cn.ancono.math.function.AbstractSVPFunction.ConstantFunction
import cn.ancono.math.numberModels.api.RealCalculator
import cn.ancono.math.set.MathSet
import cn.ancono.math.set.MathSets

/**
 * @author liyicheng
 * 2017-10-06 10:20
 */
object MathFunctionSup {

    /**
     * Returns a constant function: f(x)= c
     * @param c
     * @param mc
     * @return
     */
    fun <T> getConstant(c: T, mc: RealCalculator<T>): ConstantFunction<T> {
        return ConstantFunction(mc, c)
    }

    /**
     * Returns a constant function: f(x) = c
     * @param c
     * @return
     */
    fun <P, R> getConstant(c: R): MathFunction<P, R> {
        return MathFunction { c }
    }

    fun <T, R1, R2, R> mergeOf(f: MathFunction<T, R1>, g: MathFunction<T, R2>, add: (R1, R2) -> R): MathFunction<T, R> {
        return MergeOf2(f, g, add)
    }

    fun <T, R1, R2, R> formalAdd(f: MathFunction<T, R1>, g: MathFunction<T, R2>, add: (R1, R2) -> R): MathFunction<T, R> {
        return MergeOf2(f, g, add)
    }

    fun <T, R1, R2, R> formalMultiply(f: MathFunction<T, R1>, g: MathFunction<T, R2>, multiply: (R1, R2) -> R): MathFunction<T, R> {
        return MergeOf2(f, g, multiply)
    }
}

internal open class MergeOf2<T, R1, R2, R>(fx: MathFunction<T, out R1>, gx: MathFunction<T, out R2>,
                                           val merger: (R1, R2) -> R)
    : MathFunction<T, R> {

    open val f = fx
    open val g = gx

    private val newDomain = MathSets.intersectOf(fx.domain(), gx.domain())
    override fun apply(x: T): R {
        return merger(f(x), g(x))
    }

    override fun domain(): MathSet<T> {
        return newDomain
    }
}


