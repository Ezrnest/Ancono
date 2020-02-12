package cn.timelives.java.math.calculus

import cn.timelives.java.math.numberModels.expression.ExprCalculator
import cn.timelives.java.math.numberModels.expression.Expression
import cn.timelives.java.math.numberModels.expression.Node
import cn.timelives.java.math.numberModels.structure.Polynomial
import java.util.function.Function


/**
 * Created at 2019/12/25 22:18
 * @author  lyc
 */
object DefiniteIntegral {

}

object IndifiniteIntegral {

    fun intSin(mc: ExprCalculator = ExprCalculator.instance, variableName: String = "x"): Expression {
        return intSin0(mc, Expression.ofCharacter(variableName))
    }

    fun intCos(mc: ExprCalculator = ExprCalculator.instance, variableName: String = "x"): Expression {
        return intCos0(mc, Expression.ofCharacter(variableName))
    }

    private fun intSin0(mc: ExprCalculator, variable: Expression): Expression {
        return mc.negate(mc.cos(variable))
    }

    private fun intCos0(mc: ExprCalculator, variable: Expression): Expression {
        return mc.sin(variable)
    }


    fun intPolySin(p: Expression, mc: ExprCalculator = ExprCalculator.instance, variableName: String = "x"): Expression {
        val root = p.root
        require(Node.isPolynomial(root))
        val m = Node.getPolynomialPart(root, mc)
        val poly = Polynomial.fromMultinomial(m, variableName).mapTo(Function { Expression.fromMultinomial(it) }, mc)
        val variable = Expression.ofCharacter(variableName)
        return intPolySin0(poly, mc, variable, variableName)
//        p.root.
    }

    fun intPolyCos(p: Expression, mc: ExprCalculator = ExprCalculator.instance, variableName: String = "x"): Expression {
        val root = p.root
        require(Node.isPolynomial(root))
        val m = Node.getPolynomialPart(root, mc)
        val poly = Polynomial.fromMultinomial(m, variableName).mapTo(Function { Expression.fromMultinomial(it) }, mc)
        val variable = Expression.ofCharacter(variableName)
        return intPolyCos0(poly, mc, variable, variableName)
    }

    private fun intPolySin0(p: Polynomial<Expression>, mc: ExprCalculator, variable: Expression, vname: String): Expression {
        if (p.isConstant) {
            return mc.multiply(Expression.fromPolynomialE(p, vname), intSin0(mc, variable))
        }
        val a = intPolyCos0(p.derivative(), mc, variable, vname)
        val b = mc.multiply(Expression.fromPolynomialE(p, vname), mc.cos(variable))
        return mc.subtract(a, b)
    }

    private fun intPolyCos0(p: Polynomial<Expression>, mc: ExprCalculator, variable: Expression, vname: String): Expression {
        if (p.isConstant) {
            return mc.multiply(Expression.fromPolynomialE(p, vname), intCos0(mc, variable))
        }
        val a = mc.multiply(Expression.fromPolynomialE(p, vname), mc.sin(variable))
        val b = intPolySin0(p.derivative(), mc, variable, vname)
        return mc.subtract(a, b)
    }
}

//fun main(args: Array<String>) {
//    val mc = ExprCalculator.instance
//    val f = mc.parseExpr("x^2+2x+4")
//    val F = IndifiniteIntegral.intPolyCos(f, mc)
//    println(F)
//    println(mc.differential(F))
//}