package test.math.calculus

import cn.ancono.math.calculus.Calculus
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.Expression
import org.junit.Test

class CalculusTest {

    @Test
    fun taylorSeries() {
        val expr = Expression.valueOf("sin(x)")
        val mc = ExprCalculator.instance
        val taylor = Calculus.taylorSeries(expr, degree = 3, mc = mc)
        kotlin.test.assertTrue {
            mc.isEqual(taylor.get(3), Expression.valueOf("-1/6"))
        }
    }
}