package cn.ancono.math.function

import cn.ancono.math.MathCalculatorHolder
import cn.ancono.math.numberModels.api.DivisionRingNumberModel
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.Expression
import cn.ancono.math.numberModels.expression.Node
import cn.ancono.math.property.Composable

class ExprFunction(val expr: Expression,
                   override val calculator: ExprCalculator,
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
        return rt.type == Node.Type.POLYNOMIAL && Node.getPolynomialPart(rt, null).isZero()
    }

    override fun add(y: ExprFunction): ExprFunction {
        require(variableName == y.variableName)
        return functionOf(calculator.add(expr, y.expr))
    }

    override fun negate(): ExprFunction {
        return functionOf(calculator.negate(expr))
    }

    private fun functionOf(expr: Expression): ExprFunction {
        return ExprFunction(expr, calculator, variableName)
    }

    override fun multiply(y: ExprFunction): ExprFunction {
        require(variableName == y.variableName)
        return functionOf(calculator.multiply(expr, y.expr))
    }

    override fun reciprocal(): ExprFunction {
        return functionOf(calculator.reciprocal(expr))
    }

    override fun divide(y: ExprFunction): ExprFunction {
        require(variableName == y.variableName)
        return functionOf(calculator.divide(expr, y.expr))
    }

    override fun subtract(y: ExprFunction): ExprFunction {
        require(variableName == y.variableName)
        return functionOf(calculator.subtract(expr, y.expr))
    }

    override fun apply(x: Expression): Expression {
        return calculator.substitute(expr, variableName, x)
    }

    override fun compose(before: ExprFunction): ExprFunction {
        return ExprFunction(calculator.substitute(expr, variableName, before.expr), calculator, before.variableName)
    }

    override fun andThen(after: ExprFunction): ExprFunction {
        return after.compose(this)
    }

    override val derivative: DerivableSVFunction<Expression> by lazy {
        ExprFunction(calculator.differential(expr, (variableName)), calculator, variableName)
    }
}

fun Expression.asFunction(mc: ExprCalculator, variableName: String = "x") = ExprFunction(this, mc, variableName)


class NExprFunction(val expr: Expression,
                    override val calculator: ExprCalculator,
                    val variables: List<String>) :
        NDerivableFunction<Expression, Expression>,
        NMathFunction<Expression, Expression>,
        MathCalculatorHolder<Expression>,
//        DerivableSVFunction<Expression>, Composable<ExprFunction>,
        DivisionRingNumberModel<NExprFunction> {

    init {
        require(variables.all { it.isNotEmpty() })
    }

    override fun isZero(): Boolean {
        val rt = expr.root
        return rt.type == Node.Type.POLYNOMIAL && Node.getPolynomialPart(rt, null).isZero()
    }

    override fun add(y: NExprFunction): NExprFunction {
        require(variables == y.variables)
//        require(variableName == y.variableName)
        return functionOf(calculator.add(expr, y.expr))
    }

    override fun negate(): NExprFunction {
        return functionOf(calculator.negate(expr))
    }

    private fun functionOf(expr: Expression): NExprFunction {
        return NExprFunction(expr, calculator, variables)
    }

    override fun multiply(y: NExprFunction): NExprFunction {
        require(variables == y.variables)
        return functionOf(calculator.multiply(expr, y.expr))
    }

    override fun reciprocal(): NExprFunction {
        return functionOf(calculator.reciprocal(expr))
    }

    override fun divide(y: NExprFunction): NExprFunction {
        require(variables == y.variables)
        return functionOf(calculator.divide(expr, y.expr))
    }

    override fun subtract(y: NExprFunction): NExprFunction {
        require(variables == y.variables)
        return functionOf(calculator.subtract(expr, y.expr))
    }

    override fun apply(x: List<Expression>): Expression {
        return calculator.substituteAll(expr, variables.zip(x).toMap())
    }


    override val paramLength: Int = variables.size

    private val cachedPartials = arrayOfNulls<NExprFunction>(variables.size)

    override fun partial(i: Int): NExprFunction {
        val t = cachedPartials[i]
        if (t != null) {
            return t
        }
        val p = calculator.differential(expr, variables[i])
        val f = NExprFunction(p, calculator, variables)
        cachedPartials[i] = f
        return f
    }

}

fun Expression.asNFunction(vararg variables: String, mc: ExprCalculator = ExprCalculator.instance) = NExprFunction(this, mc, variables.toList())


//fun main() {
//    val mc = ExprCalculator.instance
//    val expr =  mc.parse("x^2*y+xy+1")
//    val x = mc.parse("x")
//    val t = mc.parse("a+b")
//    val f = expr.asNFunction("x","y")
//    println(f(x,t))
//    println(f.partial(0).expr)
//    println(f.partial(1).expr)
//}
