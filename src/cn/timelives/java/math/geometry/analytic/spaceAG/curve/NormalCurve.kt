package cn.timelives.java.math.geometry.analytic.spaceAG.curve

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.function.*
import cn.timelives.java.math.geometry.analytic.spaceAG.*
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



    val curvature: SVFunction<T>
        get() {
            val d = this.derive()
            val dd = d.derive()
            return SVFunction { t ->
                val nume = d(t).outerProduct(dd(t)).calLength()
                val deno = mathCalculator.pow(d(t).calLength(), 3L)
                mathCalculator.divide(nume, deno)
            }
        }

//    val torsion : VectorFunction<T>
//        get(){
//
//        }

    /**
     * The tangent vector of this normal curve as a vector function.
     * The vector returned this not a unit vector.
     * @see alpha
     */
    val tangentVector: VectorFunction<T>
        get() = this.derive()

    /**
     * The main normal vector of this normal curve as a vector function.
     * The vector returned this not a unit vector.
     * @see beta
     */
    val mainNormalVector: VectorFunction<T>
        get() = this.derive().derive()

    /**
     * The minor normal vector of this normal curve as a vector function.
     * The vector returned this not a unit vector.
     * @see gamma
     */
    val minorNormalVector: VectorFunction<T>
        get() = MathFunctionSup.mergeOf(tangentVector, mainNormalVector) { a, b -> a.outerProduct(b) }

    /**
     * `alpha(t)`,which is a unit vector parallel to tangent vector.
     */
    val alpha: VectorFunction<T>
        get() = MathFunction.andThen(tangentVector, MathFunction<SVector<T>, SVector<T>> { x -> x.unitVector() })

    /**
     * `beta(t)`,which is a unit vector parallel to main normal vector.
     */
    val beta: VectorFunction<T>
        get() = MathFunctionSup.mergeOf(alpha, gamma) { a, b -> a.outerProduct(b) }

    /**
     * `gamma(t)`,which is a unit vector parallel to minor normal vector.
     */
    val gamma: VectorFunction<T>
        get() {
            val d = this.derive()
            val dd = d.derive()
            return MathFunction { t ->
                d(t).outerProduct(dd(t)).unitVector()
            }
        }

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

/**
 * Returns the tangent line of this curve on the point of parametric value [t].
 * The direct vector is `alpha(t)`.
 */
fun <T : Any> NormalCurve<T>.tangentLine(t: T): Line<T> {
    val thisD = this.derive()
    val p = this(t).asPoint()
    val v = thisD(t)
    return Line.pointDirect(p, v)
}

/**
 * Returns the main normal line of this curve on the point of parametric value [t].
 * The direct vector is `beta(t)`.
 */
fun <T : Any> NormalCurve<T>.mainNormalLine(t: T): Line<T> {
    val p = this(t).asPoint()
    val v = beta(t)
    return Line.pointDirect(p, v)
}

/**
 * Returns the minor normal line of this curve on the point of parametric value [t].
 * The direct vector is `gamma(t)`.
 */
fun <T : Any> NormalCurve<T>.minorNormalLine(t: T): Line<T> {
    val p = this(t).asPoint()
    val v = gamma(t)
    return Line.pointDirect(p, v)
}

/**
 * Returns the normal plane of this curve on the point of parametric value [t].
 * The normal vector of the plane is `alpha(t)`
 */
fun <T : Any> NormalCurve<T>.normalPlane(t: T): Plane<T> {
    val p = this(t).asPoint()
    val v = alpha(t)
    return Plane.pointNormalVector(p, v)
}

/**
 * Returns the rectifying plane of this curve on the point of parametric value [t].
 * The normal vector of the plane is `beta(t)`
 */
fun <T : Any> NormalCurve<T>.rectifyingPlane(t: T): Plane<T> {
    val p = this(t).asPoint()
    val v = beta(t)
    return Plane.pointNormalVector(p, v)
}

/**
 * Returns the osculating plane of this curve on the point of parametric value [t].
 * The normal vector of the plane is `gamma(t)`
 */
fun <T : Any> NormalCurve<T>.osculatingPlane(t: T): Plane<T> {
    val p = this(t).asPoint()
    val v = gamma(t)
    return Plane.pointNormalVector(p, v)
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

fun <T : Any> NormalCurve<T>.frenetCoordSystem(t: T): SpaceAffineCoordinateSystem<T> {
    val o = this.substituteAsPoint(t)
    val e1 = alpha(t)
    val e2 = beta(t)
    val e3 = gamma(t)
    return SpaceAffineCoordinateSystem.valueOf(o, e1, e2, e3)
}



