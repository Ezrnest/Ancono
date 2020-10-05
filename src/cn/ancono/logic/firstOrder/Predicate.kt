package cn.ancono.logic.firstOrder

import java.util.*

typealias PredicateFunction = (List<Any>) -> Boolean


/**
 * Describes a predicate in first-order logic.
 * Created at 2018/9/22 19:46
 * @author  liyicheng
 */
abstract class Predicate : Comparable<Predicate> {
    /**
     * The identical name of this predicate, predicates with the same name will be considered as the same.
     */
    abstract val name: String

    /**
     * The notation of this predicate when it is shown in a logic formula, the
     * notation is not required to be identical.
     */
    open val notation: String
        get() = name

    abstract val parameters: List<Class<*>>

    abstract fun test(args: List<Any>): Boolean

    /**
     * Determines whether this predicate is equal to [other]. Returns true
     */
    override fun equals(other: Any?): Boolean {
        if (other !is Predicate) {
            return false
        }
        return name == other.name && parameters == other.parameters
    }

    override fun hashCode(): Int {
        return name.hashCode() * 31 + parameters.hashCode()
    }

    override fun compareTo(other: Predicate): Int {
        return name.compareTo(other.name)
    }

    companion object {
        fun of1(name: String, clazz: Class<*> = Any::class.java, f: PredicateFunction? = null): Predicate {
            if (f == null) {
                return UniversalPredicate(name, 1)
            }
            return PredicateImpl(name, listOf(clazz), f)
        }

        fun of2(name: String, f: PredicateFunction? = null) = if (f == null) {
            UniversalPredicate(name, 2)
        } else {
            PredicateImpl(name, Collections.nCopies(2, Any::class.java), f)
        }


    }


}

class PredicateImpl(override val name: String, override val parameters: List<Class<*>>, val f: PredicateFunction) : Predicate() {
    override fun test(args: List<Any>): Boolean {
        return f(args)
    }
}

object TruePredicate : Predicate() {
    override val name: String
        get() = "T"
    override val parameters: List<Class<*>>
        get() = emptyList()

    override fun test(args: List<Any>): Boolean {
        return true
    }
}


object FalsePredicate : Predicate() {
    override val name: String
        get() = "F"
    override val parameters: List<Class<*>>
        get() = emptyList()

    override fun test(args: List<Any>): Boolean {
        return false
    }
}
//class CurriedPredicate(val origin : Predicate, val ) : Predicate()


class UniversalPredicate(override val name: String, val paraLength: Int)
    : Predicate() {
    override val parameters: List<Class<*>> by lazy { Collections.nCopies(paraLength, Any::class.java) }

    override fun test(args: List<Any>): Boolean {
        throw UnsupportedOperationException("Universal predicate doesn't supports [test]!")
    }


}


val F1 = UniversalPredicate("F", 1)
val F2 = UniversalPredicate("F", 2)
val F3 = UniversalPredicate("F", 3)

val G1 = UniversalPredicate("G", 1)
val G2 = UniversalPredicate("G", 2)
val G3 = UniversalPredicate("G", 3)


val H1 = UniversalPredicate("H", 1)
val H2 = UniversalPredicate("H", 2)
val H3 = UniversalPredicate("H", 3)




