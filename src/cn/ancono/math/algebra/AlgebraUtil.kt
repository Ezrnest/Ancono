package cn.ancono.math.algebra

import cn.ancono.math.MathCalculator
import cn.ancono.math.MathSymbol
import cn.ancono.math.MathUtils
import cn.ancono.math.algebra.linearAlgebra.Matrix
import cn.ancono.math.algebra.linearAlgebra.MatrixSup
import cn.ancono.math.algebra.linearAlgebra.Vector
import cn.ancono.math.calculus.Calculus
import cn.ancono.math.get
import cn.ancono.math.numberModels.*
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.SimplificationStrategies
import cn.ancono.math.numberModels.structure.Polynomial
import cn.ancono.math.numberTheory.NTCalculator
import cn.ancono.math.numberTheory.combination.CombUtils
import java.util.*
import java.util.function.Function
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

object AlgebraUtil {
    /**
     * Returns a polynomial that is equal to the result of the
     * product of (x-root[0])(x-root[1])...(x-root[n])
     */
    @JvmStatic
    fun <T : Any> expandOfRoots(roots: List<T>, mc: MathCalculator<T>): Polynomial<T> {
        return when (roots.size) {
            0 -> Polynomial.zero(mc)
            1 -> Polynomial.valueOf(mc, roots.first(), mc.one)
            2 -> {
                val x1 = roots[0]
                val x2 = roots[1]
                val c = mc.multiply(x1, x2)
                val b = mc.negate(mc.add(x1, x2))
                Polynomial.valueOf(mc, c, b, mc.one)
            }
            else -> {
                val mid = roots.size / 2
                val left = expandOfRoots(roots.subList(0, mid), mc)
                val right = expandOfRoots(roots.subList(mid, roots.size), mc)
                return left.multiply(right)
            }
        }
    }

    /**
     * Determines whether the given polynomial has duplicated roots.
     */
    @JvmStatic
    fun <T : Any> hasDuplicatedRoots(polynomial: Polynomial<T>): Boolean {
        val derivated = polynomial.derivative()
        val mc = Polynomial.getCalculator(polynomial.mathCalculator)
        return mc.gcd(polynomial, derivated).isConstant
    }

    /**
     * Returns the n-th Bernoulli polynomial.
     *
     * The Bernoulli polynomial is defined by the formula:
     * > B_n(x) = sigma( C(n,k)*b(n-k)*x^k, k from 0 to n)
     */
    @JvmStatic
    fun polynomialBernoulli(n: Int): Polynomial<Fraction> {
        require(n >= 0)
        val comb = CombUtils.binomialsOf(n)
        val list = (0..n).map { k ->
            CombUtils.numBernoulli(n - k).multiply(comb.get(k.toLong()))
        }
        return Polynomial.valueOf(Fraction.calculator, list)
    }

    /**
     * Returns the n-th Bernoulli polynomial.
     */
    @JvmStatic
    fun polynomialBernoulliBig(n: Int): Polynomial<BigFraction> {
        require(n >= 0)
        val list = arrayOfNulls<BigFraction>(n + 1)
        val comb = CombUtils.binomialsBigOf(n)
        val evenBernoulli = CombUtils.numBernoulliEvenBig(n / 2 + 1)
        for (k in 0..n) {
            val i = n - k
            if (i % 2 == 1) {
                list[k] = if (i == 1) {
                    BigFraction.fromFraction(CombUtils.numBernoulli(1)).multiply(comb.get(1L))
                } else {
                    BigFraction.ZERO
                }
            } else {
                val b = evenBernoulli.get(i / 2L)
                list[k] = b.multiply(comb.get(k.toLong()))
            }
        }
        return Polynomial.valueOf(BigFraction.calculator, *list)
    }

    /**
     * Tries the find solution of a polynomial of integer coefficient.
     */
    @JvmStatic
    fun <T : Any> tryFindSolutions(p: Polynomial<T>, mc: NTCalculator<T>) {
        TODO()
    }

