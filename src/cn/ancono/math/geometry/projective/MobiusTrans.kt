package cn.ancono.math.geometry.projective

import cn.ancono.math.AbstractFlexibleMathObject
import cn.ancono.math.FMathObject
import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.function.Bijection
import cn.ancono.math.geometry.analytic.plane.TransMatrix
import cn.ancono.math.numberModels.api.*
import cn.ancono.math.numberModels.structure.Complex
import cn.ancono.math.numberModels.structure.ComplexE
import cn.ancono.math.property.Composable
import org.jetbrains.annotations.NotNull
import java.util.function.Function

/*
 * Created by liyicheng at 2020-2-24 14:00
 */
/**
 * Describes a mobius transformation on complex field: `T(z) = (az+b)/(cz+d)`
 */
class MobiusTrans<T>(
        mc: FieldCalculator<T>,
        /**
         * The complex calculator
         */
        val cc: RealCalculator<Complex<T>>,
        /**
         * The transformation matrix ((a,b),(c,d))
         */
        val m: TransMatrix<Complex<T>>
) : AbstractFlexibleMathObject<T, FieldCalculator<T>>(mc),
        Bijection<ComplexE<T>, ComplexE<T>>,
        Composable<MobiusTrans<T>>,
        MulGroupNumberModel<MobiusTrans<T>> {


    override fun apply(x: ComplexE<T>): ComplexE<T> {
        if (x !is Complex) return (cc.eval { m[0, 0] / m[1, 0] })
        return cc.eval {
            val deno = m[1, 0] * x + m[1, 1]
            if (isZero(deno)) {
                Complex.inf(calculator)
            } else {
                val nume = m[0, 0] * x + m[0, 1]
                nume / deno
            }
        }
    }


    override fun compose(before: MobiusTrans<T>): MobiusTrans<T> {
        return MobiusTrans(calculator, cc, m.compose(before.m))
    }

    override fun andThen(after: MobiusTrans<T>): MobiusTrans<T> {
        return after.compose(this)
    }

    override fun deply(y: @NotNull ComplexE<T>): ComplexE<T> {
        return reciprocal().apply(y)
    }

    override fun multiply(y: MobiusTrans<T>): MobiusTrans<T> {
        return MobiusTrans(calculator, cc, m * y.m)
    }

    override fun reciprocal(): MobiusTrans<T> {
        return MobiusTrans(calculator, cc, m.inverse())
    }

    override fun inverse(): MobiusTrans<T> {
        return reciprocal()
    }

    /**
     * Returns an equivalent Mobius transformation whose determinant of the transformation matrix is one.
     */
    fun unitize(): MobiusTrans<T> {
        val det = m.det()
        val d = cc.squareRoot(det)
        return MobiusTrans(calculator, cc, m.multiply(d.reciprocal()))
    }

    /**
     * Multiply [k] to both numerator and the denominator of this mobius transformation. This method will
     * return an equivalent Mobius transformation.
     */
    fun idMultiply(k: T): MobiusTrans<T> {
        return idMultiply(Complex.real(k, calculator))
    }

    /**
     * Multiply [k] to both numerator and the denominator of this mobius transformation. This method will
     * return an equivalent Mobius transformation.
     */
    fun idMultiply(k: Complex<T>): MobiusTrans<T> {
        return MobiusTrans(calculator, cc, m.multiply(k))
    }


    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): MobiusTrans<N> {
        val ncc = Complex.calculator(newCalculator as FieldCalculator<N>)
        val nMat = m.mapTo(ncc) { z -> z.mapTo(newCalculator, mapper) }
        return MobiusTrans(newCalculator, ncc, nMat)
    }

    override fun valueEquals(obj: FMathObject<T, FieldCalculator<T>>): Boolean {
        if (obj !is MobiusTrans) {
            return false
        }
        val m2 = obj.m
        val det1 = m.det()
        val det2 = m2.det()
        return m.multiply(det2).valueEquals(m2.multiply(det1))
    }

    override fun toString(nf: FlexibleNumberFormatter<T>): String {
        return "f(x)=( (${m[0, 0].toString(nf)})x + ${m[0, 1].toString(nf)} ) /" +
                "( (${m[1, 0].toString(nf)})x + ${m[1, 1].toString(nf)} )"
    }

    companion object {
        fun <T> of(a: T, b: T, c: T, d: T, mc: FieldCalculator<T>): MobiusTrans<T> {
            val det = mc.eval { a * c - b * d }
            if (mc.isZero(det)) {
                throw IllegalArgumentException("ac-bd = 0")
            }
            val cc = Complex.calculator(mc)
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

        fun <T> of(a: Complex<T>, b: Complex<T>, c: Complex<T>, d: Complex<T>): MobiusTrans<T> {
            val mc = a.calculator
            val det = a * d - b * c
            if (det.isZero()) {
                throw IllegalArgumentException("ad - bc= 0")
            }
            val cc = Complex.calculator(mc)
            val mat = TransMatrix.valueOf(a, b, c, d, cc)
            return MobiusTrans(mc, cc, mat)
        }

        /**
         * Returns a mobius transformation which transforms the three given points to (0,1,Inf), it
         * is required that the given three points are different.
         */
        fun <T> to01Inf(a: ComplexE<T>, b: ComplexE<T>, c: ComplexE<T>): MobiusTrans<T> {
            val mc: FieldCalculator<T> = if (b !is Complex) {
                if (a !is Complex) {
                    throw ArithmeticException()
                }
                a.calculator
            } else {
                b.calculator
            }
            // (x-a)(b-c)
            // ----------
            // (x-c)(b-a)
            if (b !is Complex) {
                require(a is Complex && c is Complex)
                // (x-a)
                // -----
                // (x-c)
                val one = Complex.one(mc)
                return of(one, a.negate(), one, c.negate())
            }
            if (a !is Complex) {
                require(c is Complex)
                // (b-c)
                // -----
                // (x-c)
                return of(Complex.zero(mc), b - c, Complex.one(mc), -c)
            }
            if (c !is Complex) {
                // (x-a)
                // ----------
                // (b-a)
                return of(Complex.one(mc), -a, Complex.zero(mc), b - a)
            }
            return to01Inf(b, a, c)
        }

        fun <T> to01Inf(a: Complex<T>, b: Complex<T>, c: Complex<T>): MobiusTrans<T> {
            val b_c = b - c
            val b_a = b - a
            // (b-c)(x-a)
            // ----------
            // (b-a)(x-c)
            return of(b_c, -a * b_c, b_a, -c * b_a)
        }

        /**
         * Returns a Mobius transformation that transforms `a1,b1,c1` to `a2,b2,c2` respectively.
         */
        fun <T> threePoints(
                a1: ComplexE<T>, b1: ComplexE<T>, c1: ComplexE<T>,
                a2: ComplexE<T>, b2: ComplexE<T>, c2: ComplexE<T>
        ): MobiusTrans<T> {
            return to01Inf(a1, b1, c1).andThen(to01Inf(a2, b2, c2).reciprocal())
        }

        /**
         * Returns a Mobius transformation that transforms `a1,b1,c1` to `a2,b2,c2` respectively.
         */
        fun <T> threePoints(
                a1: Complex<T>, b1: Complex<T>, c1: Complex<T>,
                a2: Complex<T>, b2: Complex<T>, c2: Complex<T>
        ): MobiusTrans<T> {
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