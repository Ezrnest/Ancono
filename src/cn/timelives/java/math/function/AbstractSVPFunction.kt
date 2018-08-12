/**
 *
 */
package cn.timelives.java.math.function

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.MathCalculatorHolder
import cn.timelives.java.math.MathObject
import cn.timelives.java.math.algebra.Polynomial
import cn.timelives.java.math.algebra.calculus.Derivable
import cn.timelives.java.math.algebra.calculus.Integrable
import cn.timelives.java.math.algebra.calculus.derivation
import cn.timelives.java.math.algebra.calculus.integration
import cn.timelives.java.math.numberModels.Utils
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter
import cn.timelives.java.utilities.ArraySup
import java.util.*
import java.util.function.Function

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
protected constructor(mc: MathCalculator<T>, internal val mp: Int) : AbstractSVFunction<T>(mc), SVPFunction<T>, Derivable<T, AbstractSVPFunction<T>>, Integrable<T> {

    override fun getDegree(): Int {
        return mp
    }

    /**
     * The default implement for computing the value f(x), it is calculated
     * through f(x) = a0+x(a1+x(a2+.....)
     */
    override fun apply(x: T): T {
        var re = getCoefficient(mp)
        for (i in mp - 1 downTo -1 + 1) {
            re = mc.multiply(x, re)
            re = mc.add(getCoefficient(i), re)
        }
        return re
    }

    /**
     * Returns the derivation of this function.
     * @return
     */
    override fun derive(): AbstractSVPFunction<T> {
        return derivation(this)
    }

    /*
	 * @see cn.timelives.java.math.algebra.calculus.Integrable#integrate()
	 */
    override fun integrate(): AbstractSVPFunction<T> {
        return integration(this)
    }

    /* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
	 */
    abstract override fun <N> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): AbstractSVPFunction<N>

    /* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#equals(java.lang.Object)
	 */

    override fun equals(obj: Any?): Boolean {
        if (obj is AbstractSVPFunction<*>) {
            val `as` = obj as AbstractSVPFunction<T>?
            if (degree != `as`!!.degree) {
                return false
            }
            for (i in 0 until mp) {
                if (getCoefficient(i) != `as`.getCoefficient(i)) {
                    return false
                }
            }
            return true

        }
        return false
    }

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
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject, java.util.function.Function)
	 */
    override fun <N> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean {
        if (obj !is SVPFunction<*>) {
            return false
        }
        if (obj === this) {
            return true
        }
        val f = obj as SVPFunction<N>
        return Polynomial.isEqual(this, f, Utils.mappedIsEqual(mc, mapper))
    }

    /* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.number_models.NumberFormatter)
	 */
    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
        return Polynomial.stringOf(this, mc, nf)
    }


    internal class SVPFunctionImpl1<T>
    /**
     * @param mc
     * @param maxp
     */
    (mc: MathCalculator<T>, maxp: Int, val coes: Array<T>) : AbstractSVPFunction<T>(mc, maxp) {
        /* (non-Javadoc)
		 * @see cn.timelives.java.math.function.SVFunction#apply(java.lang.Object)
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
		 * @see cn.timelives.java.math.function.AbstractSVPFunction#getCoefficient(int)
		 */
        override fun getCoefficient(n: Int): T {
            return coes[n]
        }

        /* (non-Javadoc)
		 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
		 */
        override fun <N> mapTo(mapper: Function<T, N>,
                               newCalculator: MathCalculator<N>): SVPFunctionImpl1<N> {
            return SVPFunctionImpl1(newCalculator, mp, ArraySup.mapTo(coes, mapper))
        }

        override fun equals(obj: Any?): Boolean {
            if (this === obj) return true
            if (obj !is SVPFunctionImpl1<*>) return false
            if (!super.equals(obj)) return false

            if (!Arrays.equals(coes, obj.coes)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + Arrays.hashCode(coes)
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
    (mc: MathCalculator<T>, mp: Int, val map: Map<Int, T>) : AbstractSVPFunction<T>(mc, mp) {

        /* (non-Javadoc)
		 * @see cn.timelives.java.math.function.AbstractSVPFunction#getCoefficient(int)
		 */
        override fun getCoefficient(n: Int): T {
            return map.getOrDefault(n, mc.zero)
        }

        /* (non-Javadoc)
		 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
		 */
        override fun <N> mapTo(mapper: Function<T, N>,
                               newCalculator: MathCalculator<N>): SVPFunctionImpl2<N> {
            val nmap = HashMap<Int, N>(map.size)
            for ((key, value) in map) {
                nmap[key] = mapper.apply(value)
            }
            return SVPFunctionImpl2(newCalculator, mp, nmap)
        }

        /* (non-Javadoc)
		 * @see cn.timelives.java.math.FlexibleMathObject#hashCode()
		 */
        override fun hashCode(): Int {
            return mc.hashCode() * 31 + map.hashCode()
        }

        override fun equals(obj: Any?): Boolean {
            if (this === obj) return true
            if (obj !is SVPFunctionImpl2<*>) return false
            if (!super.equals(obj)) return false

            if (map != obj.map) return false

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
    internal constructor(mc: MathCalculator<T>, a: T, b: T) : AbstractSVPFunction<T>(mc, 1) {
        private val a: T = Objects.requireNonNull(a)
        private val b: T = Objects.requireNonNull(b)

        init {
            if (mc.isZero(a)) {
                throw IllegalArgumentException("a==0")
            }
        }

        /*
		 * @see cn.timelives.java.math.function.AbstractSVPFunction#getCoefficient(int)
		 */
        override fun getCoefficient(n: Int): T {
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
		 * @see cn.timelives.java.math.function.AbstractSVPFunction#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
        override fun <N> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): LinearFunction<N> {
            return LinearFunction(newCalculator, mapper.apply(a), mapper.apply(b))
        }

    }

    /**
     * A constant function is a type of MathFunction that
     * always returns the same result.
     * @author
     */
    class ConstantFunction<T> internal constructor(mc: MathCalculator<T>,
                                                   /**
                                                    * Returns the result.
                                                    * @return
                                                    */
                                                   val result: T) : AbstractSVPFunction<T>(mc, 0), SVFunction<T> {


        /*
		 * @see cn.timelives.java.math.function.MathFunction#apply(java.lang.Object)
		 */
        override fun apply(x: T): T {
            return result
        }


        /*
		 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
        override fun <N> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): ConstantFunction<N> {
            return ConstantFunction(newCalculator, mapper.apply(result))
        }


        /*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
        override fun valueEquals(obj: MathObject<T>): Boolean {
            return if (obj !is ConstantFunction<*>) {
                false
            } else mc.isEqual(result, (obj as ConstantFunction<T>).result)
        }

        /*
		 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.numberModels.api.NumberFormatter)
		 */
        override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
            return nf.format(result, mc)
        }


        /*
		 * @see cn.timelives.java.math.function.SVPFunction#getCoefficient(int)
		 */
        override fun getCoefficient(n: Int): T {
            if (n != 0) {
                throw IndexOutOfBoundsException("n!=0")
            }
            return result
        }
    }

    companion object {


        /**
         * Creates a function with it coefficients.
         * @param mc a [MathCalculator]
         * @param coes an array of coefficients, if an element is `null`,
         * then it will be considered as zero.
         * @return a new single variable polynomial function
         */
        @SafeVarargs
        fun <T> valueOf(mc: MathCalculator<T>, vararg coes: T): AbstractSVPFunction<T> {
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
            ncoes = Arrays.copyOf(ncoes, max + 1)
            return SVPFunctionImpl1(mc, max, ncoes)
        }

        /**
         * Creates a function with it coefficients as a list.
         * @param coes an list of coefficients, `null` values are unacceptable.
         * @param mc a [MathCalculator]
         * @return a new single variable polynomial function
         */
        fun <T> valueOf(coes: List<T>, mc: MathCalculator<T>): AbstractSVPFunction<T> {
            val arr = coes.toTypedArray<Any?>() as Array<T>
            for (i in arr.indices) {
                if (arr[i] == null) {
                    throw NullPointerException("null in list: index = $i")
                }
            }
            return SVPFunctionImpl1(mc, arr.size - 1, arr)
        }

        /**
         * Creates a function with it coefficients as a map.
         * @param coes an map of coefficients, `null` values are unacceptable.
         * @param mc a [MathCalculator]
         * @return a new single variable polynomial function
         */
        fun <T> valueOf(coes: Map<Int, T>, mc: MathCalculator<T>): AbstractSVPFunction<T> {
            val map = HashMap<Int, T>()
            var mp = 0
            for ((key, value) in coes) {

                if (value == null) {
                    throw NullPointerException()
                }
                mp = Math.max(mp, key)
                map[key] = value
            }
            return SVPFunctionImpl2(mc, mp, map)
        }

        /**
         * Returns a constant function:
         * <pre>c</pre>
         * @param c the constant
         * @param mc a [MathCalculator]
         * @return a new ConstantFunction
         */
        fun <T> constant(c: T, mc: MathCalculator<T>): ConstantFunction<T> {
            return ConstantFunction(mc, c)
        }

        /**
         * Returns a linear function:
         * <pre>ax+b</pre>
         * It is required that `a!=0`.
         * @param a the coefficient of `x`
         * @param b the constant
         * @param mc a [MathCalculator]
         * @return a new LinearFunction
         */
        fun <T> linear(a: T, b: T, mc: MathCalculator<T>): LinearFunction<T> {
            return LinearFunction(mc, a, b)
        }

        /**
         * Returns a new quadratic function.
         * <pre>ax^2+bx+c</pre>
         * It is required that `a!=0`.
         * @param a the coefficient of `x^2`
         * @param b the coefficient of `x`
         * @param c the constant
         * @param mc a [MathCalculator]
         * @return a new QuadraticFunction
         */
        fun <T> quadratic(a: T, b: T, c: T, mc: MathCalculator<T>): QuadraticFunction<T> {
            return QuadraticFunction(mc, a, b, c)
        }

        /**
         * Returns a function from a Polynomial.
         * @param m a [Polynomial]
         * @param mc a [MathCalculator]
         * @return an [AbstractSVPFunction]
         */
        fun <T> fromPolynomial(m: Polynomial<T>, mc: MathCalculator<T>): AbstractSVPFunction<T> {
            if (m is AbstractSVPFunction<*>) {
                return m as AbstractSVPFunction<T>
            }
            val size = m.degree + 1
            val list = arrayOfNulls<Any>(size) as Array<T>
            for (i in 0 until size) {
                list[i] = m.getCoefficient(i)
            }
            return SVPFunctionImpl1(mc, size - 1, list)
        }

        /**
         * Returns a function from a Polynomial which is also a [MathCalculatorHolder].
         * @param m a [Polynomial]
         * @param mc a [MathCalculator]
         * @return an [AbstractSVPFunction]
         * @throws ClassCastException if `!(m instanceof MathCalculatorHolder)`;
         */
        fun <T> fromPolynomial(m: Polynomial<T>): AbstractSVPFunction<T> {
            val holder = m as MathCalculatorHolder<T>
            return fromPolynomial(m, holder.mathCalculator)
        }

        /**
         * Adds two functions.
         * @param p1
         * @param p2
         * @return
         */
        fun <T> add(p1: SVPFunction<T>, p2: SVPFunction<T>): AbstractSVPFunction<T> {
            val max = Math.max(p1.degree, p2.degree)
            val coes = arrayOfNulls<Any>(max + 1) as Array<T>
            val mc = p1.mathCalculator
            for (i in 0..max) {
                coes[i] = mc.add(p1.getCoefficient(i), p2.getCoefficient(i))
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
            val max = Math.max(p1.degree, p2.degree)
            val coes = arrayOfNulls<Any>(max + 1) as Array<T>
            val mc = p1.mathCalculator
            for (i in 0..max) {
                coes[i] = mc.subtract(p1.getCoefficient(i), p2.getCoefficient(i))
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
            val mc = p1.mathCalculator
            val map = HashMap<Int, T>()
            var i = 0
            val max1 = p1.degree
            while (i <= max1) {
                var j = 0
                val max2 = p2.degree
                while (j <= max2) {
                    val t = i + j
                    val coe = mc.multiply(p1.getCoefficient(i), p2.getCoefficient(j))
                    map.compute(t) { _, c -> if (c == null) coe else mc.add(c, coe) }
                    j++
                }
                i++
            }
            return SVPFunctionImpl2(mc, max, map)
        }

        private fun <T> multiplyToArr(p1: SVPFunction<T>, p2: SVPFunction<T>, max: Int): AbstractSVPFunction<T> {
            val mc = p1.mathCalculator
            val arr = arrayOfNulls<Any>(max + 1) as Array<T>
            run {
                var i = 0
                val max1 = p1.degree
                while (i <= max1) {
                    var j = 0
                    val max2 = p2.degree
                    while (j <= max2) {
                        val t = i + j
                        val coe = mc.multiply(p1.getCoefficient(i), p2.getCoefficient(j))
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
            return SVPFunctionImpl1(mc, max, arr)
        }
    }


}
