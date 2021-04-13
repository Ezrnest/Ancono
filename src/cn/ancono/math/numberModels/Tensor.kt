package cn.ancono.math.numberModels

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObject
import cn.ancono.math.MathObjectExtend
import cn.ancono.math.MathUtils
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.discrete.combination.Permutation
import cn.ancono.math.discrete.combination.Permutations
import cn.ancono.math.numberModels.Tensor.Companion.checkShape
import cn.ancono.math.numberModels.api.AlgebraModel
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import cn.ancono.utilities.IterUtils
import java.util.*
import java.util.function.Function

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
     * Returns the length of this tensor at the given axis.
     */
    fun lengthAt(axis: Int): Int {
        require(axis in 0 until dim)
        return shape[axis]
    }

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


    /**
     * Returns the wedge product of this tensor and [y].
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
     * Returns the inner product of this tensor and [y], which is the sum of the element-wise product of this and `y`.
     */
    fun inner(y: Tensor<T>): T {
        return TensorUtils.inner(this, y)
    }

    /**
     * Returns the matrix multiplication of this and [y].
     *
     * To perform matrix multiplication of rank `r` for two tensors `x,y`, first it is
     * required that the last `r` dimensions of `x` and first `r` dimensions of `y` have
     * the same shape.
     * The resulting tensor `z` has the shape of `x.shape[:-r] + y.shape[r:]`.
     * Denote `i, j, k` indices of length `x.dim-r, y.dim-r, r` respectively, the following
     * equation is satisfied:
     *
     *     z[i,j] = sum(k, x[i,k] * y[k,j])
     *
     *
     *
     */
    fun matmul(y: Tensor<T>, r: Int = 1): Tensor<T> {
        return TensorUtils.matmul(this, y, r)
    }


    /**
     * Returns the sum of all the elements in this tensor.
     */
    fun sumAll(): T {
        return elementSequence().reduce(mathCalculator::add)
    }

    /**
     * Returns the sum of elements in the given axis(axes). If the given axes is empty, then it will
     * return the sum of all the elements as a scalar tensor.
     */
    fun sum(vararg axes: Int): Tensor<T> {
        return TensorUtils.sum(this, axes.asList())
    }

//    fun trace() : Tensor<T>

//    fun diagonal() : Tensor<T>


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
     * Returns a new tensor of applying the given function to this tensor element-wise.
     */
    fun applyAll(f: (T) -> T): Tensor<T> {
        return mapTo(mathCalculator, f)
    }


    /**
     * Returns a view of this tensor according to the given slicing parameters.
     * It is required that an element is either
     *  * an integer, `Int`: to get the corresponding elements in that axis;
     *  * a range, `IntProgression`: to get a slice of elements in that axis;
     *  * `null`: to keep the axis as it is;
     *  * a special object, [Tensor.NEW_AXIS]: to indicate a new axis should be inserted;
     *  * a special object, [Tensor.DOTS]: (it should appear at most once)
     *    to indicate zero or more omitted axes that will be kept the same.
     *    The axes are computed according to other slicing parameters.
     */
    fun slice(vararg slices: Any?): Tensor<T> {
        return slice(slices.asList())
    }

    /**
     * Returns a view of this tensor according to the slicing ranges or indices.
     *
     * @see [slice]
     */
    fun slice(slices: List<Any?>): Tensor<T> {
        val (am, ranges, ns) = TensorUtils.computeSliceView(this, slices)
        return SlicedView(this, ranges, am, ns)
    }

    /**
     * Inserts a new axis of length 1 at the last of this tensor. The result is
     * a view.
     *
     */
    fun newAxisAt(axis: Int = -1): Tensor<T> {
        val (am, ranges, ns) = TensorUtils.newAxisSliceView(this, axis)
        return SlicedView(this, ranges, am, ns)
    }


    /**
     * Reshapes this tensor to be a view of the given shape.
     *
     * At most one `-1` can appear in the given new shape
     * indicating the length of this dimension should be computed accordingly.
     *
     * The resulting tensor will have the same element sequence as this tensor.
     *
     * @param newShape a non-empty array containing positive integers
     * except at most one element to be `-1`. Its product should be a divisor of the size of this tensor.
     */
    fun reshape(vararg newShape: Int): Tensor<T> {
        val sh = newShape.clone()
        TensorUtils.prepareNewShape(this, sh)
        return ReshapedView(this, sh)
    }

    /**
     * Reshapes this tensor to be 1-d tensor. This method is equal to `this.reshape(-1)`.
     *
     * @see reshape
     */
    fun ravel(): Tensor<T> {
        return reshape(-1)
    }


    /**
     * Broadcasts this tensor to the given shape.
     */
    fun broadcastTo(vararg newShape: Int): Tensor<T> {
        return TensorUtils.broadcastTo(this, newShape)
    }


