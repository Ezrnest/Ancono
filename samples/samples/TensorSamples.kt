package samples

import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.Tensor
import cn.ancono.math.numberModels.api.shapeString
import cn.ancono.math.numberModels.get
import cn.ancono.utilities.IterUtils

object TensorSamples {

    fun tensorDot() {
        val mc = Calculators.integer()
        val a = Tensor.of(intArrayOf(3, 4, 5), mc, 0 until 60)
        val b = Tensor.of(intArrayOf(4, 3, 2), mc, 0 until 24)
        val c = a.permute(2, 1, 0).matmul(b, 2)
        println(c.shapeString)
        println(c)

        val d = Tensor.zeros(mc, 5, 2)
        for ((i, j, k, n) in IterUtils.prodIdxN(intArrayOf(5, 2, 3, 4))) {
            d[i, j] += a[k, n, i] * b[n, k, j]
        }
        println(c.valueEquals(d))

    }
}

fun main() {
    TensorSamples.tensorDot()
}