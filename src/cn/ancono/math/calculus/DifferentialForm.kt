package cn.ancono.math.calculus

import cn.ancono.math.algebra.abs.calculator.AlgebraCalculator
import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.algebra.abs.calculator.UnitRingCalculator
import cn.ancono.math.algebra.abs.calculator.eval

import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.api.AlgebraModel
import cn.ancono.math.numberModels.api.FunctionCalculator
import cn.ancono.math.numberModels.structure.AlgebraMultinomialTemplate
import cn.ancono.utilities.CollectionSup
import java.util.*

//Re-implemented by lyc at 2021-04-02 22:41

sealed class AtomicDForm : Comparable<AtomicDForm> {

    abstract val n: Int

    override fun compareTo(other: AtomicDForm): Int {
        return when (this) {
            is CharDForm ->
                when (other) {
                    is CharDForm -> this.s.compareTo(other.s)
                    is RefDForm -> 1
                }

            is RefDForm ->
                when (other) {
                    is CharDForm -> -1
                    is RefDForm -> this.name.compareTo(other.name)
                }
        }
    }
}

data class CharDForm(val s: String) : AtomicDForm() {
    override val n: Int
        get() = 1

    override fun toString(): String {
        return "d$s"
    }
}

data class RefDForm(val name: String, override val n: Int) : AtomicDForm() {
    override fun toString(): String {
        return name
    }
}

//data class AtomicDForm(val s : String, val n : Int = 1) {
////    override fun compareTo(other: AtomicDForm): Int {
////        return s.compareTo(other.s)
////    }
//
//}

/**
 * A single term in a differential form.
 *
 * Created at 2019/9/14 14:59
 * @author  lyc
 */
