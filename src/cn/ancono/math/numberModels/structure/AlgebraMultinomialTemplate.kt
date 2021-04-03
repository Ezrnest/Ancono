package cn.ancono.math.numberModels.structure

import cn.ancono.math.algebra.abs.calculator.AlgebraCalculator
import cn.ancono.math.calculus.DifferentialForm
import cn.ancono.math.numberModels.api.AlgebraModel
import cn.ancono.math.numberModels.expression.Expression
import java.util.*


/**
 * Template class for general algebra multinomial
 */
open class AlgebraMultinomialTemplate<K : Any, V : Any> internal constructor(
        val ac: AlgebraCalculator<K, V>,
        val terms: NavigableSet<V>
) {

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

    /**
     * Override this method to create instances
     */
    protected open fun fromTerms(ts: NavigableSet<V>): AlgebraMultinomialTemplate<K, V> = AlgebraMultinomialTemplate(ac, ts)

    //    protected fun addTerms(ts1: NavigableSet<V>,ts2: NavigableSet<V>) : NavigableSet<V>{
//        return mergeTwo(ts1, ts2)
//    }
//
    protected fun subtractTerms(ts1: NavigableSet<V>, ts2: NavigableSet<V>): NavigableSet<V> {
        return mergeTwo(ts1, ts2)
    }

    protected inline fun applyAllTerms(f: (V) -> V): NavigableSet<V> {
        val re = getTS()
        for (t in terms) {
            re.add(f(t))
        }
        return re
    }

    protected fun negateTerms(): NavigableSet<V> {
        return applyAllTerms { ac.negate(it) }
    }


    protected fun singleTerm(t: V): NavigableSet<V> {
        val ts = getTS()
        ts.add(t)
        return ts
    }

//    protected fun zeroMul(): AlgebraMultinomialTemplate<K, V> {
//        val set = singleTerm(ac.zero)
//        return fromTerms(set)
//    }

    protected fun multiplyTerms(k: K): NavigableSet<V> {
        if (ac.scalarCalculator.run { isEqual(k, zero) }) {
            return singleTerm(ac.zero)
        }
        return applyAllTerms { ac.scalarMultiply(k, it) }
    }

    /**
     * The result set must not be modified.
     */
    protected fun mergingMultiply(s1: NavigableSet<V>, s2: NavigableSet<V>): NavigableSet<V> {
        val set = getTS()
        for (x in s1) {
            for (y in s2) {
                mergingAdd(set, ac.multiply(x, y))
            }
        }
        return set
    }


//    override fun multiply(y: AlgebraMultinomialTemplate<K, V>): AlgebraMultinomialTemplate<K, V> {
//        return fromTerms(mergingMultiply(terms, y.terms))
//    }

    protected open fun isZero(): Boolean {
        return terms.size == 1 && ac.isEqual(terms.first(), ac.zero)
    }

    override fun toString(): String {
        return terms.joinToString("+")
    }
}


/**
 * Describes a multinomial composed with linear independent elements of an algebra `V` on `K`.
 *
 * Created at 2019/9/12 15:51
 * @author  lyc
 */
class AlgebraMultinomial<K : Any, V : Any> internal constructor(
        ac: AlgebraCalculator<K, V>,
        terms: NavigableSet<V>
) : AlgebraMultinomialTemplate<K, V>(ac, terms), AlgebraModel<K, AlgebraMultinomial<K, V>> {
    override fun fromTerms(ts: NavigableSet<V>): AlgebraMultinomial<K, V> {
        return AlgebraMultinomial(ac, ts)
    }

    override fun add(y: AlgebraMultinomial<K, V>): AlgebraMultinomial<K, V> {
        return fromTerms(mergeTwo(terms, y.terms))
    }

    override fun negate(): AlgebraMultinomial<K, V> {
        return fromTerms(applyAllTerms { ac.negate(it) })
    }

    override fun subtract(y: AlgebraMultinomial<K, V>): AlgebraMultinomial<K, V> {
        return fromTerms(subtractTerms(terms, y.terms))
    }

    override fun isZero(): Boolean {
        return super.isZero()
    }

    override fun multiply(k: K): AlgebraMultinomial<K, V> {
        return fromTerms(multiplyTerms(k))
    }

    override fun multiply(y: AlgebraMultinomial<K, V>): AlgebraMultinomial<K, V> {
        return fromTerms(mergingMultiply(terms, y.terms))
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