package cn.ancono.math.numberModels;

import cn.ancono.math.geometry.analytic.plane.PVector;
import cn.ancono.math.geometry.analytic.plane.Point;
import cn.ancono.math.numberModels.api.FieldNumberModel;
import cn.ancono.math.numberModels.api.RealCalculator;
import cn.ancono.utilities.SNFSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

;

/**
 * An implement of complex number where double is used and more methods are
 * supplied.
 *
 * @author lyc
 */
public record ComplexD(double a, double b) implements FieldNumberModel<ComplexD> {
    /**
     * An useful value in complex.
     */
    private static final double TWO_PI = 2 * Math.PI;

    public static final double ANGLE_UPPER_BOUND = Math.PI;
    public static final double ANGLE_DOWNER_BOUND = -Math.PI;

    public static final ComplexD ZERO = new ComplexD(0d, 0d);
    public static final ComplexD ONE = new ComplexD(1d, 0d);
    public static final ComplexD I = new ComplexD(0d, 1d);
    public static final ComplexD PI = new ComplexD(Math.PI, 0d);
    public static final ComplexD E = new ComplexD(Math.E, 0d);


    /**
     * Returns Re(this).
     *
     * @return Re(this)
     */
    public double re() {
        return a;
    }

    /**
     * Returns Im(this).
     *
     * @return Im(this)
     */
    public double im() {
        return b;
    }

    /**
     * Returns arg(this),the angle must be in [-pi,pi]
     */
    public double arg() {
        return Math.atan2(b, a);
    }

    /**
     * Returns |this|.
     *
     * @return |this|
     */
    public double mod() {
        return Math.hypot(a, b);
    }

    /**
     * Returns |this| as a complex number.
     *
     * @return |this|
     */
    public ComplexD modAsC() {
        return ComplexD.real(mod());
    }

    /**
     * Returns {@code this + z}
     *
     * @param z another complex
     * @return {@code this + z}
     */
    @Override
    public @NotNull
    ComplexD add(ComplexD z) {
        return new ComplexD(a + z.a, b + z.b);
    }

    /**
     * Returns {@code -this}
     *
     * @return {@code -this}
     */
    @Override
    public @NotNull
    ComplexD negate() {
        return new ComplexD(-a, -b);
    }

    /**
     * Returns {@code this - z}
     *
     * @param z another complex
     * @return {@code this - z}
     */
    @Override
    public @NotNull
    ComplexD subtract(ComplexD z) {
        return new ComplexD(a - z.a, b - z.b);
    }

    /**
     * Returns {@code this * z}
     *
     * @param z another complex
     * @return {@code this * z}
     */
    @Override
    public @NotNull
    ComplexD multiply(ComplexD z) {
        return new ComplexD(a * z.a - b * z.b, a * z.b + b * z.a);
    }

    public ComplexD multiply(double d) {
        return new ComplexD(a * d, b * d);
    }

    /**
     * Returns {@code this / z}
     *
     * @param z another complex
     * @return {@code this / z}
     * @throws ArithmeticException if z = 0
     */
    @Override
    public @NotNull
    ComplexD divide(ComplexD z) {
        double d = z.a * z.a + z.b * z.b;
        double an = a * z.a + b * z.b;
        double bn = b * z.a - a * z.b;
        an /= d;
        bn /= d;
        return new ComplexD(an, bn);
    }

    public ComplexD divide(double d) {
        return new ComplexD(a / d, b / d);
    }


    /**
     * Returns {@code 1/this}
     *
     * @return {@code 1/this}
     */
    @Override
    public @NotNull
    ComplexD reciprocal() {
        double mod2 = a * a + b * b;
        return new ComplexD(a / mod2, -b / mod2);
    }

    /**
     * Returns the conjugate complex number of {@code this}:
     * <span style="text-decoration: overline">this</span>
     */
    public ComplexD conjugate() {
        return new ComplexD(a, -b);
    }


