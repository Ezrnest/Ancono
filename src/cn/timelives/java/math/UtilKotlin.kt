package cn.timelives.java.math

import cn.timelives.java.math.algebra.linearAlgebra.Matrix
import cn.timelives.java.math.algebra.linearAlgebra.Vector
import cn.timelives.java.math.geometry.analytic.planeAG.PVector
import cn.timelives.java.math.geometry.analytic.planeAG.Point
import cn.timelives.java.math.geometry.analytic.spaceAG.SPoint
import cn.timelives.java.math.geometry.analytic.spaceAG.SVector
import cn.timelives.java.math.numberModels.Multinomial
import cn.timelives.java.math.numberModels.Term
import cn.timelives.java.math.numberModels.structure.Complex
import cn.timelives.java.math.set.MathSet
import cn.timelives.java.math.set.MathSets
import cn.timelives.java.utilities.CollectionSup
import java.util.*
import kotlin.collections.ArrayList

operator fun <T> Vector<T>.get(i: Int) = this.getNumber(i)!!
operator fun <T> Matrix<T>.get(i: Int, j: Int) = this.getNumber(i, j)!!

operator fun <T> Matrix<T>.times(mat : Matrix<T>) = Matrix.multiplyMatrix(this,mat)!!
operator fun <T> Matrix<T>.minus(mat : Matrix<T>) = Matrix.minusMatrix(this,mat)!!
operator fun <T> Matrix<T>.plus(mat : Matrix<T>) = Matrix.addMatrix(this,mat)!!
operator fun <T> Matrix<T>.unaryMinus() = this.negative()!!
operator fun <T : Any> Vector<T>.unaryMinus() = this.negative()!!

operator fun <T : Any> PVector<T>.unaryMinus() = this.negative()!!
operator fun <T : Any> SVector<T>.unaryMinus() = this.negative()!!
operator fun <T : Any> Vector<T>.plus(v: Vector<T>) = Vector.addVector(this, v)!!

operator fun <T : Any> PVector<T>.plus(v: PVector<T>) = this.add(v)!!
operator fun <T : Any> SVector<T>.plus(v: SVector<T>) = this.add(v)!!
operator fun <T : Any> Vector<T>.minus(v: Vector<T>) = Vector.subtractVector(this, v)!!

operator fun <T : Any> PVector<T>.minus(v: PVector<T>) = this.add(v)!!
operator fun <T : Any> SVector<T>.minus(v: SVector<T>) = this.subtract(v)!!
operator fun <T : Any> SVector<T>.times(k: T) = this.multiplyNumber(k)!!

operator fun <T : Any> PVector<T>.times(k: T) = this.multiplyNumber(k)!!
operator fun <T : Any> Vector<T>.times(k: T) = this.multiplyNumber(k)!!
operator fun <T : Any> PVector<T>.component1() = this.x!!


operator fun <T : Any> PVector<T>.component2() = this.y!!
operator fun <T : Any> SVector<T>.component1() = this.x!!

operator fun <T : Any> SVector<T>.component2() = this.y!!
operator fun <T : Any> SVector<T>.component3() = this.z!!
operator fun <T : Any> Point<T>.component1() = this.x!!

operator fun <T : Any> Point<T>.component2() = this.y!!
operator fun <T : Any> SPoint<T>.component1() = this.x!!

operator fun <T : Any> SPoint<T>.component2() = this.y!!
operator fun <T : Any> SPoint<T>.component3() = this.z!!
operator fun <T : Any> Complex<T>.component1() = this.re()

operator fun <T> Matrix<T>.times(v : Vector<T>) = Vector.multiplyToVector(this,v)!!
operator fun <T> Vector<T>.times(mat : Matrix<T>) = Vector.multiplyByVector(this,mat)!!

operator fun <T : Any> Complex<T>.component2() = this.im()


operator fun Multinomial.minus(y: Multinomial) = this.subtract(y)!!
operator fun Multinomial.times(y: Multinomial) = this.multiply(y)!!
operator fun Multinomial.times(y: Term) = this.multiply(y)!!
operator fun Multinomial.div(y: Term) = this.divide(y)!!


operator fun <T:Any> MathSet<T>.minus(another : MathSet<T>) : MathSet<T> =
        MathSet { this.contains(it) && !another.contains(it) }

infix fun <T:Any> MathSet<T>.intersect(another: MathSet<T>) : MathSet<T> = MathSets.intersectOf(this,another)
infix fun <T:Any> MathSet<T>.and(another: MathSet<T>) : MathSet<T> = this intersect another
infix fun <T:Any> MathSet<T>.union(another: MathSet<T>) : MathSet<T> = MathSets.unionOf(this,another)
infix fun <T:Any> MathSet<T>.or(another: MathSet<T>) : MathSet<T> = this union another

fun <T> List<T>.exclude(idx : Int) : List<T>{
    val result = ArrayList<T>(this.size-1)
    for(n in 0 until idx){
        result += this[n]
    }
    for(n in (idx+1) until this.size){
        result += this[n]
    }
    return result
}

fun <T : Comparable<T>> List<T>.isSorted(inversedNaturalOrder : Boolean = false) : Boolean{
    if(this.size < 2){
        return true
    }
    for(n in 1 until size){
        if(inversedNaturalOrder){
            if(this[n-1] < this[n]){
                return false
            }
        }else{
            if(this[n-1] > this[n]){
                return false
            }
        }
    }
    return true
}

//class CalWrapper<T : Any>(val mc: MathCalculator<T>) {
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
//fun <T : Any, R> with(mc: MathCalculator<T>, block: CalWrapper<T>.() -> R): R =
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
