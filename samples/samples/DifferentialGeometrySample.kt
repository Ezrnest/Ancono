package samples

import cn.ancono.math.calculus.Calculus.derivation
import cn.ancono.math.function.asFunction
import cn.ancono.math.function.asNFunction
import cn.ancono.math.function.invoke
import cn.ancono.math.geometry.analytic.space.SVector
import cn.ancono.math.geometry.differential.NormalCurve
import cn.ancono.math.geometry.differential.NormalCurveComposed
import cn.ancono.math.geometry.differential.NormalSurface
import cn.ancono.math.minus
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.Expression
import cn.ancono.math.numberModels.expression.SimplificationStrategies
import cn.ancono.math.plus
import cn.ancono.math.set.Interval

object DifferentialGeometrySample {
    //    init{
//        SimplificationStrategies.setEnableSpi(true)
//    }
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
        println("r1:")
        println(r1.curvature(t))
        println(r1.torsion(t))
        println("r2:")
        println(r2.curvature(t))
        println(r2.torsion(t))
    }

    fun proof1() {
        val at = getVector(mc, 0)
        val bt = getVector(mc, 1)
        val innerProduct = at.innerProduct(bt)
        val left = innerProduct.derivation()
        val right = mc.add(at.derivation().innerProduct(bt), at.innerProduct(bt.derivation()))
        println("left - right is ${mc.subtract(left, right)}")
    }

    fun proof2() {
        val at = getVector(mc, 0)
        val bt = getVector(mc, 1)
        val left = at.outerProduct(bt).derivation()
        val right = at.derivation().outerProduct(bt) + at.outerProduct(bt.derivation())
        println("left - right is ${(left - right)}")
    }

    fun proof3() {
        val at = getVector(mc, 0)
        val bt = getVector(mc, 1)
        val ct = getVector(mc, 2)

        val left = SVector.mixedProduct(at, bt, ct).derivation()
        val right = mc.addX(SVector.mixedProduct(at.derivation(), bt, ct),
                SVector.mixedProduct(at, bt.derivation(), ct),
                SVector.mixedProduct(at, bt, ct.derivation()))
        println("left - right is ${mc.subtract(left, right)}")
    }

    fun getVector(mc: ExprCalculator, num: Int = 0): SVector<Expression> = SVector.valueOf(Expression.valueOf("f${num}_(x)"),
            Expression.valueOf("g${num}_(x)"), Expression.valueOf("h${num}_(x)"), mc)

    fun SVector<Expression>.derivation(): SVector<Expression> = this.applyFunction { it.derivation() }


    fun makeSurface(expr: String): NormalSurface<Expression> {
        val (xt, yt, zt) = expr.split(",").map { it.trim() }
        val x = mc.parse(xt).asNFunction("u", "v")
        val y = mc.parse(yt).asNFunction("u", "v")
        val z = mc.parse(zt).asNFunction("u", "v")
        return NormalSurface.fromFunctionXYZ(x, y, z, mc)
    }

    fun calculateFundForm() {
        val expr = "a*cos(u)cos(v),a*cos(u)sin(v),a*sin(u)"
//        ExprCalculator.

        val r = makeSurface(expr)
        val u = mc.parse("u")
        val v = mc.parse("v")
        println(r.E(u, v))
        println(r.F(u, v))
        println(r.G(u, v))
    }
}

fun main() {
    DifferentialGeometrySample.calculateFundForm()
}