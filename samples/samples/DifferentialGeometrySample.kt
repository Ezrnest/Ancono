package samples

import cn.ancono.math.function.asFunction
import cn.ancono.math.function.invoke
import cn.ancono.math.geometry.analytic.spaceAG.curve.NormalCurve
import cn.ancono.math.geometry.analytic.spaceAG.curve.NormalCurveComposed
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.Expression
import cn.ancono.math.set.Interval

object DifferentialGeometrySample {
    val mc = ExprCalculator.instance
    fun makeCurve(expr: String): NormalCurve<Expression> {
        val (xt, yt, zt) = expr.split(",").map { it.trim() }
        val x = mc.parse(xt).asFunction(mc, "t")
        val y = mc.parse(yt).asFunction(mc, "t")
        val z = mc.parse(zt).asFunction(mc, "t")
        return NormalCurveComposed(x, y, z, Interval.universe(mc), mc)
    }

    fun computeCurvature() {
        val t = mc.parse("t")
        val r1 = makeCurve("t+Sqr3*sin(t), 2cos(t),Sqr3*t-sin(t)")
        val r2 = makeCurve("2cos(t/2),2sin(t/2),-t")
        println(r1.curvature(t))
        println(r1.torsion(t))
        println(r2.curvature(t))
        println(r2.torsion(t))

    }
}

fun main() {
    DifferentialGeometrySample.computeCurvature()
}