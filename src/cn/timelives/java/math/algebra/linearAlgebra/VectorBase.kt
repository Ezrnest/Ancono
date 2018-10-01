package cn.timelives.java.math.algebra.linearAlgebra

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.MathObject
import cn.timelives.java.math.MathObjectExtend
import cn.timelives.java.math.exceptions.OutOfDomainException
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter
import cn.timelives.java.math.property.Composable
import cn.timelives.java.math.times
import cn.timelives.java.utilities.CollectionSup
import java.util.function.Function


/*
 * Created at 2018/9/10
 * @author liyicheng
 */
/**
 * Describes a vector base. All vectors in the base are column vector.
 */
interface IVectorBase<T : Any> {

    /**
     * The dimension(size) of the base vectors.
     */
    val vectorDimension: Int

    /**
     * The number of base vectors.
     */
    val baseSize: Int
        get() = vectors.size

    /**
     * The base vectors, the size of the list is smaller than or equal to [vectorDimension]
     */
    val vectors: List<Vector<T>>

    /**
     * Returns the matrix
     */
    fun getVectorsAsMatrix(): Matrix<T> {
        return Matrix.fromVectors(false, vectors)
    }

    /**
     * Reduces the given vector, whose size must be equal to [vectorDimension], and
     * returns a column vector whose n-st element represents the coefficient
     * of n-st base vector.
     * @throws OutOfDomainException if the vector cannot be reduced
     */
    fun reduce(v: Vector<T>): Vector<T> {
        requireVectorSize(v)
        val vectors = vectors
        val mat = Matrix.fromVectors(false, *vectors.toTypedArray(), v)
        val result = MatrixSup.solveLinearEquation(mat)
        return result.base ?: throw OutOfDomainException("The Vector cannot be reduced.")
    }

    /**
     * Determines whether the given vector can be reduced by this vector base.
     */
    fun canReduce(v: Vector<T>): Boolean {
        requireVectorSize(v)
        return MatrixSup.determineSolutionType(Matrix.fromVectors(false, *vectors.toTypedArray(), v)) !=
                LinearEquationSolution.Situation.NO_SOLUTION
    }

    /**
     * Returns the vector base after the transformation of a invertible square matrix whose
     * size is [vectorDimension]
     */
    fun transform(mat: Matrix<T>): IVectorBase<T>

    fun transfrom(f: (Vector<T>) -> Vector<T>): IVectorBase<T>

//    /**
//     * Returns the transformation matrix `mat` that `this.transform(mat) == vb`
//     */
//    fun transMatrixTo(vb : VectorBase<T>) : Matrix<T>{
//        require(dimension == vb.dimension)
//        require(baseSize == vb.baseSize)
//
//    }

    /**
     * Converts a vector representing the coordinate in `this` base to absolute base.
     * @param cordInThisBase a vector whose size is [baseSize]
     */
    fun produce(cordInThisBase: Vector<T>): Vector<T> {
        requireVectorSize(baseSize, cordInThisBase)
        @Suppress("UNCHECKED_CAST")
        val result = Array<Any>(baseSize) { i ->
            vectors[i].innerProduct(cordInThisBase)
        } as Array<T>
        return Vector.createVector(cordInThisBase.mathCalculator, *result)
    }

    /**
     * Returns the direct sum of this two vector bases. The direct sum is defined as
     * a vector base whose vectors can be reduced to a linear combination of
     * vectors in `this` and `vb`. In other words, the new vector base has the base vector
     * of a maximum linear-irrelevant set of vectors among vectors in `this` and `vb`.
     *
     * It is required that [vb] has the same [vectorDimension] as `this`.
     * @return a vector base whose [vectorDimension] is `this.vectorDimension`
     */
    fun directSum(vb : IVectorBase<T>) : IVectorBase<T>

    /**
     * Returns the direct product of this two vector bases. The result is a vector base
     * whose [vectorDimension] is the  `this.vectorDimension + vb.vectorDimension`. The
     * base vectors of the result vector base is composed of two parts: vectors with
     * preceding elements that are equal to corresponding elements in base vectors in `this` and
     * succeeding zeros, and vectors with preceding zeros and succeeding elements that are equal to
     * elements in base vectors in `vb` correspondingly.
     *
     * 
     * For example, assume `this` is a vector base of {(1,0),(0,1)}, and `vb` is a vector base of {(1,2)},
     * then the result of direct product is a vector base of {(1,0,0,0),(0,1,0,0),(0,0,1,2)}.
     *  @return a vector base whose [vectorDimension] is `this.vectorDimension + vb.vectorDimension` and
     *  whose [baseSize] is `this.baseSize+vb.baseSize`
     */
    fun directProduct(vb : IVectorBase<T>) : IVectorBase<T>

}