data class DFBase<T> internal constructor(
        val coefficient: T,
        val bases: Array<AtomicDForm>
) {

    //    override fun toString(): String {
//        if (DFBaseCalculator.ec.isZero(coefficient)) {
//            return "0"
//        }
//        return "($coefficient)${
//            bases.joinToString(MathSymbol.LOGIC_AND) 
//        }"
//    }

    companion object {
//        val ZERO = valueOf(Expression.ZERO, emptyList())

        private fun inverseOrderCountAndCheckDuplicate(list: Array<AtomicDForm>): Int {
            var count = 0
            for (i in list.indices) {
                val x = list[i]
                for (j in i + 1 until list.size) {
                    val y = list[j]
                    val c = x.compareTo(y)
                    if (c > 0) {
                        count += x.n * y.n
                    } else if (c == 0) {
                        return -1
                    }
                }
            }
            return count
        }

        private fun <T> valueOf0(coe: T, bases: Array<AtomicDForm>, mc: FieldCalculator<T>): DFBase<T> {
            val invCount = inverseOrderCountAndCheckDuplicate(bases)
            if (invCount < 0) {
                return zero(mc)
            }
//            ArraySup.in
            Arrays.sort(bases)
            return DFBase(coe, bases)
        }


        fun <T> of(coe: T, mc: FieldCalculator<T>, bases: List<String>): DFBase<T> {
            val arr = Array<AtomicDForm>(bases.size) { CharDForm(bases[it]) }
            return valueOf0(coe, arr, mc)
        }

        fun <T> of(coe: T, mc: FieldCalculator<T>, vararg bases: String): DFBase<T> {
            return of(coe, mc, bases.asList())
        }


        fun <T> valueOf(coe: T, mc: FieldCalculator<T>, bases: List<AtomicDForm>): DFBase<T> {
            return valueOf0(coe, bases.toTypedArray(), mc)
        }

        fun <T> valueOf(coe: T, mc: FieldCalculator<T>, vararg bases: AtomicDForm): DFBase<T> {
            return valueOf(coe, mc, bases.asList())
        }

        fun <T> zero(mc: FieldCalculator<T>): DFBase<T> {
            return valueOf(mc.zero, mc, emptyList())
        }

        fun <T> constant(coe: T): DFBase<T> {
            return DFBase(coe, emptyArray())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DFBase<*>

        if (coefficient != other.coefficient) return false
        if (!bases.contentEquals(other.bases)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = coefficient.hashCode()
        result = 31 * result + bases.contentHashCode()
        return result
    }
}

object DFBaseComparator : Comparator<DFBase<*>> {
    override fun compare(o1: DFBase<*>, o2: DFBase<*>): Int {
        return Arrays.compare(o1.bases, o2.bases)
    }
}

class DFBaseCalculator<T>(val mc: FunctionCalculator<T>) : AlgebraCalculator<T, DFBase<T>> {
    override val scalarCalculator: FieldCalculator<T>
        get() = mc

    override fun scalarMultiply(k: T, v: DFBase<T>): DFBase<T> {
        val t = mc.multiply(k, v.coefficient)
        if (mc.isZero(t)) {
            return zero
        }
        return DFBase(t, v.bases)
    }

    override fun isEqual(x: DFBase<T>, y: DFBase<T>): Boolean {
        return x.bases.contentEquals(y.bases) && mc.isEqual(x.coefficient, y.coefficient)
    }

    override val zero: DFBase<T> = DFBase(mc.zero, emptyArray())

    override fun add(x: DFBase<T>, y: DFBase<T>): DFBase<T> {
        if (mc.isZero(x.coefficient)) {
            return y
        }
        if (mc.isZero(y.coefficient)) {
            return x
        }
        return DFBase(mc.add(x.coefficient, y.coefficient), x.bases)
    }

    override fun negate(x: DFBase<T>): DFBase<T> {
        return DFBase(mc.negate(x.coefficient), x.bases)
    }

    @Suppress("UNCHECKED_CAST")
    override fun multiply(x: DFBase<T>, y: DFBase<T>): DFBase<T> {
        if (mc.isZero(x.coefficient) || mc.isZero(y.coefficient)) {
            return zero
        }
        val b1 = x.bases
        val b2 = y.bases
        val nBases = arrayOfNulls<AtomicDForm>(b1.size + b2.size)
        var i1 = 0
        var i2 = 0
        var i = 0
        var invCount = 0
        var invParts = b1.sumOf { it.n }
        while (i1 < b1.size && i2 < b2.size) {
            val comp = b1[i1].compareTo(b2[i2])
            when {
                comp < 0 -> {
                    val t = b1[i1++]
                    invParts -= t.n
                    nBases[i++] = t
                }
                comp == 0 -> return zero
                else -> {
                    val t = b2[i2++]
                    invCount += invParts * t.n
                    nBases[i++] = t
                }
            }
        }
        if (i1 < b1.size) {
            do {
                nBases[i++] = b1[i1++]
            } while (i1 < b1.size)
        } else {
            while (i2 < b2.size) {
                nBases[i++] = b2[i2++]
            }
        }
        val coe = mc.multiply(x.coefficient, y.coefficient)
        return if (invCount % 2 == 1) {
            DFBase(mc.negate(coe), nBases as Array<AtomicDForm>)
        } else {
            DFBase(coe, nBases as Array<AtomicDForm>)
        }
    }

    override fun isLinearDependent(u: DFBase<T>, v: DFBase<T>): Boolean {
        return mc.isZero(u.coefficient) || mc.isZero(v.coefficient) || u.bases.contentEquals(v.bases)
    }
}

/**
 * Defines a differential form on number model type `T`,
 * it is required that the calculator of `T` is a FunctionCalculator, that is, it supports differential.
 */
class DifferentialForm<T> internal constructor(terms: NavigableSet<DFBase<T>>, val dc: DFBaseCalculator<T>) :
        AlgebraMultinomialTemplate<T, DFBase<T>>(dc, terms),
        AlgebraModel<T, DifferentialForm<T>> {

    private val fc = dc.mc

    override fun fromTerms(ts: NavigableSet<DFBase<T>>): DifferentialForm<T> {
        return DifferentialForm(ts, dc)
    }

    fun differential(vararg variables: String): DifferentialForm<T> {
        return differential(variables.toList())
    }

    private fun <T> Array<T>.sumBy(start: Int, end: Int, mapping: (T) -> Int): Int {
        var res = 0
        for (i in start until end) {
            res += mapping(this[i])
        }
        return res
    }

    private fun diffSingle(term: DFBase<T>, variables: List<String>, re: NavigableSet<DFBase<T>>) {
        val coe = term.coefficient
        val bases = term.bases
//        val basesStr = bases.map{it.}
        for (v in variables) {
            var idx = bases.binarySearch(CharDForm(v))
            if (idx >= 0) {
                continue
            }
            idx = -idx - 1 // inserting index
            var nCoe = fc.differential(coe, v)

            val invCount = bases.sumBy(0, idx) { it.n }

            if (invCount % 2 == 1) {
                nCoe = fc.negate(nCoe)
            }
            val nBase = arrayOfNulls<AtomicDForm>(bases.size + 1)
            System.arraycopy(bases, 0, nBase, 0, idx)
            nBase[idx] = CharDForm(v)
            System.arraycopy(bases, idx, nBase, idx + 1, bases.size - idx)

            @Suppress("UNCHECKED_CAST")
            mergingAdd(re, DFBase(nCoe, nBase as Array<AtomicDForm>))
//            println(nBase)
        }
    }
//
    /**
     * Computes the exterior differential of this differential form. A list of variable names is required.
     */
    fun differential(variables: List<String> = DefaultVariables): DifferentialForm<T> {
        val re = getTS()
        for (t in terms) {
            diffSingle(t, variables, re)
        }
        if (re.isEmpty()) {
            return zero(dc)
        }
        return DifferentialForm(re, dc)
    }

    override fun add(y: DifferentialForm<T>): DifferentialForm<T> {
        return fromTerms(mergeTwo(terms, y.terms))
    }

    override fun negate(): DifferentialForm<T> {
        return fromTerms(applyAllTerms { ac.negate(it) })
    }

    override fun subtract(y: DifferentialForm<T>): DifferentialForm<T> {
        return fromTerms(subtractTerms(terms, y.terms))
    }

    @Suppress("RedundantOverride")
    override fun isZero(): Boolean {
        return super.isZero()
    }

    override fun multiply(k: T): DifferentialForm<T> {
        return fromTerms(multiplyTerms(k))
    }

    override fun divide(k: T): DifferentialForm<T> {
        return multiply(ac.scalarCalculator.reciprocal(k))
    }

    override fun multiply(y: DifferentialForm<T>): DifferentialForm<T> {
        return fromTerms(mergingMultiply(terms, y.terms))
    }


    override fun isLinearRelevant(v: DifferentialForm<T>): Boolean {
        if (terms != v.terms) {
            // using the DFBaseComparator
            return false
        }
        val t1 = terms.toList()
        val t2 = v.terms.toList()
        val mc = dc.mc
        val r = mc.eval { t2.first().coefficient / t1.first().coefficient }
        return (1 until t1.size).all { i ->
            mc.eval {
                isEqual(t1[i].coefficient * r, t2[i].coefficient)
            }
        }
    }

    companion object {
        val DefaultVariables = listOf("x", "y", "z")
        internal fun <T> getSet(): NavigableSet<DFBase<T>> {
            return TreeSet(DFBaseComparator)
        }

        /**
         * Returns a differential form according to the given coefficient and base.
         */
        fun <T> monomial(base: DFBase<T>, dc: DFBaseCalculator<T>): DifferentialForm<T> {
            val terms = getSet<T>()
            terms.add(base)
            return DifferentialForm(terms, dc)
        }

        fun <T> monomial(c: T, bases: List<String>, dc: DFBaseCalculator<T>): DifferentialForm<T> {
            return monomial(DFBase.of(c, dc.mc, bases), dc)
        }

        fun <T> zeroForm(c: T, dc: DFBaseCalculator<T>): DifferentialForm<T> = monomial(DFBase.constant(c), dc)

        /**
         * The differential form zero.
         */
        fun <T> zero(dc: DFBaseCalculator<T>): DifferentialForm<T> = zeroForm(dc.rZero, dc)


        fun <T> monomial(coe: T, dc: DFBaseCalculator<T>, bases: List<AtomicDForm>): DifferentialForm<T> {
            return monomial(DFBase.valueOf(coe, dc.mc, *bases.toTypedArray()), dc)
        }

        fun <T> of(dc: DFBaseCalculator<T>, terms: List<DFBase<T>>): DifferentialForm<T> {
            val set = getSet<T>()
            val re = DifferentialForm(set, dc)
            re.mergingAddAll(set, terms)
            if (set.isEmpty()) {
                return zero(dc)
            }
            return re
        }

        /**
         * Returns a differential form according to the given coefficient and bases.
         */
        fun <T> of(coe: T, mc: FunctionCalculator<T>, vararg bases: String): DifferentialForm<T> {
            val t = DFBase.of(coe, mc, bases.asList())
            val terms = getSet<T>()
            terms.add(t)
            return DifferentialForm(terms, DFBaseCalculator(mc))
        }

        fun <T> calculator(mc: FunctionCalculator<T>): DiffFormCalculator<T> {
            return DiffFormCalculator(mc)
        }


    }


}


class DiffFormCalculator<T>(val mc: FunctionCalculator<T>)
    : AlgebraCalculator<T, DifferentialForm<T>>, UnitRingCalculator<DifferentialForm<T>> {
    val dc = DFBaseCalculator(mc)

    override val zero: DifferentialForm<T> = DifferentialForm.zero(dc)
    override val one: DifferentialForm<T> = DifferentialForm.zeroForm(dc.mc.one, dc)

    override fun isEqual(x: DifferentialForm<T>, y: DifferentialForm<T>): Boolean {
        return CollectionSup.collectionEqualSorted(x.terms, y.terms, dc::isEqual)
    }


    override fun add(x: DifferentialForm<T>, y: DifferentialForm<T>): DifferentialForm<T> {
        return x.add(y)
    }

    override fun negate(x: DifferentialForm<T>): DifferentialForm<T> {
        return x.negate()
    }


    override fun subtract(x: DifferentialForm<T>, y: DifferentialForm<T>): DifferentialForm<T> {
        return x.subtract(y)
    }

    override fun multiply(x: DifferentialForm<T>, y: DifferentialForm<T>): DifferentialForm<T> {
        return x.multiply(y)
    }


    fun divideLong(x: DifferentialForm<T>, n: Long): DifferentialForm<T> {
        return x.multiply(mc.of(Fraction.of(1, n)))
    }

    override fun exactDivide(x: DifferentialForm<T>, y: DifferentialForm<T>): DifferentialForm<T> {
        throw ArithmeticException()
    }


//    override fun constantValue(name: String): DifferentialForm<T>? {
//        val c = mc.constantValue(name) ?: return null
//        return DifferentialForm.zeroForm(c, dc)
//    }


    override val scalarCalculator: FieldCalculator<T>
        get() = mc

    override fun scalarMultiply(k: T, v: DifferentialForm<T>): DifferentialForm<T> {
        return v.multiply(k)
    }

    override fun of(n: Long): DifferentialForm<T> {
        return DifferentialForm.zeroForm(mc.of(n), dc)
    }

//    override fun of(x: Fraction): DifferentialForm<T> {
//        return DifferentialForm.zeroForm(mc.of(x), dc)
//    }

    override fun isLinearDependent(u: DifferentialForm<T>, v: DifferentialForm<T>): Boolean {
        return u.isLinearRelevant(v)
    }

    @Suppress("RedundantOverride")
    override fun isLinearDependent(vs: List<DifferentialForm<T>>): Boolean {
        return super.isLinearDependent(vs)
    }

    override fun isZero(x: DifferentialForm<T>): Boolean {
        return x.isZero()
    }


    @Suppress("UNCHECKED_CAST")
    override val numberClass: Class<DifferentialForm<T>>
        get() = DifferentialForm::class.java as Class<DifferentialForm<T>>


    override fun isUnit(x: DifferentialForm<T>): Boolean {
        throw UnsupportedOperationException()
    }
}