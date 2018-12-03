package cn.timelives.java.math.algebra

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.numberModels.BigFraction
import cn.timelives.java.math.numberModels.Fraction
import cn.timelives.java.math.numberModels.structure.PolynomialX
import cn.timelives.java.math.numberTheory.combination.CombUtils

object AlgebraUtil {
    /**
     * Returns a polynomial that is equal to the result of the
     * product of (x-root[0])(x-root[1])...(x-root[n])
     */
    @JvmStatic
    fun <T : Any> expandOfRoots(roots: List<T>, mc: MathCalculator<T>): PolynomialX<T> {
        return when (roots.size) {
            0 -> PolynomialX.zero(mc)
            1 -> PolynomialX.valueOf(mc, roots.first(), mc.one)
            2 -> {
                val x1 = roots[0]
                val x2 = roots[1]
                val c = mc.multiply(x1, x2)
                val b = mc.negate(mc.add(x1, x2))
                PolynomialX.valueOf(mc, c, b, mc.one)
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
    fun <T : Any> hasDuplicatedRoots(polynomial: PolynomialX<T>): Boolean {
        val derivated = polynomial.derivative()
        val mc = PolynomialX.getCalculator(polynomial.mathCalculator)
        return mc.gcd(polynomial, derivated).isConstant
    }

    /**
     * Returns the n-th Bernoulli polynomial.
     *
     * The Bernoulli polynomial is defined by the formula:
     * > B_n(x) = sigma( C(n,k)*b(n-k)*x^k, k from 0 to n)
     */
    @JvmStatic
    fun polynomialBernoulli(n: Int): PolynomialX<Fraction> {
        require(n >= 0)
        val comb = CombUtils.binomialsOf(n)
        val list = (0..n).map { k ->
            CombUtils.numBernoulli(n - k).multiply(comb.get(k.toLong()))
        }
        return PolynomialX.valueOf(Fraction.calculator, list)
    }

    /**
     * Returns the n-th Bernoulli polynomial.
     */
    @JvmStatic
    fun polynomialBernoulliBig(n: Int): PolynomialX<BigFraction> {
        require(n >= 0)
        val list = arrayOfNulls<BigFraction>(n+1)
        val comb = CombUtils.binomialsBigOf(n)
        val evenBernoulli = CombUtils.numBernoulliEvenBig(n / 2+1)
        for (k in 0..n) {
            val i = n-k
            if (i % 2 == 1) {
                list[k] = if(i == 1){
                    BigFraction.fromFraction(CombUtils.numBernoulli(1)).multiply(comb.get(1L))
                }else{
                    BigFraction.ZERO
                }
            } else {
                val b =  evenBernoulli.get(i/2L)
                list[k] = b.multiply(comb.get(k.toLong()))
            }
        }
        return PolynomialX.valueOf(BigFraction.calculator,*list)
    }
}

//fun main(args: Array<String>) {
//    println(AlgebraUtil.polynomialBernoulli(6))
//    println(AlgebraUtil.polynomialBernoulliBig(20))
//}