internal fun <T : Any> IVectorBase<T>.requireVectorSize(v: Vector<T>) {
    requireVectorSize(this.vectorDimension, v)
}

internal fun requireVectorSize(size: Int, v: Vector<*>) {
    require(v.size == size) {
        "Vector's size must be $size"
    }
}

/**
 * Determines whether this vector base is unit, which is true if all the vectors
 * in this base is unit vector.
 */
fun <T : Any> IVectorBase<T>.isUnit() = vectors.all { it.isUnitVector }

/**
 * Determines whether this vector base is orthogonal, which is true if each vector
 * in this vector base is perpendicular to all other vectors.
 */
fun <T : Any> IVectorBase<T>.isOrthogonal(): Boolean {
    for (i in vectors.indices) {
        val v = vectors[i]
        for (j in (i + 1) until vectors.size) {
            if (!v.isPerpendicular(vectors[j])) {
                return false
            }
        }
    }
    return true
}

/**
 * Determines whether this vector base is full, that is, whether the dimension of this
 * vector base is equal to the number of base vectors.
 */
fun <T:Any> IVectorBase<T>.isFull() : Boolean = vectorDimension == baseSize

/**
 * Determines whether this vector base can be transformed to a standard vector base by
 * a linear transformation whose determinant is one.
 */
fun <T:Any> IVectorBase<T>.isSimilarStandard() : Boolean = isFull() && isOrthogonal() && isUnit()

/**
 * Converts this vector base to a full vector base. Throws an exception if
 * this vector base is not full.
 */
fun <T:Any> IVectorBase<T>.asFullVectorBase() : FullVectorBase<T>{
    if(!isFull()){
        throw IllegalArgumentException("Not full!")
    }
    if(this is FullVectorBase){
        return this
    }
    return FullVectorBase(vectorDimension,vectors)
}


