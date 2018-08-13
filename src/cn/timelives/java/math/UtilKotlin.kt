package cn.timelives.java.math

import cn.timelives.java.math.algebra.linearAlgebra.Matrix
import cn.timelives.java.math.algebra.linearAlgebra.Vector
import cn.timelives.java.math.geometry.analytic.planeAG.PVector
import cn.timelives.java.math.geometry.analytic.planeAG.Point
import cn.timelives.java.math.geometry.analytic.spaceAG.SPoint
import cn.timelives.java.math.geometry.analytic.spaceAG.SVector
import cn.timelives.java.math.numberModels.structure.Complex

operator fun <T> Vector<T>.get(i: Int) = this.getNumber(i)!!
operator fun <T> Matrix<T>.get(i: Int, j: Int) = this.getNumber(i, j)!!


operator fun <T> Vector<T>.unaryMinus() = this.negative()!!
operator fun <T> PVector<T>.unaryMinus() = this.negative()!!
operator fun <T> SVector<T>.unaryMinus() = this.negative()!!

operator fun <T> Vector<T>.plus(v: Vector<T>) = Vector.addVector(this, v)!!
operator fun <T> PVector<T>.plus(v: PVector<T>) = this.add(v)!!
operator fun <T> SVector<T>.plus(v: SVector<T>) = this.add(v)!!

operator fun <T> Vector<T>.minus(v: Vector<T>) = Vector.subtractVector(this, v)!!
operator fun <T> PVector<T>.minus(v: PVector<T>) = this.add(v)!!
operator fun <T> SVector<T>.minus(v: SVector<T>) = this.add(v)!!

operator fun <T> PVector<T>.component1() = this.x!!
operator fun <T> PVector<T>.component2() = this.y!!

operator fun <T> SVector<T>.component1() = this.x!!
operator fun <T> SVector<T>.component2() = this.y!!
operator fun <T> SVector<T>.component3() = this.z!!

operator fun <T> Point<T>.component1() = this.x!!
operator fun <T> Point<T>.component2() = this.y!!

operator fun <T> SPoint<T>.component1() = this.x!!
operator fun <T> SPoint<T>.component2() = this.y!!
operator fun <T> SPoint<T>.component3() = this.z!!

operator fun <T> Complex<T>.component1() = this.re()!!
operator fun <T> Complex<T>.component2() = this.im()!!


