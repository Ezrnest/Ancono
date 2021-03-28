package cn.ancono.math.numberModels.structure

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathObject
import cn.ancono.math.MathObjectExtend
import cn.ancono.math.exceptions.ExceptionUtil
import cn.ancono.math.numberModels.MathCalculatorAdapter
import cn.ancono.math.numberModels.api.*
import java.util.function.Function

class PFraction<T : Any>(mc: MathCalculator<T>, val nume: Polynomial<T>, val deno: Polynomial<T>)
    : MathObjectExtend<T>(mc),
        FieldNumberModel<PFraction<T>>,
        AlgebraModel<T, PFraction<T>>,
        Simplifiable<T, PFraction<T>> {

    private fun ofT(t: T): PFraction<T> {
        return of(Polynomial.constant(mc, t), Polynomial.one(mc))
    }

    private fun of(nume: Polynomial<T>, deno: Polynomial<T>): PFraction<T> {
        val p = Polynomial.simplifyFraction(nume, deno)
        return PFraction(mc, p.first, p.second)
    }

    override fun add(y: PFraction<T>): PFraction<T> {
        val n: Polynomial<T> = nume * y.deno + deno * y.nume
        val d = deno * y.deno
        return of(n, d)
    }

    override fun negate(): PFraction<T> {
        return PFraction(mc, nume.negate(), deno)
    }

    override fun multiply(y: PFraction<T>): PFraction<T> {
        val n = nume * y.nume
        val d = deno * y.deno
        return of(n, d)
    }

    override fun multiply(k: T): PFraction<T> {
        if (mc.isZero(k)) {
            return zero(mc)
        }
        return PFraction(mc, nume.multiply(k), deno)
    }

    fun multiplyLong(k: Long): PFraction<T> {
        if (k == 0L) {
            return zero(mc)
        }
        return PFraction(mc, nume.multiplyLong(k), deno)
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
        val d = deno.multiplyLong(k)
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
        return homoMap(x, Function { ofT(it) })
    }

    override fun simplify(): PFraction<T> {
        return of(nume, deno)
    }

    override fun simplify(sim: Simplifier<T>): PFraction<T> {
        val p = Polynomial.simplifyFraction(nume, deno, sim)
        return PFraction(mc, p.first, p.second)
    }


    override fun <N : Any> mapTo(newCalculator: MathCalculator<N>, mapper: Function<T, N>): PFraction<N> {
        val n2 = nume.mapTo(newCalculator, mapper)
        val d2 = nume.mapTo(newCalculator, mapper)
        return PFraction(newCalculator, n2, d2)
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is PFraction) {
            return false
        }
        val p1 = nume * obj.deno
        val p2 = deno * obj.nume
        return p1.valueEquals(p2)
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
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
        fun <T : Any> of(p: Polynomial<T>): PFraction<T> {
            val mc = p.mathCalculator
            return PFraction(mc, p, Polynomial.one(mc))
        }

        @JvmStatic
        fun <T : Any> one(mc: MathCalculator<T>): PFraction<T> {
            val polyOne = Polynomial.one(mc)
            return PFraction(mc, polyOne, polyOne)
        }

        @JvmStatic
        fun <T : Any> zero(mc: MathCalculator<T>): PFraction<T> {
            return of(Polynomial.zero(mc))
        }

        @JvmStatic
        fun <T : Any> getCalculator(mc: MathCalculator<T>, sim: Simplifier<T>? = null): PFractionCalculator<T> {
            return PFractionCalculator(mc, sim)
        }
    }
}

class PFractionCalculator<T : Any>(val mc: MathCalculator<T>, val sim: Simplifier<T>?) : MathCalculatorAdapter<PFraction<T>>() {
    override val one: PFraction<T> = PFraction.one(mc)
    override val zero: PFraction<T> = PFraction.zero(mc)

    override fun isZero(para: PFraction<T>): Boolean {
        return para.isZero()
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
        return super.negate(x)
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
        return simplify(x.multiplyLong(n))
    }

    override fun divideLong(x: PFraction<T>, n: Long): PFraction<T> {
        return simplify(x.divideLong(n))
    }

    override fun reciprocal(x: PFraction<T>): PFraction<T> {
        return x.reciprocal()
    }

    override val numberClass: Class<*>
        get() = super.numberClass
}