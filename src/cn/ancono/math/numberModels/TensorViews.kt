package cn.ancono.math.numberModels

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathUtils
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.discrete.combination.Permutation
import cn.ancono.utilities.ArraySup
import cn.ancono.utilities.IterUtils
import java.util.*
import kotlin.math.min


/*
 * Created by liyicheng at 2021-04-11 10:04
 */


open class SlicedView<T : Any>(
        tensor: Tensor<T>,
        /**
         * The ranges in t. `ranges.size = t.dim`.
         */
        protected val ranges: List<IntProgression>,
        /**
         * Maps the axis to [tensor]'s axis. `axisMap.size = this.dim`.
         *
         * `axisMap[i] = -1` means a new axis.
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

    /**
     * All the indices related to this sliced view in the original tensor.
     *
     * The sequence is not ordered.
     */
    protected val originalIndicesNoOrder: Sequence<Index> = IterUtils.prodIdx(ranges, copy = false)


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


//    override fun elementSequence(): Sequence<T> {
//        return originalIndices.map { pos -> t[pos] }
//    }


    protected fun composeSliceTo(am: IntArray, ranges: List<IntProgression>)
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
        val (am, ranges, sh) = TensorUtils.computeSliceView(this, slices)
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


//    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): MutableTensor<N> {
//        return ATensor.buildFromSequence(newCalculator, sh, elementSequence().map { mapper.apply(it) })
//    }

    override fun slice(slices: List<Any?>): MutableTensor<T> {
        val (am, ranges, sh) = TensorUtils.computeSliceView(this, slices)
        val (newAxisMap, newRanges) = composeSliceTo(am, ranges)
        return MutableSliceView(this.t, newRanges, newAxisMap, sh)
    }

    override fun newAxisAt(axis: Int): MutableTensor<T> {
        val (am, ranges, sh) = TensorUtils.newAxisSliceView(this, axis)
        val (newAxisMap, newRanges) = composeSliceTo(am, ranges)
        return MutableSliceView(this.t, newRanges, newAxisMap, sh)
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


    /*
    Inherited methods:
     */


    override fun permute(vararg newAxis: Int): MutableTensor<T> {
        return super<MutableTensor>.permute(*newAxis)
    }

    override fun transpose(axis1: Int, axis2: Int): MutableTensor<T> {
        return super<MutableTensor>.transpose(axis1, axis2)
    }


    override fun setAll(v: T) {
        originalIndicesNoOrder.forEach { idx -> t[idx] = v }
    }

    override fun sumAll(): T {
        return originalIndicesNoOrder.map { idx -> t[idx] }.reduce(mc::add) // no order here
    }

    override fun transform(f: (T) -> T) {
        originalIndicesNoOrder.forEach { idx -> t[idx] = f(t[idx]) }
    }

}

abstract class CombinedView<T : Any>(tensors: List<Tensor<T>>, shape: IntArray)
    : AbstractTensor<T>(tensors[0].mathCalculator, shape) {
    open val ts: List<Tensor<T>> = tensors


    override fun sumAll(): T {
        return ts.fold(mc.zero) { re, t -> mc.add(re, t.sumAll()) }
    }

    override fun isZero(): Boolean {
        return ts.all { it.isZero() }
    }

    override fun all(predicate: (T) -> Boolean): Boolean {
        return ts.all { it.all(predicate) }
    }

    override fun any(predicate: (T) -> Boolean): Boolean {
        return ts.any { it.any(predicate) }
    }

}

open class ConcatView<T : Any>(val axis: Int, tensors: List<Tensor<T>>, shape: IntArray)
    : CombinedView<T>(tensors, shape) {

    protected val axisLevels = IntArray(tensors.size + 1)

    override val ts: List<Tensor<T>> = tensors

    init {
        axisLevels[0] = 0
        for (i in tensors.indices) {
            axisLevels[i + 1] = axisLevels[i] + tensors[i].lengthAt(axis)
        }
    }

    override fun getChecked(idx: Index): T {
        val k = ArraySup.binarySearchFloor(axisLevels, 0, axisLevels.size, idx[axis])
        val nIdx = idx.copyOf()
        nIdx[axis] -= axisLevels[k]
        return ts[k][nIdx]
    }

}

class MutableConcatView<T : Any>(axis: Int, tensors: List<MutableTensor<T>>, shape: IntArray)
    : ConcatView<T>(axis, tensors, shape), MutableTensor<T> {
    override val ts: List<MutableTensor<T>> = tensors
    override fun set(idx: Index, v: T) {
        checkIdx(idx)
        val k = ArraySup.binarySearchFloor(axisLevels, 0, axisLevels.size, idx[axis])
        val nIdx = idx.copyOf()
        nIdx[axis] -= axisLevels[k]
        ts[k][nIdx] = v
    }

    override fun setAll(v: T) {
        for (t in ts) {
            t.setAll(v)
        }
    }
}

open class StackView<T : Any>(val axis: Int, tensors: List<Tensor<T>>, shape: IntArray)
    : CombinedView<T>(tensors, shape) {

    override val ts: List<Tensor<T>> = tensors
    protected fun transIdx(idx: Index): Index {
        val nIdx = IntArray(dim - 1)
        idx.copyInto(nIdx, endIndex = axis)
        idx.copyInto(nIdx, axis, axis + 1)
        return nIdx
    }

    override fun getChecked(idx: Index): T {
        val k = idx[axis]
        val nIdx = transIdx(idx)
        return ts[k][nIdx]
    }

    override fun sumAll(): T {
        return ts.fold(mc.zero) { re, t -> mc.add(re, t.sumAll()) }
    }

    override fun isZero(): Boolean {
        return ts.all { it.isZero() }
    }
}

class MutableStackView<T : Any>(axis: Int, tensors: List<MutableTensor<T>>, shape: IntArray)
    : StackView<T>(axis, tensors, shape), MutableTensor<T> {
    override val ts: List<MutableTensor<T>> = tensors
    override fun set(idx: Index, v: T) {
        checkIdx(idx)
        val nIdx = transIdx(idx)
        val k = idx[axis]
        ts[k][nIdx] = v
    }

    override fun setAll(v: T) {
        for (t in ts) {
            t.setAll(v)
        }
    }
}


open class ReshapedView<T : Any>(tensor: Tensor<T>, shape: IntArray)
    : AbstractTensor<T>(tensor.mathCalculator, shape) {

    open val t: Tensor<T> = tensor

    protected fun computeShift(shape: IntArray): IntArray {
        val dim = shape.size
        val shifts = IntArray(dim)
        var s = 1
        for (i in (dim - 1) downTo 0) {
            shifts[i] = s
            s *= shape[i]
        }
        return shifts
    }

    private val shifts: IntArray = computeShift(shape)
    private val shiftsT: IntArray = computeShift(tensor.shape)

    /**
     * Converts the index to absolute position in 1-d array.
     */
    protected fun toPos(idx: Index): Int {
        var pos = 0
        for (i in 0 until dim) {
            pos += idx[i] * shifts[i]
        }
        return pos
    }

    /**
     * Converts the absolute position in 1-d array to index in t.
     */
    protected fun toIdx(pos0: Int): Index {
        val idx = IntArray(dim)
        var pos = pos0
        for (i in 0 until dim) {
            val t = pos / shiftsT[i]
            pos -= t * shiftsT[i]
            idx[i] = t
        }
        return idx
    }

    override fun getChecked(idx: Index): T {
        val pos = toPos(idx)
        val tIdx = toIdx(pos)
        return t[tIdx]
    }

    override fun sumAll(): T {
        return t.sumAll()
    }

    override fun elementSequence(): Sequence<T> {
        return t.elementSequence()
    }

    override fun flattenToList(): List<T> {
        return t.flattenToList()
    }

    override fun reshape(vararg newShape: Int): Tensor<T> {
        return t.reshape(*newShape)
    }

    override fun ravel(): Tensor<T> {
        return if (dim == 1) {
            this
        } else {
            t.ravel()
        }
    }

    override fun all(predicate: (T) -> Boolean): Boolean {
        return t.all(predicate)
    }

    override fun any(predicate: (T) -> Boolean): Boolean {
        return t.any(predicate)
    }
}

class MutableReshapedView<T : Any>(tensor: MutableTensor<T>, shape: IntArray)
    : ReshapedView<T>(tensor, shape), MutableTensor<T> {
    override val t: MutableTensor<T> = tensor
    override fun set(idx: Index, v: T) {
        checkIdx(idx)
        val pos = toPos(idx)
        val tIdx = toIdx(pos)
        t[tIdx] = v
    }

    override fun setAll(v: T) {
        t.setAll(v)
    }

    override fun transform(f: (T) -> T) {
        t.transform(f)
    }

    override fun reshape(vararg newShape: Int): MutableTensor<T> {
        return t.reshape(*newShape)
    }

    override fun ravel(): MutableTensor<T> {
        return if (dim == 1) {
            this
        } else {
            t.ravel()
        }
    }
}

class BroadcastView<T : Any>(
        val t: Tensor<T>, shape: IntArray,
//                             val originAxes: IntArray,
        val d: Int,
        private val extendedAxes: IntArray,
//                             val newAxes: IntArray
) : AbstractTensor<T>(t.mathCalculator, shape) {
    /*
     * originAxes[i]
     */

    override fun getChecked(idx: Index): T {
        val tIdx = idx.copyOfRange(d, idx.size)
        for (ax in extendedAxes) {
            tIdx[ax] = 0
        }
        return t[tIdx]
    }

    //    override fun getChecked(idx: Index): T {
//        val tIdx = IntArray(t.dim)
//        for (i in originAxes.indices) {
//            tIdx[i] = idx[originAxes[i]]
//        }
//        for (ax in extendedAxes) {
//            tIdx[ax] = 0
//        }
//        return t[tIdx]
//    }
//
    override fun sumAll(): T {
        val re = t.sumAll()
        var k = 1L
        for (ax in 0 until d) {
            k *= sh[ax]
        }
        return mc.multiplyLong(re, k)
    }

}

open class IndexMapView<T : Any>(
        open val tensor: Tensor<T>,
        /**
         * Axis map.
         *
         *     tIdx[i] = idx[am[i]]
         */
        val am: IntArray,
        val offsets: IntArray,
        shape: IntArray) : AbstractTensor<T>(tensor.mathCalculator, shape) {

    protected fun mapIdx(idx: Index): IntArray {
        val tIdx = offsets.clone()
        for (i in tIdx.indices) {
            tIdx[i] += idx[am[i]]
        }
        return tIdx
    }

    override fun getChecked(idx: Index): T {
        val tIdx = mapIdx(idx)
        return tensor[tIdx]
    }
}

class MutableIndexMapView<T : Any>(override val tensor: MutableTensor<T>, am: IntArray, offsets: IntArray, shape: IntArray) :
        IndexMapView<T>(tensor, am, offsets, shape), MutableTensor<T> {
    override fun set(idx: Index, v: T) {
        checkIdx(idx)
        val tIdx = mapIdx(idx)
        tensor[tIdx] = v
    }
}

internal object TensorUtils {

    fun addIfNegative(a: Int, m: Int): Int {
        return if (a < 0) {
            a + m
        } else {
            a
        }
    }


    fun <T : Any> add(x: Tensor<T>, y: Tensor<T>): MutableTensor<T> {
        val (x1, y1) = broadcast(x, y)
        val mc = x.mathCalculator
        return ATensor.buildFromSequence(mc, x1.shape, x1.indices.map { idx -> mc.add(x1[idx], y1[idx]) })
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
    fun <T : Any> subtract(x0: Tensor<T>, y0: Tensor<T>): MutableTensor<T> {
        val (x, y) = broadcast(x0, y0)
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
    fun <T : Any> multiply(x0: Tensor<T>, y0: Tensor<T>): MutableTensor<T> {
        val (x, y) = broadcast(x0, y0)
        val mc = x.mathCalculator
        return ATensor.buildFromSequence(mc, x.shape, x.indices.map { idx -> mc.multiply(x[idx], y[idx]) })
    }

    /**
     * Returns the **element-wise** division of this tensor and `y`.
     *
     * @throws ArithmeticException if zero-division happens
     */
    fun <T : Any> divide(x0: Tensor<T>, y0: Tensor<T>): MutableTensor<T> {
//        Tensor.checkShape(x, y)
        val (x, y) = broadcast(x0, y0)
        val mc = x.mathCalculator
        return ATensor.buildFromSequence(mc, x.shape, x.indices.map { idx -> mc.divide(x[idx], y[idx]) })
    }

    fun <T : Any> inner(x: Tensor<T>, y: Tensor<T>): T {
        require(x.isSameShape(y)) {
            "Two tensor must have the same shape for inner!" +
                    "Given shapes: ${x.shape.contentToString()}, ${y.shape.contentToString()}."
        }
        val mc = x.mathCalculator
        return x.elementSequence().zip(y.elementSequence()).fold(mc.zero) { re, (a, b) ->
            mc.eval { re + a * b }
        }
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
        Tensor.checkShape(x, y)
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


    fun <T : Any> computeSliceView(x: Tensor<T>, slices: List<Any?>)
            : Triple<IntArray, List<IntProgression>, IntArray> {
        // remark: we need union type here
        var l = 0 // in this tensor
        val ns = arrayListOf<Int>()
        val ranges = arrayListOf<IntProgression>()
        val shape = x.shape
        val am = arrayListOf<Int>()
        for ((pos, t) in slices.withIndex()) {
            if (Tensor.NEW_AXIS != t && Tensor.DOTS != t) {
                require(l < x.dim) { "Too many indices!" }
            }
            when (t) {
                is Int -> {
                    val i = MathUtils.mod(t, shape[l])
                    ranges.add(i..i)
                    l++
                }
                is IntProgression -> {
                    val r = IntProgression.fromClosedRange(
                            addIfNegative(t.first, shape[l]),
                            addIfNegative(t.last, shape[l]),
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
                    ranges.add(0 until shape[l])
                    ns.add(shape[l])
                    l++
                }
                Tensor.NEW_AXIS -> {
                    ns.add(1)
                    am.add(-1)
                }
                Tensor.DOTS -> {
                    var rem = x.dim - ranges.size
                    for (j in (pos + 1) until slices.size) {
                        val t2 = slices[j]
                        require(Tensor.DOTS != t2) {
                            "Only one '...' is allowed in slice!"
                        }
                        if (t2 == null || t2 is Int || t2 is IntProgression) {
                            rem--
                        }
                    }
                    repeat(rem) {
                        am.add(l)
                        ranges.add(0 until shape[l])
                        ns.add(shape[l])
                        l++
                    }
                }
                else -> {
                    throw IllegalArgumentException("Not supported slice for $t")
                }
            }
        }
        while (l < x.dim) {
            am.add(l)
            ranges.add(0 until shape[l])
            ns.add(shape[l])
            l++
        }
        if (ns.isEmpty()) {
            // return a 1-d tensor instead
            am.add(-1)
            ns.add(1)
        }
        return Triple(am.toIntArray(), ranges, ns.toIntArray())
    }

    fun newAxisSliceView(x: Tensor<*>, axis: Int):
            Triple<IntArray, List<IntProgression>, IntArray> {
        val dim = x.dim
        val ax = addIfNegative(axis, dim)
        require(ax in 0 until dim)
        val shape = x.shape
        val ns = IntArray(dim + 1)
        val ranges = shape.map { 0 until it }
        val am = IntArray(dim + 1)
        for (i in 0 until ax) {
            ns[i] = shape[i]
            am[i] = i
        }
        for (i in ax until dim) {
            ns[i + 1] = shape[i]
            am[i + 1] = i
        }
        ns[ax] = 1
        am[ax] = -1
        return Triple(am, ranges, ns)
    }

    /**
     * Returns the general einsum of several tensors, assume `R = resShape.size`,
     * `M = mulShape.size`, then the general formula is
     *
     *      result[i_1,...,i_R] = sum(j_1,...,j_M; prod(k; t_k[tIdx]))
     *      where tIdx[l] = i_{tToResList[k][l]} or j_{tToMulList[k][l]}
     *
     *
     * @param resShape the shape of resulting tensors
     * @param mulShape the shape of multiplying axes
     * @param tToResList whose element represents a tensor's axis that is only selected.
     * @param tToMulList whose element represents a tensor's axis that will be multiplied (with other tensors'
     * corresponding axes)
     */
    fun <T : Any> einsum(ts: List<Tensor<T>>,
                         resShape: IntArray, mulShape: IntArray,
                         tToResList: List<IntArray>, tToMulList: List<IntArray>,
                         mc: MathCalculator<T>): ATensor<T> {
        val n = ts.size
        val result = ATensor.constant(mc.zero, resShape, mc)
        val data = result.data
        val tIdxList = Array(ts.size) { IntArray(ts[it].dim) }

        val mIndices = IterUtils.prodIdxN(mulShape)
        fun placeIdx(partIdx: Index, tToPartList: List<IntArray>) {
            for (k in 0 until n) {
                val tToPart = tToPartList[k]
                val tIdx = tIdxList[k]
                for (l in tToPart.indices step 2) {
                    val axisT = tToPart[l]
                    val axisR = tToPart[l + 1]
                    tIdx[axisT] = partIdx[axisR]
                }
            }
        }

        var pos = 0
        for (rIdx in result.indices) {
            placeIdx(rIdx, tToResList)
            //place the indices corresponds to res part
            var re = mc.zero
            for (mIdx in mIndices) {
                placeIdx(mIdx, tToMulList)
                //place the indices corresponds to mul part
                var mul = mc.one
                for (k in 0 until n) {
                    val t = ts[k]
                    val tIdx = tIdxList[k]
                    mul = mc.eval { mul * t[tIdx] }
                }
                re = mc.eval { re + mul }
            }
            data[pos++] = re
        }
        return result
    }

    val CHAR_PATTERN = "\\w\\d*".toRegex()


    fun <T : Any> einsum(ts: List<Tensor<T>>, expr: String): MutableTensor<T> {
        require(ts.isNotEmpty())
        val i1 = expr.indexOf("->")
        val tAxes = if (i1 >= 0) {
            expr.substring(0, i1)
        } else {
            expr
        }.split(",").also {
            require(it.size == ts.size) {
                "Count mismatch: ${it.size} tensors are required but ${ts.size} is given. "

            }
        }.mapIndexed { i, s ->
            CHAR_PATTERN.findAll(s.trim()).map {
                it.value
            }.toList().also {
                require(it.size == ts[i].dim) {
                    "Dim mismatch for ${i + 1}-th tensor: " +
                            "${it.size} is required but given tensor dim = ${ts[i].dim}. " +
                            "Expr=[${expr}], tensor dims=${ts.joinToString { t -> t.dim.toString() }}"
                }
            }
        }
        val charCount = sortedMapOf<String, Int>().also {
            tAxes.asSequence().flatten().forEach { s -> it.merge(s, 1, Int::plus) }
        }
        val chars = charCount.keys
        val res: List<String> = if (i1 >= 0) {
            val s = expr.substring(i1 + 2)
            CHAR_PATTERN.findAll(s.trim()).map {
                it.value
            }.toList()
        } else {
            charCount.entries.filter { it.value == 1 }.map { it.key }
        }

        val mul = chars.toSortedSet().also { it.removeAll(res) }
        val chToResIdx = res.withIndex().associate { it.value to it.index }
        val chToMulIdx = mul.withIndex().associate { it.value to it.index }
        fun shapeFor(part: Collection<String>): IntArray {
            return if (part.isEmpty()) {
                intArrayOf(1)
            } else {
                IntArray(part.size)
            }
        }

        val resShape = shapeFor(res)
        val mulShape = shapeFor(mul)
        val n = ts.size
        val tToResList = ArrayList<IntArray>(n)
        val tToMulList = ArrayList<IntArray>(n)
        for (k in 0 until n) {
            val t = ts[k]
            val tShape = t.shape
            val axes = tAxes[k]
            val tToRes = arrayListOf<Int>()
            val tToMul = arrayListOf<Int>()
            for (l in 0 until t.dim) {
                val ch = axes[l]
                fun addIdxAndCheckShape(chToPartIdx: Map<String, Int>, tToPart: MutableList<Int>, partShape: IntArray) {
                    val idx = chToPartIdx[ch] ?: return
                    if (partShape[idx] == 0) {
                        partShape[idx] = tShape[l]
                    } else {
                        require(partShape[idx] == tShape[l]) {
                            "Shape mismatch for ${l + 1}-th tensor at axis $idx, " +
                                    "required length=${partShape[idx]} but ${tShape[idx]} is given. " +
                                    "Expr=[${expr}], shapes=${ts.joinToString { it.shape.contentToString() }}"

                        }
                    }
                    tToPart += l
                    tToPart += idx
                }
                addIdxAndCheckShape(chToResIdx, tToRes, resShape)
                addIdxAndCheckShape(chToMulIdx, tToMul, mulShape)
            }
            tToResList += tToRes.toIntArray()
            tToMulList += tToMul.toIntArray()
        }
        //TODO optimize the order of mul
        return einsum(ts, resShape, mulShape, tToResList, tToMulList, ts[0].mathCalculator)
    }


    fun <T : Any> sumInOneAxis(t: Tensor<T>, sumAxis: Int): MutableTensor<T> {
        val mc = t.mathCalculator
        val axis = addIfNegative(sumAxis, t.dim)
        require(axis in 0 until t.dim)
        if (t.dim == 1) {
            return Tensor.scalar(t.sumAll(), t.mathCalculator)
        }
        val tShape = t.shape
        val shape = IntArray(t.dim - 1)
        tShape.copyInto(shape, 0, 0, axis)
        tShape.copyInto(shape, axis, axis + 1)
        val result = ATensor.zeros(shape, mc)
        val data = result.data
        val tIdx = IntArray(t.dim)
        val axisLen = t.lengthAt(axis)
        var pos = 0
        for (idx in result.indices) {
            idx.copyInto(tIdx, 0, 0, axis)
            idx.copyInto(tIdx, axis + 1, axis)
            var re = mc.zero
            for (i in 0 until axisLen) {
                tIdx[axis] = i
                re = mc.eval { re + t[tIdx] }
            }
            data[pos++] = re
        }
        return result
    }

    /**
     * Returns the sum of [t] in given [sumAxes] and [remAxes], it is required that both axes are non-empty.
     */
    fun <T : Any> sumInAxes(t: Tensor<T>, sumAxes: IntArray, remAxes: IntArray): MutableTensor<T> {
        val mc = t.mathCalculator
        val tShape = t.shape
        fun makeShapeArray(axes: IntArray): IntArray {
            val shape = IntArray(axes.size)
            for (i in axes.indices) {
                shape[i] = tShape[axes[i]]
            }
            return shape
        }

        val sumShape = makeShapeArray(sumAxes)
        val resShape = makeShapeArray(remAxes)
        val result = ATensor.zeros(resShape, mc)
        val data = result.data
        val tIdx = IntArray(t.dim)

        fun placeIdx(idx: Index, am: IntArray) {
            for (i in am.indices) {
                tIdx[am[i]] = idx[i]
            }
        }

        var pos = 0
        val sumIndices = IterUtils.prodIdxN(sumShape)
        for (idx in result.indices) {
            placeIdx(idx, remAxes)
            var re = mc.zero
            for (sumIdx in sumIndices) {
                placeIdx(sumIdx, sumAxes)
                re = mc.eval { re + t[tIdx] }
            }
            data[pos++] = re
        }
        return result
    }


    fun <T : Any> sum(t: Tensor<T>, sumAxesList: List<Int>): MutableTensor<T> {
        if (sumAxesList.isEmpty()) {
            return Tensor.scalar(t.sumAll(), t.mathCalculator)
        }
        if (sumAxesList.size == 1) {
            return sumInOneAxis(t, sumAxesList.first())
        }
        val axesSet = sumAxesList.asSequence().map {
            val axis = addIfNegative(it, t.dim)
            require(axis in 0 until t.dim)
            axis
        }.toSet()
        if (axesSet.size == t.dim) {
            return Tensor.scalar(t.sumAll(), t.mathCalculator)
        }
        val sumAxes = axesSet.toMutableList()
        val remAxes = (0 until t.dim).filterNotTo(arrayListOf()) { it in axesSet }
        sumAxes.sortBy { axis -> t.lengthAt(axis) } // place axes of bigger length backwards
        return sumInAxes(t, sumAxes.toIntArray(), remAxes.toIntArray())
    }

    fun <T : Any> prepareConcat(ts: List<Tensor<T>>, axis: Int): Pair<Int, IntArray> {
        require(ts.isNotEmpty())
        val dim = ts[0].dim
        val shape = ts[0].shape
        val ax = addIfNegative(axis, dim)
        require(ax in 0 until dim) {
            "Axis $axis out of bound."
        }
        shape[axis] = 0
        for ((k, t) in ts.withIndex()) {
            require(t.dim == dim) {
                "Tensor dim mismatch for ${k + 1}-th tensor: required dim=$dim, but ${t.dim} is given."
            }
            for (l in shape.indices) {
                if (l == ax) {
                    shape[l] += t.lengthAt(l)
                } else {
                    require(shape[l] == t.lengthAt(l)) {
                        "Tensor shape mismatch for ${k + 1}-th tensor at axis ${l}: " +
                                "required length=${shape[l]}, but ${t.lengthAt(l)} is given."
                    }
                }
            }
        }
        return ax to shape
    }


    fun <T : Any> prepareStack(ts: List<Tensor<T>>, axis: Int): Pair<Int, IntArray> {
        require(ts.isNotEmpty())
        require(ts.all { it.isSameShape(ts[0]) }) {
            "Cannot stack tensors of shapes: ${ts.joinToString { it.shape.contentToString() }}"
        }
        val ax = addIfNegative(axis, ts[0].dim)
        val shape = ts[0].shape
        val ns = IntArray(shape.size + 1)
        shape.copyInto(ns, endIndex = ax)
        shape.copyInto(ns, ax + 1, ax)
        ns[ax] = ts.size
        return ax to ns
    }

    //    fun <T:Any> reshape(x : T)
    fun prepareNewShape(t: Tensor<*>, ns: IntArray) {
        val size = t.size
        require(ns.isNotEmpty())
        var n1Idx = -1
        var s = 1
        for (i in ns.indices) {
            val len = ns[i]
            if (len == -1) {
                if (n1Idx != -1) {
                    throw IllegalArgumentException("Only one -1 is allowed in the shape array: ${ns.contentToString()}!")
                }
                n1Idx = i
            } else {
                require(len > 0) {
                    "Shape must be positive: ${ns.contentToString()}!"
                }
                s *= len
            }
        }
        if (n1Idx >= 0) {
            val len = size / s
            require(s * len == size) {
                "The given shape ${ns.contentToString()} does not fit the original shape ${t.shape.contentToString()}."
            }
            ns[n1Idx] = len
        } else {
            require(s == size) {
                "The given shape ${ns.contentToString()} does not fit the original shape ${t.shape.contentToString()}."
            }
        }
    }

    fun <T : Any> broadcastTo(t: Tensor<T>, ns: IntArray): Tensor<T> {
        if (t.shape.contentEquals(ns)) {
            return t
        }
        require(t.dim <= ns.size) {
            "Cannot broad cast ${t.shape.contentToString()} to ${ns.contentToString()}!"
        }
        val extendedAxes = arrayListOf<Int>()
        val shape = t.shape
        val d = ns.size - t.dim
        for (l in shape.lastIndex downTo 0) {
            if (shape[l] == ns[d + l]) {
                continue
            }
            if (shape[l] == 1) {
                extendedAxes.add(l)
                continue
            }
            throw IllegalArgumentException("Cannot broadcast ${t.shape.contentToString()} to ${ns.contentToString()}, shape mismatch" +
                    "at axis ${l + d}.")
        }
        return BroadcastView(t, ns, d, extendedAxes.toIntArray())
    }

    private fun <T : Any> broadcast0(t1: Tensor<T>, t2: Tensor<T>): Pair<Tensor<T>, Tensor<T>> {
        val nDim = t2.dim
        val newShape = IntArray(nDim)
        val d2 = t2.dim - t1.dim
        val s1 = t1.shape
        val s2 = t2.shape
        val extended1 = arrayListOf<Int>()
        val extended2 = arrayListOf<Int>()
        for (l in s1.lastIndex downTo 0) {
            if (s1[l] == s2[l + d2]) {
                newShape[l + d2] = s1[l]
                continue
            }
            if (s1[l] == 1) {
                newShape[l + d2] = s2[l]
                extended1.add(l)
                continue
            }
            if (s2[l + d2] == 1) {
                newShape[l + d2] = s1[l]
                extended2.add(l + d2)
                continue
            }
            throw IllegalArgumentException("Cannot broadcast ${s1.contentToString()} with ${s2.contentToString()}, " +
                    "shape mismatch at axis ${l} .")
        }
        s2.copyInto(newShape, endIndex = d2)
        val r1 = BroadcastView(t1, newShape, d2, extended1.toIntArray())
        val r2 = if (extended2.isEmpty()) {
            t2
        } else {
            BroadcastView(t2, newShape, 0, extended2.toIntArray())
        }
        return r1 to r2
//        val newAxes2 = intArrayOf()

    }

    fun <T : Any> broadcast(t1: Tensor<T>, t2: Tensor<T>): Pair<Tensor<T>, Tensor<T>> {
        if (t1.isSameShape(t2)) {
            return t1 to t2
        }
        return if (t1.dim <= t2.dim) {
            broadcast0(t1, t2)
        } else {
            broadcast0(t2, t1)
        }

    }

    fun <T : Any> matmul(x: Tensor<T>, y: Tensor<T>, r: Int): MutableTensor<T> {
        val shape1 = x.shape
        val shape2 = y.shape
        val dim1 = shape1.size
        val dim2 = shape2.size
        val mc = x.mathCalculator
        require(dim1 >= r && dim2 >= r)
        if (dim1 == r && dim2 == r) {
            return Tensor.scalar(x.inner(y), mc)
        }
        val mShape = shape1.sliceArray(dim1 - r until dim1)
        require(mShape.contentEquals(shape2.sliceArray(0 until r)))
        var rShape = shape1.sliceArray(0 until (dim1 - r)) + shape2.sliceArray(r until dim2)
        if (rShape.isEmpty()) {
            rShape = intArrayOf(1)
        }
        val result = ATensor.zeros(rShape, mc)
        val data = result.data
        val xIdx = IntArray(dim1)
        val yIdx = IntArray(dim2)
        val mIndices = IterUtils.prodIdxN(mShape)
        var pos = 0
        for (rIdx in result.indices) {
            rIdx.copyInto(xIdx, endIndex = dim1 - r)
            rIdx.copyInto(yIdx, destinationOffset = r, startIndex = dim1 - r)
            var re = mc.zero
            for (mIdx in mIndices) {
                mIdx.copyInto(xIdx, destinationOffset = dim1 - r)
                mIdx.copyInto(yIdx)
                re = mc.eval { re + x[xIdx] * y[yIdx] }
            }
            data[pos++] = re
        }
        return result

    }

    private fun prepareDiag(x: Tensor<*>, axis1: Int, axis2: Int, offset: Int): Triple<IntArray, IntArray, IntArray> {
        require(x.dim >= 2) {
            "The given tensor's dim must >= 2!"
        }
        val dim = x.dim
        val ax1 = addIfNegative(axis1, dim)
        val ax2 = addIfNegative(axis2, dim)
        require(ax1 in 0 until dim && ax2 in 0 until dim) {
            "Invalid axes: $axis1, $axis2 for tensor dim=$dim. "
        }
        require(ax1 != ax2) {
            "The two axes for diagonal must not be the same!"
        }
        val am = IntArray(dim)
        // xIdx[i] = idx[am[i]], x: i <-> view: am[i]
        val offsets = IntArray(dim)
        val shape = IntArray(dim - 1)
        val xShape = x.shape

        fun computeShape(ax1: Int, ax2: Int, offset: Int): Int {
            val s = min(xShape[ax1], xShape[ax2] - offset)
            require(s > 0) {
                "The resulting tensor is empty! " +
                        "Diagonal in axes $axis1,$axis2 with offset=$offset, " +
                        "tensor shape=${x.shape.contentToString()}."
            }
            return s
        }
        if (offset >= 0) {
            shape[dim - 2] = computeShape(ax1, ax2, offset)
            offsets[ax2] = offset
        } else {
            shape[dim - 2] = computeShape(ax2, ax1, -offset)
            offsets[ax1] = -offset
        }


        var pos = 0
        for (i in 0 until dim) {
            if (i == ax1 || i == ax2) {
                continue
            }
            am[i] = pos
            shape[pos] = xShape[i]
            pos++
        }
        am[ax1] = dim - 2
        am[ax2] = dim - 2

        return Triple(am, offsets, shape)

    }

    fun <T : Any> diagonal(x: Tensor<T>, axis1: Int, axis2: Int, offset: Int): Tensor<T> {
        val (am, offsets, shape) = prepareDiag(x, axis1, axis2, offset)
        return IndexMapView(x, am, offsets, shape)
    }

    fun <T : Any> diagonal(x: MutableTensor<T>, axis1: Int, axis2: Int, offset: Int): MutableTensor<T> {
        val (am, offsets, shape) = prepareDiag(x, axis1, axis2, offset)
        return MutableIndexMapView(x, am, offsets, shape)
    }
}
