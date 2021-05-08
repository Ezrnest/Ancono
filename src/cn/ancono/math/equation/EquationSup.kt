/**
 * 2017-10-06
 */
package cn.ancono.math.equation

import cn.ancono.math.MathUtils
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.calculus.Calculus
import cn.ancono.math.calculus.Calculus.DEFAULT_DELTA
import cn.ancono.math.calculus.Calculus.DEFAULT_RANGE
import cn.ancono.math.equation.inequation.Inequation
import cn.ancono.math.equation.inequation.SVPInequation
import cn.ancono.math.function.SVFunction
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.api.RealCalculator
import cn.ancono.math.set.Interval
import cn.ancono.math.set.IntervalUnion
import cn.ancono.math.set.MathSets
import java.util.*
import kotlin.math.absoluteValue

/**
 * This class provides useful static methods to do transformation between equation,
 * inequation and function, and also necessary tools.
 * @author liyicheng
 * 2017-10-06 15:52
 */
object EquationSup {


    /**
     * Solve an equation that
     * <pre>ax^2 + bx + c = 0</pre>
     * This method will ignore imaginary solutions.
     *
     * This method will return a list of solutions,which will contain
     * no element if there is no real solution(`delta<0`),
     * one if there is only one solution(or two solutions of the identity value)(`delta==0`)
     * or two elements if there are two solutions((`delta>0`).
     *
     * This method normally requires `squareRoot()` method of the [RealCalculator].
     *
     * @param a  the coefficient of x^2.
     * @param b  the coefficient of x.
     * @param c  the constant coefficient
     * @param mc a MathCalculator
     * @return the list of solution with no order.
     */
    fun <T> solveQuadraticR(a: T, b: T, c: T, mc: RealCalculator<T>): List<T> {
        //Calculate the delta
        val delta = mc.eval { b * b - 4L * a * c }
        var compare = 1
        try {
            compare = mc.compare(delta, mc.zero)
        } catch (ex: UnsupportedOperationException) {
            try {
                if (mc.isZero(delta)) compare = 0
            } catch (ex2: UnsupportedOperationException) {
            }
        }
        //		Printer.print(delta);
        return when {
            compare < 0 -> {
                //no solution
                emptyList()
            }
            compare == 0 -> {
                val re = mc.eval { -(b / a / 2L) }
                // -b/2a
                listOf(re)
            }
            else -> {
                // x1 = (-b + sqr(delta)) / 2a
                // x2 = (-b - sqr(delta)) / 2a
                solveQuadraticDelta(a, b, c, delta, mc)
            }
        }
    }

    private fun <T> solveQuadraticDelta(a: T, b: T, c: T, delta: T, mc: RealCalculator<T>): List<T> {
        val t = mc.squareRoot(delta)
        val a2 = mc.eval { 2L * a }
        val x1 = mc.eval {
            (-b + t) / a2
        }
        val x2 = mc.eval {
            (-b - t) / a2
        }
        return listOf(x1, x2)
    }

    /**
     * Solve an equation of
     *
     *     ax^2 + bx + c = 0
     *
     * This method will use the root-formula and will compute all of the solutions(include imaginary
     * solutions), and always returns two solutions even if the two solutions are the identity.
     *
     * @param a  the coefficient of x^2, non-zero
     * @param b  the coefficient of x.
     * @param c  the constant coefficient
     * @param mc a RealCalculator
     * @return a list of the solutions
     */
    fun <T> solveQuadraticC(a: T, b: T, c: T, mc: RealCalculator<T>): List<T> {
        val delta = mc.eval { b * b - 4L * a * c }
        return solveQuadraticDelta(a, b, c, delta, mc)
    }

    /**
     * Solves an inequality of
     * <pre>ax^2 + bx + c = 0</pre>
     *
     * @param a   the coefficient of x^2.
     * @param b   the coefficient of x.
     * @param c   the constant coefficient
     * @param mc  a MathCalculator
     * @param <T>
     * @return
    </T> */
    fun <T> solveInequality(a: T, b: T, c: T, op: Type, mc: RealCalculator<T>?): IntervalUnion<T> {
        return SVPInequation.quadratic(a, b, c, op, mc).solution
    }


    /**
     * This method will try to solve the equation using the solution-formulas. Because
     * formulas are only available when `n<5`, if `n>=5`,an exception will
     * be thrown.
     *
     * **Warning: this method is not fully implemented yet.**
     * @return a list of solutions,including imaginary roots.
     * @throws ArithmeticException if `n>=5`
     */
    @JvmStatic
    fun <T> solveUsingFormula(sv: SVPEquation<T>): List<T> {
        val mc = sv.calculator
        return when (sv.mp) {
            1 -> {
                listOf(mc.negate(mc.divide(sv.get(0),
                        sv.get(1))))
            }
            2 -> {
                solveQuadraticC(sv[2], sv[1], sv[0], mc)
            }
            //TODO implement the formulas
            else -> {
                throw ArithmeticException("No formula available.")
            }
        }
    }

