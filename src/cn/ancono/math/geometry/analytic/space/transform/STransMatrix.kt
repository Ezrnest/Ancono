package cn.ancono.math.geometry.analytic.space.transform

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObject
import cn.ancono.math.MathObjectExtend
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.geometry.analytic.space.SPoint
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import cn.ancono.utilities.StringSup
import java.util.function.Function

/**
 * A space transformation matrix is a 4*4 matrix.
 * mat = <pre>
 *     a11 a12 a13 t1
 *     a21 a22 a23 t2
 *     a31 a32 a33 t3
 *     px  py  pz  s
 * </pre>
 * (x',y',z', _ ) = mat * (x,y,z,1)T
 */
class STransMatrix<T> internal constructor(mc: MathCalculator<T>, val matrix: Matrix<T>) : MathObjectExtend<T>(mc) {

    val rotateMatrix: SRotateMatrix<T> = SRotateMatrix.valueOf(matrix.subMatrix(0, 0, 2, 2))

    fun transform(p: SPoint<T>): SPoint<T> {
        val v = p.extendVector()
        val nv = Vector.multiplyToVector(matrix, v)
        val k: T = nv[3]
        return SPoint.valueOf(nv[0] / k, nv[1] / k, nv[2] / k, mc)
    }


    override fun <N> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): STransMatrix<N> {
        return STransMatrix(newCalculator, matrix.mapTo(newCalculator, mapper))
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is STransMatrix) {
            return false
        }
        return matrix.valueEquals(obj.matrix)
    }

    override fun <N> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean {
        if (obj !is STransMatrix) {
            return false
        }
        return matrix.valueEquals(obj.matrix, mapper)
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
        return StringSup.formatMatrix(Array(4) { i ->
            Array(4) { j ->
                nf.format(matrix[i, j], mc)
            }
        })
    }

}

/**
 * Returns a vector of (x,y,z,1)T, which is a column vector.
 */
fun <T> SPoint<T>.extendVector(): Vector<T> {
    return Vector.of(mathCalculator, x, y, z, mathCalculator.one)
}