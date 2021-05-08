package cn.ancono.math.numberModels.structure

import cn.ancono.math.AbstractMathObject
import cn.ancono.math.MathObject
import cn.ancono.math.algebra.abs.calculator.EqualPredicate
import cn.ancono.math.algebra.abs.calculator.FieldCalculator

import cn.ancono.math.function.Bijection
import cn.ancono.math.geometry.analytic.plane.PVector
import cn.ancono.math.geometry.analytic.plane.Point
import cn.ancono.math.geometry.analytic.space.SPoint
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.MathCalculatorAdapter
import cn.ancono.math.numberModels.api.*
import org.jetbrains.annotations.NotNull
import java.util.*
import java.util.function.Function
import kotlin.math.atan2

/*
Created by liyicheng 2020/2/24
*/

/**
 * Describes the expanded complex including the infinity point.
 */
sealed class ComplexE<T> constructor(mc: FieldCalculator<T>) : AbstractMathObject<T, FieldCalculator<T>>(mc) {

    abstract fun isInf(): Boolean

    abstract override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): ComplexE<N>
}

class ComplexInf<T> internal constructor(mc: FieldCalculator<T>) : ComplexE<T>(mc) {

    override fun isInf(): Boolean {
        return true
    }

    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): ComplexInf<N> {
        return ComplexInf(newCalculator as FieldCalculator)
    }

    override fun valueEquals(obj: MathObject<T, FieldCalculator<T>>): Boolean {
        return obj is ComplexInf
    }

    override fun toString(nf: NumberFormatter<T>): String {
        return "Inf"
    }

    override fun equals(other: Any?): Boolean {
        return other is ComplexInf<*>
    }

    override fun hashCode(): Int {
        return 0
    }
}


/**
 * Complex number, a type of number that can be written as `A+Bi`, where A,B are
 * both real number, and "i" is the square root of `-1`.
 *
 *
 * In this type of number, all calculation will consider that both A and B are real number,
 * and followings are the basic rules.
 *  * Add:<pre> (A+Bi)+(C+Di) = (A+B)+(C+Di)</pre>
 *  * Negate:<pre> -(A+Bi) = -A + (-B)i</pre>
 *  * Subtract:<pre>Z1 - Z2 = Z1 + (-Z2)</pre>
 *  * Multiple:<pre>(A+Bi)*(C+Di)=(AC-BD)+(AD+BC)i</pre>
 *  * Divide:<pre>(A+Bi)/(C+Di)=1/(C^2+D^2)*((AC+BD)+(BC-AD)i)</pre>
 * Operations such as modulus and conjugate are also provided.
 *
 *
 * The complex itself requires a type of number to implement A and B,such as
 * `Complex<Double>` and `Complex<Long>`,and complex is a kind of number too.
 * This may cause some waste, to use complex number only as a kind of number type, you may use [cn.ancono.math.numberModels.ComplexI]
 *
 * @author lyc
 *
 */
