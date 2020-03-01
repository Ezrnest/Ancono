package cn.ancono.math.numberModels

import cn.ancono.math.MathCalculator
import cn.ancono.math.geometry.analytic.planeAG.PVector
import cn.ancono.math.geometry.analytic.planeAG.Point
import cn.ancono.utilities.SNFSupport

import java.text.DecimalFormat

/**
 * An implement of complex number where double is used and more methods are
 * supplied.
 * @author lyc
 */
class ComplexI(private val a: Double, private val b: Double) {
    private var m = UNCALCULATED
    private var arg = UNCALCULATED
    /**
     * Returns Re(this).
     * @return Re(this)
     */
    fun re(): Double {
        return a
    }

    /**
     * Returns Im(this).
     * @return Im(this)
     */
    fun im(): Double {
        return b
    }

    /**
     * Returns arg(this),the angle must be in [-pi,pi]
     * @return
     */
    fun arg(): Double {
        if (arg == UNCALCULATED) {
            arg = Math.atan2(b, a)
        }
        return arg
    }

    /**
     * Returns |this|.
     * @return |this|
     */
    fun mod(): Double {
        if (m == UNCALCULATED) {
            m = Math.hypot(a, b)
        }
        return m
    }

    /**
     * Returns `this + z`
     * @param z another complex
     * @return `this + z`
     */
    fun add(z: ComplexI): ComplexI {
        return ComplexI(a + z.a, b + z.b)
    }

    /**
     * Returns `-this`
     * @return `-this`
     */
    fun negate(): ComplexI {
        return ComplexI(-a, -b)
    }

    /**
     * Returns `this - z`
     * @param z another complex
     * @return `this - z`
     */
    fun subtract(z: ComplexI): ComplexI {
        return ComplexI(a - z.a, b - z.b)
    }

    /**
     * Returns `this * z`
     * @param z another complex
     * @return `this * z`
     */
    fun multiply(z: ComplexI): ComplexI {
        return ComplexI(a * z.a - b * z.b, a * z.b + b * z.a)
    }

    /**
     * Returns `this / z`
     * @param z another complex
     * @return `this / z`
     * @throws ArithmeticException if z = 0
     */
    fun divide(z: ComplexI): ComplexI {
        val d = z.a * z.a + z.b * z.b
        var an = a * z.a + b * z.b
        var bn = b * z.a - a * z.b
        an /= d
        bn /= d
        return ComplexI(an, bn)
    }

    /**
     * Returns `1/this`
     * @return `1/this`
     */
    fun reciprocal(): ComplexI {
        val mod2 = a * a + b * b
        return ComplexI(a / mod2, -b / mod2)
    }

    /**
     * Returns the conjugate complex number of `this`.
     * @return
     * <pre>____
     * this
    </pre> *
     */
    fun conjugate(): ComplexI {
        return ComplexI(a, -b)
    }


    /**
     * Returns `this^p`,this method will calculate by using angle form.If
     * `p==0`,ONE will be returned.
     *
     *
     * @see .pow
     * @param p
     * @return `this^p`
     */
    fun powArg(p: Long): ComplexI {
        if (p == 0L) {
            return ONE
        }
        // (r,theta)^p = (r^p,p*theta)
        var arg = arg()
        var m = mod()
        m = Math.pow(m, p.toDouble())
        arg *= p.toDouble()
        return modArg(m, arg)
    }

    /**
     * Returns `this^p`.This method is based on multiply operation.If
     * `p==0`,ONE will be returned.
     *
     *
     * @see .powArg
     * @param p
     * @return `this^p`
     */
    fun pow(p: Long): ComplexI {
        var p = p
        if (p < 0) {
            return this.reciprocal().pow(-p)
        }
        //		if(p==0){
        //			return ONE;
        //		}
        var t = ONE
        var mul = this
        while (p != 0L) {
            if (p and 1 != 0L) {
                t = t.multiply(mul)
            }
            mul = mul.multiply(mul)
            p = p shr 1
        }
        return t
    }


    /**
     * Returns n-th roots of the complex.
     * @param n must fit `n>0`
     * @return
     */
    fun root(n: Long): ComplexResult {
        if (n <= 0) {
            throw IllegalArgumentException("n<=0")
        }
        val arg = arg()
        var m = mod()

        m = Math.exp(Math.log(m) / n)
        return RootResult(n, m, arg)
    }

    /**
     * Returns <pre>
     * this<sup>f</sup>
    </pre> *
     * @param f a Fraction
     * @return
     */
    fun pow(f: Fraction): ComplexResult {
        if (f.signum == 0) {
            //			if(this.a == 0 && this.b == 0){
            //				throw new IllegalArgumentException("0^0");
            //			}
            return RootResult(1, 1.0, arg())
        }
        val p: Long
        val q: Long
        if (f.signum == -1) {
            p = f.denominator
            q = f.numerator
        } else {
            p = f.numerator
            q = f.denominator
        }
        return pow(p).root(q)
    }

    private class RootResult(size: Long, private val m: Double, private val arg: Double) : ComplexResult(size) {

