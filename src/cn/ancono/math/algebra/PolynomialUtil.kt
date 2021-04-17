package cn.ancono.math.algebra

import cn.ancono.math.MathUtils
import cn.ancono.math.algebra.abs.calculator.UFDCalculator
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.MatrixSup
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.Multinomial
import cn.ancono.math.numberModels.Term
import cn.ancono.math.numberModels.api.minus
import cn.ancono.math.numberModels.api.plus
import cn.ancono.math.numberModels.api.times
import cn.ancono.math.numberModels.structure.Polynomial
import cn.ancono.math.numberTheory.ZModPCalculator
import java.util.*
import kotlin.math.absoluteValue

//Created by lyc at 2020-03-01 13:59
/**
 * Provides utility algorithms for polynomials.
 */
object PolynomialUtil {


//    /**
//     * Tries the find solution of a polynomial of integer coefficient.
//     */

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
        val pf = p.mapTo(Fraction.calculator) { Fraction.of(it) }
        for (nume in cf) {
            for (deno in ff) {
                var root = Fraction.of(nume, deno)

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
        return this.mapTo(Fraction.calculator) { Fraction.of(it) }
    }

    /**
     * Multiplies an integer to this polynomial to make this polynomial becomes
     * a polynomial of long.
     */
    fun Polynomial<Fraction>.toLongPoly(): Polynomial<Long> {
        val lcm = this.coefficients().fold(1L) { a, f ->
            MathUtils.lcm(a, f.denominator)
        }

        return this.mapTo(Calculators.longCal()) { it.multiply(lcm).toLong() }
    }

    fun decomposeInt(p: Polynomial<Long>): DecomposedPoly<Fraction> {
        val map = TreeMap<Polynomial<Fraction>, Int>()
        decomposion0(p, map)
        return DecomposedPoly(p.toFractionPoly(), map.toList())
    }

    fun decomposeFrac(p: Polynomial<Fraction>): DecomposedPoly<Fraction> {
        val lcm = p.coefficients().fold(1L) { g, f ->
            MathUtils.lcm(g, f.denominator)
        }
        return decomposeInt(p.mapTo(Calculators.longCal()) { it ->
            val re = it.numerator * lcm / it.denominator
            if (it.isPositive) {
                re
            } else {
                -re
            }
        })
    }

    private fun decomposion0(p: Polynomial<Long>, list: MutableMap<Polynomial<Fraction>, Int>) {
        when (p.leadingPower) {
            -1, 0 -> return
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
                    val coe = poly.get(i)
                    matBuilder.set(coe, i, index)
                    matBuilder.set(coe, i + 1, index + 1)
                }
                index += 2
            } else {
                for (i in 0..poly.degree) {
                    val coe = poly.get(i)
                    matBuilder.set(coe, i, index)
                }
                index++
            }
        }

