package cn.ancono.math.geometry.projective

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObject
import cn.ancono.math.MathObjectExtend
import cn.ancono.math.algebra.abstractAlgebra.calculator.eval
import cn.ancono.math.function.Bijection
import cn.ancono.math.geometry.analytic.planeAG.TransMatrix
import cn.ancono.math.get
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import cn.ancono.math.numberModels.api.MulGroupNumberModel
import cn.ancono.math.numberModels.api.times
import cn.ancono.math.property.Composable
import java.util.function.Function


/**
 * Describes the linear fractional transformation on field T.
 *
 */
class LinearFracTrans<T : Any> internal constructor(private val m: TransMatrix<T>)
    : MathObjectExtend<T>(m.mathCalculator),
        Bijection<T, T>,
        Composable<LinearFracTrans<T>>,
        MulGroupNumberModel<LinearFracTrans<T>> {

    /**
     * Gets the matrix representation of the linear fractional transformation. The determinant of the matrix
     * is not zero.
     *
     */
    val matrix: TransMatrix<T>
        get() = m

    override fun apply(x: T): T {
        return mc.eval {
            divide(
                    m[0, 0] * x + m[1, 0],
                    m[0, 1] * x + m[1, 1]
            )
        }
    }

    override fun deply(y: T): T {
        return reciprocal().apply(y)
    }

    override fun compose(before: LinearFracTrans<T>): LinearFracTrans<T> {
        return this.multiply(before)
    }

    override fun andThen(after: LinearFracTrans<T>): LinearFracTrans<T> {
        return after.multiply(this)
    }

    override fun multiply(y: LinearFracTrans<T>): LinearFracTrans<T> {
        return LinearFracTrans(this.m * y.m)
    }

    override fun reciprocal(): LinearFracTrans<T> {
        return LinearFracTrans(m.reciprocal())
    }


    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): LinearFracTrans<N> {
        return LinearFracTrans(m.mapTo(mapper, newCalculator))
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is LinearFracTrans) {
            return false
        }
        return m.valueEquals(obj.m)
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
        return "f(x)=( (${nf.format(m[0, 0], mc)})x + ${nf.format(m[0, 1], mc)} ) /" +
                "( (${nf.format(m[1, 0], mc)})x + ${nf.format(m[1, 1], mc)} )"
    }

    companion object {


        fun <T : Any> of(a: T, b: T, c: T, d: T, mc: MathCalculator<T>): LinearFracTrans<T> {
            val det = mc.eval { a * c - b * d }
            if (mc.isZero(det)) {
                throw IllegalArgumentException("ac-bd = 0")
            }
            return LinearFracTrans(TransMatrix.valueOf(a, b, c, d, mc))
        }

    }

}