        override val isInfinite: Boolean
            get() = false

        override fun iterator(): Iterator<ComplexI> {
            return object : Iterator<ComplexI> {
                private var index: Long = 0

                override fun next(): ComplexI {
                    return ComplexI.modArg(m, (index++ * TWO_PI + arg) / size)
                }

                override fun hasNext(): Boolean {
                    return index < size
                }
            }
        }

        override fun mainValue(): ComplexI {
            return ComplexI.modArg(m, arg / size)
        }

        override operator fun contains(z: ComplexI): Boolean {
            if (z.mod() == m) {
                //we use two-divide method
                val arg = z.arg()
                var downer: Long = 0
                var upper = size - 1
                while (downer <= upper) {
                    val t = (downer + upper) / 2
                    val arg0 = (arg + t * TWO_PI) / size
                    if (arg0 == arg) {
                        return true
                    } else if (arg0 < arg) {
                        downer = t + 1
                    } else {
                        upper = t - 1
                    }
                }
            }
            return false
        }

    }

    //	public ComplexResult

    /**
     * This class describes the complex result set of multiple result functions in complex
     * calculation such as root() or so on.
     *
     *
     * In the implement of this class,usually,the results will only be calculated when
     * they are required,and they are not saved,so if the result is required for multiple times,
     * extra temptation is recommended.
     *
     * @author lyc
     */
    abstract class ComplexResult internal constructor(protected val size: Long) : Iterable<ComplexI> {
        /**
         * Returns `true` if the number of result.
         * @return
         */
        open val isInfinite: Boolean
            get() = size == -1L

        /**
         * Returns the number of complexes in this result set,if the
         * number of results is infinite,this method should return `-1`
         * @return the number of results,or `-1`
         */
        fun number(): Long {
            return size
        }

        /**
         * Returns the main value of this result.
         * @return a complex number
         */
        abstract fun mainValue(): ComplexI

        /**
         * Returns `true` if the result contains the result.This method is
         * usually used in the infinite-value result.
         * @param z complex number
         * @return `true` if the result contains the specific complex.
         */
        abstract operator fun contains(z: ComplexI): Boolean
    }


    /**
     * Returns the point representing this Complex number,the calculator will be
     * the default Double-calculator.
     * @return a point
     */
    fun toPoint(mc: MathCalculator<Double>): Point<Double> {
        return Point(mc, a, b)
    }

