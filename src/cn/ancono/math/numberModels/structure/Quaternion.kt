package cn.ancono.math.numberModels.structure

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObject
import cn.ancono.math.MathObjectExtend
import cn.ancono.math.algebra.abs.FiniteGroups
import cn.ancono.math.algebra.abs.calculator.DivisionRingCalculator
import cn.ancono.math.algebra.abs.calculator.asGroupCalculator
import cn.ancono.math.algebra.abs.group.finite.AbstractFiniteGroup
import cn.ancono.math.geometry.analytic.space.SVector
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import java.util.function.Function


class Quaternion<T>(val a: T, val b: T, val c: T, val d: T, mc: MathCalculator<T>) : MathObjectExtend<T>(mc) {
    val tensor: T by lazy(LazyThreadSafetyMode.NONE) {
        a * a + b * b + c * c + d * d
    }

    fun real(): Quaternion<T> {
        return real(a, mc)
    }

    operator fun plus(y: Quaternion<T>): Quaternion<T> {
        return Quaternion(a + y.a, b + y.b, c + y.c, d + y.d, mc)
    }

    operator fun minus(y: Quaternion<T>): Quaternion<T> {
        return Quaternion(a - y.a, b - y.b, c - y.c, d - y.d, mc)
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
        return Quaternion(na, nb, nc, nd, mc)
    }

    operator fun unaryMinus(): Quaternion<T> = Quaternion(-a, -b, -c, -d, mc)

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
        return Quaternion(a, -b, -c, -d, mc)
    }

    operator fun times(y: T): Quaternion<T> {
        return Quaternion(y * a, y * b, y * c, y * d, mc)
    }

    operator fun div(y: T): Quaternion<T> {
        return Quaternion(a / y, b / y, c / y, d / y, mc)
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
    override fun <N> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): Quaternion<N> {
        return Quaternion(mapper.apply(a), mapper.apply(b), mapper.apply(c), mapper.apply(d), newCalculator)
    }

    companion object {

        fun <T> real(a: T, mc: MathCalculator<T>): Quaternion<T> {
            return mc.zero.let { Quaternion(a, it, it, it, mc) }
        }

        fun <T> valueOf(a: T, b: T, c: T, d: T, mc: MathCalculator<T>): Quaternion<T> {
            return Quaternion(a, b, c, d, mc)
        }

        fun <T> parse(str: String, mc: MathCalculator<T>, deliminator: Regex = Regex(","), parser: (String) -> T): Quaternion<T> {
            val arr = deliminator.split(str).map(parser)
            return valueOf(arr[0], arr[1], arr[2], arr[3], mc)
        }

        fun <T> zero(mc: MathCalculator<T>): Quaternion<T> {
            return mc.zero.let { Quaternion(it, it, it, it, mc) }
        }

        fun <T> one(mc: MathCalculator<T>): Quaternion<T> {
            return mc.run { Quaternion(one, zero, zero, zero, mc) }
        }

        fun <T> baseI(mc: MathCalculator<T>): Quaternion<T> {
            return mc.run { Quaternion(zero, one, zero, zero, mc) }
        }

        fun <T> baseJ(mc: MathCalculator<T>): Quaternion<T> {
            return mc.run { Quaternion(zero, zero, one, zero, mc) }
        }

        fun <T> baseK(mc: MathCalculator<T>): Quaternion<T> {
            return mc.run { Quaternion(zero, zero, zero, one, mc) }
        }


        fun <T> getCalculator(mc: MathCalculator<T>): QuaternionCalculator<T> {
            return QuaternionCalculator(mc)
        }

        /**
         * Returns the quaternion eight-group, whose elements are `1,-1,i,j,k,-i,-j,-k` and
         * the group operation is multiplication.
         */
        fun <T> quaternionGroup(mc: MathCalculator<T>): AbstractFiniteGroup<Quaternion<T>> {
            val qc = getCalculator(mc)
            val gc = qc.asGroupCalculator()
            val e = one(mc)
            val i = baseI(mc)
            val j = baseJ(mc)
            val k = baseK(mc)
            val _e = -e
            val _i = -i
            val _j = -j
            val _k = -k
            return FiniteGroups.createGroupWithoutCheck(gc, e, i, j, k, _e, _i, _j, _k)
        }


    }
}

class QuaternionCalculator<T>(val mc: MathCalculator<T>) : DivisionRingCalculator<Quaternion<T>> {
    override val isMultiplyCommutative: Boolean
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