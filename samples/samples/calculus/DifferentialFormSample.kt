package samples.calculus

import cn.ancono.math.calculus.DifferentialForm
import cn.ancono.math.numberModels.api.times
import cn.ancono.math.numberModels.expression.ExprCalculator
/*
 * Created by lyc at 2020/2/29
 */
fun main() {
    DifferentialFormSample.sample1()
}

object DifferentialFormSample {
    fun sample1() {
        val ec = ExprCalculator.instance
        val vars = listOf("x", "y", "z", "w")
        val w = DifferentialForm.of(ec.parse("xyzw"), ec)
        val v = DifferentialForm.of(ec.parse("x+y+z+w"), ec)
        println(w)
        println(v)
        val w2 = w.differential(vars)
        println(w2)
        val v2 = v.differential(vars)
        println(v2)
        println(v2 * w2)
        println(w2 * v2)
        println(w2.differential(vars))
        println(v2.differential(vars))
    }

    fun sample2() {
        val ec = ExprCalculator.instance
        val w = DifferentialForm.of(ec.one, ec, "w")
        println(w)
    }
}