package cn.timelives.java.math.numberModels.structure

import cn.timelives.java.math.MathCalculator
import cn.timelives.java.math.MathObject
import cn.timelives.java.math.exceptions.UnsupportedCalculationException
import cn.timelives.java.math.numberModels.ComplexI
import cn.timelives.java.math.numberModels.api.DivisionRingNumberModel
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter
import cn.timelives.java.math.numberModels.MathCalculatorAdapter
import cn.timelives.java.math.geometry.analytic.planeAG.PVector
import cn.timelives.java.math.geometry.analytic.planeAG.Point

import java.util.ArrayList
import java.util.Objects
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

/**
 * Complex number ,a type of number that can be written as A+Bi,where A,B are
 * both real number,and "i" is the square root of `-1`.
 *
 *
 * In this type of number,all calculation will consider that both A and B are real number,
 * and followings are the basic rules.
 *  * Add:<pre> (A+Bi)+(C+Di) = (A+B)+(C+Di)</pre>
 *  * Negate:<pre> -(A+Bi) = -A + (-B)i</pre>
 *  * Subtract:<pre>Z1 - Z2 = Z1 + (-Z2)</pre>
 *  * Multiple:<pre>(A+Bi)*(C+Di)=(AC-BD)+(AD+BC)i</pre>
 *  * DIvide:<pre>(A+Bi)/(C+Di)=1/(C^2+D^2)*((AC+BD)+(BC-AD)i)</pre>
 * Operations such as modulus and conjugate are also provided.
 *
 *
 * The complex itself requires a type of number to implement A and B,such as
 * `Complex<Double>` and `Complex<Long>`,and complex is a kind of number too.
 * This may cause some waste, to use complex number only as a kind of number type, you may use
 * [ComplexI]
 * @author lyc
 *
 * @param <T>
</T> */
class Complex<T : Any> internal constructor(mc: MathCalculator<T>, a: T, b: T) : MathObject<T>(mc), DivisionRingNumberModel<Complex<T>> {
    private val a: T = Objects.requireNonNull(a)
    private val b: T = Objects.requireNonNull(b)
    private var m: T? = null
    private var arg: T? = null

    /**
     * Returns the real part of this,which is
     * equal to `Re(this)`.
     * @return `Re(this)`
     */
    fun re(): T {
        return a
    }

    /**
     * Returns the imaginary part of this,which is
     * equal to `Im(this)`.
     * @return `Im(this)`
     */
    fun im(): T {
        return b
    }

    /**
     * Returns the modulus of this complex number,which is
     * equal to `|this|`.
     * @return `|this|`
     */
    fun modulus(): T {
        if (m == null) {
            m = mc.squareRoot(mc.add(mc.multiply(a, a), mc.multiply(b, b)))
        }
        return m!!
    }

    /**
     * Returns the vector of this(`<a,b>`).
     * @return a vector
     */
    fun toVector(): PVector<T> {
        return PVector.valueOf(a, b, mc)
    }

    /**
     * Returns the point representation in complex plane of this number.
     * @return point(a,b)
     */
    fun toPoint(): Point<T> {
        return Point.valueOf(a, b, mc)
    }

    /**
     * Returns the trigonometrical form of this complex number.
     * The first element is the argument and the second element is the modulus
     * of this complex number.
     * vector.
     */
    fun triForm(): List<T> {
        val list = ArrayList<T>(2)
        list.add(arg())
        list.add(modulus())
        return list
    }

    /**
     * Returns the argument of this complex number,which is
     * equal to arg(this).The range of the angle will be in [0,2Pi).
     *
     * Notice : arg(0) = 0.
     * @return arg(this)
     */
    fun arg(): T {
        if (arg == null) {
            val pi = mc.constantValue(MathCalculator.STR_PI)!!
            val compa = mc.compare(a, mc.zero)
            val compb = mc.compare(b, mc.zero)
            if (compa == 0) {
                if (compb == 0) {
                    return mc.zero
                }
                return if (compb > 0) {
                    mc.divideLong(pi, 2L)
                } else {
                    mc.add(mc.divideLong(pi, 2L), pi)
                }
            }
            if (compb == 0) {
                return if (compa > 0) {
                    mc.zero
                } else {
                    pi
                }
            }
            var theta = mc.arctan(mc.abs(mc.divide(b, a)))
            if (compa > 0) {
                if (compb < 0) {
                    theta = mc.subtract(mc.multiplyLong(pi, 2L), theta)
                }
            } else {
                theta = if (compb > 0) {
                    mc.subtract(pi, theta)
                } else {
                    mc.add(theta, pi)
                }
            }
            arg = theta

        }
        return arg!!
    }

