package cn.ancono.math.algebra.linear

import cn.ancono.math.AbstractFlexibleMathObject
import cn.ancono.math.FMathObject
import cn.ancono.math.MathCalculator
import cn.ancono.math.algebra.abs.calculator.*
import cn.ancono.math.numberModels.api.*
import cn.ancono.utilities.ArraySup
import java.util.function.Function
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToLong


/*
 * Created by liyicheng at 2021-04-28 18:49
 */


abstract class AbstractVector<T>(
        mc: RingCalculator<T>,
        /**
         * The size of this vector, the number of element that this vector contains.
         */
        final override val size: Int)
    : AbstractFlexibleMathObject<T, RingCalculator<T>>(mc), GenVector<T> {
    protected fun checkSameSize(v: AbstractVector<*>) {
        require(size == v.size) {
            "Shape mismatch: $size and ${v.size}"
        }
    }

    /**
     * Returns the square of the Euclidean norm of this vector, which
     * is equal to the sum of square of each element.
     *
     *
     * This method is generally the same as `this.inner(this)`.
     *
     * @return `|this|^2`
     */
    open fun normSq(): T {
        val mc = calculator
        var re = mc.zero
        for (i in 0 until size) {
            val t = get(i)
            re = mc.add(mc.multiply(t, t), re)
        }
        return re
    }

    /**
     * Return the Euclidean norm of this vector, which
     * is equal to the square root of the sum of square of each elements.
     * The result is non-negative.
     *
     * @return `|this|`
     */
    open fun norm(): T {
        val mc = calculator as MathCalculator
        return mc.squareRoot(normSq())
    }

    /**
     * This method will return the inner product of `this` and `v`.
     * The size of the two vectors must be the identity while what kind of vector
     * (row or column) is ignored.
     *
     * @param v a vector
     * @return the inner(scalar) product of this two vectors.
     * @throws ArithmeticException if dimension doesn't match
     */
    open infix fun inner(v: AbstractVector<T>): T {
        checkSameSize(v)
        val mc = calculator
        var re = mc.zero

        for (i in 0 until size) {
            mc.eval {
                re += get(i) * v[i]
            }
        }

        return re
    }

    abstract override fun applyAll(f: (T) -> T): AbstractVector<T>


    abstract override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): AbstractVector<N>

    override fun valueEquals(obj: FMathObject<T, RingCalculator<T>>): Boolean {
        if (obj !is Vector) {
            return false
        }
        return indices.all { i -> calculator.isEqual(this[i], obj[i]) }
    }


    override fun toString(nf: FlexibleNumberFormatter<T>): String {
        return indices.joinToString(", ", "(", ")") { nf.format(get(it)) }
    }
}

