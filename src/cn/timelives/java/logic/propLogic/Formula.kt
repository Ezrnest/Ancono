@file:Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")

package cn.timelives.java.logic.propLogic

import cn.timelives.java.math.isSorted
import cn.timelives.java.utilities.BinarySup
import cn.timelives.java.utilities.CollectionSup
import java.lang.AssertionError
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.AbstractMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.experimental.buildSequence

/*
 * Created at 2018/9/18
 * @author liyicheng
 */
typealias TruthAssignment = Map<String, Boolean>

val TrueFormula = true.asProposition().f
val FalseFormula = false.asProposition().f
val p: PropFormula = "p".f

val q: PropFormula = "q".f
val r: PropFormula = "r".f

val T = TrueFormula

val F = FalseFormula

/**
 * Describes a proposition formula in logic.
 */
sealed class Formula : Comparable<Formula> {


    /**
     * The names of propositions concerned in this statement.
     */
    abstract val variableNames: Set<String>

    internal abstract val bracketLevel: Int

    /**
     * Returns the propositions occurred in this formula.
     */
    abstract val propositions: Set<Proposition>

    /**
     * Substitute the truth assignment to the state and determines the
     * result.
     */
    abstract fun substitute(truthAssignment: TruthAssignment): Boolean

    /**
     * Substitute the part of the propositions in the statement and returns
     * a new statement, which is not simplified.
     */
    abstract fun substitutePartly(truthAssignment: TruthAssignment): Formula

    /**
     * Recursively applies the function until the given depth.
     */
    abstract fun recurApply(depth: Int = Int.MAX_VALUE, f: (Formula) -> Formula): Formula

    /**
     * Generates the truth table of this formula.
     */
    fun computeTruthTable(): List<Pair<TruthAssignment, Boolean>> {
        val names = variableNames
        var taMap = TruthAssignmentMap(names)
        val total = 1 shl names.size
        val list = ArrayList<Pair<TruthAssignment, Boolean>>(total)
        repeat(total) {
            list.add(taMap to substitute(taMap))
            taMap = taMap.next()
        }
        return list
    }

    @Suppress("EXPERIMENTAL_FEATURE_WARNING")
    fun iterateTruthTable(): Sequence<Pair<TruthAssignment, Boolean>> = buildSequence {
        val names = variableNames
        var taMap = TruthAssignmentMap(names)
        val total = 1 shl names.size
        repeat(total) {
            yield(taMap to substitute(taMap))
            taMap = taMap.next()
        }
    }

    val isTautology: Boolean by lazy { iterateTruthTable().all { it.second } }

    val isContradictory: Boolean by lazy { iterateTruthTable().none { it.second } }

    val isSatisfiable: Boolean by lazy { iterateTruthTable().any { it.second } }


    /**
     * Returns a string representation of this formula without the outer bracket.
     */
    abstract override fun toString(): String


//    internal inline fun wrapBracketToString(another : Formula, block : (String)->String) : String{
//        return if(another.bracketLevel >= this.bracketLevel){
//            block("($another)")
//        }else{
//            block(another.toString())
//        }
//    }

    internal fun wrapBracket(f: Formula): String = if (f.bracketLevel >= this.bracketLevel) {
        "($f)"
    } else {
        f.toString()
    }

    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int


}

val Formula.order: Int
    get() = when (this) {
        is PropFormula -> 0
        is NotFormula -> 1
        is AOFormula -> if(this.isAnd){
            2
        }else{
            3
        }
        is ImplyFormula -> 4
        is EquivalentFormula -> 5
    }


fun Formula.isTrueAssignment(truthAssignment: TruthAssignment) = this assign truthAssignment

fun Formula.isFalseAssignment(truthAssignment: TruthAssignment) = this assign truthAssignment


val Formula.isPropOrNot: Boolean
    get() = (this is PropFormula) || ((this is NotFormula) && this.formula is PropFormula)

