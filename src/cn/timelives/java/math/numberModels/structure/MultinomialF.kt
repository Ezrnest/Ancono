package cn.timelives.java.math.numberModels.structure

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.MathObject
import cn.timelives.java.math.MathObjectExtend
import cn.timelives.java.math.algebra.IMTerm
import cn.timelives.java.math.algebra.IMultinomial
import cn.timelives.java.math.exceptions.ExceptionUtil
import cn.timelives.java.math.numberModels.CalculatorUtils
import cn.timelives.java.math.numberModels.api.AlgebraModel
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter
import cn.timelives.java.math.numberTheory.EuclidRingNumberModel
import cn.timelives.java.utilities.CollectionSup
import java.util.*
import java.util.function.Function
import kotlin.Comparator

typealias CharMap = NavigableMap<String, Int>
typealias TermSet<T> = NavigableSet<TermF<T>>

object LexicographicalComparator : Comparator<TermF<*>> {
    override fun compare(o1: TermF<*>, o2: TermF<*>): Int {
        return CollectionSup.compareLexi(o1.characters, o2.characters)
    }

}

/*
 * Created at 2018/12/12 18:49
 * @author  liyicheng
 */
data class TermF<F : Any>
(override val characters: CharMap, override val coefficient: F) : IMTerm<F>, Comparable<TermF<F>> {

    constructor(coe: F) : this(Collections.emptyNavigableMap(), coe)

    override fun compareTo(other: TermF<F>): Int {
        return LexicographicalComparator.compare(this, other)
    }

    companion object {
        fun multiplyCharMap(m1: CharMap, m2: CharMap): CharMap {
            val nMap: CharMap = TreeMap(m1)
            for ((ch, p) in m2) {
                nMap.merge(ch, p) { p1,p2 ->
                    val re = p1 + p2
                    if (re == 0) {
                        //remove if zero
                        null
                    } else {
                        re
                    }
                }
            }
            return nMap
        }

//        inline fun combineCharMap(m1 : CharMap, m2 : CharMap, f : (Int,Int)->Int) : Ca

        fun divideCharMap(m1: CharMap, m2: CharMap): CharMap {
            val nMap: CharMap = TreeMap(m1)
            for ((ch, p) in m2) {
                nMap.compute(ch) { _, u ->
                    if (u == null) {
                        -p
                    } else {
                        val re = u - p
                        if (re == 0) {
                            //remove if zero
                            null
                        } else {
                            re
                        }
                    }
                }
            }
            return nMap
        }

    }
}

