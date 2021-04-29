package cn.ancono.math

import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.geometry.analytic.plane.PVector
import cn.ancono.math.geometry.analytic.plane.Point
import cn.ancono.math.geometry.analytic.space.SPoint
import cn.ancono.math.geometry.analytic.space.SVector
import cn.ancono.math.numberModels.Multinomial
import cn.ancono.math.numberModels.Term
import cn.ancono.math.numberModels.structure.Complex
import cn.ancono.math.set.MathSet
import cn.ancono.math.set.MathSets

/*
 * Provides extension methods for convenience.
 */

/**
 * Returns the transpose of this matrix.
 */
val <T> Matrix<T>.T: Matrix<T>
    get() = this.transpose()


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


operator fun <T> Matrix<T>.times(v: Vector<T>) = Vector.multiplyToVector(this, v)
operator fun <T> Vector<T>.times(mat: Matrix<T>) = Vector.multiplyByVector(this, mat)

operator fun <T> Complex<T>.component1() = this.re()
operator fun <T> Complex<T>.component2() = this.im()

//operator fun <T> IPolynomial<T>.get(n: Int) = this.get(n)!!
//operator fun Multinomial.minus(y: Multinomial) = this.subtract(y)
//operator fun Multinomial.times(y: Multinomial) = this.multiply(y)
operator fun Multinomial.times(y: Term) = this.multiply(y)!!
operator fun Multinomial.div(y: Term) = this.divide(y)!!

//operator fun <T, S> Pair<T, S>.component1(): T = this.first
//operator fun <T, S> Pair<T, S>.component2(): S = this.second

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

//class CalWrapper<T>(val mc: MathCalculator<T>) {
//    operator fun T.plus(y: T) = mc.add(this, y)
//    operator fun T.minus(y: T) = mc.subtract(this, y)
//    operator fun T.times(y: T) = mc.multiply(this, y)
//    operator fun T.div(y: T) = mc.divide(this, y)
//    operator fun T.times(y: Long) = mc.multiplyLong(this, y)
//    operator fun T.div(y: Long) = mc.divideLong(this, y)
//    operator fun T.compareTo(y: T) = mc.compare(this, y)
//    operator fun T.unaryMinus() = mc.negate(this)
//    operator fun T.rangeTo(y: T) = Interval.closedInterval(this, y, mc)!!
//}
//
//fun <T, R> with(mc: MathCalculator<T>, block: CalWrapper<T>.() -> R): R =
//        block.invoke(CalWrapper(mc))
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
