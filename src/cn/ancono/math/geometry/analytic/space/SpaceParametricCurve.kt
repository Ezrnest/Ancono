package cn.ancono.math.geometry.analytic.space

import cn.ancono.math.MathCalculatorHolder
import cn.ancono.math.function.MathFunction
import cn.ancono.math.function.SVFunction
import java.util.function.UnaryOperator

typealias VectorFunction<T> = MathFunction<T, SVector<T>>

/**
 * Describes a parametric curve in three-dimensional Euclidean space.
 * Mathematically, a parametric curve is represented by a vector function: `r(t) : (a,b) -> E^3`.
 */
interface SpaceParametricCurve<T> : VectorFunction<T>, MathCalculatorHolder<T> {

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