abstract class Vector<T>(
        mc: RingCalculator<T>,
        size: Int)
    : AbstractVector<T>(mc, size), VectorModel<T, Vector<T>>, GenVector<T> {


    /**
     * Returns a unit vector of this vector's direction, throws an exception
     * if this vector is an zero vector.
     *
     *     this/|this|
     *
     *
     * @return a vector
     */
    open fun unitize(): Vector<T> {
        val mc = calculator as FieldCalculator
        val norm = norm()
        if (mc.isZero(norm)) {
            throw ArithmeticException("This vector is zero!")
        }
        return this.multiply(mc.reciprocal(norm))
    }


    /**
     * Determines whether the two vectors are perpendicular.
     *
     * @param v another vector
     * @return `true` of the vectors are perpendicular
     */
    open fun isPerpendicular(v: Vector<T>): Boolean {
        return calculator.isZero(inner(v))
    }

    /**
     * Returns the angle of `this` and `v`.
     * <pre> arccos(this · v / (|this| |v|))</pre>
     *
     * @return <pre> arccos(this · v / (|this| |v|))</pre>
     */
    open fun angle(v: Vector<T>): T {
        val mc = calculator as MathCalculator
        return mc.arccos(angleCos(v))
    }

    /**
     * Returns the cos value of the angle of `this` and `v`.
     * <pre>this · v / (|this| |v|)</pre>
     *
     * @return <pre>this · v / (|this| |v|)</pre>
     */
    open fun angleCos(v: Vector<T>): T {
        val pro = inner(v)
        val mc = calculator as MathCalculator
        return mc.divide(pro, mc.multiply(norm(), v.norm()))
    }


    /**
     * Determines whether this vector is a zero vector.
     */
    open fun isZero(): Boolean {
        val mc = calculator
        return (0 until size).all { mc.isZero(this[it]) }
    }

    /**
     * Determines whether this vector is an unit vector.
     */
    open fun isUnitVector(): Boolean {
        val mc = calculator as UnitRingCalculator
        return mc.isEqual(normSq(), mc.one)
    }


    /**
     * Determines whether the two vectors are parallel.
     * If any of the two vector is a zero vector , than
     * the method will return true.
     *
     * @param v a vector
     * @return `true` if `this // v`
     */
    open fun isParallel(v: Vector<T>): Boolean {
        // dimension check
        checkSameSize(v)
        if (isZero() || v.isZero()) {
            return true
        }
        val mc = calculator as FieldCalculator
        var not0 = 0
        while (mc.isZero(get(not0))) {
            if (!mc.isZero(v[not0])) {
                return false
            }
            not0++
            if (not0 + 1 == size) {
                return true
            }
        }
        val t1 = get(not0)
        val t2 = v[not0]
        for (i in not0 + 1 until size) {
            if (!mc.isEqual(mc.multiply(t1, v[i]), mc.multiply(t2, get(i)))) {
                return false
            }
        }
        return true
    }


    /**
     * Creates a new vector of the given size. The elements in the corresponding positions are equal to the elements
     * in this and remaining elements are set to zero.
     */
    open fun resize(size: Int): Vector<T> {
        return AVector.copyOfRange(size, this, 0, size, 0, calculator)
    }

    /**
     * Expands this vector adding padding zeros to the left and to the right.
     */
    open fun expand(left: Int, right: Int): Vector<T> {
        val newSize = left + right + size
        val start = min(0, left)
        val end = size - min(0, right)
        val destIdx = max(0, left)
        return AVector.copyOfRange(newSize, this, start, end, destIdx, calculator)
    }

    abstract override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): Vector<N>


    override fun add(y: Vector<T>): Vector<T> {
        return VectorImpl.add(this, y)
    }

    override fun negate(): Vector<T> {
        return VectorImpl.negate(this)
    }

    override fun subtract(y: Vector<T>): Vector<T> {
        return VectorImpl.subtract(this, y)
    }

    override fun multiply(k: T): Vector<T> {
        return VectorImpl.multiply(this, k)
    }

    override fun divide(k: T): Vector<T> {
        return VectorImpl.divide(this, k)
    }

    /**
     * Multiplies this vector with the matrix [m], viewing this as a row vector.
     *
     * It is required that the [size] of this is equal to the [Matrix.row] of [m].
     *
     * @return `this * m`
     * @see Vector.multiplyByVector
     */
    open fun multiply(m: Matrix<T>): Vector<T> {
        return multiplyByVector(this, m)
    }

    operator fun times(m: Matrix<T>): Vector<T> = multiply(m)


    companion object {
        /**
         * Returns a vector whose elements are all [x].
         */
        @JvmStatic
        fun <T> constant(x: T, size: Int, mc: RingCalculator<T>): MutableVector<T> {
            return AVector.constant(x, size, mc)
        }

        /**
         * Returns a vector with all ones.
         */
        @JvmStatic
        fun <T> ones(size: Int, mc: UnitRingCalculator<T>): MutableVector<T> {
            return constant(mc.one, size, mc)
        }

        /**
         * Returns a zero vector.
         */
        @JvmStatic
        fun <T> zero(size: Int, mc: RingCalculator<T>): MutableVector<T> {
            return AVector.zero(size, mc)
        }

        /**
         * Returns a unit column vector of the given [size].
         */
        fun <T> unitVector(size: Int, unitIndex: Int, mc: UnitRingCalculator<T>): Vector<T> {
            return AVector.unitVector(size, unitIndex, mc)
        }


        /**
         * Returns a list of all unit vectors of the given [size].
         */
        fun <T> unitVectors(size: Int, mc: UnitRingCalculator<T>): List<Vector<T>> {
            return AVector.unitVectors(size, mc)
        }

        /**
         * Creates a vector according to the given supplier.
         */
        @JvmStatic
        fun <T> of(size: Int, mc: RingCalculator<T>, supplier: (Int) -> T): MutableVector<T> {
            return AVector.of(size, mc, supplier)
        }

        /**
         * Creates a vector with the given [elements] list.
         */
        @JvmStatic
        fun <T> of(elements: List<T>, mc: RingCalculator<T>): MutableVector<T> {
            return AVector.of(elements, mc)
        }

        /**
         * Creates a vector with the given [elements] array.
         */
        @JvmStatic
        fun <T> of(mc: RingCalculator<T>, vararg elements: T): MutableVector<T> {
            return of(elements.asList(), mc)
        }

        /**
         * Returns the result of `mat * v`, it is required the vector's size is
         * equal to the matrix's column count. This method will treat the vector as a
         * row vector.
         *
         * @param mat a matrix
         * @param v   a vector
         * @return `mat * v` as a column vector, which has the length of `mat.getRowCount()`.
         */
        @JvmStatic
        fun <T> multiplyToVector(mat: AbstractMatrix<T>, v: AbstractVector<T>): Vector<T> {
            require(mat.column == v.size) { "mat.column != v.size" }
            val mc = mat.calculator
            val result = Array<Any?>(mat.row) { k ->
                var t = mc.zero
                for (j in mat.colIndices) {
                    t = mc.eval { t + mat[k, j] * v[j] }
                }
                t
            }
            return AVector(mc, result)
        }

        /**
         * Returns the result of v * mat, it is required the vector's size is
         * equal to the matrix's row count. This method will ignore whether the vector is
         * a column vector.
         *
         * @param v   a vector
         * @param mat a matrix
         * @return `v * mat` as a row vector, which has the length of `mat.getColumnCount()`.
         */
        @JvmStatic
        fun <T> multiplyByVector(v: AbstractVector<T>, mat: AbstractMatrix<T>): Vector<T> {
            require(mat.row == v.size) { "mat.row != v.size" }
            val mc = mat.calculator
            val result = Array<Any?>(mat.column) { k ->
                var t = mc.zero
                for (i in mat.rowIndices) {
                    t = mc.eval { t + v[i] * mat[i, k] }
                }
                t
            }
            return AVector(mc, result)
        }


        /**
         * Orthogonalizes the given vectors by using Schmidt method, it is required that the
         * given vectors are linear irrelevant.
         *
         * @param vs an array of vectors
         * @return a new list of vectors
         */
        @JvmStatic
        @SafeVarargs
        fun <T> orthogonalize(vs: List<Vector<T>>): List<Vector<T>> {
            //vs    : a1,a2,a3 ... an
            //list  : b1,b2,b3 ... bn
            //temp1 : -b1/b1^2 ... -bn/bn^2
            //temp2 : used when adding
            val n = vs.size
            if (n < 2) {
                return vs
            }
            val size = vs[0].size
            require(vs.all { it.size == size })
            val mc = vs[0].calculator

            val list: MutableList<Vector<T>> = ArrayList(n)
            val us = ArrayList<Vector<T>>(n - 1)
            //us: b/b^2
            list.add(vs[0])
            //b1 = a1
            for (i in 1 until n) {
                val u = vs[i - 1]
                us += u.divide(mc.negate(u.normSq()))

                val v = copyOf(vs[i])
                for (j in 0 until i) {
                    v.addMulAssign(us[j].inner(v), list[j])
                }
                list.add(v)
            }
            return list
        }

        @JvmStatic
        @SafeVarargs
        fun <T> orthogonalizeAndUnit(vs: List<Vector<T>>): List<Vector<T>> {
            return orthogonalize(vs).map { it.unitize() }
        }

        /**
         * Returns the vector of minimum norm in the Z-span of `a,b`, that is,
         * a vector `v` such that
         * <pre>|v| = min {|ma + nb| : m, n in Z}</pre>
         */
        fun shortestSpan(v1: Vector<Double>, v2: Vector<Double>): Vector<Double> {
            var u = v1
            var v = v2
            require(u.size != v.size) { "a.size != b.size!" }
            var a = u.normSq()
            var b = v.normSq()
            if (a < b) {
                val t = u
                u = v
                v = t
                val t2 = a
                a = b
                b = t2
            }
            while (true) {
                val n = u.inner(v)
                val r = (n / b).roundToLong()
                val T = a - 2 * r * n + r * r * b
                if (T >= b) {
                    return v
                }
                val t = u - v.multiply(r)
                u = v
                v = t
                a = b
                b = T
            }
        }

        fun <T> copyOf(v: GenVector<T>, mc: RingCalculator<T>): MutableVector<T> {
            return AVector.copyOf(v, mc)
        }

        fun <T> copyOf(v: AbstractVector<T>): MutableVector<T> {
            return copyOf(v, v.calculator)
        }

        fun <T> isLinearDependent(vs: List<AbstractVector<T>>): Boolean {
            val matrix = Matrix.fromVectors(vs)
            return matrix.rank() < vs.size
        }

        /**
         * Finds a maximal linear independent subset from the given vectors and
         * return them as a vector basis.
         */
        fun <T> maxIndependent(vs: List<AbstractVector<T>>): VectorBasis<T> {
            val matrix = Matrix.fromVectors(vs).toMutable()
            val pivots = MatrixImpl.toUpperTriangle(matrix)
            return VectorBasis.createBaseWithoutCheck(pivots.map { copyOf(vs[it]) })
        }
    }

}

