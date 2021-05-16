package cn.ancono.utilities

import cn.ancono.utilities.EnumState.*

private enum class EnumState {
    UNKNOWN,
    READY,
    EXHAUSTED
}

/**
 * Contains some utilities for iterators and sequences.
 */
object IterUtils {


    private class ProductIteratorNoCopy<T>(val iterables: List<Iterable<T>>) : Iterator<List<T>> {
        val iterators = Array(iterables.size) { i ->
            iterables[i].iterator()
        }
        private var ready: EnumState = READY

        val elements: MutableList<T?>

        init {

            elements = iterators.mapTo(ArrayList(iterators.size)) {
                if (it.hasNext()) {
                    it.next()
                } else {
                    ready = EXHAUSTED
                    null
                }
            }

        }

        protected fun prepareNext() {
            if (ready != UNKNOWN) {
                return
            }

            for (i in iterators.lastIndex downTo 0) {
                val it = iterators[i]
                if (it.hasNext()) {
                    elements[i] = it.next()
                    ready = READY
                    return
                } else {
                    iterators[i] = iterables[i].iterator()
                    elements[i] = iterators[i].next()
                }
            }
            ready = EXHAUSTED
        }


        override fun hasNext(): Boolean {
            prepareNext()
            return ready == READY
        }

        override fun next(): List<T> {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            ready = UNKNOWN
            @Suppress("UNCHECKED_CAST")
            return elements as List<T>

        }
    }

    /**
     * Returns a sequence corresponding to the cartesian product of the given iterables. The last iterable is
     * iterated over first.
     *
     * @param its a list of iterables
     *
     * @param copy whether to directly return the backing list in the sequence iterator or make a
     * copy of it. If `copy = false`, the resulting sequence is read-only-traversable.
     * That is, each element in the sequence should only be used before subsequent invocations of `next()` and `hasNext()`.
     * Setting it to `false` can provide better performance.
     *
     */
    fun <T> prod(its: List<Iterable<T>>, copy: Boolean = true): Sequence<List<T>> {
        if (its.isEmpty()) {
            return emptySequence()
        }
        val seq = Sequence { ProductIteratorNoCopy(its) }
        return if (copy) {
            seq.map { ArrayList(it) }
        } else {
            seq
        }
    }

    /**
     * Returns a sequence corresponding to the cartesian product of the given iterables.
     *
     * @see prod
     */
    fun <T> prod(vararg its: Iterable<T>): Sequence<List<T>> {
        return prod(its.asList())
    }

    private abstract class IdxIteratorTemplate(val n: Int) : Iterator<IntArray> {
        protected var state: EnumState = UNKNOWN // 0: non-determined, 1: ready, 2: exhausted
        protected val indices = IntArray(n)
        protected abstract fun prepareNext()

        override fun hasNext(): Boolean {
            prepareNext()
            return state == READY
//            return indices.any {   }
//            return indices[0] in ranges[0]
        }

        override fun next(): IntArray {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            state = UNKNOWN
            return indices
        }
    }

    private class IndexIteratorNoCopy(val ranges: List<IntProgression>) : IdxIteratorTemplate(ranges.size) {


//        private operator fun IntProgression.contains(x : Int) : Boolean  = this.first <= x && x <= this.last

        init {
            for (i in indices.indices) {
                indices[i] = ranges[i].first
            }
            val nonEmpty = ranges.all { !it.isEmpty() }
            state = if (nonEmpty) {
                READY
            } else {
                EXHAUSTED
            }
        }

        override fun prepareNext() {
            if (state != UNKNOWN) {
                return
            }
            for (i in indices.lastIndex downTo 0) {
                val range = ranges[i]
                if (indices[i] != range.last) {
                    indices[i] += range.step
                    state = READY
                    return
                } else {
                    indices[i] = range.first
                }

            }
            state = EXHAUSTED // end
        }


    }


