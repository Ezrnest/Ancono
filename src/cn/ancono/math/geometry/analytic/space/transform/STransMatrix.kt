package cn.ancono.math.geometry.analytic.space.transform

import cn.ancono.math.AbstractMathObject
import cn.ancono.math.IMathObject
import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.geometry.analytic.space.SPoint
import cn.ancono.math.numberModels.api.NumberFormatter
import cn.ancono.math.numberModels.api.RealCalculator
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
class STransMatrix<T> internal constructor(mc: FieldCalculator<T>, val matrix: Matrix<T>)
    : AbstractMathObject<T, FieldCalculator<T>>(mc) {

    val rotateMatrix: SRotateMatrix<T> = SRotateMatrix.valueOf(matrix.subMatrix(0, 0, 3, 3))

    fun transform(p: SPoint<T>): SPoint<T> {
        val v = p.extendVector()
        val nv = Vector.multiplyToVector(matrix, v)
        val k: T = nv[3]
        return calculator.eval {
            SPoint.valueOf(nv[0] / k, nv[1] / k, nv[2] / k, this as RealCalculator<T>)  //TODO fix
        }
    }


    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): STransMatrix<N> {
        return STransMatrix(newCalculator as FieldCalculator, matrix.mapTo(newCalculator, mapper))
    }

    override fun valueEquals(obj: IMathObject<T>): Boolean {
        if (obj !is STransMatrix) {
            return false
        }
        return matrix.valueEquals(obj.matrix)
    }

    override fun toString(nf: NumberFormatter<T>): String {
        return StringSup.formatMatrix(Array(4) { i ->
            Array(4) { j ->
                nf.format(matrix[i, j])
            }
        })
    }

}

/**
 * Returns a vector of (x,y,z,1)T, which is a column vector.
 */
fun <T> SPoint<T>.extendVector(): Vector<T> {
    return Vector.of(calculator, x, y, z, calculator.one)
}