//    /**
//     * The operator-overloading version of the method [slice].
//     *
//     * @see [slice]
//     */
//    operator fun get(vararg ranges: Any?): Tensor<T>{
//    return slice(ranges.asList())
//    }



    /**
     * Returns a axis-permuted view of this tensor.
     * The `i`-th axis in the resulting tensor corresponds to the `p.apply(i)`-th axis in this tensor.
     */
    fun permute(p: Permutation): Tensor<T> {
        require(p.size() == dim)
        val sh = this.shape
        val ranges = shape.map { 0 until it }
        return SlicedView(this, ranges, p.array, p.apply(sh))
    }

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
     *
     */
    fun transpose(axis1: Int = -1, axis2: Int = -2): Tensor<T> {
        return permute(Permutations.swap(dim,
                TensorUtils.addIfNegative(axis1, dim),
                TensorUtils.addIfNegative(axis2, dim)))
    }


    /**
     * Returns `true` if all elements in this tensor match the given [predicate].
     *
     */
    fun all(predicate: (T) -> Boolean): Boolean {
        return elementSequence().all(predicate)
    }

    /**
     * Returns true if at least one element in this tensor matches the given predicate.
     *
     */
    fun any(predicate: (T) -> Boolean): Boolean {
        return elementSequence().any(predicate)
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

    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): Tensor<N> {
        return ATensor.buildFromSequence(newCalculator, shape, elementSequence().map { mapper.apply(it) })
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is Tensor) {
            return false
        }
        val mc = mathCalculator
        if (!this.isSameShape(obj)) {
            return false
        }
        return elementSequence().zip(obj.elementSequence()).all { (a, b) -> mc.isEqual(a, b) }
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
        const val NEW_AXIS = "NEW_AXIS"

        /**
         * Used in [slice] to indicate omitted axes.
         */
        const val DOTS = "..."

        private fun checkValidShape(shape: IntArray) {
            require(shape.isNotEmpty())
            require(shape.all { s -> s > 0 })
        }

        fun <T : Any> checkShape(x: Tensor<T>, y: Tensor<T>) {
            if (!x.isSameShape(y)) {
                throw IllegalArgumentException("Shape mismatch: ${x.shape.contentToString()} and ${y.shape.contentToString()}.")
            }
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
         * Creates a tensor from the given multi-dimensional list/array. [elements] (and its
         * nesting lists) can contain either elements
         * of type `T` or lists, and the shape in each dimension should be consistent.
         *
         *
         */
        fun <T : Any> of(elements: List<Any>, mc: MathCalculator<T>): MutableTensor<T> {
            return ATensor.fromNestingList(elements, mc)
        }

        /**
         * Creates a tensor of the given [shape] with its [elements], it is required that the length of
         * [elements] is equal to the product of [shape].
         */
        fun <T : Any> of(shape: IntArray, mc: MathCalculator<T>, vararg elements: T): MutableTensor<T> {
            checkValidShape(shape)
            val size = MathUtils.product(shape)
            require(elements.size == size) {
                "$size elements expected but ${elements.size} is given!"
            }
            val data = Arrays.copyOf(elements, size, Array<Any>::class.java)
            @Suppress("UNCHECKED_CAST")
            return ATensor(mc, shape, data as Array<T>)
        }

        /**
         * Creates a 2-dimensional tensor from a matrix. The `(i,j)`-th element in the returned tensor
         * is equal to `(i,j)`-th element in `m`.
         */
        fun <T : Any> fromMatrix(m: Matrix<T>): MutableTensor<T> {
            return ATensor.fromMatrix(m)
        }

        /**
         * Returns a copy of the given tensor as a mutable tensor.
         */
        fun <T : Any> copyOf(t: Tensor<T>): MutableTensor<T> {
            return ATensor.copyOf(t)
        }

        /**
         * Returns the einsum of several tensors defined by the given expression.
         *
         *
         * Examples:
         *
         *      | Expression | Required Shapes | Result Shape | Description                 | Equivalent Method
         *      |------------|-----------------|--------------|-----------------------------|-------------------------
         *      | i->i       | (a)             | (a)          | identity                    | x
         *      | i->        | (a)             | (1)          | sum                         | x.sum()
         *      | ij->ji     | (a,b)           | (b,a)        | transpose                   | x.transpose()
         *      | ii         | (a,a)           | (a)          | trace                       | x.trace()
         *      | ii->i      | (a,a)           | (1)          | diagonal                    | x.trace().sum()
         *      | ij,ij->ij  | (a,b),(a,b)     | (a,b)        | element-wise multiplication | x.multiply(y)
         *      | ij,jk->ik  | (a,b),(b,c)     | (a,c)        | matrix multiplication       | x.matmul(y)
         */
        fun <T : Any> einsum(expr: String, vararg tensors: Tensor<T>): MutableTensor<T> {
            return TensorUtils.einsum(tensors.asList(), expr)
        }


        /**
         * Returns a tensor of shape `(1)` that represents the given scalar.
         */
        fun <T : Any> scalar(x: T, mc: MathCalculator<T>): MutableTensor<T> {
            return constants(x, mc, 1)
        }

        /**
         * Concatenate several tensors as a view.
         * The tensors must have the same dimensions and their shapes must
         * be equal in all axes except the concatenating axis.
         *
         * For example, concatenating two tensors of shape `(a,b), (a,c)` at axis 1 will result in a
         * tensor of shape `(a,b+c)`.
         */
        fun <T : Any> concat(ts: List<Tensor<T>>, axis: Int = 0): Tensor<T> {
            val (ax, shape) = TensorUtils.prepareConcat(ts, axis)
            return ConcatView(ax, ts, shape)
        }

        /**
         * Concatenate several tensors as a view.
         *
         * @see concat
         */
        fun <T : Any> concat(vararg ts: Tensor<T>, axis: Int = 0): Tensor<T> {
            return concat(ts.asList(), axis)
        }

        /**
         * Concatenate several mutable tensors as a mutable view.
         * The tensors must have the same dimensions and their shapes must
         * be equal in all axes except the concatenating axis.
         *
         * Any changes to the resulting view will be reflected to the original tensors.
         *
         * For example, concatenating two tensors of shape `(a,b), (a,c)` at axis 1 will result in a
         * tensor of shape `(a,b+c)`.
         */
        fun <T : Any> concatM(ts: List<MutableTensor<T>>, axis: Int = 0): MutableTensor<T> {
            val (ax, shape) = TensorUtils.prepareConcat(ts, axis)
            return MutableConcatView(ax, ts, shape)
        }

        /**
         * Concatenate several mutable tensors as a mutable view.
         * Any changes to the resulting view will be reflected to the original tensors.
         *
         * @see concatM
         */
        fun <T : Any> concatM(vararg ts: MutableTensor<T>, axis: Int = 0): MutableTensor<T> {
            return concatM(ts.asList(), axis)
        }

        /**
         * Stacks several tensors on a new axis as a view. It is required that all the given tensors are
         * of the same shape.
         *
         * For example, stacking two tensors of shape `(a,b)` at axis 0 will result in a tensor
         * of shape `(2,a,b)`.
         */
        fun <T : Any> stack(ts: List<Tensor<T>>, axis: Int = 0): Tensor<T> {
            val (ax, shape) = TensorUtils.prepareStack(ts, axis)
            return StackView(axis, ts, shape)
        }

        /**
         * Stacks several tensors on a new axis as a view. It is required that all the given tensors are
         * of the same shape.
         *
         * @see stack
         */
        fun <T : Any> stack(vararg ts: Tensor<T>, axis: Int = 0): Tensor<T> {
            return stack(ts.asList(), axis)
        }

        /**
         * Stacks several mutable tensors on a new axis as mutable view.
         * It is required that all the given tensors are of the same shape.
         *
         * For example, stacking two tensors of shape `(a,b)` at axis 0 will result in a tensor
         * of shape `(2,a,b)`.
         */
        fun <T : Any> stackM(ts: List<MutableTensor<T>>, axis: Int = 0): MutableTensor<T> {
            val (ax, shape) = TensorUtils.prepareStack(ts, axis)
            return MutableStackView(axis, ts, shape)
        }

        /**
         * Stacks several mutable tensors on a new axis as mutable view.
         * It is required that all the given tensors are
         * of the same shape.
         *
         * @see stackM
         */
        fun <T : Any> stackM(vararg ts: MutableTensor<T>, axis: Int = 0): MutableTensor<T> {
            return stackM(ts.asList(), axis)
        }

    }

}