    /**
     * Returns a sequence corresponding to the cartesian product of the given int progressions.
     * The last dimension is iterated over first.
     *
     *
     * @param copy whether to directly return the backing int array in the sequence iterator or make a
     * copy of it. If `copy = false`, the resulting sequence is read-only-traversable.
     * That is, each element in the sequence should only be used before subsequent invocations of `next()` and `hasNext()`.
     * Setting it to `false` can provide better performance.
     */
    fun prodIdx(ranges: List<IntProgression>, copy: Boolean = true): Sequence<IntArray> {
        if (ranges.isEmpty()) {
            return emptySequence()
        }
        val seq = Sequence { IndexIteratorNoCopy(ranges) }
        return if (copy) {
            seq.map { it.clone() }
        } else {
            seq
        }

    }

    /**
     * Returns a sequence corresponding to the cartesian product of the given int progressions.
     *
     * @see prodIdx
     */
    fun prodIdx(vararg ranges: IntProgression): Sequence<IntArray> {
        return prodIdx(ranges.asList())
    }

    /**
     * Returns a sequence corresponding to the cartesian product of the given int progressions.
     *
     * This method is a no-copy version of the corresponding `prodIdx` method.
     *
     *  @see prodIdx
     */
    fun prodIdxN(vararg ranges: IntProgression): Sequence<IntArray> {
        return prodIdx(ranges.asList(), copy = false)
    }

    /**
     * Returns the cartesian product of `[0,bounds[i])`.
     */
    fun prodIdx(bounds: IntArray): Sequence<IntArray> {
        return prodIdx(bounds.map { b -> 0 until b })
    }

    /**
     * Returns the cartesian product of `[0,bounds[i])`.
     *
     * This method is a no-copy version of the corresponding `prodIdx` method.
     */
    fun prodIdxN(bounds: IntArray): Sequence<IntArray> {
        return prodIdx(bounds.map { b -> 0 until b }, copy = false)
    }

    /**
     * Returns the cartesian product of two iterables as a sequence of pairs.
     */
    fun <T, S> prod2(it1: Iterable<T>, it2: Iterable<S>): Sequence<Pair<T, S>> {
        val s1 = it1.asSequence()
        val s2 = it2.asSequence()
        return s1.flatMap { a ->
            s2.map { b -> a to b }
        }
    }

    private class GapIndexIteratorNoCopy(
            val ds: IntArray,
            a: Int,
            val b: Int
    ) : IdxIteratorTemplate(ds.size) {
        init {
            require(ds.all { it >= 0 })
        }

        /*
        Constraints:
        a <= indices[0]
        indices[i] + ds[i] <= indices[i+1]
        indices[i] <= b
         */
        val dSum: IntArray = IntArray(n)

        init {
            indices[0] = a
            for (i in (n - 2) downTo 0) {
                dSum[i] = dSum[i + 1] + ds[i]
            }
            state = if (placeAfter(0)) {
                READY
            } else {
                EXHAUSTED
            }
        }

        private fun placeAfter(start: Int): Boolean {
            if (dSum[start] + indices[start] > b) {
                return false
            }
            for (i in start..(n - 2)) {
                indices[i + 1] = indices[i] + ds[i]
            }
            return true
        }

        override fun prepareNext() {
            if (state != UNKNOWN) {
                return
            }
            indices[n - 1]++
            if (indices[n - 1] <= b) {
                state = READY
                return
            }
            var i = n - 2
            while (i >= 0) {
                indices[i]++
                if (placeAfter(i)) {
                    state = READY
                    return
                }
                i--
                continue
            }
            state = EXHAUSTED
        }

    }


//    /**
//     * Returns a sequence of integer indices `(x_0, ..., x_{n-1})` that
//     *
//     *     a <= x_0 <= b
//     *     x_i + d <= x_{i+1} <= b
//     *
//     * It is required that `d >= 0`.
//     *
//     * This method is a no-copy version of the corresponding `idxGapN` method.
//     */
//    fun idxGapN(n: Int, a: Int, b: Int, d: Int): Sequence<IntArray> {
//        val ds = IntArray(n)
//        ds.fill(d)
//        return idxGap(a, b, ds, false)
//    }

