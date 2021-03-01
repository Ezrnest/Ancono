package cn.ancono.math.numeric

import cn.ancono.math.MathUtils
import java.util.function.DoubleUnaryOperator


/**
 * Common utilities provided for numeric methods.
 *
 * Created by liyicheng at 2020-06-02 19:27
 */
object NumericSup {

    fun linearInterpolate(from: Double, to: Double, data: DoubleArray, d: Double): DoubleUnaryOperator {
        require(from < to)
        require(d > 0)
        return DoubleUnaryOperator { x ->
            if (x < from - d || x > to + d) {
                return@DoubleUnaryOperator 0.0
            }
            val i = ((x - from) / d).toInt()
            val y1 = if (i in data.indices) {
                data[i]
            } else {
                0.0
            }
            val y2 = if ((i + 1) in data.indices) {
                data[i + 1]
            } else {
                0.0
            }
            val x1 = from + i * d
            val k = (x - x1) / d
            MathUtils.interpolate(y1, y2, k)
        }
    }


}