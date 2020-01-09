package cn.timelives.java.math.geometry.analytic.spaceAG.curve

import cn.timelives.java.math.MathCalculatorHolder
import cn.timelives.java.math.calculus.Calculus.derivation
import cn.timelives.java.math.function.*
import cn.timelives.java.math.geometry.analytic.spaceAG.SPoint
import cn.timelives.java.math.geometry.analytic.spaceAG.SVector
import cn.timelives.java.math.minus
import cn.timelives.java.math.numberModels.expression.ExprCalculator
import cn.timelives.java.math.numberModels.expression.Expression
import cn.timelives.java.math.numberModels.expression.SimplificationStrategies
import cn.timelives.java.math.plus
import java.util.function.UnaryOperator

typealias VectorFunction<T> = MathFunction<T, SVector<T>>

/**
 * Describes a parametric curve in three-dimension Euclid space. The curve is represented by a function: R->E3 in math
 * and by [MathFunction<T,SPoint<T>>] here. The curve can be notated as r(t), which is a vector function.
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