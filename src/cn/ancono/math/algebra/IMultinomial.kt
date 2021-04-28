package cn.ancono.math.algebra

import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter


/**
 * The single term of a multinomial.
 */
interface IMTerm<T> {
    val characters: Map<String, Int>
    val coefficient: T

    companion object {
        fun <T, MC : EqualPredicate<T>> stringOf(term: IMTerm<T>, mc: MC, nf: FlexibleNumberFormatter<T, MC>): String = buildString {
            append(nf.format(term.coefficient, mc))
            if (term.characters.isNotEmpty()) {
                var hasMul = true
                append('*')
                for ((ch, p) in term.characters) {
                    if (ch.length > 1 && !hasMul) {
                        append('*')
                    }
                    append(ch)
                    if (p != 1) {
                        append("^")
                        append(p)
                    }
                    hasMul = if (ch.length > 1 || p != 1) {
                        append('*')
                        true
                    } else {
                        false
                    }
                }
                if (hasMul) {
                    deleteCharAt(lastIndex)
                }
            }
        }
    }
}

/*
 * Created at 2018/12/12 18:51
 * @author  liyicheng
 */
interface IMultinomial<T> {
    val terms: Iterable<IMTerm<T>>

    val size: Int

    fun getCoefficient(characters: Map<String, Int>): T?

//    fun degreeOf(ch : String) : Int

    companion object {
        fun isPlusOrMinus(c: Char): Boolean {
            return c == '+' || c == '-'
        }

        fun <T, MC : EqualPredicate<T>> stringOf(m: IMultinomial<T>, mc: MC, nf: FlexibleNumberFormatter<T, MC>) = buildString {
            var start = true
            for (term in m.terms) {
                val s = IMTerm.stringOf(term, mc, nf)
                if (!start && !isPlusOrMinus(s[0]) && !isPlusOrMinus(last())) {
                    append("+")
                }
                append(s)
                start = false
            }
        }
    }
}