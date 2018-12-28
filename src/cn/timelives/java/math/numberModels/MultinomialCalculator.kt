package cn.timelives.java.math.numberModels

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.exceptions.UnsupportedCalculationException
import cn.timelives.java.math.numberModels.api.Simplifier

import java.math.BigInteger
import java.util.*

import cn.timelives.java.math.numberModels.Multinomial.*
import cn.timelives.java.math.numberTheory.NTCalculator
import java.lang.UnsupportedOperationException

@Suppress("NAME_SHADOWING")
class MultinomialCalculator : MathCalculator<Multinomial>, NTCalculator<Multinomial> {

    override val isComparable: Boolean
        get() = true

    override val zero: Multinomial
        get() = ZERO

    override val one: Multinomial
        get() = ONE

    override val numberClass: Class<Multinomial>
        get() = Multinomial::class.java

    override fun asBigInteger(x: Multinomial): BigInteger {
        if(x.isMonomial && x.first.isInteger){
            return x.first.let { t ->
                val n = x.first.numerator
                when{
                    t.signum >=0 -> n
                    else -> -n
                }
            }
        }
        throw UnsupportedOperationException()
    }


    private class Pair internal constructor(internal val n: BigInteger, internal val d: BigInteger) {
        override fun hashCode(): Int {
            return n.hashCode() * 31 + d.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (other is Pair) {
                val p = other as Pair?
                return n == p!!.n && d == p.d
            }
            return false
        }

        override fun toString(): String {
            return "[" + n.toString() + "," + d.toString() + "]"
        }

        companion object {
            internal fun of(arr: Array<BigInteger>): Pair {
                return Pair(arr[0], arr[1])
            }
        }
    }


    override fun isEqual(x: Multinomial, y: Multinomial): Boolean {
        return x == y
    }

    override fun compare(x: Multinomial, y: Multinomial): Int {
        return x.compareTo(y)
    }


    override fun add(x: Multinomial, y: Multinomial): Multinomial {
        return x.add(y)
    }

    override fun addX(vararg ps: Any): Multinomial {
        if (ps.isEmpty()) {
            return ZERO
        }
        val result = getSet()
        for (m in ps) {
            mergingAddAll(result, (m as Multinomial).terms)
        }
        return Multinomial(result)
    }

    override fun negate(x: Multinomial): Multinomial {
        return x.negate()
    }

    override fun abs(para: Multinomial): Multinomial {
        return para
    }

    override fun subtract(x: Multinomial, y: Multinomial): Multinomial {
        return x.subtract(y)
    }

    override fun isZero(para: Multinomial): Boolean {
        return para == ZERO
    }

    override fun multiply(x: Multinomial, y: Multinomial): Multinomial {
        return x.multiply(y)
    }

    override fun multiplyX(vararg ps: Any): Multinomial {
        if (ps.isEmpty()) {
            return ONE
        }
        var result = singleTerm(Term.ONE)
        for (m in ps) {
            result = mergingMultiply(result, (m as Multinomial).terms)
        }
        return Multinomial(result)
    }

    override fun divide(x: Multinomial, y: Multinomial): Multinomial {
        return x.divide(y)
    }

    override fun reciprocal(x: Multinomial): Multinomial {
        return x.reciprocal()
    }

    override fun multiplyLong(x: Multinomial, n: Long): Multinomial {
        return x.multiply(Term.valueOf(n))
    }

    override fun divideLong(x: Multinomial, n: Long): Multinomial {
        return x.divide(Term.valueOf(n))
    }

    override fun squareRoot(x: Multinomial): Multinomial {
        if (x.isMonomial) {
            return monomial(x.first.squareRoot())
        }
        throw UnsupportedCalculationException("Too complex")
    }

    override fun nroot(x: Multinomial, n: Long): Multinomial {
        if (n < 0) {
            return nroot(x, n).reciprocal()
        }
        if (n == 0L) {
            throw ArithmeticException("nroot for n = 0")
        }
        if (n == 1L) {
            return x
        }
        if (n == 2L) {
            return squareRoot(x)
        }
        if (x.isMonomial) {
            return monomial(Term.nroot(x.first, n))
        }
        throw UnsupportedCalculationException("Too complex")
    }

    override fun pow(x: Multinomial, n: Long): Multinomial {
        return x.pow(Math.toIntExact(n))
    }

    override fun constantValue(name: String): Multinomial {
        when (name) {
            MathCalculator.STR_PI -> return PI
            MathCalculator.STR_E -> return E
            MathCalculator.STR_I -> return I
        }
        throw UnsupportedCalculationException(name)
    }