/**
 * Or
 */
val Formula.isSimpleDisjunctiveForm: Boolean
    get() {
        if (this.isPropOrNot) {
            return true
        }
        if (this !is AOFormula) {
            return false
        }
        if (this.isAnd) {
            return false
        }
        return this.fs.all {
            it.isPropOrNot
        }
    }


/**
 * And
 */
val Formula.isSimpleConjunctiveForm: Boolean
    get() {
        if (this.isPropOrNot) {
            return true
        }
        if (this !is AOFormula) {
            return false
        }
        if (!this.isAnd) {
            return false
        }
        return this.fs.all {
            it.isPropOrNot
        }
    }

val Formula.isDisjunctiveNormalForm: Boolean
    get() {
        if (this.isPropOrNot) {
            return true
        }
        if (this !is AOFormula) {
            return false
        }
        if (this.isAnd) {
            return false
        }
        return this.fs.all { it.isSimpleConjunctiveForm }
    }

val Formula.isConjunctiveNormalForm: Boolean
    get() {
        if (this.isPropOrNot) {
            return true
        }
        if (this !is AOFormula) {
            return false
        }
        if (!this.isAnd) {
            return false
        }
        return this.fs.all { it.isSimpleDisjunctiveForm }
    }


class TruthAssignmentMap internal constructor(val nameArray: Array<String>, var valueBits: Long = 0) : AbstractMap<String, Boolean>() {

    constructor(variableNames: Set<String>) : this(variableNames.toTypedArray()) {
        nameArray.sort()
    }

    override val entries: Set<Map.Entry<String, Boolean>> by lazy {
        val result = TreeSet<Map.Entry<String, Boolean>>()
        for (s in nameArray) {
            result.add(object : Map.Entry<String, Boolean> {
                override val value: Boolean = get(s)!!
                override val key: String = s
            })
        }
        result
    }

    override fun get(key: String): Boolean? {
        val idx = nameArray.binarySearch(key)
        return if (idx < 0) {
            null
        } else {
            BinarySup.bitOf(valueBits, idx)
        }
    }

    fun next(): TruthAssignmentMap {
        return TruthAssignmentMap(nameArray, valueBits + 1)
    }

    fun inc() {
        valueBits++
    }


}

infix fun Formula.assign(truthAssign: TruthAssignment): Boolean = this.substitute(truthAssign)

val Proposition.f
    get() = PropFormula(this)

/**
 * Converts a boolean to a formula
 */
val Boolean.f: PropFormula
    get() = if (this) {
        TrueFormula
    } else {
        FalseFormula
    }

val String.f: PropFormula
    get() = this.asProposition().f


operator fun Formula.not() = NotFormula(this)

infix fun Formula.and(another: Formula) = AOFormula(true, listOf(this, another))
infix fun Formula.or(another: Formula) = AOFormula(false, listOf(this, another))
infix fun Formula.implies(another: Formula) = ImplyFormula(this, another)
infix fun Formula.equalTo(another: Formula) = EquivalentFormula(this, another)
fun andAll(vararg fs: Formula): Formula = andAll(fs.toList())
fun andAll(fs: List<Formula>): Formula {
    return when {
        fs.isEmpty() -> T
        fs.size == 1 -> fs[0]
        else -> AOFormula(true, fs.toList())
    }
}

fun orAll(vararg fs: Formula): Formula = orAll(fs.toList())
fun orAll(fs: List<Formula>): Formula {
    return when {
        fs.isEmpty() -> F
        fs.size == 1 -> fs[0]
        else -> AOFormula(false, fs)
    }
}

fun andOr(isAnd: Boolean, fs: List<Formula>): Formula = if (isAnd) {
    andAll(fs)
} else {
    orAll(fs)
}


class PropFormula(val proposition: Proposition) : Formula() {


    override val bracketLevel: Int
        get() = 0
    override val variableNames: Set<String>
        get() = proposition.variableNames

