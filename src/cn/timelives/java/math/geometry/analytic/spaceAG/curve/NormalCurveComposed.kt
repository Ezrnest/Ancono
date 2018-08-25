package cn.timelives.java.math.geometry.analytic.spaceAG.curve

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.function.*
import cn.timelives.java.math.geometry.analytic.spaceAG.SPoint
import cn.timelives.java.math.geometry.analytic.spaceAG.SVector
import cn.timelives.java.math.numberModels.expression.ExprCalculator
import cn.timelives.java.math.numberModels.expression.Expression
import cn.timelives.java.math.numberModels.expression.SimplificationStrategies
import cn.timelives.java.math.set.Interval

class NormalCurveComposed<T : Any>(val a: DerivableSVFunction<T>,
                                   val b: DerivableSVFunction<T>,
                                   val c: DerivableSVFunction<T>,
                                   private val domain: Interval<T>,
                                   mc: MathCalculator<T>) : NormalCurve<T> {
    override val mathCalculator: MathCalculator<T> = mc

    private val derived by lazy {
        DerivableFunction.mergeOf3(a.derive(), b.derive(), c.derive()) { x, y, z ->
            SVector.valueOf(x, y, z, mc)
        }
    }

    //lazy initialization
    override val tangentVector: VectorFunction<T>  by lazy { super.tangentVector }

    override val mainNormalVector: VectorFunction<T> by lazy { super.mainNormalVector }

    override val minorNormalVector: VectorFunction<T> by lazy { super.minorNormalVector }

    override val alpha: VectorFunction<T> by lazy { super.alpha }

    override val beta: VectorFunction<T> by lazy { super.beta }

    override val gamma: VectorFunction<T> by lazy { super.gamma }

    override val curvature: SVFunction<T> by lazy { super.curvature }

    override val ds: SVFunction<T> by lazy { super.ds }


    override fun domain(): Interval<T> = domain

    override fun derive(): DerivableFunction<T, SVector<T>> = derived

    override fun substitute(t: T): SVector<T> {
        return SVector.valueOf(a(t), b(t), c(t), mathCalculator)
    }

    override fun asFunctionX(): DerivableSVFunction<T> = a

    override fun asFunctionY(): DerivableSVFunction<T> = b

    override fun asFunctionZ(): DerivableSVFunction<T> = c

    override fun asPointFunction(): DerivableFunction<T, SPoint<T>> = this.andThenMap { SPoint.valueOf(it) }

    override fun substituteX(t: T): T = a(t)
    override fun substituteY(t: T): T = b(t)
    override fun substituteZ(t: T): T = c(t)

    override fun substituteAsPoint(t: T): SPoint<T> = SPoint.valueOf(a(t), b(t), c(t), mathCalculator)

}

fun main(args: Array<String>) {
    val mc = ExprCalculator.newInstance
    SimplificationStrategies.setCalRegularization(mc)
    val xt = Expression.valueOf("-a*cos(t)").asFunction(mc, "t")
    val yt = Expression.valueOf("a*sin(t)").asFunction(mc, "t")
    val zt = Expression.valueOf("bt").asFunction(mc, "t")
    val t = Expression.valueOf("t")
    val curve = NormalCurveComposed(xt, yt, zt, Interval.universe(mc), mc)
    println(curve.tangentVector(t))
    println(curve.curvature(t))
}