    override fun exp(a: Multinomial, b: Multinomial): Multinomial {
        if (a == ZERO) {
            if (b == ZERO) {
                throw ArithmeticException("0^0")
            }
            return ZERO
        }
        if (a == ONE) {
            return ONE
        }
        if (b.isMonomial) {
            val t = b.first
            if (t.isInteger) {
                val l = t.numerator.toLong()
                return pow(a, if(t.isNegative){-l}else{l})
            }
        }
        throw UnsupportedCalculationException()
    }

    override fun exp(x: Multinomial): Multinomial {
        if (x.isMonomial) {
            val t = x.first
            if (t.isFraction) {
                //                    throw new ArithmeticException("Too big for pow = "+p);
                return monomial(Term.characterPower(Term.E_STR,t.toFraction()))
            }
        }
        throw UnsupportedCalculationException()
    }

    override fun log(a: Multinomial, b: Multinomial): Multinomial {
        throw UnsupportedCalculationException()//TODO
    }

    override fun ln(x: Multinomial): Multinomial {
        if(!x.isMonomial){
            throw UnsupportedCalculationException()
        }
        val t = x.first
        if(t == Term.ONE){
            return ZERO
        }
        if(t.haveSameChar(Term.E) && t.isCoefficientOne){
            return monomial(Term.valueOf(t.getCharacterPower(MathCalculator.STR_E)))
        }
        throw UnsupportedCalculationException()
    }

    override fun sin(x: Multinomial): Multinomial {
        if (x.isMonomial) {
            val t = x.first
            val re = sinf(t)
            if (re != null) {
                return re
            }
        }
        throw UnsupportedCalculationException("Can't calculate sin")
    }

    override fun cos(x: Multinomial): Multinomial {
        if (x.isMonomial) {
            val t = x.first
            val re = cosf(t)
            if (re != null) {
                return re
            }
        }
        throw UnsupportedCalculationException("Can't calculate cos")
    }

    private fun sinf(f: Term): Multinomial? {
        var f = f
        if (f.isZero) {
            //sin(0) = 1
            return ZERO
        }
        if (f.haveSameChar(Term.PI)) {
            if (f.radical != BigInteger.ONE) {
                return null
            }
            // ... pi
            var nega = !f.isPositive
            if (nega)
                f = f.negate()
            val nd = arrayOf(f.numerator, f.denominator)
            //now in [0,2Pi]
            reduceByTwoPi(nd)
            nega = reduceIntoPi(nd, nega)
            //into pi.
            //sin(x) = sin(pi-x)
            subtractToHalf(nd, true)

            //			f = Formula.c
            var result: Multinomial? = SIN_VALUE[Pair.of(nd)]
            if (result != null) {
                if (nega) {
                    result = negate(result)
                }
            }
            return result
        }
        return null
    }

    private fun cosf(f: Term): Multinomial? {
        var f = f
        if (f.isZero) {
            //cos(0) = 1
            return ONE
        }
        if (f.haveSameChar(Term.PI)) {
            if (f.radical != BigInteger.ONE) {
                return null
            }
            // ... pi
            var nega = false
            if (!f.isPositive) {
                f = f.negate()
            }

            val nd = arrayOf(f.numerator, f.denominator)
            //cos(x) = sin(pi/2+x)
            //add 1/2 to nd
            addHalfPi(nd)
            reduceByTwoPi(nd)
            //now in [0,2Pi]
            nega = reduceIntoPi(nd, nega)
            //sin(x) = sin(pi-x)
            subtractToHalf(nd, nega)

            var result: Multinomial? = SIN_VALUE[Pair.of(nd)]//cos(x) = sin(pi/2+x),in the first we added pi/2.
            if (result != null) {
                if (nega) {
                    result = negate(result)
                }
            }
            return result
        }
        return null
    }

    private fun addHalfPi(nd: Array<BigInteger>) {
        val mod = nd[1].divideAndRemainder(BigInteger.valueOf(2))
        if (mod[1] == BigInteger.ZERO) {
            nd[0] = nd[0].add(mod[0])
        } else {
            nd[0] = nd[0].add(nd[0]).add(nd[1])
            nd[1] = nd[1].add(nd[1])
        }
    }

    override fun tan(x: Multinomial): Multinomial {
        if (x.isMonomial) {
            val t = x.first
            val re = tanf(t)
            if (re != null) {
                return re
            }
        }
        throw UnsupportedCalculationException("Can't calculate tan")
    }

    private fun tanf(f: Term): Multinomial? {
        var f = f
        if (f.isZero) {
            //tan(0) = 0
            return ZERO
        }
        if (f.haveSameChar(Term.PI)) {
            if (f.radical == BigInteger.ONE == false) {
                return null
            }
            var nega = !f.isPositive
            if (nega)
                f = f.negate()
            val nd = arrayOf(f.numerator(), f.denominator())
            reduceByPi(nd)
            //into pi,but we need in [0,1/2)pi.
            if (nd[1] == nd[0].multiply(BigInteger.valueOf(2L))) {
                throw ArithmeticException("tan(Pi/2)")
            }
            nega = subtractToHalf(nd, nega)
            var result: Multinomial? = TAN_VALUE[Pair.of(nd)]
            if (result != null) {
                if (nega)
                    result = negate(result)
                return result
            }
        }
        return null

    }

