package cn.ancono.math.numberModels

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObject
import cn.ancono.math.MathObjectExtend
import cn.ancono.math.MathUtils
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.discrete.combination.Permutation
import cn.ancono.math.discrete.combination.Permutations
import cn.ancono.math.numberModels.Tensor.Companion.NEW_AXIS
import cn.ancono.math.numberModels.TensorUtils.checkShape
import cn.ancono.math.numberModels.api.AlgebraModel
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import cn.ancono.utilities.IterUtils
import java.lang.IllegalArgumentException
import java.lang.IndexOutOfBoundsException
import java.lang.StringBuilder
import java.util.*
import java.util.function.Function
import kotlin.collections.ArrayList

typealias Index = IntArray

/**
 * Created at 2019/9/12 11:11
 *
 * Specified by lyc at 2021-03-31 22:26
 * @author  lyc
 */
/**
 * ## Mathematical Description
 *
 * A tensor is an element in the tensor product space of several (finite dimensional) linear spaces `V_1,...,V_m` on
 * a field `T`. Denote `V = V_1⊗...⊗V_m` the tensor product space, then `V` is also a linear space on `T`.
 *
 * Assume `dim V_i = n_i`, and `v(i,1),...,v(i,n_i)` are the basis of `V_i`.
 * We have `dim V = n_1*...*n_m`, and the basis of `V` are `v(1,r_1)⊗v(2,r_2)⊗...⊗v(m,r_m)`,
 * and each element in `V` can be written as their linear combination.
 *
 * We call the dimensions of the component linear spaces `(n_1,...,n_m)` the shape of the tensor,
 * and `m` the dimension of the tensor.
 *
 * ## Programming Description
 *
 * A tensor can be viewed as a multi-dimensional array of type `T`. Its shape is the lengths of arrays in the
 * corresponding dimensions. We call each of the dimension an 'axis' and also use 'dimension' to indicate
 * the number of dimensions.
 *
 *
 *
 */
interface Tensor<T : Any> : MathObject<T>, AlgebraModel<T, Tensor<T>> {
    //Created by lyc at 2021-04-06 22:12

    /**
     * Gets a copy the shape array of this tensor.
     */
    val shape: IntArray


    /**
     * Determines whether this tensor has the same shape as `y`.
     */
    fun isSameShape(y: Tensor<*>): Boolean {
        return shape.contentEquals(y.shape)
    }

    /**
     * The dimension of this tensor, which is equal to the length of [shape].
     */
    val dim: Int

    /**
     * Gets the number of elements in this tensor, which is equal to the product of [shape].
     */
    val size: Int

    /**
     * Gets a read-only-traversable sequence of the indices of this tensor.
     * The indices iterates the last axis first.
     *
     * This method is generally equal to `IterUtils.prodIdxN(shape)`
     *
     * @see IterUtils.prodIdxN
     */
    val indices: Sequence<Index>

    /**
     * Gets an element in this tensor according to the index.
     *
     * @param idx the index, it is required that `0 <= idx < shape`
     */
    operator fun get(idx: Index): T

    /*
    Math operations:
     */

    /**
     * Returns the element-wise sum of this tensor and `y`.
     *
     * The sum of two tensor `x,y` has the
     * shape of `max(x.shape, y.shape)`, here `max` means element-wise maximum of two arrays.
     */
    override fun add(y: Tensor<T>): Tensor<T> {
        return TensorUtils.add(this, y)
    }

    /**
     * Returns the negate of this tensor.
     *
     */
    override fun negate(): Tensor<T> {
        return TensorUtils.negate(this)
    }

    /**
     * Returns the sum of this tensor and `y`.
     *
     * The sum of two tensor `x,y` has the
     * shape of `max(x.shape, y.shape)`, here `max` means element-wise maximum of two arrays.
     */
    override fun subtract(y: Tensor<T>): Tensor<T> {
        return TensorUtils.subtract(this, y)
    }

    /**
     * Returns the result of multiplying this tensor with a scalar.
     */
    override fun multiply(k: T): Tensor<T> {
        return TensorUtils.multiply(this, k)
    }

    /**
     * Returns the **element-wise** product of this tensor and `y`.
     *
     */
    override fun multiply(y: Tensor<T>): Tensor<T> {
        return TensorUtils.multiply(this, y)
    }

    /**
     * Returns the **element-wise** division of this tensor and `y`.
     *
     * @throws ArithmeticException if zero-division happens
     */
    fun divide(y: Tensor<T>): Tensor<T> {
        return TensorUtils.divide(this, y)
    }


//    fun einsum()

    /**
     * Returns the wedge product of this tensor and `y`.
     *
     * The tensor product of two tensor `z = x⊗y` has the
     * shape of `x.shape + y.shape`, here `+` means concatenation of two arrays.
     *
     * The `[i,j]` element of `z` is equal to the scalar product of `x[i]` and `y[j]`, that is,
     *
     *     z[i,j] = x[i] * y[j]
     *
     */
    fun wedge(y: Tensor<T>): Tensor<T> {
        return TensorUtils.wedge(this, y)
    }

    /**
     * Returns the sum of all the elements in this tensor.
     */
    fun sum(): T {
        return elementSequence().reduce(mathCalculator::add)
    }

    /*
    Array-like operations:
     */

