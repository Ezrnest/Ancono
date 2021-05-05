/**
 *
 */
package cn.ancono.math.function

import cn.ancono.math.MathCalculatorHolder
import cn.ancono.math.MathObject
import cn.ancono.math.algebra.IPolynomial
import cn.ancono.math.calculus.Calculus
import cn.ancono.math.calculus.Calculus.derivation
import cn.ancono.math.calculus.Integrable
import cn.ancono.math.numberModels.CalculatorUtils
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import cn.ancono.math.numberModels.api.RealCalculator
import cn.ancono.utilities.ArraySup
import java.util.*
import java.util.function.Function
import kotlin.math.max

/**
 * A class providing single variable polynomial functions.
 * @author liyicheng
 * @see SVPFunction
 */
@Suppress("UNCHECKED_CAST")
abstract class AbstractSVPFunction<T>
/**
 * @param mc
 */
protected constructor(mc: RealCalculator<T>, internal val mp: Int) : AbstractSVFunction<T>(mc), SVPFunction<T>, DerivableSVFunction<T>, Integrable<T> {

    override fun getDegree(): Int {
        if (mp > 0) {
            return mp
        }
        if (mc.isZero(constant())) {
            return -1
        }
        return mp
    }

    /**
     * The default implement for computing the value f(x), it is calculated
     * through f(x) = a0+x(a1+x(a2+.....)
     */
    override fun apply(x: T): T {
        var re = get(mp)
        for (i in mp - 1 downTo -1 + 1) {
            re = mc.multiply(x, re)
            re = mc.add(get(i), re)
        }
        return re
    }

    override val derivative: AbstractSVPFunction<T> by lazy {
        derivation(this)
    }

    /**
     * Returns the derivation of this function.
     * @return
     */
    override fun derive(): AbstractSVPFunction<T> {
        return derivative
    }

    /*
	 * @see cn.ancono.math.calculus.Integrable#integrate()
	 */
    override fun integrate(): AbstractSVPFunction<T> {
        return Calculus.integrate(this)
    }

    /* (non-Javadoc)
	 * @see cn.ancono.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.math.number_models.MathCalculator)
	 */
    abstract override fun <N> mapTo(
            newCalculator: RealCalculator<N>,
            mapper: Function<T, N>
    ): AbstractSVPFunction<N>


    /**
     * Compares whether the another one is also a SVPFunction and determines whether
     * they are equal
     */
    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is SVPFunction<*>) {
            return false
        }
        if (obj === this) {
            return true
        }
        val f = obj as SVPFunction<T>
        return SVPFunction.isEqual(this, f) { para1, para2 -> mc.isEqual(para1, para2) }
    }

    /* (non-Javadoc)
	 * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject, java.util.function.Function)
	 */
    override fun <N> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean {
        if (obj !is SVPFunction<*>) {
            return false
        }
        if (obj === this) {
            return true
        }
        val f = obj as SVPFunction<N>
        return IPolynomial.isEqual(this, f, CalculatorUtils.mappedIsEqual(mc, mapper))
    }

    /* (non-Javadoc)
	 * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.number_models.NumberFormatter)
	 */
    override fun toString(nf: FlexibleNumberFormatter<T>): String {
        return IPolynomial.stringOf(this, mc, nf)
    }


    internal class SVPFunctionImpl1<T>
    /**
     * @param mc
     * @param maxp
     */
    (mc: RealCalculator<T>, maxp: Int, val coes: Array<T>) : AbstractSVPFunction<T>(mc, maxp) {
        /* (non-Javadoc)
		 * @see cn.ancono.math.function.SVFunction#apply(java.lang.Object)
		 */
        override fun apply(x: T): T {
            var re = coes[mp]
            for (i in mp - 1 downTo -1 + 1) {
                re = mc.multiply(x, re)
                re = mc.add(coes[i], re)
            }
            return re
        }

        /* (non-Javadoc)
		 * @see cn.ancono.math.function.AbstractSVPFunction#getCoefficient(int)
		 */
        override fun get(n: Int): T {
            return coes[n]
        }

        /* (non-Javadoc)
		 * @see cn.ancono.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.math.number_models.MathCalculator)
		 */
        override fun <N> mapTo(
                newCalculator: RealCalculator<N>,
                mapper: Function<T, N>
        ): SVPFunctionImpl1<N> {
            return SVPFunctionImpl1(newCalculator, mp, ArraySup.mapTo(coes, mapper))
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is SVPFunctionImpl1<*>) return false
            if (!super.equals(other)) return false

            if (!coes.contentEquals(other.coes)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + coes.contentHashCode()
            return result
        }


    }

    /**
     * An implement for [AbstractSVPFunction] which uses map to store data. This implement
     * is aimed to be used
     *
     *
     * @author liyicheng
     *
     * @param <T>
    </T> */
    internal class SVPFunctionImpl2<T>
    /**
     *
     */
    (mc: RealCalculator<T>, mp: Int, val map: Map<Int, T>) : AbstractSVPFunction<T>(mc, mp) {

        /* (non-Javadoc)
		 * @see cn.ancono.math.function.AbstractSVPFunction#getCoefficient(int)
		 */
        override fun get(n: Int): T {
            return map.getOrDefault(n, mc.zero)
        }

        /* (non-Javadoc)
		 * @see cn.ancono.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.math.number_models.MathCalculator)
		 */
        override fun <N> mapTo(
                newCalculator: RealCalculator<N>,
                mapper: Function<T, N>
        ): SVPFunctionImpl2<N> {
            val nmap = HashMap<Int, N>(map.size)
            for ((key, value) in map) {
                nmap[key] = mapper.apply(value)
            }
            return SVPFunctionImpl2(newCalculator, mp, nmap)
        }

        /* (non-Javadoc)
		 * @see cn.ancono.math.FlexibleMathObject#hashCode()
		 */
        override fun hashCode(): Int {
            return mc.hashCode() * 31 + map.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is SVPFunctionImpl2<*>) return false
            if (!super.equals(other)) return false

            if (map != other.map) return false

            return true
        }
    }

    /**
     * Describe the linear function.
     * @author liyicheng
     * 2017-10-07 15:08
     *
     * @param <T>
    </T> */
    class LinearFunction<T>
    /**
     * @param mc
     */
    internal constructor(mc: RealCalculator<T>, a: T, b: T) : AbstractSVPFunction<T>(mc, 1) {
        private val a: T = Objects.requireNonNull(a)
        private val b: T = Objects.requireNonNull(b)

        init {
            if (mc.isZero(a)) {
                throw IllegalArgumentException("a==0")
            }
        }

        /*
		 * @see cn.ancono.math.function.AbstractSVPFunction#getCoefficient(int)
		 */
        override fun get(n: Int): T {
            when (n) {
                0 -> {
                    return b
                }
                1 -> {
                    return a
                }
            }
            throw IndexOutOfBoundsException()
        }

        /*
		 * @see cn.ancono.math.function.AbstractSVPFunction#mapTo(java.util.function.Function, cn.ancono.math.numberModels.api.MathCalculator)
		 */
        override fun <N> mapTo(newCalculator: RealCalculator<N>, mapper: Function<T, N>): LinearFunction<N> {
            return LinearFunction(newCalculator, mapper.apply(a), mapper.apply(b))
        }

    }

    /**
     * A constant function is a type of MathFunction that
     * always returns the identity result.
     * @author
     */
    class ConstantFunction<T> internal constructor(mc: RealCalculator<T>,
                                                   /**
                                                    * Returns the result.
                                                    * @return
                                                    */
                                                   val result: T) : AbstractSVPFunction<T>(mc, 0), SVFunction<T> {


        /*
		 * @see cn.ancono.math.function.MathFunction#apply(java.lang.Object)
		 */
        override fun apply(x: T): T {
            return result
        }


        /*
		 * @see cn.ancono.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.math.numberModels.api.MathCalculator)
		 */
        override fun <N> mapTo(newCalculator: RealCalculator<N>, mapper: Function<T, N>): ConstantFunction<N> {
            return ConstantFunction(newCalculator, mapper.apply(result))
        }

        override fun <S> mapTo(mapper: Bijection<T, S>): DerivableSVFunction<S> {
            return super<AbstractSVPFunction>.mapTo(mapper)
        }

        /*
                 * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
                 */
        override fun valueEquals(obj: MathObject<T>): Boolean {
            return if (obj !is ConstantFunction<*>) {
                false
            } else mc.isEqual(result, (obj as ConstantFunction<T>).result)
        }

        /*
		 * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.numberModels.api.NumberFormatter)
		 */
        override fun toString(nf: FlexibleNumberFormatter<T>): String {
            return nf.format(result)
        }


        /*
		 * @see cn.ancono.math.function.SVPFunction#getCoefficient(int)
		 */
        override fun get(n: Int): T {
            if (n != 0) {
                throw IndexOutOfBoundsException("n!=0")
            }
            return result
        }
    }

    companion object {


        /**
         * Creates a function with it coefficients.
         * @param mc a [RealCalculator]
         * @param coes an array of coefficients, if an element is `null`,
         * then it will be considered as zero.
         * @return a new single variable polynomial function
         */
        @SafeVarargs
        fun <T> valueOf(mc: RealCalculator<T>, vararg coes: T?): AbstractSVPFunction<T> {
            var ncoes: Array<T> = Array<Any?>(coes.size) {
                if (coes[it] == null) {
                    mc.zero
                } else {
                    coes[it]
                }
            } as Array<T>

            var max = ncoes.size - 1
            while (mc.isZero(ncoes[max])) {
                max--
            }
            ncoes = ncoes.copyOf(max + 1) as Array<T>
            return SVPFunctionImpl1(mc, max, ncoes)
        }

        /**
         * Creates a function with it coefficients as a list.
         * @param coes an list of coefficients, `null` values are unacceptable.
         * @param mc a [RealCalculator]
         * @return a new single variable polynomial function
         */
        fun <T> valueOf(coes: List<T>, mc: RealCalculator<T>): AbstractSVPFunction<T> {
            val arr = coes.toTypedArray<Any?>() as Array<T>
            return SVPFunctionImpl1(mc, arr.size - 1, arr)
        }

        /**
         * Creates a function with it coefficients as a map.
         * @param coes an map of coefficients, `null` values are unacceptable.
         * @param mc a [RealCalculator]
         * @return a new single variable polynomial function
         */
        fun <T> valueOf(coes: Map<Int, T>, mc: RealCalculator<T>): AbstractSVPFunction<T> {
            val map = HashMap<Int, T>()
            var mp = 0
            for ((key, value) in coes) {

                mp = max(mp, key)
                map[key] = value
            }
            return SVPFunctionImpl2(mc, mp, map)
        }

        /**
         * Returns a constant function:
         * <pre>c</pre>
         * @param c the constant
         * @param mc a [RealCalculator]
         * @return a new ConstantFunction
         */
        fun <T> constant(c: T, mc: RealCalculator<T>): ConstantFunction<T> {
            return ConstantFunction(mc, c)
        }

        /**
         * Returns a linear function:
         * <pre>ax+b</pre>
         * It is required that `a!=0`.
         * @param a the coefficient of `x`
         * @param b the constant
         * @param mc a [RealCalculator]
         * @return a new LinearFunction
         */
        fun <T> linear(a: T, b: T, mc: RealCalculator<T>): LinearFunction<T> {
            return LinearFunction(mc, a, b)
        }

        /**
         * Returns a new quadratic function.
         * <pre>ax^2+bx+c</pre>
         * It is required that `a!=0`.
         * @param a the coefficient of `x^2`
         * @param b the coefficient of `x`
         * @param c the constant
         * @param mc a [RealCalculator]
         * @return a new QuadraticFunction
         */
        fun <T> quadratic(a: T, b: T, c: T, mc: RealCalculator<T>): QuadraticFunction<T> {
            return QuadraticFunction(mc, a, b, c)
        }

        /**
         * Returns a function from a Polynomial.
         * @param m a [IPolynomial]
         * @param mc a [RealCalculator]
         * @return an [AbstractSVPFunction]
         */
        fun <T> fromPolynomial(m: IPolynomial<T>, mc: RealCalculator<T>): AbstractSVPFunction<T> {
            if (m is AbstractSVPFunction<*>) {
                return m as AbstractSVPFunction<T>
            }
            val size = m.degree + 1
            val list = arrayOfNulls<Any>(size) as Array<T>
            for (i in 0 until size) {
                list[i] = m.get(i)
            }
            return SVPFunctionImpl1(mc, size - 1, list)
        }

        /**
         * Returns a function from a Polynomial which is also a [MathCalculatorHolder].
         * @param m a [IPolynomial]
         * @param mc a [RealCalculator]
         * @return an [AbstractSVPFunction]
         * @throws ClassCastException if `!(m instanceof MathCalculatorHolder)`;
         */
        fun <T> fromPolynomial(m: IPolynomial<T>): AbstractSVPFunction<T> {
            val holder = m as MathCalculatorHolder<T>
            return fromPolynomial(m, holder.calculator)
        }

        /**
         * Adds two functions.
         * @param p1
         * @param p2
         * @return
         */
        fun <T> add(p1: SVPFunction<T>, p2: SVPFunction<T>): AbstractSVPFunction<T> {
            val max = max(p1.degree, p2.degree)
            val coes = arrayOfNulls<Any>(max + 1) as Array<T>
            val mc = p1.calculator
            for (i in 0..max) {
                coes[i] = mc.add(p1.get(i), p2.get(i))
            }
            return valueOf(mc, *coes)
        }

        /**
         * Subtracts two functions.
         * @param p1
         * @param p2
         * @return
         */
        fun <T> subtract(p1: SVPFunction<T>, p2: SVPFunction<T>): AbstractSVPFunction<T> {
            val max = max(p1.degree, p2.degree)
            val coes = arrayOfNulls<Any>(max + 1) as Array<T>
            val mc = p1.calculator
            for (i in 0..max) {
                coes[i] = mc.subtract(p1.get(i), p2.get(i))
            }
            return valueOf(mc, *coes)
        }

        private val MAX_ARRAY_THREHOLD = 128

        /**
         * Multiplies the two SVPFunction, returns a new function as the result.
         * @param p1
         * @param p2
         * @return
         */
        fun <T> multiply(p1: SVPFunction<T>, p2: SVPFunction<T>): AbstractSVPFunction<T> {
            val max = p1.degree + p2.degree
            return if (max < MAX_ARRAY_THREHOLD) {
                multiplyToArr(p1, p2, max)
            } else {
                multiplyToMap(p1, p2, max)
            }
        }


        /**
         * @param p1
         * @param p2
         * @param max
         * @return
         */
        private fun <T> multiplyToMap(p1: SVPFunction<T>, p2: SVPFunction<T>, max: Int): AbstractSVPFunction<T> {
            val mc = p1.calculator
            val map = HashMap<Int, T>()
            var i = 0
            val max1 = p1.degree
            while (i <= max1) {
                var j = 0
                val max2 = p2.degree
                while (j <= max2) {
                    val t = i + j
                    val coe = mc.multiply(p1.get(i), p2.get(j))
                    map.compute(t) { _, c -> if (c == null) coe else mc.add(c, coe) }
                    j++
                }
                i++
            }
            return SVPFunctionImpl2(mc, max, map)
        }

        private fun <T> multiplyToArr(p1: SVPFunction<T>, p2: SVPFunction<T>, max: Int): AbstractSVPFunction<T> {
            val mc = p1.calculator
            val arr = arrayOfNulls<Any>(max + 1) as Array<T?>
            run {
                var i = 0
                val max1 = p1.degree
                while (i <= max1) {
                    var j = 0
                    val max2 = p2.degree
                    while (j <= max2) {
                        val t = i + j
                        val coe = mc.multiply(p1.get(i), p2.get(j))
                        if (arr[t] == null) {
                            arr[t] = coe
                        } else {
                            arr[t] = mc.add(arr[t], coe)
                        }
                        j++
                    }
                    i++
                }
            }
            for (i in arr.indices) {
                if (arr[i] == null) {
                    arr[i] = mc.zero
                }
            }
            return SVPFunctionImpl1(mc, max, arr as Array<T>)
        }
    }


}