    fun cot(x: Multinomial): Multinomial {
        if (x.isMonomial) {
            val t = x.first
            val re = cotf(t)
            if (re != null) {
                return re
            }
        }
        throw UnsupportedCalculationException("Can't calculate cot")
    }

    private fun cotf(f: Term): Multinomial? {
        var f = f
        if (f.haveSameChar(Term.PI)) {
            var nega = f.isPositive
            if (!nega)
                f = f.negate()
            val nd = arrayOf(f.numerator(), f.denominator())
            addHalfPi(nd)
            reduceByPi(nd)
            nega = !nega//cot(x) = -cot(x+Pi/2)
            //into pi,but we need in [0,1/2)pi.
            if (nd[1] == nd[0].multiply(BigInteger.valueOf(2L))) {
                throw ArithmeticException("cot(0)")
            }
            nega = subtractToHalf(nd, nega)
            var result: Multinomial? = TAN_VALUE[Pair.of(nd)]
            if (result != null) {
                if (!nega)
                    result = result.negate()
                return result
            }
        }
        return null

    }

    override fun arcsin(x: Multinomial): Multinomial {
        var result: Multinomial? = ARCSIN_VALUE[x]
        if (result != null)
            return result
        result = ARCSIN_VALUE[negate(x)]
        //try negative value
        if (result != null)
            return negate(result)
        //this step deals with the undefined number
        if (x.isMonomial) {
            val f = x.first
            //f should be a constant value = [-1,1]
            if (f.hasNoChar()) {
                if (f.compareTo(Term.ONE) > 0 || f.compareTo(Term.NEGATIVE_ONE) < 0)
                    throw ArithmeticException("Arcsin undifined  :  " + f.toString())
            }
        }
        throw UnsupportedCalculationException()
    }

    override fun arccos(x: Multinomial): Multinomial {
        //arccos(x) + arcsin(x) = Pi/2 --> arccos(x) = Pi/2 - arcsin(x)
        return monomial(Term.valueOf("Pi/2")).subtract(arcsin(x))
    }

    override fun arctan(x: Multinomial): Multinomial {
        var result: Multinomial? = ARCTAN_VALUE[x]
        if (result != null)
            return result
        result = ARCTAN_VALUE[x.negate()]
        //try negative value
        if (result != null)
            return negate(result)
        throw UnsupportedCalculationException()
    }

    override fun isInteger(x: Multinomial): Boolean {
        return true
    }

    override fun isQuotient(x: Multinomial): Boolean {
        return true
    }

    override fun mod(a: Multinomial, b: Multinomial): Multinomial {
        return a.divideAndRemainder(b)[1]
    }

    override fun divideToInteger(a: Multinomial, b: Multinomial): Multinomial {
        return a.divideAndRemainder(b)[0]
    }

    override fun isPositive(x: Multinomial): Boolean {
        return x != ZERO
    }

    override fun isNegative(x: Multinomial): Boolean {
        return false
    }

    override fun gcd(a: Multinomial, b: Multinomial): Multinomial {
        val comp = a.compareTo(b)
        if(comp == 0){
            return a
        }
        var a1 : Multinomial
        var b1 : Multinomial
        if(comp > 0){
            a1 = a
            b1 = b
        }else{
            a1 = b
            b1 = a
        }
        if (ZERO == a1) {
            return b1
        }
        while (ZERO != b1) {
            val t = b1
            b1 = mod(a1, b1)
            if (a1 == b1) {
                return ONE
            }
            a1 = t
        }
        return a1
    }

    override fun divideAndReminder(a: Multinomial, b: Multinomial): cn.timelives.java.utilities.structure.Pair<Multinomial, Multinomial> {
        val arr = a.divideAndRemainder(b)
        return cn.timelives.java.utilities.structure.Pair(arr[0], arr[1])
    }

    internal class MSimplifier : Simplifier<Multinomial> {

        override fun simplify(numbers: List<Multinomial>): List<Multinomial> {
            var numbers = numbers
            numbers = Multinomial.reduceGcd(numbers)
            if(numbers.size == 2){
                val pair =  simplify(numbers[0],numbers[1])
                return listOf(pair.first,pair.second)
            }
            return numbers
        }

        override fun simplify(x: Multinomial): Multinomial {
            return x
        }

