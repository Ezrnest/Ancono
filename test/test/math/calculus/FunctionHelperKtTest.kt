package test.math.calculus

import cn.ancono.math.calculus.Limit
import cn.ancono.math.calculus.LimitProcess
import cn.ancono.math.calculus.expression.FunctionHelper
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.Expression
import cn.ancono.math.numberModels.expression.simplification.buildNode
import org.junit.Assert.assertNotNull
import org.junit.Test
import test.math.TestUtils.assertMathEquals

class FunctionHelperTestA{
    val mc = ExprCalculator.instance

    @Test
    fun testLimit0(){
        val expr = mc.parse("exp(1+1/x,x)")
        val process = LimitProcess.toPositiveInf<Expression>()
        val limit = FunctionHelper.limitNode(expr.root,process, mc)
        assertNotNull(limit)
        assertMathEquals(mc.parse("e"), limit!!.value.value, mc)
    }

    @Test
    fun testLimit1(){
        val expr = mc.parse("(sin(x)-x)/(x*(cos(x)-1))")
        val process = LimitProcess.toPositiveZero(mc)
        val limit = FunctionHelper.limitNode(expr.root,process, mc)
        assertNotNull(limit)
        assertMathEquals(mc.parse("1/3"), limit!!.value.value, mc)
    }

    @Test
    fun testLimit2(){
        var expr = buildNode { Expression("x^2".p * (exp("x^3+x".p / "1+x^3".p, "1/7".p) - cos("1/x".p))) }
        expr = mc.simplify(expr)
        println(expr)
        val process = LimitProcess.toPositiveInf<Expression>()
//        FunctionHelper.hopitalThrehold = 10
        val limit = Limit.limitOf(expr,process,mc)
        println(limit)
    }

    @Test
    fun test(){
        val expr = buildNode { Expression("x^2".p * (exp("x^3+x".p / "1+x^3".p, "1/7".p) - cos("1/x".p))) }
//        val expr = mc.parseExpr("(14x^3+28+14x^-3)*exp((-x^-2)*sin(x^-1)*exp((x^3+x)/(x^3+1),1/7)+(x^-2)*cos(x^-1)*sin(x^-1),-2)*exp((x^3+x)/(x^3+1),2/7)*exp(sin(x^-1),1)")
//        println(expr)
        println(mc.differential(expr,"x",5))
    }

}