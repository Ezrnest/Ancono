package cn.ancono.math.algebra

import cn.ancono.math.numberModels.api.NumberFormatter


/**
 * The single term of a multinomial.
 */
interface IMTerm<T> {
    val characters: Map<String, Int>
    val coefficient: T

    companion object {
        fun <T> stringOf(term: IMTerm<T>, nf: NumberFormatter<T>): String = buildString {
            append(nf.format(term.coefficient))
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

        fun <T> stringOf(m: IMultinomial<T>, nf: NumberFormatter<T>) = buildString {
            var start = true
            for (term in m.terms) {
                val s = IMTerm.stringOf(term, nf)
                if (!start && !isPlusOrMinus(s[0]) && !isPlusOrMinus(last())) {
                    append("+")
                }
                append(s)
                start = false
            }
        }
    }
}