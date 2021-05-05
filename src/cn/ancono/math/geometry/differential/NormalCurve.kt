package cn.ancono.math.geometry.differential

import cn.ancono.math.MathCalculator
import cn.ancono.math.function.*
import cn.ancono.math.geometry.analytic.space.*
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.set.Interval

typealias DVFunction<T> = DerivableFunction<T, SVector<T>>

/**
 *  Describe a normal parametric curve. It is required that the vector function `r(t)` is differentiable of sufficient
 *  order and the derivative `r'(t) != 0`.
 */
abstract class NormalCurve<T>(override val calculator: MathCalculator<T>) : SpaceParametricCurve<T>, DerivableFunction<T, SVector<T>> {


    override fun asPointFunction() = this.andThenMap { SPoint.valueOf(it) }

    override fun asFunctionX(): DerivableSVFunction<T> = this.andThenMap { it.x }.asSVFunction()
    override fun asFunctionY(): DerivableSVFunction<T> = this.andThenMap { it.y }.asSVFunction()
    override fun asFunctionZ(): DerivableSVFunction<T> = this.andThenMap { it.z }.asSVFunction()

    /**
     * Returns a closed interval that indicates the domain of this parametric curve
     */
//    @JvmDefault
    abstract override fun domain(): Interval<T>

    /**
     * Returns `ds/dt` of this parametric curve. `ds/dt = |r'(t)|`
     */
    open val ds: DerivableSVFunction<T> by lazy {
        DifferentialUtil.length(calculator, derivative)
    }
//        get() = MathFunction.composeSV(this.derive(), { x -> x.calLength() })


    /**
     *  Returns the curvature(as a function) of this curve.
     */
    open val curvature: DerivableSVFunction<T> by lazy {
        // k(t) = |r' × r''| / |r'|^3
        val r1 = derivative
        val r2 = derivative.derivative
        val nume = DifferentialUtil.length(calculator, DifferentialUtil.outerProduct(r1, r2))
        val deno = DerivableFunction.composeSV(ds, AbstractSVFunction.pow(Fraction.of(3L), calculator), calculator)
        DerivableFunction.divideSV(nume, deno, calculator)
    }

    val torsion: DerivableSVFunction<T> by lazy {
        val r1 = derivative
        val r2 = r1.derivative
        val r3 = r2.derivative
        val nume = DifferentialUtil.mixedProduct(r1, r2, r3, calculator)
        val w = DifferentialUtil.outerProduct(r1, r2)
        val deno = DifferentialUtil.innerProduct(calculator, w, w)
        DerivableFunction.divideSV(nume, deno, calculator)
    }

    /**
     * The tangent vector of this normal curve as a vector function.
     * The vector returned this not a unit vector.
     * @see alpha
     */
    open val tangentVector: DVFunction<T>
        get() = derivative

    /**
     * The main normal vector of this normal curve as a vector function.
     * The vector returned this not a unit vector.
     * @see beta
     */
    open val mainNormalVector: DVFunction<T>
        get() = alpha.derivative
//        get() = this.derive().derive()

    /**
     * The minor normal vector of this normal curve as a vector function.
     * The vector returned this not a unit vector.
     * @see gamma
     */
    open val minorNormalVector: DVFunction<T> by lazy {
        DifferentialUtil.outerProduct(tangentVector, mainNormalVector)
    }


    /**
     * `alpha(t)`,which is a unit vector parallel to tangent vector.
     */
    open val alpha: DVFunction<T> by lazy {
        DifferentialUtil.unitVectorSpace(calculator, tangentVector)
    }

    /**
     * `beta(t)`,which is a unit vector parallel to main normal vector.
     */
    open val beta: DVFunction<T> by lazy {
        DifferentialUtil.unitVectorSpace(calculator, mainNormalVector)
    }
//        get() = MathFunctionSup.mergeOf(alpha, gamma) { a, b -> a.outerProduct(b) }

    /**
     * `gamma(t)`, which is a unit vector parallel to minor normal vector.
     */
    open val gamma: DVFunction<T> by lazy {
        DifferentialUtil.outerProduct(alpha, beta)
    }
//        get() {
//            val d = this.derive()
//            val dd = d.derive()
//            return MathFunction { t ->
//                d(t).outerProduct(dd(t)).unitVector()
//            }
//        }

    companion object {
        fun <T> fromFunctionXYZ(a: DerivableSVFunction<T>,
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
fun <T> NormalCurve<T>.tangentLine(t: T): Line<T> {
    val thisD = this.derive()
    val p = this(t).asPoint()
    val v = thisD(t)
    return Line.pointDirect(p, v)
}

/**
 * Returns the main normal line of this curve on the point of parametric value [t].
 * The direct vector is `beta(t)`.
 */
fun <T> NormalCurve<T>.mainNormalLine(t: T): Line<T> {
    val p = this(t).asPoint()
    val v = beta(t)
    return Line.pointDirect(p, v)
}

/**
 * Returns the minor normal line of this curve on the point of parametric value [t].
 * The direct vector is `gamma(t)`.
 */
fun <T> NormalCurve<T>.minorNormalLine(t: T): Line<T> {
    val p = this(t).asPoint()
    val v = gamma(t)
    return Line.pointDirect(p, v)
}

/**
 * Returns the normal plane of this curve on the point of parametric value [t].
 * The normal vector of the plane is `alpha(t)`
 */
fun <T> NormalCurve<T>.normalPlane(t: T): Plane<T> {
    val p = this(t).asPoint()
    val v = alpha(t)
    return Plane.pointNormalVector(p, v)
}

/**
 * Returns the rectifying plane of this curve on the point of parametric value [t].
 * The normal vector of the plane is `beta(t)`
 */
fun <T> NormalCurve<T>.rectifyingPlane(t: T): Plane<T> {
    val p = this(t).asPoint()
    val v = beta(t)
    return Plane.pointNormalVector(p, v)
}

/**
 * Returns the osculating plane of this curve on the point of parametric value [t].
 * The normal vector of the plane is `gamma(t)`
 */
fun <T> NormalCurve<T>.osculatingPlane(t: T): Plane<T> {
    val p = this(t).asPoint()
    val v = gamma(t)
    return Plane.pointNormalVector(p, v)
}

fun <T> NormalCurve<T>.arcLength(integralHelper: (SVFunction<T>, T, T) -> T): T {
    val domain = domain()
    return integralHelper(this.ds, domain.downerBound(), domain.upperBound())
}

/**
 * Performs a parametric transformation to this normal curve. It is required that the [tu] is a derivable
 * function and the derivative of [tu] must be all non-zero.
 */
fun <T> NormalCurve<T>.parametricTrans(tu: DerivableSVFunction<T>, newDomain: Interval<T>): NormalCurve<T> {
    val mc = calculator
    val origin = this
    return object : NormalCurve<T>(mc) {
        override fun domain(): Interval<T> {
            return newDomain
        }

        override fun substitute(t: T): SVector<T> {
            return origin.substitute(tu.apply(t))
        }

        override val derivative: DerivableFunction<T, SVector<T>> by lazy {
            DerivableFunction.compose(tu, origin, { a, b -> a.add(b) }, { k, v -> v.multiply(k) })
        }

    }
}

fun <T> NormalCurve<T>.frenetCoordSystem(t: T): SpaceAffineCoordinateSystem<T> {
    val o = this.substituteAsPoint(t)
    val e1 = alpha(t)
    val e2 = beta(t)
    val e3 = gamma(t)
    return SpaceAffineCoordinateSystem.valueOf(o, e1, e2, e3)
}



