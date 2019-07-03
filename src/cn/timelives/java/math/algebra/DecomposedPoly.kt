package cn.timelives.java.math.algebra

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.numberModels.api.times
import cn.timelives.java.math.numberModels.structure.Polynomial


/**
 * Describes a polynomial that has the form of
 * > (p1)^n1 * (p2)^n2 ... * (p^k)^nk
 * where pi is a polynomial.
 *
 * Created at 2018/12/13 18:51
 * @author  liyicheng
 */
open class DecomposedPoly<T:Any>(val decomposed : List<Pair<Polynomial<T>,Int>>) {
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
                expandedBackingField = decomposed.fold(Polynomial.one(decomposed[0].first.mathCalculator))
                { acc: Polynomial<T>, (p, n) -> acc * p.pow(n) }
            }
            return expandedBackingField!!
        }

    fun <N:Any> map(mapper : (T)->N, newCalculator: MathCalculator<N>) : DecomposedPoly<N>{
        val mp = java.util.function.Function(mapper)
        val re =  DecomposedPoly(decomposed.map { (poly,n)->poly.mapTo(mp,newCalculator) to n })
        re.expandedBackingField = expandedBackingField?.mapTo(mp,newCalculator)
        return re
    }

    override fun toString(): String = decomposed.joinToString(separator = "*") { (x,p) ->
        val t = "($x)"
        if(p == 1){
            t
        }else{
            "$t^$p"
        }
    }
}

/**
 * Describes a polynomial that has the form of
 * > (p)^n
 * where expanded is a polynomial.
 */
class SinglePoly<T:Any>(expanded : Polynomial<T>?, base : Polynomial<T>, pow : Int) : DecomposedPoly<T>(expanded, listOf(base to pow)){

    constructor(base :Polynomial<T>) :this(base,base,1)
    val base : Polynomial<T>
        get() = decomposed[0].first

    val pow : Int
        get() = decomposed[0].second
}