        for (i in 0 until all.degree) {
            matBuilder.set(nume.get(i), i, coeCount)
        }
        val mat = matBuilder.build()
//        mat.printMatrix()
        val solution = MatrixSup.solveLinearEquation(mat).specialSolution
        index = 0
        val re = arrayListOf<Pair<Polynomial<T>, SinglePoly<T>>>()
        for ((t, isBi) in terms) {
            if (isBi) {
                re += Polynomial.of(mc, solution[index], solution[index + 1]) to t
                index += 2
            } else {
                re += Polynomial.constant(mc, solution[index]) to t
                index++
            }
        }
        return re


    }


    /**
     * Computes the partial fraction decomposition of `nume / deno`.
     */
    @JvmStatic
    fun partialFractionInt(nume: Polynomial<Long>, deno: Polynomial<Long>):
            List<Pair<Polynomial<Fraction>, SinglePoly<Fraction>>> {
        val deneDecomposed = decomposeInt(deno)
        val fNume = nume.toFractionPoly()
        return partialFraction(fNume, deneDecomposed)
    }

    /**
     * Builds the equation
     * > a0 * `ms[0]` + ... + an * `ms[n ]` = mConst
     *
     * Terms with different characters are considered as linear irrelevant.
     * The second part of the return value is a vector of terms contained in the multinomials and
     * the first part is the expanded matrix.
     */
    @JvmStatic
    fun buildMultinomialEquation(ms: List<Multinomial>, mConst: Multinomial = Multinomial.ZERO)
            : Pair<Matrix<Multinomial>, List<Multinomial>> {
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

    @JvmStatic
    fun solveMultinomialEquation(ms: List<Multinomial>, mConst: Multinomial = Multinomial.ZERO)
            : Vector<Multinomial> {
        return MatrixSup.solveLinearEquation(buildMultinomialEquation(ms, mConst).first).oneSolution
    }


    /**
     * Performs the pseudo division of two polynomials on a ring. This algorithm finds `Q` and `R` such that
     * `d^(A.degree - B.degree + 1) A = BQ + R` and `R.degree < B.degree`. It is required that `B` is not zero and
     * `A.degree >= B.degree`.
     *
     * @param T the math calculator for [T] should at least be a ring calculator.
     */
    @JvmStatic
    fun <T : Any> pseudoDivision(A: Polynomial<T>, B: Polynomial<T>): Pair<Polynomial<T>, Polynomial<T>> {
        /*
        See Algorithm 3.1.2, page 112 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen
        Created by lyc at 2020-03-01 14:25
         */
        val m = A.degree
        val n = B.degree
        require(!B.isZero())
        require(m >= n)
        val mc = A.mathCalculator

        val d = B.first()
        var R = A
        var Q = Polynomial.zero(mc)
        var e = m - n + 1
        while (!R.isZero() && R.degree >= B.degree) {
            val S = Polynomial.powerX(R.degree - B.degree, R.first(), mc)
            Q = d * Q + S
            R = d * R - S * B
            e -= 1
        }
        val q = mc.pow(d, e.toLong())
        Q = Q.multiply(q)
        R = R.multiply(q)
        return Pair(Q, R)
    }

    /**
     * Performs the pseudo division of two polynomials on a ring and returns only the remainder.
     * This algorithm finds `Q` and `R` such that
     * `d^(A.degree - B.degree + 1) A = BQ + R` and `R.degree < B.degree`. It is required that `B` is not zero and
     * `A.degree >= B.degree`.
     *
     * @param T the math calculator for [T] should at least be a ring calculator.
     */
    @JvmStatic
    fun <T : Any> pseudoDivisionR(A: Polynomial<T>, B: Polynomial<T>): Polynomial<T> {
        /*
        See Algorithm 3.1.2, page 112 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen
        Created by lyc at 2020-03-01 14:25
         */
        require(!B.isZero())
        val m = A.degree
        val n = B.degree
        if (m < n) {
            return A
        }
        val mc = A.mathCalculator
        val d = B.first()
        var R = A
        var e = m - n + 1
        while (!R.isZero() && R.degree >= B.degree) {
            val S = Polynomial.powerX(R.degree - B.degree, R.first(), mc)
            R = d * R - S * B
            e -= 1
        }
        val q = mc.pow(d, e.toLong())
        R = R.multiply(q)
        return R
    }


    /**
     * Computes the GCD of two polynomials on an UFD.
     *
     * It is required that the calculator of [f] is an instance of [UFDCalculator].
     *
     * @see [subResultantGCD]
     */
    @JvmStatic
    fun <T : Any> primitiveGCD(f: Polynomial<T>, g: Polynomial<T>): Polynomial<T> {
        if (f.isZero()) {
            return g
        }
        if (g.isZero()) {
            return f
        }
        /*
        See Algorithm 3.2.10, page 117 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen
        Created by lyc at 2020-03-01 16:02
         */
        val mc = f.mathCalculator

        @Suppress("UNCHECKED_CAST")
        val rc = mc as UFDCalculator<T>
        val a = f.cont()
        val b = g.cont()
        val d = rc.gcd(a, b)
        var A = f.divide(a)
        var B = g.divide(b)
        while (true) {
            val R = pseudoDivisionR(A, B)
            if (R.isZero()) {
                break
            }
            if (R.isConstant) {
                B = Polynomial.one(mc)
                break
            }
            A = B
            B = R.toPrimitive()
        }
        return d * B
    }

    /**
     * Computes the GCD of two polynomials on an UFD using sub-resultant method.
     *
     *
     *
     * @see [primitiveGCD]
     */
    @JvmStatic
    fun <T : Any> subResultantGCD(f: Polynomial<T>, g: Polynomial<T>): Polynomial<T> {
        /*
        See Algorithm 3.3.1, page 118 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen
        Created by lyc at 2020-03-01 16:02


         */
        var A: Polynomial<T>
        var B: Polynomial<T>
        if (f.degree > g.degree) {
            A = f
            B = g
        } else {
            A = g
            B = f
        }
        if (B.isZero()) {
            return A
        }
        val mc = f.mathCalculator

        @Suppress("UNCHECKED_CAST")
        val rc = mc as UFDCalculator<T>
        val a = A.cont()
        val b = B.cont()
        val d = rc.gcd(a, b)
        var g1 = mc.one
        var h1 = mc.one
        while (true) {
            val t = (A.degree - B.degree).toLong()
            val R = pseudoDivisionR(A, B)
            if (R.isZero()) {
                break
            }
            if (R.isConstant) {
                B = Polynomial.one(mc)
                break
            }
            A = B
            B = R.divide(mc.multiply(g1, mc.pow(h1, t)))
            g1 = A.first()
            h1 = mc.multiply(h1, mc.pow(mc.divide(g1, h1), t))
        }
        return d * B.toPrimitive()
    }


    //

    /**
     * Maps a polynomial `f(x^p)` to `f(x)`
     */
    private fun <T : Any> polynomialChDiv(f: Polynomial<T>, p: Int): Polynomial<T> {
        require(f.degree % p == 0)
        val d = f.degree / p
        return Polynomial.of(f.mathCalculator, d) { i ->
            f.get(i * p)
        }
    }


    @Suppress("LocalVariableName")
    internal fun <T : Any> squarefreeFactorizeChP(A: Polynomial<T>, p: Int)
            : List<Pair<Polynomial<T>, Int>> {
        //Created by lyc at 2021-04-15 22:19
        /*
        Reference:
        Algorithm 3.4.2, page 126 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen


        Explanation:
        Assume A = prod(r,A_r^r) is the squarefree factorization,


        In zero-characteristic field, we have that T = (A,A') = prod(r,A_r^{r-1})
        are the duplicated parts in A, so if we divide A by it, we get A_1.
        Repeat the process and we can get all A_r.


        In finite field Z mod p, assume A = prod(r, A_r^r), then

        A' = \sum{k} ( \prod{r != k} A_r^r) k A_k^{k-1}

        here if p | k, the term in the sum is zero,
        so the degree of A_k in A' is still k.
        (the degree should have been subtracted by 1, but the corresponding coefficient is zero)
        So we can obtain the formula:

        T = (A,A') = \prod{p !| r} A_r^{r-1} \prod{p | r} A_r^r

        So we can get all the A_k for p !| k in A/T.
        To get the remaining polynomials, we repeat the T=gcd(A, A'), A = A/T
        process until A is a constant, then all the
        terms A_k in T satisfies p | k, and we have T = U(X)^p = U(X^p) (property of Z_p)
        Then, we can factorize U(X) using the same process, while
        marking the power.


         */
        var e = 1 // record the power extracted
        var T0 = A // the remaining polynomial
        val result = arrayListOf<Pair<Polynomial<T>, Int>>()

        while (!T0.isConstant) {
            val T1 = T0.derivative()
            var T = T0.gcd(T1)
            // T contains: A_k^{k-1}, p !| k
            //             A_k^k, p | k
            var V = T0.exactDivide(T)
            // V   contains: A_k, p !| k
            var r = 0
            while (!V.isConstant) {
                /*
                if V is a constant, then T only contains A_k, p | k

                we have to reduce and extract the terms in T and V
                remaining:
                T: A_k^{k-1-r}, p !| k; A_k^{k}, p | k
                V: A_k, p !| k, k >= r
                */
                r++
                if (r % p == 0) {
                    // p | r, we can only extract p !| k, so here we
                    // eliminate those p !| l and leave p|k the same.
                    T = T.exactDivide(V)
                    // reduce power by one for all
                    // A_k p !| k, k >= r
                    r++ //next r must be p !| r
                }
                val W = T.gcd(V)
                // W: A_k^{k-1-r}, p !| k, k >= r+1
                val Ar = V.exactDivide(W)
                // A_r is the remaining one, report it
                if (!Ar.isConstant) {
                    result += Ar to e * r
                }
                // V,T should be reduced
                V = W
                T = T.exactDivide(V)
            }
            //now V is a constant,
            //T only contains A_k, p | k
            //reduce the power and record it to e
            T0 = polynomialChDiv(T, p)
            e *= p
        }
        return result
    }

    fun <T : Any> squarefreeFactorizeCh0(p: Polynomial<T>): List<Pair<Polynomial<T>, Int>> {
        if (p.degree < 1) {
            return emptyList()
        }
        if (p.degree == 1) {
            return listOf(p to 1)
        }
        /*
        Explanation:
        Assume p = \prod{r} p_r^r is the squarefree factorization, then
            p' = \sum{k} (\prod{r!=k} p_r^r) k p_k^{k-1},
        so
            f_1 = (p, p') = \prod{r >= 2} p_r^{r-1}
            g_1 = p / f_1 = \prod{r} p_r
        denote g_k = \prod{r >= k} p_k and f_k = \prod{r >= k+1} p_r^{r-k},
        then we have
            g_{k+1} = (g_k, f_k),
            f_{k+1} = f_k / g_{k+1}
            p_k     = g_k / g_{k+1}
        we use the formula above to get all p_k
         */
        val result = arrayListOf<Pair<Polynomial<T>, Int>>()
        var k = 1
        var f = p.gcd(p.derivative()) // f_k
        var g = p.exactDivide(f) // g_k
        while (g.degree > 0) {
            val h = g.gcd(f) //g_{k+1} = (g_k, f_k)
            val pk = g.exactDivide(h) //  p_k = g_k / g_{k+1}
            if (pk.degree > 0) {
                result += pk to k
            }
            f = f.exactDivide(h) // f_{k+1} = f_k / g_{k+1}
            g = h
            k++
        }
        return result
    }

    /**
     * Calculates the square-free factorization for a polynomial in a field of characteristic zero or `p`.
     *
     * The square-free factorization of a polynomial `f` is
     *
     *     f = prod(r, f_r^r)
     *     where f_r is square-free and co-prime.
     *
     * For example, polynomial `x^2 + 2x + 1` is factorized to be `(x+1)^2`, and the
     * result of this method will be a list containing only one element `(2, x+1)`.
     *
     * @return a list containing all the non-constant square-free factors with their degree
     * in [f].
     *
     *
     */
    @JvmStatic
    fun <T : Any> squarefreeFactorize(f: Polynomial<T>): List<Pair<Polynomial<T>, Int>> {
        val mc = f.mathCalculator
        val p = Math.toIntExact(mc.characteristic)
        val m = f.monic()
        return if (p == 0) {
            squarefreeFactorizeCh0(m)
        } else {
            squarefreeFactorizeChP(m, p)
        }
    }

    /**
     * Factorize the given square-free polynomial [A] to a list containing products of
     * irreducible polynomials of same degree.
     *
     * For example, suppose `A = fgh` where `f,g,h` are all irreducible and
     * `f.degree = g.degree = 1, h.degree = 2`, the result of applying this method
     * to `A` will be `[(1, fg), (2,h)]`
     *
     * @return a list of pairs, the first element in the pair is the degree of
     * irreducible polynomials, and the second is the product of them.
     */
    @Suppress("LocalVariableName")
    fun <T : Any> distinctDegreeFactorizeModP(A: Polynomial<T>, mc: ZModPCalculator<T>)
            : List<Pair<Int, Polynomial<T>>> {
        /*
        See Algorithm 3.4.3, page 126 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen
        Created by lyc at 2021-04-16 15:53

        Explanation:
        Lemma 1: if deg f = d, f | (x^{p^n} + x), then d | n.
            (For its proof, refer to Hw7 Ex4 of the encryption course,
            where the proof is done to p=2 but can generalize.
            In the proof we construct a sequence of extensions:
            F_0 = Z_p, F_1 = Z_p[x]/(f_1(x)), F_2 = F_1[x]/(f_2(x)) ...
            F_d = Z_{p^n}, and |F_k| = p^{d*d_2...*d_k})

        Lemma 2: Suppose f in Z_p[x] is an irreducible polynomial, deg f = d
            then F = Z_p[x]/(P) is a field, and |F| = p^{d}
            ( in fact, F = {a_0 + a_1 x + ... + a_{d-1} x^{d-1} | a_i in Z_p } )
            so the multiplication group F* has p^d - 1 elements.
            Therefore, g^{p^d} = g, for all g in F.
            On the other hand, if e < d, then g^{p^e} != g. (by the previous lemma)

        Suppose A = \prod_{d,k} A[d,k], deg A[d,k] = d.

        We use lemma 2 to reduce all the irreducible polynomials of degree < d.
        We construct polynomials W_r = x^{p^r},
        then gcd(A, W_1(A) - x) = \prod{d <= 1, k} A[d,k] (mod A)
        therefore, we get \prod{d <= 1, k} A[d,k], and divide A by it,
        repeat the process, we can get all the products.

         */
        var V = A // the remaining polynomial
        val X = Polynomial.oneX(mc) // the polynomial x
        var W = X // x^{p^d} (mod A)
        var d = 0 // the degree d
        val result = arrayListOf<Pair<Int, Polynomial<T>>>()
        val p = mc.p
        while (true) {
            d++
            val e = V.degree
            if (2 * d > e) {
                /*
                By lemma 1, if d > e/2, the only possible d that makes V | (x^{p^d}+x) is e.
                This means there is no intermediate factors and V should be returned.

                */
                if (e > 0) {
                    result.add(e to V)
                }
                break
            }
            W = Polynomial.powerAndMod(W, p.toLong(), V) // compute next W
            val t = W - X // x^{p^d} - x
            val Ad = t.gcd(V)
            if (!Ad.isUnit()) {
                // add non-trivial factor
                result.add(d to Ad)
                V = V.exactDivide(Ad)
                W = W.mod(V)
            }
        }
        return result
    }

    /**
     * Determines whether [f] is an irreducible polynomial in `Z_p`.
     *
     * It is required that the calculator for f is a [ZModPCalculator].
     */
    fun <T : Any> isIrreducibleModP(f: Polynomial<T>): Boolean {
        require(f.mathCalculator is ZModPCalculator) {
            "A ZModPCalculator is required!"
        }
        if (f.degree <= 1) {
            return true
        }
        /*
        //Created by lyc at 2021-04-16 21:06

        See Proposition 3.4.4, page 127 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen

        Explanation:
        Proposition: deg f = n, f is irreducible iff the following two conditions are satisfied:
            1. X^{p^n} - X = 0 (mod A)
            2. (X^{p^{n/q}} - X, A) = 1 for all primes q | n.

        To prove the proposition, we introduce the following two lemma.

        Lemma 1: if f is irreducible and deg f = d, f | (x^{p^n} + x), then d | n.
            (For its proof, refer to Hw7 Ex4 of the encryption course,
            where the proof is done to p=2 but can generalize.
            In the proof we construct a sequence of extensions:
            F_0 = Z_p, F_1 = Z_p[x]/(f_1(x)), F_2 = F_1[x]/(f_2(x)) ...
            F_d = Z_{p^n}, and |F_k| = p^{d*d_2...*d_k})

        Lemma 2: Suppose f in Z_p[x] is an irreducible polynomial, deg f = d
            then F = Z_p[x]/(P) is a field, and |F| = p^{d}
            ( in fact, F = {a_0 + a_1 x + ... + a_{d-1} x^{d-1} | a_i in Z_p } )
            so the multiplication group F* has p^d - 1 elements.
            Therefore, g^{p^d} = g, for all g in F.
            On the other hand, if e < d, then g^{p^e} != g. (by the previous lemma)

        Sufficiency:
        Suppose all the irreducible factors of f are p_1 ... p_r, deg p_i = d_i,
        then condition 1 implies that all d_i | n.
        If d_i < n, then there exists a prime q, d_i | (n/q),
        then p_i | (X^{p^{n/q}} - X, A), which contradicts to condition 2.
        Therefore d_i = n, so f is irreducible.

        Necessity: Obvious by lemma 2.
         */
        val mc = f.mathCalculator as ZModPCalculator
        val p = mc.p
        val n = f.degree
        val x = Polynomial.oneX(mc)
        val xPn = Polynomial.powerAndMod(x, MathUtils.power(p.toLong(), n), f)
        // x^{p^n}
        if (!xPn.valueEquals(x)) {
            return false
        }
        val factors = MathUtils.factorReduce(n.toLong())
        for (factor in factors) {
            val t = (n / factor[0]).toInt()
            val xPnq = Polynomial.powerAndMod(x, MathUtils.power(p.toLong(), t), f)
            // x^{p^{n/q}}
            val g = (xPnq - x).gcd(f)
            if (!g.isUnit()) {
                return false
            }
        }
        return true
    }


    /**
     * Returns a random monic polynomial of degree >= 1.
     */
    private fun randomPolynomial(rd: Random, d: Int, mc: ZModPCalculator<Int>): Polynomial<Int> {
        val degree = rd.nextInt(2 * d - 1) + 1
        val p = mc.p
        return Polynomial.of(mc, degree) { i ->
            if (i == degree) {
                1
            } else {
                rd.nextInt(p)
            }
        }
    }

    private fun cantorZassSplit0(d: Int, f: Polynomial<Int>, mc: ZModPCalculator<Int>,
                                 rd: Random, list: MutableList<Polynomial<Int>>) {
        if (f.degree == d) {
            // trivial case
            list += f
            return
        }
        /*
        //Created by lyc at 2021-04-16 21:44
        See Proposition 3.4.6, page 128 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen

        Explanation:

        We have the proposition:
            T^{p^d} - T = T (T^{(p^d-1)/2} + 1)(T^{(p^d-1)/2} - 1) = T T_1 T_2
            If A = \prod{k} A_k, where deg A_k = d and A_k are co-prime irreducible,
            then A = (A,T) (A, T_1) (A, T_2)

        Use the proposition, if we can find T such that (A, T_1) is non-trivial,
        then we can decompose A.
        It can be proven that for random monic T with deg T <= 2d-1,
        the probability of (A, T_1) is non-trivial is close to 0.5.

         */

        while (true) {
            val t = randomPolynomial(rd, d, mc)
            val power = (MathUtils.power(mc.p.toLong(), d) - 1) / 2
            val tp = Polynomial.powerAndMod(t, power, f) - Polynomial.one(mc)
            val g = f.gcd(tp)
            if (g.degree == 0 || g.degree == f.degree) {
                continue
            }
            val h = f.exactDivide(g)
            cantorZassSplit0(d, g, mc, rd, list)
            cantorZassSplit0(d, h, mc, rd, list)
            break
        }
    }

    /**
     * Uses Cantor-Zassenhaus method to split a square-free product of
     * irreducible polynomials of degree [d], returns a list of factors.
     *
     * It is required that `p > 2`.
     *
     * @return a list of all it irreducible factors
     */
    fun cantorZassSplit(d: Int, product: Polynomial<Int>, mc: ZModPCalculator<Int>)
            : List<Polynomial<Int>> {
        require(mc.p > 2)
        val results = arrayListOf<Polynomial<Int>>()
        val rd = Random()
        cantorZassSplit0(d, product, mc, rd, results)
        return results
    }

    private fun polySplitZMod2Recur(a: Polynomial<Int>, d: Int, mc: ZModPCalculator<Int>,
                                    list: MutableList<Polynomial<Int>>) {
        /*
        //Created by lyc at 2021-04-16 22:44
        See Proposition 3.4.6, page 129 of
        'A Course in Computational Algebraic Number Theory', Henri Cohen
         */

        if (a.degree == d) {
            list += a
            return
        }
        var t = Polynomial.oneX(mc)
        while (true) {
            var c = t
            repeat(d - 1) {
                c = (t + c * c).mod(a)
            }
            val b = a.gcd(c)
            if (b.degree == 0 || b.degree == a.degree) {
                t *= Polynomial.powerX(2, mc) // t = t * (x^2)
                continue
            }
            val aDb = a.exactDivide(b)
            polySplitZMod2Recur(b, d, mc, list)
            polySplitZMod2Recur(aDb, d, mc, list)
            break
        }
    }

    /**
     * Splits a square-free polynomial [f] whose irreducible factors are all of degree [d]
     * in `Z_2`.
     *
     * @return a list of all it irreducible factors
     */
    fun polySplitZMod2(d: Int, f: Polynomial<Int>, mc: ZModPCalculator<Int>)
            : List<Polynomial<Int>> {
        require(mc.p == 2)
        val results = arrayListOf<Polynomial<Int>>()
        polySplitZMod2Recur(f, d, mc, results)
        return results
    }

    /**
     * Factorize the given polynomial in `Z_p`, where `p` is a prime.
     *
     * It is required that the calculator for f is a [ZModPCalculator].
     *
     * @return a [DecomposedPoly] that represents the factorization of [f]
     */
    @JvmStatic
    fun factorizeModP(f: Polynomial<Int>): DecomposedPoly<Int> {
        val squarefree = squarefreeFactorize(f)
        val results = arrayListOf<Pair<Polynomial<Int>, Int>>()
        val mc = f.mathCalculator as ZModPCalculator<Int>
        val p = mc.p
        for ((ak, k) in squarefree) {
            val distinct = distinctDegreeFactorizeModP(ak, mc)
//            println(ak)
//            println(distinct)
            for ((d, product) in distinct) {
                val factors = if (p == 2) {
                    polySplitZMod2(d, product, mc)
                } else {
                    cantorZassSplit(d, product, mc)
                }
                for (factor in factors) {
                    results.add(factor to k)
                }
            }
        }
        results.sortBy {
            it.first
        }
        return DecomposedPoly(results)
    }


}


