package cn.ancono.logic.prop

import cn.ancono.logic.prop.FormulaMatcher.Companion.A
import cn.ancono.logic.prop.FormulaMatcher.Companion.B
import cn.ancono.logic.prop.FormulaMatcher.Companion.F
import cn.ancono.logic.prop.FormulaMatcher.Companion.T
import java.util.*

/*
 * Created at 2018/9/19
 * @author liyicheng
 */
typealias ReplacementBuilder = (FormulaMatchResult) -> Formula

/**
 * Defines a simplification rule of the formula.
 */
interface Rule {

    val name: String

    val symbolRepresentation: String

    fun matches(f: Formula): Boolean

    fun apply(f: Formula): Formula

    fun tryApply(f: Formula): Formula? = if (matches(f)) {
        apply(f)
    } else {
        null
    }

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {

        val ruleFlattenAndOr = ruleFlatten()
        val ruleDoubleNegative = rule1()
        val ruleIdentityAnd = rule2()
        val ruleIdentityOr = rule3()
        val ruleAbsorptionAnd = rule4()
        val ruleAbsorptionOr = rule5()
        val ruleZeroLaw1 = rule6()
        val ruleZeroLaw2 = rule7()
        val ruleIdentityLaw1 = rule8()
        val ruleIdentityLaw2 = rule9()
        val ruleExcludedMiddle = rule10()
        val ruleContradiction = rule11()
        val ruleImplyEqual = rule12()
        val ruleEquivalenceEqual = rule13()
        val ruleAbsurdity = rule14()
        //        val ruleDeMorgan1 = rule15()
//        val ruleDeMorgan2 = rule16()
        val ruleDeMorgan = ruleDeMorgan()

        val basicRules: List<Rule> = initBasicRules()

        val toDisjunctiveNormalFormRules = basicRules + rule18()

        val toConjunctiveNormalFormRules = basicRules + rule17()


        private fun initBasicRules(): List<Rule> {
            val list = ArrayList<Rule>(20)
            list += ruleFlattenAndOr
            list += ruleDoubleNegative
            list += ruleIdentityAnd
            list += ruleIdentityOr
            list += ruleAbsorptionAnd
            list += ruleAbsorptionOr
            list += ruleZeroLaw1
            list += ruleZeroLaw2
            list += ruleIdentityLaw1
            list += ruleIdentityLaw2
            list += ruleExcludedMiddle
            list += ruleContradiction
            list += ruleImplyEqual
            list += ruleEquivalenceEqual
            list += ruleAbsurdity
            list += ruleDeMorgan
            return list
        }

    }
}

internal abstract class AbstractRule() : Rule {
    override fun apply(f: Formula): Formula {
        return tryApply(f) ?: throw IllegalArgumentException()
    }

    override fun toString(): String {
        return "$name:  $symbolRepresentation"
    }

    abstract override fun tryApply(f: Formula): Formula?
}


class MatcherRule(override val name: String, override val symbolRepresentation: String, val matcher: FormulaMatcher, val replacementBuilder: ReplacementBuilder) : Rule {
    override fun matches(f: Formula): Boolean {
        return matcher.matches(f) != null
    }

    override fun apply(f: Formula): Formula {
        val result = matcher.matches(f) ?: throw IllegalArgumentException()
        return replacementBuilder(result)
    }

    override fun tryApply(f: Formula): Formula? {
        val result = matcher.matches(f) ?: return null
        return replacementBuilder(result)
    }

    override fun toString(): String {
        return "$name:  $symbolRepresentation"
    }
}

infix fun FormulaMatcher.to(builder: ReplacementBuilder): Pair<FormulaMatcher, ReplacementBuilder> = Pair(this, builder)


fun Pair<FormulaMatcher, ReplacementBuilder>.named(name: String): Rule {
    val matcher = first
    val builder = second
    if (matcher is AndOrMatcher) {
        return wrapAndOr(name, matcher, builder)
    }
    return simpleBuildRule(name, matcher, builder)
}

fun simpleBuildRule(name: String, matcher: FormulaMatcher, builder: ReplacementBuilder): MatcherRule {
    return MatcherRule(name, generateSymbolRepresentation(matcher, builder), matcher, replacementBuilder = builder)
}

fun wrapAndOr(name: String, mat: AndOrMatcher, builder: ReplacementBuilder, symbolRepresentation: String? = null): Rule {
    if (!(mat.remainingMatcher is TrueMatcher || mat.remainingMatcher is FalseMatcher)) {
        return simpleBuildRule(name, mat, builder)
    }
    val symbol = symbolRepresentation ?: generateSymbolRepresentation(mat, builder)
    val ref = "remaining#${Random().nextInt()}"
    val remMatcher = ref.m
    val matcher = AndOrMatcher(mat.isAnd, mat.matchers, remMatcher)
    val nBuilder: ReplacementBuilder = { map ->
        val rem = map[ref]
        if (mat.isAnd.f == rem) {
            builder(map)
        } else {
            andOr(mat.isAnd, listOf(rem, builder(map)))
        }

    }
    return MatcherRule(name, symbol, matcher, nBuilder)
}

fun generateSymbolRepresentation(matcher: FormulaMatcher, replacementBuilder: ReplacementBuilder): String {
    val left = matcher.matcherToString()
    val dummyMap = matcher.refNames.associate { it to it.f }
    val right = replacementBuilder(FormulaMatchResult(dummyMap, TrueFormula, matcher))
    return "$left ⇒ $right"
}

