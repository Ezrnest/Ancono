package cn.ancono.math.algebra

import cn.ancono.math.MathCalculator
import cn.ancono.math.numberModels.structure.Polynomial


/**
 * Describes a polynomial that has the form of
 * > (p1)^n1 * (p2)^n2 ... * (p^k)^nk
 * where pi is a polynomial.
 *
 * Created at 2018/12/13 18:51
 * @author  liyicheng
 */
open class DecomposedPoly<T>(val decomposed: List<Pair<Polynomial<T>, Int>>) {
    private var expandedBackingField: Polynomial<T>? = null

    constructor(expanded: Polynomial<T>?, decomposed: List<Pair<Polynomial<T>, Int>>) : this(decomposed) {
        expandedBackingField = expanded
    }

    /**
     * The expanded form of this polynomial.
     */
    val expanded: Polynomial<T>
        get() {
            if (expandedBackingField == null) {
                expandedBackingField = decomposed.asSequence().map { (p, n) ->
                    p.pow(n.toLong())
                }.reduce(Polynomial<T>::add)
            }
            return expandedBackingField!!
        }

    val degree: Int
        get() {
            return decomposed.sumBy { (p, n) -> p.degree * n }
        }

    fun compute(x: T): T {
        val mc = decomposed.first().first.calculator
//        var r = mc.one
        return decomposed.asSequence().map { (p, n) ->
            mc.pow(p.compute(x), n.toLong())
        }.reduce(mc::multiply)
    }


    fun <N> map(newCalculator: MathCalculator<N>, mapper: (T) -> N): DecomposedPoly<N> {
        val mp = java.util.function.Function(mapper)
        val re = DecomposedPoly(decomposed.map { (poly, n) -> poly.mapTo(newCalculator, mp) to n })
        re.expandedBackingField = expandedBackingField?.mapTo(newCalculator, mp)
        return re
    }

    override fun toString(): String = decomposed.joinToString(separator = "*") { (x, p) ->
        val t = "($x)"
        if (p == 1) {
            t
        } else {
            "$t^$p"
        }
    }
}

/**
 * Describes a polynomial that has the form of
 * > (p)^n
 * where expanded is a polynomial.
 */
class SinglePoly<T>(expanded: Polynomial<T>?, base: Polynomial<T>, pow: Int) : DecomposedPoly<T>(expanded, listOf(base to pow)) {

    constructor(base: Polynomial<T>) : this(base, base, 1)

    val base: Polynomial<T>
        get() = decomposed[0].first

    val pow: Int
        get() = decomposed[0].second
}