    /**
     * Gets the elements in this tensor as a sequence. The order is the same as [indices].
     */
    fun elementSequence(): Sequence<T> {
        return indices.map { this[it] }
    }

    /**
     * Flatten this tensor to a list. The order of the elements is the same as [elementSequence].
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
     * Applies the given function to this tensor.
     */
    fun applyAll(f: (T) -> T): Tensor<T> {
        return mapTo(mathCalculator, f)
    }


    /**
     * Returns a view of this tensor according to the slicing ranges or indices.
     * It is required that an element is either
     *  * an integer, `Int`: to get the corresponding elements in that axis;
     *  * a range, `IntProgression`: to get a slice of elements in that axis;
     *  * a special object, [Tensor.NEW_AXIS]: to indicate a new axis should be inserted;
     *  * `null`: to keep the axis as it is.
     *
     */
    fun slice(vararg slices: Any?): Tensor<T> {
        return slice(slices.asList())
    }

    /**
     * Returns a view of this tensor according to the slicing ranges or indices.
     *
     * @see [slice]
     */
    fun slice(slices: List<Any?>): Tensor<T>


//    /**
//     * The operator-overloading version of the method [slice].
//     *
//     * @see [slice]
//     */
//    operator fun get(vararg ranges: Any?): Tensor<T>{
//    return slice(ranges.asList())
//    }
//


    /**
     * Returns a axis-permuted view of this tensor.
     * The `i`-th axis in the resulting tensor corresponds to the `p.apply(i)`-th axis in this tensor.
     */
    fun permute(p: Permutation): Tensor<T>

    /**
     * Returns a axis-permuted view of this tensor.
     * The `i`-th axis in the resulting tensor corresponds to the `newAxis[i]`-th axis in this tensor.
     *
     * @param newAxis its size should be equal to `this.dim`.
     */
    fun permute(vararg newAxis: Int): Tensor<T> {
        return permute(Permutations.valueOf(*newAxis))
    }

    /**
     * Transposes two axes in this tensor.
     */
    fun transpose(axis1: Int, axis2: Int): Tensor<T> {
        return permute(Permutations.swap(dim, axis1, axis2))
    }

    /*
    Vector model for T:
     */


    override fun isLinearRelevant(v: Tensor<T>): Boolean {
        return TensorUtils.isLinearDependent(this, v)
    }

    /*
    General methods for MathObject:
     */

    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): Tensor<N>

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is Tensor) {
            return false
        }
        val mc = mathCalculator
        return indices.all { idx -> mc.isEqual(get(idx), obj[idx]) }
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
        val mc = mathCalculator
        return joinTo(StringBuilder()) {
            nf.format(it, mc)
        }.toString()
    }


    companion object {

        /**
         * Used in [slice] to indicate a new axis of length one.
         */
        const val NEW_AXIS = "NEW_AXIS";

        private fun checkValidShape(shape: IntArray) {
            require(shape.isNotEmpty())
            require(shape.all { s -> s > 0 })
        }

        /**
         * Creates a tensor with all zeros.
         *
         * @param shape a non-empty array of positive integers
         */
        fun <T : Any> zeros(mc: MathCalculator<T>, vararg shape: Int): MutableTensor<T> {
            return constants(mc.zero, mc, *shape)
        }

        /**
         * Creates a tensor with all ones.
         *
         * @param shape a non-empty array of positive integers
         */
        fun <T : Any> ones(mc: MathCalculator<T>, vararg shape: Int): MutableTensor<T> {
            return constants(mc.one, mc, *shape)
        }

        /**
         * Creates a tensor filled with the given constant.
         *
         * @param shape a non-empty array of positive integers
         */
        fun <T : Any> constants(c: T, mc: MathCalculator<T>, vararg shape: Int): MutableTensor<T> {
            checkValidShape(shape)
            return ATensor.constant(c, shape.clone(), mc)
        }

        /**
         * Creates a tensor with a supplier function that takes the index as parameter.
         *
         * @param shape a non-empty array of positive integers
         */
        fun <T : Any> of(shape: IntArray, mc: MathCalculator<T>, supplier: (Index) -> T): MutableTensor<T> {
            checkValidShape(shape)
            return ATensor.buildFromSequence(mc, shape.clone(), IterUtils.prodIdxN(shape).map(supplier))
        }

        /**
         * Creates a 2-dimensional tensor from a matrix. The `(i,j)`-th element in the returned tensor
         * is equal to `(i,j)`-th element in `m`.
         */
        fun <T : Any> fromMatrix(m: Matrix<T>): MutableTensor<T> {
            return ATensor.fromMatrix(m)
        }


//        fun <T : Any> einsum(x: Tensor<T>, y: Tensor<T>, xAxis: IntArray, yAxis: IntArray): Tensor<T> {
////            val amX
//            val amXX = arrayListOf<Int>()
//            require(xAxis.size == yAxis.size)
//
//
//            val shape = run {
//                val sh = arrayListOf<Int>()
//                sh.toIntArray()
//            }
//            val mc = x.mathCalculator
//            val result = ATensor.constant(mc.zero, shape, mc)
//            val data = result.data
//            var pos = 0
//
////            fun buildIdxTo(idxR : Index, )
//            val fullIdxX = IntArray(x.dim)
//            val fullIdxY = IntArray(y.dim)
//            val rDim = shape.size
//            val mDim = 1
//            for (idxR in result.indices) {
//                for (l in 0 until rDim) {
//                    fullIdxX[amRX[l]] = idxR[l]
//                    fullIdxY[amRY[l]] = idxR[l]
//                }
//                var res = mc.zero
//                for(idxT : Index in mulIdx){
//                    for (l in 0 until mDim) {
//                        fullIdxX[xAxis[l]] = idxT[l]
//                        fullIdxY[yAxis[l]] = idxT[l]
//                    }
//                }
//                pos++
//            }
////            for(idx in IterUtils)
//        }
    }

}

