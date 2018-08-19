package cn.timelives.java.math.geometry.analytic.spaceAG.transform

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.MathObject
import cn.timelives.java.math.MathObjectExtend
import cn.timelives.java.math.algebra.linearAlgebra.Matrix
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter
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
class STransMatrix<T : Any> internal constructor(mc: MathCalculator<T>, matrix: Matrix<T>) : MathObjectExtend<T>(mc) {
    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): MathObject<N> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}