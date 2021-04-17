package samples

import cn.ancono.math.MathSymbol
import cn.ancono.math.calculus.*
import cn.ancono.math.calculus.Limit.limitOf
import cn.ancono.math.calculus.LimitProcess.Companion.toZero
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.Multinomial
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.Expression
import cn.ancono.math.numberModels.structure.Polynomial


/*
 * Created by lyc at 2020/3/1
 */
object CalculusSample {
    fun limitSample() {
        val mc = Expression.getCalculator()
        val expr = mc.parse("sin(x)/x")
        val result = limitOf(expr, toZero(mc), mc)!!
        println("as x -> 0, lim sin(x)/x = $result")
        //result = 1
    }


    fun limitSample2() {
//    val f : SVFunction<Double> = AbstractSVPFunction.quadratic(1.0,-2.0,-3.0,Calculators.getCalculatorDoubleDev())
//    println(findRoot(f,0.0))

        val mc = ExprCalculator.instance
        val expr = mc.parse("(x^6+3x^4+3x^2+1)/(x^12+6x^10+15x^8+20x^6+15x^4+6x^2+1)")
        println(expr)
        val m = Multinomial.parse("1/x+x")
        println(m)
        val p1 = LimitProcess("x", LimitValue.valueOf(Expression.valueOf("2")), LimitDirection.LEFT)
        val p2 = LimitProcess<Expression>("x", LimitValue.infinity(), LimitDirection.LEFT)
        println(Limit.limitOf(m, p1, mc))
        val t = Limit.limitOf(m, p2, mc)
        println(t)

//        println(mc.differential(inte))
//
//        val p = mc.parseExpr("p")
//        val q = mc.parseExpr("q")
//        val re = Calculus.integrationDeno2Pow(p, q, 3, mc)
//        mc.setProperty(PROP_MERGE_FRACTION, "true") // make it look better
//        val diff = mc.differential(re)
//        println("f(x) = $diff")
//        println("${MathSymbol.INTEGRAL}f(x)dx = $re + c")

    }

    fun integrateRational() {
        val mc = ExprCalculator.instance
        val mcl = Calculators.longExact()
        val nume = Polynomial.parse("x", mcl, String::toLong)
        val deno = Polynomial.parse("x^3 + 1", mcl, String::toLong)

        println("${MathSymbol.INTEGRAL} ($nume) / ($deno) dx =")
        val integral = Calculus.intRational(nume, deno, mc, "x")
        println(integral)
    }


}

fun main() {
    CalculusSample.integrateRational()
}