    @JvmStatic
    fun findOneRationalRoot(p: Polynomial<Long>): Fraction? {
        val first = p.first()!!
        val const = p.constant()!!
        if (const == 0L) {
            return Fraction.ZERO
        }
        //solution = const.factor / first.factor
        val ff = MathUtils.factors(first.absoluteValue)
        val cf = MathUtils.factors(const.absoluteValue)
        val pf = p.mapTo(Function { it -> Fraction.valueOf(it) }, Fraction.calculator)
        for (nume in cf) {
            for (deno in ff) {
                var root = Fraction.valueOf(nume, deno)

                if (pf.compute(root).isZero()) {
                    return root
                }
                root = root.negate()
                if (pf.compute(root).isZero()) {
                    return root
                }
            }
        }

        return null
    }

    fun Polynomial<Long>.toFractionPoly(): Polynomial<Fraction> {
        return this.mapTo(Function { Fraction.valueOf(it) }, Fraction.calculator)
    }

    /**
     * Multiplies an integer to this polynomial to make this polynomial becomes
     * a polynomial of long.
     */
    fun Polynomial<Fraction>.toLongPoly(): Polynomial<Long> {
        val lcm = this.fold(1L) { a, f ->
            MathUtils.lcm(a, f.denominator)
        }

        return this.mapTo(Function { it.multiply(lcm).toLong() }, Calculators.getCalculatorLong())
    }

    fun decomposeInt(p: Polynomial<Long>): DecomposedPoly<Fraction> {
        val map = TreeMap<Polynomial<Fraction>, Int>()
        decomposion0(p, map)
        return DecomposedPoly(p.toFractionPoly(), map.toList())
    }

    fun decomposeFrac(p: Polynomial<Fraction>): DecomposedPoly<Fraction> {
        val lcm = p.fold(1L) { g, f ->
            MathUtils.lcm(g, f.denominator)
        }
        return decomposeInt(p.mapTo(Function { it ->
            val re = it.numerator * lcm / it.denominator
            if (it.isPositive) {
                re
            } else {
                -re
            }
        }, Calculators.getCalculatorLong()))
    }


    private fun decomposion0(p: Polynomial<Long>, list: MutableMap<Polynomial<Fraction>, Int>) {
        when (p.degree) {
            0 -> return
            1 -> {
                list.merge(p.toFractionPoly(), 1) { t, u ->
                    t + u
                }
                return
            }
        }
        val rt = findOneRationalRoot(p)
        if (rt == null) {
            if (p.degree != 2) {
                throw ArithmeticException("Cannot decompose $p")
            }
            list.merge(p.toFractionPoly(), 1) { t, u ->
                t + u
            }
            return
        }
        val factor = Polynomial.ofRoot(Fraction.calculator, rt)
        list.merge(factor, 1) { t, u -> t + u }
        val remains = p.toFractionPoly().divideToInteger(factor)
        decomposion0(remains.toLongPoly(), list)
    }


    /**
     * Computes the partial fraction of a fraction of polynomial. It is required that `deg(nume) < deg(deno)`.
     * Returns a list of pair of polynomial
     */
    @JvmStatic
    fun <T : Any> partialFraction(nume: Polynomial<T>, deno: DecomposedPoly<T>)
            : List<Pair<Polynomial<T>, SinglePoly<T>>> {
        //coefficient matrix
        val terms = arrayListOf<Pair<SinglePoly<T>, Boolean>>()
        var coeCount = 0
        val mc = nume.mathCalculator
        val all: Polynomial<T> = deno.expanded
        for ((poly, pow) in deno.decomposed) {
            var d = poly
            val isBi = poly.degree == 2
            for (i in 1..pow) {
                terms.add(SinglePoly(d, poly, i) to isBi)
                d = d.multiply(poly)
//                d *= poly
            }
            coeCount += poly.degree * pow
        }
        val matBuilder = Matrix.getBuilder(all.degree, coeCount + 1, mc)
        //distribute coefficient
        var index = 0
        for ((t, isBi) in terms) {
            val poly = all.divideToInteger(t.expanded)
            if (isBi) {
                for (i in 0..poly.degree) {
                    val coe = poly.getCoefficient(i)
                    matBuilder.set(coe, i, index)
                    matBuilder.set(coe, i + 1, index + 1)
                }
                index += 2
            } else {
                for (i in 0..poly.degree) {
                    val coe = poly.getCoefficient(i)
                    matBuilder.set(coe, i, index)
                }
                index++
            }
        }

        for (i in 0 until all.degree) {
            matBuilder.set(nume.getCoefficient(i), i, coeCount)
        }
        val mat = matBuilder.build()
//        mat.printMatrix()
        val solution = MatrixSup.solveLinearEquation(mat).specialSolution
        index = 0
        val re = arrayListOf<Pair<Polynomial<T>, SinglePoly<T>>>()
        for ((t, isBi) in terms) {
            if (isBi) {
                re += Polynomial.valueOf(mc, solution[index], solution[index + 1]) to t
                index += 2
            } else {
                re += Polynomial.constant(mc, solution[index]) to t
                index++
            }
        }
        return re


    }