        override fun simplify(a: Multinomial, b: Multinomial): cn.timelives.java.utilities.structure.Pair<Multinomial, Multinomial> {
            val arr = Multinomial.simplifyFraction(a, b)
            return cn.timelives.java.utilities.structure.Pair(arr[0], arr[1])
        }
    }

    companion object {
        private fun ofVal(n: Long, d: Long): Pair {
            return Pair(BigInteger.valueOf(n), BigInteger.valueOf(d))
        }

        /**
         * SIN_VALUE stores the sin result stored in 0 to Pi/2
         *
         *
         */
        private val SIN_VALUE: MutableMap<Pair, Multinomial> = HashMap()

        private val TAN_VALUE: MutableMap<Pair, Multinomial> = HashMap()

        /**
         * this Map contains arcsin values
         * @see .SIN_VALUE
         */
        val ARCSIN_VALUE: MutableMap<Multinomial, Multinomial> = TreeMap()
        /**
         * this Map contains arctan values
         * @see .TAN_VALUE
         */
        val ARCTAN_VALUE: MutableMap<Multinomial, Multinomial> = TreeMap()

        init {
            initValue()
        }

        private fun initValue() {
            //		try {
            //			PolyCalculator.class.getClassLoader().loadClass(Multinomial.class.getName());
            //		} catch (ClassNotFoundException e1) {
            //			e1.printStackTrace();
            //		}

            SIN_VALUE[ofVal(0L, 1L)] = ZERO
            // sin(0) = 0
            SIN_VALUE[ofVal(1L, 6L)] = monomial(
                    Term.asFraction(1, 2, 1))
            //sin(Pi/6) = 1 / 2
            SIN_VALUE[ofVal(1L, 4L)] = monomial(
                    Term.asFraction(1, 2, 2))
            //sin(Pi/4) =sqr(2)/2
            SIN_VALUE[ofVal(1L, 3L)] = monomial(
                    Term.asFraction(1, 2, 3))
            //sin(Pi/3) = sqr(3) / 2
            SIN_VALUE[ofVal(1L, 2L)] = ONE
            //sin(Pi/2) = 1

            SIN_VALUE[ofVal(1L, 12L)] = valueOf("Sqr6/4-Sqr2/4")
            //sin(Pi/12) = sqr6/4-sqr2/4

            SIN_VALUE[ofVal(5L, 12L)] = valueOf("Sqr6/4+Sqr2/4")
            //sin(Pi/12) = sqr6/4-sqr2/4

            TAN_VALUE[ofVal(0L, 1L)] = ZERO
            // tan(0) = 0
            TAN_VALUE[ofVal(1L, 6L)] = monomial(
                    Term.asFraction(1, 3, 3))
            //tan(Pi/6) = Sqr(3)/3
            TAN_VALUE[ofVal(1L, 4L)] = monomial(Term.ONE)
            //tan(Pi/4) = 1
            TAN_VALUE[ofVal(1L, 3L)] = monomial(
                    Term.asFraction(1, 1, 3))
            //tan(Pi/3) = Sqr(3)

            TAN_VALUE[ofVal(1L, 12L)] = valueOf("2-Sqr3")
            //tan(Pi/12) = 2-Sqr3
            TAN_VALUE[ofVal(5L, 12L)] = valueOf("2+Sqr3")
            //tan(Pi/12) = 2+Sqr3

            for ((p, value) in SIN_VALUE) {
                ARCSIN_VALUE[value] = monomial(Term.asFraction(p.n, p.d, BigInteger.ONE))
            }
            for ((p, value) in TAN_VALUE) {
                ARCTAN_VALUE[value] = monomial(Term.asFraction(p.n, p.d, BigInteger.ONE))
            }
        }


        private fun reduceByTwoPi(arr: Array<BigInteger>) {
            //firstly get the Numerator
            var nume = arr[0]
            var deno = arr[1]
            deno = deno.add(deno)
            // reduce by two.
            nume = nume.mod(deno)
            arr[0] = nume
        }

        private fun reduceByPi(arr: Array<BigInteger>) {
            // firstly get the Numerator
            var nume = arr[0]
            val deno = arr[1]
            // reduce by two.
            nume = nume.mod(deno)
            arr[0] = nume
        }

        private fun reduceIntoPi(nd: Array<BigInteger>, nega: Boolean): Boolean {
            var nega = nega
            if (nd[0].compareTo(nd[1]) > 0) {
                nega = !nega
                nd[0] = nd[0].subtract(nd[1])
            }
            return nega
        }

        private fun subtractToHalf(nd: Array<BigInteger>, nega: Boolean): Boolean {
            var nega = nega
            val half = nd[1].divide(BigInteger.valueOf(2))
            if (nd[0].compareTo(half) > 0) {
                nd[0] = nd[1].subtract(nd[0])
                nega = !nega
            }
            return nega

        }
    }


}