operator fun Rule.invoke(f: Formula): Formula? = tryApply(f)


internal fun of(p: Pair<FormulaMatcher, ReplacementBuilder>, name: String) = p.named(name)

fun ruleFlatten(): Rule = object : Rule {
    override val name: String
        get() = "Flatten And/Or"
    override val symbolRepresentation: String
        get() = "(A∧B)∧C ⇒ A∧B∧C, (A∨B)∨C ⇒ A∨B∨C"

    override fun matches(f: Formula): Boolean {
        if (f !is AOFormula) {
            return false
        }
        return f.fs.any { it is AOFormula && it.isAnd == f.isAnd }
    }

    override fun apply(f: Formula): Formula {
        return tryApply(f) ?: throw IllegalArgumentException()
    }

    override fun tryApply(f: Formula): Formula? {
        if (!matches(f)) {
            return null
        }
        val t = f as AOFormula
        return AOFormula(t.isAnd, t.fs.flatMap {
            if (it is AOFormula && it.isAnd == t.isAnd) {
                it.fs
            } else {
                listOf(it)
            }
        })
    }

    override fun toString(): String {
        return "$name:  $symbolRepresentation"
    }
}

internal fun rule1(): Rule = of(
        !(!A) to { it["A"] }, "Double Negative"
)

internal fun rule2(): Rule = of(
        (A and A) to { it["A"] }, "Identity:And"
)

internal fun rule3(): Rule =
        ((A or A) to { it["A"] }).named("Identity:Or")

internal fun rule4(): Rule =
        ((A and (A or B)) to { it["A"] }).named("Absorption:And")

internal fun rule5(): Rule =
        ((A or (A and B)) to { it["A"] }).named("Absorption:Or")

internal fun rule6(): Rule =
        ((A or T) to { TrueFormula }).named("Zero Law")

internal fun rule7(): Rule =
        ((A and F) to { FalseFormula }).named("Zero Law")

internal fun rule8(): Rule =
        ((A and T) to { it["A"] }).named("Identity Law")

internal fun rule9(): Rule =
        ((A or F) to { it["A"] }).named("Identity Law")

internal fun rule10(): Rule = of(
        (A or !A) to { TrueFormula }, "Excluded Middle"
)

internal fun rule11(): Rule = of(
        (A and !A) to { FalseFormula }, "Contradiction"
)

internal fun rule12(): Rule = of(
        (A implies B) to { (!it["A"]) or it["B"] }, "Imply Equal"
)

internal fun rule13(): Rule = of(
        (A equalTo B) to
                {
                    val a = it["A"]
                    val b = it["B"]
                    (a implies b) and (b implies a)
                }, "Equivalence Equal"
)

internal fun rule14(): Rule = of(
        ((A implies B) and (A implies !B)) to { !it["A"] }, "Absurdity"
)

internal fun rule15(): Rule = of(
        (!(A or B)) to { !it["A"] and !it["B"] }, "De Morgan"
)

internal fun rule16(): Rule = of(
        (!(A and B)) to { !it["A"] or !it["B"] }, "De Morgan"
)

//internal class DistributionRule(val isAnd : Boolean) : AbstractRule(){
//    override val name: String
//        get() = "Distribution"
//    override val symbolRepresentation: String
//    get() = if(isAnd){
//        "(B∨C)∧A ⇒ (A∧B)∨(A∧C)"
//    }else{
//        "(B∧C)∨A ⇒ (A∨B)∧(A∨C)"
//    }
//
//    override fun matches(f: Formula): Boolean {
//        if(f !is AOFormula){
//            return false
//        }
//
//    }
//
//
//
//}
internal fun rule17(): Rule {
    //(B∧C)∨A ⇒ (A∨B)∧(A∨C)
    val bAndC = TypedMatcher("bAc", 2)
    val matcher = bAndC or A
    val replacer: ReplacementBuilder = { re ->
        val bAc = re["bAc"] as AOFormula
        val a = re["A"]
        val list = bAc.fs.map { a or it }
        andAll(list)
    }
    return wrapAndOr("Distribution", matcher, replacer, "(B∧C)∨A ⇒ (A∨B)∧(A∨C)")
}

internal fun rule18(): Rule {
    //(B∨C)∧A ⇒ (A∧B)∨(A∧C)
    val bAndC = TypedMatcher("bOc", 3)
    val matcher = bAndC and A
    val replacer: ReplacementBuilder = { re ->
        val bAc = re["bOc"] as AOFormula
        val a = re["A"]
        val list = bAc.fs.map { a and it }
        orAll(list)
    }
    return wrapAndOr("Distribution", matcher, replacer, "(B∨C)∧A ⇒ (A∧B)∨(A∧C)")
}


fun ruleDeMorgan(): Rule = object : AbstractRule() {
    override val name: String
        get() = "DeMorgan"
    override val symbolRepresentation: String
        get() = "¬(A∧B∧C) ⇒ ¬A∨¬B∨¬C, ¬(A∨B∨C) ⇒ ¬A∧¬B∧¬C"

    override fun matches(f: Formula): Boolean {
        if (f !is NotFormula) {
            return false
        }
        val ao = f.formula
        return ao is AOFormula
    }

    override fun tryApply(f: Formula): Formula? {
        if (!matches(f)) {
            return null
        }
        val t = f as NotFormula
        val ao = t.formula as AOFormula
        return andOr(!ao.isAnd, ao.fs.map { !it })
    }

}