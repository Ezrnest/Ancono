package cn.ancono.math.numeric

import java.util.function.DoubleUnaryOperator


/*
 * Created by liyicheng at 2021-03-30 22:20
 */

/**
 * Contains some numerical integration methods.
 */
object Integrations {

    fun simpsonMethod(f: (Double) -> Double, a: Double, b: Double, n: Int = 100): Double {
        require(a < b)
        val delta = (b - a) / n
        var result = 0.0
        var x = a
        for (k in 0 until n) {
            val x1 = x
            val x2 = x + delta
            val m = (x1 + x2) / 2
            result += f(x1) + 4 * f(m) + f(x2)
            x = x2
        }
        return result * delta / 6
    }

//    fun
}