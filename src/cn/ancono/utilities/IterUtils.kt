package cn.ancono.utilities


/**
 * Contains some utilities for iterators and sequences.
 */
object IterUtils {


    open class ProductIteratorNoCopy<T>(val iterables: List<Iterable<T>>) : Iterator<List<T>> {
        val iterators = Array(iterables.size) { i ->
            iterables[i].iterator()
        }
        private var ready: Int = 1

        val elements: MutableList<T?>

        init {

            elements = iterators.mapTo(ArrayList(iterators.size)) {
                if (it.hasNext()) {
                    it.next()
                } else {
                    ready = 2
                    null
                }
            }

        }

        protected fun prepareNext() {
            if (ready != 0) {
                return
            }

            for (i in iterators.lastIndex downTo 0) {
                val it = iterators[i]
                if (it.hasNext()) {
                    elements[i] = it.next()
                    ready = 1
                    return
                } else {
                    iterators[i] = iterables[i].iterator()
                    elements[i] = iterators[i].next()
                }
            }
            ready = 2
        }


        override fun hasNext(): Boolean {
            prepareNext()
            return ready == 1
        }

        override fun next(): List<T> {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            ready = 0
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

    open class IndexIteratorNoCopy(val ranges: List<IntProgression>) : Iterator<IntArray> {
        val indices = IntArray(ranges.size) { ranges[it].first }
        private var ready: Int = 0 // 0: non-determined, 1: ready, 2: exhausted

//        private operator fun IntProgression.contains(x : Int) : Boolean  = this.first <= x && x <= this.last

        init {
            val nonEmpty = ranges.all { !it.isEmpty() }
            ready = if (nonEmpty) {
                1
            } else {
                2
            }
        }

        private fun prepareNext() {
            if (ready != 0) {
                return
            }
            for (i in indices.lastIndex downTo 0) {
                val range = ranges[i]
                if (indices[i] != range.last) {
                    indices[i] += range.step
                    ready = 1
                    return
                } else {
                    indices[i] = range.first
                }

            }
            ready = 2 // end
//            val range = ranges[0]
//            indices[0] ++
//            ready = indices[0] <= range.last
        }

        override fun hasNext(): Boolean {
            prepareNext()
            return ready == 1
//            return indices.any {   }
//            return indices[0] in ranges[0]
        }

        override fun next(): IntArray {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            ready = 0
            return indices
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
    ) : Iterator<IntArray> {
        private var ready: Int = 0 // 0: non-determined, 1: ready, 2: exhausted
        val n = ds.size

        init {
            require(ds.all { it >= 0 })
        }

        /*
        Constraints:
        a <= xs[0]
        xs[i] + ds[i] <= xs[i+1]
        xs[i] <= b
         */
        val xs: IntArray = IntArray(n)

        val dSum: IntArray = IntArray(n)

        init {
            xs[0] = a
            for (i in (n - 2) downTo 0) {
                dSum[i] = dSum[i + 1] + ds[i]
            }
            ready = if (placeAfter(0)) {
                1
            } else {
                2
            }
        }

        private fun placeAfter(start: Int): Boolean {
            if (dSum[start] + xs[start] > b) {
                return false
            }
            for (i in start..(n - 2)) {
                xs[i + 1] = xs[i] + ds[i]
            }
            return true
        }

        private fun prepareNext() {
            if (ready != 0) {
                return
            }
            xs[n - 1]++
            if (xs[n - 1] <= b) {
                ready = 1
                return
            }
            var i = n - 2
            while (i >= 0) {
                xs[i]++
                if (placeAfter(i)) {
                    ready = 1
                    return
                }
                i--
                continue
            }
            ready = 2
        }

        override fun hasNext(): Boolean {
            prepareNext()
            return ready == 1
        }

        override fun next(): IntArray {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            ready = 0
            return xs
        }
    }

//    fun sequenceBoundIdx(bounds: List<IntRange>, ds: List<IntRange>) : Sequence<IntArray> = sequence {
//        val xs = IntArray(ds.size)
//
//    }


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

}

//fun main() {
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
//}