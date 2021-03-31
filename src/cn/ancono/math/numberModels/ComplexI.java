package cn.ancono.math.numberModels;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.exceptions.UnsupportedCalculationException;
import cn.ancono.math.geometry.analytic.plane.PVector;
import cn.ancono.math.geometry.analytic.plane.Point;
import cn.ancono.math.numberModels.api.FieldNumberModel;
import cn.ancono.utilities.SNFSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Iterator;

/**
 * An implement of complex number where double is used and more methods are
 * supplied.
 *
 * @author lyc
 */
public final class ComplexI implements FieldNumberModel<ComplexI> {
    /**
     * An useful value in complex.
     */
    private static final double TWO_PI = 2 * Math.PI;

    private final double a, b;
    //    private static final double UNCALCULATED = Double.NaN;
    private double m = Double.NaN, arg = Double.NaN;
    public static final double ANGLE_UPPER_BOUND = Math.PI;
    public static final double ANGLE_DOWNER_BOUND = -Math.PI;
    public static final ComplexI ZERO = new ComplexI(0d, 0d),
            ONE = new ComplexI(1d, 0d),
            I_ONE = new ComplexI(0d, 1d),
            PI = new ComplexI(Math.PI, 0d),
            E = new ComplexI(Math.E, 0d);


    public ComplexI(double a, double b) {
        this.a = a;
        this.b = b;
    }

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
        return Double.isNaN(arg) ? (arg = Math.atan2(b, a)) : arg;
    }

    /**
     * Returns |this|.
     *
     * @return |this|
     */
    public double mod() {
        return Double.isNaN(m) ? (m = Math.hypot(a, b)) : m;
    }

    /**
     * Returns |this| as a complex number.
     *
     * @return |this|
     */
    public ComplexI modAsC() {
        return ComplexI.real(mod());
    }

    /**
     * Returns {@code this + z}
     *
     * @param z another complex
     * @return {@code this + z}
     */
    @Override
    public ComplexI add(ComplexI z) {
        return new ComplexI(a + z.a, b + z.b);
    }

    /**
     * Returns {@code -this}
     *
     * @return {@code -this}
     */
    @Override
    public ComplexI negate() {
        return new ComplexI(-a, -b);
    }

    /**
     * Returns {@code this - z}
     *
     * @param z another complex
     * @return {@code this - z}
     */
    @Override
    public ComplexI subtract(ComplexI z) {
        return new ComplexI(a - z.a, b - z.b);
    }

    /**
     * Returns {@code this * z}
     *
     * @param z another complex
     * @return {@code this * z}
     */
    @Override
    public ComplexI multiply(ComplexI z) {
        return new ComplexI(a * z.a - b * z.b, a * z.b + b * z.a);
    }

    public ComplexI multiply(double d) {
        return new ComplexI(a * d, b * d);
    }

    /**
     * Returns {@code this / z}
     *
     * @param z another complex
     * @return {@code this / z}
     * @throws ArithmeticException if z = 0
     */
    @Override
    public ComplexI divide(ComplexI z) {
        double d = z.a * z.a + z.b * z.b;
        double an = a * z.a + b * z.b;
        double bn = b * z.a - a * z.b;
        an /= d;
        bn /= d;
        return new ComplexI(an, bn);
    }

    public ComplexI divide(double d) {
        return new ComplexI(a / d, b / d);
    }


    /**
     * Returns {@code 1/this}
     *
     * @return {@code 1/this}
     */
    @Override
    public ComplexI reciprocal() {
        double mod2 = a * a + b * b;
        return new ComplexI(a / mod2, -b / mod2);
    }

    /**
     * Returns the conjugate complex number of {@code this}:
     * <span style="text-decoration: overline">this</span>
     */
    public ComplexI conjugate() {
        return new ComplexI(a, -b);
    }


    /**
     * Returns {@code this^p},this method will calculate by using angle form.If
     * {@code p==0},ONE will be returned.<p>
     *
     * @return {@code this^p}
     * @see #pow(long)
     */
    public ComplexI powArg(long p) {
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
     * @param p
     * @return {@code this^p}
     * @see #powArg(long)
     */
    public ComplexI pow(long p) {
        if (p < 0) {
            return this.reciprocal().pow(-p);
        }
//		if(p==0){
//			return ONE;
//		}
        ComplexI t = ONE, mul = this;
        while (p != 0) {
            if ((p & 1) != 0) {
                t = t.multiply(mul);
            }
            mul = mul.multiply(mul);
            p >>= 1;
        }
        return t;
    }

    /**
     * Returns <code>x<sup>y</sup> = e<sup>ln(x)*y</sup></code>, where <code>x</code> is the complex number of
     * <code>this</code>.
     *
     * @param y a complex number
     */
    public ComplexI exp(ComplexI y) {
        return ComplexI.expZ(y.multiply(ComplexI.logarithmZ(this).mainValue()));
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
            q = f.getNumerator();
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
        public Iterator<ComplexI> iterator() {
            return new Iterator<ComplexI>() {
                private long index = 0;

                @Override
                public ComplexI next() {
                    return ComplexI.modArg(m, ((index++) * TWO_PI + arg) / size);
                }

                @Override
                public boolean hasNext() {
                    return index < size;
                }
            };
        }

        @Override
        public ComplexI mainValue() {
            return ComplexI.modArg(m, arg / size);
        }

        @Override
        public boolean isInfinite() {
            return false;
        }

        @Override
        public boolean contains(ComplexI z) {
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
    public static abstract class ComplexResult implements Iterable<ComplexI> {
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
         *
         * @return
         */
        public boolean isInfinite() {
            return size == -1;
        }

        /**
         * Returns the main value of this result.
         *
         * @return a complex number
         */
        public abstract ComplexI mainValue();

        /**
         * Returns {@code true} if the result contains the result.This method is
         * usually used in the infinite-value result.
         *
         * @param z complex number
         * @return {@code true} if the result contains the specific complex.
         */
        public abstract boolean contains(ComplexI z);
    }


    /**
     * Returns the point representing this Complex number,the calculator will be
     * the default Double-calculator.
     *
     * @return a point
     */
    public Point<Double> toPoint(MathCalculator<Double> mc) {
        return new Point<>(mc, a, b);
    }

    /**
     * Returns the vector representing this Complex number,the calculator will be
     * the default Double-calculator.
     *
     * @return a vector
     */
    public PVector<Double> toVector(MathCalculator<Double> mc) {
        return PVector.valueOf(a, b, mc);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComplexI) {
            ComplexI z = (ComplexI) obj;
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

    public static ComplexI real(double a) {
        return new ComplexI(a, 0d);
    }

    public static ComplexI imaginary(double b) {
        return new ComplexI(0d, b);
    }


    /**
     * Returns the Complex z that {@code arg(z) = arg && |z| = mod}.The {@code arg} of this complex will be adjusted so
     * that
     * it will be in [-pi,pi] and of {@code mod} is negative,then it will be turned to positive and corresponding {@code
     * arg} will
     * be modified.
     *
     * @param arg
     * @param mod
     * @return
     */
    public static ComplexI modArg(double mod, double arg) {
        if (mod < 0) {
            mod = -mod;
            arg += Math.PI;
        }
        if (arg > ANGLE_UPPER_BOUND || arg < ANGLE_DOWNER_BOUND) {
            //adjustment first.
            double pi = TWO_PI;
            if (arg > ANGLE_UPPER_BOUND) {
                pi = -pi;
            }
            do {
                arg += pi;
            } while (arg > ANGLE_UPPER_BOUND || arg < ANGLE_DOWNER_BOUND);
        }

        double a = Math.cos(arg) * mod;
        double b = Math.sin(arg) * mod;
        ComplexI z = new ComplexI(a, b);
        z.arg = arg;
        z.m = mod;
        return z;
    }

    /**
     * Returns the complex value of {@code e^z}.
     *
     * @param z a complex number
     * @return {@code e^z}
     */
    public static ComplexI expZ(ComplexI z) {
        double m = Math.exp(z.a);
        return modArg(m, z.b);
    }

    /**
     * Returns the complex value of {@code Ln(z)},which can be calculated as
     * <pre>
     * result = ln(|z|) + (arg(z)+2k*Pi)i
     * </pre>
     * and the primary value is
     * <pre> ln(|z|) + arg(z)i</pre>
     * The number of results is infinite,and
     * the iterator of the ComplexResult will iterate from
     *
     * @param z a complex number except 0.
     * @return the results.
     */
    public static ComplexResult logarithmZ(ComplexI z) {
        double mod = z.mod();
        if (mod == 0) {
            throw new ArithmeticException("ln(0)");
        }
        double x = Math.log(mod);
        double arg = z.arg();
        return new LogResult(x, arg);
    }

    private static class LogResult extends ComplexResult {
        private final double x, arg;

        LogResult(double x, double arg) {
            super(-1);
            this.x = x;
            this.arg = arg;
        }

        @Override
        public Iterator<ComplexI> iterator() {
            return new Iterator<>() {
                long index = 0;

                @Override
                public boolean hasNext() {
                    return true;
                }

                @Override
                public ComplexI next() {
                    return new ComplexI(x, arg + TWO_PI * index++);
                }
            };
        }

        @Override
        public ComplexI mainValue() {
            return new ComplexI(x, arg);
        }

        @Override
        public boolean isInfinite() {
            return true;
        }

        @Override
        public boolean contains(ComplexI z) {
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
    public static ComplexI sinZ(ComplexI z) {
        ComplexI iz = new ComplexI(-z.b, z.a);
        ComplexI eiz = expZ(iz);
        double t = eiz.a * eiz.a + eiz.b * eiz.b;
        double tt = t * 2d;
        double a = eiz.b * (t + 1) / tt;
        double b = eiz.a * (t - 1) / tt;
        return new ComplexI(a, b);
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
    public static ComplexI cosZ(ComplexI z) {
        ComplexI iz = new ComplexI(-z.b, z.a);
        ComplexI eiz = expZ(iz);
        double t = eiz.a * eiz.a + eiz.b * eiz.b;
        double tt = t * 2d;
        double a = eiz.b * (t - 1) / tt;
        double b = eiz.a * (t + 1) / tt;
        return new ComplexI(a, b);
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
    public static ComplexI tanZ(ComplexI z) {
        ComplexI iz = new ComplexI(-z.b, z.a);
        ComplexI t = expZ(iz);
        //a^2-b^2
        double a0 = t.a * t.a - t.b * t.b;
        double b0 = 2 * t.a * t.b;
        ComplexI re = of(a0 - 1, b0).divide(of(a0 + 1, b0));
        return new ComplexI(-re.b, re.a);
    }

    /**
     * Format the given complex with the given precision.
     *
     * @param precision indicate the precision.
     */
    public static String format(ComplexI z, int precision) {
        return format(z);
    }

    /**
     * Format the given complex with default precision.
     *
     * @param z a complex number
     */
    public static String format(ComplexI z) {
        StringBuilder sb = new StringBuilder();
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

    private static final DecimalFormat df = SNFSupport.DF;
    private static final double DEFAULT_RANGE_OF_ZERO = 0.0005d;

    private static ComplexI of(double a, double b) {
        return new ComplexI(a, b);
    }


    static class ComplexICalculator extends MathCalculatorAdapter<ComplexI> {
        @NotNull
        @Override
        public ComplexI getOne() {
            return ComplexI.ONE;
        }

        @NotNull
        @Override
        public ComplexI getZero() {
            return ComplexI.ZERO;
        }

        @Override
        public boolean isZero(@NotNull ComplexI para) {
            return para.isZero();
        }

        @Override
        public boolean isEqual(@NotNull ComplexI x, @NotNull ComplexI y) {
            return x.equals(y);
        }

        @Override
        public int compare(@NotNull ComplexI x, @NotNull ComplexI y) {
            throw new UnsupportedCalculationException("Complex is not comparable.");
        }

        @Override
        public boolean isComparable() {
            return false;
        }

        @NotNull
        @Override
        public ComplexI add(@NotNull ComplexI x, @NotNull ComplexI y) {
            return x.add(y);
        }

        @NotNull
        @Override
        public ComplexI negate(@NotNull ComplexI x) {
            return x.negate();
        }

        @NotNull
        @Override
        public ComplexI abs(@NotNull ComplexI para) {
            return para.modAsC();
        }

        @NotNull
        @Override
        public ComplexI subtract(@NotNull ComplexI x, @NotNull ComplexI y) {
            return x.subtract(y);
        }

        @NotNull
        @Override
        public ComplexI multiply(@NotNull ComplexI x, @NotNull ComplexI y) {
            return x.multiply(y);
        }

        @NotNull
        @Override
        public ComplexI divide(@NotNull ComplexI x, @NotNull ComplexI y) {
            return x.divide(y);
        }

        @NotNull
        @Override
        public ComplexI divideLong(@NotNull ComplexI x, long n) {
            return x.divide(n);
        }

        @NotNull
        @Override
        public ComplexI multiplyLong(@NotNull ComplexI x, long n) {
            return x.multiply(n);
        }

        @NotNull
        @Override
        public ComplexI reciprocal(@NotNull ComplexI x) {
            return x.reciprocal();
        }

        @NotNull
        @Override
        public ComplexI squareRoot(@NotNull ComplexI x) {
            return x.root(2).mainValue();
        }

        @NotNull
        @Override
        public ComplexI pow(@NotNull ComplexI x, long n) {
            return x.pow(n);
        }

        @NotNull
        @Override
        public ComplexI exp(@NotNull ComplexI a, @NotNull ComplexI b) {
            return a.exp(b);
        }

        @NotNull
        @Override
        public ComplexI log(@NotNull ComplexI a, @NotNull ComplexI b) {
            return logarithmZ(b).mainValue().divide(logarithmZ(a).mainValue());
        }

        @NotNull
        @Override
        public ComplexI cos(@NotNull ComplexI x) {
            return cosZ(x);
        }

        @NotNull
        @Override
        public ComplexI tan(@NotNull ComplexI x) {
            return tanZ(x);
        }

        @NotNull
        @Override
        public ComplexI arccos(@NotNull ComplexI x) {
            //TODO
            return super.arccos(x);
        }

        @NotNull
        @Override
        public ComplexI arctan(@NotNull ComplexI x) {
            //TODO
            return super.arctan(x);
        }

        @NotNull
        @Override
        public ComplexI nroot(@NotNull ComplexI x, long n) {
            return x.root(n).mainValue();
        }

        @Nullable
        @Override
        public ComplexI constantValue(@NotNull String name) {
            if (name.equals("i")) {
                return ComplexI.I_ONE;
            } else if (name.equalsIgnoreCase("pi")) {
                return ComplexI.PI;
            } else if (name.equals("e")) {
                return ComplexI.E;
            }
            throw new UnsupportedCalculationException("No constant value for: " + name);
        }

        @NotNull
        @Override
        public ComplexI exp(@NotNull ComplexI x) {
            return expZ(x);
        }

        @NotNull
        @Override
        public ComplexI ln(@NotNull ComplexI x) {
            return ComplexI.logarithmZ(x).mainValue();
        }

        @NotNull
        @Override
        public ComplexI sin(@NotNull ComplexI x) {
            return sinZ(x);
        }

        @NotNull
        @Override
        public ComplexI arcsin(@NotNull ComplexI x) {
            //TODO
            return super.arcsin(x);
        }

        @NotNull
        @Override
        public ComplexI of(long x) {
            return real(x);
        }

        @NotNull
        @Override
        public ComplexI of(@NotNull Fraction x) {
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