internal object VectorImpl {

    @Suppress("UNCHECKED_CAST")
    private inline fun <T> apply2(x: Vector<T>, y: Vector<T>, f: (T, T) -> T): AVector<T> {
        require(x.isSameSize(y))
        val data = Array<Any?>(x.size) { k ->
            f(x[k], y[k])
        }
        return AVector(x.calculator, data)
    }

    private inline fun <T> apply1(x: Vector<T>, f: (T) -> T): AVector<T> {
        val newData = Array<Any?>(x.size) { k ->
            @Suppress("UNCHECKED_CAST")
            f(x[k])
        }
        return AVector(x.calculator, newData)
    }

    fun <T> add(x: Vector<T>, y: Vector<T>): AVector<T> {
        val mc = x.calculator
        return apply2(x, y, mc::add)
    }

    fun <T> subtract(x: Vector<T>, y: Vector<T>): AVector<T> {
        val mc = x.calculator
        return apply2(x, y, mc::subtract)
    }

    fun <T> negate(x: Vector<T>): AVector<T> {
        val mc = x.calculator
        return apply1(x, mc::negate)
    }

    fun <T> multiply(x: Vector<T>, k: T): AVector<T> {
        val mc = x.calculator
        return apply1(x) { mc.multiply(k, it) }
    }