    override val propositions: Set<Proposition>
        get() = setOf(proposition)

    override fun substitute(truthAssignment: TruthAssignment): Boolean {
        return proposition.eval(truthAssignment)
    }

    override fun substitutePartly(truthAssignment: TruthAssignment): Formula {
        return if (truthAssignment.keys.containsAll(proposition.variableNames)) {
            proposition.eval(truthAssignment).f
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is PropFormula && proposition.equals(other.proposition)
    }

    override fun compareTo(other: Formula): Int {
        if (other.order != order) {
            return order - other.order
        }

        return proposition.compareTo((other as PropFormula).proposition)
    }


    override fun hashCode(): Int {
        return proposition.hashCode()
    }

    override fun toString(): String = proposition.toString()

    override fun recurApply(depth: Int, f: (Formula) -> Formula): Formula {
        return f(this)
    }
}

class NotFormula(val formula: Formula) : Formula() {
    override val bracketLevel: Int
        get() = 1

    override val variableNames: Set<String>
        get() = formula.variableNames

    override val propositions: Set<Proposition>
        get() = formula.propositions

    override fun substitute(truthAssignment: TruthAssignment): Boolean {
        return !formula.substitute(truthAssignment)
    }

    override fun substitutePartly(truthAssignment: TruthAssignment): NotFormula {
        return !formula.substitutePartly(truthAssignment)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NotFormula) return false

        if (formula != other.formula) return false

        return true
    }

    override fun hashCode(): Int {
        return formula.hashCode()
    }

    override fun toString(): String = "¬${wrapBracket(formula)}"

    override fun recurApply(depth: Int, f: (Formula) -> Formula): Formula {
        return f(if (depth < 1) {
            (this)
        } else {
            !formula.recurApply(depth - 1, f)
        })
    }

    override fun compareTo(other: Formula): Int {
        if (other.order != order) {
            return order - other.order
        }

        return formula.compareTo((other as NotFormula).formula)
    }


}

sealed class BiFormula(val p: Formula, val q: Formula) : Formula() {
    override fun compareTo(other: Formula): Int {
        if (other.order != order) {
            return order - other.order
        }
        val another = other as BiFormula
        val comp = p.compareTo(another.p)
        if (comp != 0) {
            return comp
        }
        return q.compareTo(another.q)
    }
}

/**
 * And and Or formula
 */
class AOFormula(val isAnd: Boolean, fs: List<Formula>) : Formula() {


    val fs: List<Formula> = if (fs.isSorted()) {
        fs
    } else {
        fs.sorted()
    }

    init {
        require(fs.size > 1)
//        if(this.fs.size == 3){
//            println(fs.joinToString())
//            println(fs.isSorted())
//            println(fs.sorted().isSorted())
//        }
    }

    override val bracketLevel: Int
        get() = 2

    override val variableNames: Set<String> by lazy { fs.flatMapTo(hashSetOf()) { it.variableNames }.toSet() }

    override val propositions: Set<Proposition> by lazy { fs.flatMapTo(hashSetOf()) { it.propositions } }

    override fun substitute(truthAssignment: TruthAssignment): Boolean = if (isAnd) {
        fs.all { it.substitute(truthAssignment) }
    } else {
        fs.any { it.substitute(truthAssignment) }
    }

    override fun substitutePartly(truthAssignment: TruthAssignment): AOFormula = AOFormula(isAnd, fs.map { it.substitutePartly(truthAssignment) })


    override fun toString(): String {
        val separator = if (isAnd) {
            "∧"
        } else {
            "∨"
        }
        return fs.joinToString(separator, transform = { wrapBracket(it) })
    }

    //"${wrapBracket(p)}∧${wrapBracket(q)}"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AOFormula) return false

        if (isAnd != other.isAnd) return false
        if (fs != other.fs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isAnd.hashCode()
        result = 31 * result + fs.hashCode()
        return result
    }

