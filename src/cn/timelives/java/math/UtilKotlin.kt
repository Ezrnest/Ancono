package cn.timelives.java.math

import cn.timelives.java.math.algebra.linearAlgebra.Matrix
import cn.timelives.java.math.algebra.linearAlgebra.Vector
import cn.timelives.java.math.geometry.analytic.planeAG.PVector
import cn.timelives.java.math.geometry.analytic.planeAG.Point
import cn.timelives.java.math.geometry.analytic.spaceAG.SPoint
import cn.timelives.java.math.geometry.analytic.spaceAG.SVector
import cn.timelives.java.math.numberModels.Calculators
import cn.timelives.java.math.numberModels.Fraction
import cn.timelives.java.math.numberModels.Multinomial
import cn.timelives.java.math.numberModels.Term
import cn.timelives.java.math.numberModels.structure.Complex
import cn.timelives.java.math.set.Interval

operator fun <T> Vector<T>.get(i: Int) = this.getNumber(i)!!
operator fun <T> Matrix<T>.get(i: Int, j: Int) = this.getNumber(i, j)!!


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
operator fun <T : Any> Complex<T>.component2() = this.im()


operator fun Multinomial.minus(y: Multinomial) = this.subtract(y)!!
operator fun Multinomial.times(y: Multinomial) = this.multiply(y)!!
operator fun Multinomial.times(y: Term) = this.multiply(y)!!
operator fun Multinomial.div(y: Term) = this.divide(y)!!

fun main(args: Array<String>) {
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