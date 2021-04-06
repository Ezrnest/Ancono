package cn.ancono.math.numeric

import cn.ancono.math.MathCalculator
import cn.ancono.math.algebra.abs.calculator.eval


/*
 * Created by liyicheng at 2021-03-30 22:20
 */


object Interpolations {

    /**
     * Returns a list of i-order differences. Denote it as `L`,
     * then
     *
     *     L[0,j] = fs[j]
     *     L[i,j] = (L[i-1,j+1] - L[i-1,j])/(xs[j+r] - xs[j])
     */
    fun <T : Any> differences(xs: List<T>, fs: List<T>, k: Int, mc: MathCalculator<T>): List<List<T>> {
        val n = xs.size
        require(fs.size == n)
        val results = ArrayList<List<T>>(k + 1)
        results.add(fs)
        for (i in 1..k) {
            val prev = results[i - 1]
            val d = ArrayList<T>(n - i)
            for (j in 0 until (n - i)) {
                d += mc.eval { (prev[j + 1] - prev[j]) / (xs[j + i] - xs[j]) }
            }
            results += d
        }
        return results
    }

}