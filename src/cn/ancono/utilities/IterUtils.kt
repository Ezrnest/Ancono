package cn.ancono.utilities


/**
 * Contains some utilities for iterators and sequences.
 */
object IterUtils {
    class ProductIterator<T>(val iterables: List<Iterable<T>>) : Iterator<List<T>> {
        val iterators = Array(iterables.size) { i ->
            iterables[i].iterator()
        }
        var elements: MutableList<T>? = if (iterators.all { it.hasNext() }) {
            iterators.mapTo(ArrayList(iterators.size)) { it.next() }
        } else {
            null
        }


        override fun hasNext(): Boolean {
            return elements != null
        }

        override fun next(): List<T> {
            val t = elements ?: throw NoSuchElementException()
            val re = ArrayList(t)
            for (i in iterators.lastIndex downTo 1) {
                val it = iterators[i]
                if (it.hasNext()) {
                    t[i] = it.next()
                    return re
                } else {
                    iterators[i] = iterables[i].iterator()
                    t[i] = iterators[i].next()
                }
            }
            if (iterators[0].hasNext()) {
                t[0] = iterators[0].next()
            } else {
                elements = null
            }
            return re
        }

    }

    /**
     * Returns a sequence corresponding to the cartesian product of the given iterables.
     */
    fun <T> prod(its: List<Iterable<T>>): Sequence<List<T>> {
        if (its.isEmpty()) {
            return emptySequence()
        }
        return Sequence { ProductIterator(its) }
    }

    /**
     * Returns a sequence corresponding to the cartesian product of the given iterables.
     *
     * @see prod
     */
    fun <T> prod(vararg its: Iterable<T>): Sequence<List<T>> {
        return prod(its.asList())
    }

    class IndexIterator(val ranges: List<IntProgression>) : Iterator<IntArray> {
        val indices = IntArray(ranges.size) { ranges[it].first }

        override fun hasNext(): Boolean {
            return indices[0] in ranges[0]
        }

        override fun next(): IntArray {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            val t = indices.copyOf()
            for (i in indices.lastIndex downTo 1) {
                val range = ranges[i]
                indices[i] += range.step
                if (indices[i] in range) {
                    return t
                } else {
                    indices[i] = range.first
                }
            }
            val range = ranges[0]
            indices[0] += range.step
            return t
        }
    }

    /**
     * Returns a sequence corresponding to the cartesian product of the given int progressions.
     *
     */
    fun prodIdx(ranges: List<IntProgression>): Sequence<IntArray> {
        return if (ranges.isEmpty()) {
            emptySequence()
        } else {
            Sequence { IndexIterator(ranges) }
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
     * Returns the cartesian product of `[0,bounds[i])`.
     */
    fun prodIdx(bounds: IntArray): Sequence<IntArray> {
        return prodIdx(bounds.map { b -> 0 until b })
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