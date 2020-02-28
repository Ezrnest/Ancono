package cn.ancono.logic.propLogic

import cn.ancono.math.exclude

typealias RefMap = Map<String, Formula>
/*
 * Created at 2018/9/18
 * @author liyicheng
 */
/**
 * Defines the matching result of node matcher.
 */
data class FormulaMatchResult(val refMapping: Map<String, Formula>, val origin: Formula, val matcher: FormulaMatcher) {
    operator fun get(refName: String): Formula = refMapping[refName] ?: throw LackOfAssignmentException(refName)
}

/**
 * Note: This matcher is experimental.
 * Performs matching operation on formula, which supports reference. A named reference has a name of type String and
 * the referred Node.
 */
interface FormulaMatcher {
    fun matches(n: Formula, refMapping: RefMap = emptyMap()): FormulaMatchResult?

    /**
     * Returns a sample of required formula of this node matcher, returns `null` if the
     * required node cannot be determined.
     */
    fun requiredFormula(refMapping: RefMap): Formula?

    /**
     * Determines whether this node matcher requires a specific node.
     */
    fun requireSpecific(refMapping: RefMap): Boolean

    val refNames: Set<String>

    fun matcherToString(): String

    fun toStringBracketLevel(): Int

    companion object {
        val A: RefMatcher = "A".m

        val B: RefMatcher = "B".m

        val C: RefMatcher = "C".m

        val T = TrueMatcher

        val F = FalseMatcher
    }
}

fun FormulaMatcher.wrap(matcher: FormulaMatcher): String = if (matcher.toStringBracketLevel() >= this.toStringBracketLevel()) {
    "(${matcher.matcherToString()})"
} else {
    matcher.matcherToString()
}


class RefMatcher(val name: String) : FormulaMatcher {
    override fun requiredFormula(refMapping: RefMap): Formula? {
        return refMapping[name]
    }

    override fun requireSpecific(refMapping: RefMap): Boolean {
        return refMapping.containsKey(name)
    }

    override val refNames: Set<String>
        get() = setOf(name)

    override fun matches(n: Formula, refMapping: RefMap): FormulaMatchResult? {
        if (refMapping.containsKey(name)) {
            return if (n == refMapping[name]) {
                FormulaMatchResult(refMapping, n, this)
            } else {
                null
            }
        }
        return FormulaMatchResult(refMapping + (name to n), n, this)
    }

    override fun matcherToString(): String = name

    override fun toStringBracketLevel(): Int = 0
}

object TrueMatcher : FormulaMatcher {
    override val refNames: Set<String>
        get() = emptySet()

    override fun matches(n: Formula, refMapping: RefMap): FormulaMatchResult? {
        if (n !is PropFormula) {
            return null
        }
        if (n.proposition != TrueStatement) {
            return null
        }
        return FormulaMatchResult(refMapping, n, this)
    }

    override fun requireSpecific(refMapping: RefMap): Boolean {
        return true
    }

    override fun requiredFormula(refMapping: RefMap): Formula? {
        return true.f
    }

    override fun matcherToString(): String = "T"

    override fun toStringBracketLevel(): Int = 0
}

object FalseMatcher : FormulaMatcher {
    override val refNames: Set<String>
        get() = emptySet()

    override fun matches(n: Formula, refMapping: RefMap): FormulaMatchResult? {
        if (n !is PropFormula) {
            return null
        }
        if (n.proposition != FalseStatement) {
            return null
        }
        return FormulaMatchResult(refMapping, n, this)
    }

    override fun requireSpecific(refMapping: RefMap): Boolean {
        return true
    }

    override fun requiredFormula(refMapping: RefMap): Formula? {
        return false.f
    }

    override fun matcherToString(): String = "F"

    override fun toStringBracketLevel(): Int = 0
}

class TypedMatcher(val name: String, val order: Int) : FormulaMatcher {
    override fun requiredFormula(refMapping: RefMap): Formula? {
        return refMapping[name]
    }

    override fun requireSpecific(refMapping: RefMap): Boolean {
        return refMapping.containsKey(name)
    }

    override val refNames: Set<String>
        get() = setOf(name)

    override fun matches(n: Formula, refMapping: RefMap): FormulaMatchResult? {
        if (refMapping.containsKey(name)) {
            return if (n == refMapping[name]) {
                FormulaMatchResult(refMapping, n, this)
            } else {
                null
            }
        }
        if (n.order != order) {
            return null
        }
        return FormulaMatchResult(refMapping + (name to n), n, this)
    }

    override fun matcherToString(): String = name

    override fun toStringBracketLevel(): Int = 0
}

internal fun buildByType(type: BiMatcher.Type, a: Formula, b: Formula): Formula {
    return when (type) {
        BiMatcher.Type.IMPLIES -> a implies b
        BiMatcher.Type.EQUAL -> a equalTo b
    }
}


class BiMatcher(private val type: Type, val a: FormulaMatcher, val b: FormulaMatcher) : FormulaMatcher {
    enum class Type {
        IMPLIES,
        EQUAL
    }


    override val refNames: Set<String>
        get() = a.refNames union b.refNames

    override fun matches(n: Formula, refMapping: RefMap): FormulaMatchResult? {
        if (n !is BiFormula) {
            return null
        }
        when (type) {
            BiMatcher.Type.IMPLIES -> if (n !is ImplyFormula) {
                return null
            }
            BiMatcher.Type.EQUAL -> if (n !is EquivalentFormula) {
                return null
            }
        }
        return if (type == Type.IMPLIES) {
            orderedMatches(n, refMapping)
        } else {
            notOrderedMatches(n, refMapping)
        }

    }