class MultinomialF<F : Any>
internal constructor(
        mc: MathCalculator<F>,
        /**
         * A navigable set, non-empty.
         */
        val terms: NavigableSet<TermF<F>>) :

        MathObjectExtend<F>(mc),
        IMultinomial<F>,
        AlgebraModel<F, MultinomialF<F>>,
        EuclidRingNumberModel<MultinomialF<F>> {

    init {
        require(terms.isNotEmpty())
    }

    //    internal val terms : TermSet<T>
//    init{
//        if(terms.isNotEmpty()){
//            val c = terms.first()
//            if(mc.isZero(c.coefficient)){
//                terms.clear()
//            }
//        }
//        this.terms = terms
//    }
    private fun fromTerms(ts: TermSet<F>): MultinomialF<F> = MultinomialF(mc, ts)

    private fun getTS(): TermSet<F> {
        return TreeSet(terms.comparator())
    }

    private fun getTS(set: TermSet<F>): TermSet<F> {
        return TreeSet(set)
    }

    private fun singleTerm(t: TermF<F>): TermSet<F> {
        val ts = getTS()
        ts.add(t)
        return ts
    }

    private fun TermF<F>.canMerge(tx: TermF<F>): Boolean {
        if (this.isZero() || tx.isZero()) {
            return true
        }
        return this.characters == tx.characters
    }

    private fun TermF<F>.isZero(): Boolean {
        return mc.isZero(this.coefficient)
    }

    private fun zeroTerm(): TermF<F> {
        return TermF(mc.zero)
    }

    private fun zeroMul(): MultinomialF<F> {
        val set = singleTerm(zeroTerm())
        return fromTerms(set)
    }

    private fun oneMul(): MultinomialF<F> {
        val set = singleTerm(TermF(mc.one))
        return fromTerms(set)
    }

    private operator fun TermF<F>.plus(tx: TermF<F>): TermF<F> {
        return TermF(characters, coefficient + tx.coefficient)
    }

    private operator fun TermF<F>.times(tx: TermF<F>): TermF<F> {
        if (this.isZero()) {
            return this
        }
        if (tx.isZero()) {
            return tx
        }
        return TermF(TermF.multiplyCharMap(this.characters, tx.characters), coefficient * tx.coefficient)
    }

    private operator fun TermF<F>.div(tx: TermF<F>): TermF<F> {
        if (this.isZero()) {
            return this
        }
        if (tx.isZero()) {
            ExceptionUtil.divideByZero()
        }
        return TermF(TermF.divideCharMap(this.characters, tx.characters), coefficient / tx.coefficient)
    }

    private operator fun TermF<F>.unaryMinus(): TermF<F> {
        if (this.isZero()) {
            return this
        }
        return TermF(characters, -coefficient)
    }

//    private fun mergeTwo(s1: TermSet<T>, s2: TermSet<T>) : TermSet<T> {
//        val it1 = s1.iterator()
//        val it2 = s2.iterator()
//        val re = getTS()
////        while(it1.hasNext() && it2.hasNext()){
////
////        }
//        var t1 = it1.next()
//        var t2 = it2.next()
//        while (true) {
//            var update1 = true
//            var update2 = true
//            when (t1.compareTo(t2)) {
//                -1 -> {
//                    re.add(t1)
//                    update2 = false
//                }
//                0 -> {
//                    re.add(t1 + t2)
//                }
//                1 -> {
//                    re.add(t2)
//                    update1 = false
//                }
//            }
//            if(update1){
//                if(!it1.hasNext()){
//                    break
//                }
//                t1 = it1.next()
//            }
//            if(update2){
//                if(!it2.hasNext()){
//                    break
//                }
//                t2 = it2.next()
//            }
//        }
//
//        while(it1.hasNext()){
//            re.add(it1.next())
//        }
//        while(it2.hasNext()){
//            re.add(it2.next())
//        }
//        return re
//    }


    private fun mergingAdd(base: TermSet<F>, e: TermF<F>): Boolean {
        val low = base.floor(e)
        if (low != null && low.canMerge(e)) {
            base.remove(low)
            return mergingAdd(base, low + e)
        }
        val high = base.higher(e)
        if (high != null && high.canMerge(e)) {
            base.remove(high)
            return mergingAdd(base, high + e)
        }
        return base.add(e)
    }

    private fun mergingAddAll(base: TermSet<F>, toAdd: Iterable<TermF<F>>) {
        for (t in toAdd) {
            mergingAdd(base, t)
        }
    }

    private inline fun mergingAddAllWith(base: TermSet<F>, toAdd: TermSet<F>, trans: (TermF<F>) -> TermF<F>) {
        for (t in toAdd) {
            mergingAdd(base, trans(t))
        }
    }

    private fun mergeTwo(s1: TermSet<F>, s2: TermSet<F>): TermSet<F> {
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

    private inline fun mergeTwoWith(s1: TermSet<F>, s2: TermSet<F>, trans: (TermF<F>) -> TermF<F>): TermSet<F> {
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


    override fun add(y: MultinomialF<F>): MultinomialF<F> {
        return fromTerms(mergeTwo(terms, y.terms))
    }

    override fun subtract(y: MultinomialF<F>): MultinomialF<F> {
        return fromTerms(mergeTwoWith(terms, y.terms) { -it })
    }


    private inline fun applyAll(f: (TermF<F>) -> TermF<F>): MultinomialF<F> {
        val re = getTS()
        for (t in terms) {
            re.add(f(t))
        }
        return fromTerms(re)
    }

    override fun negate(): MultinomialF<F> {
        return applyAll { -it }
    }

    override fun multiply(k: F): MultinomialF<F> {
        if (mc.isZero(k)) {
            return zeroMul()
        }
        return applyAll { TermF(it.characters, it.coefficient * k) }
    }

    /**
     * The result set must not be modified.
     *
     * @param s1
     * @param s2
     * @return
     */
    private fun mergingMultiply(s1: TermSet<F>, s2: TermSet<F>): TermSet<F> {
//        if()
        val set = getTS()
        for (x in s1) {
            for (y in s2) {
                mergingAdd(set, x * y)
            }
        }
        return set
    }

    internal fun multiplyToSet(set: TermSet<F>, t: TermF<F>): TermSet<F> {
        val nset = getTS()
        if (t.isZero()) {
            return nset
        }
        for (x in set) {
            val re = x * t
            nset.add(re)
        }
        return nset
    }


    override fun multiply(y: MultinomialF<F>): MultinomialF<F> {
        return fromTerms(mergingMultiply(terms, y.terms))
    }

    override fun isZero(): Boolean {
        return terms.size == 1 && terms.first().isZero()
    }

    fun isConstant(): Boolean {
        return terms.size == 1 && terms.first().characters.isEmpty()
    }

    fun isOne(): Boolean {
        return terms.size == 1 && terms.first().run { characters.isEmpty() && coefficient == mc.one }
    }

    override fun divideAndRemainder(y: MultinomialF<F>): Pair<MultinomialF<F>, MultinomialF<F>> {
        val m = getTS(terms)
        val q = singleTerm(zeroTerm())
        multinomialDivision(m, y.terms, q)
        return fromTerms(q) to fromTerms(m)
    }


    private fun getCharacters(ts: TermSet<F>): NavigableSet<String> {
        val set = TreeSet<String>()
        for (t in ts) {
            set.addAll(t.characters.keys)
        }
        return set
    }

    /**
     * @param m        modified to the remainder
     * @param divisor  remains the identity
     * @param quotient added
     */
    private fun multinomialDivision(m: TermSet<F>, divisor: TermSet<F>, quotient: TermSet<F>) {
        //multinomial division

        var remainChars: Set<String> = getCharacters(m)
        val divisorHead = divisor.first()
        val divisorChars = getCharacters(divisor)
//        val extraRemainders = ArrayList<TermX<T>>(2)

        //while(true){
        while (remainChars.containsAll(divisorChars)) {
            val head = m.first()

            if (head > divisorHead) {
                //can't divide
                break
            }
            val q = head / divisorHead
            //            if(q.containNegativePower()){
            //                extraRemainders.add(head);
            //                m.pollFirst();
            //                if(m.isEmpty()){
            //                    break;
            //                }
            //                continue;
            //            }
            mergingAdd(quotient, q)
            mergingAddAll(m, multiplyToSet(divisor, -q))
            remainChars = getCharacters(m)
        }
//        mergingAddAll(m, extraRemainders)
    }


    override fun gcdUV(y: MultinomialF<F>): Triple<MultinomialF<F>, MultinomialF<F>, MultinomialF<F>> {
        return CalculatorUtils.gcdUV(this, y, zeroMul(), oneMul())
    }


    override fun isCoprime(y: MultinomialF<F>): Boolean {
        return gcd(y).isOne()
    }

    override fun <N : Any> mapTo(mapper: Function<F, N>, newCalculator: MathCalculator<N>): MultinomialF<N> {
        return MultinomialF(newCalculator, terms.mapTo(getsDefaultTermsSet()) { TermF(it.characters, mapper.apply(it.coefficient)) })
    }

    override fun valueEquals(obj: MathObject<F>): Boolean {
        if (obj !is MultinomialF) {
            return false
        }
        if (terms.size != obj.terms.size) {
            return false
        }
        if (terms.comparator() != obj.terms.comparator()) {
            return false
        }
        val it1 = terms.iterator()
        val it2 = obj.terms.iterator()
        while (it1.hasNext()) {
            val t1 = it1.next()
            val t2 = it2.next()
            if (t1.characters != t2.characters) {
                return false
            }
            if (!mc.isEqual(t1.coefficient, t2.coefficient)) {
                return false
            }
        }
        return true
    }

    override fun toString(nf: FlexibleNumberFormatter<F, MathCalculator<F>>): String {
        return IMultinomial.stringOf(this, mc, nf)
    }

    override val size: Int
        get() = terms.size

    override fun getCoefficient(characters: Map<String, Int>): F? {
        return terms.find { it.characters == characters }?.coefficient
    }

    override fun iterator(): Iterator<IMTerm<F>> {
        return terms.iterator()
    }


    companion object {
        internal fun <T : Any> getsDefaultTermsSet(): NavigableSet<TermF<T>> {
            return TreeSet(LexicographicalComparator)
        }

    }
}