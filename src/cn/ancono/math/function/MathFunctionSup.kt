/**
 * 2017-10-06
 */
package cn.ancono.math.function

import cn.ancono.math.MathCalculator
import cn.ancono.math.function.AbstractSVPFunction.ConstantFunction
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
    fun <T : Any> getConstant(c: T, mc: MathCalculator<T>): ConstantFunction<T> {
        return ConstantFunction(mc, c)
    }

    /**
     * Returns a constant function: f(x) = c
     * @param c
     * @return
     */
    fun <P : Any, R : Any> getConstant(c: R): MathFunction<P, R> {
        return MathFunction { c }
    }

    fun <T : Any, R1 : Any, R2 : Any, R : Any> mergeOf(f: MathFunction<T, R1>, g: MathFunction<T, R2>, add: (R1, R2) -> R): MathFunction<T, R> {
        return MergeOf2(f, g, add)
    }

    fun <T : Any, R1 : Any, R2 : Any, R : Any> formalAdd(f: MathFunction<T, R1>, g: MathFunction<T, R2>, add: (R1, R2) -> R): MathFunction<T, R> {
        return MergeOf2(f, g, add)
    }

    fun <T : Any, R1 : Any, R2 : Any, R : Any> formalMultiply(f: MathFunction<T, R1>, g: MathFunction<T, R2>, multiply: (R1, R2) -> R): MathFunction<T, R> {
        return MergeOf2(f, g, multiply)
    }
}

internal open class MergeOf2<T : Any, R1 : Any, R2 : Any, R : Any>(fx: MathFunction<T, out R1>, gx: MathFunction<T, out R2>,
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