@Suppress("RedundantOverride")//Provided for Java extension
abstract class VectorBase<T : Any>(mc: MathCalculator<T>) : MathObjectExtend<T>(mc),
        IVectorBase<T>,Composable<VectorBase<T>> {
    override fun getVectorsAsMatrix(): Matrix<T> {
        return super.getVectorsAsMatrix()
    }

    override fun reduce(v: Vector<T>): Vector<T> {
        return super.reduce(v)
    }

    override fun canReduce(v: Vector<T>): Boolean {
        return super.canReduce(v)
    }

    override fun transform(mat: Matrix<T>): VectorBase<T> {
        require(mat.columnCount == vectorDimension) { "The transformation matrix must have the same size of $vectorDimension" }
        require(mat.isInvertible) { "The transformation matrix must be invertible" }
        return DVectorBase(vectorDimension, vectors.map { Vector.multiplyToVector(mat, it) })
    }

    override fun transfrom(f: (Vector<T>) -> Vector<T>): VectorBase<T> {
        return VectorBase.createBase(vectors.map(f))
    }

    override fun produce(cordInThisBase: Vector<T>): Vector<T> {
        return super.produce(cordInThisBase)
    }


    /**
     * Composes the two vector bases, which is always equal to `before.andThen(this)`.
     * It is required `this.dimension == before.baseSize`.
     * @see andThen
     */
    override fun compose(before: VectorBase<T>): VectorBase<T> {
        return before.andThen(this)
    }

    /**
     * Assume that [after] is a local vector base based on this vector base, transforms
     * [after] to a absolute vector base.
     */
    override fun andThen(after: VectorBase<T>): VectorBase<T> {
        require(this.baseSize == after.vectorDimension)
        return DVectorBase(vectorDimension,after.vectors.map { produce(it) })
    }

    override fun directProduct(vb: IVectorBase<T>): IVectorBase<T> {
        val nVectorDimension = vectorDimension + vb.vectorDimension
        val nVectors = ArrayList<Vector<T>>(baseSize + vb.baseSize)
        for(v in vectors){
            nVectors += Vector.resizeOf(v,0,vb.vectorDimension)
        }
        for (v in vb.vectors) {
            nVectors += Vector.resizeOf(v, vectorDimension, 0)
        }
        return DVectorBase(mc,nVectorDimension,nVectors)
    }

    override fun directSum(vb: IVectorBase<T>): IVectorBase<T> {
        val nVectors = ArrayList<Vector<T>>(baseSize + vb.baseSize)
        nVectors.addAll(vectors)
        for(v in vb.vectors){
            if(!Vector.isLinearRelevant(nVectors + v)){
                nVectors.add(v)
            }
        }
        return DVectorBase(vectorDimension,vectors)
    }

    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): VectorBase<N> {
        val nVectors = vectors.map { it.mapTo(mapper, newCalculator) }
        return DVectorBase(newCalculator, vectorDimension, nVectors)
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (this == obj) {
            return true
        }
        if (obj !is VectorBase) {
            return false
        }
        val vb: VectorBase<T> = obj
        return vectorDimension == vb.vectorDimension && baseSize == vb.baseSize &&
                CollectionSup.listEqual(vectors, vb.vectors) { v1, v2 -> v1.valueEquals(v2) }
    }

    override fun <N : Any> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean {
        if (this == obj) {
            return true
        }
        if (obj !is VectorBase<*>) {
            return false
        }
        val vb: VectorBase<N> = obj as VectorBase<N>
        return vectorDimension == vb.vectorDimension && baseSize == vb.baseSize &&
                CollectionSup.listEqual(vectors, vb.vectors) { v1, v2 -> v1.valueEquals(v2, mapper) }
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String = buildString {
        appendln("VectorBase: dimension=$vectorDimension, baseSize=$baseSize")
        for (v in vectors) {
            appendln(v.toString(nf))
        }
    }

    companion object {

        /**
         * Returns the standard base, which is both unit and orthogonal.
         */
        fun <T : Any> standardBase(dimension: Int, mc: MathCalculator<T>): StandardVectorBase<T> {
            require(dimension > 0) { "Dimension must be positive." }
            return StandardVectorBase(mc,dimension)
        }

        /**
         * Creates a vector base with given vectors.
         */
        fun <T : Any> createBase(vectors: List<Vector<T>>): VectorBase<T> {
            require(vectors.isNotEmpty())
            val dimension = vectors[0].size
            require(!Vector.isLinearRelevant(*vectors.toTypedArray())) { "Vectors must be linear irrelevant!" }

            val copy = vectors.map { v ->
                if (v.size != dimension) {
                    throw IllegalArgumentException("Vector's size ${v.size} is not equal to dimension=$dimension")
                }
                v.toColumnVector()
            }
            return if(dimension == vectors.size){
                FullVectorBase(dimension, copy)
            }else{
                DVectorBase(dimension, copy)
            }
        }

        /**
         * Creates a vector base with given vararg vectors.
         */
        fun <T : Any> createBase(vararg vectors: Vector<T>): VectorBase<T> {
            require(vectors.isNotEmpty())
            val dimension = vectors[0].size
            require(!Vector.isLinearRelevant(*vectors)) { "Vectors must be linear irrelevant!" }

            val copy = vectors.map { v ->
                requireVectorSize(dimension, v)
                v.toColumnVector()
            }
            return if(dimension == vectors.size){
                FullVectorBase(dimension, copy)
            }else{
                DVectorBase(dimension, copy)
            }
        }

        /**
         * Creates a full base from the given vectors.
         */
        fun <T:Any> createFullBase(vectors : List<Vector<T>>): FullVectorBase<T>{
            require(vectors.isNotEmpty())
            val dimension = vectors.size
            require(!Vector.isLinearRelevant(*vectors.toTypedArray())) { "Vectors must be linear irrelevant!" }

            val copy = vectors.map { v ->
                if (v.size != dimension) {
                    throw IllegalArgumentException("Vector's size ${v.size} is not equal to dimension=$dimension")
                }
                v.toColumnVector()
            }
            return FullVectorBase(dimension, copy)
        }

        /**
         * Creates a full base from the given vararg vectors.
         */
        fun <T:Any> createFullBase(vararg vectors : Vector<T>): FullVectorBase<T>{
            require(vectors.isNotEmpty())
            val dimension = vectors.size
            require(!Vector.isLinearRelevant(*vectors)) { "Vectors must be linear irrelevant!" }

            val copy = vectors.map { v ->
                if (v.size != dimension) {
                    throw IllegalArgumentException("Vector's size ${v.size} is not equal to dimension=$dimension")
                }
                v.toColumnVector()
            }
            return FullVectorBase(dimension, copy)
        }
    }
}

