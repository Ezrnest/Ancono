package cn.ancono.math.numberModels.structure


import cn.ancono.math.FlexibleMathObject
import cn.ancono.math.algebra.abs.calculator.RingCalculator
import cn.ancono.math.algebra.abs.calculator.UnitRingCalculator
import cn.ancono.math.exceptions.ExceptionUtil
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.MathCalculatorAdapter
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import cn.ancono.math.numberModels.api.Simplifier
import cn.ancono.math.numberTheory.IntCalculator
import cn.ancono.utilities.structure.Pair
import java.util.*
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
 *
 * [Field of fractions](https://en.wikipedia.org/wiki/Field_of_fractions)
 */
open class RingFraction<T : Any>
internal constructor(nume: T, deno: T, mc: RingCalculator<T>)
    : FlexibleMathObject<T, RingCalculator<T>>(mc) {

    /**
     * Gets the numerator of the fraction.
     * @return numerator
     */
    val nume: T = Objects.requireNonNull(nume)
    /**
     * Gets the denominator of the fraction.
     * @return denominator
     */
    val deno: T = Objects.requireNonNull(deno)


//    fun


    override fun toString(nf: FlexibleNumberFormatter<T, RingCalculator<T>>): String {
        return "(" + nf.format(nume, mc) +
                ")/(" + nf.format(deno, mc) +
                ')'.toString()
    }

    fun <N : Any> mapTo(mapper: Function<T, N>, ringCalculator: RingCalculator<N>): RingFraction<N> {
        return RingFraction(mapper.apply(nume), mapper.apply(deno), ringCalculator)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is RingFraction<*>) {
            return false
        }
        val rf = other as RingFraction<*>?
        return nume == rf!!.nume && deno == rf.deno
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + nume.hashCode()
        result = 31 * result + deno.hashCode()
        return result
    }

    fun valueEquals(f: RingFraction<T>): Boolean {
        return mc.isEqual(mc.multiply(nume, f.deno), mc.multiply(deno, f.nume))
    }

    class RFCalculator<T : Any> internal constructor(mc: RingCalculator<T>,
                                                     internal val sim: Simplifier<T>?,
                                                     private val nonZero: T)
        : MathCalculatorAdapter<RingFraction<T>>() {
        internal val mc: RingCalculator<T> = Objects.requireNonNull(mc)
        override val zero: RingFraction<T> = RingFraction(mc.zero, nonZero, mc)
        override val one: RingFraction<T> = RingFraction(nonZero, nonZero, mc)

        /**
         * Returns a fraction that is equal to `x/1`. Note that if there is no multiplication unit in the ring,
         * the numerator and the denominator are not `x` and `1`.
         *
         * **Note**: This function is essentially the injection from the ring to the fraction field.
         */
        fun of(x: T): RingFraction<T> {
            if (mc.isEqual(x, mc.zero)) {
                return zero
            }
            return valueOf(mc.multiply(nonZero, x), nonZero)
//            return RingFraction(x)
        }

        fun simplify(n: T, d: T): RingFraction<T> {
            if (sim == null) {
                return RingFraction(n, d, mc)
            }
            val pair = sim.simplify(n, d)
            val n1 = pair.first
            val d1 = pair.second
            return RingFraction(n1, d1, mc)

        }

        fun valueOf(nume: T, deno: T): RingFraction<T> {
            if (mc.isEqual(deno, mc.zero)) {
                ExceptionUtil.dividedByZero()
            }
            return simplify(nume, deno)
        }

        override fun add(x: RingFraction<T>, y: RingFraction<T>): RingFraction<T> {
            val n = mc.add(mc.multiply(x.nume, y.deno), mc.multiply(x.deno, y.nume))
            val d = mc.multiply(x.deno, y.deno)
            return simplify(n, d)
        }

        override fun negate(x: RingFraction<T>): RingFraction<T> {
            return RingFraction(mc.negate(x.nume), x.deno, mc)
        }

        override fun subtract(x: RingFraction<T>, y: RingFraction<T>): RingFraction<T> {
            val n = mc.subtract(mc.multiply(x.nume, y.deno), mc.multiply(x.deno, y.nume))
            val d = mc.multiply(x.deno, y.deno)
            return simplify(n, d)
        }

        override fun multiply(x: RingFraction<T>, y: RingFraction<T>): RingFraction<T> {
            val n = mc.multiply(x.nume, y.nume)
            val d = mc.multiply(x.deno, y.deno)
            return simplify(n, d)
        }

        override fun reciprocal(x: RingFraction<T>): RingFraction<T> {
            if (mc.isEqual(x.nume, mc.zero)) {
                ExceptionUtil.dividedByZero()
            }
            return RingFraction(x.deno, x.nume, mc)
        }

        override fun divide(x: RingFraction<T>, y: RingFraction<T>): RingFraction<T> {
            val n = mc.multiply(x.nume, y.deno)
            val d = mc.multiply(x.deno, y.nume)
            if (mc.isEqual(d, mc.zero)) {
                ExceptionUtil.dividedByZero()
            }
            return simplify(n, d)
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
        @JvmStatic
        fun <T : Any> getCalculator(ringCalculator: RingCalculator<T>, simplifier: Simplifier<T>?, noneZero: T): RFCalculator<T> {
            return RFCalculator(ringCalculator, simplifier, noneZero)
        }

        @JvmStatic
        fun <T : Any> getCalculator(mc: UnitRingCalculator<T>, simplifier: Simplifier<T>?): RFCalculator<T> {
            return RFCalculator(mc, simplifier, mc.one)
        }

        @JvmStatic
        fun <T : Any> valueOf(nume: T, deno: T, mc: RingCalculator<T>): RingFraction<T> {
            if (mc.isEqual(deno, mc.zero)) {
                ExceptionUtil.dividedByZero()
            }
            return RingFraction(nume, deno, mc)
        }

        @JvmStatic
        fun <T : Any> valueOf(nume: T, mc: UnitRingCalculator<T>): RingFraction<T> {
            return RingFraction(nume, mc.one, mc)
        }

        /**
         * Returns a composed of the
         */
        @JvmStatic
        fun <T : Any> extractGcd(fractions: List<RingFraction<T>>, mc: IntCalculator<T>): Pair<T, T> {
            val initial = Pair(mc.zero, mc.one)
            return fractions.fold(initial) { p, frac ->
                val n = mc.gcd(p.first, frac.nume)
                val d = mc.lcm(p.second, frac.deno)
                Pair(n, d)
            }
        }
    }

}