    /**
     * Returns `this+y`.
     * @param y
     * @return `this+y`
     */
    override fun add(y: Complex<T>): Complex<T> {
        return Complex(mc, mc.add(a, y.a), mc.add(b, y.b))
    }

    /**
     * Returns `this-y`.
     * @param y
     * @return `this-y`
     */
    override fun subtract(y: Complex<T>): Complex<T> {
        return Complex(mc, mc.subtract(a, y.a), mc.subtract(b, y.b))
    }

    /**
     * Returns `-this`.
     * @return `-this`
     */
    override fun negate(): Complex<T> {
        return Complex(mc, mc.negate(a), mc.negate(b))
    }

    /**
     * Returns the conjugate complex number of `this`.
     * @return conjugate of this
     */
    fun conjugate(): Complex<T> {
        return Complex(mc, a, mc.negate(b))
    }

    /**
     * Returns this*y.
     * @param y
     * @return the result of `this*y`.
     */
    override fun multiply(y: Complex<T>): Complex<T> {
        //(a+bi)*(c+di) =
        // ac-bd + (ad+bc)i
        //but we can use a trick to reduce calculation:
        //1.(a+b)(c+d) = ac + bd + ad + bc
        //2.ac , 3. bd
        //and 1 - 2 - 3 = ad + bc
        //    2 - 3 = ac - bd
        val t1 = mc.multiply(mc.add(a, b), mc.add(y.a, y.b))
        val t2 = mc.multiply(a, y.a)
        val t3 = mc.multiply(b, y.b)
        val an = mc.subtract(t2, t3)
        val bn = mc.subtract(t1, mc.add(t2, t3))
        return Complex(mc, an, bn)
    }

    /**
     * Returns this*r.
     * @param r
     * @return this*r
     */
    fun multiplyReal(r: T): Complex<T> {
        return Complex(mc, mc.multiply(a, r), mc.multiply(b, r))
    }

    /**
     * Returns this/z,throw ArithmeticException if z = 0.
     * @param y
     * @return the result of `this*z`.
     */
    override fun divide(y: Complex<T>): Complex<T> {
        //                _
        //z1 / z2 = (z1 * z2) / |z2|^2
        val sq = mc.add(mc.multiply(y.a, y.a), mc.multiply(y.b, y.b))
        //copy code here
        val tb = mc.negate(y.b)
        val t1 = mc.multiply(mc.add(a, b), mc.add(y.a, tb))
        val t2 = mc.multiply(a, y.a)
        val t3 = mc.multiply(b, tb)
        var an = mc.subtract(t2, t3)
        var bn = mc.subtract(t1, mc.add(t2, t3))
        an = mc.divide(an, sq)
        bn = mc.divide(bn, sq)
        return Complex(mc, an, bn)
    }

    override fun reciprocal(): Complex<T> {
        //         _
        // 1 / z = z / |z|^2
        val sq = mc.add(mc.multiply(a, a), mc.multiply(b, b))
        val an = mc.divide(a, sq)
        val bn = mc.divide(mc.negate(b), sq)
        return Complex(mc, an, bn)
    }

    /**
     * Calculates the result of `this^p`,the `p` should be a non-negative
     * number.
     *
     *
     * @param p the power
     * @return `this^p`
     * @throws ArithmeticException if `p==0 && this==0`
     */
    fun pow(p: Long): Complex<T> {
        var p1 = p
        if (p1 < 0) {
            throw IllegalArgumentException("Cannot calculate:p<0")
        }

        //we use this way to reduce the calculation to log(p)
        var re = Complex.real(mc.one, mc)
        if (p1 == 0L) {
            if (mc.isZero(a) && mc.isZero(b)) {
                throw ArithmeticException("0^0")
            }
            return re
        }
        var th = this
        while (p1 != 0L) {
            //which means need to multiple this one
            if (p1 and 1L != 0L) {
                re = re.multiply(th)
            }
            th = th.multiply(th)
            p1 = p1 shr 1
        }
        return re
    }

    //	public Complex<T> powf()

    override fun <N : Any> mapTo(mapper: Function<T, N>, newCalculator: MathCalculator<N>): Complex<N> {
        return Complex(newCalculator, mapper.apply(a), mapper.apply(b))
    }

    override fun equals(other: Any?): Boolean {
        if (other is Complex<*>) {
            val com = other as Complex<*>?
            return a == com!!.a && b == com.b
        }
        return false
    }

