package cn.ancono.math.algebra.linear

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObject
import cn.ancono.math.MathObjectExtend
import cn.ancono.math.algebra.abs.structure.FiniteLinearBasis
import cn.ancono.math.exceptions.OutOfDomainException
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import cn.ancono.math.numberModels.api.plus
import cn.ancono.math.property.Composable
import cn.ancono.math.property.Intersectable
import cn.ancono.utilities.CollectionSup
import java.util.function.Function


/**
 * Describes a vector base, which is a set of vectors that are linear irrelevant.
 *
 * Created at 2018/9/10
 * @author liyicheng
 */
interface IVectorBasis<T> : FiniteLinearBasis<T, Vector<T>> {

    /**
     * The dimension(size) of the base vectors.
     */
    val vectorLength: Int

    /**
     * The rank of this vector base, which is equal to the number of base vectors.
     */
    @JvmDefault
    override val rank: Int
        get() = vectors.size

    /**
     * The base vectors, the size of the list is smaller than or equal to [vectorLength]
     */
    val vectors: List<Vector<T>>

    override fun getElements(): List<Vector<T>> {
        return vectors
    }

    /**
     * Returns the matrix containing the base vectors in this vector base. The base
     * vectors are all column vectors.
     */
    fun asMatrix(): Matrix<T> {
        return Matrix.fromVectors(vectors)
    }

    /**
     * Reduces the given vector, whose size must be equal to [vectorLength], and
     * returns a column vector whose n-st element represents the coefficient
     * of n-st base vector.
     * @throws OutOfDomainException if the vector cannot be reduced
     */
    override fun reduce(v: Vector<T>): Vector<T> {
        requireVectorSize(v)
        val vectors = vectors
        val mat = Matrix.fromVectors(vectors)
        val result = Matrix.solveLinear(mat, v)
        if (result.notEmpty()) {
            return result.special
        }
        throw OutOfDomainException("The Vector cannot be reduced.")
    }

    /**
     * Determines whether the given vector can be reduced by this vector base.
     */
    fun canReduce(v: Vector<T>): Boolean {
        requireVectorSize(v)
        val mat = Matrix.fromVectors(vectors);
        return Matrix.solveLinear(mat, v).notEmpty()
    }

    /**
     * Returns the vector base after the transformation of a invertible square matrix whose
     * size is [vectorLength]
     */
    fun transform(mat: Matrix<T>): IVectorBasis<T>

    fun transform(f: (Vector<T>) -> Vector<T>): IVectorBasis<T>

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
    override fun produce(cordInThisBase: Vector<T>): Vector<T> {
        requireVectorSize(rank, cordInThisBase)
        @Suppress("UNCHECKED_CAST")
        val result = Array<Any?>(rank) { i ->
            vectors[i].inner(cordInThisBase)
        } as Array<T>
        return Vector.of(cordInThisBase.mathCalculator, *result)
    }

    fun canReduce(vb: IVectorBasis<T>): Boolean {
        return vb.vectors.all {
            canReduce(it)
        }
    }

    /**
     * Determines whether this vector base is equivalent to [vb], that is,
     * this vector base can reduce all vectors in [vb] and [vb] can also reduce
     * all vectors in this.
     */
    fun equivalentTo(vb: IVectorBasis<T>): Boolean {
        return this.canReduce(vb) and vb.canReduce(this)
    }


}

