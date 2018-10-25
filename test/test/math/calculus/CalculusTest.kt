package test.math.calculus

import cn.timelives.java.math.calculus.Calculus
import cn.timelives.java.math.numberModels.expression.ExprCalculator
import cn.timelives.java.math.numberModels.expression.Expression
import org.junit.Test

class CalculusTest {

    @Test
    fun taylorSeries() {
        val expr = Expression.valueOf("sin(x)")
        val mc = ExprCalculator.newInstance
        val taylor = Calculus.taylorSeries(expr, degree = 3, mc = mc)
        kotlin.test.assertTrue {
            mc.isEqual(taylor.getCoefficient(3), Expression.valueOf("-1/6"))
        }
    }
}