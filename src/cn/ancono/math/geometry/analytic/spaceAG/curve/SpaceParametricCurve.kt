package cn.ancono.math.geometry.analytic.spaceAG.curve

import cn.ancono.math.MathCalculatorHolder
import cn.ancono.math.calculus.Calculus.derivation
import cn.ancono.math.function.MathFunction
import cn.ancono.math.function.SVFunction
import cn.ancono.math.geometry.analytic.spaceAG.SPoint
import cn.ancono.math.geometry.analytic.spaceAG.SVector
import cn.ancono.math.minus
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.Expression
import cn.ancono.math.numberModels.expression.SimplificationStrategies
import cn.ancono.math.plus
import java.util.function.UnaryOperator

typealias VectorFunction<T> = MathFunction<T, SVector<T>>

/**
 * Describes a parametric curve in three-dimensional Euclidean space.
 * Mathematically, a parametric curve is represented by a vector function: `r(t) : (a,b) -> E^3`.
 */
interface SpaceParametricCurve<T : Any> : VectorFunction<T>, MathCalculatorHolder<T> {

    override fun apply(x: T): SVector<T> = substitute(x)


    /**
     * Substitute the given parameter [t] and returns the point.
     */
    fun substitute(t: T): SVector<T>

    fun substituteAsPoint(t: T): SPoint<T> = SPoint.valueOf(substitute(t))

    fun asPointFunction(): MathFunction<T, SPoint<T>> = MathFunction.andThen(this, MathFunction { x: SVector<T> -> SPoint.valueOf(x) })

    fun substituteX(t: T): T = substitute(t).x

    fun substituteY(t: T): T = substitute(t).y

    fun substituteZ(t: T): T = substitute(t).z

    fun asFunctionX(): SVFunction<T> = SVFunction.fromFunction(domain(), UnaryOperator(this::substituteX))
    fun asFunctionY(): SVFunction<T> = SVFunction.fromFunction(domain(), UnaryOperator(this::substituteY))
    fun asFunctionZ(): SVFunction<T> = SVFunction.fromFunction(domain(), UnaryOperator(this::substituteZ))

}

fun main(args: Array<String>) {
    val mc = ExprCalculator.instance
    SimplificationStrategies.setCalRegularization(mc)
    proof1(mc)
    proof2(mc)
    proof3(mc)
}

fun proof1(mc: ExprCalculator) {
    val at = getVector(mc, 0)
    val bt = getVector(mc, 1)
    val innerProduct = at.innerProduct(bt)
    val left = innerProduct.derivation()
    val right = mc.add(at.derivation().innerProduct(bt), at.innerProduct(bt.derivation()))
    println("left - right is ${mc.subtract(left, right)}")
}

fun proof2(mc: ExprCalculator) {
    val at = getVector(mc, 0)
    val bt = getVector(mc, 1)
    val left = at.outerProduct(bt).derivation()
    val right = at.derivation().outerProduct(bt) + at.outerProduct(bt.derivation())
    println("left - right is ${(left - right)}")
}

fun proof3(mc: ExprCalculator) {
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