/**
 * A vararg version of get. This method supports negative indices.
 */
operator fun <T : Any> Tensor<T>.get(vararg idx: Int): T {
    for (i in idx.indices) {
        if (idx[i] < 0) {
            idx[i] += lengthAt(i)
        }
    }
    return this[idx]
}

interface MutableTensor<T : Any> : Tensor<T> {
    /**
     * Sets an element in this tensor.
     */
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
        val t1 = t.broadcastTo(*shape)
        for (idx in indices) {
            set(idx, t1[idx])
        }
    }


    operator fun plusAssign(y: Tensor<T>) {
        val mc = mathCalculator
        val y1 = y.broadcastTo(*shape)
        for (idx in indices) {
            this[idx] = mc.add(this[idx], y1[idx])
        }
    }

    operator fun minusAssign(y: Tensor<T>) {
        val mc = mathCalculator
        val y1 = y.broadcastTo(*shape)
        for (idx in indices) {
            this[idx] = mc.subtract(this[idx], y1[idx])
        }
    }

    operator fun timesAssign(y: Tensor<T>) {
        val mc = mathCalculator
        val y1 = y.broadcastTo(*shape)
        for (idx in indices) {
            this[idx] = mc.multiply(this[idx], y1[idx])
        }
    }

    operator fun divAssign(y: Tensor<T>) {
        val mc = mathCalculator
        val y1 = y.broadcastTo(*shape)
        for (idx in indices) {
            this[idx] = mc.divide(this[idx], y1[idx])
        }
    }


    override fun applyAll(f: (T) -> T): MutableTensor<T> {
        return mapTo(mathCalculator, f)
    }

    /**
     * Performs the element-wise transformation to this mutable tensor in-place.
     *
     * @see applyAll
     */
    fun transform(f: (T) -> T) {
        indices.forEach { idx -> this[idx] = f(this[idx]) }
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


    override fun matmul(y: Tensor<T>, r: Int): MutableTensor<T> {
        return TensorUtils.matmul(this, y, r)
    }

    override fun sum(vararg axes: Int): MutableTensor<T> {
        return TensorUtils.sum(this, axes.asList())
    }


    override fun slice(slices: List<Any?>): MutableTensor<T> {
        val (am, ranges, ns) = TensorUtils.computeSliceView(this, slices)
        return MutableSliceView(this, ranges, am, ns)
    }

    override fun slice(vararg slices: Any?): MutableTensor<T> {
        return slice(slices.asList())
    }

    override fun newAxisAt(axis: Int): MutableTensor<T> {
        val (am, ranges, ns) = TensorUtils.newAxisSliceView(this, axis)
        return MutableSliceView(this, ranges, am, ns)
    }

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


    override fun permute(p: Permutation): MutableTensor<T> {
        require(p.size() == dim)
        val sh = this.shape
        val ranges = shape.map { 0 until it }
        return MutableSliceView(this, ranges, p.array, p.apply(sh))
    }

    override fun permute(vararg newAxis: Int): MutableTensor<T> {
        return permute(Permutations.valueOf(*newAxis))
    }

    override fun transpose(axis1: Int, axis2: Int): MutableTensor<T> {
        return permute(Permutations.swap(dim, axis1, axis2))
    }


    override fun reshape(vararg newShape: Int): MutableTensor<T> {
        val sh = newShape.clone()
        TensorUtils.prepareNewShape(this, sh)
        return MutableReshapedView(this, sh)
    }

    override fun ravel(): MutableTensor<T> {
        return reshape(-1)
    }

    fun copy(): MutableTensor<T> {
        return ATensor.copyOf(this)
    }

    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): MutableTensor<N> {
        return ATensor.buildFromSequence(newCalculator, shape, elementSequence().map { mapper.apply(it) })
    }


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

    override fun lengthAt(axis: Int): Int {
        require(axis in 0 until dim)
        return sh[axis]
    }

    override fun isSameShape(y: Tensor<*>): Boolean {
        if (y is AbstractTensor) {
            return sh.contentEquals(y.sh)
        }
        return sh.contentEquals(y.shape)
    }


    final override val dim: Int
        get() = sh.size


    override val size: Int
        get() = MathUtils.product(shape)


    override val indices: Sequence<Index> = IterUtils.prodIdxN(sh)

    /**
     * Checks whether `idx` is a valid index for this tensor, throws exception if necessary.
     */
    protected fun checkIdx(idx: Index) {
        require(idx.size == dim) {
            "Dim mismatch: required $dim, given ${idx.size}"
        }
        for (i in 0 until dim) {
            if (!(0 <= idx[i] && idx[i] < sh[i])) {
                throw IndexOutOfBoundsException("Tensor index out of bound at axis $i: " +
                        "Shape=${sh.contentToString()}, Index=${idx.contentToString()}")
            }
        }
    }

    /**
     * Gets the element in this tensor. The index is already checked valid.
     * The index should not be modified.
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


//    override fun permute(p: Permutation): Tensor<T> {
//        require(p.size() == dim)
//        val sh = this.shape
//        val ranges = shape.map { 0 until it }
//        return SlicedView(this, ranges, p.array, p.apply(sh))
//    }


    /*
    General methods for MathObject
     */

