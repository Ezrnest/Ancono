package cn.ancono.math.geometry.analytic.spaceAG.curve

import cn.ancono.math.MathCalculator
import cn.ancono.math.function.*
import cn.ancono.math.geometry.analytic.spaceAG.SPoint
import cn.ancono.math.geometry.analytic.spaceAG.SVector
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.Expression
import cn.ancono.math.numberModels.expression.SimplificationStrategies
import cn.ancono.math.set.Interval

class NormalCurveComposed<T : Any>(val a: DerivableSVFunction<T>,
                                   val b: DerivableSVFunction<T>,
                                   val c: DerivableSVFunction<T>,
                                   private val domain: Interval<T>,
                                   mc: MathCalculator<T>) : NormalCurve<T>(mc) {


    override val derivative: DerivableFunction<T, SVector<T>> by lazy {
        DerivableFunction.mergeOf3(a.derive(), b.derive(), c.derive()) { x, y, z ->
            SVector.valueOf(x, y, z, mc)
        }
    }


    override fun domain(): Interval<T> = domain

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

fun main() {
    SimplificationStrategies.setEnableSpi(true)
    val mc = ExprCalculator.instance
    val xt = Expression.valueOf("-a*cos(t)").asFunction(mc, "t")
    val yt = Expression.valueOf("a*sin(t)").asFunction(mc, "t")
    val zt = Expression.valueOf("bt").asFunction(mc, "t")
    val t = Expression.valueOf("t")
    val r = NormalCurveComposed(xt, yt, zt, Interval.universe(mc), mc)
//    println(curve.tangentVector(t))
//    println(curve.curvature(t))
//    println(curve.tangentVector(t))
    val r1 = r.derivative
    val r2 = r1.derivative
    println(r1(t))
    println(r2(t))
    println()

    println(mc.simplify(r.curvature(t)))
}