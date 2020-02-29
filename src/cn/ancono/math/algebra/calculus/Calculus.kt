/**
 *
 */
@file:JvmName("Calculus")

package cn.ancono.math.algebra.calculus

import cn.ancono.math.function.AbstractSVPFunction
import cn.ancono.math.function.SVFunction
import cn.ancono.math.function.SVPFunction
import cn.ancono.math.function.invoke
import cn.ancono.math.numberModels.Multinomial
import cn.ancono.math.numberModels.Term
import cn.ancono.math.numberModels.expression.DerivativeHelper
import cn.ancono.math.numberModels.expression.Expression
import java.util.function.DoubleUnaryOperator

/**
 * A utility class for some calculus calculations. This class
 * @author liyicheng
 */
private const val default_division: Long = 10000000

/**
 * Computes the integral of the function by using small rectangles to compute a
 * approximation. The function must be dimmed in `[a,b]`. The accuracy of the
 * result will be based on the `delta` given, while the function itself will
 * influence the accuracy of the result.
 * @param fx the function
 * @param a the starting of the area
 * @param b the ending of the area
 * @param delta the width of small intervals, positive
 * @return integral
 */
fun integralApproximationLinear(fx: DoubleUnaryOperator, a: Double, b: Double, delta: Double): Double {
    if (delta < 0) {
        throw IllegalArgumentException("delta < 0")
    }
    if (a > b) {
        throw IllegalArgumentException("a > b")
    }
    return integralApp0(fx, a, b, delta)
}

/**
 * Computes the integral of the function by using small rectangles to compute a
 * approximation. The function must be dimmed in `[a,b]`. This method will
 * divide the whole interval averagely to `division` parts and computes all of them.
 * @param fx the function
 * @param a the starting of the area
 * @param b the ending of the area
 * @param division the number of intervals to divide
 * @return integral
 */
@JvmOverloads
fun integralApproximationLinear(fx: DoubleUnaryOperator, a: Double, b: Double, division: Long = default_division): Double {
    if (division < 0) {
        throw IllegalArgumentException("division < 0")
    }
    if (a > b) {
        throw IllegalArgumentException("a > b")
    }
    val delta = (b - a) / division
    return integralApp0(fx, a, b, delta)
}

private fun integralApp0(fx: DoubleUnaryOperator, a: Double, b: Double, delta: Double): Double {
    var x = a
    var `in` = 0.0
    `in` += fx.applyAsDouble(x)
    while (true) {
        x += delta
        if (x >= b) {
            break
        }
        `in` += 2 * fx.applyAsDouble(x)
    }
    `in` += fx.applyAsDouble(b)
    `in` /= 2.0
    `in` *= delta
    return `in`
}

/**
 * Computes the integral of the function by using the Simpson formula.
 * @param fx the function
 * @param a the starting of the area
 * @param b the ending of the area
 * @param n the parameter determining the accuracy of this method, the bigger this argument is, the longer time
 * this method will take and the result will be more accurate.
 * @return integral
 */
@JvmOverloads
fun integralApproximationSimpson(fx: DoubleUnaryOperator, a: Double, b: Double, n: Long = default_division): Double {
    if (n <= 0) {
        throw IllegalArgumentException("n < 0")
    }
    if (a > b) {
        throw IllegalArgumentException("a > b")
    }
    var `in` = 0.0
    var x = a
    val delta = (b - a) / 2.0 / n.toDouble()
    `in` += fx.applyAsDouble(x)
    for (i in 0 until n) {
        x += delta
        `in` += 4 * fx.applyAsDouble(x)
        x += delta
        `in` += 2 * fx.applyAsDouble(x)
    }
    `in` += fx.applyAsDouble(b)
    `in` /= 3.0
    `in` *= delta
    return `in`
}

/**
 * Returns the derivation of a single variable polynomial function.
 * @param f
 * @return
 */
