package cn.ancono.math.algebra.linear.space

import cn.ancono.math.*
import cn.ancono.math.algebra.linear.*
import cn.ancono.math.geometry.analytic.AbstractCoordinateSystem
import cn.ancono.math.geometry.analytic.CoordinateSystem
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import java.util.function.Function


/**
 * Describes n-dimension linear space. The system is composed
 * of an origin vector([originVector]) and a vector base([vectorBasis]), whose dimension(size) is equal to
 * [standardDimension]. The [dimension] of this coordinate system is equal to
 * the number of base vectors in the [vectorBasis].
 */
interface ILinearSpace<T : Any> : CoordinateSystem<T> {
    val vectorBasis: IVectorBasis<T>

    val originVector: Vector<T>

    /**
     * The dimension of the linear space, which is equal to the number of base vectors
     */
    override val dimension: Int
        get() = vectorBasis.rank

    /**
     * The length of the vectors in this space.
     */
    override val standardDimension: Int
        get() = vectorBasis.vectorLength

    /**
     * Any vector whose size is equal to [dimension] is valid.
     */
    override fun isValidCord(v: Vector<T>): Boolean {
        return v.size == dimension
    }

    override fun contains(v: Vector<T>): Boolean {
        val local = v - originVector
        return vectorBasis.canReduce(local)
    }

    override fun fromStandardCord(v: Vector<T>): Vector<T> {
        val local = v - originVector
        return vectorBasis.reduce(local)
    }

    override fun toStandardCord(v: Vector<T>): Vector<T> {
        return vectorBasis.produce(v) + originVector
    }

    /**
     * Computes the inner product of the two vectors in the vector base.
     */
    fun innerProduce(v1: Vector<T>, v2: Vector<T>): T {
        return toStandardCord(v1).innerProduct(toStandardCord(v2))
    }

    /**
     * Returns the intersection of `this` and `s`, or `null` if the intersection is empty.
     */
    fun intersect(s: ILinearSpace<T>): ILinearSpace<T>?

}

/**
 * Describes an affine space
 */
@Suppress("CanBePrimaryConstructorProperty")
abstract class AffineSpace<T : Any>(mc: MathCalculator<T>,
                                    originVector: Vector<T>,
                                    vectorBase: VectorBasis<T>) : AbstractCoordinateSystem<T>(mc), ILinearSpace<T> {
    override val vectorBasis: VectorBasis<T> = vectorBase
    override val originVector: Vector<T> = originVector

    init {
        require(originVector.size == vectorBase.vectorLength)
    }

    override fun isValidCord(v: Vector<T>): Boolean {
        return super.isValidCord(v)
    }

    override fun contains(v: Vector<T>): Boolean {
        return super.contains(v)
    }

    override fun fromStandardCord(v: Vector<T>): Vector<T> {
        return super.fromStandardCord(v)
    }

    override fun toStandardCord(v: Vector<T>): Vector<T> {
        return super.toStandardCord(v)
    }

    override fun innerProduce(v1: Vector<T>, v2: Vector<T>): T {
        return super.innerProduce(v1, v2)
    }

    override fun intersect(s: ILinearSpace<T>): AffineSpace<T>? {
        require(this.standardDimension == s.standardDimension) { "The standard dimension must be the same!" }

        val list = ArrayList<Vector<T>>(this.dimension + s.dimension + 1)
        list.addAll(this.vectorBasis.vectors)
        list.addAll(s.vectorBasis.vectors)
        list.add(s.originVector - this.originVector)
        val mat = Matrix.fromVectors(false, list)
        val solution = MatrixSup.solveLinearEquation(mat)
        if (solution.solutionSituation == LinearEquationSolution.Situation.NO_SOLUTION) {
            //no intersection
            return null
        }
        val v = solution.specialSolution
        val vs = vectorBasis.vectors
        var ori = this.originVector
        for (i in vs.indices) {
            ori += vs[i] * v[i]
        }
        if (solution.baseSolutions == null) {
            return singlePoint(ori)
        }
        val nBases = solution.baseSolutions.map {
            var base = vs[0] * it[0]
            for (i in 1..vs.lastIndex) {
                base += vs[i] * it[i]
            }
            base
        }
        return valueOf(ori, nBases)
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String = buildString {
        append("{$originVector;")
        vectorBasis.vectors.joinTo(this)
        append("}")
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is AffineSpace) {
            return false
        }
        return dimension == obj.dimension && standardDimension == obj.standardDimension &&
                originVector.valueEquals(obj.originVector) && vectorBasis.valueEquals(obj.vectorBasis)
    }

    override fun <N : Any> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean {
        if (obj !is AffineSpace) {
            return false
        }
        return dimension == obj.dimension && standardDimension == obj.standardDimension
                && originVector.valueEquals(obj.originVector, mapper) && vectorBasis.valueEquals(obj.vectorBasis, mapper)
    }

    abstract override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): AffineSpace<N>


    companion object {
        /**
         * Creates a new linear space from the given origin vector and vector base.
         */
        @JvmStatic
        fun <T : Any> valueOf(originVector: Vector<T>, vectorBase: VectorBasis<T>): AffineSpace<T> {
            require(originVector.size == vectorBase.vectorLength)
            val mc = originVector.mathCalculator
            return DAffineSpace(mc, originVector.toColumnVector(), vectorBase)
        }

        /**
         * Creates a new linear space from the given origin vector and base vectors.
         */
        @JvmStatic
        fun <T : Any> valueOf(originVector: Vector<T>, vararg baseVectors: Vector<T>): AffineSpace<T> = valueOf(originVector, baseVectors.asList())

        /**
         * Creates a new linear space from the given origin vector and base vectors.
         */
        @JvmStatic
        fun <T : Any> valueOf(originVector: Vector<T>, baseVectors: List<Vector<T>>): AffineSpace<T> {
            if (baseVectors.isEmpty()) {
                return singlePoint(originVector)
            }
            return valueOf(originVector, VectorBasis.createBase(baseVectors))
        }

        /**
         * Creates a linear space which only contains a single point. The linear space returned
         * may not support some operations.
         */
        @JvmStatic
        fun <T : Any> singlePoint(originVector: Vector<T>): AffineSpace<T> {
            return valueOf(originVector.toColumnVector(), VectorBasis.zeroBase(originVector.size, originVector.mathCalculator))
        }

    }
}

/**
 * Returns a linear space whose origin vector is a zero vector and the vector base this `this`.
 */
fun <T : Any> VectorBasis<T>.toAffineSpace(): AffineSpace<T> {
    return AffineSpace.valueOf(Vector.zeroVector(vectorLength, mathCalculator), this)
}

internal class DAffineSpace<T : Any>(mc: MathCalculator<T>, originVector: Vector<T>,
                                     vectorBase: VectorBasis<T>) : AffineSpace<T>(mc, originVector, vectorBase) {

    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): DAffineSpace<N> {
        return DAffineSpace(
            newCalculator, originVector.mapTo(newCalculator, mapper), vectorBasis.mapTo(
                newCalculator,
                mapper
            )
        )
    }

}

