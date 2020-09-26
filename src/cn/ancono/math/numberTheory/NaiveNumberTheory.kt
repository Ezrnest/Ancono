package cn.ancono.math.numberTheory

import cn.ancono.math.MathUtils
import cn.ancono.math.numberModels.Fraction

object NaiveNumberTheory {

    /**
     * Reduces a positive number to a continuous fraction series.
     */
    fun continuousFractionReduce(x: Double, length: Int = 8): LongArray {
        require(x > 0)
        require(length > 0)
        var x1 = x
        val es = LongArray(length)
        var y = 1.0
        var i = 0
        while (i < length) {
            val reminder = x1 % y
            val l = Math.round((x1 - reminder) / y)
            x1 = y
            y = reminder
            es[i] = l
            i++
        }
        return es
    }

    /**
     * Returns a list containing [n]-th farey sequence.
     */
    fun fareySequenceList(n: Long): List<Fraction> {
        /*
        A property of farey sequence:
        1. Neighbouring terms a/b and c/d in a Farey sequence, with a/b < c/d,
        then c/d - a/b = 1/bd, which is equivalent to that bc-ad = 1, and the converse is also true.
        2. If p/q has neighbours a/b, c/d in some Farey sequence, with a/b < p/q < c/d,
        then p/q = (a+c)/(b+d)


        To compute the farey sequence, we use the property 2.
         */
        val list = ArrayList<Fraction>()
        // a/b, c/d, p/q
        //p = kc -a, q = kd -b
        //k = (n+b)/d
        var a = 0L
        var b = 1L
        var c = 1L
        var d = n
        list.add(Fraction.ZERO)
        while (c <= n) {
            val k = (n + b) / d
            val p = k * c - a
            val q = k * d - b
            a = c
            b = d
            c = p
            d = q
            list.add(Fraction.of(a, b))
        }
        return list

    }

    /**
     * Returns a list containing [n]-th farey sequence.
     */
    fun fareySequence(n: Long): Sequence<Fraction> = sequence {
        /*
        A property of farey sequence:
        1. Neighbouring terms a/b and c/d in a Farey sequence, with a/b < c/d,
        then c/d - a/b = 1/bd, which is equivalent to that bc-ad = 1, and the converse is also true.
        2. If p/q has neighbours a/b, c/d in some Farey sequence, with a/b < p/q < c/d,
        then p/q = (a+c)/(b+d)


        To compute the farey sequence, we use the property 2.
         */
        // a/b, c/d, p/q
        //p = kc -a, q = kd -b
        //k = (n+b)/d
        var a = 0L
        var b = 1L
        var c = 1L
        var d = n
        yield(Fraction.ZERO)
        while (c <= n) {
            val k = (n + b) / d
            val p = k * c - a
            val q = k * d - b
            a = c
            b = d
            c = p
            d = q
            yield(Fraction.of(a, b))
        }

    }

    /**
     * Returns the proper divisors of [n], which are its factors except [n] itself.
     */
    fun properDivisors(n: Long): LongArray {
        val factors = MathUtils.factors(n)
        return factors.sliceArray(0..factors.lastIndex)
    }

    /**
     * A number n is called abundant if the sum of its proper divisors exceeds n.
     */
    fun isAbundant(n: Int) = isAbundant(n.toLong())

    /**
     * A number n is called abundant if the sum of its proper divisors exceeds n.
     */
    fun isAbundant(n: Long): Boolean = MathUtils.factorSum(n) > 2 * n


}

//fun main(args: Array<String>) {
//    val bound = 28123
//    val abundants = (1..bound).filter(NaiveNumberTheory::isAbundant).toSortedSet()
//    println(abundants.take(10))
//    (1..bound).sumBy {n ->
////        for( a : Int in abundants){
////            val b = n - a
////            if(b < a){
////                break
////            }
////            if(abundants.contains(b))
////        }
//        if(abundants.none { abundants.contains(n - it) }){
//            n
//        }else{
//            0
//        }
//    }.apply { println(this) }
//}

