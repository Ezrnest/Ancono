package samples

import cn.ancono.math.MathSymbol
import cn.ancono.math.calculus.*
import cn.ancono.math.calculus.Limit.limitOf
import cn.ancono.math.calculus.LimitProcess.Companion.toZero
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.Multinomial
import cn.ancono.math.numberModels.expression.Expression
import cn.ancono.math.numberModels.expression.SimplificationStrategies
import cn.ancono.math.numberModels.structure.Polynomial
import kotlin.jvm.JvmStatic


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
        val m = Multinomial.valueOf("1/x+x")
        println(m)
        val p1 = LimitProcess("x", LimitValue.valueOf(Expression.valueOf("2")), LimitDirection.LEFT)
        val p2 = LimitProcess<Expression>("x", LimitValue.infinity(), LimitDirection.LEFT)
        println(Limit.limitOf(m, p1, mc))
        val t = Limit.limitOf(m, p2, mc)
        println(t)

        val mcl = Calculators.longExact()
//    val nume = Polynomial.valueOf(mc,1L,0L,0L,1L)
//    val deno = Polynomial.valueOf(mc,0L,-1L,3L,-3L,1L)
        val nume = Polynomial.valueOf(mcl, 0L, 1L)
        val deno = Polynomial.valueOf(mcl, 1, 0, 0, 1L)

        println("${MathSymbol.INTEGRAL} ($nume) / ($deno) dx")
//    println(AlgebraUtil.partialFractionInt(nume,deno))
        mc.setProperty(SimplificationStrategies.PROP_MERGE_FRACTION, "false")
        mc.setProperty(SimplificationStrategies.PROP_ENABLE_EXPAND, "false")
        mc.setProperty(SimplificationStrategies.PROP_FRACTION_TO_EXP, "true")

        val inte = Calculus.intRational(nume, deno, mc, "x")
        println(inte)
        mc.setProperty(SimplificationStrategies.PROP_ENABLE_EXPAND, "true")
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

    @JvmStatic
    fun main(args: Array<String>) {
        limitSample()
    }
}