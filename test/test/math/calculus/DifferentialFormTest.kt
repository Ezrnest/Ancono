package test.math.calculus

import cn.ancono.math.calculus.DifferentialForm
import cn.ancono.math.numberModels.expression.ExprCalculator
import org.junit.Test

class DifferentialFormTest {
    @Test
    fun testWedge() {
        val ec = ExprCalculator.instance
        val one = ec.one
//        val mc = DifferentialForm
        val f = DifferentialForm.of(ec.parse("x^2"), "x", "y", "z")
        val g = DifferentialForm.of(one, "a", "b", "c")
        println(f)
        println(f.differential("x"))
    }
}