    /**
     * Returns a sequence of integer indices `(x_0, ..., x_{n-1})` that
     *
     *     a <= x_0 <= b
     *     x_i + ds[i] <= x_{i+1} <= b
     *
     * It is required that `d >= 0`.
     *
     * @param copy whether to directly return the backing int array in the sequence iterator or make a
     * copy of it. If `copy = false`, the resulting sequence is read-only-traversable.
     * That is, each element in the sequence should only be used before subsequent invocations of `next()` and `hasNext()`.
     * Setting it to `false` can provide better performance.
     */
    fun idxGap(a: Int, b: Int, ds: IntArray, copy: Boolean = true): Sequence<IntArray> {
        if (ds.isEmpty()) {
            return emptySequence()
        }
        val seq = Sequence { GapIndexIteratorNoCopy(ds, a, b) }
        if (!copy) {
            return seq
        }
        return seq.map { it.clone() }
    }


    /**
     * Returns a sequence of integer indices `(x_0, ..., x_{n-1})` that
     *
     *     a <= x_0 <= b
     *     x_i + d <= x_{i+1} <= b
     *
     * It is required that `d >= 0`.
     */
    fun idxGap(n: Int, a: Int, b: Int, d: Int, copy: Boolean = true): Sequence<IntArray> {
        val ds = IntArray(n)
        ds.fill(d)
        return idxGap(a, b, ds, copy)
    }

    /**
     * Returns a sequence of integer indices `(x_0, ..., x_{n-1})` that
     *
     *     a <= x_0 < x_1 < ... < x_{n-1} <= b
     *
     * Remark: the number of elements in the returned sequence is equal to `C(b-a+1, n)`, where
     * `C` refer to the combination.
     *
     * @see idxGap
     * @see idxOrderedEq
     */
    fun idxOrdered(n: Int, a: Int, b: Int, copy: Boolean = true): Sequence<IntArray> {
        return idxGap(n, a, b, 1, copy)
    }

    /**
     * Returns a sequence of int arrays representing combinations of `m` integers in the range of `0, 1, ..., n-1`.
     * The resulting arrays are ordered.
     * @see idxOrdered
     */
    fun comb(n: Int, m: Int, copy: Boolean = true): Sequence<IntArray> {
        return idxOrdered(m, 0, n - 1, copy)
    }

    /**
     * Returns a sequence of integer indices `(x_0, ..., x_{n-1})` that
     *
     *     a <= x_0 <= x_1 <= ... <= x_{n-1} <= b
     *
     * Remark: the number of elements in the returned sequence is equal to `C(b-a+n, n)`, where
     * `C` refer to the combination.
     *
     * @see idxGap
     * @see idxOrdered
     */
    fun idxOrderedEq(n: Int, a: Int, b: Int, copy: Boolean = true): Sequence<IntArray> {
        return idxGap(n, a, b, 0, copy)
    }


    private fun locateNext(start: Int, selected: BooleanArray, indices: IntArray): Int {
        var i = start
        while (i >= 0) {
            selected[indices[i]] = false
            var t = indices[i] + 1
            while (t < selected.size && selected[t]) {
                t++
            }
            if (t == selected.size) {
                i--
                continue
            }
            selected[t] = true
            indices[i] = t
            break
        }
        return i
    }

