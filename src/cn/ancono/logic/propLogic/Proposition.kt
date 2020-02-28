package cn.ancono.logic.propLogic

import java.util.*

/*
 * Created at 2018/9/18
 * @author liyicheng
 */
typealias TruthAssignPredicate = (TruthAssignment) -> Boolean

/**
 * Describes a proposition.
 */
sealed class Proposition : Comparable<Proposition> {
    abstract val variableNames: Set<String>
    abstract fun eval(truthAssignment: TruthAssignment): Boolean
}

fun Boolean.asProposition() = if (this) {
    TrueStatement
} else {
    FalseStatement
}

fun String.asProposition() = SingleVariableProposition(this)


object TrueStatement : Proposition() {
    override val variableNames: Set<String>
        get() = emptySet()

    override fun eval(truthAssignment: TruthAssignment): Boolean = true

    override fun toString(): String {
        return "T"
    }

    override fun compareTo(other: Proposition): Int {
        return if (other is TrueStatement) {
            0
        } else {
            -1
        }
    }
}

object FalseStatement : Proposition() {
    override val variableNames: Set<String>
        get() = emptySet()

    override fun eval(truthAssignment: TruthAssignment): Boolean = false

    override fun toString(): String {
        return "F"
    }

    override fun compareTo(other: Proposition): Int {
        return when (other) {
            is TrueStatement -> 1
            is FalseStatement -> 0
            else -> -1
        }
    }
}

class SimpleProposition(override val variableNames: Set<String>, val predicate: TruthAssignPredicate, propName: String = "") : Proposition() {

    private val propName: String

    init {
        this.propName = if (propName.isBlank()) {
            val rd = Random()
            "p${rd.nextInt(128)}"
        } else {
            propName
        }
    }

    override fun eval(truthAssignment: TruthAssignment): Boolean = predicate(truthAssignment)


    override fun toString(): String {
        return propName
    }

    override fun compareTo(other: Proposition): Int {
        if (other !is SimpleProposition) {
            return 1
        }
        return propName.compareTo(other.propName)
    }


}

class SingleVariableProposition(val variableName: String) : Proposition() {
    override val variableNames: Set<String> = setOf(variableName)

    override fun eval(truthAssignment: TruthAssignment): Boolean {
        return truthAssignment[variableName] ?: throw LackOfAssignmentException(variableName)
    }

    override fun toString(): String {
        return variableName
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SingleVariableProposition) return false

        if (variableName != other.variableName) return false
        if (variableNames != other.variableNames) return false

        return true
    }

    override fun hashCode(): Int {
        var result = variableName.hashCode()
        result = 31 * result + variableNames.hashCode()
        return result
    }

    override fun compareTo(other: Proposition): Int {
        if (other is TrueStatement || other is FalseStatement) {
            return 1
        }
        if (other !is SingleVariableProposition) {
            return -1
        }
        return variableName.compareTo(other.variableName)
    }
}