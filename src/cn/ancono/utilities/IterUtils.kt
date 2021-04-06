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

    class ProductIterator<T>(iterables: List<Iterable<T>>) : ProductIteratorNoCopy<T>(iterables) {
        override fun next(): List<T> {
            return ArrayList(super.next())
        }
    }

    /**
     * Returns a sequence corresponding to the cartesian product of the given iterables.
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
        return if (copy) {
            Sequence { ProductIterator(its) }
        } else {
            Sequence { ProductIteratorNoCopy(its) }
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

    class IndexIterator(ranges: List<IntProgression>) : IndexIteratorNoCopy(ranges) {
        override fun next(): IntArray {
            return super.next().copyOf()
        }
    }

    /**
     * Returns a sequence corresponding to the cartesian product of the given int progressions.
     *
     *
     * @param copy whether to directly return the backing int array in the sequence iterator or make a
     * copy of it. If `copy = false`, the resulting sequence is read-only-traversable.
     * That is, each element in the sequence should only be used before subsequent invocations of `next()` and `hasNext()`.
     * Setting it to `false` can provide better performance.
     */
    fun prodIdx(ranges: List<IntProgression>, copy: Boolean = true): Sequence<IntArray> {
        return if (ranges.isEmpty()) {
            emptySequence()
        } else {
            if (copy) {
                Sequence { IndexIterator(ranges) }
            } else {
                Sequence { IndexIteratorNoCopy(ranges) }
            }
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
}