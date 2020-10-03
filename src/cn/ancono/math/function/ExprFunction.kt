package cn.ancono.math.function

import cn.ancono.math.MathCalculatorHolder
import cn.ancono.math.calculus.Calculus.derivation
import cn.ancono.math.numberModels.api.DivisionRingNumberModel
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.Expression
import cn.ancono.math.numberModels.expression.Node
import cn.ancono.math.property.Composable

class ExprFunction(val expr: Expression,
                   override val mathCalculator: ExprCalculator,
                   val variableName: String = "x") :
        SVFunction<Expression>,
        MathCalculatorHolder<Expression>,
        DerivableSVFunction<Expression>, Composable<ExprFunction>,
        DivisionRingNumberModel<ExprFunction> {

    init {
        require(variableName.isNotEmpty())
    }

    override fun isZero(): Boolean {
        val rt = expr.root
        return rt.type == Node.Type.POLYNOMIAL && Node.getPolynomialPart(rt, null).isZero
    }

    override fun add(y: ExprFunction): ExprFunction {
        require(variableName == y.variableName)
        return functionOf(mathCalculator.add(expr, y.expr))
    }

    override fun negate(): ExprFunction {
        return functionOf(mathCalculator.negate(expr))
    }

    private fun functionOf(expr: Expression): ExprFunction {
        return ExprFunction(expr, mathCalculator, variableName)
    }

    override fun multiply(y: ExprFunction): ExprFunction {
        require(variableName == y.variableName)
        return functionOf(mathCalculator.multiply(expr, y.expr))
    }

    override fun reciprocal(): ExprFunction {
        return functionOf(mathCalculator.reciprocal(expr))
    }

    override fun divide(y: ExprFunction): ExprFunction {
        require(variableName == y.variableName)
        return functionOf(mathCalculator.divide(expr, y.expr))
    }

    override fun subtract(y: ExprFunction): ExprFunction {
        require(variableName == y.variableName)
        return functionOf(mathCalculator.subtract(expr, y.expr))
    }

    override fun apply(x: Expression): Expression {
        return mathCalculator.substitute(expr, variableName, x)
    }

    override fun compose(before: ExprFunction): ExprFunction {
        return ExprFunction(mathCalculator.substitute(expr, variableName, before.expr), mathCalculator, before.variableName)
    }

    override fun andThen(after: ExprFunction): ExprFunction {
        return after.compose(this)
    }

    override val derivative: DerivableSVFunction<Expression> by lazy {
        ExprFunction(mathCalculator.differential(expr, (variableName)), mathCalculator, variableName)
    }
}

fun Expression.asFunction(mc: ExprCalculator, variableName: String = "x") = ExprFunction(this, mc, variableName)