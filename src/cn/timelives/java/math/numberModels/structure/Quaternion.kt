package cn.timelives.java.math.numberModels.structure

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.MathObject
import cn.timelives.java.math.MathObjectExtend
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.DivisionRingCalculator
import cn.timelives.java.math.geometry.analytic.spaceAG.SVector
import cn.timelives.java.math.numberModels.Calculators
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter
import java.util.function.Function


class Quaternion<T : Any>(mc: MathCalculator<T>, val a: T, val b: T, val c: T, val d: T) : MathObjectExtend<T>(mc) {
    val tensor: T by lazy(LazyThreadSafetyMode.NONE) {
        a * a + b * b + c * c + d * d
    }

    operator fun plus(y: Quaternion<T>): Quaternion<T> {
        return Quaternion(mc, a + y.a, b + y.b, c + y.c, d + y.d)
    }

    operator fun minus(y: Quaternion<T>): Quaternion<T> {
        return Quaternion(mc, a - y.a, b - y.b, c - y.c, d - y.d)
    }

    operator fun times(y: Quaternion<T>): Quaternion<T> {
        /*
     *      / 	1 	i 	j 	k
            1 	1 	i 	j 	k
            i 	i 	−1 	k 	−j
            j 	j 	−k 	−1 	i
            k 	k 	j 	−i 	−1
     */
        val na = a * y.a - b * y.b - c * y.c - d * y.d
        val nb = b * y.a + a * y.b + c * y.d - d * y.c
        val nc = c * y.a + a * y.c - b * y.d + d * y.b
        val nd = d * y.a + a * y.d + b * y.c - c * y.b
        return Quaternion(mc, na, nb, nc, nd)
    }

    operator fun unaryMinus(): Quaternion<T> = Quaternion(mc, -a, -b, -c, -d)

    /**
     * Returns the reciprocal of this quaternion:
     * (a - bi - cj - dk) / tensor, tensor = a^2 + b^2 + c^2 + d^2
     */
    fun reciprocal(): Quaternion<T> {
        return conjugate() / tensor
    }

    /**
     * Returns the norm of this: ||this||
     */
    fun norm(): T {
        return mc.squareRoot(tensor)
    }

    /**
     * Returns the conjugate of this quaternion:
     * a - bi - cj - dk
     */
    fun conjugate(): Quaternion<T> {
        return Quaternion(mc, a, -b, -c, -d)
    }

    operator fun times(y: T): Quaternion<T> {
        return Quaternion(mc, y * a, y * b, y * c, y * d)
    }

    operator fun div(y: T): Quaternion<T> {
        return Quaternion(mc, a / y, b / y, c / y, d / y)
    }

    operator fun div(y: Quaternion<T>): Quaternion<T> {
        return this * y.reciprocal()
    }

    operator fun get(i: Int): T {
        return when (i) {
            0 -> a
            1 -> b
            2 -> c
            3 -> d
            else -> throw IndexOutOfBoundsException(i)
        }
    }

    operator fun component1() = a
    operator fun component2() = b
    operator fun component3() = c
    operator fun component4() = d

    fun vectorPart(): SVector<T> = SVector.valueOf(b, c, d, mc)

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is Quaternion) {
            return false
        }
        return mc.isEqual(a, obj.a) && mc.isEqual(b, obj.b) && mc.isEqual(c, obj.c) && mc.isEqual(d, obj.d)
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String = mc.run {
        if (isZero(a) && isZero(b) && isZero(c) && isZero(d)) {
            return "0"
        }
        return buildString {
            if (!isZero(a)) {
                append(nf.format(a, mc))
                append("+")
            }
            if (!isZero(b)) {
                append("(${nf.format(b, mc)})i+")
            }
            if (!isZero(c)) {
                append("(${nf.format(c, mc)})j+")
            }
            if (!isZero(d)) {
                append("(${nf.format(d, mc)})k+")
            }
            deleteCharAt(length - 1)
        }
    }

    /*
     *      × 	1 	i 	j 	k
            1 	1 	i 	j 	k
            i 	i 	−1 	k 	−j
            j 	j 	−k 	−1 	i
            k 	k 	j 	−i 	−1
     */
    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): Quaternion<N> {
        return Quaternion(newCalculator, mapper.apply(a), mapper.apply(b), mapper.apply(c), mapper.apply(d))
    }

    companion object {

        fun <T : Any> real(a: T, mc: MathCalculator<T>): Quaternion<T> {
            return mc.zero.let { Quaternion(mc, a, it, it, it) }
        }

        fun <T : Any> valueOf(a: T, b: T, c: T, d: T, mc: MathCalculator<T>): Quaternion<T> {
            return Quaternion(mc, a, b, c, d)
        }

        fun <T : Any> zero(mc: MathCalculator<T>): Quaternion<T> {
            return mc.zero.let { Quaternion(mc, it, it, it, it) }
        }

        fun <T : Any> getCalculator(mc: MathCalculator<T>): QuaternionCalculator<T> {
            return QuaternionCalculator(mc)
        }
    }
}

class QuaternionCalculator<T : Any>(val mc: MathCalculator<T>) : DivisionRingCalculator<Quaternion<T>> {
    override val multiplyIsCommutative: Boolean
        get() = false

    override fun reciprocal(x: Quaternion<T>): Quaternion<T> {
        return x.reciprocal()
    }

    override fun divide(x: Quaternion<T>, y: Quaternion<T>): Quaternion<T> {
        return x / y
    }

    override val one: Quaternion<T> = Quaternion.real(mc.one, mc)
    override val zero: Quaternion<T> = Quaternion.zero(mc)

    override fun add(x: Quaternion<T>, y: Quaternion<T>): Quaternion<T> {
        return x + y
    }

    override fun negate(x: Quaternion<T>): Quaternion<T> {
        return -x
    }

    override fun subtract(x: Quaternion<T>, y: Quaternion<T>): Quaternion<T> {
        return x - y
    }

    override fun multiply(x: Quaternion<T>, y: Quaternion<T>): Quaternion<T> {
        return x * y
    }

    override fun isEqual(x: Quaternion<T>, y: Quaternion<T>): Boolean {
        return x.valueEquals(y)
    }


}

//fun main(args: Array<String>) {
//    val mc: MathCalculator<Double> = Calculators.getCalculatorDouble()
//    val q = Quaternion.zero(mc)
//    val (a, b, c, d) = q
//
//}