    override fun recurApply(depth: Int, f: (Formula) -> Formula): Formula {
        return f(if (depth < 1) {
            this
        } else {
            val re = fs.mapTo(ArrayList(fs.size)) { it.recurApply(depth - 1, f) }
            re.sort()
            AOFormula(isAnd, re)
        })
    }

    override fun compareTo(other: Formula): Int {
        if (other.order != order) {
            return order - other.order
        }
        val another = other as AOFormula
        if (isAnd) {
            if (!another.isAnd) {
                return -1
            }
        } else {
            if (another.isAnd) {
                return 1
            }
        }
        val fs1 = fs
        val fs2 = another.fs
        return CollectionSup.compareCollection(fs1, fs2, Comparator.naturalOrder())
    }
}

//class OrFormula(p: Formula, q: Formula) : BiFormula(p, q) {
//
//    override val bracketLevel: Int
//        get() = 2
//
//    override val variableNames: Set<String>
//        get() = p.variableNames union q.variableNames
//
//    override val propositions: Set<Proposition>
//        get() = p.propositions union q.propositions
//
//    override fun substitute(truthAssignment: TruthAssignment): Boolean {
//        return p.substitute(truthAssignment) or q.substitute(truthAssignment)
//    }
//
//    override fun substitutePartly(truthAssignment: TruthAssignment): OrFormula {
//        return p.substitutePartly(truthAssignment) or q.substitutePartly(truthAssignment)
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is OrFormula) return false
//
//        if (p != other.p) return false
//        if (q != other.q) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = p.hashCode()
//        result = 31 * result + q.hashCode()
//        return result
//    }
//
//    override fun toString(): String = "${wrapBracket(p)}∨${wrapBracket(q)}"
//
//}

class ImplyFormula(p: Formula, q: Formula) : BiFormula(p, q) {

    override val bracketLevel: Int
        get() = 2

    override val variableNames: Set<String>
        get() = p.variableNames union q.variableNames
    override val propositions: Set<Proposition>
        get() = p.propositions union q.propositions

    override fun substitute(truthAssignment: TruthAssignment): Boolean {
        val pTruth = p.substitute(truthAssignment)
        return (pTruth and q.substitute(truthAssignment)) or !pTruth
    }

    override fun substitutePartly(truthAssignment: TruthAssignment): ImplyFormula {
        return p.substitutePartly(truthAssignment) implies q.substitutePartly(truthAssignment)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImplyFormula) return false

        if (p != other.p) return false
        if (q != other.q) return false

        return true
    }

    override fun hashCode(): Int {
        var result = p.hashCode()
        result = 31 * result + q.hashCode()
        return result
    }


    override fun toString(): String {
        return "${wrapBracket(p)}→${wrapBracket(q)}"
    }

    override fun recurApply(depth: Int, f: (Formula) -> Formula): Formula {
        return f(if (depth < 1) {
            this
        } else {
            p.recurApply(depth - 1, f) implies q.recurApply(depth - 1, f)
        })
    }
}

class EquivalentFormula(p: Formula, q: Formula) : BiFormula(p, q) {
    override val bracketLevel: Int
        get() = 2

    override val variableNames: Set<String>
        get() = p.variableNames union q.variableNames

    override val propositions: Set<Proposition>
        get() = p.propositions union q.propositions

    override fun substitute(truthAssignment: TruthAssignment): Boolean {
        val pTruth = p.substitute(truthAssignment)
        val qTruth = q.substitute(truthAssignment)
        return pTruth == qTruth
    }

    override fun substitutePartly(truthAssignment: TruthAssignment): EquivalentFormula {
        return p.substitutePartly(truthAssignment) equalTo q.substitutePartly(truthAssignment)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EquivalentFormula) return false

        if (p != other.p) return false
        if (q != other.q) return false