class Complex<T> internal constructor(mc: FieldCalculator<T>, a: T, b: T) : ComplexE<T>(mc),
        FieldNumberModel<Complex<T>> {


    private val a: T = Objects.requireNonNull(a)
    private val b: T = Objects.requireNonNull(b)
    private var m: T? = null
    private var arg: T? = null

    override fun isInf(): Boolean {
        return false
    }

    override fun isZero(): Boolean {
        val mc = calculator
        return mc.isZero(a) && mc.isZero(b)
    }

    fun isReal(): Boolean {
        return calculator.isZero(b)
    }


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
            val mc = calculator as RealCalculator
            m = mc.squareRoot(mc.add(mc.multiply(a, a), mc.multiply(b, b)))
        }
        return m!!
    }

    /**
     * Returns the square of the modulus.
     */
    fun modulusSq(): T {
        val mc = calculator
        return mc.add(mc.multiply(a, a), mc.multiply(b, b))
    }

    /**
     * Returns the vector of this(`<a,b>`).
     * @return a vector
     */
    fun toVector(): PVector<T> {
        return PVector.valueOf(a, b, calculator)
    }

    /**
     * Returns the point representation in complex plane of this number.
     * @return point(a,b)
     */
    fun toPoint(): Point<T> {
        return Point.valueOf(a, b, calculator as RealCalculator<T>)
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
            val mc = calculator as RealCalculator
            val pi = mc.constantValue(RealCalculator.STR_PI)!!
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
        val mc = calculator
        return Complex(mc, mc.add(a, y.a), mc.add(b, y.b))
    }

    /**
     * Returns `this-y`.
     * @param y
     * @return `this-y`
     */
    override fun subtract(y: Complex<T>): Complex<T> {
        val mc = calculator
        return Complex(mc, mc.subtract(a, y.a), mc.subtract(b, y.b))
    }

    /**
     * Returns `-this`.
     * @return `-this`
     */
    override fun negate(): Complex<T> {
        val mc = calculator
        return Complex(mc, mc.negate(a), mc.negate(b))
    }

    /**
     * Returns the conjugate complex number of `this`.
     * @return conjugate of this
     */
    fun conj(): Complex<T> {
        val mc = calculator
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
        val mc = calculator
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
        val mc = calculator
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
        val mc = calculator
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
        val mc = calculator
        val sq = mc.add(mc.multiply(a, a), mc.multiply(b, b))
        val an = mc.divide(a, sq)
        val bn = mc.divide(mc.negate(b), sq)
        return Complex(mc, an, bn)
    }

    /**
     * Calculates the result of `this^p`.
     *
     *
     * @param n the power
     * @return `this^p`
     * @throws ArithmeticException if `p==0 && this==0`
     */
    override fun pow(n: Long): Complex<T> {
        var p1 = n
        if (p1 < 0) {
            return reciprocal().pow(-n)
        }
        val mc = calculator
        //we use this way to reduce the calculation to log(p)
        var re = real(mc.one, mc)
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


    fun squareRoot(): Complex<T> {
        val mc = calculator as RealCalculator
        fun nonNegative(t: T): Boolean {
            return mc.compare(t, mc.zero) > 0
        }

        val x = this
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
     * Returns a complex `x` that `x^n = this`.
     */
    fun nroot(n: Long): Complex<T> {
        val mc = calculator as RealCalculator
        if (isReal()) {
            return real(mc.nroot(a, n), mc)
        }
        val x = this
        var arg = x.arg()
        arg = mc.divideLong(arg, n)
        return modArg(mc.nroot(x.modulus(), n), arg, mc)
    }

    //	public Complex<T> powf()

    override fun <N> mapTo(newCalculator: EqualPredicate<N>, mapper: Function<T, N>): Complex<N> {
        return Complex(newCalculator as FieldCalculator<N>, mapper.apply(a), mapper.apply(b))
    }

    override fun equals(other: Any?): Boolean {
        if (other is Complex<*>) {
            return a == other.a && b == other.b
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
    override fun toString(nf: NumberFormatter<T>): String {
        val mc = calculator
        if (mc.isZero(a)) {
            return when {
                mc.isZero(b) -> {
                    "0"
                }
                mc.isEqual(b, mc.one) -> {
                    return "i"
                }
                else -> {
                    // possible feature for better formatting
//                    val s = nf.format(b,mc)
//                    if (s.length == 1) {
//                        return s
//                    }else{
//                        return "($s)"
//                    }
                    return "(" + nf.format(b) + ")i"
                }
            }
        } else {
            if (mc.isZero(b)) {
                return nf.format(a)
            }

            val sb = StringBuilder()
            sb.append('(')
                    .append(nf.format(a))
                    .append(")")
                    .append('+')
            if (mc.isEqual(b, mc.one)) {
                sb.append('i')
            } else {
                sb.append('(')
                        .append(nf.format(b))
                        .append(")i")
            }
            return sb.toString()
        }
    }

    override fun valueEquals(obj: MathObject<T, FieldCalculator<T>>): Boolean {
        if (obj is Complex<*>) {
            val mc = calculator
            val com = obj as Complex<T>
            return mc.isEqual(a, com.a) && mc.isEqual(b, com.b)
        }
        return false
    }

//    override fun <N> valueEquals(obj: MathObject<N>, mapper: Function<N, T>): Boolean {
//        if (obj is Complex<*>) {
//            val com = obj as Complex<N>
//            return mc.isEqual(a, mapper.apply(com.a)) && mc.isEqual(b, mapper.apply(com.b))
//        }
//        return false
//    }


    class ComplexCalculator<T>(val mc: FieldCalculator<T>) : MathCalculatorAdapter<Complex<T>>() {
        override val zero: Complex<T> = zero(mc)
        override val one: Complex<T> = real(mc.one, mc)

        /**
         * The imaginary unit `i = Sqr(-1)`.
         */
        val i: Complex<T> = imaginary(mc.one, mc)

        override fun of(n: Long): Complex<T> {
            return real(mc.of(n))
        }

        override fun of(x: Fraction): Complex<T> {
            return real(mc.of(x))
        }

        /* (non-Javadoc)
                 * @see cn.ancono.cn.ancono.utilities.math.MathCalculator#getNumberClass()
                 */
        override val numberClass: Class<Complex<T>>
            @Suppress("UNCHECKED_CAST")
            get() = Complex::class.java as Class<Complex<T>>

        override fun isEqual(x: Complex<T>, y: Complex<T>): Boolean {
            return x.valueEquals(y)
        }

        override fun compare(x: Complex<T>, y: Complex<T>): Int {
            throw UnsupportedOperationException("Complex is not comparable.")
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
        override fun abs(x: Complex<T>): Complex<T> {
            return real(x.modulus())
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
            return x.squareRoot()
        }

        /**
         * @see RealCalculator.nroot
         */
        override fun nroot(x: Complex<T>, n: Long): Complex<T> {
            return x.nroot(n)
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
            val x = (mc as RealCalculator).constantValue(name) ?: return null
            return Complex(mc, x, mc.zero)
        }

        /**
         * Returns exp(x). Assuming that x = a+bi, the function returns a
         * complex number whose modulus is equal to e^a and
         * argument is equal to b.
         */
        override fun exp(x: Complex<T>): Complex<T> {
            return modArg((mc as RealCalculator).exp(x.a), x.b, mc)
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


        /**
         * Returns the real number `a` as a complex number.
         */
        fun real(a: T): Complex<T> {
            return Complex(mc, a, mc.zero)
        }

        /**
         * Returns the complex number `a+bi`.
         */
        fun valueOf(a: T, b: T): Complex<T> {
            return Complex(mc, a, b)
        }

        /**
         * Returns the imaginary number `bi` as a complex number.
         */
        fun imaginary(b: T): Complex<T> {
            return Complex(mc, mc.zero, b)
        }

        override fun cos(x: Complex<T>): Complex<T> {
            return (exp(i * x) + exp(-i * x)) / 2L
        }

        override fun sin(x: Complex<T>): Complex<T> {
            return (exp(-i * x) - exp(i * x)) * i / (2L)
        }

        companion object {

            /**
             * The String representation of *i*, the square root of -1.
             */
            val STR_I = "i"
        }
    }


    class ComplexECalculator<T>(val mc: ComplexCalculator<T>) : MathCalculatorAdapter<ComplexE<T>>() {
        override val one: ComplexE<T> = mc.one
        override val zero: ComplexE<T> = mc.zero
        val i: ComplexE<T> = mc.i
        val inf: ComplexE<T> = inf(mc.mc)

        override fun of(n: Long): ComplexE<T> {
            return mc.of(n)
        }

        override fun of(x: Fraction): ComplexE<T> {
            return mc.of(x)
        }

        override fun isZero(x: ComplexE<T>): Boolean {
            return x is Complex && mc.isZero(x)
        }

        override fun isEqual(x: ComplexE<T>, y: ComplexE<T>): Boolean {
            if (x is Complex && y is Complex) {
                return mc.isEqual(x, y)
            } else {
                return x.isInf() && y.isInf()
            }
        }

        override fun add(x: ComplexE<T>, y: ComplexE<T>): ComplexE<T> {
            if (x !is Complex || y !is Complex) {
                return inf
            }
            return mc.add(x, y)
        }

        override fun negate(x: ComplexE<T>): ComplexE<T> {
            return if (x !is Complex) {
                inf
            } else {
                mc.negate(x)
            }
        }

        override fun abs(x: ComplexE<T>): ComplexE<T> {
            return if (x !is Complex) {
                inf
            } else {
                mc.abs(x)
            }
        }

        override fun subtract(x: ComplexE<T>, y: ComplexE<T>): ComplexE<T> {
            if (x !is Complex || y !is Complex) {
                return inf
            }
            return mc.subtract(x, y)
        }

        override fun multiply(x: ComplexE<T>, y: ComplexE<T>): ComplexE<T> {
            if (x !is Complex) {
                if (isZero(y)) {
                    throw ArithmeticException("Inf * 0")
                }
                return inf
            }
            if (y !is Complex) {
                if (isZero(x)) {
                    throw ArithmeticException("0 * Inf")
                }
                return inf
            }

            return mc.multiply(x, y)
        }

        override fun divide(x: ComplexE<T>, y: ComplexE<T>): ComplexE<T> {
            if (x !is Complex) {
                if (y !is Complex) {
                    throw ArithmeticException("Inf / Inf")
                }
                return inf
            }
            if (y !is Complex) {
                return zero
            }
            return mc.divide(x, y)
        }

        override fun multiplyLong(x: ComplexE<T>, n: Long): ComplexE<T> {
            if (x !is Complex) {
                if (n == 0L) {
                    throw ArithmeticException("Inf * 0")
                }
                return inf
            }
            return mc.multiplyLong(x, n)
        }

        override fun divideLong(x: ComplexE<T>, n: Long): ComplexE<T> {
            if (x !is Complex) {
                return inf
            }
            return mc.divideLong(x, n)
        }

        override fun reciprocal(x: ComplexE<T>): ComplexE<T> {
            return if (x !is Complex) {
                zero
            } else {
                mc.reciprocal(x)
            }
        }

        override fun squareRoot(x: ComplexE<T>): ComplexE<T> {
            if (x !is Complex) {
                return inf
            }
            return mc.squareRoot(x)
        }

        override fun pow(x: ComplexE<T>, n: Long): ComplexE<T> {
            if (x !is Complex) {
                return when {
                    n > 0 -> inf
                    n == 0L -> throw ArithmeticException("Inf^0")
                    else -> zero
                }
            }
            return mc.pow(x, n)
        }

        override fun exp(a: ComplexE<T>, b: ComplexE<T>): ComplexE<T> {
            return exp(multiply(b, ln(a)))
        }

        override fun cos(x: ComplexE<T>): ComplexE<T> {
            if (x !is Complex) {
                throw ArithmeticException("cos(Inf)")
            }
            return mc.cos(x)
        }

        override fun sin(x: ComplexE<T>): ComplexE<T> {
            if (x !is Complex) {
                throw ArithmeticException("sin(Inf)")
            }
            return mc.sin(x)
        }


        override fun tan(x: ComplexE<T>): ComplexE<T> {
            if (x !is Complex) {
                throw ArithmeticException("tan(Inf)")
            }
            return mc.tan(x)
        }

        override fun nroot(x: ComplexE<T>, n: Long): ComplexE<T> {
            if (x !is Complex) {
                return inf
            }
            return mc.nroot(x, n)
        }

        override fun exp(x: ComplexE<T>): ComplexE<T> {
            if (x !is Complex) {
                return inf
            }
            return mc.exp(x)
        }

        override fun ln(x: ComplexE<T>): ComplexE<T> {
            if (x !is Complex) {
                return inf
            }
            return mc.ln(x)
        }


        fun crossRatio(
                x1: ComplexE<T>, x2: ComplexE<T>,
                x3: ComplexE<T>, x4: ComplexE<T>
        ): ComplexE<T> {
            return crossRatio(x1, x2, x3, x4, mc.mc)
        }

        override val numberClass: Class<ComplexE<T>>
            @Suppress("UNCHECKED_CAST")
            get() = ComplexE::class.java as Class<ComplexE<T>>
    }


    companion object {
        //some argument function implements here:
        /**
         * A default implement of argument function for Double.
         */
        val DAF: (Complex<Double>) -> Double = { t: Complex<Double> ->

            var arg = atan2(t.b, t.a)
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
        @JvmStatic
        fun <T> zero(mc: FieldCalculator<T>): Complex<T> {
            val z = mc.zero
            return Complex(mc, z, z)
        }

        /**
         * Creates a new complex instance of
         * <pre>a + bi</pre>
         * @param a real part of the complex
         * @param b imaginary part of the complex
         * @param mc a [RealCalculator]
         * @return a new complex.
         */
        @JvmStatic
        fun <T> of(a: T, b: T, mc: FieldCalculator<T>): Complex<T> {
            return Complex(mc, a, b)
        }

        /**
         * Gets the value of `1`.
         */
        @JvmStatic
        fun <T> one(mc: FieldCalculator<T>): Complex<T> {
            return Complex(mc, mc.one, mc.zero)
        }

        /**
         * Gets the value of `i`.
         */
        @JvmStatic
        fun <T> i(mc: FieldCalculator<T>): Complex<T> {
            return Complex(mc, mc.zero, mc.one)
        }

        /**
         * Create a imaginary number
         * <pre>a</pre>
         * The imaginary part of this number will be 0.
         * @param a the real part
         * @param mc a [RealCalculator]
         * @return a new complex.
         */
        @JvmStatic
        fun <T> real(a: T, mc: FieldCalculator<T>): Complex<T> {
            return Complex(mc, a, mc.zero)
        }

        @JvmStatic
        fun <T> inf(mc: FieldCalculator<T>): ComplexE<T> {
            return ComplexInf(mc)
        }


        /**
         * Create a real number
         * <pre>bi</pre>
         * The real part of this number will be 0.
         * @param b the imaginary part
         * @param mc a [RealCalculator]
         * @return a new complex.
         */
        @JvmStatic
        fun <T> imaginary(b: T, mc: FieldCalculator<T>): Complex<T> {
            return Complex(mc, mc.zero, b)
        }

        /**
         * Returns the complex that is equal to:
         * <pre>r*(cos(theta)+isin(theta))</pre>
         * @param r
         * @param theta
         * @return
         */
        @JvmStatic
        fun <T> modArg(r: T, theta: T, mc: RealCalculator<T>): Complex<T> {
            return Complex(mc, mc.multiply(r, mc.cos(theta)), mc.multiply(r, mc.sin(theta)))
        }

        /**
         * Gets a calculator for complex using the given MathCalculator.
         */
        @JvmStatic
        fun <T> calculator(mc: FieldCalculator<T>): ComplexCalculator<T> {
            return ComplexCalculator(mc)
        }

        /**
         * Gets a calculator for the extended complex.
         */
        @JvmStatic
        fun <T> calculatorE(mc: RealCalculator<T>): ComplexECalculator<T> {
            return ComplexECalculator(calculator(mc))
        }

        /**
         * Returns the cross ratio of the four complex.
         *
         *     crossRatio(x1,x2,x3,x4) = (x1-x3)(x2-x4)/(x1-x4)(x2-x3)
         *
         * Some properties:
         *
         * 1. crossRatio(x1,x2,x3,x4) is real iff x1,x2,x3,x4 is on a circle (including line)
         *
         */
        @JvmStatic
        fun <T> crossRatio(x1: Complex<T>, x2: Complex<T>, x3: Complex<T>, x4: Complex<T>): Complex<T> {
            //  (x1-x3)(x2-x4)/(x1-x4)(x2-x3)
            return (x1 - x3) * (x2 - x4) / ((x1 - x4) * (x2 - x3))
        }

        /**
         * Returns the bijection of stereographic projection from north pole (0,0,1).
         */
        fun <T> stereographicProjection(mc: RealCalculator<T>): Bijection<SPoint<T>, ComplexE<T>> {
            return object : Bijection<SPoint<T>, ComplexE<T>> {
                override fun apply(x: SPoint<T>): ComplexE<T> {
                    //x/(1-z),y/(1-z)
                    val base = mc.subtract(mc.one, x.z)
                    return if (mc.isZero(base)) {
                        inf(mc)
                    } else {
                        val z1 = mc.divide(x.x, base)
                        val z2 = mc.divide(x.y, base)
                        Complex(mc, z1, z2)
                    }
                }

                override fun deply(y: @NotNull ComplexE<T>): SPoint<T> {
                    return if (y !is Complex) {
                        SPoint(mc, mc.zero, mc.zero, mc.one)
                    } else {
                        val modSq = y.modulusSq()
                        val base = mc.add(modSq, mc.one)
                        val x1 = mc.divide(mc.multiplyLong(y.a, 2), base)
                        val x2 = mc.divide(mc.multiplyLong(y.b, 2), base)
                        val x3 = mc.divide(mc.subtract(modSq, mc.one), base)
                        SPoint(mc, x1, x2, x3)
                    }
                }
            }
        }


        private fun <T> minusFrac(
                x: ComplexE<T>, y1: ComplexE<T>, y2: ComplexE<T>,
                mc: FieldCalculator<T>
        ): ComplexE<T> {
            // (x-y1)/(x-y2)
            if (x !is Complex) {
                return one(mc)
            }
            if (y2 !is Complex) {
                return if (y1 !is Complex) {
                    one(mc)
                } else {
                    zero(mc)
                }
            }
            if (y1 !is Complex) {
                return inf(mc)
            }
            val nume = x - y1
            val deno = x - y2
            return if (deno.isZero()) {
                if (nume.isZero()) {
                    one(mc)
                } else {
                    inf(mc)
                }
            } else {
                nume / deno
            }
        }

        /**
         * Returns the cross ratio of the four complex.
         *
         *     crossRatio(x1,x2,x3,x4) = (x1-x3)(x2-x4)/((x1-x4)(x2-x3))
         *
         * Some properties:
         *
         * 1. crossRatio(x1,x2,x3,x4) is real iff x1,x2,x3,x4 is on a circle (including line)
         * 2.
         *
         */
        @JvmStatic
        fun <T> crossRatio(
                x1: ComplexE<T>, x2: ComplexE<T>,
                x3: ComplexE<T>, x4: ComplexE<T>,
                mc: FieldCalculator<T>
        ): ComplexE<T> {

            //  (x1-x3)(x2-x4)/((x1-x4)(x2-x3))
            if (x1 !is Complex) {
                return minusFrac(x2, x4, x3, mc)
            }
            if (x2 !is Complex) {
                return minusFrac(x1, x3, x4, mc)
            }
            if (x3 !is Complex) {
                return minusFrac(x4, x2, x1, mc)
            }
            if (x4 !is Complex) {
                return minusFrac(x3, x1, x2, mc)
            }
            return (x1 - x3) * (x2 - x4) / ((x1 - x4) * (x2 - x3))
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


//class ComplexE<T> {
//    
//
//    private val v: Complex<T>?
//
//    val isInf: Boolean
//        @JvmName("isInf")
//        get() = v == null
//
//    val value: Complex<T>
//        get() {
//            if (isInf) {
//                throw ArithmeticException("Inf")
//            }
//            return v!!
//        }
//
//    internal constructor(v: Complex<T>) {
//        this.v = v
//    }
//
//    internal constructor() {
//        v = null
//    }
//
//    fun isLimited(): Boolean = !isInf
//
//    override fun toString(): String {
//        return if (isInf) {
//            "Inf"
//        } else {
//            v.toString()
//        }
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as ComplexE<*>
//
//        if (v != other.v) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        return v?.hashCode() ?: 0
//    }
//
//
//}