    fun <T> divide(x: Vector<T>, k: T): AVector<T> {
        val mc = x.calculator as FieldCalculator
        return apply1(x) { mc.divide(k, it) }
    }

}

abstract class MutableVector<T>(mc: RingCalculator<T>, size: Int) : Vector<T>(mc, size) {

    abstract operator fun set(i: Int, x: T)

    open operator fun plusAssign(y: Vector<T>) {
        val mc = calculator
        for (idx in indices) {
            this[idx] = mc.add(this[idx], y[idx])
        }
    }

    open operator fun minusAssign(y: Vector<T>) {
        val mc = calculator
        for (idx in indices) {
            this[idx] = mc.subtract(this[idx], y[idx])
        }
    }

    open operator fun timesAssign(k: T) {
        val mc = calculator
        for (idx in indices) {
            this[idx] = mc.multiply(k, this[idx])
        }
    }

    open operator fun divAssign(k: T) {
        val mc = calculator as FieldCalculator
        for (idx in indices) {
            this[idx] = mc.divide(this[idx], k)
        }
    }

    /**
     *
     *     this = this + k * y
     */
    open fun addMulAssign(k: T, y: Vector<T>) {
        val x = this
        calculator.eval {
            for (i in 0 until size) {
                x[i] += k * y[i]
            }
        }
    }

    /**
     * Transforms all the elements in-place with the function [f].
     */
    open fun transform(f: (T) -> T) {
        for (i in indices) {
            this[i] = f(this[i])
        }
    }


}

