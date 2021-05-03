package samples

import cn.ancono.math.function.asNFunction
import cn.ancono.math.function.invoke
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.Expression

/*
 * Created by lyc at 2020/3/1
 */
object ExpressionSample {
    val mc = ExprCalculator.instance
    fun useExpression() {
        val cal = Expression.getCalculator()
        val f1 = cal.parse("(x^2+3x+2)/(x+1)+sin(Pi/2)+exp(t)")
        println(f1)
        val f2 = cal.parse("y+1")
        println(f2)
        val f3 = cal.divide(f1, f2)
        println(f3)
    }

    fun asNFunctions() {
        val mc = ExprCalculator.instance
        val expr = mc.parse("x^2*y+xy+1")
        val x = mc.parse("x")
        val t = mc.parse("a+b")
        val f = expr.asNFunction("x", "y")
        println(f(x, t))
        println(f.partial(0).expr)
        println(f.partial(1).expr)
    }

    fun partialDerivative() {
        val expr = mc.parse("f(x,y)g(x,y)")
        println(expr)
        val px = mc.differential(expr, "x")
        val py = mc.differential(expr, "y")
        println(px)
        println(py)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        partialDerivative()
    }
}