package cn.ancono.math.geometry.projective

import cn.ancono.math.AbstractMathObject
import cn.ancono.math.MathObject
import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.function.Bijection
import cn.ancono.math.geometry.analytic.plane.TransMatrix
import cn.ancono.math.numberModels.api.MulGroupNumberModel
import cn.ancono.math.numberModels.api.NumberFormatter
import cn.ancono.math.numberModels.api.RealCalculator
import cn.ancono.math.property.Composable
import org.jetbrains.annotations.NotNull
import java.util.function.Function


/**
 * Describes the linear fractional transformation on field T.
 *
 */
class LinearFracTrans<T> internal constructor(private val m: TransMatrix<T>)
    : AbstractMathObject<T, FieldCalculator<T>>(m.calculator as FieldCalculator<T>),
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
        return calculator.eval {
            divide(
                    m[0, 0] * x + m[1, 0],
                    m[0, 1] * x + m[1, 1]
            )
        }
    }

    override fun deply(y: @NotNull T): T {
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
        return LinearFracTrans(m.inverse())
    }


    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): LinearFracTrans<N> {
        return LinearFracTrans(m.mapTo(newCalculator, mapper))
    }

    override fun valueEquals(obj: MathObject<T, FieldCalculator<T>>): Boolean {
        if (obj !is LinearFracTrans) {
            return false
        }
        return m.valueEquals(obj.m)
    }

    override fun toString(nf: NumberFormatter<T>): String {
        return "f(x)=( (${nf.format(m[0, 0])})x + ${nf.format(m[0, 1])} ) /" +
                "( (${nf.format(m[1, 0])})x + ${nf.format(m[1, 1])} )"
    }

    companion object {


        fun <T> of(a: T, b: T, c: T, d: T, mc: RealCalculator<T>): LinearFracTrans<T> {
            val det = mc.eval { a * c - b * d }
            if (mc.isZero(det)) {
                throw IllegalArgumentException("ac-bd = 0")
            }
            return LinearFracTrans(TransMatrix.valueOf(a, b, c, d, mc))
        }

    }

}