//    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): Tensor<N> {
//        return ATensor.buildFromSequence(newCalculator, sh, elementSequence().map { mapper.apply(it) })
//    }
}

abstract class AbstractMutableTensor<T : Any>(mc: MathCalculator<T>, shape: IntArray)
    : AbstractTensor<T>(mc, shape), MutableTensor<T> {
    override fun applyAll(f: (T) -> T): MutableTensor<T> {
        return mapTo(mc, f)
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
class ATensor<T : Any>(mc: MathCalculator<T>, shape: IntArray, val data: Array<T>)
    : AbstractMutableTensor<T>(mc, shape) {
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

    private inline fun inlineApplyAll(f: (T) -> T): ATensor<T> {
        for (i in 0 until size) {
            data[i] = f(data[i])
        }
        return this
    }


    override fun applyAll(f: (T) -> T): MutableTensor<T> {
        val ndata = arrayOfNulls<Any>(size)
        for (i in 0 until size) {
            ndata[i] = f(data[i])
        }
        @Suppress("UNCHECKED_CAST")
        return ATensor(mc, sh, ndata as Array<T>)
    }

    override fun transform(f: (T) -> T) {
        for (i in 0 until size) {
            data[i] = f(data[i])
        }
    }

    override fun isZero(): Boolean {
        return data.all { mc.isZero(it) }
    }


    override fun add(y: Tensor<T>): MutableTensor<T> {
        if (y is ATensor && isSameShape(y)) {
            return apply2(this, y, mc::add)
        }
        return super.add(y)

    }

    override fun negate(): MutableTensor<T> {
        return inlineApplyAll(mc::negate)
    }

    override fun subtract(y: Tensor<T>): MutableTensor<T> {
        if (y is ATensor && isSameShape(y)) {
            return apply2(this, y, mc::subtract)
        }
        return super.add(y)
    }

    override fun multiply(k: T): MutableTensor<T> {
        return applyAll { t -> mc.multiply(k, t) }.copy()
    }

    override fun multiply(y: Tensor<T>): MutableTensor<T> {
        if (y is ATensor && isSameShape(y)) {
            return apply2(this, y, mc::multiply)
        }
        return super.multiply(y)
    }

    override fun divide(y: Tensor<T>): MutableTensor<T> {
        if (y is ATensor && isSameShape(y)) {
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
        val y1 = y.broadcastTo(*sh)
        return apply2InPlace(y1, mc::add)
    }

    override fun minusAssign(y: Tensor<T>) {
        val y1 = y.broadcastTo(*sh)
        return apply2InPlace(y1, mc::subtract)
    }

    override fun timesAssign(y: Tensor<T>) {
        val y1 = y.broadcastTo(*sh)
        return apply2InPlace(y1, mc::multiply)
    }

    override fun divAssign(y: Tensor<T>) {
        val y1 = y.broadcastTo(*sh)
        return apply2InPlace(y1, mc::divide)
    }


    override fun reshape(vararg newShape: Int): MutableTensor<T> {
        val sh = newShape.clone()
        TensorUtils.prepareNewShape(this, sh)
        return ATensor(mc, sh, data)
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
        fun <T : Any> copyOf(tensor: Tensor<T>): ATensor<T> {
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

        fun <T : Any> zeros(shape: IntArray, mc: MathCalculator<T>): ATensor<T> {
            return constant(mc.zero, shape, mc)
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


        private fun <T : Any> recurAdd(list: List<Any>, shape: IntArray, level: Int, pos: Int,
                                       dest: Array<T>, clz: Class<T>): Int {
            val size = shape[level]
            require(list.size == size) {
                "Required length at axis $level is $size, but ${list.size} is given!"
            }
            var p = pos
            if (level == shape.lastIndex) {
                for (e in list) {
                    dest[p++] = clz.cast(e)
                }
                return pos + size
            }
            for (e in list) {
                require(e is List<*>) {
                    "Nesting level mismatch!"
                }
                @Suppress("UNCHECKED_CAST")
                p = recurAdd(e as List<Any>, shape, level + 1, p, dest, clz)
            }
            return p
        }

        fun <T : Any> fromNestingList(list: List<Any>, mc: MathCalculator<T>): ATensor<T> {
            val sh = arrayListOf<Int>()
            val clz = mc.numberClass
            var l = list
            while (true) {
                require(l.isNotEmpty())
                sh += l.size
                val e = l[0]
                if (clz.isInstance(e)) {
                    break
                }
                require(e is List<*>) {
                    "Elements in the given list should be either list or object of required type. " +
                            "Given: $e"
                }
                @Suppress("UNCHECKED_CAST")
                l = e as List<Any>
            }
            val shape = sh.toIntArray()
            val size = MathUtils.product(shape)

            @Suppress("UNCHECKED_CAST")
            val data = arrayOfNulls<Any>(size) as Array<T>
            val pos = recurAdd(list, shape, 0, 0, data, clz)
            assert(pos == size)
            return ATensor(mc, shape, data)
        }
    }

}


fun main() {
    val mc = Calculators.integer()
    val shape = intArrayOf(2, 2, 3)
    val shape2 = intArrayOf(2, 3, 4)
//    val v = Tensor.zeros(mc, *shape)
//    val w = Tensor.ones(mc, *shape)
    val u = Tensor.of(shape, mc) { idx -> idx.withIndex().sumBy { (1 + it.index) * it.value } }
    val w = Tensor.of(shape2, mc) { it[0] + 1 }

    val r1 = u.matmul(w, r = 2)
    val r2 = Tensor.einsum("ijk,jkl->il", u, w)
    println(r1.valueEquals(r2))
//    println(u)
//    println(w)
//
//    println(u + w)
//    println()
//    val z = u.slice(0..1, null).reshape(4, -1)
//    println(z)
//    z[1, 1..2] = 18
//    println(u)
//    val v = u.slice(DOTS, 0..0)
//    println(u.slice(NEW_AXIS, DOTS).shape.contentToString())
//    println(v.shape.contentToString())
//    println(v)
//    val v1 = v.slice(0, null)
//    v1.setAll(1)
//    println(u)
//    println(w)
//    println(w.newAxisAt())

//    println(u.sumAll() + w.sumAll())
//    println(v.sumAll())
//    println(Tensor.einsum("ij->j", u))
//    println(u.sum())
//    println(Tensor.einsum("ijk->ij", u))
//    println(u.sum(-1))
//    println(Tensor.einsum("ijk->k", u))
//    println(u.sum(0, 1))
//    val r = TensorUtils.einsum(listOf(u, w),
//            intArrayOf(3, 3, 3, 3),
//            intArrayOf(1),
//            listOf(intArrayOf(0, 1), intArrayOf(2, 3)),
//            listOf(intArrayOf(), intArrayOf()),
////            listOf(intArrayOf(0,0), intArrayOf(0,1)),
//            mc)
//    println(r)
//    println(r.valueEquals(u.wedge(w)))

//    println(Tensor.einsum("ii", u)) // trace
//    println(Tensor.einsum("ij->", u)) // sum
//    println(Tensor.einsum("ii->i", u)) // diagonal
//    println(Tensor.einsum("ij->ji", u)) // transpose
//
//    println(Tensor.einsum("ij,ij->ij", u, w)) // element-wise multiplication
//
//    println(Tensor.einsum("ij,jk->ik", u, w)) // matrix multiplication

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