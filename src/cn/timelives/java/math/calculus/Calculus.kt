/**
 *
 */
package cn.timelives.java.math.calculus

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.algebra.Polynomial
import cn.timelives.java.math.function.AbstractSVPFunction
import cn.timelives.java.math.function.SVFunction
import cn.timelives.java.math.function.SVPFunction
import cn.timelives.java.math.function.invoke
import cn.timelives.java.math.numberModels.Multinomial
import cn.timelives.java.math.numberModels.Term
import cn.timelives.java.math.numberModels.expression.DerivativeHelper
import cn.timelives.java.math.numberModels.expression.ExprCalculator
import cn.timelives.java.math.numberModels.expression.Expression
import cn.timelives.java.math.numberModels.expression.Node
import cn.timelives.java.math.numberModels.structure.PolynomialX
import cn.timelives.java.math.numberTheory.combination.CFunctions
import java.util.function.DoubleUnaryOperator
import kotlin.coroutines.experimental.buildSequence
import cn.timelives.java.math.numberModels.api.plus

object Calculus {
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
    fun <T : Any> derivation(f: SVPFunction<T>): AbstractSVPFunction<T> {
        @Suppress("UNCHECKED_CAST")
        val cns = arrayOfNulls<Any>(f.degree) as Array<T>
        val mc = f.mathCalculator
        for (i in 1..f.degree) {
            //(Ax^i)' = iA*x^(i-1)
            cns[i - 1] = mc.multiplyLong(f.getCoefficient(i), i.toLong())
        }
        return AbstractSVPFunction.valueOf(mc, *cns)
    }

    /**
     * Returns the derivation of a polynomial.
     */
    @JvmStatic
    fun <T : Any> derivation(f: Polynomial<T>, mc: MathCalculator<T>): Polynomial<T> {
        if(f is PolynomialX){
            return f.derivative()
        }
        return PolynomialX.fromPolynomial(f,mc).derivative()
    }



    /**
     * Returns the integration of a single variable polynomial function.
     * @param f
     * @return
     */
    @JvmStatic
    fun <T : Any> integration(f: SVPFunction<T>): AbstractSVPFunction<T> {
        @Suppress("UNCHECKED_CAST")
        val cns = arrayOfNulls<Any>(f.degree + 2) as Array<T>
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
    @JvmStatic
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
    @JvmStatic
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
    @JvmStatic
    fun Expression.derivation(variableName: String = "x"): Expression {
        val root = this.root
        return Expression(DerivativeHelper.derivativeNode(root, variableName))
    }

    /**
     * Returns the total differential of the given expression without performing any simplification.
     */
    @JvmStatic
    fun Expression.totalDifferential(vararg variableNames : String) : Expression{
        val set = hashSetOf(*variableNames)
        val root = this.root
        val diffs = ArrayList<Node>(set.size)
        for(name in set){
            diffs += DerivativeHelper.derivativeNode(root,name)
        }
        val nroot = Node.wrapNodeAM(true,diffs)
        return Expression(nroot)
    }

    /**
     * Determines whether the given list of expressions as differentials can compose
     * a total differential of the same variables. It is required that the names of variables in
     * the given list of are distinct.
     */
    fun composeTotalDifferential(partialDiffs : List<Pair<Expression,String>>,
                                 mc : ExprCalculator = ExprCalculator.newInstance) : Boolean{
        for(p1 in partialDiffs){
            for(p2 in partialDiffs){
                if(p1 === p2){
                    //the same
                    continue
                }
                if(p1.second == p2.second){
                    throw IllegalArgumentException("Variable name duplicated:${p1.second}")
                }
                var diff1 = p1.first.derivation(p2.second)
                var diff2 = p2.first.derivation(p1.second)
                diff1 = mc.simplify(diff1)
                diff2 = mc.simplify(diff2)
                if(!mc.isEqual(diff1,diff2)){
                    return false
                }
            }
        }
        return true
    }

    /**
     * Computes the derivation of the multinomial.
     * @param expr the Multinomial
     * @param variableName the name of the variable
     * @return the derivation of the multinomial
     */
    @JvmStatic
    fun derivation(expr: Multinomial, variableName: String = "x"): Multinomial {
        return Multinomial.fromTerms(expr.terms.map { derivation(it, variableName) })
    }

    /**
     * Computes the derivation of the term.
     */
    @JvmStatic
    fun derivation(term: Term, variableName: String = "x"): Term {
        return if (term.containsChar(variableName)) {
            term.divide(Term.singleChar(variableName))
                    .multiply(Term.valueOf(term.getCharacterPower(variableName)))
        } else {
            Term.ZERO
        }
    }

    fun taylorSeriesSeq(expr : Expression, variableName: String = "x", point : Expression = Expression.ZERO,
                       mc : ExprCalculator = ExprCalculator.newInstance)
            : Sequence<Expression> = buildSequence {
        var n = 0
        var deno = 1L
        var f = expr
        while(true){
            val nume = mc.substitute(f,variableName,point)
            val coe = mc.divideLong(nume,deno)
            yield(coe)
            n++
            deno *= n
            f = mc.differential(f,variableName)
        }
    }

//    fun taylorSeries

    fun taylorSeries(expr : Expression, variableName: String = "x",
                     point : Expression = Expression.ZERO, degree : Int = 3,
                     mc : ExprCalculator = ExprCalculator.newInstance) : PolynomialX<Expression>{
        var re = PolynomialX.zero(mc)
        var f = expr
        for(n in 0..degree){
            val nume = mc.substitute(f,variableName,point)
            val deno = CFunctions.factorial(n)
            val coe = mc.divideLong(nume,deno)
            var t = PolynomialX.binomialPower(point,n,mc)
            t = t.multiply(coe)
            re += t
            f = mc.differential(f,variableName)
        }
        return re
    }

}


fun main(args: Array<String>) {
//    val f : SVFunction<Double> = AbstractSVPFunction.quadratic(1.0,-2.0,-3.0,Calculators.getCalculatorDoubleDev())
//    println(findRoot(f,0.0))
    val mc = ExprCalculator.newInstance
    val m = Multinomial.valueOf("1/x+x")
    println(m)
    val p1 = LimitProcess("x", LimitValue.valueOf(Expression.valueOf("2")), LimitDirection.LEFT)
    val p2 = LimitProcess<Expression>("x", LimitValue.infinity(),LimitDirection.LEFT)
    println(Limit.limitOf(m,p1,mc))
    val t = Limit.limitOf(m,p2,mc)
    println(t)
}