/**
 * A vararg version of get.
 */
operator fun <T : Any> Tensor<T>.get(vararg idx: Int): T {
    return this[idx]
}

interface MutableTensor<T : Any> : Tensor<T> {
    operator fun set(idx: Index, v: T)

    /**
     * Sets all the elements in this tensor to be the same value `v`.
     */
    fun setAll(v: T) {
        for (idx in indices) {
            set(idx, v)
        }
    }

    /**
     * Sets all the elements in this tensor according to `t`.
     */
    fun setAll(t: Tensor<T>) {
        for (idx in indices) {
            set(idx, t[idx])
        }
    }

    operator fun plusAssign(y: Tensor<T>)

    operator fun minusAssign(y: Tensor<T>)

    operator fun timesAssign(y: Tensor<T>)

    operator fun divAssign(y: Tensor<T>)

    override fun applyAll(f: (T) -> T): MutableTensor<T> {
        return mapTo(mathCalculator, f)
    }

    override fun add(y: Tensor<T>): MutableTensor<T> {
        return TensorUtils.add(this, y)
    }

    override fun negate(): MutableTensor<T> {
        return TensorUtils.negate(this)
    }

    override fun subtract(y: Tensor<T>): MutableTensor<T> {
        return TensorUtils.subtract(this, y)
    }

    override fun multiply(k: T): MutableTensor<T> {
        return TensorUtils.multiply(this, k)
    }

    override fun multiply(y: Tensor<T>): MutableTensor<T> {
        return TensorUtils.multiply(this, y)
    }

    override fun divide(y: Tensor<T>): MutableTensor<T> {
        return TensorUtils.divide(this, y)
    }


    override fun wedge(y: Tensor<T>): MutableTensor<T> {
        return TensorUtils.wedge(this, y)
    }

    override fun slice(slices: List<Any?>): MutableTensor<T>


    /**
     * Sets all the element in the slice to be `v`.
     * This method is generally equal to `slice(slices.asList()).setAll(v)`.
     *
     *
     */
    operator fun set(vararg slices: Any?, v: T) {
        if (slices.all { it is Int }) {
            set(IntArray(slices.size) { i -> slices[i] as Int }, v)
        } else {
            slice(slices.asList()).setAll(v)
        }
    }

    /**
     * Sets all the element in the slice to be the same as `v`.
     * This method is generally equal to `slice(slices.asList()).setAll(v)`.
     */
    operator fun set(vararg slices: Any?, v: Tensor<T>) {
        slice(slices.asList()).setAll(v)
    }


    override fun permute(p: Permutation): MutableTensor<T>

    override fun permute(vararg newAxis: Int): MutableTensor<T> {
        return permute(Permutations.valueOf(*newAxis))
    }

    override fun transpose(axis1: Int, axis2: Int): MutableTensor<T> {
        return permute(Permutations.swap(dim, axis1, axis2))
    }

    fun copy(): MutableTensor<T>

    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): MutableTensor<N>


}

fun <T : Any, A : Appendable> Tensor<T>.joinToL(buffer: A, separators: List<CharSequence>,
                                                prefixes: List<CharSequence>,
                                                postfixes: List<CharSequence>,
                                                limits: IntArray, truncated: List<CharSequence>,
                                                transform: (T) -> CharSequence): A {
    val dim = this.dim
    val shape = this.shape
    val idx = IntArray(shape.size)
    var level = 0
//    val counts =
//    for (l in 0 until dim) {
//        buffer.append(prefixes[l])
//    }
    Outer@
    while (true) {
        while (idx[level] == shape[level]) {
            buffer.append(postfixes[level])
            idx[level] = 0
            level--
            if (level < 0) {
                break@Outer
            }
            idx[level]++
        }
        if (limits[level] >= 0 && idx[level] + 1 > limits[level]) {
            buffer.append(separators[level])
            buffer.append(truncated[level])
            idx[level] = shape[level] - 1
        }


        if (idx[level] == 0) {
            buffer.append(prefixes[level])
        } else {
            buffer.append(separators[level])
        }
        if (level == dim - 1) {
            buffer.append(transform(this[idx]))
            idx[level]++
        } else {
            level++
            continue
        }
    }
    return buffer
}


