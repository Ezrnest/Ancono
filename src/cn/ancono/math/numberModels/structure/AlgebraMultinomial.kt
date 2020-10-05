package cn.ancono.math.numberModels.structure

import cn.ancono.math.algebra.abs.calculator.AlgebraCalculator
import cn.ancono.math.numberModels.api.AlgebraModel
import java.util.*

/**
 * Describes a multinomial composed with linear independent elements of an algebra `V` on `K`.
 * Created at 2019/9/12 15:51
 * @author  lyc
 */
open class AlgebraMultinomial<K : Any, V : Any> internal constructor(
        val ac: AlgebraCalculator<K, V>,
        val terms: NavigableSet<V>
) : AlgebraModel<K, AlgebraMultinomial<K, V>> {


    protected fun canMerge(u: V, v: V): Boolean = ac.isLinearDependent(u, v)

    protected fun getTS(): NavigableSet<V> = TreeSet(terms.comparator())

    protected fun getTS(s1: NavigableSet<V>): NavigableSet<V> {
        return TreeSet(s1)
    }


    protected fun mergingAdd(base: NavigableSet<V>, e: V): Boolean {
        val low = base.floor(e)
        if (low != null && canMerge(low, e)) {
            base.remove(low)
            return mergingAdd(base, ac.add(low, e))
        }
        val high = base.higher(e)
        if (high != null && canMerge(high, e)) {
            base.remove(high)
            return mergingAdd(base, ac.add(high, e))
        }
        return base.add(e)
    }

    protected fun mergingAddAll(base: NavigableSet<V>, toAdd: Iterable<V>) {
        for (t in toAdd) {
            mergingAdd(base, t)
        }
    }

    protected inline fun mergingAddAllWith(base: NavigableSet<V>, toAdd: NavigableSet<V>, trans: (V) -> V) {
        for (t in toAdd) {
            mergingAdd(base, trans(t))
        }
    }

    protected fun mergeTwo(s1: NavigableSet<V>, s2: NavigableSet<V>): NavigableSet<V> {
        return if (s1.size > s2.size) {
            val re = getTS(s1)
            mergingAddAll(re, s2)
            re
        } else {
            val re = getTS(s2)
            mergingAddAll(re, s1)
            re
        }
    }


    protected inline fun mergeTwoWith(s1: NavigableSet<V>, s2: NavigableSet<V>, trans: (V) -> V): NavigableSet<V> {
        return if (s1.size > s2.size) {
            val re = getTS(s1)
            mergingAddAllWith(re, s2, trans)
            re
        } else {
            val re = getTS(s2)
            mergingAddAllWith(re, s1, trans)
            re
        }
    }

    private fun fromTerms(ts: NavigableSet<V>): AlgebraMultinomial<K, V> = AlgebraMultinomial(ac, ts)

    override fun add(y: AlgebraMultinomial<K, V>): AlgebraMultinomial<K, V> {
        return fromTerms(mergeTwo(terms, y.terms))
    }

    override fun subtract(y: AlgebraMultinomial<K, V>): AlgebraMultinomial<K, V> {
        return fromTerms(mergeTwoWith(terms, y.terms) { ac.negate(it) })
    }


    private inline fun applyAll(f: (V) -> V): AlgebraMultinomial<K, V> {
        val re = getTS()
        for (t in terms) {
            re.add(f(t))
        }
        return fromTerms(re)
    }

    override fun negate(): AlgebraMultinomial<K, V> {
        return applyAll { ac.negate(it) }
    }


    private fun singleTerm(t: V): NavigableSet<V> {
        val ts = getTS()
        ts.add(t)
        return ts
    }

    private fun zeroMul(): AlgebraMultinomial<K, V> {
        val set = singleTerm(ac.zero)
        return fromTerms(set)
    }

    override fun multiply(k: K): AlgebraMultinomial<K, V> {
        if (ac.scalarCalculator.run { isEqual(k, zero) }) {
            return zeroMul()
        }
        return applyAll { ac.scalarMultiply(k, it) }
    }

    /**
     * The result set must not be modified.
     */
    private fun mergingMultiply(s1: NavigableSet<V>, s2: NavigableSet<V>): NavigableSet<V> {
        val set = getTS()
        for (x in s1) {
            for (y in s2) {
                mergingAdd(set, ac.multiply(x, y))
            }
        }
        return set
    }


    override fun multiply(y: AlgebraMultinomial<K, V>): AlgebraMultinomial<K, V> {
        return fromTerms(mergingMultiply(terms, y.terms))
    }

    override fun isZero(): Boolean {
        return terms.size == 1 && ac.isEqual(terms.first(), ac.zero)
    }

    override fun toString(): String {
        return terms.joinToString("+")
    }

    companion object {


        private fun <V : Any> getTS(comp: Comparator<V>): NavigableSet<V> {
            return TreeSet(comp)
        }

        @JvmStatic
        fun <K : Any, V : Any> fromTerms(ac: AlgebraCalculator<K, V>, comp: Comparator<V>, vararg terms: V)
                : AlgebraMultinomial<K, V> {
            val s = getTS(comp)
            val am = AlgebraMultinomial(ac, s)
            am.mergingAddAll(s, terms.asIterable())
            return am
        }

        @JvmStatic
        fun <K : Any, V : Any> fromTerms(ac: AlgebraCalculator<K, V>, comp: Comparator<V>, terms: List<V>)
                : AlgebraMultinomial<K, V> {
            val s = getTS(comp)
            val am = AlgebraMultinomial(ac, s)
            am.mergingAddAll(s, terms)
            return am
        }

        @JvmStatic
        fun <K : Any, V : Any> zero(ac: AlgebraCalculator<K, V>, comp: Comparator<V>)
                : AlgebraMultinomial<K, V> {
            val s = getTS(comp)
            s.add(ac.zero)
            return AlgebraMultinomial(ac, s)
        }

    }
}