package cn.timelives.java.math.algebra

import cn.timelives.java.math.algebra.abstractAlgebra.calculator.EqualPredicate
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter


/**
 * The single term of a multinomial.
 */
interface IMTerm<T : Any> {
    val characters: Map<String, Int>
    val coefficient: T

    companion object {
        fun <T : Any, MC : EqualPredicate<T>> stringOf(term: IMTerm<T>, mc: MC, nf: FlexibleNumberFormatter<T, MC>): String = buildString {
            append(nf.format(term.coefficient, mc))
            if (term.characters.isNotEmpty()) {
                for ((ch, p) in term.characters) {
                    append('*')
                    append(ch)
                    if (p != 1) {
                        append("^")
                        append(p)
                    }
                }
            }
        }
    }
}

/*
 * Created at 2018/12/12 18:51
 * @author  liyicheng
 */
interface IMultinomial<T : Any> : Iterable<IMTerm<T>> {
    val size: Int

    fun getCoefficient(characters: Map<String, Int>): T?

//    fun degreeOf(ch : String) : Int

    companion object {
        fun <T : Any, MC : EqualPredicate<T>> stringOf(m: IMultinomial<T>, mc: MC, nf: FlexibleNumberFormatter<T, MC>) = m.joinToString("") {
            IMTerm.stringOf(it, mc, nf)
        }
    }
}