fun main() {
//    val mc = Calculators.intModP(17)
//    val fc = Fraction.calculator
//    val A = Polynomial.of(mc, 1, 0, 1)//.mapTo(fc) {Fraction.of(it.toLong())}
//    val B = Polynomial.of(mc, 2, 1)//.mapTo(fc) {Fraction.of(it.toLong())}
//    val V = A * B
////    println(Polynomial.powerAndMod(Polynomial.oneX(mc),17L,A*B))
////    println(PolynomialSup.squarefreeFactorizeModP(A*A*B))
////    println(PolynomialSup.distinctDegreeFactorizeModP(A * B, mc))
    val mc = Calculators.intMod2()

    val f = Polynomial.parse("1+x+x^3+x^4+x^6", mc, String::toInt)//.mapTo(fc) { Fraction.of(it.toLong()) }
    val g = Polynomial.parse("x^4+x", mc, String::toInt)//.mapTo(fc) { Fraction.of(it.toLong()) }
//    println(PolynomialSup.squarefreeFactorizeModP(f))
//    val factors = PolynomialUtil.factorizeModP(f)
//    println(factors)
    println(PolynomialUtil.factorizeModP(g))
    println(PolynomialUtil.isIrreducibleModP(f))
//    val t = Polynomial.parse("x^12 + x^9 + x^6 + x^3 + 1",mc,String::toInt)
//    println(PolynomialSup.polySplitZMod2(4,t,mc))
}

//fun main(args: Array<String>) {
//    val mc = Calculators.getCalLongExact()
////    val nume = Polynomial.valueOf(mc,1L,0L,0L,1L)
////    val deno = Polynomial.valueOf(mc,0L,-1L,3L,-3L,1L)
//    val nume = Polynomial.valueOf(mc, 6L, 5L)
//    val deno = Polynomial.valueOf(mc, 1L, 1L, 1L)
//
//    println("${MathSymbol.INTEGRAL} ($nume) / ($deno) dx")
////    println(AlgebraUtil.partialFractionInt(nume,deno))
//    val ec = ExprCalculator.instance
//    ec.setProperty(SimplificationStrategies.PROP_MERGE_FRACTION, "false")
//    ec.setProperty(SimplificationStrategies.PROP_ENABLE_EXPAND, "false")
//    val inte = Calculus.intRational(nume, deno, ec, "x")
//    println(inte)
//    ec.setProperty(SimplificationStrategies.PROP_ENABLE_EXPAND, "true")
//    println(ec.differential(inte))
////    println(AlgebraUtil.polynomialBernoulli(6))
////    println(AlgebraUtil.polynomialBernoulliBig(20))
//}