package cn.ancono.math.geometry.analytic.plane.curve

import cn.ancono.math.MathCalculatorHolder
import cn.ancono.math.function.MathFunction
import cn.ancono.math.function.SVFunction
import cn.ancono.math.function.invoke
import cn.ancono.math.geometry.analytic.plane.PVector
import cn.ancono.math.geometry.analytic.plane.Point
import cn.ancono.math.numberModels.api.RealCalculator
import java.util.function.UnaryOperator

typealias VectorFunction<T> = MathFunction<T, PVector<T>>

/*
 * Created at 2018/11/13 11:29
 * @author  liyicheng
 */
interface PlaneParametricCurve<T> : VectorFunction<T>, MathCalculatorHolder<T> {
    override fun apply(x: T): PVector<T> = substitute(x)


    /**
     * Substitute the given parameter [t] and returns the point.
     */
    fun substitute(t: T): PVector<T>

    fun substituteAsPoint(t: T): Point<T> = Point.fromVector(substitute(t))

    fun asPointFunction(): MathFunction<T, Point<T>> = MathFunction.andThen(this, MathFunction { x: PVector<T> -> Point.fromVector(x) })

    fun substituteX(t: T): T = substitute(t).x

    fun substituteY(t: T): T = substitute(t).y


    fun asFunctionX(): SVFunction<T> = SVFunction.fromFunction(domain(), UnaryOperator(this::substituteX))
    fun asFunctionY(): SVFunction<T> = SVFunction.fromFunction(domain(), UnaryOperator(this::substituteY))

}

internal class PPCImpl<T>(override val calculator: RealCalculator<T>, val f: SVFunction<T>, val g: SVFunction<T>) : PlaneParametricCurve<T> {
    override fun substitute(t: T): PVector<T> {
        return PVector.valueOf(f(t), g(t), calculator)
    }

    override fun substituteX(t: T): T {
        return f(t)
    }

    override fun substituteY(t: T): T {
        return g(t)
    }

    override fun asFunctionX(): SVFunction<T> {
        return f
    }

    override fun asFunctionY(): SVFunction<T> {
        return g
    }

    override fun substituteAsPoint(t: T): Point<T> {
        return Point.valueOf(f(t), g(t), calculator)
    }


}