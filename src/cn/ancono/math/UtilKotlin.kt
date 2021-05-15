package cn.ancono.math

import cn.ancono.math.geometry.analytic.plane.PVector
import cn.ancono.math.geometry.analytic.plane.Point
import cn.ancono.math.geometry.analytic.space.SPoint
import cn.ancono.math.geometry.analytic.space.SVector
import cn.ancono.math.numberModels.ComplexD
import cn.ancono.math.numberModels.Multinomial
import cn.ancono.math.numberModels.Term
import cn.ancono.math.numberModels.structure.Complex
import cn.ancono.math.set.MathSet
import cn.ancono.math.set.MathSets

/*
 * Provides extension methods for convenience.
 */


operator fun ComplexD.times(k: Double): ComplexD = this.multiply(k)
operator fun ComplexD.div(k: Double): ComplexD = this.divide(k)
operator fun ComplexD.plus(y: Double): ComplexD = this.add(ComplexD.real(y))
operator fun ComplexD.minus(y: Double): ComplexD = this.subtract(ComplexD.real(y))

operator fun <T> PVector<T>.component1(): T = this.x
operator fun <T> PVector<T>.component2(): T = this.y

operator fun <T> SVector<T>.component1(): T = this.x
operator fun <T> SVector<T>.component2(): T = this.y
operator fun <T> SVector<T>.component3(): T = this.z

operator fun <T> Point<T>.component1(): T = this.x
operator fun <T> Point<T>.component2(): T = this.y

operator fun <T> SPoint<T>.component1(): T = this.x
operator fun <T> SPoint<T>.component2(): T = this.y
operator fun <T> SPoint<T>.component3(): T = this.z

operator fun <T> Complex<T>.component1() = this.re()
operator fun <T> Complex<T>.component2() = this.im()


operator fun Multinomial.times(y: Term) = this.multiply(y)!!
operator fun Multinomial.div(y: Term) = this.divide(y)!!


operator fun <T> MathSet<T>.minus(another: MathSet<T>): MathSet<T> =
        MathSet { this.contains(it) && !another.contains(it) }

infix fun <T> MathSet<T>.intersect(another: MathSet<T>): MathSet<T> = MathSets.intersectOf(this, another)
infix fun <T> MathSet<T>.and(another: MathSet<T>): MathSet<T> = this intersect another
infix fun <T> MathSet<T>.union(another: MathSet<T>): MathSet<T> = MathSets.unionOf(this, another)
infix fun <T> MathSet<T>.or(another: MathSet<T>): MathSet<T> = this union another

fun <T> List<T>.exclude(idx: Int): List<T> {
    val result = ArrayList<T>(this.size - 1)
    for (n in 0 until idx) {
        result += this[n]
    }
    for (n in (idx + 1) until this.size) {
        result += this[n]
    }
    return result
}

fun <T : Comparable<T>> List<T>.isSorted(invertedNaturalOrder: Boolean = false): Boolean {
    if (this.size < 2) {
        return true
    }
    for (n in 1 until size) {
        if (invertedNaturalOrder) {
            if (this[n - 1] < this[n]) {
                return false
            }
        } else {
            if (this[n - 1] > this[n]) {
                return false
            }
        }
    }
    return true
}


//
//fun main(args: Array<String>) {
//    val mc = Fraction.calculator
//    with(mc){
//        Fraction.ONE
//    }
//}

//fun main(args: Array<String>) {
////    for(n in 1..10000){
////        println(MathUtils.squareRootExact(n.toLong()))
////    }
//
//    val size = 10_0000
////    Primes.getInstance().enlargePrime(1000000)
////    val map = TreeMap<Long,List<Int>>()
//    (1..size).asSequence()
//            .map { it -> it to MathUtils.factorSum(it.toLong()) }
//            .fold(TreeMap<Long, List<Int>>()) { map, p ->
//                CollectionSup.accumulateMap(map, p.second, p.first) { ArrayList() }
//                map
//            }.forEach { t, u ->
//                if (u.size > 1 && u.all {
//                            MathUtils.isPerfectSquare(it.toLong())
//                        }) {
//                    println("$t : ${u.joinToString()} = ${u.map { MathUtils.sqrtIntL(it.toLong()).toInt() }.joinToString()}")
//                }
//            }
//}
