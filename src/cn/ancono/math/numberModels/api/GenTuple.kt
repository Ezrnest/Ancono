package cn.ancono.math.numberModels.api

import cn.ancono.math.algebra.linear.Vector
import cn.ancono.utilities.IterUtils


/*
 * Created by liyicheng at 2021-04-28 18:38
 */

/**
 * A generic (multidimensional) tuple-like container with finite elements in order.
 */
interface GenTuple<T> {
    /**
     * The count of elements contained in this tuple.
     */
    val size: Int

    /**
     * Gets the elements in this generic tuple as a sequence. The order of elements should
     * remain the same.
     *
     * @see flattenToList
     */
    fun elementSequence(): Sequence<T>

    /**
     * Flatten this generic tuple to a list. The order of the elements is the same as [elementSequence].
     *
     * @see elementSequence
     */
    @Suppress("UNCHECKED_CAST")
    fun flattenToList(): List<T> {
        val size = this.size
        val data = ArrayList<T>(size)
        for (s in elementSequence()) {
            data += s
        }
        return data
    }

    /**
     * Returns a new tuple of the same type as the result of applying the given function to each element in this.
     */
    fun applyAll(f: (T) -> T): GenTuple<T>
}

typealias Index = IntArray

/**
 * Generic tensor-like container.
 */
interface GenTensor<T> : GenTuple<T> {

    val shape: IntArray

    operator fun get(idx: Index): T

    override fun applyAll(f: (T) -> T): GenTensor<T>
}

val GenTensor<*>.shapeString: String
    get() = shape.contentToString()

/**
 * Generic matrix-like container.
 */
interface GenMatrix<T> : GenTuple<T> {

    /**
     * The count of rows in this matrix.
     */
    val row: Int

    /**
     * The count of columns in this matrix.
     */
    val column: Int

    override val size: Int
        get() = row * column

    operator fun get(i: Int, j: Int): T

    override fun applyAll(f: (T) -> T): GenMatrix<T>

    override fun elementSequence(): Sequence<T> {
        return IterUtils.prodIdx(intArrayOf(row, column)).map { (i, j) -> this[i, j] }
    }


    /**
     * Determines whether this matrix is the same shape as [y].
     */
    fun isSameShape(y: GenMatrix<*>): Boolean {
        return row == y.row && column == y.column
    }

    /**
     * Determines whether this matrix is a square matrix.
     */
    fun isSquare(): Boolean {
        return row == column
    }
}

fun GenMatrix<*>.requireSquare() {
    require(isSquare()) {
        "This matrix should be square! Row=$row, Column=$column."
    }
}

interface GenVector<T> : GenTuple<T> {

    /**
     * Gets the `i`-th element in the generic vector.
     */
    operator fun get(i: Int): T

    /**
     * Returns a list containing all the elements in this generic vector in order.
     */
    fun toList(): List<T>

    override fun flattenToList(): List<T> {
        return toList()
    }

    override fun applyAll(f: (T) -> T): GenVector<T>

    /**
     * Determines whether the two vectors are of the identity size.
     *
     * @param v another vector.
     * @return `true` if they are the identity in size.
     */
    fun isSameSize(v: Vector<*>): Boolean {
        return size == v.size
    }
}

/**
 * Gets the shape of this matrix: `(row, column)`.
 */
val <T> GenMatrix<T>.shape: Pair<Int, Int>
    get() = row to column

inline val GenMatrix<*>.rowIndices: IntRange
    get() = 0 until row

inline val GenMatrix<*>.colIndices: IntRange
    get() = 0 until column

inline val GenVector<*>.indices: IntRange
    get() = 0 until size
