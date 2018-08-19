package cn.timelives.java.math.geometry.analytic.spaceAG.curve

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.function.*
import cn.timelives.java.math.geometry.analytic.spaceAG.Line
import cn.timelives.java.math.geometry.analytic.spaceAG.SPoint
import cn.timelives.java.math.geometry.analytic.spaceAG.SVector
import cn.timelives.java.math.set.Interval

/**
 *
 */
interface NormalCurve<T : Any> : SpaceParametricCurve<T>, DerivableFunction<T, SVector<T>> {


    override fun asPointFunction() = this.andThenMap { SPoint.valueOf(it) }

    override fun asFunctionX(): DerivableSVFunction<T> = this.andThenMap { it.x }.asSVFunction()
    override fun asFunctionY(): DerivableSVFunction<T> = this.andThenMap { it.y }.asSVFunction()
    override fun asFunctionZ(): DerivableSVFunction<T> = this.andThenMap { it.z }.asSVFunction()

    /**
     * Returns a closed interval that indicates the domain of this parametric curve
     */
    override fun domain(): Interval<T>

    /**
     * Returns `ds` of this parametric curve. `ds = |r'(t)|dt`
     */
    val ds: SVFunction<T>
        get() = MathFunction.composeSV(this.derive(), MathFunction<SVector<T>, T> { x -> x.calLength() })

    val alpha: VectorFunction<T>
        get() = MathFunction.compose(this.derive(), MathFunction<SVector<T>, SVector<T>> { x -> x.unitVector() })

    val curvature: SVFunction<T>
        get() = MathFunction.composeSV(curvatureVector, MathFunction<SVector<T>, T> { x -> x.calLength() })

    val curvatureVector: DerivableFunction<T, SVector<T>>
        get() = this.derive().derive()

    val mainNormalVector: VectorFunction<T>
        get() = MathFunction.compose(curvatureVector, MathFunction<SVector<T>, SVector<T>> { x -> x.unitVector() })

    val minorNormalVector: VectorFunction<T>
        get() = MathFunctionSup.mergeOf(alpha, mainNormalVector) { a, b -> a.outerProduct(b) }

    companion object {
        fun <T : Any> fromFunctionXYZ(a: DerivableSVFunction<T>,
                                      b: DerivableSVFunction<T>,
                                      c: DerivableSVFunction<T>,
                                      domain: Interval<T>,
                                      mc: MathCalculator<T>): NormalCurve<T> {
            return NormalCurveComposed(a, b, c, domain, mc)
        }
    }
}

fun <T : Any> NormalCurve<T>.tangentLine(t: T): Line<T> {
    val thisD = this.derive()
    val p = SPoint.valueOf(this(t))
    val v = thisD(t)
    return Line.pointDirect(p, v)
}

fun <T : Any> NormalCurve<T>.arcLength(integralHelper: (SVFunction<T>, T, T) -> T): T {
    val domain = domain()
    return integralHelper(this.ds, domain.downerBound(), domain.upperBound())
}

/**
 * Performs a parametric transformation to this normal curve. It is required that the [tu] is a derivable
 * function and the derivative of [tu] must be all non-zero.
 */
fun <T : Any> NormalCurve<T>.parametricTrans(tu: DerivableSVFunction<T>, newDomain: Interval<T>): NormalCurve<T> {
    val mc = mathCalculator
    val origin = this
    return object : NormalCurve<T> {
        override fun domain(): Interval<T> {
            return newDomain
        }

        override fun substitute(t: T): SVector<T> {
            return origin.substitute(tu.apply(t))
        }

        override val mathCalculator: MathCalculator<T> = mc

        override fun derive(): DerivableFunction<T, SVector<T>> {
            return DerivableFunction.compose(tu, origin, { a, b -> a.add(b) }, { k, v -> v.multiplyNumber(k) })
        }
    }
}

