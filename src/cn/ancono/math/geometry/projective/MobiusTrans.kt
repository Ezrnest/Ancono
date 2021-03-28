package cn.ancono.math.geometry.projective

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObject
import cn.ancono.math.MathObjectExtend
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.function.Bijection
import cn.ancono.math.geometry.analytic.plane.TransMatrix
import cn.ancono.math.numberModels.api.*
import cn.ancono.math.numberModels.structure.Complex
import cn.ancono.math.numberModels.structure.ComplexH
import cn.ancono.math.property.Composable
import java.util.function.Function

/*
 * Created by liyicheng at 2020-2-24 14:00
 */
/**
 * Describes a mobius transformation on complex field: `T(z) = (az+b)/(cz+d)`
 */
class MobiusTrans<T : Any>(mc: MathCalculator<T>,
                           val cc: MathCalculator<Complex<T>>,
                           val m: TransMatrix<Complex<T>>)
    : MathObjectExtend<T>(mc),
        Bijection<ComplexH<T>, ComplexH<T>>,
        Composable<MobiusTrans<T>>,
        MulGroupNumberModel<MobiusTrans<T>> {


    override fun apply(x: ComplexH<T>): ComplexH<T> {
        if (x.isInf) {
            return ComplexH.of(cc.eval { m[0, 0] / m[1, 0] })
        }
        val z = x.value
        return cc.eval {
            val deno = m[1, 0] * z + m[1, 1]
            if (isZero(deno)) {
                ComplexH.inf()
            } else {
                val nume = m[0, 0] * z + m[0, 1]
                ComplexH.of(nume / deno)
            }
        }
    }

    override fun compose(before: MobiusTrans<T>): MobiusTrans<T> {
        return MobiusTrans(mc, cc, m.compose(before.m))
    }

    override fun andThen(after: MobiusTrans<T>): MobiusTrans<T> {
        return after.compose(this)
    }

    override fun deply(y: ComplexH<T>): ComplexH<T> {
        return reciprocal().apply(y)
    }

    override fun multiply(y: MobiusTrans<T>): MobiusTrans<T> {
        return MobiusTrans(mc, cc, m * y.m)
    }

    override fun reciprocal(): MobiusTrans<T> {
        return MobiusTrans(mc, cc, m.reciprocal())
    }

    override fun inverse(): MobiusTrans<T> {
        return reciprocal()
    }

    fun unitify(): MobiusTrans<T> {
        val det = m.calDet()
        val d = cc.squareRoot(det)
        return MobiusTrans(mc, cc, m.multiplyNumber(d.reciprocal()))
    }

    /**
     * Multiply [k]
     */
    fun idMultiply(k: T): MobiusTrans<T> {
        return idMultiply(Complex.real(k, mc))
    }

    fun idMultiply(k: Complex<T>): MobiusTrans<T> {
        return MobiusTrans(mc, cc, m.multiplyNumber(k))
    }

    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): MobiusTrans<N> {
        val ncc = Complex.getCalculator(newCalculator)
        val nMat = m.mapTo(ncc, Function { z -> z.mapTo(newCalculator, mapper) })
        return MobiusTrans(newCalculator, ncc, nMat)
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is MobiusTrans) {
            return false
        }
        val m2 = obj.m
        val det1 = m.calDet()
        val det2 = m2.calDet()
        return m.multiplyNumber(det2).valueEquals(m2.multiplyNumber(det1))
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
        return "f(x)=( (${m[0, 0].toString(nf)})x + ${m[0, 1].toString(nf)} ) /" +
                "( (${m[1, 0].toString(nf)})x + ${m[1, 1].toString(nf)} )"
    }

    companion object {
        fun <T : Any> of(a: T, b: T, c: T, d: T, mc: MathCalculator<T>): MobiusTrans<T> {
            val det = mc.eval { a * c - b * d }
            if (mc.isZero(det)) {
                throw IllegalArgumentException("ac-bd = 0")
            }
            val cc = Complex.getCalculator(mc)
//            val base = cc.squareRoot(cc.real(det))
//            val a1 = cc.eval { real(a) / base }
//            val b1 = cc.eval { real(b) / base }
//            val c1 = cc.eval { real(c) / base }
//            val d1 = cc.eval { real(d) / base }
            val a1 = cc.real(a)
            val b1 = cc.real(b)
            val c1 = cc.real(c)
            val d1 = cc.real(d)
            val mat = TransMatrix.valueOf(a1, b1, c1, d1, cc)
            return MobiusTrans(mc, cc, mat)
        }

        fun <T : Any> of(a: Complex<T>, b: Complex<T>, c: Complex<T>, d: Complex<T>): MobiusTrans<T> {
            val mc = a.mathCalculator
            val det = a * d - b * c
            if (det.isZero()) {
                throw IllegalArgumentException("ad - bc= 0")
            }
            val cc = Complex.getCalculator(mc)
            val mat = TransMatrix.valueOf(a, b, c, d, cc)
            return MobiusTrans(mc, cc, mat)
        }

        /**
         * Returns a mobius transformation which transforms the three given points to (0,1,Inf), it
         * is required that the given three points are different.
         */
        fun <T : Any> to01Inf(a: ComplexH<T>, b: ComplexH<T>, c: ComplexH<T>): MobiusTrans<T> {
            val mc: MathCalculator<T> = if (a.isInf) {
                if (b.isInf) {
                    throw ArithmeticException()
                }
                b.value.mathCalculator
            } else {
                a.value.mathCalculator
            }
            // (x-b)(a-c)
            // ----------
            // (x-c)(a-b)
            if (a.isInf) {
                // (x-b)
                // -----
                // (x-c)
                val one = Complex.one(mc)
                return of(one, b.value.negate(), one, c.value.negate())
            }
            if (b.isInf) {
                // (a-c)
                // -----
                // (x-c)
                return of(Complex.zero(mc), a.value - c.value, Complex.one(mc), -c.value)
            }
            if (c.isInf) {
                // (x-b)
                // ----------
                // (a-b)
                return of(Complex.one(mc), -b.value, Complex.zero(mc), a.value - b.value)
            }
            return to01Inf(a.value, b.value, c.value)
        }

        fun <T : Any> to01Inf(a: Complex<T>, b: Complex<T>, c: Complex<T>): MobiusTrans<T> {
            val a_c = a - c
            val a_b = a - b
            return of(a_c, -b * a_c, a_b, -c * a_b)
        }


        fun <T : Any> threePoints(a1: ComplexH<T>, b1: ComplexH<T>, c1: ComplexH<T>,
                                  a2: ComplexH<T>, b2: ComplexH<T>, c2: ComplexH<T>): MobiusTrans<T> {
            return to01Inf(a1, b1, c1).andThen(to01Inf(a2, b2, c2).reciprocal())
        }

        fun <T : Any> threePoints(a1: Complex<T>, b1: Complex<T>, c1: Complex<T>,
                                  a2: Complex<T>, b2: Complex<T>, c2: Complex<T>): MobiusTrans<T> {
            return to01Inf(a1, b1, c1).andThen(to01Inf(a2, b2, c2).reciprocal())
        }
    }
}

//fun main(args: Array<String>) {
//    val mc = Calculators.getCalculatorDoubleDev()
//    val a1 = ComplexH.of(mc,0.0,0.0)
//    val b1 = ComplexH.of(mc,1.0,0.0)
//    val c1 = ComplexH.inf<Double>()
//    val a2 = ComplexH.of(mc,1.0,0.0)
//    val b2 = ComplexH.of(mc, 0.0, 1.0)
//    val c2 = ComplexH.of(mc, 0.0, -1.0)
//    val trans = MobiusTrans.threePoints(a1, b1, c1, a2, b2, c2)
//    println(trans.idMultiply(4.0))
//}