        return true
    }

    override fun hashCode(): Int {
        var result = p.hashCode()
        result = 31 * result + q.hashCode()
        return result
    }

    override fun toString(): String {
        return "${wrapBracket(p)}↔${wrapBracket(q)}"
    }

    override fun recurApply(depth: Int, f: (Formula) -> Formula): Formula {
        return f(if (depth < 1) {
            this
        } else {
            f(p) equalTo f(q)
        })
    }

}

fun Formula.simplify(steps: MutableList<SimplificationStep>? = null): Formula = simplifyByRules(this, Rule.basicRules, steps)
fun Formula.simplifyWithSteps(): Pair<Formula, List<SimplificationStep>> = simplifyByRulesWithSteps(this, Rule.basicRules)

fun Formula.toDisjunctiveNorm(steps: MutableList<SimplificationStep>? = null): Formula = simplifyByRules(this, Rule.toDisjunctiveNormalFormRules, steps)
fun Formula.toConjunctiveNorm(steps: MutableList<SimplificationStep>? = null): Formula = simplifyByRules(this, Rule.toConjunctiveNormalFormRules, steps)

fun Formula.toMainDisjunctiveNorm(props: Set<Proposition> = this.propositions, steps: MutableList<SimplificationStep>? = null): Formula {
    val f = this.toDisjunctiveNorm(steps)
    if (f is PropFormula) {
        if(props.size == 1 && props.contains(f.proposition)) {
            return f
        }

        val nfs =
            expandConj(f, props)
        return orAll(nfs).simplify(steps)
    }
    if (f !is AOFormula) {
        throw IllegalArgumentException()
    }
    val nfs = f.fs.flatMapTo(ArrayList()) {
        expandConj(it, props)
    }
//    steps?.add(SimplificationStep())
    nfs.sort()
    return orAll(nfs).simplify(steps)
}

internal fun expandConj(f: Formula, props: Set<Proposition>): List<Formula> {
    val totalSize = props.size
    if ((f is PropFormula || f is NotFormula) && totalSize == 1) {
        return listOf(f)
    }
    if (f is AOFormula && totalSize == f.fs.size) {
        return listOf(f)
    }
    val list = ArrayList<Formula>(totalSize)
    when (f) {
        is NotFormula -> {
            list += f
        }
        is PropFormula -> {
            list += f
        }
        is AOFormula -> {
            for (i in f.fs.indices) {
                list += f.fs[i]
            }
        }
        else -> {
            throw AssertionError()
        }
    }
    val rems = props.subtract(f.propositions).toList()
    val dest: MutableList<Formula> = ArrayList(1 shl rems.size)
    val map = HashMap<Pair<Proposition, Boolean>, Formula>()
    for (p in rems) {
        val t = p.f
        map += (p to true) to t
        map += (p to false) to !t
    }
    expandConj0(totalSize, list, rems, 0, dest) { p, b ->
        map[p to b]!!
    }
    return dest
}

internal fun expandConj0(totalSize: Int,
                         fs: MutableList<Formula>,
                         rems: List<Proposition>,
                         remIndex: Int,
                         dest: MutableList<Formula>,
                         propFormulaSupplier: (Proposition, Boolean) -> Formula) {
    if (remIndex == rems.size) {
        dest += andAll(fs)
        return
    }
    val curProp = rems[remIndex]
    val nfs = ArrayList<Formula>(totalSize).also { it.addAll(fs) }
    nfs += propFormulaSupplier(curProp, false)
    fs += propFormulaSupplier(curProp, true)
    expandConj0(totalSize, nfs, rems, remIndex + 1, dest, propFormulaSupplier)
    expandConj0(totalSize, fs, rems, remIndex + 1, dest, propFormulaSupplier)

}