    override fun hashCode(): Int {
        return a.hashCode() + 37 * b.hashCode()
    }

    /**
     * Return the form of (a)+(b)i
     * @return
     */
    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
        if (mc.isZero(a)) {
            return if (mc.isZero(b)) {
                "0"
            } else "(" + nf.format(b, mc) + ")i"
        } else {
            if (mc.isZero(b)) {
                return "(" + nf.format(a, mc) + ")i"
            }
            val sb = StringBuilder()
            sb.append('(').append(nf.format(a, mc))
                    .append(")")
                    .append(nf.format(b, mc))
                    .append(")i")
            return sb.toString()
        }
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj is Complex<*>) {
            val com = obj as Complex<T>
            return mc.isEqual(a, com.a) && mc.isEqual(b, com.b)
        }
        return false
    }

    override fun <N : Any> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean {
        if (obj is Complex<*>) {
            val com = obj as Complex<N>
            return mc.isEqual(a, mapper.apply(com.a)) && mc.isEqual(b, mapper.apply(com.b))
        }
        return false
    }


    class ComplexCalculator<T : Any>(private val mc: MathCalculator<T>) : MathCalculatorAdapter<Complex<T>>() {
        override val zero: Complex<T> = Complex.zero(mc)
        override val one: Complex<T> = Complex.real(mc.one, mc)
        /* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.MathCalculator#getNumberClass()
		 */
        override val numberClass: Class<Complex<*>>
            get() = Complex::class.java

        override fun isEqual(x: Complex<T>, y: Complex<T>): Boolean {
            return x.valueEquals(y)
        }

        override fun compare(x: Complex<T>, y: Complex<T>): Int {
            throw UnsupportedCalculationException("Complex Number")
        }

        override fun add(x: Complex<T>, y: Complex<T>): Complex<T> {
            return x.add(y)
        }

        override fun negate(x: Complex<T>): Complex<T> {
            return x.negate()
        }

        /**
         * This method overrides the normal `abs()` method and it
         * is equal to modulus of the complex.(|z|)
         */
        override fun abs(para: Complex<T>): Complex<T> {
            return Complex.real(para.modulus(), para.mc)
        }

        override fun subtract(x: Complex<T>, y: Complex<T>): Complex<T> {
            return x.subtract(y)
        }

        override fun multiply(x: Complex<T>, y: Complex<T>): Complex<T> {
            return x.multiply(y)
        }

        override fun divide(x: Complex<T>, y: Complex<T>): Complex<T> {
            return x.divide(y)
        }


        override fun reciprocal(x: Complex<T>): Complex<T> {
            return x.reciprocal()
        }

        override fun multiplyLong(x: Complex<T>, n: Long): Complex<T> {
            return Complex(mc, mc.multiplyLong(x.a, n), mc.multiplyLong(x.b, n))
        }

        override fun divideLong(x: Complex<T>, n: Long): Complex<T> {
            return Complex(mc, mc.divideLong(x.a, n), mc.divideLong(x.b, n))
        }

        override fun squareRoot(x: Complex<T>): Complex<T> {
            if (mc.isZero(x.im())) {
                val re = x.re()
                return if (nonNegative(re)) {
                    Complex(mc, mc.squareRoot(re), mc.zero)
                } else {
                    Complex(mc, mc.zero, mc.negate(re))
                }
            }
            val m = x.modulus()
            val r = mc.squareRoot(m)
            var cos = mc.divide(x.a, m)
            var sin = mc.divide(x.b, r)
            cos = mc.squareRoot(mc.divideLong(mc.add(cos, mc.one), 2L))
            sin = mc.squareRoot(mc.divideLong(mc.subtract(mc.one, sin), 2L))
            if (!nonNegative(x.b)) {
                cos = mc.negate(cos)
            }
            cos = mc.multiply(r, cos)
            sin = mc.multiply(r, sin)
            return Complex(mc, cos, sin)

        }

        /**
         * @see MathCalculator.nroot
         */
        override fun nroot(x: Complex<T>, n: Long): Complex<T> {
            var arg = x.arg()
            arg = mc.divideLong(arg, n)
            return Complex.modArg(mc.nroot(x.modulus(), n), arg, mc)
        }

        private fun nonNegative(t: T): Boolean {
            return mc.compare(t, mc.zero) > 0
        }

        override fun pow(x: Complex<T>, n: Long): Complex<T> {
            return x.pow(n)
        }

        override fun constantValue(name: String): Complex<T>? {
            when (name) {
                STR_I -> {
                    return Complex(mc, mc.zero, mc.one)
                }
            }
            val x = mc.constantValue(name) ?: return null
            return Complex(mc, x, mc.zero)
        }

        /**
         * Returns exp(x). Assuming that x = a+bi, the function returns a
         * complex number whose modulus is equal to e^a and
         * argument is equal to b.
         */
        override fun exp(x: Complex<T>): Complex<T> {
            return Complex.modArg(mc.exp(x.a), x.b, mc)
        }

        /**
         * Returns exp(a,b), which is equal to exp(ln(a)*b).
         */
        override fun exp(a: Complex<T>, b: Complex<T>): Complex<T> {
            return exp(multiply(ln(a), b))
        }

        /**
         * Returns the primary value of ln(x), which is equal to
         * <pre>
         * ln(|z|) + arg(z)i
        </pre> *
         */
        override fun ln(x: Complex<T>): Complex<T> {
            val mod = x.modulus()
            if (mc.isZero(mod)) {
                throw ArithmeticException("ln(0)")
            }
            val arg = x.arg()
            return Complex(mc, mod, arg)
        }

        companion object {

            /**
             * The String representation of *i*, the square root of -1.
             */
            val STR_I = "i"
        }
    }

    companion object {
        //some argument function implements here:
        /**
         * A default implement of argument function for Double.
         */
        val DAF: (Complex<Double>) -> Double = { t: Complex<Double> ->

            var arg = Math.atan2(t.b, t.a)
            //must be in [0,2pi),so add 2Pi if necessary.
            if (arg < 0) {
                arg += Math.PI * 2
            }
            arg
        }

        /**
         * Gets the value of 0.
         * @param mc
         * @return
         */
        @Suppress("UNCHECKED_CAST")
        fun <T : Any> zero(mc: MathCalculator<T>): Complex<T> {
            var c: Complex<T>? = zeros[mc] as Complex<T>
            if (c == null) {
                val z = mc.zero
                c = Complex(mc, z, z)
                zeros[mc] = c
            }
            return c
        }

        private val zeros = ConcurrentHashMap<MathCalculator<*>, Complex<*>>()

        /**
         * Creates a new complex instance of
         * <pre>a + bi</pre>
         * @param a real part of the complex
         * @param b imaginary part of the complex
         * @param mc a [MathCalculator]
         * @return a new complex.
         */
        fun <T : Any> ins(a: T, b: T, mc: MathCalculator<T>): Complex<T> {
            return Complex(mc, a, b)
        }

        /**
         * Create a imaginary number
         * <pre>a</pre>
         * The imaginary part of this number will be 0.
         * @param a the real part
         * @param mc a [MathCalculator]
         * @return a new complex.
         */
        fun <T : Any> real(a: T, mc: MathCalculator<T>): Complex<T> {
            return Complex(mc, a, mc.zero)
        }

        /**
         * Create a real number
         * <pre>bi</pre>
         * The real part of this number will be 0.
         * @param b the imaginary part
         * @param mc a [MathCalculator]
         * @return a new complex.
         */
        fun <T : Any> imaginary(b: T, mc: MathCalculator<T>): Complex<T> {
            return Complex(mc, mc.zero, b)
        }

        /**
         * Returns the complex that is equal to:
         * <pre>r*(cos(theta)+isin(theta))</pre>
         * @param r
         * @param theta
         * @return
         */
        fun <T : Any> modArg(r: T, theta: T, mc: MathCalculator<T>): Complex<T> {
            return Complex(mc, mc.multiply(r, mc.cos(theta)), mc.multiply(r, mc.sin(theta)))
        }

        /**
         * Gets a wrapped calculator for the number type complex.
         * @param mc
         * @return
         */
        fun <T : Any> getCalculator(mc: MathCalculator<T>): MathCalculator<Complex<T>> {
            return ComplexCalculator(mc)
        }
    }

    //	public static void main(String[] args) {
    //		//test here
    //		MathCalculator<Formula> mc = Formula.getCalculator();
    //		List<Complex<Formula>> list = ProgressionSup
    //				.createArithmeticProgression(Formula.valueOf("0.5"),Formula.ONE, mc)
    //				.limit(10)
    //				.mapTo(f -> Complex.real(f, mc), Complex.getCalculator(mc))
    //				.stream()
    //				.collect(Collectors.toList());
    //		Complex<Formula> mul = Complex.ins(Formula.ONE, Formula.ONE.negate(), mc);
    //		list.forEach(f -> Printer.print(f.multiply(mul)));
    //	}


}
