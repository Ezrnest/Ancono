package cn.ancono.math.numberModels.structure

import cn.ancono.math.*
import cn.ancono.math.algebra.IMTerm
import cn.ancono.math.algebra.IMultinomial
import cn.ancono.math.algebra.IPolynomial
import cn.ancono.math.algebra.PolynomialUtil.subResultantGCD
import cn.ancono.math.algebra.abs.calculator.*
import cn.ancono.math.exceptions.ExceptionUtil
import cn.ancono.math.numberModels.*
import cn.ancono.math.numberModels.api.AlgebraModel
import cn.ancono.math.numberModels.api.NumberFormatter
import cn.ancono.math.numberModels.api.RealCalculator
import cn.ancono.utilities.CollectionSup
import cn.ancono.utilities.ModelPatterns
import java.util.*
import java.util.function.Function
import java.util.regex.Pattern

typealias CharMap = NavigableMap<String, Int>
typealias TermSet<T> = NavigableSet<TermF<T>>



/*
 * Created at 2018/12/12 18:49
 * @author  liyicheng
 */
data class TermF<F>(override val coefficient: F, override val characters: CharMap) : IMTerm<F> {

    constructor(coe: F) : this(coe, Collections.emptyNavigableMap())

//    override fun compareTo(other: TermF<F>): Int {
//        return LexicographicalComparator.compare(this, other)
//    }

    fun getCharPow(ch: String): Int {
        return characters.getOrDefault(ch, 0)
    }

    fun removeChar(ch: String): TermF<F> {
        val nc = TreeMap(characters)
        nc.remove(ch)
        return TermF(coefficient, nc)
    }

    fun multiplyChar(ch: String, pow: Int): TermF<F> {
        if (pow == 0) {
            return this
        }
        val nc = TreeMap(characters)
        nc.merge(ch, pow) { a, b ->
            val re = a + b
            if (re != 0) {
                re
            } else {
                null
            }
        }
        return TermF(coefficient, nc)
    }

    fun isConstant(): Boolean {
        return characters.isEmpty()
    }

