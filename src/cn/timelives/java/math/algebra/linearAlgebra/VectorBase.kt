package cn.timelives.java.math.algebra.linearAlgebra

import cn.timelives.java.math.*
import cn.timelives.java.math.exceptions.OutOfDomainException
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter
import cn.timelives.java.math.property.Composable
import cn.timelives.java.math.property.Intersectable
import cn.timelives.java.utilities.CollectionSup
import java.lang.UnsupportedOperationException
import java.util.*
import java.util.function.Function


/**
 * Describes a vector base, which is a set of vectors that are linear irrelevant.
 * All vectors in the base are column vector.
 *
 * Created at 2018/9/10
 * @author liyicheng
 */
interface IVectorBase<T : Any> {

    /**
     * The dimension(size) of the base vectors.
     */
    val vectorDimension: Int

    /**
     * The rank of this vector base, which is equal to the number of base vectors.
     */
    val rank: Int
        get() = vectors.size

    /**
     * The base vectors, the size of the list is smaller than or equal to [vectorDimension]
     */
    val vectors: List<Vector<T>>

    /**
     * Returns the matrix containing the base vectors in this vector base. The base
     * vectors are all column vectors.
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
        return result.specialSolution ?: throw OutOfDomainException("The Vector cannot be reduced.")
    }

    /**
     * Determines whether the given vector can be reduced by this vector base.
     */
    fun canReduce(v: Vector<T>): Boolean {
        requireVectorSize(v)
        val mat = Matrix.fromVectors(false, *vectors.toTypedArray(), v);
        return MatrixSup.determineSolutionType(mat) !=
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
     * @param cordInThisBase a vector whose size is [rank]
     */
    fun produce(cordInThisBase: Vector<T>): Vector<T> {
        requireVectorSize(rank, cordInThisBase)
        @Suppress("UNCHECKED_CAST")
        val result = Array<Any>(rank) { i ->
            vectors[i].innerProduct(cordInThisBase)
        } as Array<T>
        return Vector.createVector(cordInThisBase.mathCalculator, *result)
    }

    fun canReduce(vb : IVectorBase<T>) : Boolean{
        return vb.vectors.all {
            canReduce(it) }
    }

    /**
     * Determines whether this vector base is equivalent to [vb], that is,
     * this vector base can reduce all vectors in [vb] and [vb] can also reduce
     * all vectors in this.
     */
    fun equivalentTo(vb : IVectorBase<T>) : Boolean{
        return this.canReduce(vb) and vb.canReduce(this)
    }



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
fun <T : Any> IVectorBase<T>.isFull(): Boolean = vectorDimension == rank

/**
 * Determines whether this vector base can be transformed to a standard vector base by
 * a linear transformation whose determinant is one.
 */
fun <T : Any> IVectorBase<T>.isSimilarStandard(): Boolean = isFull() && isOrthogonal() && isUnit()

/**
 * Converts this vector base to a full vector base. Throws an exception if
 * this vector base is not full.
 */
fun <T : Any> IVectorBase<T>.asFullVectorBase(): FullVectorBase<T> {
    if (!isFull()) {
        throw IllegalArgumentException("Not full!")
    }
    if (this is FullVectorBase) {
        return this
    }
    return FullVectorBase(vectorDimension, vectors)
}

fun <T : Any> IVectorBase<T>.asVectorBase(): VectorBase<T> {
    if (this is VectorBase) {
        return this
    }
    return DVectorBase(this.vectorDimension, this.vectors)
}

@Suppress("RedundantOverride")//Provided for Java extension
abstract class VectorBase<T : Any>(mc: MathCalculator<T>) : MathObjectExtend<T>(mc),
        IVectorBase<T>, Composable<VectorBase<T>>, Intersectable<VectorBase<T>> {


    protected fun requireSameVectorDimention(vb: IVectorBase<T>) {
        require(this.vectorDimension == vb.vectorDimension) { "Dimension of vectors of the two vector bases must be the same!" }
    }

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
        require(mat.columnCount == vectorDimension)
        { "The transformation matrix must have the same size of $vectorDimension" }
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
     * Returns the transformation matrix from this vector base to [vb].
     * It is required that the [vectorDimension] and [rank] are equal.
     *
     * Assuming the base vectors in `this` are `α1 α2 ... αn`, the base vectors in
     * `vb` are `β1 β2 ... βn`, and `βi` can be reduced to
     * >βi = a(i1)α1 + a(i2)α2 + ... + a(in)*αn
     *
     * Then we get a matrix **A**`' = a(ij) `. The transformation matrix is defined as the transportation of the
     * matrix, namely **A**. Then we can rewrite the equations using the matrix:
     * >(β1 β2 ... βn) = (α1 α2 ... αn)**A**
     *
     * And, if a vector `v` can be reduced to
     * >`v = x1*α1 + x2*α2 + ... + xn*αn`
     *
     * >`v = y1*β1 + y2*β2 + ... + yn*βn`
     *
     * then
     *     (x1 x2 ... xn)' = **A**(y1 y2 ... yn)'
     */
    open fun transMatrix(vb: VectorBase<T>): Matrix<T> {
        requireSameVectorDimention(vb)
        val a = getVectorsAsMatrix()
        val b = vb.getVectorsAsMatrix()
        return MatrixSup.solveMatrixEquation(a,b)
    }


//    /**
//     * Returns the linear transformation that transform this vector base to
//     * [vb].
//     * > `transformationTo(vb).transformBase(this).valueEquals(vb)`
//     */
//    open fun transformationTo(vb : VectorBase<T>) : LinearMapping<T>{
//        return LinearMapping.fromMatrix(transMatrix(vb))
//    }


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
        require(this.rank == after.vectorDimension)
        return DVectorBase(vectorDimension, after.vectors.map { produce(it) })
    }


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
     *  whose [rank] is `this.baseSize+vb.baseSize`
     */
    open fun directProduct(vb: VectorBase<T>): VectorBase<T> {
        val nVectorDimension = vectorDimension + vb.vectorDimension
        val nVectors = ArrayList<Vector<T>>(rank + vb.rank)
        for (v in vectors) {
            nVectors += Vector.resizeOf(v, 0, vb.vectorDimension)
        }
        for (v in vb.vectors) {
            nVectors += Vector.resizeOf(v, vectorDimension, 0)
        }
        return DVectorBase(mc, nVectorDimension, nVectors)
    }

    /**
     * Returns the sum of the two vector bases. The sum is defined as
     * a vector base whose vectors can be reduced to a linear combination of
     * vectors in `this` and `vb`. In other words, the new vector base has the base vector
     * of a maximum linear-irrelevant set of vectors among vectors in `this` and `vb`.
     *
     * It is required that [vb] has the same [vectorDimension] as `this`.
     * @return a vector base whose [vectorDimension] is `this.vectorDimension`
     */
    open fun sum(vb: VectorBase<T>): VectorBase<T> {
        requireSameVectorDimention(vb)
        if (this.isFull()) {
            return this
        }
        if (vb.isFull()) {
            return vb
        }
        val nVectors = ArrayList<Vector<T>>(rank + vb.rank)
        nVectors.addAll(vectors)
        for (v in vb.vectors) {
            if (!Vector.isLinearRelevant(nVectors + v)) {
                nVectors.add(v)
            }
        }
        return DVectorBase(vectorDimension, vectors)
    }

    /**
     * Returns the direct sum of the two vector bases. The intersection
     * of this vector base and [vb] must be only zero vector.
     * This method will throw an exception if the sum is not direct sum.
     *
     * It is required that [vb] has the same [vectorDimension] as `this`.
     */
    open fun directSum(vb: VectorBase<T>): VectorBase<T> {
        requireSameVectorDimention(vb)
        val nVectors = vectors + vb.vectors
        if (Vector.isLinearRelevant(nVectors)) {
            throw java.lang.IllegalArgumentException("Not direct sum!")
        }
        return DVectorBase(vectorDimension, vectors)
    }

    /**
     * Returns the intersect of `this` and `vb`, which is a vector base.
     * The bases of the new vector base are
     */
    override fun intersect(vb: VectorBase<T>): VectorBase<T> {
        require(this.vectorDimension == vb.vectorDimension) { "Dimension of vectors of the two vector bases must be the same!" }
        if (vb.isFull() || this.rank == 0) {
            return this
        }
        if (this.isFull() || vb.rank == 0) {
            return vb
        }
        val m = Matrix.fromVectors(false, this.vectors + vb.vectors)
        val solution = m.solutionSpace()!! // at least zero solution
        if (solution.rank == 0) {
            return VectorBase.zeroBase(vectorDimension, mc)
        }
        val nBases = ArrayList<Vector<T>>(solution.rank)
        for (v in solution.vectors) {
            var base = vectors[0] * v[0]
            for (i in 1..vectors.lastIndex) {
                base += vectors[i] * v[i]
            }
            nBases.add(base)
        }
        return DVectorBase(mc, vectorDimension, nBases)
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
        return vectorDimension == vb.vectorDimension && rank == vb.rank &&
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
        return vectorDimension == vb.vectorDimension && rank == vb.rank &&
                CollectionSup.listEqual(vectors, vb.vectors) { v1, v2 -> v1.valueEquals(v2, mapper) }
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String = buildString {
        appendln("VectorBase: dimension=$vectorDimension, baseSize=$rank")
        for (v in vectors) {
            appendln(v.toString(nf))
        }
    }

    companion object {

        /**
         * Creates a zero base, which contains no base vectors. This vector base does not support most of
         * operations.
         */
        @JvmStatic
        fun <T : Any> zeroBase(dimension: Int, mc: MathCalculator<T>): VectorBase<T> {
            return ZeroVectorBase(mc, dimension)
        }

        /**
         * Returns the standard base, which is both unit and orthogonal.
         */
        @JvmStatic
        fun <T : Any> standardBase(dimension: Int, mc: MathCalculator<T>): StandardVectorBase<T> {
            require(dimension > 0) { "Dimension must be positive." }
            return StandardVectorBase(mc, dimension)
        }

        /**
         * Returns the identity base, which contains unit vectors.
         * @param dimension the dimension(length) of all the vectors
         * @param baseSize the number of vectors that this base will have, must not exceed [dimension]
         */
        fun <T:Any> identityBase(dimension : Int, baseSize : Int, mc : MathCalculator<T>) : VectorBase<T>{
            require(baseSize >0)
            require(dimension >= baseSize)
            val list = (0 until baseSize).map { Vector.unitVector(dimension,it,mc) }
            return DVectorBase(dimension,list)
        }

        /**
         * Creates a vector base with given vectors, the vectors must be linear irrelevant.
         */
        @JvmStatic
        fun <T : Any> createBase(vectors: List<Vector<T>>): VectorBase<T> {
            require(vectors.isNotEmpty())
            val dimension = vectors[0].size
            require(!Vector.isLinearRelevant(vectors)) { "Vectors must be linear irrelevant!" }

            val copy = vectors.map { v ->
                if (v.size != dimension) {
                    throw IllegalArgumentException("Vector's size ${v.size} is not equal to dimension=$dimension")
                }
                v.toColumnVector()
            }
            return if (dimension == vectors.size) {
                FullVectorBase(dimension, copy)
            } else {
                DVectorBase(dimension, copy)
            }
        }

        /**
         * Creates a vector base with given vararg vectors.
         */
        @JvmStatic
        fun <T : Any> createBase(vararg vectors: Vector<T>): VectorBase<T> {
            return createBase(Arrays.asList(*vectors))
        }

        /**
         * Creates a full base from the given vectors.
         */
        @JvmStatic
        fun <T : Any> createFullBase(vectors: List<Vector<T>>): FullVectorBase<T> {
            require(vectors.isNotEmpty())
            val dimension = vectors.size
            require(!Vector.isLinearRelevant(vectors)) { "Vectors must be linear irrelevant!" }

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
        @JvmStatic
        fun <T : Any> createFullBase(vararg vectors: Vector<T>): FullVectorBase<T> {
            return createFullBase(Arrays.asList(*vectors))
        }

        @JvmStatic
        fun <T : Any> createBaseWithoutCheck(vectors: List<Vector<T>>): VectorBase<T> {
            require(vectors.isNotEmpty())
            val dimension = vectors[0].size
            val copy = vectors.map { v ->
                requireVectorSize(dimension, v)
                v.toColumnVector()
            }
            return if (dimension == vectors.size) {
                FullVectorBase(dimension, copy)
            } else {
                DVectorBase(dimension, copy)
            }
        }

        @JvmStatic
        fun <T : Any> createBaseWithoutCheck(vararg vectors: Vector<T>): VectorBase<T> {
            require(vectors.isNotEmpty())
            val dimension = vectors[0].size
            val copy = vectors.map { v ->
                requireVectorSize(dimension, v)
                v.toColumnVector()
            }
            return if (dimension == vectors.size) {
                FullVectorBase(dimension, copy)
            } else {
                DVectorBase(dimension, copy)
            }
        }
        @JvmStatic
        fun <T:Any> generate(vararg vectors : Vector<T>) : VectorBase<T>{
            return Vector.maximumLinearIrrelevant(*vectors)
        }

        @JvmStatic
        fun <T:Any> generate(vectors : List<Vector<T>>) : VectorBase<T>{
            return Vector.maximumLinearIrrelevant(vectors)
        }


    }
}

internal class DVectorBase<T : Any>(mc: MathCalculator<T>, override val vectorDimension: Int, override val vectors: List<Vector<T>>) :
        VectorBase<T>(mc) {
    constructor(vectorDimension: Int, vectors: List<Vector<T>>) : this(vectors[0].mathCalculator, vectorDimension, vectors)

    override val rank: Int = vectors.size
}

interface IFullVectorBase<T : Any> : IVectorBase<T>


/**
 * Describes a vector base whose [vectorDimension] is equal to [rank].
 */
open class FullVectorBase<T : Any> internal constructor(mc: MathCalculator<T>,
                                                        final override val vectorDimension: Int,
                                                        final override val vectors: List<Vector<T>>) :
        VectorBase<T>(mc), IFullVectorBase<T> {
    internal constructor(dimension: Int, vectors: List<Vector<T>>) : this(vectors[0].mathCalculator, dimension, vectors)

    init {
        require(vectorDimension == vectors.size)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected val vectorMatrix: Matrix<T> by lazy { super<VectorBase>.getVectorsAsMatrix() }

    @Suppress("MemberVisibilityCanBePrivate")
    protected val vectorMatrixInverse: Matrix<T> by lazy { vectorMatrix.inverse() }

    override val rank: Int
        get() = vectorDimension

    override fun transform(mat: Matrix<T>): FullVectorBase<T> {
        require(mat.columnCount == vectorDimension) { "The transformation matrix must have the same size of $vectorDimension" }
        require(mat.isInvertible) { "The transformation matrix must be invertible" }

        return FullVectorBase(mc, vectorDimension, vectors.map { Vector.multiplyToVector(mat, it) })
    }

    override fun getVectorsAsMatrix(): Matrix<T> {
        return vectorMatrix
    }

    override fun reduce(v: Vector<T>): Vector<T> {
        return Vector.multiplyToVector(vectorMatrixInverse, v)
    }

    override fun canReduce(v: Vector<T>): Boolean {
        requireVectorSize(v)
        return true
    }

    override fun canReduce(vb: IVectorBase<T>): Boolean {
        requireSameVectorDimention(vb)
        return true
    }

    open fun composeFull(before: FullVectorBase<T>): FullVectorBase<T> {
        return before.andThenFull(this)
    }

    open fun andThenFull(after: FullVectorBase<T>): FullVectorBase<T> {
        require(this.rank == after.vectorDimension)
        return FullVectorBase(vectorDimension, after.vectors.map { produce(it) })
    }

//    open fun transformationTo(fullVectorBase: IFullVectorBase<T>): Matrix<T> {
//        //mat * this = fullVectorBase
//        return fullVectorBase.getVectorsAsMatrix() * vectorMatrixInverse
//    }

    override fun transMatrix(vb: VectorBase<T>): Matrix<T> {
        return vectorMatrixInverse * vb.getVectorsAsMatrix()
    }

    /**
     * Returns the transformation matrix from standard vector base to this vector base.
     *
     * Assuming (e1 e2 ... en) is the standard vector base, the vectors in this vector base
     * are (α1 α2 ... αn), and the returned matrix is **P**, the relation can be denoted as:
     * > (α1 α2 ... αn) = (e1 e2 ... en)**P**
     */
    fun transMatrixFromStandard() : Matrix<T> = vectorMatrix

    /**
     * Returns the transformation matrix from standard vector base to this vector base.
     *
     * Assuming (e1 e2 ... en) is the standard vector base, the vectors in this vector base
     * are (α1 α2 ... αn), and the returned matrix is **P**, the relation can be denoted as:
     * > (e1 e2 ... en) = (α1 α2 ... αn)**P**
     */
    fun transMatrixToStandard() : Matrix<T> = vectorMatrixInverse






    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): FullVectorBase<N> {
        return FullVectorBase(newCalculator, vectorDimension, vectors.map { it.mapTo(mapper, newCalculator) })
    }

}

private fun <T : Any> initUnitVectors(mc: MathCalculator<T>, dimension: Int): List<Vector<T>> {
    val list = ArrayList<Vector<T>>(dimension)
    for (i in 0 until dimension) {
        list.add(Vector.unitVector(dimension, i, mc))
    }
    return list
}

/**
 * Describes the standard vector base, in which the base vectors are orthogonal and unit, the [vectorDimension]
 * and [rank] are the same.
 */
class StandardVectorBase<T : Any> internal constructor(mc: MathCalculator<T>, dimension: Int) :
        FullVectorBase<T>(mc, dimension, initUnitVectors(mc, dimension)) {

    override fun reduce(v: Vector<T>): Vector<T> {
        requireVectorSize(v)
        return v
    }

    override fun canReduce(v: Vector<T>): Boolean {
        return true
    }

    override fun canReduce(vb: IVectorBase<T>): Boolean {
        return true
    }

    override fun produce(cordInThisBase: Vector<T>): Vector<T> {
        requireVectorSize(cordInThisBase)
        return cordInThisBase
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String =
            "StandardVectorBase: dimension=$vectorDimension"

    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): FullVectorBase<N> {
        return StandardVectorBase(newCalculator, vectorDimension)
    }

    override fun compose(before: VectorBase<T>): VectorBase<T> {
        return before
    }

    override fun andThen(after: VectorBase<T>): VectorBase<T> {
        return after
    }

    override fun transMatrix(vb: VectorBase<T>): Matrix<T> {
        return vb.getVectorsAsMatrix()
    }


}

class ZeroVectorBase<T : Any> internal constructor(mc: MathCalculator<T>, dimension: Int) : VectorBase<T>(mc) {
    override val rank: Int
        get() = 0
    override val vectorDimension: Int = dimension
    override val vectors: List<Vector<T>>
        get() = emptyList()

    override fun getVectorsAsMatrix(): Matrix<T> {
        throw UnsupportedOperationException("Zero base has no vector!")
    }

    override fun reduce(v: Vector<T>): Vector<T> {
        throw UnsupportedOperationException("Zero base")
    }

    override fun canReduce(v: Vector<T>): Boolean {
        return v.isZeroVector
    }

    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): VectorBase<N> {
        return ZeroVectorBase(newCalculator, vectorDimension)
    }
}