    /**
     * Returns the solution of
     * <pre>ax^2 + bx + c *op* 0</pre>
     * The coefficient `a`,`b` and `c` may be zero.
     *
     * The operation can be any.
     * @param a the coefficient of `x^2`
     * @param b the coefficient of `x`
     * @param c the constant
     * @param op the operation
     * @param mc a [RealCalculator]
     * @return the solution
     */
    @JvmStatic
    fun <T> solveQuadraticInequality(a: T, b: T, c: T, op: Type, mc: RealCalculator<T>): IntervalUnion<T> {
        if (mc.isZero(a)) {
            return solveLinearInequality(b, c, op, mc)
        } else {
            if (Inequation.isOperation(op)) {
                return SVPInequation.quadratic(a, b, c, op, mc).solution
            } else {
                val solution = solveQuadraticR(a, b, c, mc)
                if (op === Type.EQUAL) {
                    return if (solution.isEmpty()) {
                        IntervalUnion.empty(mc)
                    } else if (solution.size == 1) {
                        IntervalUnion.single(solution[0], mc)
                    } else {
                        val x1 = solution[0]
                        val x2 = solution[1]
                        IntervalUnion.valueOf(Interval.single(x1, mc), Interval.single(x2, mc))
                    }
                } else {
                    // NOT_EQUAL
                    return if (solution.isEmpty()) {
                        IntervalUnion.universe(mc)
                    } else if (solution.size == 1) {
                        IntervalUnion.except(solution[0], mc)
                    } else {
                        var x1 = solution[0]
                        var x2 = solution[1]
                        if (mc.compare(x1, x2) > 0) {
                            val t = x1
                            x1 = x2
                            x2 = t
                        }
                        IntervalUnion.valueOf(Interval.fromNegativeInf(x1, false, mc),
                                Interval.openInterval(x1, x2, mc), Interval.toPositiveInf(x2, false, mc))
                    }
                }
            }
        }
    }

    /**
     * Returns the solution of
     * <pre>ax + b *op* 0</pre>
     * The coefficient `a` and `b` may be zero.
     *
     * The operation can be any.
     * @param a the coefficient of `x`
     * @param b the constant
     * @param op the operation
     * @param mc a [RealCalculator]
     * @return the solution
     */
    @JvmStatic
    fun <T> solveLinearInequality(a: T, b: T, op: Type, mc: RealCalculator<T>): IntervalUnion<T> {
        if (mc.isZero(a)) {
            return solveConstantIequality(b, op, mc)
        }
        if (Inequation.isOperation(op)) {
            return IntervalUnion.valueOf(SVPInequation.linear(a, b, op, mc).solution)
        }
        val x = mc.negate(mc.divide(b, a))
        return if (op === Type.EQUAL) {
            IntervalUnion.single(x, mc)
        } else {
            // NOT_EQUAL
            IntervalUnion.except(x, mc)
        }
    }

    /**
     * Returns the solution of
     * <pre> a *op* 0</pre>
     * The coefficient `a` may be zero.
     *
     * The operation can be any.
     * @param a the constant
     * @param op the operation
     * @param mc a [RealCalculator]
     * @return the solution
     */
    @JvmStatic
    fun <T> solveConstantIequality(a: T, op: Type, mc: RealCalculator<T>): IntervalUnion<T> {
        val universe = op.matches(mc.compare(a, mc.zero))
        return if (universe) IntervalUnion.universe(mc) else IntervalUnion.empty(mc)
    }


    /**
     * Returns all the rational solutions of the equation.
     */
    @JvmStatic
    fun solutionsFraction(equation: SVPEquation<Fraction>): Set<Fraction> {
        var equa = equation.simplify(Fraction.fractionSimplifier)

        var multiplier = 1L
        for (f in equa.coefficients()) {
            if (!f.isInteger) {
                multiplier = MathUtils.lcm(multiplier, f.denominator)
            }
        }
        if (multiplier != 1L) {
            equa = equa.mapTo(equa.calculator) { it.multiply(multiplier) }
        }

        val first = equa.first()!!.numerator.absoluteValue
        var const = equa.constant()!!.numerator.absoluteValue
        val result = TreeSet<Fraction>()
        var lastIndex = 0
        while (const == 0L) {
            lastIndex++
            const = equa.get(lastIndex).numerator.absoluteValue
            result.add(Fraction.ZERO)
        }
        //solution = const.factor / first.factor
        val ff = MathUtils.factors(first)
        val cf = MathUtils.factors(const)
        for (nume in cf) {
            for (deno in ff) {
                var root = Fraction.of(nume, deno)
                if (equa.isSolution(root)) {
                    result.add(root)
                }
                root = root.negate()
                if (equa.isSolution(root)) {
                    result.add(root)
                }
            }
        }

        return result

    }

    /**
     * Find a root of [f].
     * @see cn.ancono.math.calculus.Calculus.findRoot
     */
    fun findRoot(f: SVEquation<Double>,
                 initialX: Double,
                 deriveDelta: Double = DEFAULT_DELTA,
                 rootDelta: Double = DEFAULT_DELTA,
                 maxSearchRange: Double = DEFAULT_RANGE,
                 maxIterateTimes: Int = 25): Double? {
        return Calculus.findRoot(
                SVFunction.fromFunction(MathSets.universe(), f.asFunction()),
                initialX, deriveDelta, rootDelta, maxSearchRange, maxIterateTimes)
    }

}

//fun main(args: Array<String>) {
//    val equation = SVPEquation.quadratic(2,-5,2,Calculators.getCalculatorInteger()).mapTo(Function{ it -> Fraction.valueOf(it.toLong())},Fraction.calculator)
//    println(EquationSup.solutionsFraction(equation))
//    println(EquationSup.solveUsingFormula(equation))
//}
/**
 *
 */