fun <T : Any> derivation(f: SVPFunction<T>): AbstractSVPFunction<T> {
    @Suppress("UNCHECKED_CAST") val cns = arrayOfNulls<Any>(f.degree) as Array<T>
    val mc = f.mathCalculator
    for (i in 1..f.degree) {
        //(Ax^i)' = iA*x^(i-1)
        cns[i - 1] = mc.multiplyLong(f.getCoefficient(i), i.toLong())
    }
    return AbstractSVPFunction.valueOf(mc, *cns)
}

/**
 * Returns the integration of a single variable polynomial function.
 * @param f
 * @return
 */
fun <T : Any> integration(f: SVPFunction<T>): AbstractSVPFunction<T> {
    @Suppress("UNCHECKED_CAST") val cns = arrayOfNulls<Any>(f.degree + 2) as Array<T>
    val mc = f.mathCalculator
    for (i in 0..f.degree) {
        //(Ax^(i+1))' = (i+1)*Ax^i
        cns[i + 1] = mc.divideLong(f.getCoefficient(i), (i + 1).toLong())
    }
    cns[0] = mc.zero
    return AbstractSVPFunction.valueOf(mc, *cns)
}

/**
 * Approximately computes the derivative of the given function [f] at the point of [x]. This method
 * only chooses another point near the given point by [delta] and calculates the slope of the line
 * passes through the two points.
 */
fun derivativeApproximately(f: SVFunction<Double>, x: Double, delta: Double = DEFAULT_DELTA): Double {
    val y1 = f(x)
    val y2 = f(x + delta)
    return (y2 - y1) / delta
}


const val DEFAULT_DELTA: Double = 0.00000001
const val DEFAULT_RANGE: Double = 10000.0


/**
 * Finds one root of the given single-variable function using Newton-Raphson method.
 * This method will return only if the delta of two iterations is smaller than the given
 * [rootDelta], and it will return null if the root cannot be find. There are several situations
 * where the method will assume the root cannot be found.
 * 1. The difference of adjacent x values cannot satisfy [rootDelta] even after [maxIterateTimes].
 * 2. The difference of adjacent x values exceeds [maxSearchRange].
 * 3. The derivative computed occurs to be 0.
 */
fun findRoot(f: SVFunction<Double>,
             initialX: Double,
             deriveDelta: Double = DEFAULT_DELTA,
             rootDelta: Double = DEFAULT_DELTA,
             maxSearchRange: Double = DEFAULT_RANGE,
             maxIterateTimes: Int = 25): Double? {
    //x_next = x - f(x)/f'(x)
    var x = initialX
    repeat(maxIterateTimes) {
        val nextX = x - f(x) / derivativeApproximately(f, x, deriveDelta)
        if (nextX.isNaN() || nextX.isInfinite()) {
            return null
        }
        val d = Math.abs(x - nextX)
        if (d <= rootDelta) {
            return nextX
        } else if (d > maxSearchRange) {
            //failed
            return null
        }
        x = nextX
    }
    return null
}


/**
 * Computes the derivation of the given expression without performing any simplification.
 */
fun derivation(expr: Expression, variableName: String = "x"): Expression {
    val root = expr.root
    return Expression(DerivativeHelper.derivativeNode(root, variableName))
}

/**
 * Computes the derivation of the multinomial.
 * @param expr the Multinomial
 * @param variableName the name of the variable
 * @return the derivation of the multinomial
 */
fun derivation(expr: Multinomial, variableName: String = "x"): Multinomial {
    return Multinomial.fromTerms(expr.terms.map { derivation(it, variableName) })
}

/**
 * Computes the derivation of the term.
 */
fun derivation(term: Term, variableName: String = "x"): Term {
    return if (term.containsChar(variableName)) {
        term.divide(Term.singleChar(variableName))
                .multiply(Term.valueOf(term.getCharacterPower(variableName)))
    } else {
        Term.ZERO
    }
}


//fun main(args: Array<String>) {
//    val f : SVFunction<Double> = AbstractSVPFunction.quadratic(1.0,-2.0,-3.0,Calculators.getCalculatorDoubleDev())
//    println(findRoot(f,0.0))
//}