fun <T : Any, A : Appendable> Tensor<T>.joinTo(buffer: A, separator: CharSequence = ", ",
                                               prefix: CharSequence = "[",
                                               postfix: CharSequence = "]",
                                               limit: Int = -1, truncated: CharSequence = "...",
                                               transform: ((T) -> CharSequence)? = null): A {
    val dim = this.dim
    val seps = run {
        val t = ArrayList<CharSequence>(dim)

        val spaces = " ".repeat(prefix.length)
        var padded = "\n\n"
        for (i in 1 until dim - 1) {
            padded += spaces
            t += padded
        }
        if (dim > 1) {
            t += padded.substring(1) + spaces
        }
        t += separator
        t
    }

    val pres = Collections.nCopies(dim, prefix)


//    val pres = Collections.nCopies(dim, prefix)
    val posts = Collections.nCopies(dim, postfix)
    val limits = IntArray(dim) { limit }

//    val truns = ArrayList<CharSequence>(dim)
//    val vtruncated = truncated.asIterable().joinToString(separator = "\n")
//    repeat(dim - 1) { truns.add(vtruncated) }
//    truns.add(truncated)
    val truns = Collections.nCopies(dim, truncated)
    val trans = transform ?: Any::toString
    return this.joinToL(buffer, seps, pres, posts, limits, truns, trans)
}

fun <T : Any> Tensor<T>.joinToString(separator: CharSequence = " ",
                                     prefix: CharSequence = "[",
                                     postfix: CharSequence = "]",
                                     limit: Int = -1, truncated: CharSequence = "...",
                                     transform: ((T) -> CharSequence)? = null): String {
    return this.joinTo(StringBuilder(), separator, prefix, postfix, limit, truncated, transform).toString()
}


abstract class AbstractTensor<T : Any>(
        mc: MathCalculator<T>,
        /**
         * The shape of the tensor, it should not be modified
         */
        protected val sh: IntArray) : MathObjectExtend<T>(mc), Tensor<T> {

    //Created by lyc at 2021-03-31 20:39


    override val shape: IntArray
        get() = sh.clone()

    override fun isSameShape(y: Tensor<*>): Boolean {
        if (y is AbstractTensor) {
            return sh.contentEquals(y.sh)
        }
        return sh.contentEquals(y.shape)
    }


    override val dim: Int
        get() = sh.size


    override val size: Int
        get() = MathUtils.product(shape)


    override val indices: Sequence<Index> = IterUtils.prodIdxN(sh)

    /**
     * Checks whether `idx` is a valid index for this tensor, throws exception if necessary.
     */
    protected fun checkIdx(idx: Index) {
        for (i in 0 until dim) {
            if (!(0 <= idx[i] && idx[i] < sh[i])) {
                throw IndexOutOfBoundsException("Tensor index out of bound at axis $i: " +
                        "Shape=${sh.contentToString()}, Index=${idx.contentToString()}")
            }
        }
    }

    /**
     * Gets the element in this tensor. The index is already checked valid.
     */
    protected abstract fun getChecked(idx: Index): T


    /**
     * Gets an element in this tensor according to the index.
     *
     * @param idx the index, it is required that `0 <= idx < shape`
     */
    override operator fun get(idx: Index): T {
        checkIdx(idx)
        return getChecked(idx)
    }

    /*
    Math operations:
     */

    /**
     * Determines whether this tensor is all-zero.
     */
    override fun isZero(): Boolean {
        return elementSequence().all { mc.isZero(it) }
    }

    protected fun computeSliceView(slices: List<Any?>)
            : Triple<IntArray, MutableList<IntProgression>, IntArray> {
        // remark: we need union type here
        var l = 0 // in this tensor
        val ns = arrayListOf<Int>()
        val ranges = arrayListOf<IntProgression>()
        val sh = this.sh
        val am = arrayListOf<Int>()
        for (t in slices) {
            if (t !== NEW_AXIS) {
                require(l < dim) { "Too many indices!" }
            }
            when (t) {
                is Int -> {

                    val i = MathUtils.mod(t, sh[l])
                    ranges.add(i..i)
                    l++
                }
                is IntProgression -> {
                    val r = IntProgression.fromClosedRange(
                            MathUtils.mod(t.first, sh[l]),
                            MathUtils.mod(t.last, sh[l]),
                            t.step
                    )
                    require(!r.isEmpty())
                    ranges.add(r)
                    ns.add((r.last - r.first) / r.step + 1)
                    am.add(l)
                    l++
                }
                null -> {
                    am.add(l)
                    ranges.add(0 until sh[l])
                    ns.add(sh[l])
                    l++
                }
                NEW_AXIS -> {
                    ns.add(1)
                    am.add(-1)
                }
                else -> {
                    throw IllegalArgumentException("Not supported slice for $t")
                }
            }
        }
        while (l < dim) {
            am.add(l)
            ranges.add(0 until sh[l])
            ns.add(sh[l])
            l++
        }
        if (ns.isEmpty()) {
            // return a 1-d tensor instead
            am.add(-1)
            ns.add(1)
        }
        return Triple(am.toIntArray(), ranges, ns.toIntArray())
    }


    /**
     * Returns a view of this tensor according to the slicing ranges or indices.
     * It is required that the elements are either `Int`, `IntProgression` or `NEW_AXIS`.
     */
    override fun slice(slices: List<Any?>): Tensor<T> {
        val (am, ranges, ns) = computeSliceView(slices)
        return SlicedView(this, ranges, am, ns)
    }

    override fun permute(p: Permutation): Tensor<T> {
        require(p.size() == dim)
        val sh = this.shape
        val ranges = shape.map { 0 until it }
        return SlicedView(this, ranges, p.array, p.apply(sh))
    }


    /*
    General methods for MathObject
     */

    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): Tensor<N> {
        return ATensor.buildFromSequence(newCalculator, sh, elementSequence().map { mapper.apply(it) })
    }
}

