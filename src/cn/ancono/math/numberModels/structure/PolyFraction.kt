package cn.ancono.math.numberModels.structure

import cn.ancono.math.AbstractMathObject
import cn.ancono.math.MathObject
import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.exceptions.ExceptionUtil
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.api.*
import java.util.function.Function

class PFraction<T>(mc: FieldCalculator<T>, val nume: Polynomial<T>, val deno: Polynomial<T>)
    : AbstractMathObject<T, FieldCalculator<T>>(mc),
        FieldNumberModel<PFraction<T>>,
        AlgebraModel<T, PFraction<T>>,
        Simplifiable<T, PFraction<T>> {

    private fun ofT(t: T): PFraction<T> {
        return of(Polynomial.constant(calculator, t), Polynomial.one(calculator))
    }

    private fun of(nume: Polynomial<T>, deno: Polynomial<T>): PFraction<T> {
        val p = Polynomial.simplifyFraction(nume, deno)
        return PFraction(calculator, p.first, p.second)
    }

    override fun add(y: PFraction<T>): PFraction<T> {
        val n: Polynomial<T> = nume * y.deno + deno * y.nume
        val d = deno * y.deno
        return of(n, d)
    }

    override fun negate(): PFraction<T> {
        return PFraction(calculator, nume.negate(), deno)
    }

    override fun multiply(y: PFraction<T>): PFraction<T> {
        val n = nume * y.nume
        val d = deno * y.deno
        return of(n, d)
    }

    override fun multiply(k: T): PFraction<T> {
        if (calculator.isZero(k)) {
            return zero(calculator)
        }
        return PFraction(calculator, nume.multiply(k), deno)
    }

    override fun divide(k: T): PFraction<T> {
        if (isZero()) {
            return this
        }
        if (calculator.isZero(k)) {
            ExceptionUtil.dividedByZero()
        }
        return PFraction(calculator, nume, deno.multiply(k))
    }

    override fun multiply(n: Long): PFraction<T> {
        if (n == 0L) {
            return zero(calculator)
        }
        return PFraction(calculator, nume.multiply(n), deno)
    }

    override fun isZero(): Boolean {
        return nume.isZero()
    }

    override fun reciprocal(): PFraction<T> {
        if (isZero()) {
            ExceptionUtil.dividedByZero()
        }
        return of(deno, nume)
    }

    override fun subtract(y: PFraction<T>): PFraction<T> {
        val n = nume * y.deno - deno * y.nume
        val d = deno * y.deno
        return of(n, d)
    }

    override fun divide(y: PFraction<T>): PFraction<T> {
        if (y.isZero()) {
            ExceptionUtil.dividedByZero()
        }
        val n = nume * y.deno
        val d = deno * y.nume
        return of(n, d)
    }

    fun divideLong(k: Long): PFraction<T> {
        if (k == 0L) {
            ExceptionUtil.dividedByZero()
        }
        val d = deno.multiply(k)
        return of(nume, d)
    }


    /**
     * Performs a homomorphism map that maps the character of the polynomial fraction to [x] and
     * maps the coefficient using the [injection].
     */
    fun <K> homoMap(x: K, injection: Function<T, K>): K where K : FieldNumberModel<K>, K : AlgebraModel<T, K> {
        val n = nume.homoMap(x, injection)
        val d = deno.homoMap(x, injection)
        return n.divide(d)
    }

    fun substitute(x: PFraction<T>): PFraction<T> {
        return homoMap(x) { ofT(it) }
    }

    override fun simplify(): PFraction<T> {
        return of(nume, deno)
    }

    override fun simplify(sim: Simplifier<T>): PFraction<T> {
        val p = Polynomial.simplifyFraction(nume, deno, sim)
        return PFraction(calculator, p.first, p.second)
    }


    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): PFraction<N> {
        val n2 = nume.mapTo(newCalculator, mapper)
        val d2 = nume.mapTo(newCalculator, mapper)
        return PFraction(newCalculator as FieldCalculator<N>, n2, d2)
    }

    override fun valueEquals(obj: MathObject<T, FieldCalculator<T>>): Boolean {
        if (obj !is PFraction) {
            return false
        }
        val p1 = nume * obj.deno
        val p2 = deno * obj.nume
        return p1.valueEquals(p2)
    }

    override fun toString(nf: NumberFormatter<T>): String {
        if (nume.isZero()) {
            return "0"
        }
        if (deno.isOne) {
            return nume.toString(nf)
        }
        return "(${nume.toString(nf)})/(${deno.toString(nf)})"
    }


    companion object {
        @JvmStatic
        fun <T> of(p: Polynomial<T>): PFraction<T> {
            val mc = p.calculator as FieldCalculator
            return PFraction(mc, p, Polynomial.one(mc))
        }

        @JvmStatic
        fun <T> one(mc: FieldCalculator<T>): PFraction<T> {
            val polyOne = Polynomial.one(mc)
            return PFraction(mc, polyOne, polyOne)
        }

        @JvmStatic
        fun <T> zero(mc: FieldCalculator<T>): PFraction<T> {
            return of(Polynomial.zero(mc))
        }

        @JvmStatic
        fun <T> getCalculator(mc: FieldCalculator<T>, sim: Simplifier<T>? = null): PFractionCalculator<T> {
            return PFractionCalculator(mc, sim)
        }
    }
}

class PFractionCalculator<T>(val mc: FieldCalculator<T>, val sim: Simplifier<T>?) : FieldCalculator<PFraction<T>> {
    override val one: PFraction<T> = PFraction.one(mc)
    override val zero: PFraction<T> = PFraction.zero(mc)

    override val characteristic: Long
        get() = mc.characteristic


    override fun isZero(x: PFraction<T>): Boolean {
        return x.isZero()
    }

    override fun isEqual(x: PFraction<T>, y: PFraction<T>): Boolean {
        return x.valueEquals(y)
    }

    private fun simplify(x: PFraction<T>): PFraction<T> {
        if (sim == null) {
            return x
        }
        val n = x.nume
        val d = x.deno
        val pair = Polynomial.simplifyCoefficient(n, d, mc, sim)
        return PFraction(mc, pair.first, pair.second)
    }

    override fun add(x: PFraction<T>, y: PFraction<T>): PFraction<T> {
        return simplify(x.add(y))
    }

    override fun negate(x: PFraction<T>): PFraction<T> {
        return x.negate()
    }

    override fun subtract(x: PFraction<T>, y: PFraction<T>): PFraction<T> {
        return simplify(x - y)
    }

    override fun multiply(x: PFraction<T>, y: PFraction<T>): PFraction<T> {
        return simplify(x * y)
    }

    override fun divide(x: PFraction<T>, y: PFraction<T>): PFraction<T> {
        return simplify(x / y)
    }

    override fun multiplyLong(x: PFraction<T>, n: Long): PFraction<T> {
        return simplify(x.multiply(n))
    }

    override fun divideLong(x: PFraction<T>, n: Long): PFraction<T> {
        return simplify(x.divideLong(n))
    }

    override fun reciprocal(x: PFraction<T>): PFraction<T> {
        return x.reciprocal()
    }

    override fun of(n: Long): PFraction<T> {
        return PFraction.of(Polynomial.constant(mc, mc.of(n)))
    }

    override fun of(x: Fraction): PFraction<T> {
        return PFraction.of(Polynomial.constant(mc, mc.of(x)))
    }


    override val numberClass: Class<PFraction<T>>
        get() = super.numberClass
}