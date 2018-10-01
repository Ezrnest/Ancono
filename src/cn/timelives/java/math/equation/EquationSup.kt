/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation

import cn.timelives.java.math.MathUtils
import cn.timelives.java.math.equation.inequation.Inequation
import cn.timelives.java.math.equation.inequation.SVPInequation
import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.numberModels.Calculators
import cn.timelives.java.math.numberModels.Fraction
import cn.timelives.java.math.numberModels.expression.Node
import cn.timelives.java.math.numberModels.structure.RingFraction
import cn.timelives.java.math.set.FiniteSet
import cn.timelives.java.math.set.Interval
import cn.timelives.java.math.set.IntervalUnion
import java.util.*
import java.util.function.Function

/**
 * This class provides useful static methods to do transformation between equation,
 * inequation and function, and also necessary tools.
 * @author liyicheng
 * 2017-10-06 15:52
 */
object EquationSup {
    /**
     * This method will try to solve the equation using the solution-formulas.Because
     * formulas are only available when `n<5`, if `n>=5`,an exception will
     * be thrown.
     *
     * **Warning: this method is not fully implemented yet.**
     * @return a list of solutions,including imaginary roots.
     * @throws ArithmeticException if `n>=5`
     */
    fun <T> solveUsingFormula(sv: SVPEquation<T>): List<T> {
        val mc = sv.mathCalculator
        when (sv.mp) {
            1 -> {
                return Arrays.asList(mc.negate(mc.divide(sv.getCoefficient(0),
                        sv.getCoefficient(1))))
            }
            2 -> {
                val a = sv.getCoefficient(2)
                val b = sv.getCoefficient(1)
                val c = sv.getCoefficient(0)
                var delta = mc.subtract(mc.multiply(b, b), mc.multiplyLong(mc.multiply(a, c), 4L))
                // x1 = (-b + sqr(delta)) / 2a
                // x2 = (-b - sqr(delta)) / 2a
                val so = ArrayList<T>(2)
                delta = mc.squareRoot(delta)
                val a2 = mc.multiplyLong(a, 2)
                var re = mc.divide(mc.subtract(delta, b), a2)
                so.add(re)
                re = mc.negate(mc.divide(mc.add(b, delta), a2))
                so.add(re)
                return so
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
     * @param mc a [MathCalculator]
     * @return the solution
     */
    fun <T:Any> solveQInequation(a: T, b: T, c: T, op: Type, mc: MathCalculator<T>): IntervalUnion<T> {
        if (mc.isZero(a)) {
            return solveLInequation(b, c, op, mc)
        } else {
            if (Inequation.isOperation(op)) {
                return SVPInequation.quadratic(a, b, c, op, mc).solution
            } else {
                val solution = MathUtils.solveEquation(a, b, c, mc)
                if (op === Type.EQUAL) {
                    if (solution.isEmpty()) {
                        return IntervalUnion.empty(mc)
                    } else if (solution.size == 1) {
                        return IntervalUnion.single(solution[0], mc)
                    } else {
                        val x1 = solution[0]
                        val x2 = solution[1]
                        return IntervalUnion.valueOf(Interval.single(x1, mc), Interval.single(x2, mc))
                    }
                } else {
                    // NOT_EQUAL
                    if (solution.isEmpty()) {
                        return IntervalUnion.universe(mc)
                    } else if (solution.size == 1) {
                        return IntervalUnion.except(solution[0], mc)
                    } else {
                        var x1 = solution[0]
                        var x2 = solution[1]
                        if (mc.compare(x1, x2) > 0) {
                            val t = x1
                            x1 = x2
                            x2 = t
                        }
                        return IntervalUnion.valueOf(Interval.fromNegativeInf(x1, false, mc),
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
     * @param mc a [MathCalculator]
     * @return the solution
     */
    fun <T:Any> solveLInequation(a: T, b: T, op: Type, mc: MathCalculator<T>): IntervalUnion<T> {
        if (mc.isZero(a)) {
            return solveCInequation(b, op, mc)
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
     * @param mc a [MathCalculator]
     * @return the solution
     */
    fun <T:Any> solveCInequation(a: T, op: Type, mc: MathCalculator<T>): IntervalUnion<T> {
        val universe = op.matches(mc.compare(a, mc.zero))
        return if (universe) IntervalUnion.universe(mc) else IntervalUnion.empty(mc)
    }


    /**
     * Returns all the quotient solutions of the equation.
     */
    fun solutionsFraction(equation : SVPEquation<Fraction>) : Set<Fraction>{
        var equa = equation.simplify(Fraction.fractionSimplifier)

        var multiplier = 1L
        for(f in equa){
            if(!f.isInteger){
                multiplier = MathUtils.lcm(multiplier,f.denominator)
            }
        }
        if(multiplier != 1L){
            equa = equa.mapTo(Function{it.multiply(multiplier)},equa.mathCalculator)
        }

        val first = equa.first()!!.numerator
        var const = equa.constant()!!.numerator
        val result = TreeSet<Fraction>()
        var lastIndex = 0
        while (const == 0L){
            lastIndex++
            const = equa.getCoefficient(lastIndex).numerator
            result.add(Fraction.ZERO)
        }
        //solution = const.factor / first.factor
        val ff = MathUtils.factors(first)
        val cf = MathUtils.factors(const)
        for(nume in cf){
            for(deno in ff){
                var root = Fraction.valueOf(nume,deno)
                if(equa.isSolution(root)){
                    result.add(root)
                }
                root = root.negate()
                if(equa.isSolution(root)){
                    result.add(root)
                }
            }
        }

        return result

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