abstract class AbstractMutableTensor<T : Any>(mc: MathCalculator<T>, shape: IntArray) : AbstractTensor<T>(mc, shape), MutableTensor<T> {
    override fun applyAll(f: (T) -> T): MutableTensor<T> {
        return mapTo(mc, f)
    }


    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): MutableTensor<N> {
        return ATensor.buildFromSequence(newCalculator, sh, elementSequence().map { mapper.apply(it) })
    }

    override fun copy(): MutableTensor<T> {
        return ATensor.copyOf(this)
    }


    override fun slice(slices: List<Any?>): MutableTensor<T> {
        val (am, ranges, ns) = computeSliceView(slices)
        return MutableSliceView(this, ranges, am, ns)
    }

    override fun permute(p: Permutation): MutableTensor<T> {
        require(p.size() == dim)
        val sh = this.shape
        val ranges = shape.map { 0 until it }
        return MutableSliceView(this, ranges, p.array, p.apply(sh))
    }

    override fun plusAssign(y: Tensor<T>) {
        for (idx in indices) {
            this[idx] = mc.add(this[idx], y[idx])
        }
    }

    override fun minusAssign(y: Tensor<T>) {
        for (idx in indices) {
            this[idx] = mc.subtract(this[idx], y[idx])
        }
    }

    override fun timesAssign(y: Tensor<T>) {
        for (idx in indices) {
            this[idx] = mc.multiply(this[idx], y[idx])
        }
    }

    override fun divAssign(y: Tensor<T>) {
        for (idx in indices) {
            this[idx] = mc.divide(this[idx], y[idx])
        }
    }

    override fun setAll(v: T) {
        for (idx in indices) {
            set(idx, v)
        }
    }

    override fun setAll(t: Tensor<T>) {
        for (idx in indices) {
            set(idx, t[idx])
        }
    }


    override fun add(y: Tensor<T>): MutableTensor<T> {
        return super<MutableTensor>.add(y)
    }

    override fun negate(): MutableTensor<T> {
        return super<MutableTensor>.negate()
    }

    override fun subtract(y: Tensor<T>): MutableTensor<T> {
        return super<MutableTensor>.subtract(y)
    }

    override fun multiply(k: T): MutableTensor<T> {
        return super<MutableTensor>.multiply(k)
    }

    override fun multiply(y: Tensor<T>): MutableTensor<T> {
        return super<MutableTensor>.multiply(y)
    }

    override fun divide(y: Tensor<T>): MutableTensor<T> {
        return super<MutableTensor>.divide(y)
    }

    override fun wedge(y: Tensor<T>): MutableTensor<T> {
        return super<MutableTensor>.wedge(y)
    }

    override fun permute(vararg newAxis: Int): MutableTensor<T> {
        return super<MutableTensor>.permute(*newAxis)
    }

    override fun transpose(axis1: Int, axis2: Int): MutableTensor<T> {
        return super<MutableTensor>.transpose(axis1, axis2)
    }
}


/**
 * An array-implementation of tensor.
 */
class ATensor<T : Any>(mc: MathCalculator<T>, shape: IntArray, val data: Array<T>) : AbstractMutableTensor<T>(mc, shape) {
    private val shifts: IntArray = IntArray(dim)

    init {
        var s = 1
        for (i in (dim - 1) downTo 0) {
            shifts[i] = s
            s *= shape[i]
        }
    }

    override val size: Int
        get() = data.size

    private fun toPos(idx: Index): Int {
        var pos = 0
        for (i in 0 until dim) {
            pos += idx[i] * shifts[i]
        }
        return pos
    }

    private fun toIdx(pos0: Int): Index {
        val idx = IntArray(dim)
        var pos = pos0
        for (i in 0 until dim) {
            val t = pos / shifts[i]
            pos -= t * shifts[i]
            idx[i] = t
        }
        return idx
    }


    override fun getChecked(idx: Index): T {
        return data[toPos(idx)]
    }

    override fun elementSequence(): Sequence<T> {
        return data.asSequence()
    }

    override fun flattenToList(): List<T> {
        return data.asList()
    }

    override fun copy(): ATensor<T> {
        return ATensor(mc, sh, data.clone())
    }

    override fun set(idx: Index, v: T) {
        checkIdx(idx)
        data[toPos(idx)] = v
    }

    override fun setAll(v: T) {
        Arrays.fill(data, v)
    }

//    /**
//     * A low-level api for direct access.
//     */
//    fun setPos(pos : Int, v : T){
//        data[pos] = v
//    }


    private inline fun inlineApplyAll(f: (T) -> T): ATensor<T> {
        for (i in 0 until size) {
            data[i] = f(data[i])
        }
        return this
    }

    override fun applyAll(f: (T) -> T): MutableTensor<T> {
        for (i in 0 until size) {
            data[i] = f(data[i])
        }
        return this
    }

    override fun isZero(): Boolean {
        return data.all { mc.isZero(it) }
    }


    override fun add(y: Tensor<T>): MutableTensor<T> {
        if (y is ATensor) {
            return apply2(this, y, mc::add)
        }
        return super.add(y)

    }