fun Formula.isMinimalOf(propositions: List<Proposition> = this.propositions.sorted()): Boolean {
    if (propositions.size == 1) {
        return this is PropFormula && this.proposition == propositions[0]
    }
    if (this !is AOFormula) {
        return false
    }
    if (fs.size != propositions.size) {
        return false
    }
    val covered = BooleanArray(fs.size)
    for (f in fs) {
        val prop = unwrapProp(f) ?: return false
        val idx = propositions.indexOf(prop)
        if (idx < 0 || covered[idx]) {
            return false
        }
        covered[idx] = true

    }

    return true
}

internal fun unwrapProp(f: Formula): Proposition? {
    return when (f) {
        is PropFormula -> f.proposition
        is NotFormula -> {
            val t = f.formula as? PropFormula ?: return null
            t.proposition
        }
        else -> return null
    }
}

fun Formula.minimalNormIndex(propositions: List<Proposition> = this.propositions.sorted()): Long {
    if (!this.isMinimalOf(propositions)) {
        throw IllegalArgumentException()
    }
    val fs = (this as AOFormula).fs
    var re = 0L
    FOR@
    for (f in fs) {
        when (f) {
            is NotFormula -> {
                continue@FOR
            }
            is PropFormula -> {
                val prop: Proposition = f.proposition
                val idx = propositions.indexOf(prop)
                re = BinarySup.setBitOf(re, idx)
            }
            else -> throw IllegalArgumentException()
        }
    }
    return re
}

infix fun Formula.valueEquals(another: Formula): Boolean {
    if(this == another){
        return true
    }
    if(this == TrueFormula || this == FalseFormula){
        return another.valueEquals(this)
    }
    if(another == TrueFormula){
        val props = this.propositions
        val mdn = toMainDisjunctiveNorm(props)
        if(mdn == TrueFormula){
            return true
        }
        if(mdn is AOFormula){
            return mdn.fs.size == (1 shl props.size)
        }
        return false
    }
    val props = this.propositions union another.propositions
    return toMainDisjunctiveNorm(props) == another.toMainDisjunctiveNorm(props)
}

fun main(args: Array<String>) {
//    println(p and (q or true.f) equalTo p)
    val prop = (((p implies q) and (q implies r)) and p)
    println(r.toMainDisjunctiveNorm(prop.propositions))
//    val prop = !((q and r) or (r and !p) or (!q and !p))
////    val dmorgan = Rule.ruleDeMorgan1
////    println(dmorgan.matches(prop))
////    println(dmorgan.tryApply(prop))
//    val steps = ArrayList<SimplificationStep>()
//    val re = prop.toDisjunctiveNorm(steps)
//    println(re)

//    val prop = (((p implies q) and (q implies r)) and p) implies r
//    println(prop)
//
//
//
//
    println(prop.toMainDisjunctiveNorm())
//    println(dmorgan.matches(re))
//    steps.forEach { println(it) }
//    println((!(!p or q)).simplifyWithSteps())
//    val t = (q or !((!p or q)and p)).simplifyWithSteps()
//    println(t.first)
//    t.second.forEach { println(it) }
//    println(rule3().matches(p or p))
//    println((((p or q) implies r) implies p).toConjunctiveNorm())
//    val form = p or q
//    val matcher = A or B
//    println(matcher.matches(form))

//    println((orAll(!p,!q,r)).minimalNormIndex())
//    val re =f1.toMainDisjunctiveNorm()
//    println(re.simplify())
//    println(re.simplify())
//    println(Rule.basicRules)
//    assert((p implies q) valueEquals (!p or (p and q)))
//    val f2 = (!(p implies q))and q
//    println(f2.simplify())
//    println()
//    val f3 = ((p implies q)and p) implies q
//    val steps = ArrayList<SimplificationStep>()
//    println(f3.toMainDisjunctiveNorm(steps))
//    println(steps.joinToString(separator = System.lineSeparator()))
//    println((!r).compareTo(q))
//    println((q).compareTo(!r))
//    println(andAll(listOf(p,q,!r)))
//    println(andAll(listOf(!r,p,q)))
}