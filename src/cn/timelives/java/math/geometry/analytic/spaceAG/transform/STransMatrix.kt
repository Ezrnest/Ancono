package cn.timelives.java.math.geometry.analytic.spaceAG.transform

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.MathObject
import cn.timelives.java.math.MathObjectExtend
import cn.timelives.java.math.algebra.linearAlgebra.Matrix
import cn.timelives.java.math.algebra.linearAlgebra.MatrixSup
import cn.timelives.java.math.algebra.linearAlgebra.Vector
import cn.timelives.java.math.function.MathFunction
import cn.timelives.java.math.geometry.analytic.spaceAG.SPoint
import cn.timelives.java.math.get
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter
import cn.timelives.java.utilities.ArraySup
import cn.timelives.java.utilities.Printer
import cn.timelives.java.utilities.StringSup
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
class STransMatrix<T : Any> internal constructor(mc: MathCalculator<T>, val matrix: Matrix<T>) : MathObjectExtend<T>(mc) {

    val rotateMatrix: SRotateMatrix<T> = SRotateMatrix.valueOf(matrix.subMatrix(0, 0, 2, 2))

    fun transform(p: SPoint<T>): SPoint<T> {
        val v = p.extendVector()
        val nv = Vector.multiplyToVector(matrix, v)
        val k: T = nv[3]
        return SPoint.valueOf(nv[0] / k, nv[1] / k, nv[2] / k, mc)
    }


    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): STransMatrix<N> {
        return STransMatrix(newCalculator, matrix.mapTo(mapper, newCalculator))
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is STransMatrix) {
            return false
        }
        return matrix.valueEquals(obj.matrix)
    }

    override fun <N : Any> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean {
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
fun <T : Any> SPoint<T>.extendVector(): Vector<T> {
    return Vector.createVector(mathCalculator, x, y, z, mathCalculator.one)
}