    override fun negate(): MutableTensor<T> {
        return inlineApplyAll(mc::negate)
    }

    override fun subtract(y: Tensor<T>): MutableTensor<T> {
        if (y is ATensor) {
            return apply2(this, y, mc::subtract)
        }
        return super.add(y)
    }

    override fun multiply(k: T): MutableTensor<T> {
        return applyAll { t -> mc.multiply(k, t) }.copy()
    }

    override fun multiply(y: Tensor<T>): MutableTensor<T> {
        if (y is ATensor) {
            return apply2(this, y, mc::multiply)
        }
        return super.multiply(y)
    }

    override fun divide(y: Tensor<T>): MutableTensor<T> {
        if (y is ATensor) {
            return apply2(this, y, mc::divide)
        }
        return super.divide(y)
    }

    private inline fun apply2InPlace(y: Tensor<T>, f: (T, T) -> T) {
        checkShape(this, y)
        if (y is ATensor) {
            val d1 = data
            val d2 = y.data
            for (i in 0 until size) {
                d1[i] = f(d1[i], d2[i])
            }
        } else {
            var pos = 0
            val data = this.data
            for (s in y.elementSequence()) {
                data[pos] = mc.add(data[pos], s)
                pos++
            }
        }
    }

    override fun plusAssign(y: Tensor<T>) {
        return apply2InPlace(y, mc::add)

    }

    override fun minusAssign(y: Tensor<T>) {
        return apply2InPlace(y, mc::subtract)
    }

    override fun timesAssign(y: Tensor<T>) {
        return apply2InPlace(y, mc::multiply)
    }

    override fun divAssign(y: Tensor<T>) {
        return apply2InPlace(y, mc::divide)
    }


    @Suppress("UNCHECKED_CAST")
    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): ATensor<N> {
        val ndata = arrayOfNulls<Any>(size)
        for (i in 0 until size) {
            ndata[i] = mapper.apply(data[i])
        }
        return ATensor(newCalculator, sh, ndata as Array<N>)
    }


    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T : Any> buildFromSequence(mc: MathCalculator<T>, shape: IntArray, sequence: Sequence<T>): ATensor<T> {
            val size = MathUtils.product(shape)
            val data = arrayOfNulls<Any>(size)
            var pos = 0
            for (t in sequence) {
                data[pos++] = t
            }
            require(pos == size)
            return ATensor(mc, shape, data as Array<T>)
        }

        @Suppress("UNCHECKED_CAST")
        private inline fun <T : Any> apply2(x: ATensor<T>, y: ATensor<T>, f: (T, T) -> T): ATensor<T> {
            checkShape(x, y)
            val d1 = x.data
            val d2 = y.data
            val ndata = arrayOfNulls<Any>(x.size)
            for (i in 0 until x.size) {
                ndata[i] = f(d1[i], d2[i])
            }
            return ATensor(x.mc, x.sh, ndata as Array<T>)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> copyOf(tensor: AbstractTensor<T>): ATensor<T> {
            val shape = tensor.shape
            if (tensor is ATensor) {
                return tensor.copy()
            }
            val size = MathUtils.product(shape)
            val data = arrayOfNulls<Any>(size)
            var pos = 0
            for (t in tensor.elementSequence()) {
                data[pos++] = t
            }
            return ATensor(tensor.mathCalculator, shape, data as Array<T>)
        }

        fun <T : Any> constant(c: T, shape: IntArray, mc: MathCalculator<T>): ATensor<T> {
            val size = MathUtils.product(shape)
            val data = arrayOfNulls<Any>(size)
            Arrays.fill(data, c)
            @Suppress("UNCHECKED_CAST")
            return ATensor(mc, shape, data as Array<T>)
        }

        fun <T : Any> fromMatrix(m: Matrix<T>): ATensor<T> {
            var pos = 0
            val r = m.rowCount
            val c = m.columnCount
            val data = arrayOfNulls<Any>(r * c)
            for (i in 0 until r) {
                for (j in 0 until c) {
                    data[pos++] = m[i, j]
                }
            }
            @Suppress("UNCHECKED_CAST")
            return ATensor(m.mathCalculator, intArrayOf(r, c), data as Array<T>)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> wedeg(x: ATensor<T>, y: ATensor<T>): ATensor<T> {
            val mc = x.mc
            val shape = x.shape + y.shape
            val size = x.size * y.size
            val data = arrayOfNulls<Any>(size)
            val dataX = x.data
            val dataY = y.data
            var pos = 0
            for (a in dataX) {
                for (b in dataY) {
                    data[pos++] = mc.multiply(a, b)
                }
            }
            return ATensor(mc, shape, data as Array<T>)
        }

    }

}

