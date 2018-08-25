package cn.timelives.java.math.numberModels.expression.simplification

import cn.timelives.java.math.numberModels.expression.ExprCalculator
import cn.timelives.java.math.numberModels.expression.Expression
import cn.timelives.java.math.numberModels.expression.SimpleStrategy
import cn.timelives.java.math.numberModels.expression.SimplificationStrategies

internal object SimplificationKotlin {
    fun addDefault(list: MutableList<SimpleStrategy>) {
        list.add(tri1())
    }

    private fun tri1(): SimpleStrategy {
        val matcher = exp(sin("x".ref), 2.m) + exp(cos("x".ref), 2.m)
        val builder: ReplacementBuilder = { _, _ -> "1".p }
        val replacer = wrapAMReplacer(matcher, builder)
        return replacer.asStrategy(setOf(SimplificationStrategies.TRIGONOMETRIC_FUNCTION))
    }
}

fun main(args: Array<String>) {
    SimplificationStrategies.setEnableSpi(true)
    val ec = ExprCalculator.newInstance
    val expr = Expression.valueOf("exp(sin(x),2)+exp(cos(x),2)+1")
    println(ec.simplify(expr))
}