internal fun <T> IVectorBasis<T>.requireVectorSize(v: Vector<T>) {
    requireVectorSize(this.vectorLength, v)
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
fun <T> IVectorBasis<T>.isUnit() = vectors.all { it.isUnitVector() }

/**
 * Determines whether this vector base is orthogonal, which is true if each vector
 * in this vector base is perpendicular to all other vectors.
 */
fun <T> IVectorBasis<T>.isOrthogonal(): Boolean {
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
fun <T> IVectorBasis<T>.isFull(): Boolean = vectorLength == rank

/**
 * Determines whether this vector base can be transformed to a standard vector base by
 * a linear transformation whose determinant is one.
 */
fun <T> IVectorBasis<T>.isSimilarStandard(): Boolean = isFull() && isOrthogonal() && isUnit()

/**
 * Converts this vector base to a full vector base. Throws an exception if
 * this vector base is not full.
 */
fun <T> IVectorBasis<T>.asFullVectorBase(): FullVectorBasis<T> {
    if (!isFull()) {
        throw IllegalArgumentException("Not full!")
    }
    if (this is FullVectorBasis) {
        return this
    }
    return FullVectorBasis(vectorLength, vectors)
}

fun <T> IVectorBasis<T>.asVectorBase(): VectorBasis<T> {
    if (this is VectorBasis) {
        return this
    }
    return DVectorBasis(this.vectorLength, this.vectors)
}

@Suppress("RedundantOverride")//Provided for Java extension
abstract class VectorBasis<T>(mc: MathCalculator<T>) : MathObjectExtend<T>(mc),
        IVectorBasis<T>, Composable<VectorBasis<T>>, Intersectable<VectorBasis<T>> {


    protected fun requireSameVectorDimension(vb: IVectorBasis<T>) {
        require(this.vectorLength == vb.vectorLength) { "Dimension of vectors of the two vector bases must be the same!" }
    }

    override fun asMatrix(): Matrix<T> {
        return super.asMatrix()
    }

    override fun reduce(v: Vector<T>): Vector<T> {
        return super.reduce(v)
    }

    override fun canReduce(v: Vector<T>): Boolean {
        return super.canReduce(v)
    }

    override fun transform(mat: Matrix<T>): VectorBasis<T> {
        require(mat.column == vectorLength)
        { "The transformation matrix must have the same size of $vectorLength" }
        require(mat.isInvertible()) { "The transformation matrix must be invertible" }
        return DVectorBasis(vectorLength, vectors.map { Vector.multiplyToVector(mat, it) })
    }

    override fun transform(f: (Vector<T>) -> Vector<T>): VectorBasis<T> {
        return VectorBasis.createBase(vectors.map(f))
    }

    override fun produce(cordInThisBase: Vector<T>): Vector<T> {
        return super.produce(cordInThisBase)
    }

    /**
     * Returns the transformation matrix from this vector base to [vb].
     * It is required that the [vectorLength] and [rank] are equal.
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
    open fun transMatrix(vb: VectorBasis<T>): Matrix<T> {
        requireSameVectorDimension(vb)
        val a = asMatrix()
        val b = vb.asMatrix()
        return Matrix.solveLinear(a, b).first
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
    override fun compose(before: VectorBasis<T>): VectorBasis<T> {
        return before.andThen(this)
    }

    /**
     * Assume that [after] is a local vector base based on this vector base, transforms
     * [after] to a absolute vector base.
     */
    override fun andThen(after: VectorBasis<T>): VectorBasis<T> {
        require(this.rank == after.vectorLength)
        return DVectorBasis(vectorLength, after.vectors.map { produce(it) })
    }


    /**
     * Returns the direct product of this two vector bases. The result is a vector base
     * whose [vectorLength] is the  `this.vectorDimension + vb.vectorDimension`. The
     * base vectors of the result vector base is composed of two parts: vectors with
     * preceding elements that are equal to corresponding elements in base vectors in `this` and
     * succeeding zeros, and vectors with preceding zeros and succeeding elements that are equal to
     * elements in base vectors in `vb` correspondingly.
     *
     *
     * For example, assume `this` is a vector base of {(1,0),(0,1)}, and `vb` is a vector base of {(1,2)},
     * then the result of direct product is a vector base of {(1,0,0,0),(0,1,0,0),(0,0,1,2)}.
     *  @return a vector base whose [vectorLength] is `this.vectorDimension + vb.vectorDimension` and
     *  whose [rank] is `this.baseSize+vb.baseSize`
     */
    open fun directProduct(vb: VectorBasis<T>): VectorBasis<T> {
        val nVectorDimension = vectorLength + vb.vectorLength
        val nVectors = ArrayList<Vector<T>>(rank + vb.rank)
        for (v in vectors) {
            nVectors += v.expand(0, vb.vectorLength)
        }
        for (v in vb.vectors) {
            nVectors += v.expand(vectorLength, 0)
        }
        return DVectorBasis(mc, nVectorDimension, nVectors)
    }

    /**
     * Extends or shorten the vectors in this vector basis so that their length is equal to [nVectorLength]. If the new
     * vector length is smaller than the original vector length, the rank of the new vector basis may be smaller thant the
     * rank of this vector basis.
     * It is equal to projecting this vector basis to vector space.
     */
    fun projectTo(nVectorLength: Int): VectorBasis<T> {
        require(nVectorLength > 0)
        if (nVectorLength == vectorLength) {
            return this
        }
        val nVectors = vectors.map { it.resize(nVectorLength) }
        return if (nVectorLength > vectorLength) {
            DVectorBasis(mc, nVectorLength, nVectors)
        } else {
            generate(nVectors)
        }
    }

    /**
     * Returns the sum of the two vector bases. The sum is defined as
     * a vector base whose vectors can be reduced to a linear combination of
     * vectors in `this` and `vb`. In other words, the new vector base has the base vector
     * of a maximum linear-irrelevant set of vectors among vectors in `this` and `vb`.
     *
     * It is required that [vb] has the same [vectorLength] as `this`.
     * @return a vector base whose [vectorLength] is `this.vectorDimension`
     */
    open fun sum(vb: VectorBasis<T>): VectorBasis<T> {
        requireSameVectorDimension(vb)
        if (this.isFull()) {
            return this
        }
        if (vb.isFull()) {
            return vb
        }
//        val nVectors = ArrayList<Vector<T>>(rank + vb.rank)
//        nVectors.addAll(vectors)
//        for (v in vb.vectors) {
//            if (!Vector.isLinearDependent(nVectors + v)) {
//                nVectors.add(v)
//            }
//        }
//        return DVectorBasis(vectorLength, vectors)
        return generate(vectors + vb.vectors)
    }

    /**
     * Returns the direct sum of the two vector bases. The intersection
     * of this vector base and [vb] must be only zero vector.
     * This method will throw an exception if the sum is not direct sum.
     *
     * It is required that [vb] has the same [vectorLength] as `this`.
     */
    open fun directSum(vb: VectorBasis<T>): VectorBasis<T> {
        requireSameVectorDimension(vb)
        val nVectors = vectors + vb.vectors
        if (Vector.isLinearDependent(nVectors)) {
            throw java.lang.IllegalArgumentException("Not direct sum!")
        }
        return DVectorBasis(vectorLength, vectors)
    }

    /**
     * Returns the intersect of `this` and `vb`, which is a vector base.
     * The bases of the new vector base are
     */
    override fun intersect(vb: VectorBasis<T>): VectorBasis<T> {
        require(this.vectorLength == vb.vectorLength) { "Dimension of vectors of the two vector bases must be the same!" }
        if (vb.isFull() || this.rank == 0) {
            return this
        }
        if (this.isFull() || vb.rank == 0) {
            return vb
        }
        val m = Matrix.fromVectors(this.vectors + vb.vectors)
        val solution = m.kernel() // at least zero solution
        if (solution.rank == 0) {
            return VectorBasis.zero(vectorLength, mc)
        }
        val nBases = ArrayList<Vector<T>>(solution.rank)
        for (v in solution.vectors) {
            var base = vectors[0] * v[0]
            for (i in 1..vectors.lastIndex) {
                base += vectors[i] * v[i]
            }
            nBases.add(base)
        }
        return DVectorBasis(mc, vectorLength, nBases)
    }

    override fun <N> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): VectorBasis<N> {
        val nVectors = vectors.map { it.mapTo(newCalculator, mapper) }
        return DVectorBasis(newCalculator, vectorLength, nVectors)
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (this == obj) {
            return true
        }
        if (obj !is VectorBasis) {
            return false
        }
        val vb: VectorBasis<T> = obj
        return vectorLength == vb.vectorLength && rank == vb.rank &&
                CollectionSup.listEqual(vectors, vb.vectors) { v1, v2 -> v1.valueEquals(v2) }
    }

    override fun <N> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean {
        if (this == obj) {
            return true
        }
        if (obj !is VectorBasis<*>) {
            return false
        }
        val vb: VectorBasis<N> = obj as VectorBasis<N>
        return vectorLength == vb.vectorLength && rank == vb.rank &&
                CollectionSup.listEqual(vectors, vb.vectors) { v1, v2 -> v1.valueEquals(v2, mapper) }
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String = buildString {
        appendLine("VectorBase: dimension=$vectorLength, baseSize=$rank")
        for (v in vectors) {
            appendLine(v.toString(nf))
        }
    }

    companion object {

        /**
         * Creates a zero base, which contains no base vectors. This vector base does not support most of
         * operations.
         */
        @JvmStatic
        fun <T> zero(dimension: Int, mc: MathCalculator<T>): VectorBasis<T> {
            return ZeroVectorBasis(mc, dimension)
        }

        /**
         * Returns the standard base, which is both unit and orthogonal.
         */
        @JvmStatic
        fun <T> standard(dimension: Int, mc: MathCalculator<T>): StandardVectorBasis<T> {
            require(dimension > 0) { "Dimension must be positive." }
            return StandardVectorBasis(mc, dimension)
        }

        /**
         * Returns the identity base, which contains unit vectors.
         * @param dimension the dimension(length) of all the vectors
         * @param baseSize the number of vectors that this base will have, must not exceed [dimension]
         */
        fun <T> identity(dimension: Int, baseSize: Int, mc: MathCalculator<T>): VectorBasis<T> {
            require(baseSize > 0)
            require(dimension >= baseSize)
            val list = (0 until baseSize).map { Vector.unitVector(dimension, it, mc) }
            return DVectorBasis(dimension, list)
        }

        private fun <T> checkIrrelevant(vectors: List<Vector<T>>): List<Vector<T>> {
            val dimension = vectors[0].size
            require(!Vector.isLinearDependent(vectors)) { "Vectors must be linear irrelevant!" }

            val copy = vectors.map { v ->
                if (v.size != dimension) {
                    throw IllegalArgumentException("Vector's size ${v.size} is not equal to dimension=$dimension")
                }
                v
            }
            return copy
        }

        /**
         * Creates a vector base with given vectors, the vectors must be linear irrelevant.
         */
        @JvmStatic
        fun <T> createBase(vectors: List<Vector<T>>): VectorBasis<T> {
            require(vectors.isNotEmpty())
            val dimension = vectors[0].size
            val copy = checkIrrelevant(vectors)
            return if (dimension == vectors.size) {
                FullVectorBasis(dimension, copy)
            } else {
                DVectorBasis(dimension, copy)
            }
        }

        /**
         * Creates a vector base with given vararg vectors.
         */
        @SafeVarargs
        @JvmStatic
        fun <T> createBase(vararg vectors: Vector<T>): VectorBasis<T> {
            return createBase(vectors.asList())
        }

        /**
         * Creates a full base from the given vectors.
         */
        @JvmStatic
        fun <T> createFullBase(vectors: List<Vector<T>>): FullVectorBasis<T> {
            require(vectors.isNotEmpty())
            require(vectors.size == vectors[0].size)
            val dimension = vectors.size
            val copy = checkIrrelevant(vectors)
            return FullVectorBasis(dimension, copy)
        }

        /**
         * Creates a full base from the given vararg vectors.
         */
        @JvmStatic
        fun <T> createFullBase(vararg vectors: Vector<T>): FullVectorBasis<T> {
            return createFullBase(listOf(*vectors))
        }

        @JvmStatic
        fun <T> createBaseWithoutCheck(vectors: List<Vector<T>>): VectorBasis<T> {
            require(vectors.isNotEmpty())
            val dimension = vectors[0].size
            val copy = vectors.map { v ->
                requireVectorSize(dimension, v)
                v
            }
            return if (dimension == vectors.size) {
                FullVectorBasis(dimension, copy)
            } else {
                DVectorBasis(dimension, copy)
            }
        }

        @JvmStatic
        @SafeVarargs
        fun <T> createBaseWithoutCheck(vararg vectors: Vector<T>): VectorBasis<T> {
            return createBaseWithoutCheck(vectors.asList())
        }

        @JvmStatic
        @SafeVarargs
        fun <T> generate(vararg vectors: Vector<T>): VectorBasis<T> {
            return Vector.maxIndependent(vectors.asList())
        }

        @JvmStatic
        fun <T> generate(vectors: List<Vector<T>>): VectorBasis<T> {
            return Vector.maxIndependent(vectors)
        }

        @JvmStatic
        fun <T> directSumAll(vectors: List<VectorBasis<T>>): VectorBasis<T> {
            val rankSum = vectors.sumBy { it.rank }
            val ves = vectors.flatMapTo(ArrayList(rankSum)) { it.vectors }
            val sum = generate(ves)
            if (sum.rank != rankSum) {
                throw IllegalArgumentException("Not direct sum!")
            }
            return sum
        }

        @JvmStatic
        fun <T> directSumAll(vararg vectors: VectorBasis<T>): VectorBasis<T> {
            return directSumAll(vectors.asList())
        }


    }
}

internal class DVectorBasis<T>(mc: MathCalculator<T>, override val vectorLength: Int, override val vectors: List<Vector<T>>) :
        VectorBasis<T>(mc) {
    constructor(vectorDimension: Int, vectors: List<Vector<T>>) : this(vectors[0].mathCalculator, vectorDimension, vectors)

    override val rank: Int = vectors.size
}

interface IFullVectorBasis<T> : IVectorBasis<T>


/**
 * Describes a vector base whose [vectorLength] is equal to [rank].
 */
open class FullVectorBasis<T> internal constructor(mc: MathCalculator<T>,
                                                   final override val vectorLength: Int,
                                                   final override val vectors: List<Vector<T>>) :
        VectorBasis<T>(mc), IFullVectorBasis<T> {
    internal constructor(dimension: Int, vectors: List<Vector<T>>) : this(vectors[0].mathCalculator, dimension, vectors)

    init {
        require(vectorLength == vectors.size)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected val vectorMatrix: Matrix<T> by lazy { super<VectorBasis>.asMatrix() }

    @Suppress("MemberVisibilityCanBePrivate")
    protected val vectorMatrixInverse: Matrix<T> by lazy { vectorMatrix.inverse() }

    override val rank: Int
        get() = vectorLength

    override fun transform(mat: Matrix<T>): FullVectorBasis<T> {
        require(mat.column == vectorLength) { "The transformation matrix must have the same size of $vectorLength" }
        require(mat.isInvertible()) { "The transformation matrix must be invertible" }

        return FullVectorBasis(mc, vectorLength, vectors.map { Vector.multiplyToVector(mat, it) })
    }

    override fun asMatrix(): Matrix<T> {
        return vectorMatrix
    }

    override fun reduce(v: Vector<T>): Vector<T> {
        return Vector.multiplyToVector(vectorMatrixInverse, v)
    }

    override fun canReduce(v: Vector<T>): Boolean {
        requireVectorSize(v)
        return true
    }

    override fun canReduce(vb: IVectorBasis<T>): Boolean {
        requireSameVectorDimension(vb)
        return true
    }

    open fun composeFull(before: FullVectorBasis<T>): FullVectorBasis<T> {
        return before.andThenFull(this)
    }

    open fun andThenFull(after: FullVectorBasis<T>): FullVectorBasis<T> {
        require(this.rank == after.vectorLength)
        return FullVectorBasis(vectorLength, after.vectors.map { produce(it) })
    }

//    open fun transformationTo(fullVectorBase: IFullVectorBase<T>): Matrix<T> {
//        //mat * this = fullVectorBase
//        return fullVectorBase.getVectorsAsMatrix() * vectorMatrixInverse
//    }

    override fun transMatrix(vb: VectorBasis<T>): Matrix<T> {
        return vectorMatrixInverse * vb.asMatrix()
    }

    /**
     * Returns the transformation matrix from standard vector base to this vector base.
     *
     * Assuming (e1 e2 ... en) is the standard vector base, the vectors in this vector base
     * are (α1 α2 ... αn), and the returned matrix is **P**, the relation can be denoted as:
     * > (α1 α2 ... αn) = (e1 e2 ... en)**P**
     */
    fun transMatrixFromStandard(): Matrix<T> = vectorMatrix

    /**
     * Returns the transformation matrix from standard vector base to this vector base.
     *
     * Assuming (e1 e2 ... en) is the standard vector base, the vectors in this vector base
     * are (α1 α2 ... αn), and the returned matrix is **P**, the relation can be denoted as:
     * > (e1 e2 ... en) = (α1 α2 ... αn)**P**
     */
    fun transMatrixToStandard(): Matrix<T> = vectorMatrixInverse


    override fun <N> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): FullVectorBasis<N> {
        return FullVectorBasis(newCalculator, vectorLength, vectors.map { it.mapTo(newCalculator, mapper) })
    }

}

private fun <T> initUnitVectors(mc: MathCalculator<T>, dimension: Int): List<Vector<T>> {
    val list = ArrayList<Vector<T>>(dimension)
    for (i in 0 until dimension) {
        list.add(Vector.unitVector(dimension, i, mc))
    }
    return list
}

/**
 * Describes the standard vector base, in which the base vectors are orthogonal and unit, the [vectorLength]
 * and [rank] are the same.
 */
class StandardVectorBasis<T> internal constructor(mc: MathCalculator<T>, dimension: Int) :
        FullVectorBasis<T>(mc, dimension, initUnitVectors(mc, dimension)) {

    override fun reduce(v: Vector<T>): Vector<T> {
        requireVectorSize(v)
        return v
    }

    override fun canReduce(v: Vector<T>): Boolean {
        return true
    }

    override fun canReduce(vb: IVectorBasis<T>): Boolean {
        return true
    }

    override fun produce(cordInThisBase: Vector<T>): Vector<T> {
        requireVectorSize(cordInThisBase)
        return cordInThisBase
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String =
            "StandardVectorBase: dimension=$vectorLength"

    override fun <N> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): FullVectorBasis<N> {
        return StandardVectorBasis(newCalculator, vectorLength)
    }

    override fun compose(before: VectorBasis<T>): VectorBasis<T> {
        return before
    }

    override fun andThen(after: VectorBasis<T>): VectorBasis<T> {
        return after
    }

    override fun transMatrix(vb: VectorBasis<T>): Matrix<T> {
        return vb.asMatrix()
    }


}

class ZeroVectorBasis<T> internal constructor(mc: MathCalculator<T>, dimension: Int) : VectorBasis<T>(mc) {
    override val rank: Int
        get() = 0
    override val vectorLength: Int = dimension
    override val vectors: List<Vector<T>>
        get() = emptyList()

    override fun asMatrix(): Matrix<T> {
        throw UnsupportedOperationException("Zero base has no vector!")
    }

    override fun reduce(v: Vector<T>): Vector<T> {
        throw UnsupportedOperationException("Zero base")
    }

    override fun canReduce(v: Vector<T>): Boolean {
        return v.isZero()
    }

    override fun <N> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): VectorBasis<N> {
        return ZeroVectorBasis(newCalculator, vectorLength)
    }
}