open class SlicedView<T : Any>(
        tensor: Tensor<T>,
        /**
         * The ranges in t. `ranges.size = t.dim`.
         */
        protected val ranges: List<IntProgression>,
        /**
         * maps the axis to t's axis. `axisMap.size = this.dim`
         */
        protected val axisMap: IntArray,

        shape: IntArray,
) : AbstractTensor<T>(tensor.mathCalculator, shape) {

    protected open val t = tensor

//    init {
//        //check shape
//
//    }

    /**
     * The shifts in the index of t. `shifts.size = t.dim`.
     */
    protected val shifts: IntArray = IntArray(ranges.size) { i -> ranges[i].first }

    protected val steps: IntArray = IntArray(ranges.size) { i -> ranges[i].step }

    /*
    Index Convention:
    idx: in the view
    pos: in the backing tensor
     */

    protected val originalIndices: Sequence<Index> = IterUtils.prodIdx(ranges, copy = false)


    protected fun mapIdxTo(idx: Index, pos: Index) {
        // pos[axisMap[i]] + shifts[i]
        shifts.copyInto(pos)
        for (l in 0 until dim) {
            val axis = axisMap[l]
            if (axis < 0) {
                // new axis
                continue
            }
            pos[axis] += idx[l] * steps[axis]
        }
    }

    protected fun mapIdx(idx: Index): Index {
        val pos = IntArray(t.dim)
        mapIdxTo(idx, pos)
        return pos
    }

    override fun getChecked(idx: Index): T {
        val pos = mapIdx(idx)
        return t[pos]
    }


    override fun elementSequence(): Sequence<T> {
        return originalIndices.map { pos -> t[pos] }
    }


    override fun wedge(y: Tensor<T>): Tensor<T> {
        TODO("Not yet implemented")
    }

    protected fun composeSliceTo(am: IntArray, ranges: MutableList<IntProgression>)
            : Pair<IntArray, MutableList<IntProgression>> {
        for (i in am.indices) {
            if (am[i] >= 0) {
                am[i] = axisMap[am[i]]
            }
        }
        val newRanges = this.ranges.toMutableList()
        for (i in ranges.indices) {
            val r0 = this.ranges[axisMap[i]]
            val r1 = ranges[i]
            val first = r0.first + r1.first * r0.step
            val step = r0.step * r1.step
            val last = r0.first + r1.last * r0.step
            newRanges[axisMap[i]] = IntProgression.fromClosedRange(first, last, step)
        }
        return am to newRanges
    }

    override fun slice(slices: List<Any?>): Tensor<T> {
        val (am, ranges, sh) = computeSliceView(slices)
        val (newAxisMap, newRanges) = composeSliceTo(am, ranges)
        return SlicedView(this.t, newRanges, newAxisMap, sh)
    }

    override fun permute(p: Permutation): Tensor<T> {
        val am = p.array
        for (i in am.indices) {
            am[i] = axisMap[am[i]]
        }
        return SlicedView(t, ranges, am, p.apply(shape))
    }

    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): Tensor<N> {
        TODO("Not yet implemented")
    }

}

class MutableSliceView<T : Any>(
        tensor: MutableTensor<T>, ranges: List<IntProgression>,
        /**
         * maps the axis to t's axis. `axisMap.size = this.dim`
         */
        axisMap: IntArray,

        shape: IntArray,
) : SlicedView<T>(tensor, ranges, axisMap, shape), MutableTensor<T> {
    override val t: MutableTensor<T> = tensor

    override fun set(idx: Index, v: T) {
        checkIdx(idx)
        val pos = mapIdx(idx)
        t[pos] = v
    }

    override fun applyAll(f: (T) -> T): MutableTensor<T> {
        return mapTo(mc, f)
    }


    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): MutableTensor<N> {
        return ATensor.buildFromSequence(newCalculator, sh, elementSequence().map { mapper.apply(it) })
    }

    override fun copy(): MutableTensor<T> {
        return ATensor.copyOf(this)
    }


    override fun slice(slices: List<Any?>): MutableTensor<T> {
        val (am, ranges, ns) = computeSliceView(slices)
        return MutableSliceView(this, ranges, am, ns)
    }

    override fun permute(p: Permutation): MutableTensor<T> {
        val ranges = this.ranges.toMutableList()
        val am = p.array
        for (i in am.indices) {
            am[i] = axisMap[am[i]]
        }
        val (newAxisMap, newRanges) = composeSliceTo(am, ranges)
        return MutableSliceView(this.t, newRanges, newAxisMap, sh)
    }


    override fun plusAssign(y: Tensor<T>) {
        for (idx in indices) {
            this[idx] = mc.add(this[idx], y[idx])
        }
    }

    override fun minusAssign(y: Tensor<T>) {
        for (idx in indices) {
            this[idx] = mc.subtract(this[idx], y[idx])
        }
    }

    override fun timesAssign(y: Tensor<T>) {
        for (idx in indices) {
            this[idx] = mc.multiply(this[idx], y[idx])
        }
    }

    override fun divAssign(y: Tensor<T>) {
        for (idx in indices) {
            this[idx] = mc.divide(this[idx], y[idx])
        }
    }

    /*
    Inherited methods:
     */

    override fun add(y: Tensor<T>): MutableTensor<T> {
        return super<MutableTensor>.add(y)
    }

    override fun negate(): MutableTensor<T> {
        return super<MutableTensor>.negate()
    }

    override fun subtract(y: Tensor<T>): MutableTensor<T> {
        return super<MutableTensor>.subtract(y)
    }

    override fun multiply(k: T): MutableTensor<T> {
        return super<MutableTensor>.multiply(k)
    }

    override fun multiply(y: Tensor<T>): MutableTensor<T> {
        return super<MutableTensor>.multiply(y)
    }

    override fun divide(y: Tensor<T>): MutableTensor<T> {
        return super<MutableTensor>.divide(y)
    }

    override fun permute(vararg newAxis: Int): MutableTensor<T> {
        return super<MutableTensor>.permute(*newAxis)
    }

    override fun transpose(axis1: Int, axis2: Int): MutableTensor<T> {
        return super<MutableTensor>.transpose(axis1, axis2)
    }

    override fun wedge(y: Tensor<T>): MutableTensor<T> {
        return super<MutableTensor>.wedge(y)
    }


}