internal class DVectorBase<T : Any>(mc: MathCalculator<T>, override val vectorDimension: Int, override val vectors: List<Vector<T>>) :
        VectorBase<T>(mc) {
    constructor(vectorDimension: Int, vectors: List<Vector<T>>) : this(vectors[0].mathCalculator, vectorDimension, vectors)

    override val baseSize: Int = vectors.size
}

interface IFullVectorBase<T:Any> : IVectorBase<T>


open class FullVectorBase<T:Any>internal constructor(mc : MathCalculator<T>,
                                                     final override val vectorDimension: Int,
                                                     final override val vectors: List<Vector<T>>) :
        VectorBase<T>(mc),IFullVectorBase<T>{
    internal constructor(dimension: Int, vectors: List<Vector<T>>) : this(vectors[0].mathCalculator, dimension, vectors)
    init {
        require(vectorDimension == vectors.size)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected val vectorMatrix : Matrix<T> by lazy { super<VectorBase>.getVectorsAsMatrix() }

    @Suppress("MemberVisibilityCanBePrivate")
    protected val vectorMatrixInverse  : Matrix<T> by lazy {vectorMatrix.inverse()}

    override val baseSize: Int
        get() = vectorDimension

    override fun transform(mat: Matrix<T>): FullVectorBase<T> {
        require(mat.columnCount == vectorDimension) { "The transformation matrix must have the same size of $vectorDimension" }
        require(mat.isInvertible) { "The transformation matrix must be invertible" }

        return FullVectorBase(mc,vectorDimension, vectors.map { Vector.multiplyToVector(mat, it) })
    }

    override fun getVectorsAsMatrix(): Matrix<T> {
        return vectorMatrix
    }

    override fun reduce(v: Vector<T>): Vector<T> {
        return Vector.multiplyToVector(vectorMatrixInverse,v)
    }

    override fun canReduce(v: Vector<T>): Boolean = true

    open fun composeFull(before: FullVectorBase<T>): FullVectorBase<T> {
        return before.andThenFull(this)
    }

    open fun andThenFull(after: FullVectorBase<T>): FullVectorBase<T> {
        require(this.baseSize == after.vectorDimension)
        return FullVectorBase(vectorDimension,after.vectors.map { produce(it) })
    }

    open fun transformationTo(fullVectorBase: IFullVectorBase<T>) : Matrix<T>{
        //mat * this = fullVectorBase
        return fullVectorBase.getVectorsAsMatrix() * vectorMatrixInverse
    }


    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): FullVectorBase<N> {
        return FullVectorBase(newCalculator,vectorDimension,vectors.map { it.mapTo(mapper,newCalculator) })
    }

}
private fun <T:Any> initUnitVectors(mc:MathCalculator<T>,dimension: Int) : List<Vector<T>>{
    val list = ArrayList<Vector<T>>(dimension)
    for (i in 0 until dimension) {
        list.add(Vector.unitVector(dimension, i, mc))
    }
    return list
}
/**
 * Describes the standard vector base, in which the base vectors are orthogonal and unit, the [vectorDimension]
 * and [baseSize] are the same.
 */
class StandardVectorBase<T : Any>internal constructor(mc: MathCalculator<T>, dimension: Int) :
        FullVectorBase<T>(mc,dimension,initUnitVectors(mc,dimension)) {

    override fun reduce(v: Vector<T>): Vector<T> {
        requireVectorSize(v)
        return v
    }

    override fun canReduce(v: Vector<T>): Boolean {
        return true
    }

    override fun produce(cordInThisBase: Vector<T>): Vector<T> {
        requireVectorSize(cordInThisBase)
        return cordInThisBase
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String =
            "StandardVectorBase: dimension=$vectorDimension"

    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): FullVectorBase<N> {
        return StandardVectorBase(newCalculator,vectorDimension)
    }

    override fun compose(before: VectorBase<T>): VectorBase<T> {
        return before
    }

    override fun andThen(after: VectorBase<T>): VectorBase<T> {
        return after
    }

    override fun transformationTo(fullVectorBase: IFullVectorBase<T>): Matrix<T> {
        return fullVectorBase.getVectorsAsMatrix()
    }


}