    @JvmStatic
    fun partialFractionInt(nume: Polynomial<Long>, deno: Polynomial<Long>):
            List<Pair<Polynomial<Fraction>, SinglePoly<Fraction>>> {
        val deneDecomposed = decomposeInt(deno)
        val fNume = nume.toFractionPoly()
        return partialFraction(fNume, deneDecomposed)
    }

    /**
     * Builds the equation
     * > a0 * `ms[0]` + ... + an * `ms[n]` = mConst
     *
     * Terms with different characters are considered as linear irrelevant.
     * The second part of the return value is a vector of terms contained in the multinomials and
     * the first part is the expanded matrix.
     * @return
     */
    @JvmStatic
    fun buildMultinomialEquation(ms: List<Multinomial>, mConst: Multinomial = Multinomial.ZERO): Pair<Matrix<Multinomial>, List<Multinomial>> {
        val terms = TreeMap<Term, Int>()
        fun putTerms(m: Multinomial) {
            for (t in m.terms) {
                val charPart = t.characterPart()
                terms.computeIfAbsent(charPart) {
                    terms.size
                }
            }
        }
        for (m in ms) {
            putTerms(m)
        }
        putTerms(mConst)
        val builder = Matrix.getBuilder(terms.size, ms.size + 1, Multinomial.getCalculator())
        for ((c, m) in ms.withIndex()) {
            for (t in m.terms) {
                val idx = terms[t.characterPart()]!!
                builder.set(Multinomial.monomial(t.numberPart()), idx, c)
            }
        }
        for (t in mConst.terms) {
            val idx = terms[t.characterPart()]!!
            builder.set(Multinomial.monomial(t.numberPart()), idx, ms.size)
        }
        val mat = builder.build()
        val vec = terms.keys.mapTo(ArrayList(terms.size), Multinomial::monomial)
        return mat to vec
    }

    fun solveMultinomialEquation(ms: List<Multinomial>, mConst: Multinomial = Multinomial.ZERO): Vector<Multinomial> {
        return MatrixSup.solveLinearEquation(buildMultinomialEquation(ms, mConst).first).oneSolution
    }

}

fun main(args: Array<String>) {
    val mc = Calculators.getCalculatorLongExact()
//    val nume = Polynomial.valueOf(mc,1L,0L,0L,1L)
//    val deno = Polynomial.valueOf(mc,0L,-1L,3L,-3L,1L)
    val nume = Polynomial.valueOf(mc, 6L, 5L)
    val deno = Polynomial.valueOf(mc, 1L, 1L, 1L)

    println("${MathSymbol.INTEGRAL} ($nume) / ($deno) dx")
//    println(AlgebraUtil.partialFractionInt(nume,deno))
    val ec = ExprCalculator.instance
    ec.setProperty(SimplificationStrategies.PROP_MERGE_FRACTION, "false")
    ec.setProperty(SimplificationStrategies.PROP_ENABLE_EXPAND, "false")
    val inte = Calculus.intRational(nume, deno, ec, "x")
    println(inte)
    ec.setProperty(SimplificationStrategies.PROP_ENABLE_EXPAND, "true")
    println(ec.differential(inte))
//    println(AlgebraUtil.polynomialBernoulli(6))
//    println(AlgebraUtil.polynomialBernoulliBig(20))
}