    /**
     * Returns {@code this^p},this method will calculate by using angle form.If
     * {@code p==0},ONE will be returned.<p>
     *
     * @return {@code this^p}
     * @see #pow(long)
     */
    public ComplexD powArg(long p) {
        if (p == 0) {
            return ONE;
        }
        // (r,theta)^p = (r^p,p*theta)
        double arg = arg();
        double m = mod();
        m = Math.pow(m, p);
        arg *= p;
        return modArg(m, arg);
    }

    /**
     * Returns {@code this^p}.This method is based on multiply operation.If
     * {@code p==0},ONE will be returned.<p>
     *
     * @return {@code this^p}
     * @see #powArg(long)
     */
    @NotNull
    @Override
    public ComplexD pow(long n) {
        if (n < 0) {
            return this.reciprocal().pow(-n);
        }
//		if(p==0){
//			return ONE;
//		}
        ComplexD t = ONE, mul = this;
        while (n != 0) {
            if ((n & 1) != 0) {
                t = t.multiply(mul);
            }
            mul = mul.multiply(mul);
            n >>= 1;
        }
        return t;
    }

    /**
     * Returns <code>x<sup>y</sup> = e<sup>ln(x)*y</sup></code>, where <code>x</code> is the complex number of
     * <code>this</code>.
     *
     * @param y a complex number
     */
    public ComplexD pow(ComplexD y) {
        return ComplexD.exp(y.multiply(ComplexD.logarithm(this).mainValue()));
    }
//    /**
//     * Returns <code>log<sub>x</sub>(y) = ln(y)/ln(x), where <code>x</code> is the complex number of <code>this</code>.
//     * @param y a complex number
//     */
//    public ComplexResult log(ComplexI y){
//
//    }


    /**
     * Returns n-th roots of the complex.
     *
     * @param n must fit {@code n>0}
     */
    public ComplexResult root(long n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n<=0");
        }
        double arg = arg();
        double m = mod();

        m = Math.exp(Math.log(m) / n);
        return new RootResult(n, m, arg);
    }

    /**
     * Returns <pre>
     * this<sup>f</sup>
     * </pre>
     *
     * @param f a Fraction
     */
    public ComplexResult pow(Fraction f) {
        if (f.getSignum() == 0) {
//			if(this.a == 0 && this.b == 0){
//				throw new IllegalArgumentException("0^0");
//			}
            return new RootResult(1, 1, arg());
        }
        long p, q;
        if (f.getSignum() == -1) {
            p = f.getDenominator();
            q = f.getNumeratorAbs();
        } else {
            p = f.getNumerator();
            q = f.getDenominator();
        }
        return pow(p).root(q);
    }

    @Override
    public boolean isZero() {
        return a == 0 && b == 0;
    }

    private static class RootResult extends ComplexResult {

        private final double m, arg;

        protected RootResult(long size, double m, double arg) {
            super(size);
            this.arg = arg;
            this.m = m;
        }

        @NotNull
        @Override
        public Iterator<ComplexD> iterator() {
            return new Iterator<>() {
                private long index = 0;

                @Override
                public ComplexD next() {
                    return ComplexD.modArg(m, ((index++) * TWO_PI + arg) / size);
                }

                @Override
                public boolean hasNext() {
                    return index < size;
                }
            };
        }

        @Override
        public ComplexD mainValue() {
            return ComplexD.modArg(m, arg / size);
        }

        @Override
        public boolean isInfinite() {
            return false;
        }

        @Override
        public boolean contains(ComplexD z) {
            if (z.mod() == m) {
                //we use two-divide method
                double arg = z.arg();
                long downer = 0, upper = size - 1;
                while (downer <= upper) {
                    long t = (downer + upper) / 2;
                    double arg0 = (arg + t * TWO_PI) / size;
                    if (arg0 == arg) {
                        return true;
                    } else if (arg0 < arg) {
                        downer = t + 1;
                    } else {
                        upper = t - 1;
                    }
                }
            }
            return false;
        }

    }

