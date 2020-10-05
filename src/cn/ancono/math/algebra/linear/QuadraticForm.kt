package cn.ancono.math.algebra.linear

import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.Multinomial


/*
 * Created at 2019/4/2 19:23
 * @author  liyicheng
 */

class QuadraticForm {
    companion object {
        /**
         * Returns the representation matrix of the given multinomial. The characters are in lexicographical order.
         */
        @JvmStatic
        fun representationMatrix(q: Multinomial): Matrix<Fraction> {
            val chars = q.characters
            val chMap = hashMapOf<String, Int>()
            for ((i, ch) in chars.withIndex()) {
                chMap[ch] = i
            }
            val size = chars.size
            val builder = Matrix.getBuilder(size, size, Fraction.calculator)
            for (term in q.terms) {
                if (!term.isRational) {
                    throw IllegalArgumentException("Not rational")
                }
                val f = term.toFraction()
                val tch = term.character
                when {
                    tch.size == 1 -> {
                        val en = tch.firstEntry()
                        if (en.value != Fraction.TWO) {
                            throw IllegalArgumentException("Not quadratic form!")
                        }
                        val idx = chMap[en.key]!!
                        builder.set(f, idx, idx)
                    }
                    tch.size == 2 -> {
                        val en1 = tch.firstEntry()
                        val en2 = tch.lastEntry()
                        if (en1.value != Fraction.ONE || en2.value != Fraction.ONE) {
                            throw IllegalArgumentException("Not quadratic form!")
                        }
                        val i = chMap[en1.key]!!
                        val j = chMap[en2.key]!!
                        val half = f / 2
                        builder.set(half, i, j)
                        builder.set(half, j, i)
                    }
                    else -> throw IllegalArgumentException("Not quadratic form!")
                }
            }
            return builder.build()
        }

//        fun
    }
}