    /**
     * Returns the vector representing this Complex number,the calculator will be
     * the default Double-calculator.
     * @return a vector
     */
    fun toVector(mc: MathCalculator<Double>): PVector<Double> {
        return PVector.valueOf(a, b, mc)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is ComplexI) {
            val z = obj as ComplexI?
            return a == z!!.a && b == z.b
        }
        return false
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(a).append(' ')
        if (b < 0) {
            sb.append("- ").append(-b)
        } else {
            sb.append("+ ").append(b)
        }
        sb.append('i')
        return sb.toString()
    }

    private class LogResult internal constructor(private val x: Double, private val arg: Double) : ComplexResult(-1) {
        override val isInfinite: Boolean
            get() = true

        override fun iterator(): Iterator<ComplexI> {
            return object : Iterator<ComplexI> {
                internal var index: Long = 0

                override fun hasNext(): Boolean {
                    return true
                }

                override fun next(): ComplexI {
                    return ComplexI(x, arg + TWO_PI * index++)
                }
            }
        }

        override fun mainValue(): ComplexI {
            return ComplexI(x, arg)
        }

        override fun contains(z: ComplexI): Boolean {
            if (z.a == x) {
                var b = z.b
                if (b < 0) {
                    b = -b
                }
                while (b > 0) {
                    b -= TWO_PI
                }
                return b == 0.0
            }
            return false
        }
    }

    companion object {
        /**
         * An useful value in complex.
         */
        private val TWO_PI = 2 * Math.PI
        private val UNCALCULATED = java.lang.Double.NaN
        val ANGLE_UPPER_BOUND = Math.PI
        val ANGLE_DOWNER_BOUND = -Math.PI
        val ZERO = ComplexI(0.0, 0.0)
        val ONE = ComplexI(1.0, 0.0)
        val I_ONE = ComplexI(0.0, 1.0)

        fun real(a: Double): ComplexI {
            return ComplexI(a, 0.0)
        }

        fun imaginary(b: Double): ComplexI {
            return ComplexI(0.0, b)
        }


        /**
         * Returns the Complex z that `arg(z) = arg && |z| = mod`.The `arg` of this complex will be adjusted so that
         * it will be in [-pi,pi] and of `mod` is negative,then it will be turned to positive and corresponding `arg` will
         * be modified.
         * @param arg
         * @param mod
         * @return
         */
        fun modArg(mod: Double, arg: Double): ComplexI {
            var mod = mod
            var arg = arg
            if (mod < 0) {
                mod = -mod
                arg += Math.PI
            }
            if (arg > ANGLE_UPPER_BOUND || arg < ANGLE_DOWNER_BOUND) {
                //adjustment first.
                var pi = TWO_PI
                if (arg > ANGLE_UPPER_BOUND) {
                    pi = -pi
                }
                do {
                    arg += pi
                } while (arg > ANGLE_UPPER_BOUND || arg < ANGLE_DOWNER_BOUND)
            }

            val a = Math.cos(arg) * mod
            val b = Math.sin(arg) * mod
            val z = ComplexI(a, b)
            z.arg = arg
            z.m = mod
            return z
        }

        /**
         * Returns the complex value of `e^z`.
         * @param z a complex number
         * @return `e^z`
         */
        fun exponentZ(z: ComplexI): ComplexI {
            val m = Math.exp(z.a)
            return modArg(m, z.b)
        }

        /**
         * Returns the complex value of `Ln(z)`,which can be calculated as
         * <pre>
         * result = ln(|z|) + (arg(z)+2k*Pi)i
        </pre> *
         * and the primary value is
         * <pre> ln(|z|) + arg(z)i</pre>
         * The number of results is infinite,and
         * the iterator of the ComplexResult will iterate from
         * @param z a complex number except 0.
         * @return the results.
         */
        fun logarithmZ(z: ComplexI): ComplexResult {
            val mod = z.mod()
            if (mod == 0.0) {
                throw ArithmeticException("ln(0)")
            }
            val x = Math.log(mod)
            val arg = z.arg()
            return LogResult(x, arg)
        }

        /**
         * Returns sin(z),which is defined as
         * <pre>
         * (e<sup>iz</sup> - e<sup>-iz</sup>)/2
        </pre> *
         * @param z a complex
         * @return sin(z)
         */
        fun sinZ(z: ComplexI): ComplexI {
            val iz = ComplexI(-z.b, z.a)
            val eiz = exponentZ(iz)
            val t = eiz.a * eiz.a + eiz.b * eiz.b
            val tt = t * 2.0
            val a = eiz.b * (t + 1) / tt
            val b = eiz.a * (t - 1) / tt
            return ComplexI(a, b)
        }

        /**
         * Returns cos(z),which is defined as
         * <pre>
         * (e<sup>iz</sup> + e<sup>-iz</sup>)/2
        </pre> *
         * @param z a complex
         * @return cos(z)
         */
        fun cosZ(z: ComplexI): ComplexI {
            val iz = ComplexI(-z.b, z.a)
            val eiz = exponentZ(iz)
            val t = eiz.a * eiz.a + eiz.b * eiz.b
            val tt = t * 2.0
            val a = eiz.b * (t - 1) / tt
            val b = eiz.a * (t + 1) / tt
            return ComplexI(a, b)
        }

        /**
         * Returns tan(z),which is defined as
         * <pre>
         * (e<sup>iz</sup> - e<sup>-iz</sup>)/(e<sup>iz</sup> + e<sup>-iz</sup>)
        </pre> *
         * @param z a complex
         * @return tan(z)
         */
        fun tanZ(z: ComplexI): ComplexI {
            val iz = ComplexI(-z.b, z.a)
            val t = exponentZ(iz)
            //a^2-b^2
            val a0 = t.a * t.a - t.b * t.b
            val b0 = 2.0 * t.a * t.b
            val re = of(a0 - 1, b0).divide(of(a0 + 1, b0))
            return ComplexI(-re.b, re.a)
        }

        /**
         * Format the given complex with the given precision.
         * @param precision indicate the precision.
         * @return
         */
        fun format(z: ComplexI, precision: Int): String {
            return format(z)
        }

        /**
         * Format the given complex with default precision.
         * @param z
         * @return
         */
        fun format(z: ComplexI): String {
            val sb = StringBuilder()
            if (z.b < -DEFAULT_RANGE_OF_ZERO || z.b > DEFAULT_RANGE_OF_ZERO) {
                sb.append(df.format(z.a))
            } else {
                sb.append('0')
            }
            if (z.b < -DEFAULT_RANGE_OF_ZERO || z.b > DEFAULT_RANGE_OF_ZERO) {
                if (z.b < 0) {
                    sb.append('-').append(df.format(-z.b))
                } else {
                    sb.append('+').append(df.format(z.b))
                }
                sb.append('i')
            }
            return sb.toString()
        }

        private val df = SNFSupport.DF
        private val DEFAULT_RANGE_OF_ZERO = 0.0005
        private fun of(a: Double, b: Double): ComplexI {
            return ComplexI(a, b)
        }
    }


    //	public static void main(String[] args) {
    //		//test here
    ////		ComplexI[] zs = new ComplexI[16];
    ////		zs[0] = of(-2,1);
    ////		zs[1] = of(1,-2);
    ////		print(zs[0].reciprocal().add(zs[1].reciprocal()));
    //		ComplexI w = modArg(1, TWO_PI/3),sum = ZERO;
    //		print(format(w));
    //		for(int i=0;i<2011;i++){
    //			sum = sum.add(w.pow(i));
    //		}
    //		print(format(sum));
    //		print(format(w.pow(30).add(w.pow(40)).add(w.pow(50))));
    //		print(format(w.pow(2009).add(w.reciprocal().pow(2009))));
    //
    //	}
}