//	public ComplexResult

    /**
     * This class describes the complex result set of multiple result functions in complex
     * calculation such as root() or so on.<p>
     * In the implement of this class,usually,the results will only be calculated when
     * they are required,and they are not saved,so if the result is required for multiple times,
     * extra temptation is recommended.
     *
     * @author lyc
     */
    public static abstract class ComplexResult implements Iterable<ComplexD> {
        protected final long size;

        ComplexResult(long size) {
            this.size = size;
        }

        /**
         * Returns the number of complexes in this result set,if the
         * number of results is infinite,this method should return {@code -1}
         *
         * @return the number of results,or {@code -1}
         */
        public long number() {
            return size;
        }

        /**
         * Returns {@code true} if the number of result.
         */
        public boolean isInfinite() {
            return size == -1;
        }

        /**
         * Returns the main value of this result.
         *
         * @return a complex number
         */
        public abstract ComplexD mainValue();

        /**
         * Returns {@code true} if the result contains the result.This method is
         * usually used in the infinite-value result.
         *
         * @param z complex number
         * @return {@code true} if the result contains the specific complex.
         */
        public abstract boolean contains(ComplexD z);
    }


    /**
     * Returns the point representing this Complex number,the calculator will be
     * the default Double-calculator.
     *
     * @return a point
     */
    public Point<Double> toPoint(RealCalculator<Double> mc) {
        return new Point<>(mc, a, b);
    }

    /**
     * Returns the vector representing this Complex number,the calculator will be
     * the default Double-calculator.
     *
     * @return a vector
     */
    public PVector<Double> toVector(RealCalculator<Double> mc) {
        return PVector.valueOf(a, b, mc);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComplexD z) {
            return a == z.a && b == z.b;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(a).append(' ');
        if (b < 0) {
            sb.append("- ").append(-b);
        } else {
            sb.append("+ ").append(b);
        }
        sb.append('i');
        return sb.toString();
    }

    public static ComplexD real(double a) {
        return new ComplexD(a, 0d);
    }

    public static ComplexD imag(double b) {
        return new ComplexD(0d, b);
    }


    /**
     * Returns the Complex z that {@code arg(z) = arg && |z| = mod}.The {@code arg} of this complex will be adjusted so
     * that
     * it will be in [-pi,pi] and of {@code mod} is negative,then it will be turned to positive and corresponding {@code
     * arg} will
     * be modified.
     */
    public static ComplexD modArg(double mod, double arg) {
        double a = Math.cos(arg) * mod;
        double b = Math.sin(arg) * mod;
        return new ComplexD(a, b);
    }

    /**
     * Returns the complex value of {@code e^z}.
     *
     * @param z a complex number
     * @return {@code e^z}
     */
    public static ComplexD exp(ComplexD z) {
        double m = Math.exp(z.a);
        return modArg(m, z.b);
    }

    /**
     * Returns the result of <code>e<sup>it</sup></code>.
     */
    public static ComplexD expIt(ComplexD t) {
        var a = -t.b;
        var b = t.a;
        var m = Math.exp(a);
        return modArg(m, b);
    }

    /**
     * Returns the complex value of {@code Ln(z)},which can be calculated as
     * <pre>
     * result = ln(|z|) + (arg(z)+2k*Pi)i
     * </pre>
     * and the primary value is
     * <pre> ln(|z|) + arg(z)i</pre>
     * The number of results is infinite, and
     * the iterator of the ComplexResult will iterate from
     *
     * @param z a complex number except 0.
     * @return the results.
     */
    public static ComplexResult logarithm(ComplexD z) {
        var main = ln(z);
        return new LogResult(main.a, main.b);
    }

    /**
     * Returns the primary value of <code>ln(z)</code>
     * <pre>
     *     result = ln(|z|) + arg(z)i
     * </pre>
     */
    public static ComplexD ln(ComplexD z) {
        double mod = z.mod();
        if (mod == 0) {
            throw new ArithmeticException("ln(0)");
        }
        double x = Math.log(mod);
        double arg = z.arg();
        return new ComplexD(x, arg);
    }

    private static class LogResult extends ComplexResult {
        private final double x, arg;

        LogResult(double x, double arg) {
            super(-1);
            this.x = x;
            this.arg = arg;
        }

        @Override
        public @NotNull
        Iterator<ComplexD> iterator() {
            return new Iterator<>() {
                long index = 0;

                @Override
                public boolean hasNext() {
                    return true;
                }

                @Override
                public ComplexD next() {
                    return new ComplexD(x, arg + TWO_PI * index++);
                }
            };
        }

        @Override
        public ComplexD mainValue() {
            return new ComplexD(x, arg);
        }

        @Override
        public boolean isInfinite() {
            return true;
        }

        @Override
        public boolean contains(ComplexD z) {
            if (z.a == x) {
                double b = z.b;
                if (b < 0) {
                    b = -b;
                }
                while (b > 0) {
                    b -= TWO_PI;
                }
                return b == 0;
            }
            return false;
        }
    }

    /**
     * Returns sin(z),which is defined as
     * <pre>
     * (e<sup>iz</sup> - e<sup>-iz</sup>)/2
     * </pre>
     *
     * @param z a complex
     * @return sin(z)
     */
    public static ComplexD sin(ComplexD z) {
        ComplexD iz = new ComplexD(-z.b, z.a);
        ComplexD eiz = exp(iz);
        double t = eiz.a * eiz.a + eiz.b * eiz.b;
        double tt = t * 2d;
        double a = eiz.b * (t + 1) / tt;
        double b = eiz.a * (t - 1) / tt;
        return new ComplexD(a, b);
    }

    /**
     * Returns cos(z),which is defined as
     * <pre>
     * (e<sup>iz</sup> + e<sup>-iz</sup>)/2
     * </pre>
     *
     * @param z a complex
     * @return cos(z)
     */
    public static ComplexD cos(ComplexD z) {
        ComplexD iz = new ComplexD(-z.b, z.a);
        ComplexD eiz = exp(iz);
        double t = eiz.a * eiz.a + eiz.b * eiz.b;
        double tt = t * 2d;
        double a = eiz.b * (t - 1) / tt;
        double b = eiz.a * (t + 1) / tt;
        return new ComplexD(a, b);
    }

    /**
     * Returns tan(z),which is defined as
     * <pre>
     * (e<sup>iz</sup> - e<sup>-iz</sup>)/(e<sup>iz</sup> + e<sup>-iz</sup>)
     * </pre>
     *
     * @param z a complex
     * @return tan(z)
     */
    public static ComplexD tan(ComplexD z) {
        ComplexD iz = new ComplexD(-z.b, z.a);
        ComplexD t = exp(iz);
        //a^2-b^2
        double a0 = t.a * t.a - t.b * t.b;
        double b0 = 2 * t.a * t.b;
        ComplexD re = of(a0 - 1, b0).divide(of(a0 + 1, b0));
        return new ComplexD(-re.b, re.a);
    }

    /**
     * Format the given complex with the given precision.
     *
     * @param precision indicate the precision.
     */
    public static String format(ComplexD z, int precision) {
        return format(z);
    }

    /**
     * Format the given complex with default precision.
     *
     * @param z a complex number
     */
    public static String format(ComplexD z) {
        StringBuilder sb = new StringBuilder();
        var df = SNFSupport.DF;
        if (z.b < -DEFAULT_RANGE_OF_ZERO || z.b > DEFAULT_RANGE_OF_ZERO) {
            sb.append(df.format(z.a));
        } else {
            sb.append('0');
        }
        if (z.b < -DEFAULT_RANGE_OF_ZERO || z.b > DEFAULT_RANGE_OF_ZERO) {
            if (z.b < 0) {
                sb.append('-').append(df.format(-z.b));
            } else {
                sb.append('+').append(df.format(z.b));
            }
            sb.append('i');
        }
        return sb.toString();
    }

    private static final double DEFAULT_RANGE_OF_ZERO = 0.0005d;

    private static ComplexD of(double a, double b) {
        return new ComplexD(a, b);
    }


    static class ComplexICalculator extends MathCalculatorAdapter<ComplexD> {
        @NotNull
        @Override
        public ComplexD getOne() {
            return ComplexD.ONE;
        }

        @NotNull
        @Override
        public ComplexD getZero() {
            return ComplexD.ZERO;
        }

        @Override
        public boolean isZero(@NotNull ComplexD para) {
            return para.isZero();
        }

        @Override
        public boolean isEqual(@NotNull ComplexD x, @NotNull ComplexD y) {
            return x.equals(y);
        }

        @Override
        public int compare(@NotNull ComplexD x, @NotNull ComplexD y) {
            throw new UnsupportedOperationException("Complex is not comparable.");
        }

        @Override
        public boolean isComparable() {
            return false;
        }

        @NotNull
        @Override
        public ComplexD add(@NotNull ComplexD x, @NotNull ComplexD y) {
            return x.add(y);
        }

        @NotNull
        @Override
        public ComplexD negate(@NotNull ComplexD x) {
            return x.negate();
        }

        @NotNull
        @Override
        public ComplexD abs(@NotNull ComplexD x) {
            return x.modAsC();
        }

        @NotNull
        @Override
        public ComplexD subtract(@NotNull ComplexD x, @NotNull ComplexD y) {
            return x.subtract(y);
        }

        @NotNull
        @Override
        public ComplexD multiply(@NotNull ComplexD x, @NotNull ComplexD y) {
            return x.multiply(y);
        }

        @NotNull
        @Override
        public ComplexD divide(@NotNull ComplexD x, @NotNull ComplexD y) {
            return x.divide(y);
        }

        @NotNull
        @Override
        public ComplexD divideLong(@NotNull ComplexD x, long n) {
            return x.divide(n);
        }

        @NotNull
        @Override
        public ComplexD multiplyLong(@NotNull ComplexD x, long n) {
            return x.multiply(n);
        }

        @NotNull
        @Override
        public ComplexD reciprocal(@NotNull ComplexD x) {
            return x.reciprocal();
        }

        @NotNull
        @Override
        public ComplexD squareRoot(@NotNull ComplexD x) {
            return x.root(2).mainValue();
        }

        @NotNull
        @Override
        public ComplexD pow(@NotNull ComplexD x, long n) {
            return x.pow(n);
        }

        @NotNull
        @Override
        public ComplexD exp(@NotNull ComplexD a, @NotNull ComplexD b) {
            return a.pow(b);
        }

        @NotNull
        @Override
        public ComplexD log(@NotNull ComplexD a, @NotNull ComplexD b) {
            return ComplexD.logarithm(b).mainValue().divide(ComplexD.logarithm(a).mainValue());
        }

        @NotNull
        @Override
        public ComplexD cos(@NotNull ComplexD x) {
            return ComplexD.cos(x);
        }

        @NotNull
        @Override
        public ComplexD tan(@NotNull ComplexD x) {
            return ComplexD.tan(x);
        }

        @NotNull
        @Override
        public ComplexD arccos(@NotNull ComplexD x) {
            //TODO
            return super.arccos(x);
        }

        @NotNull
        @Override
        public ComplexD arctan(@NotNull ComplexD x) {
            //TODO
            return super.arctan(x);
        }

        @NotNull
        @Override
        public ComplexD nroot(@NotNull ComplexD x, long n) {
            return x.root(n).mainValue();
        }

        @Nullable
        @Override
        public ComplexD constantValue(@NotNull String name) {
            if (name.equals("i")) {
                return ComplexD.I;
            } else if (name.equalsIgnoreCase("pi")) {
                return ComplexD.PI;
            } else if (name.equals("e")) {
                return ComplexD.E;
            }
            throw new UnsupportedOperationException("No constant value for: " + name);
        }

        @NotNull
        @Override
        public ComplexD exp(@NotNull ComplexD x) {
            return ComplexD.exp(x);
        }

        @NotNull
        @Override
        public ComplexD ln(@NotNull ComplexD x) {
            return ComplexD.logarithm(x).mainValue();
        }

        @NotNull
        @Override
        public ComplexD sin(@NotNull ComplexD x) {
            return ComplexD.sin(x);
        }

        @NotNull
        @Override
        public ComplexD arcsin(@NotNull ComplexD x) {
            //TODO
            return super.arcsin(x);
        }

        @NotNull
        @Override
        public ComplexD of(long n) {
            return real(n);
        }

        @NotNull
        @Override
        public ComplexD of(@NotNull Fraction x) {
            return real(x.toDouble());
        }
    }


    private static final ComplexICalculator cal = new ComplexICalculator();

    public static ComplexICalculator getCalculator() {
        return cal;
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