internal object TensorUtils {

    fun <T : Any> checkShape(x: Tensor<T>, y: Tensor<T>) {
        if (!x.isSameShape(y)) {
            throw IllegalArgumentException("Shape mismatch: ${x.shape.contentToString()} with ${y.shape.contentToString()}")
        }
    }

    fun <T : Any> add(x: Tensor<T>, y: Tensor<T>): MutableTensor<T> {
        checkShape(x, y)
        val mc = x.mathCalculator
        return ATensor.buildFromSequence(mc, x.shape, x.indices.map { idx -> mc.add(x[idx], y[idx]) })
    }

    /**
     * Returns the negate of this tensor.
     *
     */
    fun <T : Any> negate(x: Tensor<T>): MutableTensor<T> {
        val mc = x.mathCalculator
        return ATensor.buildFromSequence(mc, x.shape, x.indices.map { idx -> mc.negate(x[idx]) })
    }

    /**
     * Returns the sum of this tensor and `y`.
     *
     * The sum of two tensor `x,y` has the
     * shape of `max(x.shape, y.shape)`, here `max` means element-wise maximum of two arrays.
     */
    fun <T : Any> subtract(x: Tensor<T>, y: Tensor<T>): MutableTensor<T> {
        checkShape(x, y)
        val mc = x.mathCalculator
        return ATensor.buildFromSequence(mc, x.shape, x.indices.map { idx -> mc.subtract(x[idx], y[idx]) })
    }

    /**
     * Returns the result of multiplying this tensor with a scalar.
     */
    fun <T : Any> multiply(x: Tensor<T>, k: T): MutableTensor<T> {
        val mc = x.mathCalculator
        return ATensor.buildFromSequence(mc, x.shape, x.indices.map { idx -> mc.multiply(k, x[idx]) })
    }

    /**
     * Returns the **element-wise** product of this tensor and `y`.
     *
     */
    fun <T : Any> multiply(x: Tensor<T>, y: Tensor<T>): MutableTensor<T> {
        checkShape(x, y)
        val mc = x.mathCalculator
        return ATensor.buildFromSequence(mc, x.shape, x.indices.map { idx -> mc.multiply(x[idx], y[idx]) })
    }

    /**
     * Returns the **element-wise** division of this tensor and `y`.
     *
     * @throws ArithmeticException if zero-division happens
     */
    fun <T : Any> divide(x: Tensor<T>, y: Tensor<T>): MutableTensor<T> {
        checkShape(x, y)
        val mc = x.mathCalculator
        return ATensor.buildFromSequence(mc, x.shape, x.indices.map { idx -> mc.divide(x[idx], y[idx]) })
    }

    fun <T : Any> wedge(x: Tensor<T>, y: Tensor<T>): MutableTensor<T> {
        if (x is ATensor && y is ATensor) {
            return ATensor.wedeg(x, y)
        }
        val shape = x.shape + y.shape
        val mc = x.mathCalculator
        val result = ATensor.constant(mc.zero, shape, mc)
        val data = result.data
        var pos = 0
        val seqX = x.elementSequence()
        val seqY = y.elementSequence()
        for (a in seqX) {
            for (b in seqY) {
                data[pos++] = mc.multiply(a, b)
            }
        }
        return result
    }

    fun <T : Any> isLinearDependent(x: Tensor<T>, y: Tensor<T>): Boolean {
        checkShape(x, y)
//        val idx = IntArray(x.dim)
        if (x.isZero() || y.isZero()) {
            return true
        }
        val mc = x.mathCalculator
//        val a = x[idx]
//        val b = y[idx]
        var k: T? = null
        for ((a, b) in x.elementSequence().zip(y.elementSequence())) {
            if (k == null) {
                if (mc.isZero(a)) {
                    if (mc.isZero(b)) {
                        continue
                    }
                    return false
                }
                if (mc.isZero(b)) {
                    return false
                }
                k = mc.divide(a, b)
            } else {
                if (!mc.isEqual(a, mc.multiply(k, b))) {
                    return false
                }
            }
        }
        return true
    }
}

fun main() {
    val mc = Calculators.integer()
    val shape = intArrayOf(2, 3)
    val shape2 = intArrayOf(3, 2)
//    val v = Tensor.zeros(mc, *shape)
//    val w = Tensor.ones(mc, *shape)
    val u = Tensor.of(shape, mc) { it.sum() }
    val w = Tensor.of(shape2, mc) { it[0] }
//    println(u)
//    println(w)
//
//    println()
//    println(u.sum())
//    println("u[0,0,0]=")
//    val w0 = u.slice(0, 0, 2 downTo 1, null, Tensor.NEW_AXIS)
//    println()
////    val w = u.permute(1, 0)
////    println(w)
//    val w2 = u.slice(0, 0)
//    println(w2)
//    println(u.permute(intArrayOf(1,0))[0])
//    u += w
//    println(u)
}