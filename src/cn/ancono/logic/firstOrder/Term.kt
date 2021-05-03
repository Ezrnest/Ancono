package cn.ancono.logic.firstOrder

import cn.ancono.utilities.CollectionSup

typealias TermFormatter = ((List<Term>) -> String)

val String.t: IndividualTerm
    get() = IndividualTerm(this, Any::class.java)

val a = "a".t
val b = "b".t
val c = "c".t
val d = "d".t
val x = "x".t
val y = "y".t
val z = "z".t
val w = "w".t

val f = TermBuilder("f")
val g = TermBuilder("g")
val h = TermBuilder("h")

sealed class Term : Comparable<Term> {
    abstract override fun toString(): String

    abstract val individualTerms: Set<IndividualTerm>

    companion object {
        fun functionOf(name: String, formatter: TermFormatter? = null) = TermBuilder(name, formatter)

    }
}

data class FunctionTerm(val fName: String, val subTerms: List<Term>,
                        private val formatter: ((List<Term>) -> String)? = null) : Term() {

    override fun toString(): String = formatter?.invoke(subTerms) ?: buildString {
        append(fName)
        subTerms.joinTo(this, ",", "(", ")")
    }

    override val individualTerms: Set<IndividualTerm> by lazy {
        subTerms.flatMapTo(hashSetOf()) {
            it.individualTerms
        }
    }


    override fun compareTo(other: Term): Int {
        if (other is IndividualTerm) {
            return 1
        }
        val ft = other as FunctionTerm
        val comp = fName.compareTo(ft.fName)
        if (comp != 0) {
            return comp
        }
        return CollectionSup.compareCollection(subTerms, other.subTerms)
    }
}


/**
 * Describes a single predicate in first ordered logic.
 * Created at 2018/9/22 19:40
 * @author  liyicheng
 */
class IndividualTerm(val name: String, val type: Class<*>) : Term() {
    override val individualTerms: Set<IndividualTerm>
        get() = setOf(this)

    fun copy(name: String): IndividualTerm {
        return IndividualTerm(name, type)
    }


    override fun toString(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        return other is IndividualTerm && (name == other.name)
    }


    override fun compareTo(other: Term): Int {
        if (other is FunctionTerm) {
            return -1
        }
        return name.compareTo((other as IndividualTerm).name)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

class TermBuilder(val name: String, private val formatter: TermFormatter? = null) {
    operator fun invoke(vararg ts: Term): Term = if (ts.isEmpty()) {
        name.t
    } else {
        FunctionTerm(name, ts.toList(), formatter)
    }
}