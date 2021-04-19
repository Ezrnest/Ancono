/**
 *
 */
package cn.ancono.math.calculus

import cn.ancono.math.MathCalculator
import cn.ancono.math.algebra.DecomposedPoly
import cn.ancono.math.algebra.IPolynomial
import cn.ancono.math.algebra.PolynomialUtil
import cn.ancono.math.algebra.SinglePoly
import cn.ancono.math.discrete.combination.CombUtils
import cn.ancono.math.function.AbstractSVPFunction
import cn.ancono.math.function.SVFunction
import cn.ancono.math.function.SVPFunction
import cn.ancono.math.function.invoke
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.Multinomial
import cn.ancono.math.numberModels.Term
import cn.ancono.math.numberModels.api.plus
import cn.ancono.math.numberModels.expression.DerivativeHelper
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.Expression
import cn.ancono.math.numberModels.expression.Node
import cn.ancono.math.numberModels.structure.Polynomial
import java.util.function.DoubleUnaryOperator

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
            cns[i - 1] = mc.multiplyLong(f.get(i), i.toLong())
        }
        return AbstractSVPFunction.valueOf(mc, *cns)
    }

    /**
     * Returns the derivation of a polynomial.
     */
    @JvmStatic
    fun <T : Any> derivation(f: IPolynomial<T>, mc: MathCalculator<T>): IPolynomial<T> {
        if (f is Polynomial) {
            return f.derivative()
        }
        return Polynomial.fromPolynomial(f, mc).derivative()
    }


    /**
     * Returns the integration of a single variable polynomial function.
     * @param f
     * @return
     */
    @JvmStatic
    fun <T : Any> integrate(f: SVPFunction<T>): AbstractSVPFunction<T> {
        @Suppress("UNCHECKED_CAST")
        val cns = arrayOfNulls<Any>(f.degree + 2) as Array<T>
        val mc = f.mathCalculator
        for (i in 0..f.degree) {
            //(Ax^(i+1))' = (i+1)*Ax^i
            cns[i + 1] = mc.divideLong(f.get(i), (i + 1).toLong())
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
    fun Expression.totalDifferential(vararg variableNames: String): Expression {
        val set = hashSetOf(*variableNames)
        val root = this.root
        val diffs = ArrayList<Node>(set.size)
        for (name in set) {
            diffs += DerivativeHelper.derivativeNode(root, name)
        }
        val nroot = Node.wrapNodeAM(true, diffs)
        return Expression(nroot)
    }

    /**
     * Determines whether the given list of expressions as differentials can compose
     * a total differential of the same variables. It is required that the names of variables in
     * the given list of are distinct.
     */
    fun composeTotalDifferential(partialDiffs: List<Pair<Expression, String>>,
                                 mc: ExprCalculator = ExprCalculator.instance): Boolean {
        for (p1 in partialDiffs) {
            for (p2 in partialDiffs) {
                if (p1 === p2) {
                    //the same
                    continue
                }
                if (p1.second == p2.second) {
                    throw IllegalArgumentException("Variable name duplicated:${p1.second}")
                }
                var diff1 = p1.first.derivation(p2.second)
                var diff2 = p2.first.derivation(p1.second)
                diff1 = mc.simplify(diff1)
                diff2 = mc.simplify(diff2)
                if (!mc.isEqual(diff1, diff2)) {
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
            val t0 = Term.singleChar(variableName)
            val t1 = term.divide(t0)
            val t2 = t1.multiply(Term.valueOf(term.getCharacterPower(variableName)))
            t2
        } else {
            Term.ZERO
        }
    }

    fun taylorSeriesSeq(expr: Expression, variableName: String = "x", point: Expression = Expression.ZERO,
                        mc: ExprCalculator = ExprCalculator.instance)
            : Sequence<Expression> = sequence {
        var n = 0
        var deno = 1L
        var f = expr
        while (true) {
            val nume = mc.substitute(f, variableName, point)
            val coe = mc.divideLong(nume, deno)
            yield(coe)
            n++
            deno *= n
            f = mc.differential(f, variableName)
        }
    }

//    fun taylorSeries

    fun taylorSeries(expr: Expression, variableName: String = "x",
                     point: Expression = Expression.ZERO, degree: Int = 3,
                     mc: ExprCalculator = ExprCalculator.instance): Polynomial<Expression> {
        var re = Polynomial.zero(mc)
        var f = expr
        for (n in 0..degree) {
            val nume = mc.substitute(f, variableName, point)
            val deno = CombUtils.factorial(n)
            val coe = mc.divideLong(nume, deno)
            var t = Polynomial.binomialPower(point, n, mc)
            t = t.multiply(coe)
            re += t
            f = mc.differential(f, variableName)
        }
        return re
    }

    fun <T : Any> integrate(p: Polynomial<T>): Polynomial<T> {
        return p.integration()
    }

    /**
     * Make substitute for `ax^2+bx+c`.
     * Returns the substituted polynomial a*u^2 +(4ac-b^2)/4a and u = x + b/2a
     */
    private fun makeSubstitute(a: Expression, b: Expression, c: Expression, mc: ExprCalculator)
            : Pair<Polynomial<Expression>, Polynomial<Expression>> {
//        if(mc.isZero(a)){
//            println("?")
//        }
        val t2 = mc.run { b / a / 2 }
        val sub = Polynomial.of(mc, t2, mc.one)
        // a (x + b/2a)^2 +(4ac-b^2)/4a
        val t = mc.run { c - t2 * t2 }
        val re = Polynomial.of(mc, t, mc.zero, a)
        return re to sub
    }


    /**
     * Returns the result of integrating a polynomial in the denominator part that has the degree of 1 or 2.
     * If the degree of the polynomial is two, it must not have real roots.
     */
    fun integrationDeno(deno: SinglePoly<Expression>, mc: ExprCalculator = ExprCalculator.instance,
                        variableName: String = "x"): Expression {
        val d = deno.base
        if (deno.pow == 1) {
            when (d.degree) {
                1 -> {
                    // 1/(ax+b)
                    // > ln(ax+b)/a
                    return mc.run { ln(Expression.fromPolynomialE(d, variableName)) / d.get(1) }
                }
                2 -> {
                    // 1/(ax^2+bx+c)

                    // 1/( ax^2 + c)
                    val a = d[2]
                    val b = d[1]
                    val c = d[0]
                    return intDeno2(a, b, c, mc, variableName)
                }
            }
            throw ArithmeticException("Not supported: $d")
//            return Expression(ln(deno.base))
        }
        if (d.degree == 1) {
            // 1/(ax+b)^n
            // > -1/(a(n-1)(ax+b)^(n-1))
            val p = Expression.fromPolynomialE(d, variableName)
            val nMinus1 = Expression.fromTerm(Term.valueOf(deno.pow - 1L))
            return mc.run {
                -mc.reciprocal(d[1] * nMinus1 * exp(p, nMinus1))
            }
        }
        if (d.degree != 2) {
            throw ArithmeticException("Not supported: $d")
        }
        //use step integration
        if (!mc.isZero(d[1])) {
            val (re, sub) = makeSubstitute(d[2], d[1], d[0], mc)
            val subRe = integrationDeno(SinglePoly(null, re, deno.pow))
            return mc.substitute(subRe, variableName, Expression.fromPolynomialE(sub, variableName))
        }
        val (p, q, sign) = getPQ(d[2], d[0], mc)
        val result = integrationDeno2Pow(p, q, deno.pow, mc, variableName)
        return if (deno.pow % 2 == 1) {
            mc.multiply(result, sign)
        } else {
            result
        }
    }

    /**
     * Computes the integration of
     * > 1/(p^2*x^2+q^2)^n
     */
    fun integrationDeno2Pow(p: Expression, q: Expression, n: Int, mc: ExprCalculator, variableName: String = "x"): Expression {
        if (n == 1) {
            return intDeno2PQ(p, q, mc, variableName)
        }
        val x = Expression.fromTerm(Term.singleChar(variableName))
        val q2 = mc.multiply(q, q)
        val poly = mc.run { p * p * x * x + q2 } // p^2*x^2+q^2
        // formula:
        // I_n = 1/2(n-1)q^2 * ( (2n-3)I_(n-1) + x / (p^2*x^2+q^2)^(n-1) )
        var inte = intDeno2PQ(p, q, mc, variableName)
        var polyT = poly //(p^2*x^2+q^2)^(n-1)
        for (i in 2..n) {
            val iMinusOne = Expression.fromTerm(Term.valueOf(i - 1L))

            val coe = mc.run { mc.reciprocal(iMinusOne * 2 * q2) }
            val twoIMinus3 = Expression.fromTerm(Term.valueOf(2L * i - 3))
            val prev = inte
            val backPart = mc.run { x / polyT }
            polyT = mc.multiply(polyT, poly)
            inte = mc.run { coe * (twoIMinus3 * prev + backPart) }
        }
        return inte
    }


    private fun getPQ(a: Expression, c: Expression, mc: ExprCalculator): Array<Expression> {
        val p: Expression
        val q: Expression
        val sign: Expression
        if (mc.compare(a, mc.zero) < 0) {
            p = mc.squareRoot(mc.negate(a))
            q = mc.squareRoot(mc.negate(c))
            sign = mc.negate(mc.one)
        } else {
            p = mc.squareRoot(a)
            q = mc.squareRoot(c)
            sign = mc.one
        }
        return arrayOf(p, q, sign)
    }

    /**
     * Returns the integration of
     * > 1/(p^2*x^2+q^2)
     */
    fun intDeno2PQ(p: Expression, q: Expression, mc: ExprCalculator, variableName: String = "x"): Expression {
        return mc.run { arctan(p / q * Expression.fromTerm(Term.singleChar(variableName))) / (p * q) }
    }

    fun intDeno2(a: Expression, b: Expression, c: Expression, mc: ExprCalculator, variableName: String): Expression {
        return if (mc.isZero(b)) {
            val (p, q, sign) = getPQ(a, c, mc)
            // 1/pq * arctan( p/q * x)
            mc.multiply(sign, intDeno2PQ(p, q, mc, variableName))
        } else {
            val (re, sub) = makeSubstitute(a, b, c, mc)
            val subRe = integrationDeno(SinglePoly(re), mc, variableName)
            mc.substitute(subRe, variableName, Expression.fromPolynomialE(sub, variableName))
        }
    }

    fun intDeno1(a: Expression, b: Expression, mc: ExprCalculator, variableName: String = "x"): Expression {
        // 1/(ax+b)
        // > ln(ax+b)/a
        return mc.run { ln(Expression.fromPolynomialE(Polynomial.of(mc, b, a), variableName)) / a }
    }

    /**
     * Returns the integration of the form of
     * > (Ax+B) / (ax^2+bx+c)^n
     */
    fun intFrac2Pow(nume: Polynomial<Expression>,
                    deno: SinglePoly<Expression>,
                    mc: ExprCalculator, variableName: String = "x"): Expression {
        // substitute first and
        if (nume.degree > 2) {
            throw ArithmeticException("Not supported: $nume")
        }
        val d = deno.base
        if (mc.isZero(d[1])) {
            return integrationFrac2Pow0(nume[1], nume[0], d[2], d[0], deno.pow, mc, variableName)
        }
        val a = d[2]
        val b = d[1]
        val c = d[0]
        val (nDeno, sub) = makeSubstitute(a, b, c, mc)
        val nNume = nume.substitute(Polynomial.of(mc, mc.negate(sub[0]), mc.one))// sub nume
        val reSub = integrationFrac2Pow0(nNume[1], nNume[0], nDeno[2], nDeno[0], deno.pow, mc, variableName)
        return mc.substitute(reSub, variableName, Expression.fromPolynomialE(sub, variableName))
    }

    /**
     * Returns the integration of
     * > (ax+b) / (p^2*x^2+q^2)
     */
    fun integrationFrac2Pow0(a: Expression, b: Expression,
                             p: Expression, q: Expression, n: Int,
                             mc: ExprCalculator,
                             variableName: String): Expression {
        val x = mc.multiply(a, integrationFrac2SingleX(p, q, n, mc, variableName))
        val y = mc.multiply(b, integrationDeno2Pow(mc.squareRoot(p), mc.squareRoot(q), n, mc, variableName))
        return mc.add(x, y)
    }

    /**
     * Integration of x/(px^2+q)^n
     */
    private fun integrationFrac2SingleX(p: Expression, q: Expression, pow: Int, mc: ExprCalculator, variableName: String): Expression {
        val deno = SinglePoly(null, Polynomial.of(mc, q, p), pow)
        val reX2 = integrationDeno(deno, mc, variableName)
        val x2 = Expression.fromTerm(Term.characterPower(variableName, Fraction.TWO))
        return mc.divideLong(mc.substitute(reX2, variableName, x2), 2L)
    }

    private fun integrationFracSingle(nume: Polynomial<Expression>,
                                      deno: SinglePoly<Expression>,
                                      mc: ExprCalculator, variableName: String): Expression {
        return if (deno.base.degree == 2) {
            intFrac2Pow(nume, deno, mc, variableName)
        } else {
            val t = integrationDeno(deno, mc, variableName)
            mc.multiply(nume[0], t)
        }
    }


    /**
     * Integrates the fraction. It is required that the denominator is decomposed.
     */
    fun intFrac(nume: Polynomial<Expression>, deno: DecomposedPoly<Expression>,
                mc: ExprCalculator, variableName: String): Expression {
        val (q, r) = nume.divideAndRemainder(deno.expanded)
        val partA = Expression.fromPolynomialE(integrate(q), variableName)

        val partials = PolynomialUtil.partialFraction(r, deno)
        var partB = mc.zero
        for ((n, d) in partials) {
            val term = if (d.base.degree == 2) {
                intFrac2Pow(n, d, mc, variableName)
            } else {
                mc.multiply(n[0], integrationDeno(d, mc, variableName))
            }
            partB = mc.add(partB, term)
        }
        return mc.add(partA, partB)
    }

    /**
     * Computes the integration of a rational function.
     */
    fun intRational(nume: Polynomial<Long>, deno: Polynomial<Long>, mc: ExprCalculator, variableName: String = "x")
            : Expression {
        val decomposed = PolynomialUtil.decomposeInt(deno)
        val eNume = nume.mapTo(mc, java.util.function.Function { x -> Expression.valueOf(x) })
        val eDeno = decomposed.map(mc, Expression::valueOf)
        return intFrac(eNume, eDeno, mc, variableName)
    }

}