    private fun orderedMatches(n: BiFormula, refMapping: RefMap): FormulaMatchResult? {
        val nMapping = orderedMatchesPair(n.p, n.q, refMapping) ?: return null
        return FormulaMatchResult(nMapping, n, this)
    }

    private fun orderedMatchesPair(p: Formula, q: Formula, refMapping: RefMap): RefMap? {
        return correspondingMatches(p, a, q, b, refMapping) ?: correspondingMatches(q, b, p, a, refMapping)
    }

    private fun correspondingMatches(p: Formula, m1: FormulaMatcher, q: Formula, m2: FormulaMatcher, refMapping: RefMap): RefMap? {
        val nMapping = m1.matches(p, refMapping)?.refMapping ?: return null
        return m2.matches(q, nMapping)?.refMapping
    }


    private fun notOrderedMatches(n: BiFormula, refMapping: RefMap): FormulaMatchResult? {
        val nMapping = orderedMatchesPair(n.p, n.q, refMapping) ?: orderedMatchesPair(n.q, n.p, refMapping)
        ?: return null
        return FormulaMatchResult(nMapping, n, this)
    }

    override fun requiredFormula(refMapping: RefMap): Formula? {
        val require1 = a.requiredFormula(refMapping) ?: return null
        val require2 = b.requiredFormula(refMapping) ?: return null
        return buildByType(type, require1, require2)
    }

    override fun requireSpecific(refMapping: RefMap): Boolean {
        return a.requireSpecific(refMapping) && b.requireSpecific(refMapping)
    }

    override fun matcherToString(): String {
        return when (type) {
            BiMatcher.Type.IMPLIES -> "${wrap(a)}→${wrap(b)}"
            BiMatcher.Type.EQUAL -> "${wrap(a)}↔${wrap(b)}"
        }
    }

    override fun toStringBracketLevel(): Int = 3


}


class NotMatcher(val a: FormulaMatcher) : FormulaMatcher {
    override fun matches(n: Formula, refMapping: RefMap): FormulaMatchResult? {
        if (n !is NotFormula) {
            return null
        }
        val nMapping = a.matches(n.formula, refMapping)?.refMapping ?: return null
        return FormulaMatchResult(nMapping, n, this)
    }

    override fun requiredFormula(refMapping: RefMap): Formula? {
        val f = a.requiredFormula(refMapping) ?: return null
        return !f
    }

    override fun requireSpecific(refMapping: RefMap): Boolean {
        return a.requireSpecific(refMapping)
    }

    override val refNames: Set<String>
        get() = a.refNames

    override fun matcherToString(): String {
        return "¬${wrap(a)}"
    }

    override fun toStringBracketLevel(): Int = 2
}

class AndOrMatcher(val isAnd: Boolean, val matchers: List<FormulaMatcher>,
                   val remainingMatcher: FormulaMatcher = isAnd.m) : FormulaMatcher {
    override fun matches(n: Formula, refMapping: RefMap): FormulaMatchResult? {
        if (n !is AOFormula) {
            return null
        }
        if (n.isAnd != isAnd) {
            return null
        }

        val re = match0(refMapping, matchers, 0, n.fs) ?: return null
        var nMapping = re.first
        val remaining = re.second

        nMapping = if (!remaining.isEmpty()) {
            val reAsAM = andOr(isAnd, remaining)
            val result = remainingMatcher.matches(reAsAM, nMapping) ?: return null
            result.refMapping
        } else {
            val rem = isAnd.f
            remainingMatcher.matches(rem, nMapping)?.refMapping ?: return null
        }

        return FormulaMatchResult(nMapping, n, this)
    }


    private fun match0(refMapping: RefMap, matchers: List<FormulaMatcher>, matIdx: Int, remainingNodes: List<Formula>)
            : Pair<RefMap, List<Formula>>? {
        if (matIdx >= matchers.size) {
            return refMapping to remainingNodes
        }
        val matcher = matchers[matIdx]

        for (n in remainingNodes.asSequence().withIndex()) {
            val f = n.value
            val result = matcher.matches(f, refMapping) ?: continue
            val nRemaining = remainingNodes.exclude(n.index)
//            if(matIdx == matchers.size - 1){
//                return result.refMapping to nRemaining
//            }
            val refMap = match0(result.refMapping, matchers, matIdx + 1, nRemaining)
            if (refMap != null) {
                return refMap
            }
            //failed
        }
        return null
    }

    override fun requiredFormula(refMapping: RefMap): Formula? {
        return null
    }

    override fun requireSpecific(refMapping: RefMap): Boolean {
        return false
    }

    override val refNames: Set<String> by lazy { matchers.flatMapTo(hashSetOf()) { it.refNames } }

    override fun matcherToString(): String {
        val separator = if (isAnd) {
            "∧"
        } else {
            "∨"
        }
        return matchers.joinToString(separator, transform = { wrap(it) })
    }

    override fun toStringBracketLevel(): Int {
        return 2
    }

}


/**
 * Converts a boolean to
 */
val Boolean.m: FormulaMatcher
    get() = if (this) {
        TrueMatcher
    } else {
        FalseMatcher
    }


val String.m: RefMatcher
    get() = RefMatcher(this)


operator fun FormulaMatcher.not() = NotMatcher(this)

infix fun FormulaMatcher.and(another: FormulaMatcher) = AndOrMatcher(true, listOf(this, another))
infix fun FormulaMatcher.or(another: FormulaMatcher) = AndOrMatcher(false, listOf(this, another))
infix fun FormulaMatcher.implies(another: FormulaMatcher) = BiMatcher(BiMatcher.Type.IMPLIES, this, another)
infix fun FormulaMatcher.equalTo(another: FormulaMatcher) = BiMatcher(BiMatcher.Type.EQUAL, this, another)