class AVector<T>
internal constructor(mc: RingCalculator<T>, val data: Array<Any?>)
    : MutableVector<T>(mc, data.size) {

    override fun set(i: Int, x: T) {
        data[i] = x
    }

    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): AVector<N> {
        val newData = Array<Any?>(size) {
            @Suppress("UNCHECKED_CAST")
            mapper.apply(data[it] as T)
        }
        return AVector(newCalculator as RingCalculator, newData)
    }

    override fun elementSequence(): Sequence<T> {
        @Suppress("UNCHECKED_CAST")
        return data.asSequence() as Sequence<T>
    }

    override fun applyAll(f: (T) -> T): AVector<T> {
        return apply1(this, f)
    }

    override fun get(i: Int): T {
        @Suppress("UNCHECKED_CAST")
        return data[i] as T
    }

    override fun toList(): List<T> {
        @Suppress("UNCHECKED_CAST")
        return data.asList() as List<T>
    }

    override fun add(y: Vector<T>): Vector<T> {
        if (y is AVector) {
            val mc = calculator
            return apply2(this, y, mc::add)
        }
        return super.add(y)
    }

    override fun negate(): Vector<T> {
        val mc = calculator
        return apply1(this, mc::negate)
    }

    override fun multiply(k: T): Vector<T> {
        val mc = calculator
        return apply1(this) { mc.multiply(k, it) }
    }

    override fun divide(k: T): Vector<T> {
        val mc = calculator as FieldCalculator
        return apply1(this) { mc.divide(it, k) }
    }

    override fun multiply(n: Long): Vector<T> {
        val mc = calculator
        return apply1(this) { x -> mc.multiplyLong(x, n) }
    }

    override fun resize(size: Int): Vector<T> {
        val result = data.copyOf(size)
        if (size > this.size) {
            val mc = calculator
            result.fill(mc.zero, this.size)
        }
        return AVector(calculator, result)
    }


    companion object {

        @Suppress("UNCHECKED_CAST")
        private inline fun <T> apply2(x: AVector<T>, y: AVector<T>, f: (T, T) -> T): AVector<T> {
            x.checkSameSize(y)
            val d1 = x.data
            val d2 = y.data
            val newData = Array<Any?>(d1.size) { k ->
                f(d1[k] as T, d2[k] as T)
            }

            return AVector(x.calculator, newData)
        }

        private inline fun <T> apply1(x: AVector<T>, f: (T) -> T): AVector<T> {
            val data = x.data
            val ndata = Array<Any?>(data.size) { k ->
                @Suppress("UNCHECKED_CAST")
                f(data[k] as T)
            }
            return AVector(x.calculator, ndata)
        }


        fun <T> constant(x: T, size: Int, mc: RingCalculator<T>): AVector<T> {
            require(size > 0)
            val data = ArraySup.fillArr(size, x, Any::class.java)
            return AVector(mc, data)
        }

        fun <T> zero(size: Int, mc: RingCalculator<T>): AVector<T> {
            return constant(mc.zero, size, mc)
        }

        fun <T> ones(size: Int, mc: UnitRingCalculator<T>): AVector<T> {
            return constant(mc.one, size, mc)
        }

        fun <T> of(size: Int, mc: RingCalculator<T>, supplier: (Int) -> T): AVector<T> {
            require(size > 0)
            val data = Array<Any?>(size) {
                supplier(it)
            }
            return AVector(mc, data)
        }

        fun <T> of(elements: List<T>, mc: RingCalculator<T>): AVector<T> {
            return AVector(mc, elements.toTypedArray())
        }

        fun <T> copyOf(v: GenVector<T>, mc: RingCalculator<T>): AVector<T> {
            return if (v is AVector) {
                AVector(v.calculator, v.data.clone())
            } else {
                val data = Array<Any?>(v.size) { v[it] }
                AVector(mc, data)
            }
        }

        fun <T> copyOfRange(newSize: Int, v: GenVector<T>, start: Int, end: Int, destIdx: Int, mc: RingCalculator<T>): AVector<T> {
            val result = zero(newSize, mc)
            if (v is AVector) {
                v.data.copyInto(result.data, destIdx, start, end)
            } else {
                for (i in start until end) {
                    result[i + destIdx] = v[i]
                }
            }
            return result
        }

        /**
         * Returns a unit column vector of the given length.
         */
        fun <T> unitVector(length: Int, unitIndex: Int, mc: UnitRingCalculator<T>): AVector<T> {
            val result = zero(length, mc)
            result[unitIndex] = mc.one
            return result
        }


        /**
         * Returns a list of all unit vectors of the given length.
         */
        fun <T> unitVectors(length: Int, mc: UnitRingCalculator<T>): List<AVector<T>> {
            return (0 until length).map { unitVector(length, it, mc) }
        }
    }
}


/**
 * Converts this vector as a column matrix whose shape is `(size, 1)`.
 */
fun <T> Vector<T>.asColumnMatrix(): Matrix<T> {
    return Matrix.of(size, 1, calculator, toList())
}

/**
 * Converts this vector as a row matrix whose shape is `(1, size)`.
 */
fun <T> Vector<T>.asRowMatrix(): Matrix<T> {
    return Matrix.of(1, size, calculator, toList())
}