    companion object {
        fun multiplyCharMap(m1: CharMap, m2: CharMap): CharMap {
            val nMap: CharMap = TreeMap(m1)
            for ((ch, p) in m2) {
                nMap.merge(ch, p) { p1, p2 ->
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

        fun <F> constant(c: F): TermF<F> {
            return TermF(c, TreeMap())
        }

        private val CHAR_PATTERN = Pattern.compile("((?<ch1>[a-zA-Z]_?\\d*)|(\\{(?<ch2>[^}]+)\\}))(\\^(?<pow>[+-]?\\d+))?")


        fun parseChar(chs: String): CharMap {
            val map = TreeMap<String, Int>()
            val matcher = CHAR_PATTERN.matcher(chs)
            while (matcher.lookingAt()) {
                val ch = matcher.group("ch1") ?: matcher.group("ch2")
                val pow = matcher.group("pow")?.toInt() ?: 1
                map.merge(ch, pow, Int::plus)
                matcher.region(matcher.end(), chs.length)
            }
            if (!matcher.hitEnd()) {
                throw IllegalArgumentException("Illegal format: $chs")
            }
            return map
        }

        /**
         * Parses the character part and combines it with the coefficient to build a term. The format of
         * character is:
         *
         *     character  ::= ch1 | ("{" ch2 "}")
         *     ch1        ::= [a-zA-Z]\w*
         *     ch2        ::= [^{]+
         *     power      ::= (+|-)? \d+
         *
         * `ch1` or `ch2` will be the characters stored in the resulting term.
         *
         */
        fun <F> parse(coe: F, chs: String): TermF<F> {
            return TermF(coe, parseChar(chs))
        }
    }

    object TermComparator : Comparator<TermF<*>> {
        override fun compare(o1: TermF<*>, o2: TermF<*>): Int {
            return CollectionSup.compareLexi(o1.characters, o2.characters)
        }
    }
}

/**
 * Describes the multinomial ring on field `F`.
 */
class MultinomialF<F>
internal constructor(
        mc: FieldCalculator<F>,
        /**
         * A navigable set, non-empty.
         */
        internal val ts: NavigableSet<TermF<F>>) :

        AbstractMathObject<F, FieldCalculator<F>>(mc),
        IMultinomial<F>,
        AlgebraModel<F, MultinomialF<F>> {

//    init {
//        require(ts.isNotEmpty())
//    }

    override val terms: NavigableSet<TermF<F>>
        get() = Collections.unmodifiableNavigableSet(ts)
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

    val characters: Set<String>
        get() = ts.flatMapTo(TreeSet()) {
            it.characters.keys
        }


    private fun fromTerms(ts: TermSet<F>): MultinomialF<F> = MultinomialF(calculator, ts)

    private fun getTS(): TermSet<F> {
        return TreeSet(ts.comparator())
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
        return calculator.isZero(this.coefficient)
    }

    private fun zeroTerm(): TermF<F> {
        return TermF(calculator.zero)
    }

    private fun zeroMul(): MultinomialF<F> {
        val set = singleTerm(zeroTerm())
        return fromTerms(set)
    }

    private fun oneMul(): MultinomialF<F> {
        val set = singleTerm(TermF(calculator.one))
        return fromTerms(set)
    }

    private operator fun TermF<F>.plus(tx: TermF<F>): TermF<F> {
        return TermF(calculator.add(coefficient, tx.coefficient), characters)
    }

    private operator fun TermF<F>.times(tx: TermF<F>): TermF<F> {
        if (this.isZero()) {
            return this
        }
        if (tx.isZero()) {
            return tx
        }
        return TermF(calculator.eval { coefficient * tx.coefficient }, TermF.multiplyCharMap(this.characters, tx.characters))
    }

    private operator fun TermF<F>.div(tx: TermF<F>): TermF<F> {
        if (this.isZero()) {
            return this
        }
        if (tx.isZero()) {
            ExceptionUtil.dividedByZero()
        }
        return TermF(calculator.eval { coefficient / tx.coefficient }, TermF.divideCharMap(this.characters, tx.characters))
    }

    private operator fun TermF<F>.unaryMinus(): TermF<F> {
        if (this.isZero()) {
            return this
        }
        return TermF(calculator.negate(coefficient), characters)
    }


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
        return fromTerms(mergeTwo(ts, y.ts))
    }

    override fun subtract(y: MultinomialF<F>): MultinomialF<F> {
        return fromTerms(mergeTwoWith(ts, y.ts) { -it })
    }


    private inline fun applyAll(f: (TermF<F>) -> TermF<F>): MultinomialF<F> {
        val re = getTS()
        for (t in ts) {
            re.add(f(t))
        }
        return fromTerms(re)
    }

    override fun negate(): MultinomialF<F> {
        return applyAll { -it }
    }

    override fun multiply(k: F): MultinomialF<F> {

        if (calculator.isZero(k)) {
            return zeroMul()
        }
        return applyAll { TermF(calculator.multiply(it.coefficient, k), it.characters) }
    }

    override fun divide(k: F): MultinomialF<F> {
        return multiply(calculator.reciprocal(k))
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
        return fromTerms(mergingMultiply(ts, y.ts))
    }

    override fun pow(n: Long): MultinomialF<F> {
        return ModelPatterns.binaryProduce(n, this, MultinomialF<F>::multiply)
    }

    override fun isZero(): Boolean {
        return ts.size == 1 && ts.first().isZero()
    }

    fun isConstant(): Boolean {
        return ts.size == 1 && ts.first().isConstant()
    }

    fun isOne(): Boolean {
        return ts.size == 1 && ts.first().run { characters.isEmpty() && coefficient == calculator.one }
    }

    fun isUnit(): Boolean {
        if (ts.size != 1) {
            return false
        }
        val t = ts.first()
        return t.isConstant() && calculator.isUnit(t.coefficient)
    }

    fun divideAndRemainder(y: MultinomialF<F>): Pair<MultinomialF<F>, MultinomialF<F>> {
        val m = getTS(ts)
        val q = singleTerm(zeroTerm())
        multinomialDivision(m, y.ts, q)
        return fromTerms(q) to fromTerms(m)
    }

    fun exactDivide(y: MultinomialF<F>): MultinomialF<F> {
        val m = getTS(ts)
        val q = singleTerm(zeroTerm())
        multinomialDivision(m, y.ts, q)
        if (m.isNotEmpty()) {
            ExceptionUtil.notExactDivision(this, y)
        }
        return fromTerms(q)
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

            if (TermF.TermComparator.compare(head, divisorHead) > 0) {
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

//    override fun gcdUV(y: MultinomialF<F>): Triple<MultinomialF<F>, MultinomialF<F>, MultinomialF<F>> {
//        return CalculatorUtils.gcdUV(this, y, zeroMul(), oneMul())
//    }


//    override fun isCoprime(y: MultinomialF<F>): Boolean {
//        return gcd(y).isOne()
//    }


    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<F, N>): MultinomialF<N> {
        return MultinomialF(
                newCalculator as FieldCalculator,
                ts.mapTo(getDefaultTermsSet()) { TermF(mapper.apply(it.coefficient), it.characters) })
    }

    override fun valueEquals(obj: IMathObject<F>): Boolean {
        if (obj !is MultinomialF) {
            return false
        }
        if (ts.size != obj.ts.size) {
            return false
        }
        if (ts.comparator() != obj.ts.comparator()) {
            return false
        }
        val it1 = ts.iterator()
        val it2 = obj.ts.iterator()
        while (it1.hasNext()) {
            val t1 = it1.next()
            val t2 = it2.next()
            if (t1.characters != t2.characters) {
                return false
            }
            if (!calculator.isEqual(t1.coefficient, t2.coefficient)) {
                return false
            }
        }
        return true
    }

    override fun toString(nf: NumberFormatter<F>): String {
        return IMultinomial.stringOf(this, nf)
    }

    override val size: Int
        get() = ts.size

    override fun getCoefficient(characters: Map<String, Int>): F? {
        return ts.find { it.characters == characters }?.coefficient
    }


    companion object {
        internal fun <F> getDefaultTermsSet(): NavigableSet<TermF<F>> {
            return TreeSet(TermF.TermComparator)
        }


        fun <F> monomial(t: TermF<F>, mc: FieldCalculator<F>): MultinomialF<F> {
            val s = getDefaultTermsSet<F>()
            s.add(t)
            return MultinomialF(mc, s)
        }

        fun <F> one(mc: FieldCalculator<F>): MultinomialF<F> {
            return monomial(TermF(mc.one), mc)
        }

        fun <F> zero(mc: FieldCalculator<F>): MultinomialF<F> {
            return monomial(TermF(mc.zero), mc)
        }


        fun <F> of(mc: FieldCalculator<F>, terms: List<TermF<F>>): MultinomialF<F> {
            val set = getDefaultTermsSet<F>()
            val re = MultinomialF(mc, set)
            re.mergingAddAll(set, terms)
            if (set.isEmpty()) {
                return zero(mc)
            }
            return re
        }

        fun <F> of(mc: FieldCalculator<F>, vararg terms: TermF<F>): MultinomialF<F> {
            return of(mc, terms.asList())
        }

        /**
         * Returns a multinomial built from the given coefficients and character strings.
         * The format
         */
        fun <F> of(mc: FieldCalculator<F>, vararg terms: Pair<F, String>): MultinomialF<F> {
            return of(mc, terms.map { (c, s) -> TermF.parse(c, s) })
        }

        private val PLUS_AND_MINUS = "+-".toCharArray()

        /**
         * Parse an expression of multinomial. The expression can be described as
         *
         *     expression ::= monomial (+|- monomial)*
         *     monomial   ::= coefficient ("*" characters)?
         *     characters ::= character ("^" power)? characters?
         *     character  ::= ch1 | ("{" ch2 "}")
         *     ch1        ::= [a-zA-Z]\w*
         *     ch2        ::= [^{]+
         *     power      ::= (+|-)? \d+
         *
         *
         *
         * The following are some samples for the situation when parser is `Integer::parseInt`:
         *
         *     ab^2c{pi}^-2z
         *     1*ab-2*cd{pi}^2z
         */
        fun <F> parse(expr: String, mc: FieldCalculator<F>, parser: (String) -> F): MultinomialF<F> {
            var idx = 0

            val terms = arrayListOf<TermF<F>>()
            while (idx < expr.length) {
                var start = idx
                while (start < expr.length) {
                    val c = expr[start]
                    if (c != '+' && c != '-') {
                        break
                    }
                    start++
                }
//                for(i in )
                var end = expr.indexOfAny(PLUS_AND_MINUS, start)
                if (end == -1) {
                    end = expr.length
                }
                val part = expr.substring(idx, end).trim()

                val idxOfMul = part.lastIndexOf('*')
                if (idxOfMul == -1) {
                    terms.add(TermF.constant(ParserUtils.parseCoefficient(part, parser, mc)))
                } else {
                    val strCoe = part.substring(0, idxOfMul)
                    val coe = ParserUtils.parseCoefficient(strCoe, parser, mc)
                    val chs = part.substring(idxOfMul + 1)
                    terms.add(TermF.parse(coe, chs))
                }


                idx = end
            }
            return of(mc, terms)
        }

//        fun <F> gcd()

        fun <F> asPolynomial(m: MultinomialF<F>, ch: String, mmc: MultinomialFCalculator<F>): Polynomial<MultinomialF<F>> {
            val mc = m.calculator
            var deg = 0
            for (f in m.ts) {
                val pow: Int = f.getCharPow(ch)
                if (pow < 0) {
                    throw ArithmeticException("Unsupported exponent for:[$ch] in $f")
                }
                if (pow > deg) {
                    deg = pow
                }
            }

            val arr = ArrayList(Collections.nCopies(deg + 1, mmc.zero))
            for (f in m.ts) {
                val pow = f.getCharPow(ch)
                val coe = monomial(f.removeChar(ch), mc)
                arr[pow] = arr[pow].add(coe)
            }
            return Polynomial.of(mmc, arr)
        }

        fun <F> fromPolynomialM(p: IPolynomial<MultinomialF<F>>, ch: String): MultinomialF<F> {
            val mc = p.constant().calculator
            val dummy = zero(mc)
            val result = dummy.ts
            for (i in 0..p.leadingPower) {
                val coe = p.get(i)
                if (coe.isZero()) {
                    continue
                }
                dummy.mergingAddAllWith(result, coe.ts) { it.multiplyChar(ch, i) }
            }
            return MultinomialF(mc, result)
        }
    }

}

class MultinomialFCalculator<T>(val mc: RealCalculator<T>)
    : UFDCalculator<MultinomialF<T>>, OrderPredicate<MultinomialF<T>> {
    override val one: MultinomialF<T> = MultinomialF.one(mc)
    override val zero: MultinomialF<T> = MultinomialF.zero(mc)


    override fun isZero(x: MultinomialF<T>): Boolean {
        return x.isZero()
    }

    override fun isEqual(x: MultinomialF<T>, y: MultinomialF<T>): Boolean {
        return x.valueEquals(y)
    }

    override fun isUnit(x: MultinomialF<T>): Boolean {
        return x.isUnit()
    }

    override fun compare(x: MultinomialF<T>, y: MultinomialF<T>): Int {
        return x.compareTo(y)
    }

    override fun add(x: MultinomialF<T>, y: MultinomialF<T>): MultinomialF<T> {
        return x.add(y)
    }

    override fun negate(x: MultinomialF<T>): MultinomialF<T> {
        return x.negate()
    }

    override fun subtract(x: MultinomialF<T>, y: MultinomialF<T>): MultinomialF<T> {
        return x.subtract(y)
    }

    override fun multiply(x: MultinomialF<T>, y: MultinomialF<T>): MultinomialF<T> {
        return x.multiply(y)
    }

//    override fun divide(x: MultinomialF<T>, y: MultinomialF<T>): MultinomialF<T> {
//        val pair = x.divideAndRemainder(y)
//        if (pair.second.isZero()) {
//            return pair.first
//        }
//        throw UnsupportedOperationException()
//    }

    override fun multiplyLong(x: MultinomialF<T>, n: Long): MultinomialF<T> {
        return x.multiply(CalculatorUtils.valueOfLong(n, mc))
    }

//    override fun divideLong(x: MultinomialF<T>, n: Long): MultinomialF<T> {
//        return x.multiply(mc.reciprocal(CalculatorUtils.valueOfLong(n, mc)))
//    }


    private fun gcd0(a: MultinomialF<T>, b: MultinomialF<T>): MultinomialF<T> {
        // consider m1 and m2 as polynomial on fraction ring of multinomial
        // see the similar method in Multinomial
        val ch: String = a.ts.asSequence().flatMap { it.characters.keys }.first()
        val p1 = MultinomialF.asPolynomial(a, ch, this) // Polynomial<Multinomial>

        val p2 = MultinomialF.asPolynomial(b, ch, this)
//
        val gcd = subResultantGCD(p1, p2)
        return MultinomialF.fromPolynomialM(gcd, ch)
    }

    override fun gcd(a: MultinomialF<T>, b: MultinomialF<T>): MultinomialF<T> {
        //Created by lyc at 2021-04-02 17:4
        // see the corresponding method in Multinomial
        if (a.isZero()) {
            return b
        }
        if (b.isZero()) {
            return a
        }
        if (a.isConstant() || b.isConstant()) {
            return MultinomialF.one(a.calculator)
        }
        return gcd0(a, b)
    }

    override fun exactDivide(x: MultinomialF<T>, y: MultinomialF<T>): MultinomialF<T> {
        return x.exactDivide(y)
    }

    override fun isExactDivide(a: MultinomialF<T>, b: MultinomialF<T>): Boolean {
        return a.divideAndRemainder(b).second.isZero()
    }

    override fun pow(x: MultinomialF<T>, n: Long): MultinomialF<T> {
        return x.pow(n)
    }

    override fun of(n: Long): MultinomialF<T> {
        return MultinomialF.monomial(TermF.constant(mc.of(n)), mc)

    }

    fun of(x: Fraction): MultinomialF<T> {
        return MultinomialF.monomial(TermF.constant(mc.of(x)), mc)
    }
}

//fun main() {
////    val map = TermF.parseChar("ab^2{pi}^3")
////    println(map)
//}