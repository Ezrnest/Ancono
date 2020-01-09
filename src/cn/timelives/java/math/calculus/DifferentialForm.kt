package cn.timelives.java.math.calculus

import cn.timelives.java.math.MathSymbol
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.AlgebraCalculator
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.FieldCalculator
import cn.timelives.java.math.numberModels.api.times
import cn.timelives.java.math.numberModels.expression.ExprCalculator
import cn.timelives.java.math.numberModels.expression.Expression
import cn.timelives.java.math.numberModels.structure.AlgebraMultinomial
import cn.timelives.java.utilities.ArraySup
import cn.timelives.java.utilities.CollectionSup
import java.lang.StringBuilder
import java.util.*
import kotlin.Comparator

/**
 * Created at 2019/9/14 14:59
 * @author  lyc
 */
class DFBase internal constructor(
        val coefficient: Expression,
        val bases: Array<String>
) {

    override fun toString(): String {
        if (DFBaseCalculator.ec.isZero(coefficient)) {
            return "0"
        }
        return "($coefficient)${bases.joinToString(MathSymbol.LOGIC_AND) {
            "d$it"
        }
        }"
    }

    companion object {
        internal fun <T : Comparable<T>> inverseOrderCountAndCheckDuplicate(list: Array<T>): Int {
            var count = 0
            for (i in list.indices) {
                val x = list[i]
                for (j in i + 1 until list.size) {
                    val y = list[j]
                    val c = x.compareTo(y)
                    if (c > 0) {
                        count++
                    } else if (c == 0) {
                        return -1
                    }
                }
            }
            return count
        }

        private fun valueOf0(coe: Expression, bases: Array<String>): DFBase {
            val invCount = inverseOrderCountAndCheckDuplicate(bases)
            if (invCount < 0) {
                return DFBaseCalculator.zero
            }
            Arrays.sort(bases)
            return DFBase(coe, bases)
        }

        @Suppress("UNCHECKED_CAST")
        fun valueOf(coe: Expression, vararg bases: String): DFBase {
            val arr = Array(bases.size) { bases[it] }
            return valueOf0(coe, arr)
        }

        fun valueOf(coe: Expression, bases: List<String>): DFBase {
            return valueOf(coe, *bases.toTypedArray())
        }
    }
}

object DFBaseComparator : Comparator<DFBase> {
    override fun compare(o1: DFBase, o2: DFBase): Int {
        return Arrays.compare(o1.bases, o2.bases)
    }
}

object DFBaseCalculator : AlgebraCalculator<Expression, DFBase> {
    val ec = ExprCalculator.instance
    override val scalarCalculator: FieldCalculator<Expression>
        get() = ec

    override fun scalarMultiply(k: Expression, v: DFBase): DFBase {
        val t = ec.multiply(k, v.coefficient)
        if (ec.isZero(t)) {
            return zero
        }
        return DFBase(t, v.bases)
    }

    override fun isEqual(x: DFBase, y: DFBase): Boolean {
        return x.bases.contentEquals(y.bases) && ec.isEqual(x.coefficient, y.coefficient)
    }

    override val zero: DFBase = DFBase(ec.zero, emptyArray())

    override fun add(x: DFBase, y: DFBase): DFBase {
        if (ec.isZero(x.coefficient)) {
            return y
        }
        if (ec.isZero(y.coefficient)) {
            return x
        }
        return DFBase(ec.add(x.coefficient, y.coefficient), x.bases)
    }

    override fun negate(x: DFBase): DFBase {
        return DFBase(ec.negate(x.coefficient), x.bases)
    }

    @Suppress("UNCHECKED_CAST")
    override fun multiply(x: DFBase, y: DFBase): DFBase {
        if (ec.isZero(x.coefficient) || ec.isZero(y.coefficient)) {
            return zero
        }
        val b1 = x.bases
        val b2 = y.bases
        val nBases = Array(b1.size + b2.size){""}
        var i1 = 0
        var i2 = 0
        var i = 0
        var invCount = 0
        while (i1 < b1.size && i2 < b2.size) {
            val comp = b1[i1].compareTo(b2[i2])
            when {
                comp < 0 -> {
                    nBases[i++] = b1[i1++]
                    invCount += i2
                }
                comp == 0 -> return zero
                else -> {
                    nBases[i++] = b2[i2++]
                }
            }
        }
        if (i1 < b1.size) {
            do {
                nBases[i++] = b1[i1++]
                invCount += i2
            } while (i1 < b1.size)
        } else {
            while (i2 < b2.size) {
                nBases[i++] = b2[i2++]
            }
        }
        val coe = ec.multiply(x.coefficient, y.coefficient)
        return if (invCount % 2 == 1) {
            DFBase(ec.negate(coe), nBases)
        } else {
            DFBase(coe, nBases)
        }
    }

    override fun isLinearRelevant(u: DFBase, v: DFBase): Boolean {
        return ec.isZero(u.coefficient) || ec.isZero(v.coefficient) || u.bases.contentEquals(v.bases)
    }
}

class DifferentialForm internal constructor(terms: NavigableSet<DFBase>)
    : AlgebraMultinomial<Expression, DFBase>(DFBaseCalculator, terms) {


    fun differential(vararg variables: String): DifferentialForm {
        return differential(variables.toList())
    }

    private fun diffSingle(term: DFBase, variables: List<String>, re: NavigableSet<DFBase>) {
        val coe = term.coefficient
        val bases = term.bases
        for (v in variables) {
            var idx = bases.binarySearch(v)
            if (idx >= 0) {
                continue
            }
            idx = -idx - 1
            var nCoe = DFBaseCalculator.ec.differential(coe, v)
            if (idx % 2 == 1) {
                nCoe = DFBaseCalculator.ec.negate(nCoe)
            }
            val nBase = Array(bases.size + 1) { "" }
            System.arraycopy(bases, 0, nBase, 0, idx)
            nBase[idx] = v
            System.arraycopy(bases, idx, nBase, idx + 1, bases.size - idx)

            mergingAdd(re, DFBase(nCoe, nBase))
        }
    }

    fun differential(variables: List<String> = DefaultVariables): DifferentialForm {
        val re = getTS()
        for (t in terms) {
            diffSingle(t, variables, re)
        }
        return DifferentialForm(re)
    }

    companion object {
        val DefaultVariables = listOf("x", "y", "z")
        internal fun getSet(): NavigableSet<DFBase> {
            return TreeSet(DFBaseComparator)
        }

        fun valueOf(coe: Expression, vararg bases: String): DifferentialForm {
            val t = DFBase.valueOf(coe, *bases)
            val terms = getSet()
            terms.add(t)
            return DifferentialForm(terms)
        }

    }


}

fun main(args: Array<String>) {
    val ec = ExprCalculator.instance
    val vars = listOf("x","y","z","w")
    val w = DifferentialForm.valueOf(ec.parseExpr("xyzw"))
    val v = DifferentialForm.valueOf(ec.parseExpr("x+y+z+w"))
    println(w)
    println(v)
    val w2 = w.differential(vars)
    println(w2)
    val v2 = v.differential(vars)
    println(v2)
    println(v2 * w2)
    println(w2 * v2)
    println(w2.differential(vars))
    println(v2.differential(vars))
}