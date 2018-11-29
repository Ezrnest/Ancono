package cn.timelives.java.math.algebra.linearAlgebra.space

import cn.timelives.java.math.*
import cn.timelives.java.math.algebra.linearAlgebra.*
import cn.timelives.java.math.geometry.analytic.AbstractCoordinateSystem
import cn.timelives.java.math.geometry.analytic.CoordinateSystem
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter
import java.util.function.Function


/**
 * Describes n-dimension linear space. The system is composed
 * of an origin vector([originVector]) and a vector base([vectorBase]), whose dimension(size) is equal to
 * [standardDimension]. The [dimension] of this coordinate system is equal to
 * the number of base vectors in the [vectorBase].
 */
interface ILinearSpace<T : Any> : CoordinateSystem<T> {
    val vectorBase: IVectorBase<T>

    val originVector: Vector<T>

    /**
     * The dimension of the linear space, which is equal to the number of base vectors
     */
    override val dimension: Int
        get() = vectorBase.rank

    /**
     * The length of the vectors in this space.
     */
    override val standardDimension: Int
        get() = vectorBase.vectorDimension

    /**
     * Any vector whose size is equal to [dimension] is valid.
     */
    override fun isValidCord(v: Vector<T>): Boolean {
        return v.size == dimension
    }

    override fun contains(v: Vector<T>): Boolean {
        val local = v - originVector
        return vectorBase.canReduce(local)
    }

    override fun fromStandardCord(v: Vector<T>): Vector<T> {
        val local = v - originVector
        return vectorBase.reduce(local)
    }

    override fun toStandardCord(v: Vector<T>): Vector<T> {
        return vectorBase.produce(v) + originVector
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

@Suppress("CanBePrimaryConstructorProperty")
abstract class LinearSpace<T : Any>(mc: MathCalculator<T>,
                                    originVector: Vector<T>,
                                    vectorBase: VectorBase<T>) : AbstractCoordinateSystem<T>(mc), ILinearSpace<T> {
    override val vectorBase: VectorBase<T> = vectorBase
    override val originVector: Vector<T> = originVector

    init {
        require(originVector.size == vectorBase.vectorDimension)
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

    override fun intersect(s: ILinearSpace<T>): ILinearSpace<T>? {
        require(this.standardDimension == s.standardDimension) { "The standard dimension must be the same!" }

        val list = ArrayList<Vector<T>>(this.dimension + s.dimension + 1)
        list.addAll(this.vectorBase.vectors)
        list.addAll(s.vectorBase.vectors)
        list.add( s.originVector - this.originVector)
        val mat = Matrix.fromVectors(false, list)
        val solution = MatrixSup.solveLinearEquation(mat)
        if (solution.solutionSituation== LinearEquationSolution.Situation.NO_SOLUTION) {
            //no intersection
            return null
        }
        val v = solution.specialSolution
        val vs = vectorBase.vectors
        var ori =  this.originVector
        for( i in vs.indices){
            ori += vs[i] * v[i]
        }
        if(solution.baseSolutions == null){
            return singlePoint(ori)
        }
        val nBases = solution.baseSolutions.map {
            var base = vs[0] * it[0]
            for(i in 1..vs.lastIndex){
                base += vs[i] * it[i]
            }
            base
        }
        return valueOf(ori, nBases)
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String = buildString {
        append("{$originVector;")
        vectorBase.vectors.joinTo(this)
        append("}")
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is LinearSpace) {
            return false
        }
        return dimension == obj.dimension && standardDimension == obj.standardDimension
                && originVector.valueEquals(obj.originVector) && vectorBase.valueEquals(obj.vectorBase)
    }

    override fun <N : Any> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean {
        if (obj !is LinearSpace) {
            return false
        }
        return dimension == obj.dimension && standardDimension == obj.standardDimension
                && originVector.valueEquals(obj.originVector, mapper) && vectorBase.valueEquals(obj.vectorBase, mapper)
    }

    abstract override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): LinearSpace<N>


    companion object {
        /**
         * Creates a new linear space from the given origin vector and vector base.
         */
        @JvmStatic
        fun <T : Any> valueOf(originVector: Vector<T>, vectorBase: VectorBase<T>): LinearSpace<T> {
            require(originVector.size == vectorBase.vectorDimension)
            val mc = originVector.mathCalculator
            return DLinearSpace(mc, originVector, vectorBase)
        }

        /**
         * Creates a new linear space from the given origin vector and base vectors.
         */
        @JvmStatic
        fun <T : Any> valueOf(originVector: Vector<T>, vararg baseVectors: Vector<T>): LinearSpace<T> = valueOf(originVector, VectorBase.createBase(*baseVectors))

        /**
         * Creates a new linear space from the given origin vector and base vectors.
         */
        @JvmStatic
        fun <T : Any> valueOf(originVector: Vector<T>, baseVectors: List<Vector<T>>): LinearSpace<T> =
                valueOf(originVector, VectorBase.createBase(baseVectors))

        /**
         * Creates a linear space which only contains a single point. The linear space returned
         * may not support some operations.
         */
        @JvmStatic
        fun <T : Any> singlePoint(originVector: Vector<T>): LinearSpace<T> {
            return valueOf(originVector, VectorBase.zeroBase(originVector.size, originVector.mathCalculator))
        }

    }
}

/**
 * Returns a linear space whose origin vector is a zero vector and the vector base this `this`.
 */
fun <T : Any> VectorBase<T>.toLinearSapce(): LinearSpace<T> {
    return LinearSpace.valueOf(Vector.zeroVector(vectorDimension, mathCalculator), this)
}

internal class DLinearSpace<T : Any>(mc: MathCalculator<T>, originVector: Vector<T>,
                                     vectorBase: VectorBase<T>) : LinearSpace<T>(mc, originVector, vectorBase) {

    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): DLinearSpace<N> {
        return DLinearSpace(newCalculator, originVector.mapTo(mapper, newCalculator), vectorBase.mapTo(mapper, newCalculator))
    }

}