    private fun fillRemain(pos: Int, selected: BooleanArray, indices: IntArray) {
        var i = pos + 1
        var last = 0
        while (i < indices.size) {
            try {
                while (selected[last]) {
                    last++
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            selected[last] = true
            indices[i] = last
            last++
            i++
        }
    }

    private class PermutationIterNoCopy(m: Int, n: Int) : IdxIteratorTemplate(n) {
        private val selected: BooleanArray = BooleanArray(m)

        init {
            for (i in 0 until n) {
                indices[i] = i
                selected[i] = true
            }
            state = READY
        }


        override fun prepareNext() {
            if (state != UNKNOWN) {
                return
            }
            val i = locateNext(n - 1, selected, indices)
            if (i < 0) {
                state = EXHAUSTED // end
                return
            }
            fillRemain(i, selected, indices)
            state = READY
        }


    }

    /**
     * Returns a sequence of arrays representing permutations of `m` integers in the range of `0, 1, ..., n-1`.
     *
     * For example, `perm(3,2)` will return a sequence of `[0,1], [0,2], [1,0], [1,2], [2,0], [2,1]`.
     */
    fun perm(n: Int, m: Int, copy: Boolean = true): Sequence<IntArray> {
        if (n < m) {
            return emptySequence()
        }
        val seq = Sequence { PermutationIterNoCopy(n, m) }
        if (copy) {
            return seq.map { it.clone() }
        }
        return seq
    }


    private class PermutationIterRevNoCopy(val n: Int) : Iterator<Pair<IntArray, Int>> {
        private var state: EnumState = UNKNOWN // 0: non-determined, 1: ready, 2: exhausted
        private val indices = IntArray(n)
        private val selected: BooleanArray = BooleanArray(n)
        private var rev: Int = 0
        private val revDiffTable: IntArray = IntArray(n - 2)

        init {
            for (i in 0 until n) {
                indices[i] = i
                selected[i] = true
            }
            state = READY
            for (i in revDiffTable.lastIndex downTo 0) {
                val r = n - i
                revDiffTable[i] = 1 - (r - 1) * (r - 2) / 2
            }
        }

        private fun prepareNext() {
            if (state != UNKNOWN) {
                return
            }
            if (indices[n - 2] < indices[n - 1]) {
                //trivial swapping case
                rev++
                val t = indices[n - 1]
                indices[n - 1] = indices[n - 2]
                indices[n - 2] = t
                state = READY
                return
            }
            selected[indices[n - 1]] = false
            selected[indices[n - 2]] = false
            val i = locateNext(n - 3, selected, indices)
            if (i < 0) {
                state = EXHAUSTED // end
                return
            }
            rev += revDiffTable[i]
            fillRemain(i, selected, indices)
            state = READY
        }

        override fun hasNext(): Boolean {
            prepareNext()
            return state == READY
        }

        override fun next(): Pair<IntArray, Int> {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            state = UNKNOWN
            return indices to rev
        }


    }

    /**
     * Returns a sequence of arrays representing permutations of `n` integers in the range of `0, 1, ..., n-1` along
     * with the reverse count of the permutation.
     *
     */
    fun permRev(n: Int, copy: Boolean = true): Sequence<Pair<IntArray, Int>> {
        if (n <= 1) {
            return sequenceOf(IntArray(n) to 0)
        }
        val seq = Sequence { PermutationIterRevNoCopy(n) }
        if (copy) {
            return seq.map { it.first.clone() to it.second }
        }
        return seq
    }

}

fun main() {
//    val n = 5
//    val seq = IterUtils.perm(3, 2, false)
//    for (x in seq) {
//        println(x.contentToString() + ": " + CombUtils.reverseCount(x))
//    }
//    val seq2 = IterUtils.permRev(n,false)
//    for ((x, inv) in seq2) {
//        val rev = CombUtils.reverseCount(x)
//        println(x.contentToString() + ": $inv, $rev")
//    }

//    val n = 4
//    val x = IntArray(n){it}
//    val count = IntArray(n-1)
//    while(true){
//        println(x.contentToString())
//        var j = 0
//        while(j < n-1) {
//            if(count[j] <= j){
//                val t = x[j]
//                x[j] = x[j+1]
//                x[j+1] = t
//                count[j]++
//                break
//            }
//            j++
//        }
//        if (j == n-1) {
//            break
//        }
//        j--
//        while (j >= 0) {
//            count[j] = 0
//            j--
//        }
//    }
//    val bounds = IntArray(12) { 5 }
//    measureTimeMillis {
//        val seq = IterUtils.prodIdx(bounds)
//        var t = 0
//        seq.forEach { t += it.size+1 }
//        println(t)
//    }.also { println(it) }
//    println()
//    measureTimeMillis {
//        val seq = IterUtils.prodIdx(bounds)
//        var t = 0
//        seq.forEach { t += it.size+1 }
//        println(t)
//    }.also { println(it) }
//    println()
//    measureTimeMillis {
//        val seq = IterUtils.prodIdxN(bounds)
//        var t = 0
//        seq.forEach { t += it.size+1 }
//        println(t)
//    }.also { println(it) }
//    println()
}