package cn.timelives.java.math.numberModels.structure


import cn.timelives.java.math.FlexibleMathObject
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.DivisionRingCalculator
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.FieldCalculator
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.RingCalculator
import cn.timelives.java.math.exceptions.ExceptionUtil
import cn.timelives.java.math.numberModels.api.DivisionRingNumberModel
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter
import cn.timelives.java.math.numberModels.api.Simplifier

import java.util.Objects
import java.util.function.Function

/**
 * A ring fraction is created from a type T which can be
 * a ring. The fraction itself and the corresponding operations
 * consists a field called the fraction field.
 * <P>
 * A ring fraction consists of a numerator and a denominator, and can be
 * denoted as (n,d). The equivalent relation of two fraction (n1,d1) and (n2,d2)
 * is that `n1*d2 = n2*d1`.
</P> *
 *
 * <h3>Notice:</h3>
 * To operate the ring fraction, the corresponding ring fraction calculator must be used, which
 * supports the basic operations defined on a field calculator.
 * [Field of fractions](https://en.wikipedia.org/wiki/Field_of_fractions)
 */
class RingFraction<T : Any>
internal constructor(nume: T, deno: T, mc: RingCalculator<T>) : FlexibleMathObject<T, RingCalculator<T>>(mc) {

    /**
     * Gets the numerator of the fraction.
     * @return numerator
     */
    val nume: T
    /**
     * Gets the denominator of the fraction.
     * @return denominator
     */
    val deno: T

    init {
        this.nume = Objects.requireNonNull(nume)
        this.deno = Objects.requireNonNull(deno)
    }

    override fun toString(nf: FlexibleNumberFormatter<T, RingCalculator<T>>): String {
        return "(" + nf.format(nume, mc) +
                ")/(" + nf.format(deno, mc) +
                ')'.toString()
    }

    fun <N : Any> mapTo(mapper: Function<T, N>, ringCalculator: RingCalculator<N>): RingFraction<N> {
        return RingFraction(mapper.apply(nume), mapper.apply(deno), ringCalculator)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj === this) {
            return true
        }
        if (obj !is RingFraction<*>) {
            return false
        }
        val rf = obj as RingFraction<*>?
        return nume == rf!!.nume && deno == rf.deno
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + nume.hashCode()
        result = 31 * result + deno.hashCode()
        return result
    }

    class RFCalculator<T : Any> internal constructor(mc: RingCalculator<T>, internal val sim: Simplifier<T>?, nonZero: T) : FieldCalculator<RingFraction<T>> {
        internal val mc: RingCalculator<T> = Objects.requireNonNull(mc)
        override val zero: RingFraction<T> = RingFraction(mc.zero, nonZero, mc)
        override val one: RingFraction<T> = RingFraction(nonZero, nonZero, mc)

        fun valueOf(nume: T, deno: T): RingFraction<T> {
            var nume = nume
            var deno = deno
            if (mc.isEqual(deno, mc.zero)) {
                ExceptionUtil.divideByZero()
            }
            if (sim != null) {
                val pair = sim.simplify(nume, deno)
                nume = pair.first
                deno = pair.second
            }
            return RingFraction(nume, deno, mc)
        }

        override fun add(x: RingFraction<T>, y: RingFraction<T>): RingFraction<T> {
            var n = mc.add(mc.multiply(x.nume, y.deno), mc.multiply(x.deno, y.nume))
            var d = mc.multiply(x.deno, y.deno)
            if (sim != null) {
                val pair = sim.simplify(n, d)
                n = pair.first
                d = pair.second
            }
            return RingFraction(n, d, mc)
        }

        override fun negate(x: RingFraction<T>): RingFraction<T> {
            return RingFraction(mc.negate(x.nume), x.deno, mc)
        }

        override fun subtract(x: RingFraction<T>, y: RingFraction<T>): RingFraction<T> {
            var n = mc.subtract(mc.multiply(x.nume, y.deno), mc.multiply(x.deno, y.nume))
            var d = mc.multiply(x.deno, y.deno)
            if (sim != null) {
                val pair = sim.simplify(n, d)
                n = pair.first
                d = pair.second
            }
            return RingFraction(n, d, mc)
        }

        override fun multiply(x: RingFraction<T>, y: RingFraction<T>): RingFraction<T> {
            var n = mc.multiply(x.nume, y.nume)
            var d = mc.multiply(x.deno, y.deno)
            if (sim != null) {
                val pair = sim.simplify(n, d)
                n = pair.first
                d = pair.second
            }
            return RingFraction(n, d, mc)
        }

        override fun reciprocal(x: RingFraction<T>): RingFraction<T> {
            if (mc.isEqual(x.nume, mc.zero)) {
                ExceptionUtil.divideByZero()
            }
            return RingFraction(x.deno, x.nume, mc)
        }

        override fun divide(x: RingFraction<T>, y: RingFraction<T>): RingFraction<T> {

            var n = mc.multiply(x.nume, y.deno)
            var d = mc.multiply(x.deno, y.nume)
            if (mc.isEqual(d, mc.zero)) {
                ExceptionUtil.divideByZero()
            }
            if (sim != null) {
                val pair = sim.simplify(n, d)
                n = pair.first
                d = pair.second
            }
            return RingFraction(n, d, mc)
        }

        override fun isEqual(x: RingFraction<T>, y: RingFraction<T>): Boolean {
            return mc.isEqual(mc.multiply(x.nume, y.deno), mc.multiply(x.deno, y.nume))
        }
    }

    companion object {


        /**
         * Gets a calculator for fraction field.
         * @param ringCalculator
         * @param simplifier
         * @param noneZero
         * @param <T>
         * @return
        </T> */
        fun <T : Any> getCalculator(ringCalculator: RingCalculator<T>, simplifier: Simplifier<T>, noneZero: T): RFCalculator<T> {
            return RFCalculator(ringCalculator, simplifier, noneZero)
        }

        fun <T : Any> valueOf(nume: T, deno: T, mc: RingCalculator<T>): RingFraction<T> {
            if (mc.isEqual(deno, mc.zero)) {
                ExceptionUtil.divideByZero()
            }
            return RingFraction(nume, deno, mc)
        }
    }

}
